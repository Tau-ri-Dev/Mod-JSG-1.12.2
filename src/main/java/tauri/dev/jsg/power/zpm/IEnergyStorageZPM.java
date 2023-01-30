package tauri.dev.jsg.power.zpm;

public interface IEnergyStorageZPM {
    int receiveEnergy(int maxReceive, boolean simulate);
    int extractEnergy(int maxExtract, boolean simulate);
    long getEnergyStored();
    long getMaxEnergyStored();
    boolean canExtract();
    boolean canReceive();
}
