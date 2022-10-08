package tauri.dev.jsg.state.stargate;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.state.State;
import net.minecraft.util.math.BlockPos;

public class StargateVaporizeBlockParticlesRequest extends State {
	public StargateVaporizeBlockParticlesRequest() {}
	
	public BlockPos block;
	
	public StargateVaporizeBlockParticlesRequest(BlockPos block) {
		this.block = block;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(block.toLong());
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		block = BlockPos.fromLong(buf.readLong());
	}
}
