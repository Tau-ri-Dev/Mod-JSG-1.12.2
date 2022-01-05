package mrjake.aunis.state.transportrings;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.state.State;

public class TransportRingsStartAnimationRequest extends State {
	public TransportRingsStartAnimationRequest() {}
	
	public long animationStart;
	
	public TransportRingsStartAnimationRequest(long animationStart) {
		this.animationStart = animationStart;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(animationStart);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		animationStart = buf.readLong();
	}

}
