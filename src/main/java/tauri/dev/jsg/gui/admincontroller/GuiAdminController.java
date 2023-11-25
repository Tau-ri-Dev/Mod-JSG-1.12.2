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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.gui.JSGTexturedGui;
import tauri.dev.jsg.gui.base.JSGButton;
import tauri.dev.jsg.gui.element.GuiHelper;
import tauri.dev.jsg.gui.element.ModeButton;
import tauri.dev.jsg.gui.mainmenu.GuiCustomMainMenu;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.loader.texture.TextureLoader;
import tauri.dev.jsg.renderer.stargate.StargateAbstractRenderer;
import tauri.dev.jsg.renderer.stargate.StargateAbstractRendererState;
import tauri.dev.jsg.stargate.EnumStargateState;
import tauri.dev.jsg.stargate.network.StargateAddressDynamic;
import tauri.dev.jsg.stargate.network.StargateNetwork;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.util.JSGMinecraftHelper;
import tauri.dev.jsg.util.JSGTextureLightningHelper;
import tauri.dev.jsg.util.math.TemperatureHelper;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

@SideOnly(Side.CLIENT)
public class GuiAdminController extends JSGTexturedGui {

    public final StargateClassicBaseTile gateTile;
    public final World world;
    public final BlockPos pos;
    public final EntityPlayer player;

    public StargateNetwork stargateNetwork;

    public final AddressesSection addressesSection;

    public int mouseX;
    public int mouseY;
    public float partialTicks;
    public int[] center = new int[]{0, 0};
    public int[] gateCenter = center;
    public float[] gateRotation = new float[]{0, 0};
    public int guiRight;
    public int guiBottom;

    public Notifier notifer = new Notifier();

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
            if (lastDialedAddressLength == -1)
                lastDialedAddressLength = imaginaryGateTile.getDialedAddress().size();

            if (!compound.hasKey("sgNetwork")) return;
            stargateNetwork = new StargateNetwork();
            stargateNetwork.deserializeNBT(compound.getCompoundTag("sgNetwork"));
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

        if (renderer instanceof StargateAbstractRenderer) gateRenderer = (StargateAbstractRenderer) renderer;
        else return;

        // ---------------------
        // RENDER THE GATE

        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.color(1, 1, 1);

