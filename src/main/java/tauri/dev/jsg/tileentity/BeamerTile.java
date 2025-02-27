package tauri.dev.jsg.tileentity;

import com.google.common.collect.Iterators;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import net.minecraft.block.Block;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.beamer.*;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.gui.container.beamer.BeamerContainerGui;
import tauri.dev.jsg.gui.container.beamer.BeamerContainerGuiUpdate;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.packet.StateUpdateRequestToServer;
import tauri.dev.jsg.power.general.SmallEnergyStorage;
import tauri.dev.jsg.sound.JSGSoundHelper;
import tauri.dev.jsg.sound.JSGSoundHelperClient;
import tauri.dev.jsg.sound.SoundEventEnum;
import tauri.dev.jsg.sound.SoundPositionedEnum;
import tauri.dev.jsg.stargate.EnumScheduledTask;
import tauri.dev.jsg.stargate.EnumStargateState;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateProviderInterface;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.state.beamer.BeamerFluidUpdate;
import tauri.dev.jsg.state.beamer.BeamerRendererActionState;
import tauri.dev.jsg.state.beamer.BeamerRendererState;
import tauri.dev.jsg.state.beamer.BeamerRendererUpdate;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.tileentity.util.*;
import tauri.dev.jsg.util.JSGAxisAlignedBB;
import tauri.dev.jsg.util.LinkingHelper;
import tauri.dev.jsg.util.NBTHelper;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static tauri.dev.jsg.block.JSGBlocks.BEAMER_BLOCK;

@Optional.Interface(iface = "li.cil.oc.api.network.Environment", modid = "opencomputers")
public class BeamerTile extends SidedTileEntity implements ITickable, IUpgradable, StateProviderInterface, ScheduledTaskExecutorInterface, Environment {

    // -----------------------------------------------------------------------------
    // Ticking & loading

    public static final float BEAMER_BEAM_MAX_RADIUS = 0.1375f;

    private EnumFacing facing;
    private TargetPoint targetPoint;
    private JSGAxisAlignedBB renderBox = new JSGAxisAlignedBB(0, 0, 0, 1, 1, 1); // To be replaced in updateFacing()
    private JSGAxisAlignedBB renderBoxOffsetted = renderBox;
    private StargatePos targetGatePos;

    public EnumFacing getFacing() {
        return facing;
    }

    public JSGAxisAlignedBB getRenderBoxForDisplay() {
        return renderBox;
    }

