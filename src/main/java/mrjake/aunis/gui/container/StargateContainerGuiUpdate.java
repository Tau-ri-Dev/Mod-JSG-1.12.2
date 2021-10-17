package mrjake.aunis.gui.container;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.stargate.EnumIrisMode;
import mrjake.aunis.state.State;

public class StargateContainerGuiUpdate extends State {
	public StargateContainerGuiUpdate() {}
	
	public int energyStored;
	public int transferedLastTick;
	public float secondsToClose;
	public EnumIrisMode irisMode;
	public int irisCode;

	public StargateContainerGuiUpdate(int energyStored, int transferedLastTick, float secondsToClose, EnumIrisMode irisMode, int irisCode) {
		this.energyStored = energyStored;
		this.transferedLastTick = transferedLastTick;
		this.secondsToClose = secondsToClose;
		this.irisMode = irisMode;
		this.irisCode = irisCode;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(energyStored);
		buf.writeInt(transferedLastTick);
		buf.writeFloat(secondsToClose);
		buf.writeByte(irisMode.id);
		buf.writeInt(irisCode);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		energyStored = buf.readInt();
		transferedLastTick = buf.readInt();
		secondsToClose = buf.readFloat();
		irisMode = EnumIrisMode.getValue(buf.readByte());
		irisCode = buf.readInt();
	}
}
