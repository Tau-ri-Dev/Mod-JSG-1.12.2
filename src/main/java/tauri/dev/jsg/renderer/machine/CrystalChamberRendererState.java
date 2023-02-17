package tauri.dev.jsg.renderer.machine;

import net.minecraft.item.ItemStack;

public class CrystalChamberRendererState extends AbstractMachineRendererState {
    public CrystalChamberRendererState() {
        super();
    }

    public CrystalChamberRendererState(long workStateChanged, int machineProgress, boolean isWorking, ItemStack workingOnItemStack) {
        super(workStateChanged, machineProgress, isWorking, workingOnItemStack);
    }
}
