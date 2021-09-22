package mrjake.aunis.gui;

import mrjake.aunis.gui.element.NumberOnlyTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumHand;

import java.io.IOException;

/**
 * @author matousss
 */
public class GuiSendCode extends GuiBase {
    EnumHand hand;
    public GuiSendCode() {
        super(250, 100, 4, FRAME_COLOR, BG_COLOR, TEXT_COLOR, 4);
        this.hand = hand;
    }

    NumberOnlyTextField codeField;
    AunisGuiButton sendButton;

    @Override
    public void initGui() {
        super.initGui();
        codeField = new NumberOnlyTextField(0, Minecraft.getMinecraft().fontRenderer, width/2-80, height/2-25, 160, 20);
        sendButton = new AunisGuiButton(1,width/2-70, height/2-25+28, 140, 20, I18n.format("gui.gdo.send_button"));

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.pushMatrix();
            translateToCenter();
            drawBackground();
            drawString(I18n.format("gui.gdo.send_code") + ": ", 5, 5, 0x00AA00);
        GlStateManager.popMatrix();
        codeField.drawTextBox();
        sendButton.drawButton(mc, mouseX, mouseY, Minecraft.getMinecraft().getRenderPartialTicks());
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        codeField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        //super.mouseClicked(mouseX, mouseY, mouseButton);
        codeField.mouseClicked(mouseX, mouseY, mouseButton);
        //if (GuiHelper.isPointInRegion(sendButton.x, sendButton.y, sendButton.x + sendButton.width, sendButton.y + sendButton.height, mouseX, mouseY)) {
        if(sendButton.mousePressed(this.mc, mouseX, mouseY)){
            System.out.println("sending code lol" + codeField.getText());
            sendButton.playPressSound(this.mc.getSoundHandler());
        }
    }
}
