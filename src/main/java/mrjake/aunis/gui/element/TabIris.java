package mrjake.aunis.gui.element;

import mrjake.aunis.Aunis;
import mrjake.aunis.stargate.EnumIrisMode;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * @author matousss
 */
public class TabIris extends TabCodeInput{
    protected static final ResourceLocation MODES_ICONS =
            new ResourceLocation(Aunis.ModID, "textures/gui/iris_mode.png");

    protected EnumIrisMode irisMode;
    protected ModeButton buttonChangeMode = new ModeButton(
            1,inputField.x + inputField.width + 5, inputField.y, 16, MODES_ICONS, 32, 128, 4);

    protected TabIris(TabIrisBuilder builder) {
        super(builder);
        this.irisMode = builder.irisMode;
    }

    @Override
    public void render(FontRenderer fontRenderer, int mouseX, int mouseY) {
        super.render(fontRenderer, mouseX, mouseY);
        buttonChangeMode.drawButton(mouseX, mouseY, irisMode.id);
    }


    //todo fix this shit
    public static TabIris.TabIrisBuilder builder() {
        return new TabIris.TabIrisBuilder();
    }

    public static class TabIrisBuilder extends TabCodeInputBuilder {
        private EnumIrisMode irisMode = EnumIrisMode.OPENED;

        public TabIrisBuilder setIrisMode(EnumIrisMode irisMode) {
            this.irisMode = irisMode;
            return this;
        }

        @Override
        public TabCodeInput build() {
            return new TabIris(this);
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        inputField.mouseClicked(mouseX, mouseY, mouseButton);
        if (GuiHelper.isPointInRegion(buttonChangeMode.x, buttonChangeMode.y,
                buttonChangeMode.width, buttonChangeMode.height, mouseX, mouseY)) {
            if (mouseButton == 0) {
                System.out.println("hovno");
            }
            else if (mouseButton == 1) {
                System.out.println("vepřové");
            }
            else System.out.println("?????????????????? " + mouseButton );
        }

    }

}
