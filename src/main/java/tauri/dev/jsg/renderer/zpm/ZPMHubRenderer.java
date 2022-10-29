package tauri.dev.jsg.renderer.zpm;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.loader.texture.TextureLoader;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.tileentity.energy.ZPMHubTile;
import tauri.dev.jsg.tileentity.energy.ZPMTile;

import javax.annotation.Nonnull;

public class ZPMHubRenderer extends TileEntitySpecialRenderer<ZPMHubTile> {
    @Override
    public void render(@Nonnull ZPMHubTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        long tick = te.getWorld().getTotalWorldTime();
        double stage = ((double) (tick - te.animationStart)) / ZPMHubTile.SLIDING_ANIMATION_LENGTH;
        if(!te.isAnimating)
            stage = (te.isSlidingUp) ? -1 : 1; // 1 = down; -1 = up


        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        JSGConfig.rescaleToConfig();
        ElementEnum.ZPM_HUB.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
        GlStateManager.popMatrix();
    }
}
