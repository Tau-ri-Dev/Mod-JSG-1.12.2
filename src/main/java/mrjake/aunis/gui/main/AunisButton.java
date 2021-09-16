package mrjake.aunis.gui.main;

import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.loader.texture.TextureLoader;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

import static org.lwjgl.opengl.GL11.glTexCoord2f;

/**
 * @author matousss
 */
public class AunisButton extends GuiButton {
    public AunisButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }

    public AunisButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks, BiomeOverlayEnum biomeOverlay) {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRenderer;
            //mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
            ElementEnum.MILKYWAY_RING.bindTexture(biomeOverlay);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;

            if (packedFGColour != 0)
            {
                j = packedFGColour;
            }
            else
            if (!this.enabled)
            {
                j = 10526880;
            }
            else if (this.hovered)
            {
                j = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
        }
    }
}
