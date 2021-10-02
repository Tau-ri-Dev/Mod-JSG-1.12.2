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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
        //super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.pushMatrix();
            translateToCenter();
            drawBackground();
            drawString(I18n.format("gui.gdo.send_code"), 5, 5, 0x00AA00);
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
            System.out.println("GDO: 1");
            sendButton.playPressSound(this.mc.getSoundHandler());
            ItemStack gdo = this.mc.player.getHeldItem(hand);
            if(gdo.hasTagCompound()) {
                System.out.println("GDO: 2");
                NBTTagCompound compound = gdo.getTagCompound();
                World world = this.mc.player.getEntityWorld();
                assert compound != null;
                if(compound.hasKey("linkedGate")){
                    System.out.println("GDO: 3");
                    int code = Integer.parseInt(codeField.getText());
                    AunisPacketHandler.INSTANCE.sendToServer(new GDOActionPacketToServer(GDOActionEnum.SEND_CODE, hand, code, false));

                    /*StargateUniverseBaseTile gateTile = (StargateUniverseBaseTile) world.getTileEntity(pos);
                    assert gateTile != null;
                    StargateClassicBaseTile targetGate = null;
                    if(gateTile.getStargateState().initiating() || gateTile.getStargateState().engaged()) {
                        System.out.println("GDO: 4");
                        targetGate = (StargateClassicBaseTile) StargateNetwork.get(world).getStargate(gateTile.getDialedAddress()).getTileEntity();
                        if (targetGate != null) {
                            targetGate.receiveIrisCode(this.mc.player, Integer.parseInt(codeField.getText()));
                            System.out.println("GDO: 5 - sending code");
                        }
                    }*/
                }
            }
            if(codeField.getText().length() < 1){
                this.mc.player.sendStatusMessage(GDOMessages.CODE_NOT_SET.textComponent, true);
            }
        }
    }
}
