package mrjake.aunis.gui.element;

import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.gui.AunisGuiButton;
import mrjake.aunis.stargate.EnumIrisMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * @author matousss
 */
public class TabIris extends Tab {
    protected static final ResourceLocation MODES_ICONS =
            new ResourceLocation(Aunis.ModID, "textures/gui/iris_mode.png");


    protected NumberOnlyTextField inputField = new NumberOnlyTextField(1,
            Minecraft.getMinecraft().fontRenderer, guiLeft + 6, guiTop + defaultY + 25,
            64, 16);

    public int code;
    public EnumIrisMode irisMode;
    protected ModeButton buttonChangeMode = new ModeButton(
            1, inputField.x + inputField.width + 5, guiTop + defaultY + 25, 16, MODES_ICONS,
            64, 32, 4);


    protected TabIris(TabIrisBuilder builder) {
        super(builder);
        this.irisMode = builder.irisMode;
        code = builder.code;
        buttonChangeMode.setCurrentState(irisMode.id);
        inputField.setMaxStringLength(AunisConfig.irisConfig.irisCodeLength);
        inputField.setText(code > -1 ? Integer.toString(code) : "");
        inputField.setEnabled(buttonChangeMode.getCurrentState() == EnumIrisMode.AUTO.id);
    }

    public void updateValue(EnumIrisMode irisMode) {
        buttonChangeMode.setCurrentState(irisMode.id);
        inputField.setEnabled(buttonChangeMode.getCurrentState() == EnumIrisMode.AUTO.id);
    }

    public void updateValue(int irisCode) {
        inputField.setText(irisCode > 0 ? irisCode + "" : "");
    }

    @Override
    public void render(FontRenderer fontRenderer, int mouseX, int mouseY) {
        if (!isVisible()) return;

        super.render(fontRenderer, mouseX, mouseY);
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        buttonChangeMode.x = guiLeft + currentOffsetX + 64 + 11;
        buttonChangeMode.drawButton(mouseX, mouseY);
        GlStateManager.disableBlend();
        inputField.x = guiLeft + 6 + currentOffsetX;
        inputField.drawTextBox();
    }


    public static TabIris.TabIrisBuilder builder() {
        return new TabIris.TabIrisBuilder();
    }

    public static class TabIrisBuilder extends TabBuilder {
        private EnumIrisMode irisMode = EnumIrisMode.OPENED;

        private int code = -1;


        public TabIris.TabIrisBuilder setCode(int code) {
            this.code = code;
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
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        inputField.mouseClicked(mouseX, mouseY, mouseButton);
        if (GuiHelper.isPointInRegion(buttonChangeMode.x, buttonChangeMode.y,
                buttonChangeMode.width, buttonChangeMode.height, mouseX, mouseY)) {
            System.out.println("posouvám lol");
            switch (mouseButton) {
                case 0:
                    buttonChangeMode.nextState();
                    break;
                case 1:
                    buttonChangeMode.previousState();
                    break;
                case 2:
                    buttonChangeMode.setCurrentState(0);
                    break;

            }

            inputField.setEnabled(buttonChangeMode.getCurrentState() == EnumIrisMode.AUTO.id);
            buttonChangeMode.playPressSound(Minecraft.getMinecraft().getSoundHandler());
        }
        System.out.println("je to " + getIrisMode().name());

    }

    public EnumIrisMode getIrisMode() {
        return EnumIrisMode.getValue((byte) buttonChangeMode.getCurrentState());
    }

    public int getCode() {
        return inputField.getText().length() > 0 ? Integer.parseInt(inputField.getText()) : -1;
    }

    public void keyTyped(char typedChar, int keyCode) {
        inputField.textboxKeyTyped(typedChar, keyCode);
    }
}
