package mrjake.aunis.gui.element;

import mrjake.aunis.gui.GuiBase;
import mrjake.aunis.loader.texture.Texture;
import mrjake.aunis.loader.texture.TextureLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

/**
 * @author matousss
 */
public class ModeButton extends GuiButton {
    public final int textureHeight;
    public final int textureWidth;
    public final int variants;
    private Texture texture;

    public ModeButton(int buttonId, int x, int y, int widthIn, ResourceLocation texture, int textureHeight, int textureWidth, int variants) {
        super(buttonId, x, y, widthIn, widthIn, "");
        this.textureHeight = textureHeight;
        this.textureWidth = textureWidth;
        this.variants = variants;
        this.texture = TextureLoader.getTexture(texture);
    }


    public void drawButton(int mouseX, int mouseY, int variant) {
        if (this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            this.mouseDragged(Minecraft.getMinecraft(), mouseX, mouseY);

            int fgcolor = 0xCCCCCC;
            int bgcolor = 0xFF1D2026;

           if (!this.hovered) {
                bgcolor = 0xFF313640;
            }

            drawRect(x, y, x + width, y + height, GuiBase.FRAME_COLOR);
            drawRect(x + 1, y + 1, x + width - 1, y + height - 1, bgcolor);

            texture.bindTexture();
            drawModalRectWithCustomSizedTexture(x, y, 0, variant * (textureWidth / variant), width, height, textureWidth / variant, textureHeight);
        }
    }


}
