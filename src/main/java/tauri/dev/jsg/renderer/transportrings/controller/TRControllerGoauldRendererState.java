package tauri.dev.jsg.renderer.transportrings.controller;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.renderer.activation.Activation;
import tauri.dev.jsg.renderer.activation.DHDActivation;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsAbstractTile;
import tauri.dev.jsg.transportrings.SymbolGoauldEnum;
import tauri.dev.jsg.transportrings.SymbolTypeTransportRingsEnum;
import tauri.dev.jsg.transportrings.TransportRingsAddress;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TRControllerGoauldRendererState extends TRControllerAbstractRendererState {
    private static final String SYMBOL_TEXTURE_BASE = "textures/tesr/transportrings/controller/goauld/goauld_button_";
    private static final String LIGHT_TEXTURE_BASE = "textures/tesr/transportrings/controller/goauld/goauld_light_";
    private static final String SYMBOL_TEXTURE_END = "jpg";
    private static final String LIGHT_TEXTURE_END = "jpg";
    private static final Map<BiomeOverlayEnum, TextureContainer> BIOME_TEXTURE_MAP = new HashMap<>();

    static {
        for (BiomeOverlayEnum biomeOverlay : BiomeOverlayEnum.values()) {
            TextureContainer container = new TextureContainer();

            for (int i = 0; i <= 5; i++) {
                container.SYMBOL_RESOURCE_MAP.put(i, new ResourceLocation(JSG.MOD_ID, SYMBOL_TEXTURE_BASE + i + biomeOverlay.suffix + "." + SYMBOL_TEXTURE_END));
                container.LIGHT_RESOURCE_MAP.put(i, new ResourceLocation(JSG.MOD_ID, LIGHT_TEXTURE_BASE + i + biomeOverlay.suffix + "." + LIGHT_TEXTURE_END));
            }

            BIOME_TEXTURE_MAP.put(biomeOverlay, container);
        }
    }

    // Symbols
    // Not saved
    public final Map<Integer, Integer> BUTTON_STATE_MAP = new HashMap<>(SymbolGoauldEnum.values().length);
    public List<Activation<SymbolInterface>> activationList = new ArrayList<>();

    public TRControllerGoauldRendererState(TransportRingsAddress addressDialed, BiomeOverlayEnum biomeOverride, boolean ringsAreConnected) {
        super(addressDialed, biomeOverride, ringsAreConnected);
    }

    public TRControllerGoauldRendererState(){
        super();
    }

    public TRControllerGoauldRendererState initClient(BlockPos pos, BiomeOverlayEnum biomeOverlay, TransportRingsAbstractTile rings) {
        super.initClient(pos, biomeOverlay, rings);

        for (SymbolGoauldEnum symbol : SymbolGoauldEnum.values()) {
            BUTTON_STATE_MAP.put(symbol.getId(), addressDialed.contains(symbol) ? 5 : 0);
        }

        return this;
    }

    private boolean isSymbolActiveClientSide(SymbolGoauldEnum symbol) {
        return BUTTON_STATE_MAP.get(symbol.getId()) != 0;
    }

    public void clearSymbols(long totalWorldTime) {
        for (SymbolGoauldEnum symbol : SymbolGoauldEnum.values()) {
            if (isSymbolActiveClientSide(symbol)) {
                activationList.add(new DHDActivation(symbol, totalWorldTime, true));
            }
        }
    }

    public void activateSymbol(long totalWorldTime, SymbolGoauldEnum symbol) {
        activationList.add(new DHDActivation(symbol, totalWorldTime, false));
    }

    public void deactivateSymbol(long totalWorldTime, SymbolGoauldEnum symbol) {
        activationList.add(new DHDActivation(symbol, totalWorldTime, true));
    }

    @Override
    public void iterate(World world, double partialTicks) {
        Activation.iterate(activationList, world.getTotalWorldTime(), partialTicks, (index, stage) -> {
            BUTTON_STATE_MAP.put(index.getId(), Math.round(stage));
        });
    }

    public ResourceLocation getButtonTexture(int symbolId, BiomeOverlayEnum biomeOverlay) {
        TextureContainer container = BIOME_TEXTURE_MAP.get(biomeOverlay);
        if(symbolId == SymbolGoauldEnum.LIGHT.id)
            return container.LIGHT_RESOURCE_MAP.get(BUTTON_STATE_MAP.get(symbolId));
        return container.SYMBOL_RESOURCE_MAP.get(BUTTON_STATE_MAP.get(symbolId));
    }

    @Override
    public boolean isButtonActive(SymbolInterface symbol) {
        return BUTTON_STATE_MAP.get((symbol).getId()) == 5;
    }

    @Override
    public int getActivatedButtons() {
        int count = 0;
        for (int state : BUTTON_STATE_MAP.values()) {
            if (state > 0) count++;
        }
        return count;
    }

    public void fromBytes(ByteBuf buf) {
        addressDialed = new TransportRingsAddress(SymbolTypeTransportRingsEnum.GOAULD);
        super.fromBytes(buf);
    }

    private static class TextureContainer {
        public final Map<Integer, ResourceLocation> SYMBOL_RESOURCE_MAP = new HashMap<>();
        public final Map<Integer, ResourceLocation> LIGHT_RESOURCE_MAP = new HashMap<>();
    }
}
