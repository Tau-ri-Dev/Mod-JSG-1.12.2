package mrjake.aunis.transportrings;

import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.loader.model.ModelLoader;
import mrjake.aunis.stargate.network.StargateAddressDynamic;
import mrjake.aunis.stargate.network.SymbolInterface;
import mrjake.aunis.stargate.network.SymbolTypeEnum;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public enum SymbolTransportRingsEnum implements SymbolInterface {
    ALPHA(0, 0, "Alpha"),
    BETA(1, 1, "Beta"),
    GAMA(2, 2, "Gamma"),
    DELTA(3, 3, "Delta"),
    OMEGA(4, 4, "Omega"),
    ORIGIN(5, 5, "Origin");

    public int id;
    public int angleIndex;

    public String englishName;
    public String translationKey;
    public ResourceLocation iconResource;

    SymbolTransportRingsEnum(int id, int angleIndex, String englishName) {
        this.id = id;

        this.angleIndex = angleIndex;

        this.englishName = englishName;
        this.translationKey = "glyph.aunis.transportrings." + englishName.toLowerCase().replace(" ", "_");
        this.iconResource = new ResourceLocation(Aunis.ModID, "textures/gui/symbol/transportrings/" + englishName.toLowerCase() + ".png");
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

    public static SymbolTransportRingsEnum getRandomSymbol(Random random) {
        int id = 0;
        do {
            id = random.nextInt(5);
        } while (id == ORIGIN.id);

        return valueOf(id);
    }

    public static boolean validateDialedAddress(TransportRingsAddress address) {
        if (address.size() < 5)
            return false;

        if (!address.get(address.size()-1).origin())
            return false;

        return true;
    }

    public static List<SymbolTransportRingsEnum> stripOrigin(List<SymbolTransportRingsEnum> dialedAddress) {
        return dialedAddress.subList(0, dialedAddress.size()-1);
    }

    public static SymbolTransportRingsEnum getOrigin() {
        return ORIGIN;
    }

    private static final Map<Integer, SymbolTransportRingsEnum> ID_MAP = new HashMap<>();
    private static final Map<String, SymbolTransportRingsEnum> ENGLISH_NAME_MAP = new HashMap<>();

    static {
        for (SymbolTransportRingsEnum symbol : values()) {
            ID_MAP.put(symbol.id, symbol);
            ENGLISH_NAME_MAP.put(symbol.englishName.toLowerCase(), symbol);
        }
    }

    public static final SymbolTransportRingsEnum valueOf(int id) {
        return ID_MAP.get(id);
    }

    public static final SymbolTransportRingsEnum fromEnglishName(String englishName) {
        return ENGLISH_NAME_MAP.get(englishName.toLowerCase().replace("ö", "o"));
    }
}
