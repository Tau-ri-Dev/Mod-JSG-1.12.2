package tauri.dev.jsg.gui.element.tabs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class TabTRSettings extends Tab {

    private String name;
    private int dist;
    public GuiTextField nameTextField;
    public GuiTextField distanceTextField;
    int boxHeight = 10;
    int boxWidth = (107 - 50);

    private boolean keyTyped = false;

    public void setParams(String name, int dist) {
        this.name = name;
        this.dist = dist;
    }

    protected TabTRSettings(TabTRSettingsBuilder builder) {
        super(builder);
        this.name = builder.name;
        this.dist = builder.dist;

        int id = 0;

        nameTextField = new GuiTextField(++id,
                Minecraft.getMinecraft().fontRenderer, 0, boxHeight,
                boxWidth, boxHeight);
        nameTextField.setText(name);
        distanceTextField = new GuiTextField(++id,
                Minecraft.getMinecraft().fontRenderer, 0, boxHeight,
                boxWidth, boxHeight);
        distanceTextField.setText(dist + "");
    }

    // todo(Mine): temporarily solution
    public void tryToUpdateInputs() {
        if (!keyTyped) {
            try {
                if (!(nameTextField.getText().equals(name)))
                    nameTextField.setText(name);
                if (Integer.parseInt(distanceTextField.getText()) != dist)
                    distanceTextField.setText(dist + "");
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void render(FontRenderer fontRenderer, int mouseX, int mouseY) {
        if (!isVisible()) return;
        tryToUpdateInputs();
        super.render(fontRenderer, mouseX, mouseY);

        int y = guiTop + defaultY + 20;
        int x = guiLeft + currentOffsetX + 15;
        nameTextField.x = x;
        nameTextField.y = y + boxHeight;
        nameTextField.drawTextBox();
        fontRenderer.drawString(I18n.format("tile.jsg.transportrings_block.rings_name"), x, y, 4210752);

        y += 22;
        distanceTextField.x = x;
        distanceTextField.y = y + boxHeight;
        distanceTextField.drawTextBox();
        fontRenderer.drawString(I18n.format("tile.jsg.transportrings_block.rings_distance"), x, y, 4210752);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        nameTextField.mouseClicked(mouseX, mouseY, mouseButton);
        distanceTextField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {
        if (nameTextField.textboxKeyTyped(typedChar, keyCode)) {
            keyTyped = true;
            return true;
        }
        if (distanceTextField.textboxKeyTyped(typedChar, keyCode)) {
            keyTyped = true;
            return true;
        }

        return false;
    }

    @Override
    public void updateScreen() {
        nameTextField.updateCursorCounter();
        distanceTextField.updateCursorCounter();
    }

    public static TabTRSettings.TabTRSettingsBuilder builder() {
        return new TabTRSettings.TabTRSettingsBuilder();
    }

    public static class TabTRSettingsBuilder extends TabBuilder {

        public String name;
        public int dist;

        public TabTRSettingsBuilder setParams(String name, int dist) {
            this.name = name;
            this.dist = dist;
            return this;
        }

        @Override
        public TabTRSettings build() {
            return new TabTRSettings(this);
        }
    }
}
