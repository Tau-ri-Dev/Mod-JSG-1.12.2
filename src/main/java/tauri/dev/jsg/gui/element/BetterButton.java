package tauri.dev.jsg.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class BetterButton extends GuiButton {
    public final int textureWidth;
    public final int textureHeight;
    public final int texU;
    public final int texV;
    private final ResourceLocation texture;
    private boolean enabled = true;

    public BetterButton(int buttonId, int x, int y, int size, ResourceLocation texture, int textureWidth, int textureHeight, int texU, int texV) {
        super(buttonId, x, y, size, size, "");
        this.textureHeight = textureHeight;
        this.textureWidth = textureWidth;
        this.texture = texture;
        this.texU = texU;
        this.texV = texV;
    }

    public void drawButton(int mouseX, int mouseY, boolean toggled) {
        if (this.visible) {
            this.hovered = toggled || GuiHelper.isPointInRegion(x, y, width, height, mouseX, mouseY);

            this.mouseDragged(Minecraft.getMinecraft(), mouseX, mouseY);

            Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
            if (enabled && hovered) {
                drawModalRectWithCustomSizedTexture(x, y, texU + width, texV, width, height, textureWidth, textureHeight);
            } else {
                drawModalRectWithCustomSizedTexture(x, y, texU, texV, width, height, textureWidth, textureHeight);
            }
        }
    }

    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

    public void drawButton(int mouseX, int mouseY) {
        drawButton(mouseX, mouseY, false);
    }

    public boolean isMouseOnButton(int mouseX, int mouseY) {
        return GuiHelper.isPointInRegion(x, y, width, height, mouseX, mouseY);
    }

    @Override
    public void playPressSound(@Nonnull SoundHandler soundHandlerIn) {
        if(!enabled) return;
        super.playPressSound(soundHandlerIn);
    }
}
