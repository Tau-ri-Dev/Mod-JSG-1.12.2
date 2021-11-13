package mrjake.aunis.item.gdo;

import mrjake.aunis.Aunis;
import mrjake.aunis.item.AunisItems;
import mrjake.aunis.item.oc.ItemOCMessage;
import mrjake.aunis.item.renderer.AunisFontRenderer;
import mrjake.aunis.item.renderer.ItemRenderHelper;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import org.lwjgl.opengl.GL11;

public class GDOTEISR extends TileEntityItemStackRenderer {

    @Override
    public void renderByItem(ItemStack stack) {
        float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
        TransformType transformType = AunisItems.GDO.getLastTransform();

        GlStateManager.pushMatrix();

        // Item frame
        if (transformType == TransformType.FIXED) {
            GlStateManager.translate(0.53, 0.50, 0.5);
            GlStateManager.rotate(90, 1, 0, 0);
            GlStateManager.rotate(180, 0, 0, 1);

            GlStateManager.scale(0.2f, 0.2f, 0.2f);
        } else {
            boolean mainhand = AunisItems.GDO.getLastTransform() == TransformType.FIRST_PERSON_RIGHT_HAND;
            EnumHandSide handSide = mainhand ? EnumHandSide.RIGHT : EnumHandSide.LEFT;

            EntityPlayer player = Minecraft.getMinecraft().player;
            float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
            float angle = ItemRenderHelper.getMapAngleFromPitch(pitch);

            renderArms(handSide, angle, partialTicks);
            angle = 1 - angle;


            if (handSide == EnumHandSide.RIGHT) {
                GlStateManager.translate(0.8, 0, -0.5);
                GlStateManager.rotate(35, 1, 0, 0);
                GlStateManager.rotate(15, 0, 0, 1);

                GlStateManager.translate(0, 0.3 * angle, -0.1 * angle);
                GlStateManager.rotate(25 * angle, 1, 0, 0);
            } else {
                GlStateManager.translate(-0.2, 0, -0.55);
                GlStateManager.rotate(30, 1, 0, 0);
                GlStateManager.rotate(-20, 0, 0, 1);
                GlStateManager.rotate(25, 0, 1, 0);

                GlStateManager.translate(0, 0.3 * angle, -0.0 * angle);
                GlStateManager.rotate(25 * angle, 1, 0, 0);
            }

            GlStateManager.scale(0.3f, 0.3f, 0.3f);
            //GlStateManager.scale(0.05f, 0.05f, 0.05f);
        }

        ElementEnum.GDO.bindTextureAndRender(BiomeOverlayEnum.NORMAL);

        // Translate rendered text
        GlStateManager.translate(0, 0.40f, 0.1f);
        GlStateManager.rotate(-90, 1, 0, 0);

        // ---------------------------------------------------------------------------------------------
        // List rendering

        GlStateManager.enableBlend();

        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            GDOMode mode = GDOMode.valueOf(compound.getByte("mode"));

            if (mode.next() == GDOMode.CODE_SENDER) {
                drawStringWithShadow(0.68f, 0.316f, mode.localize(), true, false);
                if (mode.next() != mode) {
                    drawStringWithShadow(0.68f, 0.616f, mode.next().localize(), false, false);
                }
            } else {
                if (mode.next() != mode) {
                    drawStringWithShadow(0.68f, 0.316f, mode.next().localize(), false, false);
                }
                drawStringWithShadow(0.68f, 0.616f, mode.localize(), true, false);
            }

            boolean notLinked = mode.linkable && !compound.hasKey(mode.tagPosName);

            if (notLinked) {
                GlStateManager.pushMatrix();

                GlStateManager.translate(0.92f, 0.05f, -.2f);
                Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(Aunis.ModID, "textures/gui/universe_warning.png"));
                GlStateManager.enableTexture2D();
                GlStateManager.enableBlend();
                GlStateManager.color(0.91f, 1, 1, 1);
                drawTexturedRect(0.72f, 0.26f, 0, 0.24f, 0.24f);

                GlStateManager.popMatrix();

            } else {
                int selected = compound.getByte("selected");
                NBTTagList tagList = compound.getTagList(mode.tagListName, NBT.TAG_COMPOUND);

                for (int offset = -1; offset <= 1; offset++) {
                    int index = selected + offset;
                    if (index >= 0 && index < tagList.tagCount()) {

                        boolean active = offset == 0;
                        NBTTagCompound entryCompound = (NBTTagCompound) tagList.getCompoundTagAt(index);

                        switch (mode) {
                            case CODE_SENDER:
                                break;
                            case OC:
                                ItemOCMessage message = new ItemOCMessage(entryCompound);
                                drawStringWithShadow(1.27f, 0.474f - 0.2f*offset, message.name, active, false);
                                break;
                        }
                    }
                }
                if (mode == GDOMode.OC) drawStringWithShadow(1.17f, 0.474f, ">", true, false);
            }
        }

        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static void drawStringWithShadow(float x, float y, String text, boolean active, boolean red) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, -.1f);
        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.scale(0.015f, 0.015f, 0.015f);

        int color = active ? 0x000000 : 0x7D7D7D;
        if (red) {
            color = 0xA01010;
        }

        AunisFontRenderer.getFontRenderer().drawString(text, -6, 19, color, false);

        if (active) {
            GlStateManager.translate(-0.4, 0.6, -0.3);
            AunisFontRenderer.getFontRenderer().drawString(text, -6, 19, 0x606060, false);
        }

        GlStateManager.popMatrix();
    }

    private static void drawTexturedRect(float x, float y, float z, float w, float h) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex3f(x, y, z);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex3f(x + w, y, z);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex3f(x + w, y + h, z);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex3f(x, y + h, z);
        GL11.glEnd();
    }

    private static void renderArms(EnumHandSide handSide, float angle, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(20, 20, 20);
        ItemRenderHelper.applyBobbing(partialTicks);

        if (handSide == EnumHandSide.RIGHT) {
            GlStateManager.translate(-0.3, -0.4, 0.0);
            GlStateManager.rotate(25, 0, 0, 1);

            GlStateManager.translate(-0.15 * angle, -0.5 * angle, 0.0);
            GlStateManager.rotate(10 * angle, 0, 0, 1);
        } else {
            GlStateManager.translate(0.3, -0.4, 0.0);
            GlStateManager.rotate(-25, 0, 0, 1);

            GlStateManager.translate(0.15 * angle, -0.5 * angle, 0.0);
            GlStateManager.rotate(-10 * angle, 0, 0, 1);
        }

        ItemRenderHelper.renderArmFirstPersonSide(0, handSide, 0, null);
        GlStateManager.popMatrix();
    }
}
