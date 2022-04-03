package mrjake.aunis.state.transportrings;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.state.State;
import mrjake.aunis.util.AunisAxisAlignedBB;

public class TransportRingsStartAnimationRequest extends State {
	public TransportRingsStartAnimationRequest() {}
	
	public long animationStart;
	public int ringsDistance;
	
	public TransportRingsStartAnimationRequest(long animationStart, int distance) {
		this.animationStart = animationStart;
		this.ringsDistance = distance;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(animationStart);
		buf.writeInt(ringsDistance);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		animationStart = buf.readLong();
		ringsDistance = buf.readInt();
	}

}
