package tauri.dev.jsg.gui.container.transportrings;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.state.State;

import java.nio.charset.StandardCharsets;

public class TRGuiUpdate extends State {
	public TRGuiUpdate() {}
	
	public int energyStored;
	public int transferedLastTick;
	public String ringsName;
	public int distance;

	public TRGuiUpdate(int energyStored, int transferedLastTick, String ringsName, int distance) {
		this.energyStored = energyStored;
		this.transferedLastTick = transferedLastTick;
		this.ringsName = ringsName;
		this.distance = distance;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(energyStored);
		buf.writeInt(transferedLastTick);
		buf.writeInt(ringsName.length());
		buf.writeCharSequence(ringsName, StandardCharsets.UTF_8);
		buf.writeInt(distance);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		energyStored = buf.readInt();
		transferedLastTick = buf.readInt();
		int size = buf.readInt();
		ringsName = buf.readCharSequence(size, StandardCharsets.UTF_8).toString();
		distance = buf.readInt();
	}
}
