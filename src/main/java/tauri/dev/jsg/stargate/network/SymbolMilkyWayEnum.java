package tauri.dev.jsg.stargate.network;

import net.minecraft.util.ResourceLocation;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.origins.OriginsLoader;
import tauri.dev.jsg.loader.model.ModelLoader;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static tauri.dev.jsg.config.origins.OriginsLoader.DEFAULT_ORIGIN_ID;
import static tauri.dev.jsg.config.origins.OriginsLoader.MOD_POINT_OF_ORIGINS_COUNT;

public enum SymbolMilkyWayEnum implements SymbolInterface {
    SCULPTOR(0, 19, "Sculptor", "0.obj"),
    SCORPIUS(1, 8, "Scorpius", "1.obj"),
    CENTAURUS(2, 4, "Centaurus", "2.obj"),
    MONOCEROS(3, 31, "Monoceros", "3.obj"),
    ORIGIN(4, 0, "Point of Origin", "4.obj"),
    PEGASUS(5, 18, "Pegasus", "5.obj"),
    ANDROMEDA(6, 21, "Andromeda", "6.obj"),
    SERPENSCAPUT(7, 6, "Serpens Caput", "7.obj"),
    ARIES(8, 23, "Aries", "8.obj"),
    LIBRA(9, 5, "Libra", "9.obj"),
    ERIDANUS(10, 28, "Eridanus", "10.obj"),
    LEOMINOR(11, 37, "Leo Minor", "11.obj"),
    HYDRA(12, 33, "Hydra", "12.obj"),
    SAGITTARIUS(13, 11, "Sagittarius", "13.obj"),
    SEXTANS(14, 36, "Sextans", "14.obj"),
    SCUTUM(15, 10, "Scutum", "15.obj"),
    PISCES(16, 20, "Pisces", "16.obj"),
    VIRGO(17, 2, "Virgo", "17.obj"),
    BOOTES(18, 3, "Bootes", "18.obj"),
    AURIGA(19, 27, "Auriga", "19.obj"),
    CORONAAUSTRALIS(20, 9, "Corona Australis", "20.obj"),
    GEMINI(21, 32, "Gemini", "21.obj"),
    LEO(22, 38, "Leo", "22.obj"),
    CETUS(23, 25, "Cetus", "23.obj"),
    TRIANGULUM(24, 22, "Triangulum", "24.obj"),
    AQUARIUS(25, 17, "Aquarius", "25.obj"),
    MICROSCOPIUM(26, 13, "Microscopium", "26.obj"),
    EQUULEUS(27, 16, "Equuleus", "27.obj"),
    CRATER(28, 1, "Crater", "28.obj"),
    PERSEUS(29, 24, "Perseus", "29.obj"),
    CANCER(30, 35, "Cancer", "30.obj"),
    NORMA(31, 7, "Norma", "31.obj"),
    TAURUS(32, 26, "Taurus", "32.obj"),
    CANISMINOR(33, 30, "Canis Minor", "33.obj"),
    CAPRICORNUS(34, 14, "Capricornus", "34.obj"),
    LYNX(35, 34, "Lynx", "35.obj"),
    ORION(36, 29, "Orion", "36.obj"),
    PISCISAUSTRINUS(37, 15, "Piscis Austrinus", "37.obj"),
    BRB(38, -1, "Bright Red Button", "BRB.obj");

    public static final float ANGLE_PER_GLYPH = 9.2307692f;

    public final int id;
    public final int angleIndex;
    public final float angle;

    public final String englishName;
    public final String translationKey;
    private final ResourceLocation iconResource;
    private final ResourceLocation modelResource;

    SymbolMilkyWayEnum(int id, int angleIndex, String englishName, String model) {
        this.id = id;

        this.angleIndex = angleIndex;
        this.angle = 360 - (angleIndex * ANGLE_PER_GLYPH);

        this.englishName = englishName;
        this.translationKey = "glyph.jsg.milkyway." + englishName.toLowerCase().replace(" ", "_");
        this.iconResource = new ResourceLocation(JSG.MOD_ID, "textures/gui/symbol/milkyway/" + englishName.toLowerCase() + ".png");

        this.modelResource = ModelLoader.getModelResource("milkyway/" + model);
    }

