package mrjake.aunis.renderer.dialhomedevice;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.Aunis;
import mrjake.aunis.item.AunisItems;
import mrjake.aunis.renderer.activation.Activation;
import mrjake.aunis.renderer.activation.DHDActivation;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.stargate.network.*;
import mrjake.aunis.state.State;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DHDAbstractRendererState extends State {
	public DHDAbstractRendererState() {}

	public DHDAbstractRendererState(StargateAddressDynamic addressDialed, boolean brbActive, BiomeOverlayEnum biomeOverride, boolean stargateIsConnected) {
		this.addressDialed = addressDialed;
		this.brbActive = brbActive;
		this.biomeOverride = biomeOverride;
		this.stargateIsConnected = stargateIsConnected;
	}
	
	public DHDAbstractRendererState initClient(BlockPos pos, float horizontalRotation, BiomeOverlayEnum biomeOverlay, boolean stargateIsConnected) {
		this.pos = pos;
		this.horizontalRotation = horizontalRotation;
		this.biomeOverlay = biomeOverlay;
		this.stargateIsConnected = stargateIsConnected;
		return this;
	}

	public void setIsConnected(boolean connected){
		stargateIsConnected = connected;
	}
	
	
	// Global
	// Not saved
	public BlockPos pos;
	public float horizontalRotation;
	protected BiomeOverlayEnum biomeOverlay;
	// Saved
	public boolean stargateIsConnected;
	
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

	public abstract boolean isButtonActive(SymbolInterface symbol);
	public abstract int getActivatedButtons();

	
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

		buf.writeBoolean(stargateIsConnected);
	}

	public void fromBytes(ByteBuf buf) {
		addressDialed.fromBytes(buf);
		brbActive = buf.readBoolean();
		
		if (buf.readBoolean()) {
			biomeOverride = BiomeOverlayEnum.values()[buf.readInt()];
		}
		stargateIsConnected = buf.readBoolean();
	}
}