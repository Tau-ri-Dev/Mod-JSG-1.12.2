package tauri.dev.jsg.gui.element.tabs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.gui.element.GuiHelper;
import tauri.dev.jsg.gui.element.ModeButton;
import tauri.dev.jsg.gui.element.NumberOnlyTextField;
import tauri.dev.jsg.stargate.EnumIrisMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author matousss
 */
public class TabIris extends Tab {
    protected static final ResourceLocation MODES_ICONS =
            new ResourceLocation(JSG.MOD_ID, "textures/gui/iris_mode.png");


    protected NumberOnlyTextField inputField = new NumberOnlyTextField(1,
            Minecraft.getMinecraft().fontRenderer, guiLeft + 6, guiTop + defaultY + 25,
            64, 16);

    public String code;
    public EnumIrisMode irisMode;
    protected ModeButton buttonChangeMode = new ModeButton(
            1, inputField.x + inputField.width + 5, guiTop + defaultY + 25, 16, MODES_ICONS,
            80, 32, 5);

    protected boolean isUniverse;


    protected TabIris(TabIrisBuilder builder) {
        super(builder);
        this.irisMode = builder.irisMode;
        this.isUniverse = builder.isUniverse;
        code = builder.code;
        buttonChangeMode.setCurrentState(irisMode.id);
        inputField.setMaxStringLength(JSGConfig.Stargate.iris.irisCodeLength);
        inputField.setText(code);
        inputField.setEnabled(buttonChangeMode.getCurrentState() == EnumIrisMode.AUTO.id);
    }

    public void updateValue(EnumIrisMode irisMode) {
        buttonChangeMode.setCurrentState(irisMode.id);
        inputField.setEnabled(buttonChangeMode.getCurrentState() == EnumIrisMode.AUTO.id);
    }

    public void updateValue(String irisCode) {
        inputField.setText(irisCode);
    }

    @Override
    public void render(FontRenderer fontRenderer, int mouseX, int mouseY) {
        if (!isVisible()) return;

        super.render(fontRenderer, mouseX, mouseY);
        buttonChangeMode.x = guiLeft + currentOffsetX + 64 + 11;
        buttonChangeMode.drawButton(mouseX, mouseY);
        inputField.x = guiLeft + 6 + currentOffsetX;
        inputField.drawTextBox();
    }

    @Override
    public void renderFg(GuiScreen screen, FontRenderer fontRenderer, int mouseX, int mouseY) {
        super.renderFg(screen, fontRenderer, mouseX, mouseY);
        if (isVisible() && isOpen()) {
            if (GuiHelper.isPointInRegion(buttonChangeMode.x, buttonChangeMode.y, buttonChangeMode.getButtonWidth(), buttonChangeMode.getButtonWidth(), mouseX, mouseY)) {
                List<String> text = new ArrayList<>();
                text.add(I18n.format("gui.stargate.iris.help_title") +" "+ I18n.format("gui.stargate.iris."+getIrisMode().name().toLowerCase()));
                text.add(TextFormatting.GRAY + I18n.format("gui.stargate.iris." + getIrisMode().name()
                        .toLowerCase() + "_help")
                );
                if (buttonChangeMode.getCurrentState() == 2) {
                    text.add(TextFormatting.GRAY + I18n.format("gui.stargate.iris.auto1_help"));
                }
                screen.drawHoveringText(text, mouseX - guiLeft, mouseY - guiTop);
            }
        }
    }

    public static TabIris.TabIrisBuilder builder() {
        return new TabIris.TabIrisBuilder();
    }

    public static class TabIrisBuilder extends TabBuilder {
        private EnumIrisMode irisMode = EnumIrisMode.OPENED;

        private String code = "";
        private boolean isUniverse = false;


        public TabIris.TabIrisBuilder setCode(String code) {
            this.code = code;
            return this;
        }

        public TabIrisBuilder setIsUniverse(boolean is){
            isUniverse = is;
            return this;
        }

        public TabIrisBuilder setIrisMode(EnumIrisMode irisMode) {
            this.irisMode = irisMode;
            return this;
        }

        @Override
        public TabIris build() {
            return new TabIris(this);
        }
    }

    /*
     * left = 0
     * right = 1
     * middle = 2
     * */

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        inputField.mouseClicked(mouseX, mouseY, mouseButton);
        if (GuiHelper.isPointInRegion(buttonChangeMode.x, buttonChangeMode.y,
                buttonChangeMode.width, buttonChangeMode.height, mouseX, mouseY)) {
            switch (mouseButton) {
                case 0:
                    buttonChangeMode.nextState();
                    if (!JSG.ocWrapper.isModLoaded() && buttonChangeMode.getCurrentState() == 3) {
                        buttonChangeMode.nextState();
                    }
                    if (!isUniverse && buttonChangeMode.getCurrentState() == 4) {
                        buttonChangeMode.nextState();
                    }
                    break;
                case 1:
                    buttonChangeMode.previousState();
                    if (!isUniverse && buttonChangeMode.getCurrentState() == 4) {
                        buttonChangeMode.previousState();
                    }
                    if (!JSG.ocWrapper.isModLoaded() && buttonChangeMode.getCurrentState() == 3) {
                        buttonChangeMode.previousState();
                    }
                    break;
                case 2:
                    buttonChangeMode.setCurrentState(0);
                    break;

            }

            inputField.setEnabled(buttonChangeMode.getCurrentState() == EnumIrisMode.AUTO.id);
            buttonChangeMode.playPressSound(Minecraft.getMinecraft().getSoundHandler());
        }

    }

    public EnumIrisMode getIrisMode() {
        return EnumIrisMode.getValue((byte) buttonChangeMode.getCurrentState());
    }

    public String getCode() {
        return inputField.getText().length() > 0 ? inputField.getText() : "";
    }

    public boolean keyTyped(char typedChar, int keyCode) {
        return inputField.textboxKeyTyped(typedChar, keyCode);
    }

    private Runnable onTabClose = null;

    public void setOnTabClose(Runnable onTabClose) {
        this.onTabClose = onTabClose;
    }

    @Override
    public void closeTab() {
        if (onTabClose != null) onTabClose.run();

        super.closeTab();

    }
}
