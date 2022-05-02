package mrjake.aunis.gui.element.tabs;

import mrjake.aunis.config.ingame.AunisTileEntityConfig;
import mrjake.aunis.gui.element.GuiHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;
import java.util.List;

public class TabConfig extends TabScrollAble {


    private final List<GuiTextField> FIELDS = new ArrayList<>();
    protected AunisTileEntityConfig config;
    private Runnable onTabClose = null;

    protected TabConfig(TabConfigBuilder builder) {
        super(builder);
        this.config = builder.config;

        int count = 0;
        for (int i = 0; i < config.getOptions().size(); i++) {
            if (config.getOption(i) != null) {
                GuiTextField field = config.getOption(i).createField(30 * count + guiTop + defaultY + 5);
                if (field != null) {
                    FIELDS.add(field);
                    count++;
                }
            }
        }
    }

    public static TabConfig.TabConfigBuilder builder() {
        return new TabConfig.TabConfigBuilder();
    }

    @Override
    public void render(FontRenderer fontRenderer, int mouseX, int mouseY) {
        if (!isVisible()) return;
        super.render(fontRenderer, mouseX, mouseY);

        for (GuiTextField field : FIELDS) {
            int id = field.getId() - 100;
            int y = field.y - 10;
            int x = guiLeft + 6 + currentOffsetX;
            if (canRenderEntry(x, y)) {
                if (id < 0) continue;
                fontRenderer.drawString(I18n.format(config.getOption(id).getLabel()), x, y, 4210752);

                field.x = x;
                field.drawTextBox();
            }
        }
    }

    @Override
    public void renderFg(GuiScreen screen, FontRenderer fontRenderer, int mouseX, int mouseY) {
        super.renderFg(screen, fontRenderer, mouseX, mouseY);
        if (isVisible() && isOpen()) {
            for (GuiTextField field : FIELDS) {
                int y = field.y - 10;
                int x = field.x;
                int id = field.getId() - 100;

                if (GuiHelper.isPointInRegion(x, y, field.width, 10 + field.height, mouseX, mouseY)) {
                    screen.drawHoveringText(config.getOption(id).getComment(), mouseX - guiLeft, mouseY - guiTop);
                }
            }
        }
    }

    @Override
    public void scroll(int k) {
        if (k == 0) return;
        if (k < 0) k = -1;
        if (k > 0) k = 1;
        if (isVisible() && isOpen() && canContinueScrolling(k)) {
            for (GuiTextField field : FIELDS) {
                field.y += SCROLL_AMOUNT * k;
            }
        }
    }

    @Override
    public boolean canContinueScrolling(int k) {
        int top = 25 + guiTop + defaultY + 5;
        int bottom = guiTop + defaultY + height;
        if (FIELDS.size() < 1) return false;
        return ((FIELDS.get(0).y <= top) && k == 1) || ((FIELDS.get(FIELDS.size() - 1).y >= (bottom - 15 - 10)) && k == -1);
    }

    @Override
    public boolean canRenderEntry(int x, int y) {
        int top = 25 + guiTop + defaultY + 3;
        int bottom = guiTop + defaultY + height;
        return y >= (top - 2) && (y + FIELDS.get(0).height + 2) <= bottom;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isVisible() && isOpen()) {
            for (GuiTextField field : FIELDS) {
                field.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (isVisible() && isOpen()) {
            for (GuiTextField field : FIELDS) {
                field.textboxKeyTyped(typedChar, keyCode);
            }
        }
    }

    public void setOnTabClose(Runnable onTabClose) {
        this.onTabClose = onTabClose;
    }

    @Override
    public void closeTab() {
        if (onTabClose != null) onTabClose.run();
        super.closeTab();

    }

    public static class TabConfigBuilder extends TabBuilder {
        private AunisTileEntityConfig config;

        public TabBuilder setConfig(AunisTileEntityConfig config) {
            this.config = config;
            return this;
        }

        @Override
        public TabConfig build() {
            return new TabConfig(this);
        }
    }
}
