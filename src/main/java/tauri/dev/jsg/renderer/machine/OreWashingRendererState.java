package tauri.dev.jsg.renderer.machine;

import net.minecraft.item.ItemStack;

public class OreWashingRendererState extends AbstractMachineRendererState {
    public OreWashingRendererState() {
        super();
    }

    public OreWashingRendererState(long workStateChanged, int machineProgress, boolean isWorking, ItemStack workingOnItemStack) {
        super(workStateChanged, machineProgress, isWorking, workingOnItemStack);
    }
}
