package mrjake.aunis.state.transportrings;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.config.ingame.AunisTileEntityConfig;
import mrjake.aunis.state.State;
import mrjake.aunis.util.AunisAxisAlignedBB;

public class TransportRingsStartAnimationRequest extends State {
	public TransportRingsStartAnimationRequest() {}
	
	public long animationStart;
	public int ringsDistance;
	public AunisTileEntityConfig ringsConfig = new AunisTileEntityConfig();
	
	public TransportRingsStartAnimationRequest(long animationStart, int distance, AunisTileEntityConfig config) {
		this.animationStart = animationStart;
		this.ringsDistance = distance;
		this.ringsConfig = config;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(animationStart);
		buf.writeInt(ringsDistance);
		ringsConfig.toBytes(buf);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		animationStart = buf.readLong();
		ringsDistance = buf.readInt();
		ringsConfig.fromBytes(buf);
	}

}
