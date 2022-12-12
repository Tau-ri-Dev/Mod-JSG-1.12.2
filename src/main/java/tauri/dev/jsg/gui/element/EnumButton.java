package tauri.dev.jsg.gui.element;

import net.minecraft.client.Minecraft;
import tauri.dev.jsg.config.ingame.JSGConfigEnumEntry;

import java.util.List;

public class EnumButton extends ModeButton {
    public final List<JSGConfigEnumEntry> entries;

    public EnumButton(int buttonId, int x, int y, List<JSGConfigEnumEntry> entries) {
        super(buttonId, x, y, 16*4, 20, null, 0, 0, 0);
        this.entries = entries;
        this.states = entries.size();
    }

    @Override
    public void drawButton(int mouseX, int mouseY) {
        this.displayString = entries.get(getCurrentState()).name;
        this.width = Minecraft.getMinecraft().fontRenderer.getStringWidth(displayString) + 20;
        super.drawButton(Minecraft.getMinecraft(), mouseX, mouseY, 0);
    }
}
