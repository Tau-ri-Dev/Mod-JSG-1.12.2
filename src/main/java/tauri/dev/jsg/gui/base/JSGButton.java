package tauri.dev.jsg.gui.base;

import net.minecraft.client.gui.GuiButton;

import java.util.ArrayList;
import java.util.List;

public class JSGButton extends GuiButton {

    public List<String> hoverText = new ArrayList<>();
    private ActionCallback actionCallback;

    public JSGButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    public void performAction() {
        actionCallback.performAction();
    }

    public JSGButton setFgColor(int fgColor) {
        packedFGColour = fgColor;
        return this;
    }

    public JSGButton setActionCallback(ActionCallback callback) {
        actionCallback = callback;
        return this;
    }

    public JSGButton setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public JSGButton setHoverText(List<String> lines){
        this.hoverText = lines;
        return this;
    }

    public interface ActionCallback {
        void performAction();
    }
}