package tauri.dev.jsg.tileentity.energy;

import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.stargate.power.StargateAbstractEnergyStorage;

public class ZPMCreativeTile extends ZPMTile {
    private final StargateAbstractEnergyStorage energyStorage = new StargateAbstractEnergyStorage(JSGConfig.powerConfig.zpmCapacity, JSGConfig.powerConfig.zpmHubMaxEnergyTransfer) {

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return maxExtract;
        }

        public int receiveEnergyInternal(int maxReceive, boolean simulate) {
            return 0;
        }

        public void setEnergyStored(int energyStored) {
            this.energy = capacity;
            onEnergyChanged();
        }

        @Override
        protected void onEnergyChanged() {
            markDirty();
        }

        // we do not want ZPM to be chargeable
        @Override
        public int receiveEnergy(int maxEnergy, boolean simulate) {
            return 0;
        }
    };

    @Override
    public StargateAbstractEnergyStorage getEnergyStorage() {
        return energyStorage;
    }
}
