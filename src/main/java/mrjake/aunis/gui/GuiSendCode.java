package mrjake.aunis.gui;

import mrjake.aunis.Aunis;
import mrjake.aunis.gui.element.GuiHelper;
import mrjake.aunis.gui.element.NumberOnlyTextField;
import mrjake.aunis.item.gdo.GDOActionEnum;
import mrjake.aunis.item.gdo.GDOActionPacketToServer;
import mrjake.aunis.packet.AunisPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumHand;

import java.io.IOException;

/**
 * @author matousss
 */
public class GuiSendCode extends GuiBase {
    EnumHand hand;
    public GuiSendCode() {
        super(196, 160, 8, FRAME_COLOR, BG_COLOR, TEXT_COLOR, 4);
        this.hand = hand;
    }

    NumberOnlyTextField codeField;
    AunisGuiButton sendButton;

    @Override
    public void initGui() {
        super.initGui();
        codeField = new NumberOnlyTextField(0, Minecraft.getMinecraft().fontRenderer,
                50, 20, 128, 64);

        sendButton = new AunisGuiButton(1,54, 40, 120, 62, I18n.format("aunis.gui.send"));

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        translateToCenter();
        drawBackground();
        drawString(I18n.format("aunis.gdo.enter_code") + ": ", 0, 20, 0x00AA00);
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
        super.mouseClicked(mouseX, mouseY, mouseButton);
        codeField.mouseClicked(mouseX, mouseY, mouseButton);

        if (GuiHelper.isPointInRegion(sendButton.x, sendButton.y, sendButton.x + sendButton.width, sendButton.y + sendButton.height, mouseX, mouseY)) {
            System.out.println("sending code lol");
        }
    }
}
