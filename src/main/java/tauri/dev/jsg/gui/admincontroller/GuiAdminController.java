package tauri.dev.jsg.gui.admincontroller;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;
import tauri.dev.jsg.gui.element.ArrowButton;
import tauri.dev.jsg.gui.element.GuiHelper;
import tauri.dev.jsg.gui.mainmenu.GuiCustomMainMenu;
import tauri.dev.jsg.renderer.stargate.StargateAbstractRenderer;
import tauri.dev.jsg.renderer.stargate.StargateAbstractRendererState;
import tauri.dev.jsg.stargate.network.StargateNetwork;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.util.JSGTextureLightningHelper;

import java.io.IOException;

public class GuiAdminController extends GuiScreen {

    public final StargateClassicBaseTile gateTile;
    public final World world;
    public final EntityPlayer player;

    public static StargateNetwork lastStargateNetwork = null;

    public final AddressesSection addressesSection;

    public int mouseX;
    public int mouseY;
    public float partialTicks;
    public int[] center = new int[]{0, 0};
    public int[] gateCenter = center;
    public float[] gateRotation = new float[]{0, 0};

    public int guiLeft;
    public int guiTop;
    public int guiRight;
    public int guiBottom;

    public GuiAdminController(EntityPlayer player, World world, int x, int y, int z) {
        this.world = world;
        this.player = player;
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (te instanceof StargateClassicBaseTile) {
            gateTile = (StargateClassicBaseTile) te;
        } else gateTile = null;
        addressesSection = new AddressesSection(this);
        addressesSection.generateAddressEntries();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public int[] getCenterPos(int rectWidth, int rectHeight) {
        return GuiCustomMainMenu.getCenterPos(rectWidth, rectHeight, width, height);
    }

    @SuppressWarnings("all")
    public void renderStargate() {
        // Get and check renderer state
        StargateAbstractRendererState rendererState = gateTile.getRendererStateClient();
        if (rendererState == null) return;

        // Get and check renderer
        TileEntitySpecialRenderer<StargateClassicBaseTile> renderer = (TileEntitySpecialRenderer<StargateClassicBaseTile>) TileEntityRendererDispatcher.instance.renderers.get(gateTile.getClass());
        StargateAbstractRenderer gateRenderer = null;
        if (renderer == null) return;

        if (renderer instanceof StargateAbstractRenderer)
            gateRenderer = (StargateAbstractRenderer) renderer;
        else
            return;

        // ---------------------
        // RENDER THE GATE

        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1, 1, 1);

        GlStateManager.translate(gateCenter[0], gateCenter[1], 60);
        GlStateManager.scale(-20, -20, -20);

        GlStateManager.pushMatrix();
        GlStateManager.rotate(180, 0, 1, 0);

        GlStateManager.pushMatrix();
        GlStateManager.rotate(gateRotation[0], 0, 1, 0);
        GlStateManager.rotate(gateRotation[1], 1, 0, 0);

        JSGTextureLightningHelper.disable();
        gateRenderer.renderWholeGate(gateTile, partialTicks, rendererState);
        JSGTextureLightningHelper.enable();
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.color(1, 1, 1, 1);

        // ---------------------
    }

    @Override
    public void initGui() {
        super.initGui();
        center = getCenterPos(0, 0);
        gateCenter = center; //new int[]{(int) (center[0] / 2 * 2.5f), (int) (center[1] / 2 * 1.5f)};

        guiLeft = OFFSET; //(int) (center[0] / 2 * 0.5);
        guiTop = OFFSET; //(int) (center[1] / 2 * 0.5);
        guiRight = width - OFFSET; //(int) (center[0] / 2 * 3.5);
        guiBottom = height - OFFSET; //(int) (center[1] / 2 * 3.5);

        addressesSection.guiLeft = guiLeft + OFFSET;
        addressesSection.guiTop = guiTop + OFFSET;
        addressesSection.width = 145 + (AddressesSection.OFFSET * 2);
        addressesSection.height = guiBottom - OFFSET - addressesSection.guiTop - 30; // -30 because of abort, iris, and close buttons
        addressesSection.generateAddressEntriesBoxes();
    }

    public static final int OFFSET = 15;

    public void calculateGateRotation() {
        /*..float difX = (float) (gateCenter[0] - mouseX) / (gateCenter[0] != 0 ? gateCenter[0] : 1);
        float difY = (float) (gateCenter[1] - mouseY) / (gateCenter[1] != 0 ? gateCenter[1] : 1);
        gateRotation[0] = -difX * 30;
        gateRotation[1] = -difY * 30;*/
    }

    @Override
    public void drawBackground(int tint) {
        drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        drawGradientRect(guiLeft, guiTop, guiRight - guiLeft, guiBottom - guiTop, 0x8B8B8B, 0x8B8B8B);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (gateTile == null) return;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.partialTicks = partialTicks;
        drawBackground(0);
        calculateGateRotation();

        addressesSection.renderEntries();

        GlStateManager.pushMatrix();
        GlStateManager.enableDepth();
        GlStateManager.translate(0, 0, 90);
        addressesSection.renderFg();
        GlStateManager.popMatrix();

        // Should be last
        renderStargate();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        for (GuiTextField field : addressesSection.entriesTextFields) {
            field.textboxKeyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (GuiTextField field : addressesSection.entriesTextFields) {
            field.mouseClicked(mouseX, mouseY, mouseButton);
        }
        for (ArrowButton button : addressesSection.dialButtons) {
            if (GuiHelper.isPointInRegion(button.x, button.y, button.width, button.height, mouseX, mouseY)) {
                button.performAction();
                button.playPressSound(Minecraft.getMinecraft().getSoundHandler());
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (!GuiHelper.isPointInRegion(addressesSection.guiLeft, addressesSection.guiTop, addressesSection.width, addressesSection.height, mouseX, mouseY))
            return;

        if (wheel != 0) {
            addressesSection.scroll(wheel);
        }
    }

    public void drawGradientRect(int left, int top, int right, int bottom, int starColor, int endColor) {
        super.drawGradientRect(left, top, right, bottom, starColor, endColor);
    }

    public void renderControlButtons(){

    }
}
