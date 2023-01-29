package tauri.dev.jsg.tileentity.energy;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.energy.ZPMBlock;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.gui.container.zpmhub.ZPMHubContainerGuiUpdate;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.packet.StateUpdateRequestToServer;
import tauri.dev.jsg.stargate.power.StargateClassicEnergyStorage;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateProviderInterface;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.state.energy.ZPMHubRendererUpdate;
import tauri.dev.jsg.util.JSGAdvancementsUtil;
import tauri.dev.jsg.util.JSGItemStackHandler;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;
import java.util.Objects;

import static tauri.dev.jsg.util.JSGAdvancementsUtil.tryTriggerRangedAdvancement;

@Optional.Interface(iface = "li.cil.oc.api.network.Environment", modid = "opencomputers")
public class ZPMHubTile extends TileEntity implements ITickable, ICapabilityProvider, StateProviderInterface, Environment {

    private static final int SLIDING_ANIMATION_LENGTH = 50;// int ticks

    public int getAnimationLength() {
        return SLIDING_ANIMATION_LENGTH;
    }

    public int getContainerSize() {
        return 3;
    }

    public void triggerAdvancement() {
        if (itemStackHandler.getStackInSlot(0).isEmpty()) return;
        if (itemStackHandler.getStackInSlot(1).isEmpty()) return;
        if (itemStackHandler.getStackInSlot(2).isEmpty()) return;
        tryTriggerRangedAdvancement(this, JSGAdvancementsUtil.EnumAdvancementType.ZPM_HUB);
    }

