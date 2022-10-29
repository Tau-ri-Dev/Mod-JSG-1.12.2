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
        GlStateManager.translate(0.27, 0.4, 0.43);
        TextureLoader.getTexture(TextureLoader.getTextureResource("zpm/zpm" + level + ".png")).bindTexture();
        ElementEnum.ZPM.render();
        GlStateManager.popMatrix();
    }
}
