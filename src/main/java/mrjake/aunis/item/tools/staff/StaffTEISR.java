package mrjake.aunis.item.tools.staff;

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

public class StaffTEISR extends TileEntityItemStackRenderer {

    @Override
    public void renderByItem(ItemStack stack) {
        float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
        ItemCameraTransforms.TransformType transformType = AunisItems.STAFF.getLastTransform();

        GlStateManager.pushMatrix();

        // Item frame
        if (transformType == ItemCameraTransforms.TransformType.FIXED) {
            GlStateManager.translate(0.6, 0.5, 0.4);
            GlStateManager.scale(0.045, 0.045, 0.045);
        } else {
            boolean mainhand = AunisItems.STAFF.getLastTransform() == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;
            EnumHandSide handSide = mainhand ? EnumHandSide.RIGHT : EnumHandSide.LEFT;

            EntityPlayer player = Minecraft.getMinecraft().player;
            float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
            float angle = ItemRenderHelper.getMapAngleFromPitch(pitch);
            angle = 1 - angle;

            if (handSide == EnumHandSide.RIGHT) {
                GlStateManager.translate(0.0, -10.0, 0.0);
                GlStateManager.rotate(90, 0, 1, 0);

                GlStateManager.translate(0, 0.3 * angle, -0.1 * angle);
            } else {
                GlStateManager.translate(-50, -10, 0);
                GlStateManager.rotate(90, 0, 1, 0);

                GlStateManager.translate(0, 0.3 * angle, 0);
            }

            GlStateManager.scale(7.0f, 7.0f, 7.0f);
        }

        ElementEnum.STAFF.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
        GlStateManager.popMatrix();
    }
}
