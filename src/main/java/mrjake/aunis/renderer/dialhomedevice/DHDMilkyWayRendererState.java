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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DHDMilkyWayRendererState extends DHDAbstractRendererState {
    private static final String SYMBOL_TEXTURE_BASE = "textures/tesr/milkyway/symbol";
    private static final String BRB_TEXTURE_BASE = "textures/tesr/milkyway/brb";
    private static final String SYMBOL_TEXTURE_END = "jpg";
    private static final String BRB_TEXTURE_END = "jpg";
    private static final Map<BiomeOverlayEnum, TextureContainer> BIOME_TEXTURE_MAP = new HashMap<>();

    static {
        for (BiomeOverlayEnum biomeOverlay : BiomeOverlayEnum.values()) {
            TextureContainer container = new TextureContainer();

            for (int i = 0; i <= 5; i++) {
                container.SYMBOL_RESOURCE_MAP.put(i, new ResourceLocation(Aunis.MOD_ID, SYMBOL_TEXTURE_BASE + i + biomeOverlay.suffix + "." + SYMBOL_TEXTURE_END));
                container.BRB_RESOURCE_MAP.put(i, new ResourceLocation(Aunis.MOD_ID, BRB_TEXTURE_BASE + i + biomeOverlay.suffix + "." + BRB_TEXTURE_END));
            }

            BIOME_TEXTURE_MAP.put(biomeOverlay, container);
        }
    }

    // Symbols
    // Not saved
    private final Map<SymbolInterface, Integer> BUTTON_STATE_MAP = new HashMap<>(38);
    public List<Activation<SymbolInterface>> activationList = new ArrayList<>();

    public DHDMilkyWayRendererState() {
    }

    public DHDMilkyWayRendererState(StargateAddressDynamic addressDialed, boolean brbActive, BiomeOverlayEnum biomeOverride, boolean stargateIsConnected) {
        super(addressDialed, brbActive, biomeOverride, stargateIsConnected);
    }

    public DHDMilkyWayRendererState initClient(BlockPos pos, float horizontalRotation, BiomeOverlayEnum biomeOverlay, boolean stargateIsConnected) {
        super.initClient(pos, horizontalRotation, biomeOverlay, stargateIsConnected);

        for (SymbolMilkyWayEnum symbol : SymbolMilkyWayEnum.values()) {
            if (symbol.brb())
                BUTTON_STATE_MAP.put(symbol, brbActive ? 5 : 0);
            else
                BUTTON_STATE_MAP.put(symbol, addressDialed.contains(symbol) ? 5 : 0);
        }

        return this;
    }

    private boolean isSymbolActiveClientSide(SymbolMilkyWayEnum symbol) {
        return BUTTON_STATE_MAP.get(symbol) != 0;
    }

    public void clearSymbols(long totalWorldTime) {
        for (SymbolMilkyWayEnum symbol : SymbolMilkyWayEnum.values()) {
            if (isSymbolActiveClientSide(symbol)) {
                activationList.add(new DHDActivation(symbol, totalWorldTime, true));
            }
        }
    }

    public void activateSymbol(long totalWorldTime, SymbolMilkyWayEnum symbol) {
        activationList.add(new DHDActivation(symbol, totalWorldTime, false));
    }

    @Override
    public void iterate(World world, double partialTicks) {
        Activation.iterate(activationList, world.getTotalWorldTime(), partialTicks, (index, stage) -> {
            BUTTON_STATE_MAP.put(index, Math.round(stage));
        });
    }

    public ResourceLocation getButtonTexture(SymbolMilkyWayEnum symbol, BiomeOverlayEnum biomeOverlay) {
        TextureContainer container = BIOME_TEXTURE_MAP.get(biomeOverlay);

        if (symbol.brb())
            return container.BRB_RESOURCE_MAP.get(BUTTON_STATE_MAP.get(symbol));

        return container.SYMBOL_RESOURCE_MAP.get(BUTTON_STATE_MAP.get(symbol));
    }

    @Override
    public boolean isButtonActive(SymbolInterface symbol) {
        return BUTTON_STATE_MAP.get((SymbolMilkyWayEnum) symbol) == 5;
    }

    @Override
    public int getActivatedButtons() {
        int count = 0;
        SymbolInterface origin = SymbolMilkyWayEnum.getOrigin();
        for (int state : BUTTON_STATE_MAP.values()) {
            if (state > 0) count++;
        }
        if (BUTTON_STATE_MAP.get((SymbolMilkyWayEnum) origin) > 0) count--;
        return count;
    }

    public void fromBytes(ByteBuf buf) {
        addressDialed = new StargateAddressDynamic(SymbolTypeEnum.MILKYWAY);
        super.fromBytes(buf);
    }

    private static class TextureContainer {
        public final Map<Integer, ResourceLocation> SYMBOL_RESOURCE_MAP = new HashMap<>();
        public final Map<Integer, ResourceLocation> BRB_RESOURCE_MAP = new HashMap<>();
    }
}