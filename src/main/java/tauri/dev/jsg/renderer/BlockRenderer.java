package tauri.dev.jsg.renderer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

public class BlockRenderer {

    /**
     * This method renders block using provided {@link IBlockState}.
     * Call {@code translate(x, y, z)} before this.
     *
     * @param world       The world in which rendering takes place.
     * @param relativePos Relative position of the rendered block.
     * @param state       {@link IBlockState}of the block to render.
     * @param lightPos    Position from which the light level will be taken.
     */
    public static void render(World world, BlockPos relativePos, IBlockState state, BlockPos lightPos) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(relativePos.getX() - lightPos.getX(), relativePos.getY() - lightPos.getY(), relativePos.getZ() - lightPos.getZ());

        GlStateManager.disableLighting();

        // Render block
        BlockRendererDispatcher blockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        blockRendererDispatcher.getBlockModelRenderer().renderModelFlat(world, blockRendererDispatcher.getModelForState(state), state, lightPos, tessellator.getBuffer(), false, 0);
        tessellator.draw();

        GlStateManager.popMatrix();

        GlStateManager.enableLighting();
    }

    public static void renderItemGUI(@Nullable ItemStack stack) {
        if(stack == null) return;
        float alpha = 1.0f;
        final ResourceLocation itemGlintTexture = new ResourceLocation("textures/misc/enchanted_item_glint.png");
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        IBakedModel bakedmodel = renderItem.getItemModelWithOverrides(stack, null, null);
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        //GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.color(1, 1, 1, alpha);
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        GlStateManager.translate((float) 0, (float) 0, 100.0F);
        GlStateManager.translate(8.0F, 8.0F, 0.0F);
        GlStateManager.scale(1.0F, -1.0F, 1.0F);
        GlStateManager.scale(16.0F, 16.0F, 16.0F);
        if (bakedmodel.isGui3d())
            GlStateManager.enableLighting();
        else
            GlStateManager.disableLighting();

        bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false);
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);

            if (bakedmodel.isBuiltInRenderer()) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
                GlStateManager.enableRescaleNormal();
                stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
            } else {
                renderItem.renderModel(bakedmodel, stack);

                if (stack.hasEffect()) {
                    GlStateManager.depthMask(false);
                    GlStateManager.depthFunc(514);
                    GlStateManager.disableLighting();
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
                    textureManager.bindTexture(itemGlintTexture);
                    GlStateManager.matrixMode(5890);
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(8.0F, 8.0F, 8.0F);
                    float f = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
                    GlStateManager.translate(f, 0.0F, 0.0F);
                    GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.color(1, 1, 1, alpha);
                    renderItem.renderModel(bakedmodel, -8372020);
                    GlStateManager.popMatrix();
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(8.0F, 8.0F, 8.0F);
                    float f1 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
                    GlStateManager.translate(-f1, 0.0F, 0.0F);
                    GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.color(1, 1, 1, alpha);
                    renderItem.renderModel(bakedmodel, -8372020);
                    GlStateManager.popMatrix();
                    GlStateManager.matrixMode(5888);
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    GlStateManager.enableLighting();
                    GlStateManager.depthFunc(515);
                    GlStateManager.depthMask(true);
                    textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                }
            }

            GlStateManager.popMatrix();
        }
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    }
}
