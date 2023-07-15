package tauri.dev.jsg.tileentity.stargate;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.stargate.StargateDimensionConfig;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.power.general.EnergyRequiredToOperate;
import tauri.dev.jsg.power.general.SmallEnergyStorage;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.renderer.stargate.StargateAbstractRendererState;
import tauri.dev.jsg.renderer.stargate.StargateOrlinRendererState;
import tauri.dev.jsg.sound.*;
import tauri.dev.jsg.stargate.EnumScheduledTask;
import tauri.dev.jsg.stargate.EnumStargateState;
import tauri.dev.jsg.stargate.StargateClosedReasonEnum;
import tauri.dev.jsg.stargate.StargateOpenResult;
import tauri.dev.jsg.stargate.merging.StargateAbstractMergeHelper;
import tauri.dev.jsg.stargate.merging.StargateOrlinMergeHelper;
import tauri.dev.jsg.stargate.network.StargateNetwork;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.network.SymbolMilkyWayEnum;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.state.stargate.StargateOrlinSparkState;
import tauri.dev.jsg.tileentity.util.ScheduledTask;
import tauri.dev.jsg.util.JSGAxisAlignedBB;
import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.jsg.worldgen.util.GeneratedStargate;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class StargateOrlinBaseTile extends StargateAbstractBaseTile {

    public static final EnumSet<BiomeOverlayEnum> SUPPORTED_OVERLAYS = EnumSet.of(
            BiomeOverlayEnum.NORMAL,
            BiomeOverlayEnum.FROST,
            BiomeOverlayEnum.MOSSY);

    // ------------------------------------------------------------------------
    // Stargate state
    public static final JSGAxisAlignedBB RENDER_BOX = new JSGAxisAlignedBB(-1.5, 0, -0.6, 1.5, 3, 1.5);
    private final SmallEnergyStorage energyStorage = new SmallEnergyStorage();
    public boolean canNotGenerate = false;
    private int openCount = 0;
    private boolean isPowered;
    private int sparkIndex;

    public void setLinkedDHD(BlockPos dhdPos, int linkId) {
    }

    /**
     * Checks openCount of ALL members.
     *
     * @return True if the gate (or any of it's parts) had been used 2 times (default)
     */
    public boolean isBroken() {
        if (openCount >= JSGConfig.Stargate.mechanics.stargateOrlinMaxOpenCount)
            return true;

        return StargateOrlinMergeHelper.INSTANCE.getMaxOpenCount(world, pos, facing) >= JSGConfig.Stargate.mechanics.stargateOrlinMaxOpenCount;
    }

    public void addDrops(List<ItemStack> drops) {

        if (openCount >= JSGConfig.Stargate.mechanics.stargateOrlinMaxOpenCount) {
            Random rand = new Random();

            drops.add(new ItemStack(Items.IRON_INGOT, 2 + rand.nextInt(3)));
        } else {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger("openCount", openCount);

            ItemStack stack = new ItemStack(Item.getItemFromBlock(JSGBlocks.STARGATE_ORLIN_BASE_BLOCK));
            stack.setTagCompound(compound);

            drops.add(stack);
        }
    }

    public void initializeFromItemStack(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();

            if (compound != null && compound.hasKey("openCount")) {
                openCount = compound.getInteger("openCount");
            }
        }
    }

    @Override
    public SymbolTypeEnum getSymbolType() {
        return SymbolTypeEnum.MILKYWAY;
    }

    @Override
    public void dialingFailed(StargateOpenResult result) {
        super.dialingFailed(result);

        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_FAILED_SOUND, 30));
    }

    @Override
    protected void addFailedTaskAndPlaySound() {
        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_FAIL, 83));
        playSoundEvent(StargateSoundEventEnum.DIAL_FAILED);
    }

    // ------------------------------------------------------------------------
    // Ticking

    @Override
    public void openGate(StargatePos targetGatePos, boolean isInitiating) {
        if (world.provider.getDimensionType() == DimensionType.OVERWORLD)
            StargateNetwork.get(world).setLastActivatedOrlins(gateAddressMap.get(SymbolTypeEnum.MILKYWAY));
        super.openGate(targetGatePos, isInitiating);
    }

    @Override
    protected void disconnectGate() {
        super.disconnectGate();

        openCount++;
        StargateOrlinMergeHelper.INSTANCE.incrementMembersOpenCount(world, pos, facing);

        if (isBroken()) {
            addTask(new ScheduledTask(EnumScheduledTask.STARGATE_ORLIN_BROKE_SOUND, 5));
        }
    }

    @Override
    public boolean canAcceptConnectionFrom(StargatePos targetGatePos) {
        return super.canAcceptConnectionFrom(targetGatePos) && targetGatePos.dimensionID == DimensionType.NETHER.getId() && !isBroken();
    }

    public void updateNetherAddress() {
        dialedAddress.clear();
        if (!network.hasNetherGate() || !network.isStargateInNetwork(network.getNetherGate()) || network.getStargate(network.getNetherGate()) == null) {
            if (!world.isRemote && world.provider.getDimensionType() == DimensionType.OVERWORLD) {
                JSG.info("Orlin gate requested building of new nether gate... Build started...");
                GeneratedStargate stargate = StargateNetwork.generateNetherGate(network, world, pos);
                if (stargate == null) {
                    canNotGenerate = true;
                    markDirty();
                }
            }
        }
        if (network.hasNetherGate()) {
            dialedAddress.addAll(network.getNetherGate().subList(0, StargateDimensionConfig.netherOverworld8thSymbol() ? 7 : 6));
            dialedAddress.addSymbol(SymbolMilkyWayEnum.ORIGIN);
        }
        markDirty();

        JSG.debug("Orlin's dialed address: " + dialedAddress);
    }


    // ------------------------------------------------------------------------
    // Redstone

    public EnergyRequiredToOperate getEnergyRequiredToDial() {
        return getEnergyRequiredToDial(network.getStargate(dialedAddress));
    }

    @Override
    public BlockPos getGateCenterPos() {
        return pos.offset(EnumFacing.UP, 1);
    }

    @Override
    public void update() {
        super.update();

        if (world.isRemote) {
            if (!world.getBlockState(pos).getValue(JSGProps.RENDER_BLOCK) && rendererStateClient != null)
                JSG.proxy.orlinRendererSpawnParticles(world, getRendererStateClient());

            // Each 2s check for the biome overlay
            if (world.getTotalWorldTime() % 40 == 0 && rendererStateClient != null) {
                rendererStateClient.setBiomeOverlay(BiomeOverlayEnum.updateBiomeOverlay(world, getMergeHelper().getTopBlock().add(pos), getSupportedOverlays()));
            }
        }
    }


    // ------------------------------------------------------------------------
    // Merging

    @Override
    public EnumSet<BiomeOverlayEnum> getSupportedOverlays() {
        return SUPPORTED_OVERLAYS;
    }

    // ------------------------------------------------------------------------
    // Killing

    public void redstonePowerUpdate(boolean power) {
        if (!isMerged())
            return;

        if ((isPowered && !power) || (!isPowered && power)) {
            isPowered = power;

            if (isPowered && stargateState.idle() && !isBroken())
                beginOpening();

            else if (!isPowered && stargateState.initiating()) {
                attemptClose(StargateClosedReasonEnum.REQUESTED);
            }

            markDirty();
        }
    }

    public void beginOpening() {
        if (world.provider.getDimensionType() != DimensionType.OVERWORLD) {
            JSGSoundHelper.playSoundEvent(world, getGateCenterPos(), SoundEventEnum.GATE_ORLIN_FAIL);
            return;
        }
        updateNetherAddress();
        if (isBroken()) return;
        switch (checkAddressAndEnergy(dialedAddress)) {
            case OK:
                stargateState = EnumStargateState.DIALING;

                startSparks();
                JSGSoundHelper.playSoundEvent(world, getGateCenterPos(), SoundEventEnum.GATE_ORLIN_DIAL);

                addTask(new ScheduledTask(EnumScheduledTask.STARGATE_ORLIN_OPEN));
                return;

            case ADDRESS_MALFORMED:
                if (!world.isRemote && world.provider.getDimensionType() == DimensionType.OVERWORLD) {
                    StargateNetwork.generateNetherGate(network, world, pos);
                    JSG.info("Orlin gate requested building of new nether gate... Build started...");
                }
                beginOpening();
                //JSG.error("Orlin's gate: wrong dialed address");
                break;

            case NOT_ENOUGH_POWER:
                //JSG.info("Orlin's gate: Not enough power");
                break;

            case ABORTED:
            case ABORTED_BY_EVENT:
            case CALLER_HUNG_UP:
                break;
        }
    }

    @Override
    public StargateAbstractMergeHelper getMergeHelper() {
        return StargateOrlinMergeHelper.INSTANCE;
    }


    // ------------------------------------------------------------------------
    // Rendering

    @Override
    protected JSGAxisAlignedBB getHorizonKillingBox(boolean server) {
        return new JSGAxisAlignedBB(-0.5, 1, -0.5, 0.5, 2, 1.5);
    }

    @Override
    protected int getHorizonSegmentCount(boolean server) {
        return 2;
    }

    @Override
    protected List<JSGAxisAlignedBB> getGateVaporizingBoxes(boolean server) {
        return Collections.singletonList(new JSGAxisAlignedBB(-0.5, 1, -0.5, 0.5, 2, 0.5));
    }


    // ------------------------------------------------------------------------
    // Sounds

    @Override
    protected JSGAxisAlignedBB getHorizonTeleportBox(boolean server) {
        return new JSGAxisAlignedBB(-1.0, 0.6, -0.15, 1.0, 2.7, -0.05);
    }

    @Override
    protected StargateAbstractRendererState createRendererStateClient() {
        return new StargateOrlinRendererState();
    }


    // ------------------------------------------------------------------------
    // States

    @Override
    public StargateOrlinRendererState getRendererStateClient() {
        return (StargateOrlinRendererState) super.getRendererStateClient();
    }

    @Override
    protected SoundPositionedEnum getPositionedSound(StargateSoundPositionedEnum soundEnum) {
        return null;
    }


    // ------------------------------------------------------------------------
    // Sparks

    @Override
    protected SoundEventEnum getSoundEvent(StargateSoundEventEnum soundEnum) {
        switch (soundEnum) {
            case OPEN:
                return SoundEventEnum.GATE_MILKYWAY_OPEN;
            case CLOSE:
                return SoundEventEnum.GATE_MILKYWAY_CLOSE;
            case DIAL_FAILED:
                return SoundEventEnum.GATE_ORLIN_FAIL;
            case GATE_BROKE:
                return SoundEventEnum.GATE_ORLIN_BROKE;
            default:
                return null;
        }
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        if (stateType == StateTypeEnum.SPARK_STATE) {
            return new StargateOrlinSparkState();
        }
        return super.createState(stateType);
    }


    // ------------------------------------------------------------------------
    // Scheduled tasks

    @Override
    @SideOnly(Side.CLIENT)
    public void setState(StateTypeEnum stateType, State state) {
        if (stateType == StateTypeEnum.SPARK_STATE) {
            if (getRendererStateClient() == null) return;
            StargateOrlinSparkState sparkState = (StargateOrlinSparkState) state;
            getRendererStateClient().sparkFrom(sparkState.sparkIndex, sparkState.spartStart);
        } else {
            super.setState(stateType, state);
        }
    }


    // ------------------------------------------------------------------------
    // Power

    public void startSparks() {
        sparkIndex = 0;

        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_ORLIN_SPARK, 5));
    }

    @Override
    public void executeTask(EnumScheduledTask scheduledTask, NBTTagCompound customData) {
        switch (scheduledTask) {
            case STARGATE_ORLIN_OPEN:
                StargatePos targetGatePos = network.getStargate(dialedAddress);

                if (hasEnergyToDial(targetGatePos) && targetGatePos != null && targetGatePos.getTileEntity() != null) {
                    targetGatePos.getTileEntity().incomingWormhole(dialedAddress.size());
                }

                attemptOpenAndFail();
                break;

            case STARGATE_ORLIN_SPARK:
                JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.SPARK_STATE, new StargateOrlinSparkState(sparkIndex, world.getTotalWorldTime())), targetPoint);

                if (sparkIndex < 6 && sparkIndex != -1)
                    addTask(new ScheduledTask(EnumScheduledTask.STARGATE_ORLIN_SPARK, 24));

                sparkIndex++;

                break;

            case STARGATE_FAILED_SOUND:
                playSoundEvent(StargateSoundEventEnum.DIAL_FAILED);

                break;

            case STARGATE_ORLIN_BROKE_SOUND:
                playSoundEvent(StargateSoundEventEnum.GATE_BROKE);

                break;

            default:
                super.executeTask(scheduledTask, customData);
                break;
        }
    }

    @Override
    protected SmallEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    protected EnergyRequiredToOperate getEnergyRequiredToDial(StargatePos targetGatePos) {
        return super.getEnergyRequiredToDial(targetGatePos).mul(JSGConfig.Stargate.power.stargateOrlinEnergyMul).cap(JSGConfig.Stargate.power.stargateEnergyStorage / 4 - 1000000);
    }

    @Override
    protected JSGAxisAlignedBB getRenderBoundingBoxRaw() {
        return RENDER_BOX;
    }

    // ------------------------------------------------------------------------
    // NBT

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("isPowered", isPowered);
        compound.setInteger("openCount", openCount);
        compound.setBoolean("canNotGenerate", canNotGenerate);

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        isPowered = compound.getBoolean("isPowered");
        openCount = compound.getInteger("openCount");
        canNotGenerate = compound.getBoolean("canNotGenerate");

        super.readFromNBT(compound);
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
	@SuppressWarnings("unused")
    public Object[] getGateType(Context context, Arguments args) {
        return new Object[]{isMerged() ? "ORLIN" : null};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() -- Tries to open the gate")
	@SuppressWarnings("unused")
    public Object[] engageGate(Context context, Arguments args) {
        if (!isMerged()) return new Object[]{null, "stargate_failure_not_merged", "Stargate is not merged"};

        if (stargateState.idle()) {
            if (!isBroken()) {
                beginOpening();
                return new Object[]{null, "stargate_engage"};
            } else {
                return new Object[]{null, "stargate_failure_opening", "Stargate is broken"};
            }
        } else {
            return new Object[]{null, "stargate_failure_busy", "Stargate is busy", stargateState.toString()};
        }
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() -- Tries to close the gate")
	@SuppressWarnings("unused")
    public Object[] disengageGate(Context context, Arguments args) {
        if (!isMerged()) return new Object[]{null, "stargate_failure_not_merged", "Stargate is not merged"};

        if (stargateState.engaged()) {
            if (getStargateState().initiating()) {
                attemptClose(StargateClosedReasonEnum.REQUESTED);
                return new Object[]{"stargate_disengage"};
            } else return new Object[]{null, "stargate_failure_wrong_end", "Unable to close the gate on this end"};
        } else {
            return new Object[]{null, "stargate_failure_not_open", "The gate is closed"};
        }
    }
}
