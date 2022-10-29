package tauri.dev.jsg.tileentity.energy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.packet.StateUpdateRequestToServer;
import tauri.dev.jsg.stargate.power.StargateAbstractEnergyStorage;
import tauri.dev.jsg.state.energy.CapacitorPowerLevelUpdate;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateProviderInterface;
import tauri.dev.jsg.state.StateTypeEnum;

import javax.annotation.Nonnull;

public class ZPMTile extends TileEntity implements ITickable, ICapabilityProvider, StateProviderInterface {

    // ------------------------------------------------------------------------
    // Loading & ticking

    private final StargateAbstractEnergyStorage energyStorage = new StargateAbstractEnergyStorage(JSGConfig.powerConfig.zpmCapacity, JSGConfig.powerConfig.zpmHubMaxEnergyTransfer) {

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
            powerLevel = ZPMHubTile.getZPMPowerLevel(energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored());
            if (powerLevel != lastPowerLevel) {
                JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE)), targetPoint);

                lastPowerLevel = powerLevel;
            }

            energyTransferedLastTick = energyStorage.getEnergyStored() - energyStoredLastTick;
            energyStoredLastTick = energyStorage.getEnergyStored();
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("energyStorage", getEnergyStorage().serializeNBT());

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        getEnergyStorage().deserializeNBT(compound.getCompoundTag("energyStorage"));

        super.readFromNBT(compound);
    }

    public int getEnergyTransferedLastTick() {
        return energyTransferedLastTick;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return (facing == null && capability == CapabilityEnergy.ENERGY) || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (facing == null && capability == CapabilityEnergy.ENERGY)
            return CapabilityEnergy.ENERGY.cast(getEnergyStorage());

        return super.getCapability(capability, facing);
    }


    // -----------------------------------------------------------------------------
    // State

    @Override
    public State getState(StateTypeEnum stateType) {
        if (stateType == StateTypeEnum.RENDERER_UPDATE) {
            return new CapacitorPowerLevelUpdate(powerLevel);
        }
        return null;
    }


    @Override
    public State createState(StateTypeEnum stateType) {
        if (stateType == StateTypeEnum.RENDERER_UPDATE) {
            return new CapacitorPowerLevelUpdate();
        }
        return null;
    }


    @Override
    public void setState(StateTypeEnum stateType, State state) {
        if (stateType == StateTypeEnum.RENDERER_UPDATE) {
            powerLevel = ((CapacitorPowerLevelUpdate) state).powerLevel;
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }
}
