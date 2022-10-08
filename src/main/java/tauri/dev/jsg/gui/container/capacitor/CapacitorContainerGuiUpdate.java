package tauri.dev.jsg.gui.container.capacitor;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.state.State;

public class CapacitorContainerGuiUpdate extends State {
	public CapacitorContainerGuiUpdate() {}
	
	public int energyStored;
	public int energyTransferedLastTick;
	
	public CapacitorContainerGuiUpdate(int energyStored, int energyTransferedLastTick) {
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
