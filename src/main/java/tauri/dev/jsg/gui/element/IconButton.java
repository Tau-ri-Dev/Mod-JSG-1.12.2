package tauri.dev.jsg.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

import static tauri.dev.jsg.gui.element.GuiHelper.isPointInRegion;

public class IconButton extends GuiButton {
    public String[] label;
    public ResourceLocation texture;
    public final int u;
    public final int v;
    public final int width;
    public final int height;
    public boolean enableHover;
    public final int texSize;

    public IconButton(int buttonId, int x, int y, ResourceLocation texture, int texSize, int u, int v, int width, int height, boolean enableHover, String... label) {
        super(buttonId, x, y, width, height, "");
        this.label = label;
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.texSize = texSize;
        this.enableHover = enableHover;
    }

    public void drawButton(int mouseX, int mouseY) {
        if (this.visible) {
            this.hovered = isPointInRegion(x, y, width, height, mouseX, mouseY);
            Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
            if (enableHover && hovered && enabled) {
                drawModalRectWithCustomSizedTexture(x, y, u + width, v, width, height, texSize, texSize);
            } else {
                drawModalRectWithCustomSizedTexture(x, y, u, v, width, height, texSize, texSize);
            }
        }
    }

    public void drawFg(int mouseX, int mouseY, GuiScreen screen) {
        if (hovered)
            screen.drawHoveringText(Arrays.asList(label), mouseX, mouseY);
    }
}
