package mrjake.aunis.tileentity.energy;

import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.gui.container.zpm.ZPMContainerGuiUpdate;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.packet.StateUpdateRequestToServer;
import mrjake.aunis.renderer.energy.ZPMRendererState;
import mrjake.aunis.stargate.power.ZPMEnergyStorage;
import mrjake.aunis.state.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.network.NetworkRegistry;


public class ZPMTile extends CapacitorTile {

    // ---------------------------------------------------------------------------------------------------
    // Renderer state

    private ZPMRendererState rendererStateClient;

    public ZPMRendererState getRendererStateClient() {
        return rendererStateClient;
    }

    public ZPMEnergyStorage getEnergyStorage(){
        return energyStorage;
    }

    protected ZPMEnergyStorage energyStorage = new ZPMEnergyStorage() {

        @Override
        protected void onEnergyChanged() {
            markDirty();
        }
    };

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            targetPoint = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
        } else {
            AunisPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.RENDERER_STATE));
        }
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            for (EnumFacing facing : EnumFacing.VALUES) {
                TileEntity tile = world.getTileEntity(pos.offset(facing));

                if (tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
                    int extracted = energyStorage.extractEnergy(AunisConfig.powerConfig.stargateMaxEnergyTransfer, true);
                    extracted = tile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite()).receiveEnergy(extracted, false);

                    energyStorage.extractEnergy(extracted, false);
                }
            }

            powerLevel = Math.round(energyStorage.getEnergyStored() / (float)energyStorage.getMaxEnergyStored() * 10);
            if (powerLevel != lastPowerLevel) {
                AunisPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE)), targetPoint);

                lastPowerLevel = powerLevel;
            }

            energyTransferedLastTick = energyStorage.getEnergyStored() - energyStoredLastTick;
            energyStoredLastTick = energyStorage.getEnergyStored();
        }
    }


    // -----------------------------------------------------------------------------
    // State

    @Override
    public State getState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_UPDATE:
                return new ZPMPowerLevelUpdate(powerLevel);

            case GUI_UPDATE:
                return new ZPMContainerGuiUpdate(energyStorage.getEnergyStored(), energyTransferedLastTick);

            case RENDERER_STATE:
                return new ZPMRendererState(powerLevel);

            default:
                return null;
        }
    }


    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_UPDATE:
                return new ZPMPowerLevelUpdate();

            case GUI_UPDATE:
                return new ZPMContainerGuiUpdate();

            case RENDERER_STATE:
                return new ZPMRendererState();

            default:
                return null;
        }
    }


    @Override
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {
            case RENDERER_UPDATE:
                powerLevel = ((ZPMPowerLevelUpdate) state).powerLevel;
                world.markBlockRangeForRenderUpdate(pos, pos);
                break;

            case GUI_UPDATE:
                ZPMContainerGuiUpdate guiUpdate = (ZPMContainerGuiUpdate) state;
                energyStorage.setEnergyStored(guiUpdate.energyStored);
                energyTransferedLastTick = guiUpdate.energyTransferedLastTick;
                break;


            case RENDERER_STATE:
                rendererStateClient = ((ZPMRendererState) state).initClient(pos, powerLevel);

                break;

            default:
                break;
        }
    }
}
