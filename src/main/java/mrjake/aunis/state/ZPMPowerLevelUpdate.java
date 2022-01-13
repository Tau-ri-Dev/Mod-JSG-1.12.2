package mrjake.aunis.state;

import io.netty.buffer.ByteBuf;

public class ZPMPowerLevelUpdate extends State {
	public ZPMPowerLevelUpdate() {}

	public int powerLevel;

	public ZPMPowerLevelUpdate(int powerLevel) {
		this.powerLevel = powerLevel;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(powerLevel);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		powerLevel = buf.readInt();
	}

}
