package mrjake.aunis.gui.container.transportrings;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.stargate.EnumIrisMode;
import mrjake.aunis.state.State;

public class TRGuiUpdate extends State {
	public TRGuiUpdate() {}
	
	public int energyStored;
	public int transferedLastTick;

	public TRGuiUpdate(int energyStored, int transferedLastTick) {
		this.energyStored = energyStored;
		this.transferedLastTick = transferedLastTick;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(energyStored);
		buf.writeInt(transferedLastTick);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		energyStored = buf.readInt();
		transferedLastTick = buf.readInt();
	}
}