    @Override
    public void onLoad() {
        updateFacing(world.getBlockState(pos).getValue(JSGProps.FACING_HORIZONTAL));

        if (!world.isRemote) {
            targetPoint = new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
        } else {
            JSGPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.RENDERER_STATE));
            JSGPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.RENDERER_UPDATE));
        }
    }

    public void updateFacing(EnumFacing facing) {
        this.facing = facing;
        this.renderBox = new JSGAxisAlignedBB(-0.5, 0, -0.5, 0.5, 1, JSGConfig.Beamer.mechanics.reach).rotate(facing).offset(0.5, 0, 0.5);
        this.renderBoxOffsetted = this.renderBox.offset(pos);
    }

    private static final BlockMatcher BEAMER_MATCHER = BlockMatcher.forBlock(BEAMER_BLOCK);

    private BeamerStatusEnum updateBeamerStatus() {
        if (beamerMode == BeamerModeEnum.NONE)
            return BeamerStatusEnum.NO_CRYSTAL;

        if (!isLinked())
            return BeamerStatusEnum.NOT_LINKED;

        StargateClassicBaseTile gateTile = getLinkedGateTile();

        if (!gateTile.getStargateState().engaged()) {
            if (targetGatePos != null) {
                targetGatePos = null;
                markDirty();
            }
            return BeamerStatusEnum.CLOSED;
        }

        updateTargetBeamerData(targetGatePos);

        boolean isLaser = (this.getMode() == BeamerModeEnum.LASER);

        if (beamerRole == BeamerRoleEnum.DISABLED)
            return BeamerStatusEnum.BEAMER_DISABLED;

        if (isLaser && beamerRole == BeamerRoleEnum.RECEIVE)
            return BeamerStatusEnum.BEAMER_CANNOT_RECEIVE;

        if (!isLaser) {
            if (targetBeamerWorld == null || targetBeamerPos == null || !BEAMER_MATCHER.apply(targetBeamerWorld.getBlockState(targetBeamerPos)))
                return BeamerStatusEnum.NO_BEAMER;

            BeamerTile targetBeamerTile = (BeamerTile) targetBeamerWorld.getTileEntity(targetBeamerPos);

            if (targetBeamerTile == null)
                return BeamerStatusEnum.NO_BEAMER;

            if (targetBeamerTile.isObstructed)
                return BeamerStatusEnum.OBSTRUCTED_TARGET;

            if (targetBeamerTile.getRole() == BeamerRoleEnum.DISABLED)
                return BeamerStatusEnum.BEAMER_DISABLED_TARGET;

            if (targetBeamerTile.getStatus() == BeamerStatusEnum.BEAMER_DISABLED_BY_LOGIC)
                return BeamerStatusEnum.BEAMER_DISABLED_BY_LOGIC_TARGET;

            if (beamerRole == targetBeamerTile.getRole()) {
                if (beamerRole == BeamerRoleEnum.TRANSMIT)
                    return BeamerStatusEnum.TWO_TRANSMITTERS;
                else
                    return BeamerStatusEnum.TWO_RECEIVERS;
            }

            if (beamerMode.id != targetBeamerTile.getMode().id)
                return BeamerStatusEnum.MODE_MISMATCH;
        }

        if (isObstructed)
            return BeamerStatusEnum.OBSTRUCTED;

        if ((isLaser && !gateTile.getStargateState().initiating()) || (!isLaser && beamerMode != BeamerModeEnum.POWER && ((gateTile.getStargateState().initiating() && beamerRole != BeamerRoleEnum.TRANSMIT) || (gateTile.getStargateState() == EnumStargateState.ENGAGED && beamerRole != BeamerRoleEnum.RECEIVE))))
            return BeamerStatusEnum.INCOMING;

        if (isLaser && this.energyStorage.getEnergyStored() < JSGConfig.Beamer.power.laserEnergy)
            return BeamerStatusEnum.NO_POWER;

        switch (redstoneMode) {
            case AUTO:
                if (beamerRole == BeamerRoleEnum.RECEIVE && (beamerMode == BeamerModeEnum.POWER || beamerMode == BeamerModeEnum.FLUID)) {
                    float level = 0;

                    if (beamerMode == BeamerModeEnum.POWER)
                        level = energyStorage.getEnergyStored() / (float) energyStorage.getMaxEnergyStored();
                    else
                        level = fluidHandler.getFluidAmount() / (float) fluidHandler.getCapacity();

                    level *= 100;

                    if (beamerStatus == BeamerStatusEnum.OK) {
                        if (level > stop)
                            return BeamerStatusEnum.BEAMER_DISABLED_BY_LOGIC;

                        return BeamerStatusEnum.OK;
                    }

                    if (beamerStatus == BeamerStatusEnum.BEAMER_DISABLED_BY_LOGIC) {
                        if (level < start)
                            return BeamerStatusEnum.OK;

                        return BeamerStatusEnum.BEAMER_DISABLED_BY_LOGIC;
                    }
                }

                if (beamerMode == BeamerModeEnum.ITEMS) {
                    if (beamerStatus == BeamerStatusEnum.OK) {
                        if (timeWithoutItemTransfer > inactivity)
                            return BeamerStatusEnum.BEAMER_DISABLED_BY_LOGIC;

                        return BeamerStatusEnum.OK;
                    }

                    if (beamerStatus == BeamerStatusEnum.BEAMER_DISABLED_BY_LOGIC) {
                        BeamerTile targetBeamerTile = (BeamerTile) targetBeamerWorld.getTileEntity(targetBeamerPos);
                        for (int i = 1; i < 5; i++) {
                            if ((beamerRole == BeamerRoleEnum.RECEIVE && !Objects.requireNonNull(targetBeamerTile).itemStackHandler.getStackInSlot(i).isEmpty()) || (beamerRole == BeamerRoleEnum.TRANSMIT && !itemStackHandler.getStackInSlot(i).isEmpty())) {
                                return BeamerStatusEnum.OK;
                            }
                        }

                        return BeamerStatusEnum.BEAMER_DISABLED_BY_LOGIC;
                    }
                }

                break;

            case ON_HIGH:
                return world.isBlockPowered(pos) ? BeamerStatusEnum.OK : BeamerStatusEnum.BEAMER_DISABLED_BY_LOGIC;

            case ON_LOW:
                return world.isBlockPowered(pos) ? BeamerStatusEnum.BEAMER_DISABLED_BY_LOGIC : BeamerStatusEnum.OK;

            case IGNORED:
                return (ocLocked ? BeamerStatusEnum.BEAMER_DISABLED_BY_LOGIC : BeamerStatusEnum.OK);
        }

        return BeamerStatusEnum.OK;
    }

    private boolean addedToNetwork = false;

    private int powerTransferredSinceLastSignal = 0;
    private final List<FluidStack> fluidsTransferredSinceLastSignal = new ArrayList<>();
    private final List<ItemStack> itemsTransferredSinceLastSignal = new ArrayList<>();

    private boolean firstCheck = true;

    @Override
    public void update() {
        if (!world.isRemote) {
            ScheduledTask.iterate(scheduledTasks, world.getTotalWorldTime());

            // This cannot be done in onLoad because it makes
            // TE invisible to the network sometimes.
            if (!addedToNetwork) {
                JSG.ocWrapper.joinOrCreateNetwork(this);
                addedToNetwork = true;
            }

            BeamerStatusEnum lastBeamerStatus = beamerStatus;

            if (firstCheck || world.getTotalWorldTime() % 20 == 0) {
                firstCheck = false;

                int lastComp = comparatorOutput;
                comparatorOutput = updateComparatorOutput();

                if (lastComp != comparatorOutput) {
                    world.updateComparatorOutputLevel(pos, BEAMER_BLOCK);
                }

                if (isLinked()) {
                    StargateClassicBaseTile gateTile = getLinkedGateTile();
                    if (gateTile != null) {
                        if (gateTile.getStargateState().engaged()) {
                            updateObstructed();
                        }
                    } else {
                        basePos = null;
                        markDirty();
                    }
                }
            }

            beamerStatus = updateBeamerStatus();

            if (targetGatePos == null) {
                beamerStatus = BeamerStatusEnum.NOT_LINKED;
                markDirty();
            }

            if ((beamerStatus == BeamerStatusEnum.OK || beamerStatus == BeamerStatusEnum.OBSTRUCTED) && beamerMode != BeamerModeEnum.NONE && beamerRole != BeamerRoleEnum.DISABLED) {
                BeamerBeam.isSomethingInBeam(this, true, true);
            }

            if (beamerStatus == BeamerStatusEnum.OK) {
                // Push the items into target beamer
                if (beamerRole == BeamerRoleEnum.TRANSMIT) {
                    BeamerTile targetBeamerTile = (BeamerTile) targetBeamerWorld.getTileEntity(targetBeamerPos);

                    switch (beamerMode) {
                        case POWER:
                            int tx = energyStorage.extractEnergy(JSGConfig.Beamer.container.energyTransfer, true);
                            if (targetBeamerTile == null) break;
                            tx = targetBeamerTile.energyStorage.receiveEnergyInternal(tx, false);
                            energyStorage.extractEnergy(tx, false);
                            powerTransferredSinceLastSignal += tx;
                            break;

                        case FLUID:
                            FluidStack fluid = fluidHandler.drainInternal(tauri.dev.jsg.config.JSGConfig.Beamer.container.fluidTransfer, false);
                            if (fluid == null) break;
                            if (targetBeamerTile == null) break;
                            int filled = targetBeamerTile.fluidHandler.fillInternal(fluid, true);
                            fluidHandler.drainInternal(filled, true);

                            if (filled > 0) {
                                // FluidStack equals does not check amount
                                int index = fluidsTransferredSinceLastSignal.indexOf(fluid);

                                if (index != -1) {
                                    // Fluid exists
                                    FluidStack fluidStack = fluidsTransferredSinceLastSignal.get(index);
                                    fluidStack.amount += filled;
                                } else {
                                    // Does not exists
                                    fluidsTransferredSinceLastSignal.add(fluid.copy());
                                }
                            }

                            break;

                        case ITEMS:
                            if (targetBeamerTile == null) break;
                            int toTransfer = tauri.dev.jsg.config.JSGConfig.Beamer.container.itemTransfer;

                            for (int i = 1; i < 5; i++) {
                                for (int k = 1; k < 5; k++) {
                                    ItemStack copyOfStack = itemStackHandler.extractItem(i, toTransfer, true).copy();

                                    if (copyOfStack.isEmpty())
                                        break;
                                    int count = copyOfStack.getCount();
                                    int accepted = count - targetBeamerTile.itemStackHandler.insertItemInternal(k, copyOfStack, false).getCount();
                                    if (accepted > 0) {
                                        itemStackHandler.extractItem(i, accepted, false);

                                        toTransfer -= accepted;
                                        timeWithoutItemTransfer = 0;

                                        if (itemStackHandler.getStackInSlot(i).isEmpty())
                                            break;
                                    }
                                    if (toTransfer <= 0)
                                        break;
                                }
                                if (toTransfer <= 0)
                                    break;
                            }

                            if (toTransfer == tauri.dev.jsg.config.JSGConfig.Beamer.container.itemTransfer && world.getTotalWorldTime() % 20 == 0) {
                                timeWithoutItemTransfer++;
                            }

                            break;

                        case LASER:
                            powerTransferredSinceLastSignal += energyStorage.extractEnergy(JSGConfig.Beamer.power.laserEnergy, false);
                            TileEntity te = this.targetGatePos.getTileEntity();
                            if (te instanceof StargateClassicBaseTile) {
                                StargateClassicBaseTile gate = (StargateClassicBaseTile) te;
                                gate.tryHeatUp(true, true, 0.3, 0.6, 0, -1, -1);
                            }
                            break;

                        default:
                            break;
                    }

                    if (world.getTotalWorldTime() % JSGConfig.Beamer.mechanics.signalIntervalTicks == 0) {
                        // Every second send signal about transferred power/fluids/items

//						JSG.info(String.format("power=%d, fluids=%s, items=%s", powerTransferredSinceLastSignal, fluidsTransferredSinceLastSignal.toString(), itemsTransferredSinceLastSignal.toString()));

                        Map<String, Integer> map = new HashMap<>();

                        switch (beamerMode) {
                            case POWER:
                                if (powerTransferredSinceLastSignal > 0) {
                                    map.put("power", powerTransferredSinceLastSignal);
                                    sendSignal(null, "beamer_transfers", map);
                                    powerTransferredSinceLastSignal = 0;
                                }

                                break;

                            case FLUID:
                                if (!fluidsTransferredSinceLastSignal.isEmpty()) {
                                    for (FluidStack stack : fluidsTransferredSinceLastSignal) {
                                        map.put(stack.getLocalizedName(), stack.amount);
                                    }

                                    sendSignal(null, "beamer_transfers", map);
                                    fluidsTransferredSinceLastSignal.clear();
                                }

                                break;

                            case ITEMS:
                                if (!itemsTransferredSinceLastSignal.isEmpty()) {
                                    for (ItemStack stack : itemsTransferredSinceLastSignal) {
                                        map.put(stack.getDisplayName(), stack.getCount());
                                    }

                                    sendSignal(null, "beamer_transfers", map);
                                    itemsTransferredSinceLastSignal.clear();
                                }

                                break;

                            case NONE:
                                break;
                        }

                        markDirty();
                    }

                } // end transmit role
            } // end ok state

            // Push item into other power/fluid/item storages
            if (beamerRole == BeamerRoleEnum.RECEIVE) {
                for (EnumFacing side : EnumFacing.values()) {
                    TileEntity tileEntity = world.getTileEntity(pos.offset(side));

                    if (tileEntity != null) {
                        switch (beamerMode) {
                            case POWER:
                                if (tileEntity.hasCapability(CapabilityEnergy.ENERGY, side.getOpposite())) {
                                    IEnergyStorage targetEnergyStorage = tileEntity.getCapability(CapabilityEnergy.ENERGY, side.getOpposite());
                                    int tx = energyStorage.extractEnergy(tauri.dev.jsg.config.JSGConfig.Beamer.container.energyTransfer, true);
                                    tx = targetEnergyStorage.receiveEnergy(tx, false);
                                    energyStorage.extractEnergy(tx, false);
                                }

                                break;

                            case FLUID:
                                if (tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite())) {
                                    IFluidHandler targetFluidHandler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
                                    FluidStack drained = fluidHandler.drain(tauri.dev.jsg.config.JSGConfig.Beamer.container.fluidTransfer, false);
                                    int filled = targetFluidHandler.fill(drained, true);
                                    fluidHandler.drain(filled, true);
                                }

                                break;

                            case ITEMS:
                                if (beamerStatus == BeamerStatusEnum.OK && world.getTotalWorldTime() % 20 == 0) {
                                    timeWithoutItemTransfer++;
                                }

                                if (tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite())) {
                                    IItemHandler targetItemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite());
                                    if (targetItemHandler == null) break;

                                    int toTransfer = tauri.dev.jsg.config.JSGConfig.Beamer.container.itemTransfer;

                                    for (int i = 1; i < 5; i++) {
                                        for (int k = 0; k < targetItemHandler.getSlots(); k++) {
                                            ItemStack copyOfStack = itemStackHandler.extractItem(i, toTransfer, true).copy();
                                            if (copyOfStack.isEmpty())
                                                break;
                                            int count = copyOfStack.getCount();
                                            int accepted = count - targetItemHandler.insertItem(k, copyOfStack, false).getCount();
                                            itemStackHandler.extractItem(i, accepted, false);
                                            toTransfer -= accepted;

                                            if (toTransfer == 0)
                                                break;
                                        }

                                        if (toTransfer == 0)
                                            break;
                                    }
                                }

                                break;

                            default:
                                break;
                        }
                    }
                }
            }


            if (beamerMode == BeamerModeEnum.POWER || beamerMode == BeamerModeEnum.LASER) {
                energyTransferredLastTick = energyStorage.getEnergyStored() - energyStoredLastTick;
                energyStoredLastTick = energyStorage.getEnergyStored();
            }

            if (lastBeamerStatus != beamerStatus) {
                syncToClient();

                if (beamerStatus == BeamerStatusEnum.OK) {
                    sendSignal(null, "beamer_started");
                    JSGSoundHelper.playSoundEvent(world, pos, SoundEventEnum.BEAMER_START); // 0.611s delay = 12 ticks
                    addTask(new ScheduledTask(EnumScheduledTask.BEAMER_TOGGLE_SOUND, 12));
                    sendRenderingAction(BeamerRendererAction.BEAM_ON);
                } else if (lastBeamerStatus == BeamerStatusEnum.OK) {
                    sendSignal(null, "beamer_stopped");
                    JSGSoundHelper.playSoundEvent(world, pos, SoundEventEnum.BEAMER_STOP); // 0.634s delay = 12 ticks
                    addTask(new ScheduledTask(EnumScheduledTask.BEAMER_TOGGLE_SOUND, 12));
                    sendRenderingAction(BeamerRendererAction.BEAM_OFF);
                }
            }
        }

        // Client update
        else {
            float speed = 0.005f;

            if (beamRadiusShrink) {
                if (beamRadiusClient > 0)
                    beamRadiusClient -= speed;
                else {
                    beamRadiusShrink = false;
                    beamRadiusClient = 0;
                }
            } else if (beamRadiusWiden) {
                if (beamRadiusClient < BEAMER_BEAM_MAX_RADIUS)
                    beamRadiusClient += speed;
                else {
                    beamRadiusWiden = false;
                    beamRadiusClient = BEAMER_BEAM_MAX_RADIUS;
                }
            }
        }
    }

    private void syncToClient() {
        sendState(StateTypeEnum.RENDERER_STATE, getState(StateTypeEnum.RENDERER_STATE));
    }

    private void sendRenderingAction(BeamerRendererAction action) {
        sendState(StateTypeEnum.RENDERER_ACTION, new BeamerRendererActionState(action));
    }

    public void updateTargetBeamerData(@Nullable StargatePos targetGatePos) {
        boolean isLaser = (this.getMode() == BeamerModeEnum.LASER);
        if (isLaser) {
            targetBeamerWorld = this.world;
            targetBeamerPos = this.pos;
            markDirty();
            return;
        }

        BlockPos remoteBeamerPos = findTargetBeamerPos(targetGatePos);

        if (remoteBeamerPos != null) {
            // Beamer found
            targetBeamerWorld = targetGatePos.getWorld();
            targetBeamerPos = remoteBeamerPos;

            BeamerTile remoteBeamerTile = (BeamerTile) targetBeamerWorld.getTileEntity(targetBeamerPos);
            if (remoteBeamerTile == null) return;

            //targetBeamerDistance = remoteBeamerTile.beamLengthServer;

            // Link remote
            remoteBeamerTile.targetBeamerWorld = this.world;
            remoteBeamerTile.targetBeamerPos = this.pos;
            //remoteBeamerTile.targetBeamerDistance = this.beamLengthServer;
        } else {
            targetBeamerWorld = null;
            targetBeamerPos = null;
        }
    }

    public void gateEngaged(StargatePos targetGatePos) {
        this.targetGatePos = targetGatePos;
        markDirty();
        updateTargetBeamerData(targetGatePos);
        syncToClient();
    }

    public void gateClosed() {
        clearTargetBeamerPos();
        this.targetGatePos = null;
        markDirty();
    }

    /**
     * Searches for the first beamer block linked to the targetGatePos gate
     *
     * @param targetGatePos
     * @return {@link BlockPos} of the beamer block or {@code null} if no beamer found
     */
    @Nullable
    private BlockPos findTargetBeamerPos(StargatePos targetGatePos) {
        if (targetGatePos == null) return null;
        World targetWorld = targetGatePos.getWorld();
        BlockPos targetPos;
        ArrayList<BlockPos> blacklist = new ArrayList<>();
        int loop = 0;
        do {
            loop++;
            targetPos = getNearest(targetWorld, targetGatePos.gatePos, blacklist);
            if (targetPos == null)
                break;


            BeamerTile targetBeamer = (BeamerTile) targetWorld.getTileEntity(targetPos);

            if (targetBeamer == null || !targetBeamer.isLinked()) {
                blacklist.add(targetPos);
                continue;
            }

            if (targetBeamer.beamerMode.id != this.beamerMode.id) {
                blacklist.add(targetPos);
                continue;
            }

            if (targetBeamer.beamerMode.id == BeamerModeEnum.NONE.id) {
                blacklist.add(targetPos);
                continue;
            }

            if (targetBeamer.beamerRole.id == this.beamerRole.id) {
                blacklist.add(targetPos);
                continue;
            }

            if (targetBeamer.targetBeamerPos != null && !(targetBeamer.targetBeamerPos.equals(this.pos))) {
                blacklist.add(targetPos);
                continue;
            }

            if (!(targetGatePos.gatePos.equals(targetBeamer.basePos))) {
                blacklist.add(targetPos);
                continue;
            }

            return targetPos.toImmutable();
        } while (loop < 100);
        return null;
    }

    public BlockPos getNearest(World world, BlockPos pos, ArrayList<BlockPos> blacklist) {
        Block[] blocks = {BEAMER_BLOCK};
        return LinkingHelper.findClosestPos(world, pos, new BlockPos(JSGConfig.Beamer.mechanics.reach, JSGConfig.Beamer.mechanics.reach, JSGConfig.Beamer.mechanics.reach), blocks, blacklist);
    }

    public void clearTargetBeamerPos() {
        if (targetBeamerPos != null) {
            BeamerTile targetBeamerTile = (BeamerTile) targetBeamerWorld.getTileEntity(targetBeamerPos);
            if (targetBeamerTile != null) targetBeamerTile.targetBeamerPos = null;
        }
    }

    // -----------------------------------------------------------------------------
    // Beamer

    private BeamerModeEnum beamerMode = BeamerModeEnum.NONE;
    private BeamerRoleEnum beamerRole = BeamerRoleEnum.TRANSMIT;
    private BeamerStatusEnum beamerStatus = BeamerStatusEnum.OBSTRUCTED;
    private World targetBeamerWorld = null;
    private BlockPos targetBeamerPos = null;
    private int comparatorOutput;
    private RedstoneModeEnum redstoneMode = RedstoneModeEnum.AUTO;
    private int start = 10;
    private int stop = 90;
    private int inactivity = 5;
    private int timeWithoutItemTransfer;
    private boolean ocLocked;

    private boolean isObstructed;

    public BeamerModeEnum getMode() {
        return beamerMode;
    }

    public BeamerRoleEnum getRole() {
        return beamerRole;
    }

    public BeamerStatusEnum getStatus() {
        return beamerStatus;
    }

    public int getComparatorOutput() {
        return comparatorOutput;
    }

    public RedstoneModeEnum getRedstoneMode() {
        return redstoneMode;
    }

    public void setRedstoneMode(RedstoneModeEnum redstoneMode) {
        this.redstoneMode = redstoneMode;
        this.ocLocked = false;

        markDirty();
    }

    public void setStartStop(int start, int stop) {
        this.start = start;
        this.stop = stop;

        markDirty();
    }

    public int getStart() {
        return start;
    }

    public int getStop() {
        return stop;
    }

    public void setInactivity(int inactivity) {
        this.inactivity = inactivity;
        markDirty();
    }

    public int getInactivity() {
        return inactivity;
    }

    public boolean isActive() {
        return beamerStatus == BeamerStatusEnum.OK;
    }

    public void setNextRole() {
        beamerRole = beamerRole.next();
        markDirty();
    }

    public void updateObstructed() {
        if (basePos == null)
            return;

        isObstructed = ((StargateClassicBaseTile) (Objects.requireNonNull(world.getTileEntity(this.basePos)))).isIrisClosed() || BeamerBeam.isSomethingInBeam(this, false, false);
        markDirty();
    }

    public int updateComparatorOutput() {

        switch (beamerMode) {
            case POWER:
                return ComparatorHelper.getComparatorLevel(energyStorage);

            case FLUID:
                return ComparatorHelper.getComparatorLevel(fluidHandler);

            case ITEMS:
                return ComparatorHelper.getComparatorLevel(itemStackHandler, 1);

            default:
                return 0;
        }
    }

    // -----------------------------------------------------------------------------
    // Linking

    private BlockPos baseVect;
    private BlockPos basePos;

    public boolean isLinked() {
        return basePos != null;
    }

    public BlockPos getLinkedGate() {
        return basePos;
    }

    public StargateClassicBaseTile getLinkedGateTile() {
        return (StargateClassicBaseTile) world.getTileEntity(basePos);
    }

    /**
     * @param baseVect South-rotated gate-to-beamer vector.
     */
    public void setLinkedGate(BlockPos basePos, BlockPos baseVect) {
        if (basePos == null || baseVect == null) {
            this.basePos = null;
            this.baseVect = null;
        } else {
            this.basePos = basePos.toImmutable();
            this.baseVect = baseVect.toImmutable();
        }

        markDirty();
    }

    @Override
    public ItemStackHandler getItemStackHandler() {
        return itemStackHandler;
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        switch (side) {
            case UP:
            case DOWN:
                return new int[]{0};
            default:
                if (beamerMode == BeamerModeEnum.ITEMS) {
                    return new int[]{1, 2, 3, 4};
                } else break;
        }
        return new int[0];
    }


    // -----------------------------------------------------------------------------
    // Item handler

    private class ItemStackHandlerBeamer extends ItemStackHandler {

        public ItemStackHandlerBeamer(int slots) {
            super(slots);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            Item item = stack.getItem();

            if (slot == 0) {
                return JSGItems.BEAMER_CRYSTAL_POWER.equals(item) || JSGItems.BEAMER_CRYSTAL_FLUID.equals(item) || JSGItems.BEAMER_CRYSTAL_ITEMS.equals(item) || JSGItems.BEAMER_CRYSTAL_LASER.equals(item);
            }
            return true;
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            if (slot == 0)
                return 1;

            return super.getStackLimit(slot, stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (!world.isRemote && slot == 0)
                updateMode();

            markDirty();
        }

        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack stack = super.extractItem(slot, amount, simulate);

            if (!world.isRemote && slot == 0 && !simulate)
                updateMode();

            return stack;
        }

        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (beamerRole != BeamerRoleEnum.TRANSMIT && slot != 0)
                return stack;

            return super.insertItem(slot, stack, simulate);
        }

        public ItemStack insertItemInternal(int slot, ItemStack stack, boolean simulate) {
            timeWithoutItemTransfer = 0;

            return super.insertItem(slot, stack, simulate);
        }
    }

    private final ItemStackHandlerBeamer itemStackHandler = new ItemStackHandlerBeamer(5);

    private void updateMode() {
        beamerMode = getModeFromItem(itemStackHandler.getStackInSlot(0).getItem());
        markDirty();
        syncToClient();
    }

    @Override
    public Iterator<Integer> getUpgradeSlotsIterator() {
        return Iterators.singletonIterator(0);
    }

    public static BeamerModeEnum getModeFromItem(Item crystal) {
        if (crystal == JSGItems.BEAMER_CRYSTAL_POWER)
            return BeamerModeEnum.POWER;

        else if (crystal == JSGItems.BEAMER_CRYSTAL_FLUID)
            return BeamerModeEnum.FLUID;

        else if (crystal == JSGItems.BEAMER_CRYSTAL_ITEMS)
            return BeamerModeEnum.ITEMS;

        else if (crystal == JSGItems.BEAMER_CRYSTAL_LASER)
            return BeamerModeEnum.LASER;

        return BeamerModeEnum.NONE;
    }

    // -----------------------------------------------------------------------------
    // Fluid handler

    private Fluid previouslyStoredFluid = null;

    private FluidTank fluidHandler = new FluidTank(null, tauri.dev.jsg.config.JSGConfig.Beamer.container.fluidCapacity) {

        protected void onContentsChanged() {
            if (beamerRole == BeamerRoleEnum.TRANSMIT && (fluid == null || fluid.getFluid() != previouslyStoredFluid)) {
                // Transmitting and has fluid
                Fluid fluidContained = fluid != null ? fluid.getFluid() : null;
                BeamerFluidUpdate update = new BeamerFluidUpdate(fluidContained);

                sendState(StateTypeEnum.BEAMER_FLUID_UPDATE, update);

                // Sync update to target beamer
                if (targetBeamerWorld != null && targetBeamerPos != null) {
                    BeamerTile targetTile = (BeamerTile) targetBeamerWorld.getTileEntity(targetBeamerPos);
                    targetTile.sendState(StateTypeEnum.BEAMER_FLUID_UPDATE, update);
                }
                previouslyStoredFluid = fluidContained;
            }

            markDirty();
        }

        public boolean canFill() {
            return beamerRole == BeamerRoleEnum.TRANSMIT;
        }

        ;
    };


    // -----------------------------------------------------------------------------
    // Power system

    private final SmallEnergyStorage energyStorage = new SmallEnergyStorage(tauri.dev.jsg.config.JSGConfig.Beamer.container.energyCapacity, JSGConfig.Beamer.container.energyTransfer) {

        @Override
        protected void onEnergyChanged() {
            markDirty();
        }

        @Override
        public boolean canReceive() {
            return beamerRole == BeamerRoleEnum.TRANSMIT;
        }
    };

    private int energyStoredLastTick = 0;
    private int energyTransferredLastTick = 0;

    public int getEnergyTransferredLastTick() {
        return energyTransferredLastTick;
    }


    // -----------------------------------------------------------------------------
    // Capabilities

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {

        // Not front
        if (facing != this.facing) {
            return (beamerMode == BeamerModeEnum.ITEMS && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                    || ((beamerMode == BeamerModeEnum.POWER || beamerMode == BeamerModeEnum.LASER) && capability == CapabilityEnergy.ENERGY)
                    || (beamerMode == BeamerModeEnum.FLUID && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                    || super.hasCapability(capability, facing);
        }

        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

        // Not front
        if (facing != this.facing) {
            if ((beamerMode == BeamerModeEnum.ITEMS || facing == null) && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemStackHandler);

            else if ((beamerMode == BeamerModeEnum.POWER || facing == null || beamerMode == BeamerModeEnum.LASER) && capability == CapabilityEnergy.ENERGY)
                return CapabilityEnergy.ENERGY.cast(energyStorage);

            else if ((beamerMode == BeamerModeEnum.FLUID || facing == null) && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidHandler);
        }

        return super.getCapability(capability, facing);
    }


    // ---------------------------------------------------------------------------------------------------
    // Tasks

    private boolean loopSoundPlaying;

    List<ScheduledTask> scheduledTasks = new ArrayList<>();

    @Override
    public void addTask(ScheduledTask scheduledTask) {
        scheduledTask.setExecutor(this);
        scheduledTask.setTaskCreated(world.getTotalWorldTime());

        scheduledTasks.add(scheduledTask);
        markDirty();
    }

    @Override
    public void executeTask(EnumScheduledTask scheduledTask, NBTTagCompound customData) {
        if (scheduledTask == EnumScheduledTask.BEAMER_TOGGLE_SOUND) {
            JSGSoundHelper.playPositionedSound(world, pos, SoundPositionedEnum.BEAMER_LOOP, isActive());

            loopSoundPlaying ^= true;
            markDirty();
        }
    }

    // ---------------------------------------------------------------------------------------------------
    // States

    @Nullable
    public Fluid lastFluidTransferred;

    public int beamOffsetFromGateTarget;
    public int beamOffsetFromTargetX;
    public int beamOffsetFromTargetY;
    public int beamOffsetFromTargetZ;
    public float beamRadiusClient;
    private boolean beamRadiusWiden;
    private boolean beamRadiusShrink;

    protected void sendState(StateTypeEnum type, State state) {
        if (world.isRemote)
            return;

        if (targetPoint != null) {
            JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, type, state), targetPoint);
        } else {
            JSG.debug("targetPoint was null trying to send " + type + " from " + this.getClass().getCanonicalName());
        }
    }

    @Override
    public State getState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_UPDATE:
                return new BeamerRendererUpdate(beamerStatus);

            case RENDERER_STATE:
                int distance = 0;
                int d1 = 0;
                int d2 = 0;

                if (baseVect != null) {
                    d1 = baseVect.getZ();
                    if (d1 < 0) d1 = -d1;

                    if (targetBeamerWorld != null && targetBeamerPos != null) {
                        BeamerTile tile = (BeamerTile) targetBeamerWorld.getTileEntity(targetBeamerPos);
                        if (tile != null && tile.isLinked() && tile.basePos != null && tile.baseVect != null) {
                            d2 = tile.baseVect.getZ();
                            if (d2 < 0) d2 = -d2;

                            EnumFacing.Axis ax1 = targetBeamerWorld.getBlockState(tile.basePos).getValue(JSGProps.FACING_HORIZONTAL).getAxis();
                            EnumFacing.Axis ax2 = world.getBlockState(basePos).getValue(JSGProps.FACING_HORIZONTAL).getAxis();

                            int x1 = (ax1 == EnumFacing.Axis.Z ? (tile.basePos.getX() - tile.getPos().getX()) : (tile.basePos.getZ() - tile.getPos().getZ()));
                            int x2 = (ax2 == EnumFacing.Axis.Z ? (basePos.getX() - getPos().getX()) : (basePos.getZ() - getPos().getZ()));
                            beamOffsetFromTargetX = x1 - x2;
                            //JSG.info("X: " + beamOffsetFromTargetXClient);

                            int y1 = (tile.basePos.getY() - tile.getPos().getY());
                            int y2 = (basePos.getY() - getPos().getY());
                            beamOffsetFromTargetY = y1 - y2;
                            //JSG.info("Y: " + beamOffsetFromTargetYClient);
                            markDirty();
                        }
                    }

                    if (beamerMode == BeamerModeEnum.LASER) {
                        EnumFacing.Axis ax2 = world.getBlockState(basePos).getValue(JSGProps.FACING_HORIZONTAL).getAxis();

                        int x1 = 0;
                        int x2 = (ax2 == EnumFacing.Axis.Z ? (basePos.getX() - getPos().getX()) : (basePos.getZ() - getPos().getZ()));
                        beamOffsetFromTargetX = x1 - x2;

                        int y1 = (basePos.getY() - getLinkedGateTile().getGateCenterPos().getY());
                        int y2 = (basePos.getY() - getPos().getY());
                        beamOffsetFromTargetY = y1 - y2;
                        markDirty();
                    }

                    distance = d1 + d2;
                    this.beamOffsetFromGateTarget = d2;
                    this.beamOffsetFromTargetZ = distance;
                    markDirty();

                }

                return new BeamerRendererState(beamerMode, beamerRole, beamerStatus, isObstructed, beamOffsetFromTargetZ, beamOffsetFromGateTarget, beamOffsetFromTargetX, beamOffsetFromTargetY);

            case GUI_UPDATE:
                return new BeamerContainerGuiUpdate(energyStorage.getEnergyStored(), energyTransferredLastTick, fluidHandler.getFluid(), beamerRole, redstoneMode, start, stop, inactivity);

            case BEAMER_FLUID_UPDATE:
                FluidStack fluidStack = fluidHandler.getFluid();
                return new BeamerFluidUpdate(fluidStack != null ? fluidStack.getFluid() : null);

            default:
                return null;
        }
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_ACTION:
                return new BeamerRendererActionState();

            case RENDERER_STATE:
                return new BeamerRendererState();

            case RENDERER_UPDATE:
                return new BeamerRendererUpdate();

            case GUI_UPDATE:
                return new BeamerContainerGuiUpdate();

            case BEAMER_FLUID_UPDATE:
                return new BeamerFluidUpdate();

            default:
                return null;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {
            case RENDERER_STATE:
                BeamerRendererState rendererState = (BeamerRendererState) state;

                beamerMode = rendererState.beamerMode;
                beamerRole = rendererState.beamerRole;
                beamerStatus = rendererState.beamerStatus;
                isObstructed = rendererState.isObstructed;
                beamOffsetFromTargetZ = rendererState.beamLength;
                beamOffsetFromGateTarget = rendererState.beamLengthTarget;
                beamOffsetFromTargetX = rendererState.beamOffsetFromTargetXClient;
                beamOffsetFromTargetY = rendererState.beamOffsetFromTargetYClient;
                world.markBlockRangeForRenderUpdate(pos, pos);
                break;

            case RENDERER_UPDATE:
                BeamerRendererUpdate update = (BeamerRendererUpdate) state;

                beamRadiusClient = update.beamerStatus == BeamerStatusEnum.OK ? BEAMER_BEAM_MAX_RADIUS : 0;
                JSGSoundHelperClient.playPositionedSoundClientSide(pos, SoundPositionedEnum.BEAMER_LOOP, update.beamerStatus == BeamerStatusEnum.OK);

                break;

            case RENDERER_ACTION:
                BeamerRendererActionState rendererAction = (BeamerRendererActionState) state;

                switch (rendererAction.action) {
                    case BEAM_ON:
                        beamRadiusClient = 0;
                        beamRadiusWiden = true;
                        beamRadiusShrink = false;
                        break;

                    case BEAM_OFF:
                        beamRadiusClient = BEAMER_BEAM_MAX_RADIUS;
                        beamRadiusWiden = false;
                        beamRadiusShrink = true;
                        break;
                }

                break;

            case GUI_UPDATE:
                BeamerContainerGuiUpdate guiUpdate = (BeamerContainerGuiUpdate) state;
                energyStorage.setEnergyStored(guiUpdate.energyStored);
                energyTransferredLastTick = guiUpdate.transferredLastTick;
                fluidHandler.setFluid(guiUpdate.fluidStack);
                beamerRole = guiUpdate.beamerRole;
                redstoneMode = guiUpdate.mode;
                start = guiUpdate.start;
                stop = guiUpdate.stop;
                inactivity = guiUpdate.inactivity;

                GuiScreen screen = Minecraft.getMinecraft().currentScreen;
                if (screen instanceof BeamerContainerGui) {
                    ((BeamerContainerGui) screen).updateStartStopInactivity();
                }

                break;

            case BEAMER_FLUID_UPDATE:
                BeamerFluidUpdate fluidUpdate = (BeamerFluidUpdate) state;

                lastFluidTransferred = fluidUpdate.fluidContained;
                JSG.debug("Received beamer fluid update: " + lastFluidTransferred);

                break;

            default:
                break;
        }
    }


    // ---------------------------------------------------------------------------------------------------
    // NBT

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        if (baseVect != null && basePos != null) {
            compound.setLong("baseVect", baseVect.toLong());
            compound.setLong("basePos", basePos.toLong());
        }

        compound.setTag("itemStackHandler", itemStackHandler.serializeNBT());
        compound.setTag("energyStorage", energyStorage.serializeNBT());

        NBTTagCompound fluidHandlerCompound = new NBTTagCompound();
        fluidHandler.writeToNBT(fluidHandlerCompound);
        compound.setTag("fluidHandler", fluidHandlerCompound);

        compound.setInteger("beamerMode", beamerMode.getKey());
        compound.setInteger("beamerRole", beamerRole.getKey());
        compound.setInteger("redstoneMode", redstoneMode.getKey());
        compound.setInteger("start", start);
        compound.setInteger("stop", stop);
        compound.setInteger("inactivity", inactivity);
        compound.setBoolean("ocLocked", ocLocked);
        compound.setBoolean("loopSoundPlaying", loopSoundPlaying);

        compound.setTag("scheduledTasks", ScheduledTask.serializeList(scheduledTasks));

        if (node != null) {
            NBTTagCompound nodeCompound = new NBTTagCompound();
            node.save(nodeCompound);

            compound.setTag("node", nodeCompound);
        }

        compound.setInteger("powerTransferredSinceLastSignal", powerTransferredSinceLastSignal);
        compound.setTag("fluidsTransferredSinceLastSignal", NBTHelper.serializeFluidStackList(fluidsTransferredSinceLastSignal));
        compound.setTag("itemsTransferredSinceLastSignal", NBTHelper.serializeItemStackList(itemsTransferredSinceLastSignal));

        if (targetGatePos != null) {
            compound.setInteger("targetGatePosSymbolType", targetGatePos.symbolType.id);
            compound.setTag("targetGatePos", targetGatePos.serializeNBT());
        }

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("baseVect") && compound.hasKey("basePos")) {
            baseVect = BlockPos.fromLong(compound.getLong("baseVect"));
            basePos = BlockPos.fromLong(compound.getLong("basePos"));
        }

        itemStackHandler.deserializeNBT(compound.getCompoundTag("itemStackHandler"));
        energyStorage.deserializeNBT(compound.getCompoundTag("energyStorage"));
        fluidHandler.readFromNBT(compound.getCompoundTag("fluidHandler"));

        beamerMode = BeamerModeEnum.valueOf(compound.getInteger("beamerMode"));
        beamerRole = BeamerRoleEnum.valueOf(compound.getInteger("beamerRole"));
        redstoneMode = RedstoneModeEnum.valueOf(compound.getInteger("redstoneMode"));
        start = compound.getInteger("start");
        stop = compound.getInteger("stop");
        inactivity = compound.getInteger("inactivity");
        ocLocked = compound.getBoolean("ocLocked");
        loopSoundPlaying = compound.getBoolean("loopSoundPlaying");

        powerTransferredSinceLastSignal = compound.getInteger("powerTransferredSinceLastSignal");
        fluidsTransferredSinceLastSignal.clear();
        itemsTransferredSinceLastSignal.clear();
        NBTHelper.deserializeFluidStackList(compound.getTagList("fluidsTransferredSinceLastSignal", NBT.TAG_COMPOUND), fluidsTransferredSinceLastSignal);
        NBTHelper.deserializeItemStackList(compound.getTagList("itemsTransferredSinceLastSignal", NBT.TAG_COMPOUND), itemsTransferredSinceLastSignal);

        if (node != null && compound.hasKey("node"))
            node.load(compound.getCompoundTag("node"));

        ScheduledTask.deserializeList(compound.getCompoundTag("scheduledTasks"), scheduledTasks, this);

        if (compound.hasKey("targetGatePos")) {
            SymbolTypeEnum symbolType = SymbolTypeEnum.valueOf(compound.getInteger("targetGatePosSymbolType"));
            targetGatePos = new StargatePos(symbolType, compound.getCompoundTag("targetGatePos"));
        }
    }


    // ---------------------------------------------------------------------------------------------------
    // Rendering distance

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return renderBoxOffsetted;
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 65536;
    }

    // ------------------------------------------------------------------------
    // OpenComputers

    @Override
    public void onChunkUnload() {
        if (node != null)
            node.remove();
    }

    @Override
    public void invalidate() {
        if (node != null)
            node.remove();

        super.invalidate();
    }

    // ------------------------------------------------------------
    // Node-related work
    private final Node node = JSG.ocWrapper.createNode(this, "beamer");

    @Override
    @Optional.Method(modid = "opencomputers")
    public Node node() {
        return node;
    }

    @Override
    @Optional.Method(modid = "opencomputers")
    public void onConnect(Node node) {
    }

    @Override
    @Optional.Method(modid = "opencomputers")
    public void onDisconnect(Node node) {
    }

    @Override
    @Optional.Method(modid = "opencomputers")
    public void onMessage(Message message) {
    }

    public void sendSignal(Object context, String name, Object... params) {
//		if (params.length > 0)
//			JSG.info("sending signal " + name + ", params: " + params[0].toString());
        JSG.ocWrapper.sendSignalToReachable(node, (Context) context, name, params);
    }

    // ------------------------------------------------------------
    // Methods

    @net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getJSGVersion(Context context, Arguments args) {
        return new Object[]{JSG.MOD_VERSION};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] isActive(Context context, Arguments args) {
        return new Object[]{isActive()};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] setActive(Context context, Arguments args) {
        ocLocked = !args.checkBoolean(0);
        markDirty();

        return new Object[]{};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] start(Context context, Arguments args) {
        ocLocked = false;
        markDirty();

        return new Object[]{};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] stop(Context context, Arguments args) {
        ocLocked = true;
        markDirty();

        return new Object[]{};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getBeamerMode(Context context, Arguments args) {
        return new Object[]{beamerMode.toString().toLowerCase()};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getBeamerRole(Context context, Arguments args) {
        return new Object[]{beamerRole.toString().toLowerCase()};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] setBeamerRole(Context context, Arguments args) {
        try {
            beamerRole = BeamerRoleEnum.valueOf(args.checkString(0).toUpperCase());
            markDirty();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Wrong Role name");
        }

        return new Object[]{};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getBeamerRedstoneMode(Context context, Arguments args) {
        return new Object[]{redstoneMode.toString().toLowerCase()};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] setBeamerRedstoneMode(Context context, Arguments args) {
        try {
            redstoneMode = RedstoneModeEnum.valueOf(args.checkString(0).toUpperCase());
            markDirty();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Wrong Mode name");
        }

        return new Object[]{};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] toggleBeamerRole(Context context, Arguments args) {
        switch (beamerRole) {
            case RECEIVE:
                beamerRole = BeamerRoleEnum.TRANSMIT;
                return new Object[]{beamerRole.toString().toLowerCase()};

            case TRANSMIT:
                beamerRole = BeamerRoleEnum.RECEIVE;
                return new Object[]{beamerRole.toString().toLowerCase()};

            case DISABLED:
                return new Object[]{"err_beamer_disabled"};

            default:
                return null;
        }
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getBeamerStatus(Context context, Arguments args) {
        return new Object[]{beamerStatus.toString().toLowerCase()};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getBufferStored(Context context, Arguments args) {
        switch (beamerMode) {
            case POWER:
                return new Object[]{energyStorage.getEnergyStored()};
            case FLUID:
                return new Object[]{fluidHandler.getFluidAmount(), (fluidHandler.getFluid() != null ? JSG.proxy.localize(fluidHandler.getFluid().getFluid().getUnlocalizedName()) : null)};
            case ITEMS:
                List<Map.Entry<String, Integer>> stackList = new ArrayList<>(4);

                for (int i = 1; i < 5; i++) {
                    ItemStack stack = itemStackHandler.getStackInSlot(i);
                    stackList.add(new AbstractMap.SimpleEntry<String, Integer>(stack.getDisplayName(), stack.getCount()));
                }

                return new Object[]{stackList};

            default:
                return new Object[]{"no_mode_set"};
        }
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getBufferCapacity(Context context, Arguments args) {
        switch (beamerMode) {
            case POWER:
                return new Object[]{energyStorage.getMaxEnergyStored()};
            case FLUID:
                return new Object[]{fluidHandler.getCapacity()};
            case ITEMS:
                List<Map.Entry<String, Integer>> stackList = new ArrayList<>(4);

                for (int i = 1; i < 5; i++) {
                    ItemStack stack = itemStackHandler.getStackInSlot(i);
                    stackList.add(new AbstractMap.SimpleEntry<String, Integer>(stack.getDisplayName(), stack.getMaxStackSize()));
                }

                return new Object[]{stackList};

            default:
                return new Object[]{"no_mode_set"};
        }
    }
}
