package mrjake.aunis.gui.container.zpm;

import mrjake.aunis.gui.container.CapacitorContainerGuiUpdate;

public class ZPMContainerGuiUpdate extends CapacitorContainerGuiUpdate {
    public ZPMContainerGuiUpdate() {}

    public ZPMContainerGuiUpdate(int energyStored, int energyTransferedLastTick) {
        this.energyStored = energyStored;
        this.energyTransferedLastTick = energyTransferedLastTick;
    }
}
