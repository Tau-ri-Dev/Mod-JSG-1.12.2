package tauri.dev.jsg.tileentity.energy;

import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.power.zpm.ZPMEnergyStorage;
import tauri.dev.jsg.renderer.zpm.ZPMRenderer;

public class ZPMCreativeTile extends ZPMTile {
    private final ZPMEnergyStorage energyStorage = new ZPMEnergyStorage((long) JSGConfig.ZPM.power.zpmCapacity, JSGConfig.ZPM.power.zpmHubMaxEnergyTransfer) {

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return maxExtract;
        }

        @Override
        public void setEnergyStored(long energyStored) {
            this.energy = capacity;
            onEnergyChanged();
        }

        @Override
        public long getEnergyStored(){
            return capacity;
        }

        @Override
        protected void onEnergyChanged() {
            markDirty();
        }
    };


    @Override
    public void update() {
        if (!world.isRemote) {
            if (getEnergyStorage().getEnergyStored() < getEnergyStorage().getMaxEnergyStored()) {
                getEnergyStorage().setEnergyStored(getEnergyStorage().getMaxEnergyStored());
            }
        }
    }

    @Override
    public ZPMEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public ZPMRenderer.ZPMModelType getType() {
        return ZPMRenderer.ZPMModelType.CREATIVE;
    }
}
