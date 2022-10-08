package tauri.dev.jsg.gui.container.dhd;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.tileentity.util.ReactorStateEnum;

public class DHDContainerGuiUpdate extends State {
	public DHDContainerGuiUpdate() {}
	
	public int fluidAmount;
	public int tankCapacity;
	public ReactorStateEnum reactorState;
	public boolean isLinked;
	
	public DHDContainerGuiUpdate(int fluidAmount, int tankCapacity, ReactorStateEnum reactorState, boolean isLinked) {
		this.fluidAmount = fluidAmount;
		this.tankCapacity = tankCapacity;
		this.reactorState = reactorState;
		this.isLinked = isLinked;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(fluidAmount);
		buf.writeInt(tankCapacity);
		buf.writeShort(reactorState.getKey());
		buf.writeBoolean(isLinked);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		fluidAmount = buf.readInt();
		tankCapacity = buf.readInt();
		reactorState = ReactorStateEnum.valueOf(buf.readShort());
		isLinked = buf.readBoolean();
	}

}
