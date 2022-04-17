package mrjake.aunis.transportrings;

import mrjake.aunis.Aunis;
import mrjake.aunis.stargate.network.SymbolInterface;
import mrjake.aunis.stargate.network.SymbolTypeEnum;
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
    OMEGA(4, 4, "Omega"),
    ORIGIN(5, 5, "Origin"),
    LIGHT(6, 6, "Light");

    public int id;
    public int angleIndex;

    public String englishName;
    public String translationKey;
    public ResourceLocation iconResource;
    public ResourceLocation modelResource;

    SymbolOriEnum(int id, int angleIndex, String englishName) {
        this.id = id;

        this.angleIndex = angleIndex;

        this.englishName = englishName;
        this.translationKey = "glyph.aunis.transportrings.ori." + englishName.toLowerCase().replace(" ", "_");
        this.iconResource = new ResourceLocation(Aunis.ModID, "textures/gui/symbol/transportrings/ori/" + englishName.toLowerCase() + ".png");
        this.modelResource = new ResourceLocation(Aunis.ModID, "models/tesr/transportrings/controller/ori/ori_button_" + (id+1) + ".obj");
    }

    @Override
    public boolean origin() {
        return this == ORIGIN;
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
    public ResourceLocation getIconResource() {
        return iconResource;
    }

    @Override
    public String localize() {
        return Aunis.proxy.localize(translationKey);
    }

    @Override
    public SymbolTypeEnum getSymbolType() {
        return null;
    }

    public SymbolTypeTransportRingsEnum getTRSymbolType() {
        return SymbolTypeTransportRingsEnum.ORI;
    }

    public static SymbolOriEnum getRandomSymbol(Random random) {
        int id = 0;
        do {
            id = random.nextInt(5);
        } while (id == ORIGIN.id || id == LIGHT.id);

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
        return dialedAddress.subList(0, dialedAddress.size()-1);
    }

    public static SymbolOriEnum getOrigin() {
        return ORIGIN;
    }

    private static final Map<Integer, SymbolOriEnum> ID_MAP = new HashMap<>();
    private static final Map<String, SymbolOriEnum> ENGLISH_NAME_MAP = new HashMap<>();

    static {
        for (SymbolOriEnum symbol : values()) {
            ID_MAP.put(symbol.id, symbol);
            ENGLISH_NAME_MAP.put(symbol.englishName.toLowerCase(), symbol);
        }
    }

    public static final SymbolOriEnum valueOf(int id) {
        return ID_MAP.get(id);
    }

    public static final SymbolOriEnum fromEnglishName(String englishName) {
        return ENGLISH_NAME_MAP.get(englishName.toLowerCase().replace("ö", "o"));
    }
}
