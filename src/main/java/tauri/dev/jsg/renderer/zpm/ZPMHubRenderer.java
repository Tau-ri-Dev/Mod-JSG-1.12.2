package tauri.dev.jsg.renderer.zpm;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.tileentity.energy.ZPMHubTile;

import javax.annotation.Nonnull;

public class ZPMHubRenderer extends TileEntitySpecialRenderer<ZPMHubTile> {
    @Override
    public void render(@Nonnull ZPMHubTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        long tick = te.getWorld().getTotalWorldTime();
        double stage = ((double) (tick - te.animationStart)) / te.getAnimationLength();
        if (!te.isAnimating)
            stage = (te.isSlidingUp) ? 1 : 0; // 0 = down; 1 = up
        else if (!te.isSlidingUp)
            stage = 1 - stage;

        float plusY = (float) stage * 0.3f;


        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.translate(0.5, 0, 0.5);
        GlStateManager.rotate(te.facingAngle, 0, 1, 0);

        renderMainObject();

        for (int i = 0; i < 3; i++) {
            renderZPM(i, te, plusY);
        }
        GlStateManager.popMatrix();
    }

    protected void renderMainObject(){
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.025, 0.025, 0.025);
        ElementEnum.ZPM_HUB.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
        GlStateManager.popMatrix();
    }

    protected void renderZPM(int zpmId, ZPMHubTile te, float plusY){
        int level = -1;
        float zx = 0;
        float zy = 0.9f;
        float zz = 0;
        switch (zpmId) {
            case 0:
                level = te.zpm1Level;
                zx = 0.2f;
                zz = 0.18f;
                break;
            case 1:
                level = te.zpm2Level;
                zx = -0.33f;
                zz = 0.18f;
                break;
            case 2:
                level = te.zpm3Level;
                zx = -0.07f;
                zz = -0.27f;
                break;
            default:
                break;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(zx, zy + plusY, zz);
        ZPMRenderer.renderZPM(level, 0.4f, (!te.isSlidingUp && !te.isAnimating));
        GlStateManager.popMatrix();
    }
}
