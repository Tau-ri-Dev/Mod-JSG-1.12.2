package mrjake.aunis.renderer.dialhomedevice;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.Aunis;
import mrjake.aunis.renderer.activation.Activation;
import mrjake.aunis.renderer.activation.DHDPegasusActivation;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.stargate.network.StargateAddressDynamic;
import mrjake.aunis.stargate.network.SymbolPegasusEnum;
import mrjake.aunis.stargate.network.SymbolTypeEnum;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DHDPegasusRendererState extends DHDAbstractRendererState {
	public DHDPegasusRendererState() {}

	private static final String SYMBOL_TEXTURE_BASE = "textures/tesr/pegasus/symbol";
	private static final String BRB_TEXTURE_BASE = "textures/tesr/pegasus/brb";
	private static final String SYMBOL_TEXTURE_END = "png";
	private static final String BRB_TEXTURE_END = "jpg";

	private static final Map<BiomeOverlayEnum, TextureContainer> BIOME_TEXTURE_MAP = new HashMap<>();

	private static class TextureContainer {
		public final Map<Integer, ResourceLocation> SYMBOL_RESOURCE_MAP = new HashMap<>();
		public final Map<Integer, ResourceLocation> BRB_RESOURCE_MAP = new HashMap<>();
	}

	static {
		for (BiomeOverlayEnum biomeOverlay : BiomeOverlayEnum.values()) {
			TextureContainer container = new TextureContainer();

			for (int i=0; i<=5; i++) {
				container.SYMBOL_RESOURCE_MAP.put(i, new ResourceLocation(Aunis.ModID, SYMBOL_TEXTURE_BASE + i + biomeOverlay.suffix + "." + SYMBOL_TEXTURE_END));
				container.BRB_RESOURCE_MAP.put(i, new ResourceLocation(Aunis.ModID, BRB_TEXTURE_BASE + i + biomeOverlay.suffix + "." + BRB_TEXTURE_END));
			}

			BIOME_TEXTURE_MAP.put(biomeOverlay, container);
		}
	}

	public DHDPegasusRendererState(StargateAddressDynamic addressDialed, boolean brbActive, BiomeOverlayEnum biomeOverride) {
		super(addressDialed, brbActive, biomeOverride);
	}

	public DHDPegasusRendererState initClient(BlockPos pos, float horizontalRotation, BiomeOverlayEnum biomeOverlay) {
		super.initClient(pos, horizontalRotation, biomeOverlay);

		for (SymbolPegasusEnum symbol : SymbolPegasusEnum.values()) {
			if (symbol.brb())
				BUTTON_STATE_MAP.put(symbol, brbActive ? 5 : 0);
			else
				BUTTON_STATE_MAP.put(symbol, addressDialed.contains(symbol) ? 5 : 0);
		}

		return this;
	}

	// Symbols
	// Not saved
	private final Map<SymbolPegasusEnum, Integer> BUTTON_STATE_MAP = new HashMap<>(38);
	public List<Activation<SymbolPegasusEnum>> activationList = new ArrayList<>();

	private boolean isSymbolActiveClientSide(SymbolPegasusEnum symbol) {
		return BUTTON_STATE_MAP.get(symbol) != 0;
	}

	public void clearSymbols(long totalWorldTime) {
		for (SymbolPegasusEnum symbol : SymbolPegasusEnum.values()) {
			if (isSymbolActiveClientSide(symbol)) {
				activationList.add(new DHDPegasusActivation(symbol, totalWorldTime, true));
			}
		}
	}

	public void activateSymbol(long totalWorldTime, SymbolPegasusEnum symbol) {
		activationList.add(new DHDPegasusActivation(symbol, totalWorldTime, false));
	}

	@Override
	public void iterate(World world, double partialTicks) {
		Activation.iterate(activationList, world.getTotalWorldTime(), partialTicks, (index, stage) -> {
			BUTTON_STATE_MAP.put(index, Math.round(stage));
		});
	}

	public ResourceLocation getButtonTexture(SymbolPegasusEnum symbol, BiomeOverlayEnum biomeOverlay) {
		TextureContainer container = BIOME_TEXTURE_MAP.get(biomeOverlay);

		if (symbol.brb())
			return container.BRB_RESOURCE_MAP.get(BUTTON_STATE_MAP.get(symbol));

		return container.SYMBOL_RESOURCE_MAP.get(BUTTON_STATE_MAP.get(symbol));
	}

	public void fromBytes(ByteBuf buf) {
		addressDialed = new StargateAddressDynamic(SymbolTypeEnum.PEGASUS);
		super.fromBytes(buf);
	}
}