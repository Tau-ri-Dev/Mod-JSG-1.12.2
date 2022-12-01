package tauri.dev.jsg.gui.container.machine.assembler;

import tauri.dev.jsg.gui.container.machine.AbstractMachineContainerGuiUpdate;

public class AssemblerContainerGuiUpdate extends AbstractMachineContainerGuiUpdate {
    public AssemblerContainerGuiUpdate() {
        super();
    }

    public AssemblerContainerGuiUpdate(int energyStored, int energyTransferedLastTick, long machineStart, long machineEnd) {
        super(energyStored, energyTransferedLastTick, machineStart, machineEnd);
    }
}
