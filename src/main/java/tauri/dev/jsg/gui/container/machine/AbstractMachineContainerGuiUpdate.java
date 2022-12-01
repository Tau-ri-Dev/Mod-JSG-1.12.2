package tauri.dev.jsg.gui.container.machine;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.state.State;

public abstract class AbstractMachineContainerGuiUpdate extends State {
    public AbstractMachineContainerGuiUpdate() {
    }

    public int energyStored;
    public int energyTransferedLastTick;
    public long machineStart;
    public long machineEnd;

    public AbstractMachineContainerGuiUpdate(int energyStored, int energyTransferedLastTick, long machineStart, long machineEnd) {
        this.energyStored = energyStored;
        this.energyTransferedLastTick = energyTransferedLastTick;
        this.machineStart = machineStart;
        this.machineEnd = machineEnd;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(energyStored);
        buf.writeInt(energyTransferedLastTick);
        buf.writeLong(machineStart);
        buf.writeLong(machineEnd);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        energyStored = buf.readInt();
        energyTransferedLastTick = buf.readInt();
        machineStart = buf.readLong();
        machineEnd = buf.readLong();
    }
}
