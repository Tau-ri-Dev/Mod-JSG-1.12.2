package tauri.dev.jsg.gui.admincontroller;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.gui.JSGTexturedGui;
import tauri.dev.jsg.gui.element.ArrowButton;
import tauri.dev.jsg.gui.element.GuiHelper;
import tauri.dev.jsg.gui.mainmenu.GuiCustomMainMenu;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.loader.texture.TextureLoader;
import tauri.dev.jsg.renderer.stargate.StargateAbstractRenderer;
import tauri.dev.jsg.renderer.stargate.StargateAbstractRendererState;
import tauri.dev.jsg.stargate.network.StargateAddressDynamic;
import tauri.dev.jsg.stargate.network.StargateNetwork;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.util.JSGTextureLightningHelper;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;

public class GuiAdminController extends JSGTexturedGui {

    public final StargateClassicBaseTile gateTile;
    public final World world;
    public final BlockPos pos;
    public final EntityPlayer player;

    public final StargateNetwork stargateNetwork;

    public final AddressesSection addressesSection;

    public int mouseX;
    public int mouseY;
    public float partialTicks;
    public int[] center = new int[]{0, 0};
    public int[] gateCenter = center;
    public float[] gateRotation = new float[]{0, 0};
    public int guiRight;
    public int guiBottom;

    public GuiAdminController(EntityPlayer player, World world, BlockPos pos, StargateNetwork stargateNetwork) {
        super(512, 256, 512, 256);
        this.world = world;
        this.player = player;
        this.pos = pos;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof StargateClassicBaseTile) {
            gateTile = (StargateClassicBaseTile) te;
        } else gateTile = null;
        this.addressesSection = new AddressesSection(this);
        this.stargateNetwork = stargateNetwork;

        regenerateStargate();
    }

    public void regenerateStargate() {
        try {
            imaginaryGateTile = Objects.requireNonNull(gateTile).getClass().newInstance();
            imaginaryGateTile.setWorld(world);
            imaginaryGateTile.setPos(pos);

            ItemStack stack = Minecraft.getMinecraft().player.getHeldItem(getHand());
            if (stack.getItem() != JSGItems.ADMIN_CONTROLLER) return;
            NBTTagCompound compound = stack.getTagCompound();
            if (compound == null) return;
            if (!compound.hasKey("gateNBT")) return;
            imaginaryGateTile.readFromNBT(compound.getCompoundTag("gateNBT"));
        } catch (Exception e) {
            JSG.error(e);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public int[] getCenterPos(int rectWidth, int rectHeight) {
        return GuiCustomMainMenu.getCenterPos(rectWidth, rectHeight, xSize, ySize);
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
        GlStateManager.enableAlpha();
        GlStateManager.color(1, 1, 1);

        GlStateManager.translate(gateCenter[0], gateCenter[1], 0);
        GlStateManager.scale(-17, -17, -17);

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
    public ResourceLocation getBackground() {
        return new ResourceLocation(JSG.MOD_ID, "textures/gui/gui_admin_controller.png");
    }

    @Override
    public void initGui() {
        super.initGui();
        center = getCenterPos(0, 0);
        gateCenter = center;

        guiRight = xSize;
        guiBottom = ySize;

        addressesSection.guiLeft = OFFSET;
        addressesSection.guiTop = OFFSET;
        addressesSection.width = 145 + (AddressesSection.OFFSET * 2);
        addressesSection.height = ySize - OFFSET * 2;
        addressesSection.generateAddressEntriesBoxes(true);
    }

    public static final int OFFSET = 15;

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks) {
        if (gateTile == null) return;
        this.mouseX = mouseX - guiLeft;
        this.mouseY = mouseY - guiTop;
        this.partialTicks = partialTicks;

        // render background
        addressesSection.generateAddressEntries();
        addressesSection.generateAddressEntriesBoxes(false);
        addressesSection.renderEntries();

        renderControlButtons();
        renderGateInfo();

        // Render foreground
        GlStateManager.enableDepth();
        GlStateManager.translate(0, 0, 180);
        addressesSection.renderFg();
        GlStateManager.translate(0, 0, -180);

        // render gate model
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
            field.mouseClicked(this.mouseX, this.mouseY, mouseButton);
        }
        for (ArrowButton button : addressesSection.dialButtons) {
            if (GuiHelper.isPointInRegion(button.x, button.y, button.width, button.height, this.mouseX, this.mouseY)) {
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

    public void renderControlButtons() {

    }

    @Nonnull
    public EnumHand getHand() {
        EnumHand hand = EnumHand.MAIN_HAND;
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() != JSGItems.ADMIN_CONTROLLER) {
            hand = EnumHand.OFF_HAND;
            stack = player.getHeldItem(hand);
            if (stack.getItem() != JSGItems.ADMIN_CONTROLLER) return EnumHand.MAIN_HAND;
        }
        return hand;
    }

    public StargateClassicBaseTile imaginaryGateTile = null;

    public void renderGateInfo() {
        regenerateStargate();
        if (imaginaryGateTile == null) return;

        // Render dialed address
        StargateAddressDynamic dialedAddress = imaginaryGateTile.getDialedAddress();
        int originId = imaginaryGateTile.getOriginId();
        boolean isUniverse = dialedAddress.getSymbolType() == SymbolTypeEnum.UNIVERSE;
        int height = (ySize - 2 * OFFSET - 8 * 3) / 9;
        int width = (isUniverse ? height / 2 : height);
        int x = guiRight - height - OFFSET;
        if (isUniverse)
            x += width / 2;
        for (int i = 0; i < dialedAddress.size(); i++) {
            int y = OFFSET + (i * (height + 3));
            SymbolInterface symbol = dialedAddress.get(i);
            renderSymbol(x, y, width, height, symbol, originId);
        }
        // ----------------------
    }

    public static void renderSymbol(int x, int y, int w, int h, SymbolInterface symbol, int originId) {
        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_ADD);

        GlStateManager.color(0f, 0f, 0f, 1);

        if (symbol.renderIconByMinecraft(originId))
            Minecraft.getMinecraft().getTextureManager().bindTexture(symbol.getIconResource(originId));
        else
            TextureLoader.getTexture(symbol.getIconResource(originId)).bindTexture();
        drawScaledCustomSizeModalRect(x, y, 0, 0, 256, 256, w, h, 256, 256);

        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);

        GlStateManager.popMatrix();
    }
}
