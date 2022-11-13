package tauri.dev.jsg.transportrings;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public enum SymbolGoauldEnum implements SymbolInterface {
    AMUN(0, 0, "Amun"),
    SERKET(1, 1, "Serket"),
    KHEPRI(2, 3, "Khepri"),
    RA(3, 2, "Ra"),
    FELLUCA(4, 4, "Felluca"),
    COBRA(5, 5, "Cobra"),
    LIGHT(6, 6, "Light");

    public int id;
    public int angleIndex;

    public String englishName;
    public String translationKey;
    public ResourceLocation iconResource;
    public ResourceLocation modelResource;

    SymbolGoauldEnum(int id, int angleIndex, String englishName) {
        this.id = id;

        this.angleIndex = angleIndex;

        this.englishName = englishName;
        this.translationKey = "glyph.jsg.transportrings.goauld." + englishName.toLowerCase().replace(" ", "_");
        this.iconResource = new ResourceLocation(JSG.MOD_ID, "textures/gui/symbol/transportrings/goauld/" + englishName.toLowerCase() + ".png");
        this.modelResource = new ResourceLocation(JSG.MOD_ID, "models/tesr/transportrings/controller/goauld/goauld_button_" + (id+1) + ".obj");
    }

    public static SymbolGoauldEnum getOrigin() {
        return COBRA;
    }

    @Override
    public boolean origin() {
        return this == getOrigin();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public float getAngle() {
        return angleIndex;
    }

    @Override
    public int getAngleIndex() {
        return angleIndex;
    }

    @Override
    public String getEnglishName() {
        return englishName;
    }

    @Override
    public String toString() {
        return getEnglishName();
    }

    @Override
    public ResourceLocation getIconResource(BiomeOverlayEnum overlay, int dimId) {
        return iconResource;
    }

    @Override
    public String localize() {
        return JSG.proxy.localize(translationKey);
    }

    @Override
    public SymbolTypeEnum getSymbolType() {
        return null;
    }

    public SymbolTypeTransportRingsEnum getTRSymbolType() {
        return SymbolTypeTransportRingsEnum.GOAULD;
    }

    public static SymbolGoauldEnum getRandomSymbol(Random random) {
        int id = 0;
        do {
            id = random.nextInt(5);
        } while (id == getOrigin().id || id == LIGHT.id);

        return valueOf(id);
    }

    public static boolean validateDialedAddress(TransportRingsAddress address) {
        if (address.size() < 5)
            return false;

        if (!address.get(address.size()-1).origin())
            return false;

        return true;
    }

    public static List<SymbolInterface> stripOrigin(List<SymbolInterface> dialedAddress) {
        return dialedAddress.subList(0, dialedAddress.size() - 1);
    }

    private static final Map<Integer, SymbolGoauldEnum> ID_MAP = new HashMap<>();
    private static final Map<Integer, SymbolGoauldEnum> ANGLE_INDEX_MAP = new HashMap<>();
    private static final Map<String, SymbolGoauldEnum> ENGLISH_NAME_MAP = new HashMap<>();

    static {
        for (SymbolGoauldEnum symbol : values()) {
            ID_MAP.put(symbol.id, symbol);
            ANGLE_INDEX_MAP.put(symbol.angleIndex, symbol);
            ENGLISH_NAME_MAP.put(symbol.englishName.toLowerCase(), symbol);
        }
    }

    public static SymbolGoauldEnum valueOf(int id) {
        return ID_MAP.get(id);
    }

    public static SymbolGoauldEnum fromEnglishName(String englishName) {
        return ENGLISH_NAME_MAP.get(englishName.toLowerCase().replace("ö", "o"));
    }

    public static SymbolGoauldEnum getSymbolByAngleIndex(int index){
        return ANGLE_INDEX_MAP.get(index);
    }
}
