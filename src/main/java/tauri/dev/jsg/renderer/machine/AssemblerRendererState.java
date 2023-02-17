package tauri.dev.jsg.renderer.machine;

import net.minecraft.item.ItemStack;

public class AssemblerRendererState extends AbstractMachineRendererState {
    public AssemblerRendererState() {
        super();
    }

    public AssemblerRendererState(long workStateChanged, int machineProgress, boolean isWorking, ItemStack workingOnItemStack) {
        super(workStateChanged, machineProgress, isWorking, workingOnItemStack);
    }
}
