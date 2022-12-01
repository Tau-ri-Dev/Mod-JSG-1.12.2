package tauri.dev.jsg.renderer.machine;

import net.minecraft.item.ItemStack;

public class CrystalChamberRendererState extends AbstractMachineRendererState {
    public CrystalChamberRendererState() {
        super();
    }

    public CrystalChamberRendererState(int machineProgress, boolean isWorking, ItemStack workingOnItemStack) {
        super(machineProgress, isWorking, workingOnItemStack);
    }
}
