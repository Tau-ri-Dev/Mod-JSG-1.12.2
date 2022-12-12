package tauri.dev.jsg.stargate.network;

import net.minecraft.util.ResourceLocation;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;

public interface SymbolInterface {

    boolean origin();

    float getAngle();

    int getAngleIndex();

    int getId();

    String getEnglishName();

    ResourceLocation getIconResource(BiomeOverlayEnum overlay, int dimensionId, int configOrigin);

    default ResourceLocation getIconResource(int originId) {
        return getIconResource(BiomeOverlayEnum.NORMAL, 0, originId);
    }

    default ResourceLocation getIconResource(BiomeOverlayEnum overlay, int dimensionId) {
        return getIconResource(overlay, dimensionId, -1);
    }

    default ResourceLocation getIconResource() {
        return getIconResource(BiomeOverlayEnum.NORMAL, 0);
    }

    String localize();

    SymbolTypeEnum getSymbolType();
}
