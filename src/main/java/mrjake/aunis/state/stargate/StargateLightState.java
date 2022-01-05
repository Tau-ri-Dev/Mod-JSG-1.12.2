package mrjake.aunis.state.stargate;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.state.State;

public class StargateLightState extends State {
	public StargateLightState() {}
	
	
	private boolean isLitUp;
	
	public StargateLightState(boolean isLitUp) {
		this.isLitUp = isLitUp;
	}
	
	public boolean isLitUp() {
		return isLitUp;
	}
	
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(isLitUp);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		isLitUp = buf.readBoolean();
	}
}
