package tauri.dev.jsg.gui.container.zpmhub;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.state.State;

public class ZPMHubContainerGuiUpdate extends State {
	public ZPMHubContainerGuiUpdate() {}
	
	public long energyStored;
	public long energyTransferedLastTick;
	
	public ZPMHubContainerGuiUpdate(long energyStored, long energyTransferedLastTick) {
		this.energyStored = energyStored;
		this.energyTransferedLastTick = energyTransferedLastTick;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(energyStored);
		buf.writeLong(energyTransferedLastTick);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		energyStored = buf.readLong();
		energyTransferedLastTick = buf.readLong();
	}
}
