package mrjake.aunis.gui.element;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

/**
 * @author matousss
 */
public class NumberOnlyTextField extends GuiTextField {
    int rootX;
    int rootY;

    // forge is ...
    protected boolean isEnabled;


    public NumberOnlyTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
        super(componentId, fontrendererObj, x, y, par5Width, par6Height);
    }

    @Override
    public void writeText(String textToWrite) {
        textToWrite = textToWrite.replaceAll("\\D+", "");
        super.writeText(textToWrite);
    }

    public void setNumber(double number){
        setText(number + "");
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled && isFocused()) setFocused(false);
        this.isEnabled = enabled;
        super.setEnabled(enabled);
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
