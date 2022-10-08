package tauri.dev.jsg.gui.container.stargate;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.stargate.EnumIrisMode;
import tauri.dev.jsg.state.State;

public class StargateContainerGuiUpdate extends State {
	public StargateContainerGuiUpdate() {}
	
	public int energyStored;
	public int transferedLastTick;
	public float secondsToClose;
	public EnumIrisMode irisMode;
	public int irisCode;
	public float openedSeconds;

	public StargateContainerGuiUpdate(int energyStored, int transferedLastTick, float secondsToClose, EnumIrisMode irisMode, int irisCode, float openedSeconds) {
		this.energyStored = energyStored;
		this.transferedLastTick = transferedLastTick;
		this.secondsToClose = secondsToClose;
		this.irisMode = irisMode;
		this.irisCode = irisCode;
		this.openedSeconds = openedSeconds;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(energyStored);
		buf.writeInt(transferedLastTick);
		buf.writeFloat(secondsToClose);
		buf.writeByte(irisMode.id);
		buf.writeInt(irisCode);
		buf.writeFloat(openedSeconds);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		energyStored = buf.readInt();
		transferedLastTick = buf.readInt();
		secondsToClose = buf.readFloat();
		irisMode = EnumIrisMode.getValue(buf.readByte());
		irisCode = buf.readInt();
		openedSeconds = buf.readFloat();
	}
}
