package tauri.dev.jsg.renderer.machine;

import net.minecraft.item.ItemStack;

public class AssemblerRendererState extends AbstractMachineRendererState {
    public AssemblerRendererState() {
        super();
    }

    public AssemblerRendererState(int machineProgress, boolean isWorking, ItemStack workingOnItemStack) {
        super(machineProgress, isWorking, workingOnItemStack);
    }
}
