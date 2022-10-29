package tauri.dev.jsg.renderer.machine;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.loader.texture.TextureLoader;
import tauri.dev.jsg.particle.ParticleBlenderSmoke;
import tauri.dev.jsg.renderer.BlockRenderer;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.tileentity.machine.CrystalChamberTile;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class CrystalChamberRenderer extends TileEntitySpecialRenderer<CrystalChamberTile> {

    @Override
    public void render(@Nonnull CrystalChamberTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        CrystalChamberRendererState rendererState = te.getRendererState();
        if (rendererState != null) {
            long tick = te.getWorld().getTotalWorldTime();

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.color(1, 1, 1, 1f);
            if (rendererState.craftingStack != null) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.5f, 0.4f, 0.5f);
                GlStateManager.scale(0.8f, 0.8f, 0.8f);
                GlStateManager.rotate((float) tick * 2, 0, 1f, 0);
                GlStateManager.translate(0f, 0.05f * Math.sin((float) tick / 9), 0f);
                BlockRenderer.renderItemOnGround(rendererState.craftingStack);
                GlStateManager.popMatrix();
                if(rendererState.isWorking) {
                    new ParticleBlenderSmoke(-1f + ((float) Math.random() * 2), -1f + ((float) Math.random() * 2), 2f, 7, 9, 0, 0, true, (motion) -> {
                        motion.x = 0;
                        motion.z = 0;
                    }).spawn(te.getWorld(), te.getPos(), 90, true);
                }

            }
            GlStateManager.popMatrix();
        }
    }
}
