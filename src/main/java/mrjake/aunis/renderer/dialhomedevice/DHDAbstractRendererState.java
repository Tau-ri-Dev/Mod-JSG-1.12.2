package mrjake.aunis.renderer.dialhomedevice;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.Aunis;
import mrjake.aunis.renderer.activation.Activation;
import mrjake.aunis.renderer.activation.DHDActivation;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.stargate.network.StargateAddressDynamic;
import mrjake.aunis.stargate.network.SymbolInterface;
import mrjake.aunis.stargate.network.SymbolMilkyWayEnum;
import mrjake.aunis.stargate.network.SymbolTypeEnum;
import mrjake.aunis.state.State;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DHDAbstractRendererState extends State {
	public DHDAbstractRendererState() {}

	public DHDAbstractRendererState(StargateAddressDynamic addressDialed, boolean brbActive, BiomeOverlayEnum biomeOverride) {
		this.addressDialed = addressDialed;
		this.brbActive = brbActive;
		this.biomeOverride = biomeOverride;
	}
	
	public DHDAbstractRendererState initClient(BlockPos pos, float horizontalRotation, BiomeOverlayEnum biomeOverlay) {
		this.pos = pos;
		this.horizontalRotation = horizontalRotation;
		this.biomeOverlay = biomeOverlay;
		return this;
	}
	
	
	// Global
	// Not saved
	public BlockPos pos;
	public float horizontalRotation;
	protected BiomeOverlayEnum biomeOverlay;
	
	// Symbols
	// Not saved
	// Saved
	public StargateAddressDynamic addressDialed;
	public boolean brbActive;
	
	// Biome Override
	// Saved
	public BiomeOverlayEnum biomeOverride;
	
	public BiomeOverlayEnum getBiomeOverlay() {
		if (biomeOverride != null)
			return biomeOverride;
		
		return biomeOverlay;
	}
	
	public void setBiomeOverlay(BiomeOverlayEnum biomeOverlay) {
		this.biomeOverlay = biomeOverlay;
	}

	public abstract void iterate(World world, double partialTicks);
	
	
	public void toBytes(ByteBuf buf) {
		addressDialed.toBytes(buf);
		buf.writeBoolean(brbActive);
		
		if (biomeOverride != null) {
			buf.writeBoolean(true);
			buf.writeInt(biomeOverride.ordinal());
		}
		
		else {
			buf.writeBoolean(false);
		}
	}

	public void fromBytes(ByteBuf buf) {
		addressDialed.fromBytes(buf);
		brbActive = buf.readBoolean();
		
		if (buf.readBoolean()) {
			biomeOverride = BiomeOverlayEnum.values()[buf.readInt()];
		}
	}
}