        GlStateManager.translate(gateCenter[0], gateCenter[1], 1);
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
        return new ResourceLocation(JSG.MOD_ID, "textures/gui/admin_controller/gui_admin_controller.png");
    }

    @Override
    public void initGui() {
        super.initGui();
        center = getCenterPos(0, 0);
        gateCenter = new int[]{center[0], center[1] - OFFSET / 2};

        guiRight = xSize;
        guiBottom = ySize;

        addressesSection.guiLeft = OFFSET;
        addressesSection.guiTop = OFFSET;
        addressesSection.width = 145 + (AddressesSection.OFFSET * 2);
        addressesSection.height = ySize - OFFSET * 2;
        addressesSection.init(true);
    }

    public static final int OFFSET = 15;

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks) {
        if (gateTile == null) return;
        this.mouseX = mouseX - guiLeft;
        this.mouseY = mouseY - guiTop;
        this.partialTicks = partialTicks;

        renderDialedAddressBoxes();

        // render background
        addressesSection.generateAddressEntries();
        addressesSection.renderEntries();

        renderControlButtons();
        renderGateInfo();

        notifer.render(this, 117, 4);

        // Render actual foreground
        GlStateManager.pushMatrix();
        GlStateManager.enableDepth();
        GlStateManager.pushMatrix();
        // pushed to z 180 because of the gate
        GlStateManager.translate(0, 0, 180);

        renderDialedAddress();
        addressesSection.renderFg();

        GlStateManager.popMatrix();

        // render gate model
        // Should be last
        renderStargate();
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        for (GuiTextField field : addressesSection.entriesTextFields) {
            field.textboxKeyTyped(typedChar, keyCode);
        }
        addressesSection.searchField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (GuiTextField field : addressesSection.entriesTextFields) {
            field.mouseClicked(this.mouseX, this.mouseY, mouseButton);
        }
        addressesSection.searchField.mouseClicked(this.mouseX, this.mouseY, mouseButton);
        for (ModeButton button : addressesSection.dialButtons) {
            button.mouseClickedPerformAction(this.mouseX, this.mouseY, mouseButton);
        }
        for (JSGButton button : addressesSection.optionButtons) {
            if (button.enabled && GuiHelper.isPointInRegion(button.x, button.y, button.width, button.height, this.mouseX, this.mouseY)) {
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

    public int lastDialedAddressLength = -1;
    public long symbolEngagingAnimationStart = -1;
    public static final int SYMBOL_ANIMATION_LENGTH = 40;

    public void renderDialedAddress() {
        if (imaginaryGateTile == null) return;
        if (imaginaryGateTile.getStargateState().notInitiating()) return;

        StargateAddressDynamic dialedAddress = imaginaryGateTile.getDialedAddress();
        int originId = imaginaryGateTile.getOriginId();
        boolean isUniverse = dialedAddress.getSymbolType() == SymbolTypeEnum.UNIVERSE;
        int height = (ySize - 2 * OFFSET - 8 * 3) / 9;
        int width = (isUniverse ? height / 2 : height);
        int x = guiRight - OFFSET - 20 - width/2;
        for (int i = 0; i < (dialedAddress.size() - 1); i++) {
            int y = OFFSET + (i * (height + 3));
            SymbolInterface symbol = dialedAddress.get(i);
            renderSymbol(x, y, width, height, symbol, originId);
        }

        // Render last symbol (animate engaging)
        int i = (dialedAddress.size() - 1);
        if (i >= 0) {
            int targetY = OFFSET + (i * (height + 3));

            float symbolSize;

            int currentX = x;
            int currentY = targetY;

            final long currentTick = JSGMinecraftHelper.getPlayerTickClientSide();
            if (lastDialedAddressLength != dialedAddress.size()) {
                lastDialedAddressLength = dialedAddress.size();
                symbolEngagingAnimationStart = currentTick;
            }

            final float MAX_SCALE = 3f;

            if (symbolEngagingAnimationStart != -1) {
                final double stage = ((double) (currentTick - symbolEngagingAnimationStart) / SYMBOL_ANIMATION_LENGTH);
                if (stage > 1) {
                    symbolEngagingAnimationStart = -1;
                } else {

                    if (stage <= 0.25) {
                        symbolSize = (float) ((stage / 0.25) * MAX_SCALE);
                    } else if (stage <= 0.5) {
                        symbolSize = MAX_SCALE;
                    } else {
                        symbolSize = MAX_SCALE - (float) (((stage - 0.5) / 0.5) * (MAX_SCALE - 1.f));
                    }

                    int heightMax = (int) (height * MAX_SCALE);
                    int widthMax = (isUniverse ? heightMax / 2 : heightMax);

                    height *= symbolSize;
                    width = (isUniverse ? height / 2 : height);

                    if (stage <= 0.5) {
                        currentX = gateCenter[0] - (width / 2);
                        currentY = gateCenter[1] - (height / 2);
                    } else {
                        int minX = gateCenter[0] - (widthMax / 2);
                        int minY = gateCenter[1] - (heightMax / 2);

                        currentX = (int) (minX + (x - minX) * ((stage - 0.5) / 0.5));
                        currentY = (int) (minY + (targetY - minY) * ((stage - 0.5) / 0.5));
                    }
                }
            }

            SymbolInterface symbol = dialedAddress.get(i);
            renderSymbol(currentX, currentY, width, height, symbol, originId);
        }
    }

    public void renderDialedAddressBoxes() {
        if (imaginaryGateTile == null) return;
        StargateAddressDynamic dialedAddress = imaginaryGateTile.getDialedAddress();
        for (int i = 0; i < (dialedAddress.size() - 1); i++) {
            // Symbol in place - render box
            renderSymbolBox(i, false);
        }
        // Render last symbol (animate engaging)
        int i = (dialedAddress.size() - 1);
        if (i >= 0) {
            if(symbolEngagingAnimationStart == -1){
                // Symbol in place - render box
                renderSymbolBox(i, imaginaryGateTile.isFinalActive());
            }
        }
    }

    public void renderSymbolBox(int symbolIndex, boolean isFinal){
        if(symbolIndex < 0) symbolIndex = 0;
        if(symbolIndex > 8) symbolIndex = 8;
        String symbolBox = (symbolIndex + "_" + (symbolIndex == 8 || (symbolIndex >= 6 && isFinal) ? "1" : "0"));
        ResourceLocation textureLocation = new ResourceLocation(JSG.MOD_ID, "textures/gui/admin_controller/symbol_box_" + symbolBox + ".png");
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();

        EnumStargateState sgState = imaginaryGateTile.getStargateState();

        if(sgState.incoming() || sgState.notInitiating() || sgState.unstable()){
            GlStateManager.color(222f/255f, 139f/255f, 15f/255f);
        }
        else if(sgState.failing()){
            GlStateManager.color(222f/255f, 22f/255f, 15f/255f);
        }
        else if(sgState.engaged()){
            GlStateManager.color(20f/255f, 195f/255f, 14f/255f);
        }
        else{
            GlStateManager.color(8f/255f, 141f/255f, 218f/255f);
        }

        mc.getTextureManager().bindTexture(textureLocation);
        drawModalRectWithCustomSizedTexture(0, 0, 0, 0, xSize, ySize, texW, texH);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public void renderGateInfo() {
        regenerateStargate();
        if (imaginaryGateTile == null) return;

        String[] s = new String[]{
                "Gate temp: " + JSGConfig.General.visual.temperatureUnit.getTemperatureToDisplay(TemperatureHelper.asKelvins(TemperatureHelper.asCelsius(imaginaryGateTile.gateHeat).toKelvins()), 0),
                "Iris temp: " + JSGConfig.General.visual.temperatureUnit.getTemperatureToDisplay(TemperatureHelper.asKelvins(TemperatureHelper.asCelsius(imaginaryGateTile.irisHeat).toKelvins()), 0),
                "Time opened: " + imaginaryGateTile.getOpenedSecondsToDisplayAsMinutes(),
                "Energy: " + String.format("%.0f", (float) imaginaryGateTile.getEnergyStored()) + "RF",
                "Seconds to close: " + imaginaryGateTile.getEnergySecondsToClose(),
                "Installed capacitors: " + (imaginaryGateTile.getPowerTier() - 1),
                "Gate state: " + imaginaryGateTile.getStargateState()
        };

        int y = guiBottom - OFFSET - 10 * s.length - 23;
        for (String line : s) {
            int width = fontRenderer.getStringWidth(line);
            int lineX = guiRight - OFFSET - width - 41;
            fontRenderer.drawString(line, lineX, y, 0xffffff, true);
            y += 10;
        }
    }

    public void renderSymbol(int x, int y, int w, int h, SymbolInterface symbol, int originId) {
        if(symbol == null) return;
        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.color(0f, 0f, 0f, 1);

        if (symbol.renderIconByMinecraft(originId))
            Minecraft.getMinecraft().getTextureManager().bindTexture(symbol.getIconResource(originId));
        else TextureLoader.getTexture(symbol.getIconResource(originId)).bindTexture();
        drawScaledCustomSizeModalRect(x, y, 0, 0, 256, 256, w, h, 256, 256);

        if (GuiHelper.isPointInRegion(x, y, w, h, this.mouseX, this.mouseY)) {
            Util.drawHoveringText(Collections.singletonList(symbol.localize()), this.mouseX, this.mouseY, this.width, this.height, -1, fontRenderer);
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void onGuiClosed() {
        for (GuiTextField f : addressesSection.entriesTextFields) {
            f.setFocused(false);
        }
        super.onGuiClosed();
    }
}
