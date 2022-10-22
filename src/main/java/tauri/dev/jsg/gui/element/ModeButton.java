package tauri.dev.jsg.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class ModeButton extends GuiButton {
    public final int textureWidth;
    public final int textureHeight;
    public final int states;
    public final int texU;
    public final int texV;
    private final ResourceLocation texture;
    private int currentState = 0;

    public ModeButton(int buttonId, int x, int y, int size, ResourceLocation texture, int textureWidth, int textureHeight, int states, int texU, int texV) {
        super(buttonId, x, y, size, size, "");
        this.textureHeight = textureHeight;
        this.textureWidth = textureWidth;
        this.states = states;
        this.texture = texture;
        this.texU = texU;
        this.texV = texV;
    }

    public ModeButton(int buttonId, int x, int y, int size, ResourceLocation texture, int textureWidth, int textureHeight, int states) {
        this(buttonId, x, y, size, texture, textureWidth, textureHeight, states, 0, size);
    }


    public void drawButton(int mouseX, int mouseY) {
        if (this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            this.mouseDragged(Minecraft.getMinecraft(), mouseX, mouseY);

            Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
            if (hovered) {
                drawModalRectWithCustomSizedTexture(x, y, texU + width, texV, width, height, textureWidth, textureHeight);
            } else {
                drawModalRectWithCustomSizedTexture(x, y, texU, texV, width, height, textureWidth, textureHeight);
            }
            drawModalRectWithCustomSizedTexture(x, y, texU + (currentState * width), texV, width, height, textureWidth, textureHeight);
        }
    }

    public void nextState() {
        if (currentState == states - 1) currentState = 0;
        else currentState++;
    }

    public void previousState() {
        if (currentState == 0) currentState = states - 1;
        else currentState--;
    }

    /*
     * left = 0
     * right = 1
     * middle = 2
     *
     */
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GuiHelper.isPointInRegion(this.x, this.y,
                this.width, this.height, mouseX, mouseY)) {
            switch (mouseButton) {
                case 0:
                    this.nextState();
                    break;
                case 1:
                    this.previousState();
                    break;
                case 2:
                    this.setCurrentState(0);
                    break;

            }
            this.playPressSound(Minecraft.getMinecraft().getSoundHandler());
        }

    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }
}
