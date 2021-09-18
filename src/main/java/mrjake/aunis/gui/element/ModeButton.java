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
    public final int textureWidth;
    public final int textureHeight;
    public final int variants;
    private final ResourceLocation texture;

    public ModeButton(int buttonId, int x, int y, int size, ResourceLocation texture, int textureWidth, int textureHeight, int variants) {
        super(buttonId, x, y, size, size, "");
        this.textureHeight = textureHeight;
        this.textureWidth = textureWidth;
        this.variants = variants;
        this.texture = texture;
    }


    public void drawButton(int mouseX, int mouseY, int variant) {
        if (this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            this.mouseDragged(Minecraft.getMinecraft(), mouseX, mouseY);


            Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
            if (hovered) {
                drawModalRectWithCustomSizedTexture(x, y, width, height, width, height, textureWidth, textureHeight);
            }
            else {
                drawModalRectWithCustomSizedTexture(x, y, 0, height, width, height, textureWidth, textureHeight);
            }
            drawModalRectWithCustomSizedTexture(x, y, variant * width, 0, width, height, textureWidth, textureHeight);
        }
    }


}
