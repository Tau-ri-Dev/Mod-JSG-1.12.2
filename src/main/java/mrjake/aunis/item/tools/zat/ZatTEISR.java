package mrjake.aunis.item.tools.zat;

import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.item.AunisItems;
import mrjake.aunis.item.renderer.ItemRenderHelper;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

public class ZatTEISR extends TileEntityItemStackRenderer {

    @Override
    public void renderByItem(ItemStack stack) {
        float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
        ItemCameraTransforms.TransformType transformType = AunisItems.ZAT.getLastTransform();

        GlStateManager.pushMatrix();

        // Item frame
        if (transformType == ItemCameraTransforms.TransformType.FIXED) {
            AunisConfig.DevConfig config = AunisConfig.devConfig;

            GlStateManager.translate(config.zatX, config.zatY, config.zatZ);
            GlStateManager.rotate(80, 0, 1, 0);

            GlStateManager.scale(0.2f, 0.2f, 0.2f);
        } else {
            boolean mainhand = AunisItems.ZAT.getLastTransform() == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;
            EnumHandSide handSide = mainhand ? EnumHandSide.RIGHT : EnumHandSide.LEFT;

            EntityPlayer player = Minecraft.getMinecraft().player;
            float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
            float angle = ItemRenderHelper.getMapAngleFromPitch(pitch);
            angle = 1 - angle;

            if (handSide == EnumHandSide.RIGHT) {
                GlStateManager.translate(1.5, -0.3, -1.0);
                GlStateManager.rotate(15, 1, 0, 0);

                GlStateManager.translate(0, 0.3 * angle, -0.1 * angle);
            } else {
                GlStateManager.translate(-1.0, -0.3, -1.0);
                GlStateManager.rotate(15, 1, 0, 0);

                GlStateManager.translate(0, 0.3 * angle, 0);
            }

            GlStateManager.scale(0.30f, 0.30f, 0.30f);
        }

        ElementEnum.ZAT.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
        GlStateManager.popMatrix();
    }
}
