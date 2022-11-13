package tauri.dev.jsg.renderer.zpm;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.loader.texture.TextureLoader;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.tileentity.energy.ZPMHubTile;
import tauri.dev.jsg.util.JSGMinecraftHelper;
import tauri.dev.jsg.util.JSGTextureLightningHelper;

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
        renderMainObject(te);

        for (int i = 0; i < 3; i++) {
            renderZPM(i, te, plusY);
        }
        GlStateManager.popMatrix();
    }

    protected void renderMainObject(ZPMHubTile tile) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0.6, 0);
        GlStateManager.scale(0.025, 0.025, 0.025);
        ElementEnum.ZPM_HUB.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
        int zpmHubLights = (int) Math.round(Math.abs(Math.sin(JSGMinecraftHelper.getClientTick()/8f)) * 5);
        if ((tile.zpm1Level == -1) && (tile.zpm2Level == -1) && (tile.zpm3Level == -1)) zpmHubLights = 0;
        if (zpmHubLights > 5) zpmHubLights = 5;
        if (zpmHubLights < 0) zpmHubLights = 0;
        JSGTextureLightningHelper.lightUpTexture(tile.getWorld(), tile.getPos(), zpmHubLights/5f);
        TextureLoader.getTexture(TextureLoader.getTextureResource("zpm/hub/pg_lights" + zpmHubLights + ".jpg")).bindTexture();
        ElementEnum.ZPM_HUB_LIGHTS.render();
        JSGTextureLightningHelper.resetLight(tile.getWorld(), tile.getPos());
        GlStateManager.popMatrix();
    }

    protected void renderZPM(int zpmId, ZPMHubTile te, float plusY) {
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
        ZPMRenderer.renderZPM(getWorld(), te.getPos(), level, 0.4f, (!te.isSlidingUp && !te.isAnimating));
        GlStateManager.popMatrix();
    }
}
