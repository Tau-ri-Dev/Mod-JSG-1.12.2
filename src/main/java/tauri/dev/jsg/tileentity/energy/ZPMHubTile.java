package tauri.dev.jsg.tileentity.energy;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlocks;
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
import tauri.dev.jsg.util.JSGItemStackHandler;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ZPMHubTile extends TileEntity implements ITickable, ICapabilityProvider, StateProviderInterface {

    public static final int SLIDING_ANIMATION_LENGTH = 50;// int ticks

    protected final ItemStackHandler itemStackHandler = new JSGItemStackHandler(3) {

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (!isSlidingUp || isAnimating) return false;
            if (stack.getItem() instanceof ItemBlock) {
                ItemBlock itemBlock = (ItemBlock) stack.getItem();
                return itemBlock.getBlock() == JSGBlocks.ZPM;
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
        }
    };

    private final StargateClassicEnergyStorage energyStorage = new StargateClassicEnergyStorage(0, JSGConfig.powerConfig.zpmHubMaxEnergyTransfer) {

        @Override
        protected void onEnergyChanged() {
            markDirty();
            sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
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

    @Override
    public void update() {
        if (!world.isRemote) {
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

            energyTransferedLastTick = energyStorage.getEnergyStored() - energyStoredLastTick;
            energyStoredLastTick = energyStorage.getEnergyStored();

            if (isAnimating) {
                if ((animationStart + SLIDING_ANIMATION_LENGTH) < this.world.getTotalWorldTime()) {
                    isAnimating = false;
                    animationStart = -1;
                    sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
                    markDirty();
                }
            }
        }
    }

    private int currentPowerTier;

    private void updatePowerTier() {
        int powerTier = 1;

        for (int i = 0; i < 3; i++) {
            if (!itemStackHandler.getStackInSlot(i).isEmpty()) {
                powerTier++;
            }
        }

        if (powerTier != currentPowerTier) {
            currentPowerTier = powerTier;
            energyStorage.clearStorages();

            for (int i = 0; i < 3; i++) {
                ItemStack stack = itemStackHandler.getStackInSlot(i);

                if (!stack.isEmpty()) {
                    energyStorage.addStorage(stack.getCapability(CapabilityEnergy.ENERGY, null));
                }
            }
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("energyStorage", getEnergyStorage().serializeNBT());
        compound.setTag("itemStackHandler", itemStackHandler.serializeNBT());

        compound.setBoolean("isAnimating", isAnimating);
        compound.setBoolean("isSlidingUp", isSlidingUp);
        compound.setLong("animationStart", animationStart);

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        getEnergyStorage().deserializeNBT(compound.getCompoundTag("energyStorage"));
        itemStackHandler.deserializeNBT(compound.getCompoundTag("itemStackHandler"));

        isAnimating = compound.getBoolean("isAnimating");
        isSlidingUp = compound.getBoolean("isSlidingUp");
        animationStart = compound.getLong("animationStart");

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
            JSG.logger.debug("targetPoint was null trying to send " + type + " from " + this.getClass().getCanonicalName());
        }
    }

    @Override
    public State getState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_UPDATE:
                ItemStack stack1 = itemStackHandler.getStackInSlot(0);
                ItemStack stack2 = itemStackHandler.getStackInSlot(1);
                ItemStack stack3 = itemStackHandler.getStackInSlot(2);

                int zpm1Level = stack1.isEmpty() ? -1 : getZPMPowerLevel(Objects.requireNonNull(stack1.getCapability(CapabilityEnergy.ENERGY, null)).getEnergyStored(), Objects.requireNonNull(stack1.getCapability(CapabilityEnergy.ENERGY, null)).getMaxEnergyStored());
                int zpm2Level = stack2.isEmpty() ? -1 : getZPMPowerLevel(Objects.requireNonNull(stack2.getCapability(CapabilityEnergy.ENERGY, null)).getEnergyStored(), Objects.requireNonNull(stack2.getCapability(CapabilityEnergy.ENERGY, null)).getMaxEnergyStored());
                int zpm3Level = stack3.isEmpty() ? -1 : getZPMPowerLevel(Objects.requireNonNull(stack3.getCapability(CapabilityEnergy.ENERGY, null)).getEnergyStored(), Objects.requireNonNull(stack3.getCapability(CapabilityEnergy.ENERGY, null)).getMaxEnergyStored());

                return new ZPMHubRendererUpdate(animationStart, isAnimating, isSlidingUp, zpm1Level, zpm2Level, zpm3Level, facing.getHorizontalAngle());

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
}
