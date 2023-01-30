package tauri.dev.jsg.tileentity.energy;

import tauri.dev.jsg.power.zpm.ZPMEnergyStorage;

public class ZPMCreativeTile extends ZPMTile {
    private final ZPMEnergyStorage energyStorage = new ZPMEnergyStorage(Long.MAX_VALUE, Integer.MAX_VALUE) {

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
    public ZPMEnergyStorage getEnergyStorage() {
        return energyStorage;
    }
}
