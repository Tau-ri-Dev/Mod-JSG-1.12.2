package tauri.dev.jsg.machine;

public abstract class AbstractMachineRecipe {
    public abstract int getWorkingTime(); // in ticks
    public abstract int getEnergyPerTick();
}
