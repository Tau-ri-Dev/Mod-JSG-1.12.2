package tauri.dev.jsg.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import tauri.dev.jsg.packet.AncientSignSaveToServer;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.renderer.props.AncientSignRenderer;
import tauri.dev.jsg.tileentity.props.AncientSignTile;
import tauri.dev.jsg.util.FacingToRotation;
import tauri.dev.jsg.util.main.JSGProps;

import java.io.IOException;

import static tauri.dev.jsg.renderer.AncientRenderer.isCharNotAllowed;

@SideOnly(Side.CLIENT)
public class AncientSignEditGui extends GuiScreen {
    private final AncientSignTile tileSign;
    private int updateCounter;
    private int editLine;
    private GuiButton doneBtn;

    public AncientSignEditGui(AncientSignTile teSign) {
        this.tileSign = teSign;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        this.doneBtn = this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, I18n.format("gui.done")));
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

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 200) {
            this.editLine = this.editLine - 1;
            if(this.editLine < 0) this.editLine = (AncientSignTile.LINES-1);
            return;
        }
        else if (keyCode == 208 || keyCode == 28 || keyCode == 156) {
            this.editLine = this.editLine + 1;
            if(this.editLine > (AncientSignTile.LINES-1))
                this.editLine = 0;
            return;
        }
        else if (keyCode == 1) {
            this.actionPerformed(this.doneBtn);
            return;
        }

        String s = this.tileSign.ancientText[this.editLine];

        if (keyCode == 14 && !s.isEmpty()) {
            s = s.substring(0, s.length() - 1);
        }
        else if (!isCharNotAllowed(typedChar) && AncientSignRenderer.ONE_CHAR_X*(s.length() + 1) <= 90) {
            s = s + typedChar;
        }

        this.tileSign.ancientText[this.editLine] = s;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, I18n.format("sign.edit"), this.width / 2, 40, 16777215);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) (this.width / 2), 0.0F, 50.0F);
        GlStateManager.scale(-93.75F, -93.75F, -93.75F);
        EnumFacing facing = tileSign.getWorld().getBlockState(tileSign.getPos()).getValue(JSGProps.FACING_HORIZONTAL);
        float rotation = FacingToRotation.getIntRotation(facing, false);

        GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0F, -1.0625F, 0.0F);

        if (this.updateCounter / 6 % 2 == 0) {
            this.tileSign.lineBeingEdited = this.editLine;
        }

        TileEntityRendererDispatcher.instance.render(this.tileSign, -0.5D, -0.75D, -0.5D, 0.0F);
        this.tileSign.lineBeingEdited = -1;
        GlStateManager.popMatrix();
    }
}
