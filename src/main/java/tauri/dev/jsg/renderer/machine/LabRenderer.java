package tauri.dev.jsg.renderer.machine;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.tileentity.machine.lab.LabTile;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;

public class LabRenderer extends TileEntitySpecialRenderer<LabTile> {
    @Override
    public void render(@Nonnull LabTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        //if(!shouldRender(te)) return;

        GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 1, 0);

                // Render machine
                GlStateManager.pushMatrix();
                    ElementEnum.LAB_MACHINE.bindTextureAndRender();
                    // Render interfaces
                    GlStateManager.pushMatrix();
                        ElementEnum.LAB_INTERFACE.bindTexture(BiomeOverlayEnum.NORMAL);
                        for(int i = 1; i <= 8; i++){
                            ElementEnum.renderModel("machine/lab/interfaces/interface_" + i + ".obj");
                        }
                    GlStateManager.popMatrix();
                GlStateManager.popMatrix();

                // Render fluids
                GlStateManager.pushMatrix();
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    GlStateManager.enableBlend();
                    ElementEnum.LAB_TUBES.bindTextureAndRender();
                    for(int i = 1; i <= 12; i++){
                        // Todo(Mine): Check liquid from container
                        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                        GlStateManager.color(1, 1, 1, 1);
                        //GL11.glEnable(GL11.GL_SCISSOR_TEST);
                        //TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(JSGFluids.NAQUADAH_MOLTEN_REFINED.getStill().toString());
                        //GL11.glScissor((int) sprite.get(), (int) sprite.getMinV(), sprite.getIconWidth(), sprite.getIconHeight());

                        ElementEnum.renderModel("machine/lab/liquids/tube_" + i + ".obj");
                        //GL11.glDisable(GL11.GL_SCISSOR_TEST);
                    }
                    GlStateManager.disableBlend();
                GlStateManager.popMatrix();

            GlStateManager.popMatrix();

        GlStateManager.popMatrix();
    }

    protected boolean shouldRender(LabTile te){
        IBlockState s = te.getWorld().getBlockState(te.getPos());
        return s.getPropertyKeys().contains(JSGProps.RENDER_BLOCK) && !s.getValue(JSGProps.RENDER_BLOCK);
    }
}
