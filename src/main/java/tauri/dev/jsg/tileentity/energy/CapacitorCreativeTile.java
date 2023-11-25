package tauri.dev.jsg.tileentity.energy;

import tauri.dev.jsg.power.general.SmallEnergyStorage;

public class CapacitorCreativeTile extends CapacitorTile {
    private final SmallEnergyStorage energyStorage = new SmallEnergyStorage() {

        // Creative item should not receive any energy...
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

        // Creative item should not receive any energy...
        @Override
        public int receiveEnergyInternal(int maxReceive, boolean simulate) {
            return 0;
        }

        // Creative item should not receive any energy...
        @Override
        public boolean canReceive(){
            return false;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return maxExtract;
        }

        @Override
        public int getEnergyStored(){
            return capacity;
        }

        @Override
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
    public void update() {
        if (!world.isRemote) {
            if(getEnergyStorage().getEnergyStored() < getEnergyStorage().getMaxEnergyStored()){
                getEnergyStorage().setEnergyStored(getEnergyStorage().getMaxEnergyStored());
            }
        }
        super.update();
    }

    @Override
    public SmallEnergyStorage getEnergyStorage() {
        return energyStorage;
    }
}
