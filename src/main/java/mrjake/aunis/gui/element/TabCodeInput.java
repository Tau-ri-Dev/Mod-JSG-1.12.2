package mrjake.aunis.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

/**
 * @author matousss
 */
public class TabCodeInput extends Tab{
    protected int code;
    public GuiTextField inputField = new NumberOnlyTextField(1,
            Minecraft.getMinecraft().fontRenderer, guiLeft + 6, guiTop + defaultY + 25, 64, 16);

    protected TabCodeInput(TabCodeInputBuilder builder) {
        super(builder);
        code = builder.code;
        inputField.setMaxStringLength(9);
        inputField.setText(code > -1 ? Integer.toString(code) : "");
        inputField.setEnabled(true);
    }

    @Override
    public void render(FontRenderer fontRenderer, int mouseX, int mouseY){
        super.render(fontRenderer, mouseX, mouseY);

//        Minecraft.getMinecraft().getTextureManager().bindTexture(bgTexLocation);
//        GlStateManager.color(1, 1, 1, 1);
       //Gui.drawModalRectWithCustomSizedTexture(guiLeft+currentOffsetX+5, guiTop+defaultY+24, slotTexX, slotTexY, 18, 18, textureSize, textureSize);
        inputField.x = guiLeft + 6 + currentOffsetX;
        inputField.drawTextBox();
       // saveButton.drawButton(Minecraft.getMinecraft(), mouseX, mouseY, Minecraft.getMinecraft().world.getTotalWorldTime());

    }



//    @Override
//    public void renderFg(GuiScreen screen, FontRenderer fontRenderer, int mouseX, int mouseY) {
//        super.renderFg(screen, fontRenderer, mouseX, mouseY);
//
//        if (isVisible() && isOpen()) {
//            if (GuiHelper.isPointInRegion(guiLeft + currentOffsetX + 6, guiTop + defaultY + 25, 64, 16, mouseX, mouseY)) {
//                List<String> text = new ArrayList<>();
//                text.add(I18n.format("gui.unknown"));
//                screen.drawHoveringText(text, mouseX - guiLeft, mouseY - guiTop);
//            }
//        }
//    }

    // ------------------------------------------------------------------------------------------------
    // Builder

    public static TabCodeInput.TabCodeInputBuilder builder() {
        return new TabCodeInput.TabCodeInputBuilder();
    }

    public static class TabCodeInputBuilder extends TabBuilder {
        private int code = -1;


        public TabCodeInput.TabCodeInputBuilder setCode(int code) {
            this.code = code;

            return this;
        }

        @Override
        public TabCodeInput build() {
            return new TabCodeInput(this);
        }
    }

    @Override
    public void closeTab() {
        inputField.setFocused(false);
        super.closeTab();
    }
}
