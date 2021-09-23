package mrjake.aunis.gui;

import ibxm.Player;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.gui.element.NumberOnlyTextField;
import mrjake.aunis.item.gdo.GDOMessages;
import mrjake.aunis.stargate.network.StargateNetwork;
import mrjake.aunis.tileentity.stargate.StargateClassicBaseTile;
import mrjake.aunis.tileentity.stargate.StargateUniverseBaseTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import scala.reflect.internal.Trees;

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
            System.out.println(this.mc.player.getName() + " is sending code: " + codeField.getText());
            sendButton.playPressSound(this.mc.getSoundHandler());
            ItemStack gdo = this.mc.player.getHeldItem(hand);
            if(gdo.hasTagCompound()) {
                NBTTagCompound compound = gdo.getTagCompound();
                World world = this.mc.player.getEntityWorld();
                assert compound != null;
                if(compound.hasKey("linkedGate")){
                    BlockPos pos = BlockPos.fromLong(compound.getLong("linkedGate"));
                    StargateClassicBaseTile gateTile = (StargateClassicBaseTile) world.getTileEntity(pos);
                    assert gateTile != null;
                    StargateClassicBaseTile targetGate = null;
                    if(gateTile.isMerged() && gateTile.getStargateState().initiating() || gateTile.getStargateState().engaged()) {
                        targetGate = (StargateClassicBaseTile) StargateNetwork.get(world).getStargate(gateTile.getDialedAddress()).getTileEntity();
                        if (targetGate != null)
                            targetGate.receiveIrisCode(this.mc.player, Integer.parseInt(codeField.getText()));
                    }
                }
            }
            if(codeField.getText().length() < 1){
                this.mc.player.sendStatusMessage(GDOMessages.CODE_NOT_SET.textComponent, true);
            }
        }
    }
}
