package tauri.dev.jsg.gui.base;

import net.minecraft.client.gui.GuiButton;

public class JSGButton extends GuiButton {

	private ActionCallback actionCallback;
	
	public void performAction() {
		actionCallback.performAction();
	}
	
	public JSGButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
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
	
	public interface ActionCallback {
		void performAction();
	}
}