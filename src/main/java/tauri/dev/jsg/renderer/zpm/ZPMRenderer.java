package tauri.dev.jsg.renderer.zpm;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.loader.texture.TextureLoader;
import tauri.dev.jsg.tileentity.energy.ZPMTile;

import javax.annotation.Nonnull;

public class ZPMRenderer extends TileEntitySpecialRenderer<ZPMTile> {
    @Override
    public void render(@Nonnull ZPMTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        int level = te.getPowerLevel();
        if (level > 5) level = 5;
        if (level < 0) level = 0;
        GlStateManager.translate(0.32, 0.27, 0.43);
        renderZPM(level, 0.8f);
        GlStateManager.popMatrix();
    }

    public static void renderZPM(int powerLevel, float size) {
        renderZPM(powerLevel, size, false);
    }
    public static void renderZPM(int powerLevel, float size, boolean on) {
        if (powerLevel < 0) return;
        GlStateManager.scale(size, size, size);
        TextureLoader.getTexture(TextureLoader.getTextureResource("zpm/zpm" + powerLevel + (on ? "" : "_off") + ".png")).bindTexture();
        ElementEnum.ZPM.render();
    }
}