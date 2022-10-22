package tauri.dev.jsg.gui.container.machine.sgassembler;

import tauri.dev.jsg.gui.container.machine.AbstractAssemblerContainerGuiUpdate;

public class SGAssemblerGuiUpdate extends AbstractAssemblerContainerGuiUpdate {

    public SGAssemblerGuiUpdate(){
        super();
    }

    public SGAssemblerGuiUpdate(int energyStored, int energyTransferedLastTick, long machineStart, long machineEnd) {
        super(energyStored, energyTransferedLastTick, machineStart, machineEnd);
    }
}
