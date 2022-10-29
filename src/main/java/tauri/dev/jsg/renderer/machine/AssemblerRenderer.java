package tauri.dev.jsg.renderer.machine;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.loader.texture.TextureLoader;
import tauri.dev.jsg.particle.ParticleBlenderAtoms;
import tauri.dev.jsg.renderer.BlockRenderer;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.tileentity.machine.AssemblerTile;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class AssemblerRenderer extends TileEntitySpecialRenderer<AssemblerTile> {

    @Override
    public void render(@Nonnull AssemblerTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        AssemblerRendererState rendererState = te.getRendererState();
        if (rendererState != null) {
            long tick = te.getWorld().getTotalWorldTime();

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.translate(0.5f, 1.0f, 0.5f);
            GlStateManager.color(1, 1, 1, 1f);
            if (rendererState.craftingStack != null) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0f, 0.1f, 0f);
                GlStateManager.scale(0.8f, 0.8f, 0.8f);
                GlStateManager.rotate((float) tick * 2, 0, 1f, 0);
                GlStateManager.translate(0f, 0.05f * Math.sin((float) tick / 9), 0f);
                BlockRenderer.renderItemOnGround(rendererState.craftingStack);
                GlStateManager.popMatrix();
                if (rendererState.isWorking) {
                    for (int i = 0; i < 8; i++) {
                        new ParticleBlenderAtoms(-1f + ((float) Math.random() * 2), -1f + ((float) Math.random() * 2), 2.7f, 7, 7, 1, 1, false, (motion) -> {
                            motion.x = -0.5f + (Math.random() * 1f);
                            motion.z = -0.5f + (Math.random() * 1f);
                        }).spawn(te.getWorld(), te.getPos(), 90, true);
                    }
                    ElementEnum.ASSEMBLER_MACHINE.bindTexture(BiomeOverlayEnum.NORMAL);
                } else
                    TextureLoader.getTexture(TextureLoader.getTextureResource("machine/assembler/assembler_off.png")).bindTexture();
            } else
                TextureLoader.getTexture(TextureLoader.getTextureResource("machine/assembler/assembler_off.png")).bindTexture();

            GlStateManager.translate(0, -1, 0);
            GlStateManager.scale(0.03, 0.03, 0.03);
            ElementEnum.ASSEMBLER_MACHINE.render();
            GlStateManager.popMatrix();
        }
    }
}
