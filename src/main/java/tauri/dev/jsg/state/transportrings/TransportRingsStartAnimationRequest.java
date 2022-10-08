package tauri.dev.jsg.state.transportrings;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.config.ingame.JSGTileEntityConfig;
import tauri.dev.jsg.state.State;

public class TransportRingsStartAnimationRequest extends State {
	public TransportRingsStartAnimationRequest() {}
	
	public long animationStart;
	public int ringsDistance;
	public JSGTileEntityConfig ringsConfig = new JSGTileEntityConfig();
	
	public TransportRingsStartAnimationRequest(long animationStart, int distance, JSGTileEntityConfig config) {
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
