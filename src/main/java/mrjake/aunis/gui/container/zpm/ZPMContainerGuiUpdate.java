package mrjake.aunis.gui.container.zpm;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.state.State;

public class ZPMContainerGuiUpdate extends State {
    public ZPMContainerGuiUpdate() {}

    public int energyStored;
    public int energyTransferedLastTick;

    public ZPMContainerGuiUpdate(int energyStored, int energyTransferedLastTick) {
        this.energyStored = energyStored;
        this.energyTransferedLastTick = energyTransferedLastTick;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(energyStored);
        buf.writeInt(energyTransferedLastTick);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        energyStored = buf.readInt();
        energyTransferedLastTick = buf.readInt();
    }
}
