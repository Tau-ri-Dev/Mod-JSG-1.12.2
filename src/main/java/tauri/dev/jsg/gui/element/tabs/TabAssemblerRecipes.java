package tauri.dev.jsg.gui.element.tabs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import tauri.dev.jsg.gui.element.BetterButton;
import tauri.dev.jsg.machine.assembler.AssemblerRecipe;
import tauri.dev.jsg.renderer.BlockRenderer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static tauri.dev.jsg.gui.element.GuiHelper.isPointInRegion;

public class TabAssemblerRecipes extends Tab {
    //private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(JSG.MOD_ID, "textures/gui/container_assembler.png");
    private final HashMap<BetterButton, AssemblerRecipe> BUTTONS = new HashMap<>();

    private AssemblerRecipe selectedRecipe = null;
    private final net.minecraft.inventory.Container container;

    protected TabAssemblerRecipes(TabButtonsWithBlocksBuilder builder) {
        super(builder);
        BUTTONS.putAll(builder.buttons);
        container = builder.container;
    }

    public static TabButtonsWithBlocksBuilder builder() {
        return new TabButtonsWithBlocksBuilder();
    }

    @Override
    public void render(FontRenderer fontRenderer, int mouseX, int mouseY) {
        super.render(fontRenderer, mouseX, mouseY);

        if (isVisible()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(guiLeft, guiTop, 0);
            for (BetterButton button : BUTTONS.keySet()) {
                int id = button.id - 100;
                if (id < 0) continue;
                GlStateManager.pushMatrix();
                GlStateManager.translate(currentOffsetX, defaultY, 0);
                GlStateManager.enableBlend();
                GlStateManager.color(1, 1, 1, 1);
                button.drawButton(mouseX - (guiLeft + currentOffsetX), mouseY - (guiTop + defaultY), (selectedRecipe == BUTTONS.get(button)));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            if (selectedRecipe != null) {
                GlStateManager.pushMatrix();
                // main items
                int i = -1;
                for (int y = 0; y < 3; y++) {
                    for (int x = 0; x < 3; x++) {
                        i++;
                        if (i >= selectedRecipe.getPattern().size()) continue;
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(34 + (18 * x), 47 + (18 * y), 0);
                        BlockRenderer.renderItemGUI(selectedRecipe.getPattern().get(i));
                        GlStateManager.translate(0, 0, 150f);
                        //Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
                        //drawModalRectWithCustomSizedTexture(0, 0, 0, 232, 16, 16, 512, 512);
                        GlStateManager.popMatrix();
                    }
                }
                // sub stack
                GlStateManager.pushMatrix();
                GlStateManager.translate(102, 65, 0);
                BlockRenderer.renderItemGUI(selectedRecipe.getSubItemStack());
                GlStateManager.translate(0, 0, 150f);
                //Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
                //drawModalRectWithCustomSizedTexture(0, 0, 0, 232, 16, 16, 512, 512);
                GlStateManager.popMatrix();
                // result
                GlStateManager.pushMatrix();
                GlStateManager.translate(146, 65, 0);
                BlockRenderer.renderItemGUI(selectedRecipe.getResult());
                GlStateManager.translate(0, 0, 150f);
                //Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
                //drawModalRectWithCustomSizedTexture(0, 0, 0, 232, 16, 16, 512, 512);
                GlStateManager.popMatrix();

                GlStateManager.popMatrix();
            }
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void renderFg(GuiScreen screen, FontRenderer fontRenderer, int mouseX, int mouseY) {
        super.renderFg(screen, fontRenderer, mouseX, mouseY);
        if (isVisible() && isOpen()) {
            List<String> itemName;
            for (BetterButton button : BUTTONS.keySet()) {
                int id = button.id - 100;
                if (id < 0) continue;
                if (isPointInRegion((guiLeft + currentOffsetX) + button.x, (guiTop + defaultY) + button.y, 16, 16, mouseX, mouseY)) {
                    itemName = Collections.singletonList(I18n.format(BUTTONS.get(button).getUnlocalizedName()));
                    screen.drawHoveringText(itemName, mouseX - guiLeft, mouseY - guiTop);
                }
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (BetterButton button : BUTTONS.keySet()) {
            if (isPointInRegion(button.x + guiLeft + currentOffsetX, button.y + guiTop + defaultY, button.width, button.height, mouseX, mouseY)) {
                button.playPressSound(Minecraft.getMinecraft().getSoundHandler());
                selectedRecipe = BUTTONS.get(button);
                return;
            }
        }
    }


    // ------------------------------------------------------------------------------------------------
    // Builder

    public static class TabButtonsWithBlocksBuilder extends TabBuilder {

        private final HashMap<BetterButton, AssemblerRecipe> buttons = new HashMap<>();
        private net.minecraft.inventory.Container container;

        public TabButtonsWithBlocksBuilder addButton(int id, int x, int y, ResourceLocation texture, int texU, int texV, AssemblerRecipe recipe) {
            buttons.put(new BetterButton(id, x, y, 16, texture, 512, 512, texU, texV), recipe);
            return this;
        }

        public TabButtonsWithBlocksBuilder setContainer(net.minecraft.inventory.Container container) {
            this.container = container;
            return this;
        }

        @Override
        public TabAssemblerRecipes build() {
            return new TabAssemblerRecipes(this);
        }
    }
}
