package mrjake.aunis.gui;

import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.gui.element.NumberOnlyTextField;
import mrjake.aunis.item.gdo.GDOActionEnum;
import mrjake.aunis.item.gdo.GDOActionPacketToServer;
import mrjake.aunis.item.gdo.GDOMessages;
import mrjake.aunis.packet.AunisPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author matousss
 */
public class GuiSendCode extends GuiBase {
    EnumHand hand;
    public GuiSendCode(EnumHand hand) {
        super(260, 200, 4, FRAME_COLOR, BG_COLOR, TEXT_COLOR, 4);
        this.hand = hand;
    }
    String message = null;
    int messageColor = 14876672;
    AunisGuiButton sendButton;
    NumberOnlyTextField codeField;
    protected List<GuiButton> aunisButtonList = new ArrayList<>();

    @Override
    public void initGui() {
        super.initGui();
        aunisButtonList.clear();
        codeField = new NumberOnlyTextField(0, Minecraft.getMinecraft().fontRenderer, width/2-80, height/2-75, 160, 20);
        codeField.setMaxStringLength(AunisConfig.irisConfig.irisCodeLength);
        codeField.setFocused(true);

        // init num pad
        int i = 0;
        for(int y = 0; y < 4; y++) {
            for(int x = 0; x < 3; x++) {
                if(y != 3){
                    aunisButtonList.add(new AunisGuiButton(i + 3, (width / 2 - 48) + (33 * x), (height / 2 - 35) + (33 * y), 30, 30, "" + (i + 1)));
                    i++;
                }
                else{
                    if(x == 0) sendButton = new AunisGuiButton(1,(width / 2 - 48), (height / 2 - 35) + (33 * y), 30, 30, "OK");
                    if(x == 1) aunisButtonList.add(new AunisGuiButton(12, (width / 2 - 48) + 33, (height / 2 - 35) + (33 * y), 30, 30, "0"));
                    if(x == 2) aunisButtonList.add(new AunisGuiButton(13, (width / 2 - 48) + (33 * 2), (height / 2 - 35) + (33 * y), 30, 30, "<-"));
                }
            }
        }
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

        for (GuiButton button : aunisButtonList) {
            button.enabled = true;
            if(button.id == 13){
                button.enabled = codeField.getText().length() > 0;
                sendButton.enabled = button.enabled;
            }
            else{
                button.enabled = codeField.getText().length() < AunisConfig.irisConfig.irisCodeLength;
            }
        }

        // draw num pad
        for (GuiButton guiButton : this.aunisButtonList) {
            ((AunisGuiButton) guiButton).drawButton(this.mc, mouseX, mouseY, partialTicks);
        }


        if (message != null) {
            drawCenteredString(mc.fontRenderer, message, width/2, codeField.y + 25, messageColor);
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
                        codeField.setFocused(true);
                        return;
                    }
                    int code = Integer.parseInt(codeField.getText());
                    AunisPacketHandler.INSTANCE.sendToServer(new GDOActionPacketToServer(GDOActionEnum.SEND_CODE, hand, code, false));
                    this.mc.player.closeScreen();
                }
            }
            codeField.setFocused(true);
        }

        // click on num pad
        for (GuiButton guibutton : this.aunisButtonList) {
            if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
                guibutton.playPressSound(this.mc.getSoundHandler());
                if (guibutton.id != 13)
                    codeField.setText(codeField.getText() + guibutton.displayString);
                else {
                    if(codeField.getText().length() > 0)
                        codeField.setText(codeField.getText().substring(0, codeField.getText().length() - 1));
                }
            }
        }
    }
}
