package mrjake.aunis.state;

import io.netty.buffer.ByteBuf;

public class TransportRingsStartAnimationRequest extends State {
	public TransportRingsStartAnimationRequest() {}
	
	public long animationStart;
	public int ringsHeight;

	public TransportRingsStartAnimationRequest(long animationStart, int height) {
		this.animationStart = animationStart;
		this.ringsHeight = height;
		System.out.println("L: " + this.ringsHeight);
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(animationStart);
		buf.writeInt(ringsHeight);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		animationStart = buf.readLong();
		ringsHeight = buf.readInt();
	}

}