    public boolean brb() {
        return this == BRB;
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
        return angle;
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
    public ResourceLocation getIconResource(int originId) {
        if (this == ORIGIN) {
            if (JSGConfig.Stargate.pointOfOrigins.enableDiffOrigins) {
                if (originId >= MOD_POINT_OF_ORIGINS_COUNT)
                    return OriginsLoader.getResource(OriginsLoader.EnumOriginFileType.TEXTURE, originId);
                return new ResourceLocation(JSG.MOD_ID, "textures/gui/symbol/milkyway/origin_" + originId + ".png");
            }
            return new ResourceLocation(JSG.MOD_ID, "textures/gui/symbol/milkyway/origin_" + DEFAULT_ORIGIN_ID + ".png");
        }
        return getIconResource(BiomeOverlayEnum.NORMAL, 0, originId);
    }

    @Override
    public boolean renderIconByMinecraft(int originId){
        return (this != ORIGIN || !JSGConfig.Stargate.pointOfOrigins.enableDiffOrigins || originId < MOD_POINT_OF_ORIGINS_COUNT);
    }

    @Override
    public ResourceLocation getIconResource(BiomeOverlayEnum overlay, int dimensionId, int configOrigin) {
        if (this == ORIGIN)
            return getIconResource(StargateClassicBaseTile.getOriginId(overlay, dimensionId, configOrigin));
        return iconResource;
    }

    public ResourceLocation getModelResource(BiomeOverlayEnum overlay, int dimensionId, boolean forDHD, boolean lightModel, int configOrigin) {
        return getModelResource(overlay, dimensionId, forDHD, lightModel, false, configOrigin);
    }

    private ResourceLocation getModelResource(BiomeOverlayEnum overlay, int dimensionId, boolean forDHD, boolean lightModel, boolean notFound, int configOrigin) {
        ResourceLocation modelResource;
        if (this == ORIGIN) {
            if (!notFound && JSGConfig.Stargate.pointOfOrigins.enableDiffOrigins)
                modelResource = ModelLoader.getModelResource("milkyway/" + (!forDHD ? "ring/" : "") + "origin_" + StargateClassicBaseTile.getOriginId(overlay, dimensionId, configOrigin) + (lightModel ? "_light" : "") + ".obj");
            else
                modelResource = ModelLoader.getModelResource("milkyway/" + (!forDHD ? "ring/" : "") + "origin_" + DEFAULT_ORIGIN_ID + (lightModel ? "_light" : "") + ".obj");

            if (ModelLoader.getModel(modelResource) == null && !notFound) {
                JSG.error("Origin model not loaded!");
                JSG.error(modelResource.getResourceDomain() + ":" + modelResource.getResourcePath());
                return getModelResource(overlay, dimensionId, forDHD, lightModel, true, configOrigin);
            }

            return modelResource;
        }

        return this.modelResource;
    }

    @Override
    public String localize() {
        return JSG.proxy.localize(translationKey);
    }

    @Override
    public SymbolTypeEnum getSymbolType() {
        return SymbolTypeEnum.MILKYWAY;
    }

    public static SymbolInterface getSymbolByAngle(float angle) {

        for (SymbolMilkyWayEnum symbol : values()) {
            if (symbol.angle == angle)
                return symbol;
        }
        for (SymbolMilkyWayEnum symbol : values()) {
            if (symbol.angle == 360 - angle)
                return symbol;
        }

        return getOrigin();
    }

    public static float getAngleOfNearest(float angle) {
        int end = 38;
        int current = 0;

        int loops = 0;
        int temp = end;
        while (current < end) {
            temp = end - current;

            if ((angle < getAngleByAngIndex(temp) && angle < getAngleByAngIndex(temp - 1)) || angle == getAngleByAngIndex(temp))
                return getAngleByAngIndex(temp);
            current++;

            loops++;
            if (loops > 250)
                break;
        }
        return getAngleByAngIndex(temp);
    }

    public static float getAngleByAngIndex(int index) {
        if (index < 0) index = 0;
        if (index > 38) index = 38;
        for (SymbolMilkyWayEnum symbol : values()) {
            if (symbol.angleIndex == index) {
                return symbol.angle;
            }
        }
        return 0;
    }

    // ------------------------------------------------------------
    // Static

    public static SymbolMilkyWayEnum getRandomSymbol(Random random) {
        int id = 0;
        do {
            id = random.nextInt(38);
        } while (id == ORIGIN.id);

        return valueOf(id);
    }

    public static boolean validateDialedAddress(StargateAddressDynamic stargateAddress) {
        if (stargateAddress.size() < 7)
            return false;

        if (!stargateAddress.get(stargateAddress.size() - 1).origin())
            return false;

        return true;
    }

    public static List<SymbolInterface> stripOrigin(List<SymbolInterface> dialedAddress) {
        return dialedAddress.subList(0, dialedAddress.size() - 1);
    }

    public static int getMinimalSymbolCountTo(SymbolTypeEnum symbolType, boolean localDial) {

        boolean eightChevrons = JSGConfig.Stargate.mechanics.pegAndMilkUseEightChevrons;

        switch (symbolType) {
            case MILKYWAY:
                return localDial ? 7 : 8;
            case PEGASUS:
                if (eightChevrons)
                    return 8;
                else
                    return localDial ? 7 : 8;
            case UNIVERSE:
                return 9;
        }

        return 0;
    }

    public static SymbolInterface getOrigin() {
        return ORIGIN;
    }

    public static int getMaxSymbolsDisplay(boolean hasUpgrade) {
        return hasUpgrade ? 8 : 6;
    }

    public static float getAnglePerGlyph() {
        return ANGLE_PER_GLYPH;
    }

    public static SymbolInterface getTopSymbol() {
        return ORIGIN;
    }

    private static final Map<Integer, SymbolMilkyWayEnum> ID_MAP = new HashMap<>();
    private static final Map<String, SymbolMilkyWayEnum> ENGLISH_NAME_MAP = new HashMap<>();

    static {
        for (SymbolMilkyWayEnum symbol : values()) {
            ID_MAP.put(symbol.id, symbol);
            ENGLISH_NAME_MAP.put(symbol.englishName.toLowerCase(), symbol);
        }
    }

    public static final SymbolMilkyWayEnum valueOf(int id) {
        return ID_MAP.get(id);
    }

    public static final SymbolMilkyWayEnum fromEnglishName(String englishName) {
        return ENGLISH_NAME_MAP.get(englishName.toLowerCase().replace("ö", "o"));
    }
}
