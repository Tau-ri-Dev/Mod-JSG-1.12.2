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

public enum SymbolOriEnum implements SymbolInterface {
    ALPHA(0, 0, "Alpha"),
    BETA(1, 1, "Beta"),
    GAMMA(2, 2, "Gamma"),
    DELTA(3, 3, "Delta"),
    EPSILON(4, 4, "Epsilon"),
    ZETA(5, 5, "Zeta"),
    ETA(6, 6, "Eta"),
    THETA(7, 7, "Theta"),
    IOTA(8, 8, "Iota"),
    KAPPA(9, 9, "Kappa"),
    LAMBDA(10, 10, "Lambda"),
    MU(11, 11, "Mu"),
    NU(12, 12, "Nu"),
    XI(13, 13, "Xi"),
    OMICRON(14, 14, "Omicron"),
    PI(14, 14, "Pi"),
    RHO(15, 15, "Rho"),
    LIGHT(16, 16, "Light");

    public final int id;
    public final int angleIndex;

    public final String englishName;
    public final String translationKey;
    public final ResourceLocation iconResource;
    public final ResourceLocation modelResource;

    SymbolOriEnum(int id, int angleIndex, String englishName) {
        this.id = id;

        this.angleIndex = angleIndex;

        this.englishName = englishName;
        this.translationKey = "glyph.jsg.transportrings.ori." + englishName.toLowerCase().replace(" ", "_");
        this.iconResource = new ResourceLocation(JSG.MOD_ID, "textures/gui/symbol/transportrings/ori/" + englishName.toLowerCase() + ".png");
        this.modelResource = new ResourceLocation(JSG.MOD_ID, "models/tesr/transportrings/controller/ori/ori_button_" + (id+1) + ".obj");
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

    public static SymbolOriEnum getRandomSymbol(Random random) {
        int id;
        do {
            id = random.nextInt(5);
        } while (id == getOrigin().id || id == LIGHT.id);

        return valueOf(id);
    }

    public static boolean validateDialedAddress(TransportRingsAddress address) {
        if (address.size() < 5)
            return false;

        return address.get(address.size() - 1).origin();
    }

    public static List<SymbolInterface> stripOrigin(List<SymbolInterface> dialedAddress) {
        return dialedAddress.subList(0, dialedAddress.size()-1);
    }

    public static SymbolOriEnum getOrigin() {
        return ALPHA;
    }

    private static final Map<Integer, SymbolOriEnum> ID_MAP = new HashMap<>();
    private static final Map<Integer, SymbolOriEnum> ANGLE_INDEX_MAP = new HashMap<>();
    private static final Map<String, SymbolOriEnum> ENGLISH_NAME_MAP = new HashMap<>();

    static {
        for (SymbolOriEnum symbol : values()) {
            ID_MAP.put(symbol.id, symbol);
            ANGLE_INDEX_MAP.put(symbol.angleIndex, symbol);
            ENGLISH_NAME_MAP.put(symbol.englishName.toLowerCase(), symbol);
        }
    }

    public static SymbolOriEnum valueOf(int id) {
        return ID_MAP.get(id);
    }

    public static SymbolOriEnum fromEnglishName(String englishName) {
        return ENGLISH_NAME_MAP.get(englishName.toLowerCase().replace("ö", "o"));
    }

    public static SymbolOriEnum getSymbolByAngleIndex(int index){
        return ANGLE_INDEX_MAP.get(index);
    }
}
