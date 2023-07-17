package tauri.dev.jsg.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import tauri.dev.jsg.gui.base.JSGButton;

import java.util.HashMap;
import java.util.Map;

public class ModeButton extends JSGButton {
    public final int textureWidth;
    public final int textureHeight;
    protected final ResourceLocation texture;
    public int states;
    public Map<Integer, Boolean> enabled = new HashMap<>();
    protected int currentState = 0;

    public ModeButton(int buttonId, int x, int y, int size, ResourceLocation texture, int textureWidth, int textureHeight, int states) {
        this(buttonId, x, y, size, size, texture, textureWidth, textureHeight, states);
    }

    public ModeButton(int buttonId, int x, int y, int width, int height, ResourceLocation texture, int textureWidth, int textureHeight, int states) {
        super(buttonId, x, y, width, height, "");
        this.textureHeight = textureHeight;
        this.textureWidth = textureWidth;
        this.states = states;
        this.texture = texture;
        for (int i = 0; i < states; i++) {
            enabled.put(i, true);
        }
    }

    public void setStates(int i) {
        this.states = i;
    }


    public void drawButton(int mouseX, int mouseY) {
        if (this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            this.mouseDragged(Minecraft.getMinecraft(), mouseX, mouseY);

            GlStateManager.pushMatrix();
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
            if (!isEnabledCurrent()) {
                drawModalRectWithCustomSizedTexture(x, y, width * 2, height, width, height, textureWidth, textureHeight);
            } else if (hovered) {
                drawModalRectWithCustomSizedTexture(x, y, width, height, width, height, textureWidth, textureHeight);
            } else {
                drawModalRectWithCustomSizedTexture(x, y, 0, height, width, height, textureWidth, textureHeight);
            }
            drawModalRectWithCustomSizedTexture(x, y, currentState * width, 0, width, height, textureWidth, textureHeight);
            GlStateManager.disableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.popMatrix();
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

    public boolean isEnabledCurrent(){
        return enabled.get(currentState);
    }

    public void setEnabled(int state, boolean enabled) {
        this.enabled.put(state, enabled);
    }

    public void mouseClickedPerformAction(int mouseX, int mouseY, int mouseButton) {
        if (GuiHelper.isPointInRegion(this.x, this.y,
                this.width, this.height, mouseX, mouseY)) {
            switch (mouseButton) {
                case 0:
                    if (isEnabledCurrent())
                        performAction();
                    break;
                case 1:
                    this.nextState();
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
        if (currentState < 0) currentState = 0;
        if (currentState > (states - 1)) currentState = (states - 1);
        this.currentState = currentState;
    }
}
