package tauri.dev.jsg.gui.element.tabs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import tauri.dev.jsg.gui.element.GuiHelper;
import tauri.dev.jsg.gui.element.ModeButton;

import java.util.HashMap;

public class TabButtonsWithBlocks extends Tab {
    private final HashMap<ModeButton, ItemStack> BUTTONS = new HashMap<>();

    protected TabButtonsWithBlocks(TabButtonsWithBlocksBuilder builder) {
        super(builder);
        BUTTONS.putAll(builder.buttons);
    }

    public static TabButtonsWithBlocksBuilder builder() {
        return new TabButtonsWithBlocksBuilder();
    }

    @Override
    public void render(FontRenderer fontRenderer, int mouseX, int mouseY) {
        super.render(fontRenderer, mouseX, mouseY);

        if (isVisible()) {
            for (ModeButton button : BUTTONS.keySet()) {
                int id = button.id - 100;
                if (id < 0) continue;
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.color(1, 1, 1, 1);
                GlStateManager.translate(guiLeft + currentOffsetX, guiTop + defaultY, 0);
                button.drawButton(mouseX - (guiLeft + currentOffsetX), mouseY - (guiTop + defaultY));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (ModeButton button : BUTTONS.keySet()) {
            if (GuiHelper.isPointInRegion(button.x + guiLeft + currentOffsetX, button.y + guiTop + defaultY, button.width, button.height, mouseX, mouseY)) {
                button.playPressSound(Minecraft.getMinecraft().getSoundHandler());
            }
        }
    }

    // ------------------------------------------------------------------------------------------------
    // Builder

    public static class TabButtonsWithBlocksBuilder extends TabBuilder {

        private final HashMap<ModeButton, ItemStack> buttons = new HashMap<>();

        public TabButtonsWithBlocksBuilder addButton(int id, ItemStack stack, int x, int y, ResourceLocation texture, int texU, int texV) {
            buttons.put(new ModeButton(id, x, y, 16, texture, 512, 512, 1, texU, texV), stack);
            return this;
        }

        @Override
        public TabButtonsWithBlocks build() {
            return new TabButtonsWithBlocks(this);
        }
    }
}