    protected final ItemStackHandler itemStackHandler = new JSGItemStackHandler(getContainerSize()) {

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (!isSlidingUp || isAnimating) return false;
            if (stack.getItem() instanceof ItemBlock) {
                ItemBlock itemBlock = (ItemBlock) stack.getItem();
                return itemBlock.getBlock() instanceof ZPMBlock;
            }
            return false;
        }

        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!isSlidingUp || isAnimating) return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
            updatePowerTier();
            markDirty();
            triggerAdvancement();
        }
    };

    private final StargateClassicEnergyStorage energyStorage = new StargateClassicEnergyStorage(0, JSGConfig.powerConfig.zpmHubMaxEnergyTransfer) {

        @Override
        protected void onEnergyChanged() {
            markDirty();
            sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            // We don't want to have ZPMs rechargeable... Obvious...
            return 0;
        }
    };

    protected int energyStoredLastTick = 0;
    protected int energyTransferedLastTick = 0;
    private EnumFacing facing;
    public float facingAngle;
    private NetworkRegistry.TargetPoint targetPoint;

    public long animationStart;
    public boolean isAnimating;
    public boolean isSlidingUp = true;

    // Only on client
    public int zpm1Level;
    public int zpm2Level;
    public int zpm3Level;
    // ---------------

    public void startAnimation() {
        if (isAnimating) return;
        animationStart = this.world.getTotalWorldTime();
        isAnimating = true;
        isSlidingUp = !isSlidingUp;
        sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
        markDirty();
    }

    public NetworkRegistry.TargetPoint getTargetPoint() {
        return targetPoint;
    }

    public StargateClassicEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public void onLoad() {
        facing = world.getBlockState(pos).getValue(JSGProps.FACING_HORIZONTAL);
        if (!world.isRemote) {
            updatePowerTier();
            targetPoint = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
        } else {
            JSGPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.RENDERER_UPDATE));
        }
    }

    private boolean addedToNetwork = false;

    @Override
    public void update() {
        if (!world.isRemote) {
            if (!addedToNetwork) {
                addedToNetwork = true;
                JSG.ocWrapper.joinOrCreateNetwork(this);
            }

            if (!isSlidingUp && !isAnimating) {
                for (EnumFacing facing : EnumFacing.VALUES) {
                    TileEntity tile = world.getTileEntity(pos.offset(facing));

                    if (tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
                        int extracted = energyStorage.extractEnergy(JSGConfig.powerConfig.zpmHubMaxEnergyTransfer, true);
                        extracted = Objects.requireNonNull(tile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite())).receiveEnergy(extracted, false);

                        energyStorage.extractEnergy(extracted, false);
                    }
                }
            }

            if (energyStoredLastTick != (energyStorage.getEnergyStored() - energyStoredLastTick)) {
                sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
            }

            energyTransferedLastTick = energyStorage.getEnergyStored() - energyStoredLastTick;
            if (energyTransferedLastTick > 0) energyTransferedLastTick = 0;
            energyStoredLastTick = energyStorage.getEnergyStored();
            markDirty();

            if (isAnimating) {
                if ((animationStart + getAnimationLength()) < this.world.getTotalWorldTime()) {
                    isAnimating = false;
                    animationStart = -1;
                    sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
                    markDirty();
                }
            }
        }
    }

    private int currentPowerTier;

    protected void updatePowerTier() {
        int powerTier = 1;

        for (int i = 0; i < getContainerSize(); i++) {
            if (!itemStackHandler.getStackInSlot(i).isEmpty()) {
                powerTier++;
            }
        }

        if (powerTier != currentPowerTier) {
            currentPowerTier = powerTier;
            energyStorage.clearStorages();

            for (int i = 0; i < getContainerSize(); i++) {
                ItemStack stack = itemStackHandler.getStackInSlot(i);

                if (!stack.isEmpty()) {
                    energyStorage.addStorage(stack.getCapability(CapabilityEnergy.ENERGY, null));
                }
            }
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setTag("energyStorage", getEnergyStorage().serializeNBT());
        compound.setTag("itemStackHandler", itemStackHandler.serializeNBT());

        compound.setBoolean("isAnimating", isAnimating);
        compound.setBoolean("isSlidingUp", isSlidingUp);
        compound.setLong("animationStart", animationStart);

        if (node != null) {
            NBTTagCompound nodeCompound = new NBTTagCompound();
            node.save(nodeCompound);

            compound.setTag("node", nodeCompound);
        }

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        getEnergyStorage().deserializeNBT(compound.getCompoundTag("energyStorage"));
        itemStackHandler.deserializeNBT(compound.getCompoundTag("itemStackHandler"));

        isAnimating = compound.getBoolean("isAnimating");
        isSlidingUp = compound.getBoolean("isSlidingUp");
        animationStart = compound.getLong("animationStart");

        if (node != null && compound.hasKey("node")) node.load(compound.getCompoundTag("node"));

        super.readFromNBT(compound);
    }

    public int getEnergyTransferedLastTick() {
        return energyTransferedLastTick;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityEnergy.ENERGY) || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY)
            return CapabilityEnergy.ENERGY.cast(getEnergyStorage());

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemStackHandler);

        return super.getCapability(capability, facing);
    }

    public static int getZPMPowerLevel(int energyStored, int maxEnergy) {
        return Math.round(energyStored / (float) maxEnergy * 5);
    }


    // -----------------------------------------------------------------------------
    // State

    protected void sendState(StateTypeEnum type, State state) {
        if (world.isRemote) return;
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
                ItemStack stack1 = getContainerSize() > 0 ? itemStackHandler.getStackInSlot(0) : ItemStack.EMPTY;
                ItemStack stack2 = getContainerSize() > 1 ? itemStackHandler.getStackInSlot(1) : ItemStack.EMPTY;
                ItemStack stack3 = getContainerSize() > 2 ? itemStackHandler.getStackInSlot(2) : ItemStack.EMPTY;

                int zpm1Level = stack1.isEmpty() ? -1 : getZPMPowerLevel(Objects.requireNonNull(stack1.getCapability(CapabilityEnergy.ENERGY, null)).getEnergyStored(), Objects.requireNonNull(stack1.getCapability(CapabilityEnergy.ENERGY, null)).getMaxEnergyStored());
                int zpm2Level = stack2.isEmpty() ? -1 : getZPMPowerLevel(Objects.requireNonNull(stack2.getCapability(CapabilityEnergy.ENERGY, null)).getEnergyStored(), Objects.requireNonNull(stack2.getCapability(CapabilityEnergy.ENERGY, null)).getMaxEnergyStored());
                int zpm3Level = stack3.isEmpty() ? -1 : getZPMPowerLevel(Objects.requireNonNull(stack3.getCapability(CapabilityEnergy.ENERGY, null)).getEnergyStored(), Objects.requireNonNull(stack3.getCapability(CapabilityEnergy.ENERGY, null)).getMaxEnergyStored());

                return new ZPMHubRendererUpdate(animationStart, isAnimating, isSlidingUp, zpm1Level, zpm2Level, zpm3Level, (facing.getHorizontalIndex() - 2) * 90);

            case GUI_UPDATE:
                return new ZPMHubContainerGuiUpdate(energyStorage.getEnergyStoredInternally(), energyTransferedLastTick);

            default:
                return null;
        }
    }


    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_UPDATE:
                return new ZPMHubRendererUpdate();

            case GUI_UPDATE:
                return new ZPMHubContainerGuiUpdate();

            default:
                return null;
        }
    }


    @Override
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {
            case RENDERER_UPDATE:
                ZPMHubRendererUpdate s = (ZPMHubRendererUpdate) state;
                this.animationStart = s.animationStart;
                this.isAnimating = s.isAnimating;
                this.isSlidingUp = s.slidingUp;
                this.zpm1Level = s.zpm1Level;
                this.zpm2Level = s.zpm2Level;
                this.zpm3Level = s.zpm3Level;
                this.facingAngle = s.facing;
                world.markBlockRangeForRenderUpdate(pos, pos);
                markDirty();
                break;

            case GUI_UPDATE:
                ZPMHubContainerGuiUpdate guiUpdate = (ZPMHubContainerGuiUpdate) state;
                energyStorage.setEnergyStoredInternally(guiUpdate.energyStored);
                energyTransferedLastTick = guiUpdate.energyTransferedLastTick;
                break;

            default:
                break;
        }
    }


    // ------------------------------------------------------------
    // Node-related work
    private final Node node = JSG.ocWrapper.createNode(this, "zpmhub");

    @Override
    public void onChunkUnload() {
        if (node != null) node.remove();
    }

    @Override
    public void invalidate() {
        if (node != null) node.remove();

        super.invalidate();
    }

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
        JSG.ocWrapper.sendSignalToReachable(node, (Context) context, name, params);
    }

    // ------------------------------------------------------------
    // Methods
    // function(arg:type[, optionArg:type]):resultType; Description.

    @net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    @Callback
    @SuppressWarnings("unused")
    public Object[] getJSGVersion(Context context, Arguments args) {
        return new Object[]{JSG.MOD_VERSION};
    }

    @SuppressWarnings("unused")
    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() -- Toggles ZPM slots")
    public Object[] toggleSlots(Context context, Arguments args) {
        if (!isAnimating) {
            startAnimation();
            if (!isSlidingUp)
                return new Object[]{null, true, "slots_toggled_down", "Slots are sliding down now!"};
            return new Object[]{null, true, "slots_toggled_up", "Slots are sliding up now!"};
        }
        return new Object[]{null, false, "slots_busy", "Slots are busy!"};
    }
}
