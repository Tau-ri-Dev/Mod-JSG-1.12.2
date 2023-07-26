package tauri.dev.jsg.gui.base;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import tauri.dev.jsg.gui.base.JSGButton.ActionCallback;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Adds setText that returns this inststance.
 *
 * @author MrJake222
 *
 */
public class JSGTextField extends GuiTextField {

	private ActionCallback actionCallback;
	private String originalContent;
	private boolean numbersOnly;

	public boolean performActionOnKeyUp = false;
	public JSGTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int width, int height, String originalContent) {
		super(componentId, fontrendererObj, x, y, width, height);
		this.originalContent = originalContent;
		setText(originalContent);
	}

	public JSGTextField setMaxStringLengthBetter(int maxNameLength) {
		super.setMaxStringLength(maxNameLength);
		return this;
	}

	public JSGTextField setActionCallback(ActionCallback callback) {
		actionCallback = callback;
		return this;
	}

	public JSGTextField setNumbersOnly() {
		this.numbersOnly = true;
		return this;
	}

	@Override
	@ParametersAreNonnullByDefault
	public void writeText(String textToWrite) {
		if (numbersOnly) {
			textToWrite = textToWrite.replaceAll("\\D+","");
		}

		super.writeText(textToWrite);

		if(performActionOnKeyUp){
			if (isFocused() && !originalContent.equals(getText())) {
				originalContent = getText();
				actionCallback.performAction();
			}
		}
	}

	@Override
	public void deleteFromCursor(int num){
		super.deleteFromCursor(num);

		if(performActionOnKeyUp){
			if (isFocused() && !originalContent.equals(getText())) {
				originalContent = getText();
				actionCallback.performAction();
			}
		}
	}

	@Override
	public void setFocused(boolean focused) {
		if (isFocused() && !focused && !originalContent.equals(getText())) {
			// Unfocused and changed name
			originalContent = getText();
			actionCallback.performAction();
		}
		else if(!isFocused() && focused){
			originalContent = getText();
		}

		super.setFocused(focused);
	}
}
