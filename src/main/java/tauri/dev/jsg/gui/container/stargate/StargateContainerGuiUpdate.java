package tauri.dev.jsg.gui.container.stargate;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.stargate.EnumIrisMode;
import tauri.dev.jsg.state.State;

import java.nio.charset.StandardCharsets;

public class StargateContainerGuiUpdate extends State {
	public StargateContainerGuiUpdate() {}
	
	public int energyStored;
	public int transferedLastTick;
	public float secondsToClose;
	public EnumIrisMode irisMode;
	public String irisCode;
	public long openedSince;
	public double gateTemp;
	public double irisTemp;

	public StargateContainerGuiUpdate(int energyStored, int transferedLastTick, float secondsToClose, EnumIrisMode irisMode, String irisCode, long openedSince, double gateTemp, double irisTemp) {
		this.energyStored = energyStored;
		this.transferedLastTick = transferedLastTick;
		this.secondsToClose = secondsToClose;
		this.irisMode = irisMode;
		this.irisCode = irisCode;
		this.openedSince = openedSince;
		this.gateTemp = gateTemp;
		this.irisTemp = irisTemp;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(energyStored);
		buf.writeInt(transferedLastTick);
		buf.writeFloat(secondsToClose);
		buf.writeByte(irisMode.id);
		buf.writeInt(irisCode.length());
		buf.writeCharSequence(irisCode, StandardCharsets.UTF_8);
		buf.writeLong(openedSince);
		buf.writeDouble(gateTemp);
		buf.writeDouble(irisTemp);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		energyStored = buf.readInt();
		transferedLastTick = buf.readInt();
		secondsToClose = buf.readFloat();
		irisMode = EnumIrisMode.getValue(buf.readByte());
		int codeSize = buf.readInt();
		irisCode = buf.readCharSequence(codeSize, StandardCharsets.UTF_8).toString();
		openedSince = buf.readLong();
		gateTemp = buf.readDouble();
		irisTemp = buf.readDouble();
	}
}
