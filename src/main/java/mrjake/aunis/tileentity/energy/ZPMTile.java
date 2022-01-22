package mrjake.aunis.tileentity.energy;

import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.gui.container.zpm.ZPMContainerGuiUpdate;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.packet.StateUpdateRequestToServer;
import mrjake.aunis.renderer.energy.ZPMRendererState;
import mrjake.aunis.stargate.power.StargateAbstractEnergyStorage;
import mrjake.aunis.state.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;


public class ZPMTile extends CapacitorTile {

    // ---------------------------------------------------------------------------------------------------
    // Renderer state

    @Override
    public void onLoad() {
        if (world.isRemote){
            AunisPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.RENDERER_UPDATE));
            AunisPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.RENDERER_STATE));
        }
        else super.onLoad();
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            setPowerLevel(Math.round(getEnergyStorage().getEnergyStored() / (float)AunisConfig.powerConfig.zpmEnergyStorage * 10));
            if (getPowerLevel() != getLastPowerLevel()) {
                AunisPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE)), getTargetPoint());

                setLastPowerLevel(getPowerLevel());
            }

            energyTransferedLastTick = getEnergyStorage().getEnergyStored() - energyStoredLastTick;
            energyStoredLastTick = getEnergyStorage().getEnergyStored();
        }
    }

    private ZPMRendererState rendererStateClient;

    public ZPMRendererState getRendererStateClient() {
        return rendererStateClient;
    }

    @Override
    public StargateAbstractEnergyStorage getEnergyStorage(){
        return energyStorage;
    }

    protected StargateAbstractEnergyStorage energyStorage = new StargateAbstractEnergyStorage(AunisConfig.powerConfig.zpmEnergyStorage) {
        @Override
        protected void onEnergyChanged() {
            markDirty();
        }
    };

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return false;
        // prevents from filling zpm with capacitor
    }

    // -----------------------------------------------------------------------------
    // State

    @Override
    public State getState(StateTypeEnum stateType) {
        switch (stateType) {
            case GUI_UPDATE:
                return new ZPMContainerGuiUpdate(getEnergyStorage().getEnergyStored(), energyTransferedLastTick);

            case RENDERER_STATE:
            case RENDERER_UPDATE:
                return new ZPMRendererState(Math.round((float) getPowerLevel()/2));

            default:
                return super.getState(stateType);
        }
    }


    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {
            case GUI_UPDATE:
                return new ZPMContainerGuiUpdate();

            case RENDERER_STATE:
            case RENDERER_UPDATE:
                return new ZPMRendererState();

            default:
                return super.createState(stateType);
        }
    }


    @Override
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {

            case GUI_UPDATE:
                ZPMContainerGuiUpdate guiUpdate = (ZPMContainerGuiUpdate) state;
                energyStorage.setEnergyStored(guiUpdate.energyStored);
                energyTransferedLastTick = guiUpdate.energyTransferedLastTick;
                break;

            case RENDERER_STATE:
                rendererStateClient = ((ZPMRendererState) state).initClient(pos, Math.round((float) getPowerLevel()/2));
                break;

            case RENDERER_UPDATE:
                setPowerLevel(((ZPMRendererState) state).powerLevel);
                break;

            default:
                super.setState(stateType, state);
                break;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }
}
