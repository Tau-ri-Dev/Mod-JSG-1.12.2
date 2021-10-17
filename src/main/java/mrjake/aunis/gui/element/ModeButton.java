package mrjake.aunis.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

/**
 * @author matousss
 */
public class ModeButton extends GuiButton {
    public final int textureWidth;
    public final int textureHeight;
    public final int states;
    private final ResourceLocation texture;
    private int currentState = 0;

    public ModeButton(int buttonId, int x, int y, int size, ResourceLocation texture, int textureWidth, int textureHeight, int states) {
        super(buttonId, x, y, size, size, "");
        this.textureHeight = textureHeight;
        this.textureWidth = textureWidth;
        this.states = states;
        this.texture = texture;
    }


    public void drawButton(int mouseX, int mouseY) {
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
            drawModalRectWithCustomSizedTexture(x, y, currentState * width, 0, width, height, textureWidth, textureHeight);
        }
    }

    public void nextState() {
        if (currentState == states - 1) currentState = 0;
        else currentState++;
    }

    public void previousState() {
        if (currentState == 0) currentState = states-1;
        else currentState--;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }
}
