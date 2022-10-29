package tauri.dev.jsg.tileentity.energy;

import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.gui.container.capacitor.CapacitorContainerGuiUpdate;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.packet.StateUpdateRequestToServer;
import tauri.dev.jsg.stargate.power.StargateAbstractEnergyStorage;
import tauri.dev.jsg.state.energy.CapacitorPowerLevelUpdate;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateProviderInterface;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.util.JSGItemStackHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class CapacitorTile extends TileEntity implements ITickable, ICapabilityProvider, StateProviderInterface {

    // ------------------------------------------------------------------------
    // Loading & ticking

    protected final ItemStackHandler itemStackHandler = new JSGItemStackHandler(1) {

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 0) {
                return stack.hasCapability(CapabilityEnergy.ENERGY, null);
            }
            return true;
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    };
    private final StargateAbstractEnergyStorage energyStorage = new StargateAbstractEnergyStorage() {

        @Override
        protected void onEnergyChanged() {
            markDirty();
        }
    };
    protected int energyStoredLastTick = 0;
    protected int energyTransferedLastTick = 0;
    private TargetPoint targetPoint;
    private int powerLevel;
    private int lastPowerLevel;

    public TargetPoint getTargetPoint() {
        return targetPoint;
    }

    public int getPowerLevel() {
        return powerLevel;
    }


    // ------------------------------------------------------------------------
    // NBT

    public StargateAbstractEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            targetPoint = new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
        } else {
            JSGPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.RENDERER_UPDATE));
        }
    }


    // -----------------------------------------------------------------------------
    // Power system

    @Override
    public void update() {
        if (!world.isRemote) {
            for (EnumFacing facing : EnumFacing.VALUES) {
                TileEntity tile = world.getTileEntity(pos.offset(facing));

                if (tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
                    int extracted = energyStorage.extractEnergy(tauri.dev.jsg.config.JSGConfig.powerConfig.stargateMaxEnergyTransfer, true);
                    extracted = tile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite()).receiveEnergy(extracted, false);

                    energyStorage.extractEnergy(extracted, false);
                }
            }

            ItemStack stack = itemStackHandler.getStackInSlot(0);
            if (!stack.isEmpty() && stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
                IEnergyStorage targetEnergyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);
                if (targetEnergyStorage != null) {
                    int extracted = energyStorage.extractEnergy(JSGConfig.powerConfig.stargateMaxEnergyTransfer, true);
                    extracted = targetEnergyStorage.receiveEnergy(extracted, false);
                    energyStorage.extractEnergy(extracted, false);
                }
            }

            powerLevel = Math.round(energyStorage.getEnergyStored() / (float) energyStorage.getMaxEnergyStored() * 10);
            if (powerLevel != lastPowerLevel) {
                JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE)), targetPoint);

                lastPowerLevel = powerLevel;
            }

            energyTransferedLastTick = energyStorage.getEnergyStored() - energyStoredLastTick;
            energyStoredLastTick = energyStorage.getEnergyStored();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("energyStorage", getEnergyStorage().serializeNBT());
        compound.setTag("itemStackHandler", itemStackHandler.serializeNBT());

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        getEnergyStorage().deserializeNBT(compound.getCompoundTag("energyStorage"));
        itemStackHandler.deserializeNBT(compound.getCompoundTag("itemStackHandler"));

        super.readFromNBT(compound);
    }

    public int getEnergyTransferedLastTick() {
        return energyTransferedLastTick;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityEnergy.ENERGY) || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY)
            return CapabilityEnergy.ENERGY.cast(getEnergyStorage());

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemStackHandler);

        return super.getCapability(capability, facing);
    }


    // -----------------------------------------------------------------------------
    // State

    @Override
    public State getState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_UPDATE:
                return new CapacitorPowerLevelUpdate(powerLevel);

            case GUI_UPDATE:
                return new CapacitorContainerGuiUpdate(energyStorage.getEnergyStored(), energyTransferedLastTick);

            default:
                return null;
        }
    }


    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_UPDATE:
                return new CapacitorPowerLevelUpdate();

            case GUI_UPDATE:
                return new CapacitorContainerGuiUpdate();

            default:
                return null;
        }
    }


    @Override
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {
            case RENDERER_UPDATE:
                powerLevel = ((CapacitorPowerLevelUpdate) state).powerLevel;
                world.markBlockRangeForRenderUpdate(pos, pos);
                break;

            case GUI_UPDATE:
                CapacitorContainerGuiUpdate guiUpdate = (CapacitorContainerGuiUpdate) state;
                energyStorage.setEnergyStored(guiUpdate.energyStored);
                energyTransferedLastTick = guiUpdate.energyTransferedLastTick;
                break;

            default:
                break;
        }
    }
}
