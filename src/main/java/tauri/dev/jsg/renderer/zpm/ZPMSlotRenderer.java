package tauri.dev.jsg.renderer.zpm;

import net.minecraft.client.renderer.GlStateManager;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.tileentity.energy.ZPMHubTile;

public class ZPMSlotRenderer extends ZPMHubRenderer {

    @Override
    protected void renderMainObject(ZPMHubTile tile) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.42, 0, -0.5);
        GlStateManager.scale(0.85, 0.85, 0.85);
        ElementEnum.ZPM_SLOT.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
        GlStateManager.popMatrix();
    }

    @Override
    protected void renderZPM(int zpmId, ZPMHubTile te, float plusY) {
        if (zpmId != 0) return;
        int level = te.zpm1Level;
        float zx = -0.1f;
        float zz = -0.08f;
        GlStateManager.pushMatrix();
        GlStateManager.translate(zx, 1 + (plusY * 0.8), zz);
        ZPMRenderer.renderZPM(level, 0.57f, (!te.isSlidingUp && !te.isAnimating));
        GlStateManager.popMatrix();
    }
}
