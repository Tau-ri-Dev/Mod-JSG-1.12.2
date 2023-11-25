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
import tauri.dev.jsg.util.JSGTextureLightningHelper;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class AssemblerRenderer extends TileEntitySpecialRenderer<AssemblerTile> {

    @Override
    public void render(@Nonnull AssemblerTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        AssemblerRendererState rendererState = (AssemblerRendererState) te.getRendererState();
        if (rendererState != null) {
            long tick = te.getWorld().getTotalWorldTime();
            float color = 1f;
            final float MIN_COLOR = 0.3f;
            final int ANIMATION_TIME = 20;

            final long workingTime = (te.getWorld().getTotalWorldTime() - rendererState.workStateChanged);

            if (workingTime < ANIMATION_TIME) {
                if (rendererState.isWorking && rendererState.craftingStack != null)
                    color = (float) workingTime / ANIMATION_TIME * (1 - MIN_COLOR) + MIN_COLOR;
                else
                    color = 1f - (float) workingTime / ANIMATION_TIME * (1 - MIN_COLOR) + MIN_COLOR;
            }

            boolean isMachineOn = false;


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
                    isMachineOn = true;
                }
            }

            GlStateManager.translate(0, -1, 0);
            GlStateManager.scale(0.03, 0.03, 0.03);

            GlStateManager.pushMatrix();
            GlStateManager.color(1, 1, 1);
            ElementEnum.ASSEMBLER_MACHINE.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            JSGTextureLightningHelper.lightUpTexture(getWorld(), te.getPos(), color);
            GlStateManager.color(color, color, color, 1f);
            if (isMachineOn || workingTime < ANIMATION_TIME)
                TextureLoader.getTexture(TextureLoader.getTextureResource("machine/assembler/assembler_on.png")).bindTexture();
            else
                TextureLoader.getTexture(TextureLoader.getTextureResource("machine/assembler/assembler_off.png")).bindTexture();
            ElementEnum.ASSEMBLER_MACHINE.render();
            GlStateManager.popMatrix();

            GlStateManager.popMatrix();
        }
    }
}
