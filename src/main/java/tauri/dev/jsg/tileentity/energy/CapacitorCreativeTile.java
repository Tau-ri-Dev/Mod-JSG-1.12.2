package tauri.dev.jsg.tileentity.energy;

import tauri.dev.jsg.stargate.power.StargateAbstractEnergyStorage;

public class CapacitorCreativeTile extends CapacitorTile {
    private final StargateAbstractEnergyStorage energyStorage = new StargateAbstractEnergyStorage() {

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

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
    };

    @Override
    public StargateAbstractEnergyStorage getEnergyStorage() {
        return energyStorage;
    }
}
