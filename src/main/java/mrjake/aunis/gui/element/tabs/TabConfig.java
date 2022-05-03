package mrjake.aunis.gui.element.tabs;

import mrjake.aunis.config.ingame.AunisTileEntityConfig;
import mrjake.aunis.gui.element.GuiHelper;
import mrjake.aunis.tileentity.stargate.StargateClassicBaseTile;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;
import java.util.List;

public class TabConfig extends TabScrollAble {

    private final List<GuiTextField> FIELDS = new ArrayList<>();
    public AunisTileEntityConfig config;
    public StargateClassicBaseTile gateTile;
    private Runnable onTabClose = null;

    protected TabConfig(TabConfigBuilder builder) {
        super(builder);
        this.gateTile = builder.gateTile;
        updateConfig(gateTile.getConfig(), true);
    }

    public static TabConfig.TabConfigBuilder builder() {
        return new TabConfig.TabConfigBuilder();
    }

    @Override
    public void render(FontRenderer fontRenderer, int mouseX, int mouseY) {
        if (!isVisible()) return;
        super.render(fontRenderer, mouseX, mouseY);
        updateConfig(null, false); // update pos of fields
        for (GuiTextField field : FIELDS) {
            int id = field.getId() - 100;
            int y = field.y - 10;
            int x = field.x;
            if (canRenderEntry(x, y)) {
                if (id < 0) continue;
                fontRenderer.drawString(I18n.format(config.getOption(id).getLabel()), x, y, 4210752);
                field.drawTextBox();
            }
        }
        renderCover(fontRenderer);
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
    public boolean canContinueScrolling(int k) {
        int top = guiTop + defaultY + 30;
        int bottom = guiTop + defaultY + height - 34;
        if (FIELDS.size() < 1) return false;
        return ((FIELDS.get(0).y <= top) && k == 1) || ((FIELDS.get(FIELDS.size() - 1).y >= bottom) && k == -1);
    }

    @Override
    public boolean canRenderEntry(int x, int y) {
        int top = guiTop + defaultY + 3;
        int bottom = guiTop + defaultY + height;
        return y >= top && (y + FIELDS.get(0).height) <= (bottom - 12);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (GuiTextField field : FIELDS) {
            field.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        for (GuiTextField field : FIELDS) {
            field.textboxKeyTyped(typedChar, keyCode);
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

    public AunisTileEntityConfig getConfig(boolean saveValues) {
        if (saveValues) {
            for (GuiTextField field : FIELDS) {
                config.getOption(field.getId() - 100).setValue(field.getText());
            }
        }
        return config;
    }

    public void updateConfig(AunisTileEntityConfig config, boolean resetFields) {
        if (config != null) this.config = config;
        int k = guiTop + defaultY + 34;
        if (resetFields) {
            FIELDS.clear();
            for (int i = 0; i < this.config.getOptions().size(); i++) {
                if (this.config.getOption(i) != null) {
                    GuiTextField field = this.config.getOption(i).createField(k);
                    if (field != null) {
                        FIELDS.add(field);
                    }
                }
            }
        } else {
            for (int i = 0; i < FIELDS.size(); i++) {
                if (FIELDS.get(i) != null) {
                    FIELDS.get(i).y = 27 * i + k + scrolled;
                    FIELDS.get(i).x = guiLeft + 6 + currentOffsetX + 25;
                }
            }
        }
    }

    public static class TabConfigBuilder extends TabBuilder {

        private StargateClassicBaseTile gateTile;

        public TabConfigBuilder setGateTile(StargateClassicBaseTile gateTile) {
            this.gateTile = gateTile;
            return this;
        }

        @Override
        public TabConfig build() {
            return new TabConfig(this);
        }
    }
}
