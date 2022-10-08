package tauri.dev.jsg.renderer.stargate;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.stargate.EnumStargateState;
import tauri.dev.jsg.state.State;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class StargateAbstractRendererState extends State {

    public StargateAbstractRendererState() {}
	
	protected StargateAbstractRendererState(StargateAbstractRendererStateBuilder builder) {
		if (builder.stargateState.engaged()) {
			doEventHorizonRender = true;
			vortexState = StargateAbstractRenderer.EnumVortexState.STILL;
		}
	}
	
	public StargateAbstractRendererState initClient(BlockPos pos, EnumFacing facing, BiomeOverlayEnum biomeOverlay) {
		this.pos = pos;
		this.facing = facing;
		
		if (facing.getAxis() == EnumFacing.Axis.X)
			facing = facing.getOpposite();
		
		this.horizontalRotation = facing.getHorizontalAngle();
		this.verticalRotation = facing.getHorizontalAngle();
		this.biomeOverlay = biomeOverlay;
		
		return this;
	}
	
	// Global
	// Not saved
	public BlockPos pos;
	public EnumFacing facing;
	public float horizontalRotation;
	public float verticalRotation;
	private BiomeOverlayEnum biomeOverlay;
	
	// Gate
	// Saved
	public boolean doEventHorizonRender = false;
	public StargateAbstractRenderer.EnumVortexState vortexState = StargateAbstractRenderer.EnumVortexState.FORMING;
	
	// Event horizon
	// Not saved
	public StargateRendererStatic.QuadStrip backStrip;
	public boolean backStripClamp;
	public Float whiteOverlayAlpha;
	public long gateWaitStart = 0;
	public long gateWaitClose = 0;
	public boolean zeroAlphaSet;	
	public boolean horizonUnstable = false;
	public int horizonSegments = 0;
	
	public void openGate(long totalWorldTime) {		
		gateWaitStart = totalWorldTime;
		
		zeroAlphaSet = false;
		backStripClamp = true;
		whiteOverlayAlpha = 1.0f;
		
		vortexState = StargateAbstractRenderer.EnumVortexState.FORMING;
		doEventHorizonRender = true;
	}
	
	public void closeGate(long totalWorldTime) {		
		gateWaitClose = totalWorldTime;
		vortexState = StargateAbstractRenderer.EnumVortexState.CLOSING;
	}
	
	public BiomeOverlayEnum getBiomeOverlay() {
		return biomeOverlay;
	}
	
	public void setBiomeOverlay(BiomeOverlayEnum biomeOverlay) {
		this.biomeOverlay = biomeOverlay;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(doEventHorizonRender);
		buf.writeInt(vortexState.index);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {		
		doEventHorizonRender = buf.readBoolean();
		vortexState = StargateAbstractRenderer.EnumVortexState.valueOf( buf.readInt() );
	}
	
	
	// ------------------------------------------------------------------------
	// Builder
	
	public static StargateAbstractRendererStateBuilder builder() {
		return new StargateAbstractRendererStateBuilder();
	}
	
	public static class StargateAbstractRendererStateBuilder {
		
		// Gate
		protected EnumStargateState stargateState;

		public StargateAbstractRendererStateBuilder setStargateState(EnumStargateState stargateState) {
			this.stargateState = stargateState;
			return this;
		}
		
		public StargateAbstractRendererState build() {
			return new StargateAbstractRendererState(this);
		}
	}
}
