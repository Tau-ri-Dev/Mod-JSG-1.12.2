package tauri.dev.jsg.power.zpm;

import java.util.ArrayList;
import java.util.List;

public class ZPMHubEnergyStorage extends ZPMEnergyStorage {

    private final List<IEnergyStorageZPM> storages = new ArrayList<>();

    public ZPMHubEnergyStorage(int maxTransfer) {
        super(0, maxTransfer);
    }

    public void clearStorages() {
        storages.clear();
    }

    public void addStorage(IEnergyStorageZPM storage) {
        storages.add(storage);
    }

    @Override
    public long getEnergyStored() {
        long energyStored = this.energy;

        for (IEnergyStorageZPM storage : storages)
            energyStored += storage.getEnergyStored();

        return energyStored;
    }

    @Override
    public long getMaxEnergyStored() {
        long maxEnergyStored = this.capacity;

        for (IEnergyStorageZPM storage : storages)
            maxEnergyStored += storage.getMaxEnergyStored();

        return maxEnergyStored;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int toReceive = maxReceive;
        toReceive -= super.receiveEnergy(maxReceive, simulate);

        for (IEnergyStorageZPM storage : storages) {
            if (toReceive == 0)
                return maxReceive;

            toReceive -= storage.receiveEnergy(toReceive, simulate);
        }

        return maxReceive - toReceive;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int toExtract = maxExtract;

        for (IEnergyStorageZPM storage : storages) {
            if (toExtract == 0)
                return maxExtract;

            toExtract -= storage.extractEnergy(toExtract, simulate);
        }

        toExtract -= super.extractEnergy(toExtract, simulate);
        return maxExtract - toExtract;
    }

    public void setEnergyStoredInternally(long energy) {
        this.energy = energy;
    }

    public long getEnergyStoredInternally() {
        return this.energy;
    }

    public long getMaxEnergyStoredInternally() {
        return this.capacity;
    }
}
