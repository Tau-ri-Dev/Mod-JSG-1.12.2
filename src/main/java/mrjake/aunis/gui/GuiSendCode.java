package mrjake.aunis.gui;

import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.gui.element.NumberOnlyTextField;
import mrjake.aunis.item.gdo.GDOActionEnum;
import mrjake.aunis.item.gdo.GDOActionPacketToServer;
import mrjake.aunis.item.gdo.GDOMessages;
import mrjake.aunis.packet.AunisPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

import java.io.IOException;

/**
 * @author matousss
 */
public class GuiSendCode extends GuiBase {
    EnumHand hand;
    public GuiSendCode(EnumHand hand) {
        super(250, 100, 4, FRAME_COLOR, BG_COLOR, TEXT_COLOR, 4);
        this.hand = hand;
    }
    String message = null;
    int messageColor = 14876672;
    NumberOnlyTextField codeField;
    AunisGuiButton sendButton;

    @Override
    public void initGui() {
        super.initGui();
        codeField = new NumberOnlyTextField(0, Minecraft.getMinecraft().fontRenderer, width/2-80, height/2-25, 160, 20);
        codeField.setMaxStringLength(AunisConfig.irisConfig.irisCodeLength);
        sendButton = new AunisGuiButton(1,width/2-70, height/2-25+28, 140, 20, I18n.format("gui.gdo.send_button"));

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
            translateToCenter();
            drawBackground();
            drawString(I18n.format("gui.gdo.send_code"), 5, 5, 0x00AA00);
        GlStateManager.popMatrix();
        codeField.drawTextBox();
        sendButton.drawButton(mc, mouseX, mouseY, Minecraft.getMinecraft().getRenderPartialTicks());
        if (message != null) {
            drawCenteredString(mc.fontRenderer, message, width/2, sendButton.y + 25, messageColor);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        codeField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        codeField.mouseClicked(mouseX, mouseY, mouseButton);
        if(sendButton.mousePressed(this.mc, mouseX, mouseY)){
            sendButton.playPressSound(this.mc.getSoundHandler());
            ItemStack gdo = this.mc.player.getHeldItem(hand);
            if(gdo.hasTagCompound()) {
                NBTTagCompound compound = gdo.getTagCompound();
                if(compound.hasKey("linkedGate")){
                    if (codeField.getText().isEmpty()) {
                        message = GDOMessages.CODE_NOT_SET.textComponent.getFormattedText();
                        messageColor = 14876672;
                        return;
                    }
                    int code = Integer.parseInt(codeField.getText());
                    AunisPacketHandler.INSTANCE.sendToServer(new GDOActionPacketToServer(GDOActionEnum.SEND_CODE, hand, code, false));
                    this.mc.player.closeScreen();
                }
            }
        }
    }
}
