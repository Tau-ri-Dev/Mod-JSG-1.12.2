package mrjake.aunis.gui.element.tabs;

import mrjake.aunis.config.ingame.AunisConfigOption;
import mrjake.aunis.config.ingame.AunisConfigOptionTypeEnum;
import mrjake.aunis.config.ingame.AunisTileEntityConfig;
import mrjake.aunis.gui.element.GuiHelper;
import mrjake.aunis.gui.element.ModeButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;
import java.util.List;

public class TabConfig extends TabScrollAble {

    private final List<GuiTextField> FIELDS = new ArrayList<>();
    private final List<ModeButton> BUTTONS = new ArrayList<>();
    public AunisTileEntityConfig config;
    private Runnable onTabClose = null;

    protected TabConfig(TabConfigBuilder builder) {
        super(builder);
        this.config = builder.config;
        updateConfig(builder.config, true);
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
        for (ModeButton button : BUTTONS) {
            int id = button.id - 100;
            int y = button.y - 10;
            int x = button.x;
            if (canRenderEntry(x, y)) {
                if (id < 0) continue;
                fontRenderer.drawString(I18n.format(config.getOption(id).getLabel()), x, y, 4210752);
                GlStateManager.enableBlend();
                GlStateManager.color(1, 1, 1, 1);
                button.drawButton(mouseX, mouseY);
                GlStateManager.disableBlend();
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
                    screen.drawHoveringText(config.getOption(id).getCommentToRender(), mouseX - guiLeft, mouseY - guiTop);
                }
            }
            for (ModeButton button : BUTTONS) {
                int y = button.y - 10;
                int x = button.x;
                int id = button.id - 100;

                if (GuiHelper.isPointInRegion(x, y, 75, 10 + button.height, mouseX, mouseY)) {
                    screen.drawHoveringText(config.getOption(id).getCommentToRender(), mouseX - guiLeft, mouseY - guiTop);
                }
            }
        }
    }

    @Override
    public boolean canContinueScrolling(int k) {
        int top = guiTop + defaultY + 30;
        int bottom = guiTop + defaultY + height - 34;
        if (FIELDS.size() < 1 && BUTTONS.size() < 1) return false;

        boolean isTop = ((FIELDS.size() > 0 && FIELDS.get(0).getId() < BUTTONS.get(0).id) ? FIELDS.get(0).y > top : BUTTONS.get(0).y > top);
        boolean isBottom = ((FIELDS.size() > 0 && FIELDS.get(FIELDS.size() - 1).getId() >= BUTTONS.get(BUTTONS.size() - 1).id) ? FIELDS.get(FIELDS.size() - 1).y < bottom : BUTTONS.get(BUTTONS.size() - 1).y < bottom);

        return (!isTop && k == 1) || (!isBottom && k == -1);
    }

    @Override
    public boolean canRenderEntry(int x, int y) {
        int top = guiTop + defaultY + 3;
        int bottom = guiTop + defaultY + height;
        int height = ((FIELDS.size() > 0) ? (FIELDS.get(0).height) : ((BUTTONS.size() > 0) ? (BUTTONS.get(0).height) : (16)));
        return y >= top && (y + height) <= (bottom - 12);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (GuiTextField field : FIELDS) {
            field.mouseClicked(mouseX, mouseY, mouseButton);
        }
        for (ModeButton button : BUTTONS) {
            if (GuiHelper.isPointInRegion(button.x, button.y,
                    button.width, button.height, mouseX, mouseY)) {
                switch (mouseButton) {
                    case 0:
                        button.nextState();
                        break;
                    case 1:
                        button.previousState();
                        break;
                    case 2:
                        button.setCurrentState(config.getOption(button.id - 100).getIntValue(true));
                        break;

                }
                button.playPressSound(Minecraft.getMinecraft().getSoundHandler());
            }
        }
        getConfig(true);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        for (GuiTextField field : FIELDS) {
            field.textboxKeyTyped(typedChar, keyCode);
        }
        getConfig(true);
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
            for (ModeButton button : BUTTONS) {
                config.getOption(button.id - 100).setValue(button.getCurrentState() + "");
            }
        }
        return config;
    }

    public void updateConfig(AunisTileEntityConfig config, boolean resetFields) {
        if (config != null) this.config = config;
        int k = guiTop + defaultY + 34;
        if (resetFields) {
            FIELDS.clear();
            BUTTONS.clear();
            for (int i = 0; i < this.config.getOptions().size(); i++) {
                AunisConfigOption option = this.config.getOption(i);
                if (option != null) {
                    if (option.type == AunisConfigOptionTypeEnum.SWITCH || option.type == AunisConfigOptionTypeEnum.BOOLEAN) {
                        ModeButton btn = option.createButton(k);
                        if (btn != null) {
                            BUTTONS.add(btn);
                        }
                    } else {
                        GuiTextField field = option.createField(k);
                        if (field != null) {
                            FIELDS.add(field);
                        }
                    }
                }
            }
        } else {
            for (ModeButton btn : BUTTONS) {
                if (btn != null) {
                    btn.y = 27 * (btn.id - 100) + k + scrolled;
                    btn.x = guiLeft + 6 + currentOffsetX + 25;
                }
            }
            for (GuiTextField field : FIELDS) {
                if (field != null) {
                    field.y = 27 * (field.getId() - 100) + k + scrolled;
                    field.x = guiLeft + 6 + currentOffsetX + 25;
                }
            }
        }
    }

    public static class TabConfigBuilder extends TabBuilder {

        private AunisTileEntityConfig config;

        public TabConfigBuilder setConfig(AunisTileEntityConfig config) {
            this.config = config;
            return this;
        }

        @Override
        public TabConfig build() {
            return new TabConfig(this);
        }
    }
}
