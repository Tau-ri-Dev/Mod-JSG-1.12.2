package tauri.dev.jsg.gui.element;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.gui.base.JSGButton;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class ArrowButton extends JSGButton {
    public static ResourceLocation TEXTURE = new ResourceLocation(JSG.MOD_ID, "textures/gui/arrow_button.png");
    public static int SIZE = 20;
    public enum ArrowType {
        UP(20, 0),
        DOWN(0, 0),
        RIGHT(40, 0),
        LEFT(60, 0),
        CROSS(80, 0),
        PLUS(100,0);

        public final int texX;
        public final int texY;

        ArrowType(int texX, int texY) {
            this.texX = texX;
            this.texY = texY;
        }
    }

    public final ArrowType type;
    public ArrowButton(int buttonId, int x, int y, ArrowType type) {
        super(buttonId, x, y, 20, 20, "");
        this.type = type;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        super.drawButton(mc, mouseX, mouseY, partialTicks);
        if (visible) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
            drawModalRectWithCustomSizedTexture(this.x, this.y, type.texX, type.texY, SIZE, SIZE, 120, 20);
        }
    }
}
