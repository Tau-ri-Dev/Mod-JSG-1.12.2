package tauri.dev.jsg.transportrings;

import net.minecraft.util.ResourceLocation;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public enum SymbolAncientEnum implements SymbolInterface {
    UNUS(0, 0, "Unus"),
    DUO(1, 1, "Duo"),
    TRIBUS(2, 2, "Tribus"),
    QUATTUOR(3, 3, "Quattuor"),
    QUINQUE(4, 4, "Quinque"),
    SEX(5, 5, "Sex"),
    SEPTEM(6, 6, "Septem"),
    OCTINGENTI(7, 7, "Octingenti"),
    NOVEM(8, 8, "Novem"),
    LIGHT(9, 9, "Light");

    public final int id;
    public final int angleIndex;

    public final String englishName;
    public final String translationKey;
    public final ResourceLocation iconResource;
    public final ResourceLocation modelResource;

    SymbolAncientEnum(int id, int angleIndex, String englishName) {
        this.id = id;

        this.angleIndex = angleIndex;

        this.englishName = englishName;
        this.translationKey = "glyph.jsg.transportrings.ancient." + englishName.toLowerCase().replace(" ", "_");
        this.iconResource = new ResourceLocation(JSG.MOD_ID, "textures/gui/symbol/transportrings/ancient/" + englishName.toLowerCase() + ".png");
        this.modelResource = new ResourceLocation(JSG.MOD_ID, "models/tesr/transportrings/controller/ancient/ancient_button_" + (id + 1) + ".obj");
    }

    @Override
    public boolean origin() {
        return this == UNUS;
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
    public ResourceLocation getIconResource(BiomeOverlayEnum overlay, int dimId, int configOrigin) {
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

    public static SymbolAncientEnum getRandomSymbol(Random random) {
        int id;
        do {
            id = random.nextInt(9);
        } while (id == UNUS.id || id == LIGHT.id);

        return valueOf(id);
    }

    public static boolean validateDialedAddress(TransportRingsAddress address) {
        if (address.size() < 5)
            return false;

        return address.get(address.size() - 1).origin();
    }

    public static List<SymbolInterface> stripOrigin(List<SymbolInterface> dialedAddress) {
        return dialedAddress.subList(0, dialedAddress.size() - 1);
    }

    public static SymbolAncientEnum getOrigin() {
        return UNUS;
    }

    private static final Map<Integer, SymbolAncientEnum> ID_MAP = new HashMap<>();
    private static final Map<Integer, SymbolAncientEnum> ANGLE_INDEX_MAP = new HashMap<>();
    private static final Map<String, SymbolAncientEnum> ENGLISH_NAME_MAP = new HashMap<>();

    static {
        for (SymbolAncientEnum symbol : values()) {
            ID_MAP.put(symbol.id, symbol);
            ANGLE_INDEX_MAP.put(symbol.angleIndex, symbol);
            ENGLISH_NAME_MAP.put(symbol.englishName.toLowerCase(), symbol);
        }
    }

    public static SymbolAncientEnum valueOf(int id) {
        return ID_MAP.get(id);
    }

    public static SymbolAncientEnum fromEnglishName(String englishName) {
        return ENGLISH_NAME_MAP.get(englishName.toLowerCase().replace("ö", "o"));
    }

    public static SymbolAncientEnum getSymbolByAngleIndex(int index) {
        return ANGLE_INDEX_MAP.get(index);
    }
}
