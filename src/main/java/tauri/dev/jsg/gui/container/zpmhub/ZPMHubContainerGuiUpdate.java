package tauri.dev.jsg.gui.container.zpmhub;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.state.State;

public class ZPMHubContainerGuiUpdate extends State {
	public ZPMHubContainerGuiUpdate() {}
	
	public int energyStored;
	public int energyTransferedLastTick;
	
	public ZPMHubContainerGuiUpdate(int energyStored, int energyTransferedLastTick) {
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
