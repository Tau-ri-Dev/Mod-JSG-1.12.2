package tauri.dev.jsg.tileentity.machine;

import net.minecraft.item.Item;
import tauri.dev.jsg.gui.container.machine.sgassembler.SGAssemblerGuiUpdate;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateTypeEnum;

public class StargateAssemblerTile extends AbstractAssemblerTile {
    @Override
    public Item[] getAllowedSchematics() {
        return JSGItems.SG_SCHEMATICS_ITEMS;
    }

    @Override
    public State getState(StateTypeEnum stateType) {
        if (stateType == StateTypeEnum.GUI_UPDATE) {
            return new SGAssemblerGuiUpdate(energyStorage.getEnergyStored(), energyTransferedLastTick, machineStart, machineEnd);
        }
        return null;
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        if (stateType == StateTypeEnum.GUI_UPDATE) {
            return new SGAssemblerGuiUpdate();
        }
        return null;
    }

    @Override
    public void setState(StateTypeEnum stateType, State state) {
        if (stateType == StateTypeEnum.GUI_UPDATE) {
            SGAssemblerGuiUpdate guiUpdate = (SGAssemblerGuiUpdate) state;
            energyStorage.setEnergyStored(guiUpdate.energyStored);
            energyTransferedLastTick = guiUpdate.energyTransferedLastTick;
            machineStart = guiUpdate.machineStart;
            machineEnd = guiUpdate.machineEnd;
            markDirty();
        }
    }
}
