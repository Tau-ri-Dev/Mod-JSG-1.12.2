package tauri.dev.jsg.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.packet.AncientSignSaveToServer;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.renderer.AncientRenderer;
import tauri.dev.jsg.tileentity.props.AncientSignTile;

import java.awt.*;
import java.io.IOException;

import static tauri.dev.jsg.renderer.AncientRenderer.isCharNotAllowed;
import static tauri.dev.jsg.renderer.props.AncientSignRenderer.ONE_CHAR_X;
import static tauri.dev.jsg.renderer.props.AncientSignRenderer.ONE_CHAR_Y;

@SideOnly(Side.CLIENT)
public class AncientSignEditGui extends JSGTexturedGui {
    private final AncientSignTile tileSign;
    private int updateCounter;
    private int editLine;
    private GuiButton doneBtn;

    public AncientSignEditGui(AncientSignTile teSign) {
        super(176, 140);
        this.tileSign = teSign;
    }

    @Override
    public ResourceLocation getBackground() {
        return new ResourceLocation(JSG.MOD_ID, "textures/gui/gui_ancient_sign.png");
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        int width = fontRenderer.getStringWidth(I18n.format("gui.done")) + 20;
        this.doneBtn = this.addButton(new GuiButton(0, guiLeft + 8, guiTop + ySize - 25, width, 20, I18n.format("gui.done")));
        this.tileSign.setEditable(false);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        JSGPacketHandler.INSTANCE.sendToServer(new AncientSignSaveToServer(tileSign.getPos(), tileSign.ancientText));

        this.tileSign.setEditable(true);
    }

    @Override
    public void updateScreen() {
        ++this.updateCounter;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 0) {
                this.tileSign.markDirty();
                this.mc.displayGuiScreen(null);
            }
        }
    }

    public void addString(String s){
        StringBuilder ss = new StringBuilder(this.tileSign.ancientText[this.editLine]);
        for(int i = 0; i < s.length(); i++) {
            char typedChar = s.charAt(i);
            if (!isCharNotAllowed(typedChar) && ONE_CHAR_X * (ss.length() + 1) <= 90) {
                ss.append(typedChar);
            }
        }
        this.tileSign.ancientText[this.editLine] = ss.toString();
    }

    public void removeChar(){
        String s = this.tileSign.ancientText[this.editLine];
        s = s.substring(0, s.length() - 1);
        this.tileSign.ancientText[this.editLine] = s;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(GuiScreen.isKeyComboCtrlV(keyCode)){
            addString(GuiScreen.getClipboardString());
            return;
        }

        if (keyCode == 200) {
            this.editLine = this.editLine - 1;
            if (this.editLine < 0) this.editLine = (AncientSignTile.LINES - 1);
            return;
        } else if (keyCode == 208 || keyCode == 28 || keyCode == 156) {
            this.editLine = this.editLine + 1;
            if (this.editLine > (AncientSignTile.LINES - 1))
                this.editLine = 0;
            return;
        } else if (keyCode == 1) {
            this.actionPerformed(this.doneBtn);
            return;
        }

        String s = this.tileSign.ancientText[this.editLine];
        if (keyCode == 14 && !s.isEmpty())
            removeChar();
        else
            addString(typedChar + "");
    }


    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks) {
        fontRenderer.drawString(I18n.format("gui.ancient_sign.edit"), 8, 16, 0x404040);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.pushMatrix();
        GlStateManager.translate(width / 2f, guiTop + 30, 50.0F);

        if (updateCounter / 6 % 2 == 0) {
            tileSign.lineBeingEdited = this.editLine;
        }

        String[] lines = tileSign.ancientText;

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.9, 0.9, 0.9);

        Color color = new Color(0x404040);
        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);

        int i = 0;
        for (String l : lines) {
            GlStateManager.pushMatrix();

            if (tileSign.lineBeingEdited == i)
                l = ": " + l + " :";

            double startX = -((ONE_CHAR_X * l.length()) / 2);
            GlStateManager.translate(startX, 0, 0);
            AncientRenderer.renderString(l, true);
            GlStateManager.popMatrix();
            GlStateManager.translate(0, ONE_CHAR_Y, 0);
            i++;
        }

        GlStateManager.popMatrix();

        this.tileSign.lineBeingEdited = -1;
        GlStateManager.popMatrix();

    }
}
