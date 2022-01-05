package mrjake.aunis.gui.mainmenu.screens;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.gui.base.AunisGuiButton;
import mrjake.aunis.gui.base.AunisGuiSlider;
import mrjake.aunis.gui.mainmenu.screens.serverlist.AunisServerListEntryLanDetected;
import mrjake.aunis.gui.mainmenu.screens.serverlist.AunisServerListEntryNormal;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.stargate.ChevronEnum;
import mrjake.aunis.sound.AunisSoundHelperClient;
import mrjake.aunis.sound.SoundPositionedEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.ServerPinger;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AunisMultiPlayerGui extends GuiMultiplayer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ServerPinger oldServerPinger = new ServerPinger();
    private ServerSelectionList serverListSelector;
    private ServerList savedServerList;
    private GuiButton btnEditServer;
    private GuiButton btnSelectServer;
    private GuiButton btnDeleteServer;
    private boolean deletingServer;
    private boolean addingServer;
    private boolean editingServer;
    private boolean directConnect;
    private String hoveringText;
    private ServerData selectedServer;
    private LanServerDetector.LanServerList lanServerList;
    private LanServerDetector.ThreadLanServerFind lanServerDetector;
    private boolean initialized;
    protected int eventButton;
    protected int touchValue;
    protected long lastMouseEvent;

    public AunisMultiPlayerGui(GuiScreen parentScreen, BiomeOverlayEnum overlay) {
        super(parentScreen);
        net.minecraftforge.fml.client.FMLClientHandler.instance().setupServerList();
        this.lastScreen = parentScreen;
        this.overlay = overlay;
    }

    @SubscribeEvent
    public static void onSounds(PlaySoundEvent event) {
        event.setResultSound(null);
    }

    // ------------------------------------------
    // DEFINE VARIABLES

    protected static float animationStage = 0;
    protected final float ringAnimationStepSetting = 0.3f;
    protected float ringAnimationStep = 0.0f;
    protected float ringAnimationSpeed = 0.0f;
    protected final boolean speedUpGate = true;

    protected BiomeOverlayEnum overlay;
    protected float screenCenterHeight = (((float) height) / 2f);
    protected float screenCenterWidth = ((float) width) / 2f;
    protected List<GuiButton> aunisButtonList = new ArrayList<>();
    protected List<GuiButton> aunisButtonSliders = new ArrayList<>();
    protected static final ResourceLocation BACKGROUND_TEXTURE = AunisConfig.mainMenuConfig.disableAunisMainMenu ? null : new ResourceLocation(Aunis.ModID, "textures/gui/mainmenu/background.jpg");

    // render kawoosh and event horizon
    protected float kawooshState = 0f;
    protected float gateZoom = 0.0f;
    protected float gatePos = 0.0f;

    // options vars
    private static final GameSettings.Options[] SCREEN_OPTIONS = new GameSettings.Options[] {GameSettings.Options.FOV};
    private final GuiScreen lastScreen;
    protected boolean isUnloading = false;

    /**
     * ------------------------------------------
     * showVersionAlert indexes:
     * <p>
     * 0 -> Version is good
     * 1 -> Alert is open
     * 2 -> Alert closed by client
     * <p>
     * ------------------------------------------
     */
    // ------------------------------------------

    // slow down and speed up gate ring
    public void updateRingSpeed(){
        if(!AunisConfig.mainMenuConfig.gateRotation) return;
        if (!speedUpGate) {
            if (ringAnimationSpeed > 0.0f) ringAnimationSpeed -= 0.05f;
        } else {
            if (ringAnimationSpeed < 1.0f) ringAnimationSpeed += 0.05f;
        }
        ringAnimationStep = ringAnimationStepSetting * ringAnimationSpeed;
        animationStage += ringAnimationStep;
    }

    // play sound
    public void updateSound() {
        AunisSoundHelperClient.playPositionedSoundClientSide(new BlockPos(1, 0, 0), SoundPositionedEnum.MAINMENU_MUSIC, AunisConfig.mainMenuConfig.playMusic);
        AunisSoundHelperClient.playPositionedSoundClientSide(new BlockPos(0, 0, 1), SoundPositionedEnum.MAINMENU_RING_ROLL,
                (AunisConfig.mainMenuConfig.gateRotation && kawooshState == 0 && ringAnimationSpeed > 0.0f));
    }

    // update ring rotation and overlay
    public void updateAnimation() {
        this.screenCenterHeight = (((float) height) / 2f);
        this.screenCenterWidth = ((float) width) / 2f;

        if(gateZoom == 0.0f) gateZoom = ((((float) height) / 10f) - 3);
        if(gatePos == 0.0f) gatePos = ((float) width) / 2f;

        float step = 8f;
        if(!isUnloading) {
            if (this.gatePos < (this.width - 54f)) this.gatePos += step * 4f;
            if (this.gateZoom < (this.width / 6f)) this.gateZoom += step;

            if (this.gatePos > (this.width - 57f)) this.gatePos -= step * 4f;
            if (this.gateZoom > ((this.width / 6f) + 5f)) this.gateZoom -= step;
        }
        else{
            if (this.gatePos > 0.0f) this.gatePos -= step * 4f;
            if (this.gateZoom > 0.0f) this.gateZoom -= step;

            if (this.gatePos+((step*4) + 25) <= ((float) width) / 2f) this.mc.displayGuiScreen(this.lastScreen);
        }
        if (animationStage > 360) animationStage = 0f;
        updateRingSpeed();
    }

    // RENDER MAIN MENU
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // ------------------------------
        // ANIMATIONS AND SOUNDS

        updateAnimation();
        updateSound();

        // ------------------------------
        // DRAWING BACKGROUND

        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        this.mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawScaledCustomSizeModalRect(0, 0, 0, 0, width, height, width, height, width, height);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.popMatrix();

        // ------------------------------
        // DRAWING WHOLE GATE MODEL

        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.translate(this.gatePos, screenCenterHeight, 0f);
        GlStateManager.scale(this.gateZoom, this.gateZoom, this.gateZoom);
        GlStateManager.rotate(-180f, 0f, 0f, 1f);
        GlStateManager.rotate(180f, 0f, 1f, 0f);

        // ------------------------------
        // DRAWING GATE

        GlStateManager.pushMatrix();
        ElementEnum.MILKYWAY_GATE_MAINMENU.bindTextureAndRender(this.overlay);
        GlStateManager.popMatrix();

        // ------------------------------
        // DRAWING RING

        GlStateManager.pushMatrix();
        GlStateManager.translate(0f, 0f, -3f);
        // ring rotation animation
        GlStateManager.rotate(animationStage, 0, 0, 1);
        // -----
        ElementEnum.MILKYWAY_RING_MAINMENU.bindTextureAndRender(this.overlay);
        GlStateManager.popMatrix();

        // ------------------------------
        // DRAWING CHEVRONS

        for (int i = 0; i < 9; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 0.03f);
            GlStateManager.rotate(ChevronEnum.valueOf(i).rotation, 0, 0, 1);
            ElementEnum.MILKYWAY_CHEVRON_FRAME_MAINMENU.bindTextureAndRender(this.overlay);
            GlStateManager.translate(0, 0, 0);
            ElementEnum.MILKYWAY_CHEVRON_LIGHT_MAINMENU.bindTextureAndRender(this.overlay);
            GlStateManager.translate(0, 0, 0);
            ElementEnum.MILKYWAY_CHEVRON_MOVING_MAINMENU.bindTextureAndRender(this.overlay);
            GlStateManager.popMatrix();
        }
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableTexture2D();
        GlStateManager.popMatrix();

        // ------------------------------
        // DRAWING BUTTONS

        if(!isUnloading) {

            GlStateManager.enableTexture2D();
            this.hoveringText = null;
            this.serverListSelector.drawScreen(mouseX, mouseY, partialTicks);
            this.drawCenteredString(this.fontRenderer, I18n.format("multiplayer.title"), this.width / 2, 20, 16777215);
            GlStateManager.pushMatrix();
            for (GuiButton guiButton : this.aunisButtonList) {
                ((AunisGuiButton) guiButton).drawButton(this.mc, mouseX, mouseY, partialTicks);
            }

            for (GuiButton guiButton : this.aunisButtonSliders) {
                ((AunisGuiSlider) guiButton).drawButton(this.mc, mouseX, mouseY, partialTicks);
            }

            for (GuiLabel guiLabel : this.labelList) {
                guiLabel.drawLabel(this.mc, mouseX, mouseY);
            }

            btnEditServer.drawButton(this.mc, mouseX, mouseY, partialTicks);
            btnDeleteServer.drawButton(this.mc, mouseX, mouseY, partialTicks);
            btnSelectServer.drawButton(this.mc, mouseX, mouseY, partialTicks);

            GlStateManager.popMatrix();
            if (this.hoveringText != null)
            {
                this.drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.hoveringText)), mouseX, mouseY);
            }
        }
    }

    protected void frame(int x, int y, int w, int h, int thickness, int borderColor, int background) {
        // Up
        drawRect(x, y, x + w, y + thickness, borderColor);
        // Down
        drawRect(x, y + h - thickness, x + w, y + h, borderColor);
        // Left
        drawRect(x, y + thickness, x + thickness, y + h - thickness, borderColor);
        // Right
        drawRect(x + w - thickness, y + thickness, x + w, y + h - thickness, borderColor);
        // Background
        drawRect(x + thickness, y + thickness, x + w - thickness, y + h - thickness, background);
    }

    @Override
    public void initGui(){
        Keyboard.enableRepeatEvents(true);
        this.aunisButtonList.clear();

        if (this.initialized)
        {
            this.serverListSelector.setDimensions(this.width, this.height, 32, this.height - 64);
        }
        else
        {
            this.initialized = true;
            this.savedServerList = new ServerList(this.mc);
            this.savedServerList.loadServerList();
            this.lanServerList = new LanServerDetector.LanServerList();

            try
            {
                this.lanServerDetector = new LanServerDetector.ThreadLanServerFind(this.lanServerList);
                this.lanServerDetector.start();
            }
            catch (Exception exception)
            {
                LOGGER.warn("Unable to start LAN server detection: {}", (Object)exception.getMessage());
            }

            this.serverListSelector = new ServerSelectionList(this, this.mc, this.width, this.height, 32, this.height - 64, 36);
            this.serverListSelector.updateOnlineServers(this.savedServerList);
        }

        this.createButtons();
    }

    public void handleMouseInput() throws IOException
    {
        int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int k = Mouse.getEventButton();

        if (Mouse.getEventButtonState())
        {
            if (this.mc.gameSettings.touchscreen && this.touchValue++ > 0)
            {
                return;
            }

            this.eventButton = k;
            this.lastMouseEvent = Minecraft.getSystemTime();
            this.mouseClicked(i, j, this.eventButton);
        }
        else if (k != -1)
        {
            if (this.mc.gameSettings.touchscreen && --this.touchValue > 0)
            {
                return;
            }

            this.eventButton = -1;
            this.mouseReleased(i, j, k);
        }
        else if (this.eventButton != -1 && this.lastMouseEvent > 0L)
        {
            long l = Minecraft.getSystemTime() - this.lastMouseEvent;
            this.mouseClickMove(i, j, this.eventButton, l);
        }
        this.serverListSelector.handleMouseInput();
    }

    @Override
    public void createButtons(){
        aunisButtonList.clear();
        btnEditServer = new AunisGuiButton(7, this.width / 2 - 154, this.height - 28, 70, 20, I18n.format("selectServer.edit"));
        btnDeleteServer = new AunisGuiButton(2, this.width / 2 - 74, this.height - 28, 70, 20, I18n.format("selectServer.delete"));
        btnSelectServer = new AunisGuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("selectServer.select"));
        aunisButtonList.add(new AunisGuiButton(4, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("selectServer.direct")));
        aunisButtonList.add(new AunisGuiButton(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.format("selectServer.add")));
        aunisButtonList.add(new AunisGuiButton(8, this.width / 2 + 4, this.height - 28, 70, 20, I18n.format("selectServer.refresh")));
        aunisButtonList.add(new AunisGuiButton(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.format("gui.cancel")));
        selectServer(this.serverListSelector.getSelected());
    }

    public void updateScreen()
    {

        if (this.lanServerList.getWasUpdated())
        {
            List<LanServerInfo> list = this.lanServerList.getLanServers();
            this.lanServerList.setWasNotUpdated();
            this.serverListSelector.updateNetworkServers(list);
        }

        this.oldServerPinger.pingPendingNetworks();
    }

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);

        if (this.lanServerDetector != null)
        {
            this.lanServerDetector.interrupt();
            this.lanServerDetector = null;
        }

        this.oldServerPinger.clearPendingNetworks();
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            GuiListExtended.IGuiListEntry guilistextended$iguilistentry = this.serverListSelector.getSelected() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelected());

            if (button.id == 2 && guilistextended$iguilistentry instanceof AunisServerListEntryNormal)
            {
                String s4 = ((AunisServerListEntryNormal)guilistextended$iguilistentry).getServerData().serverName;

                if (s4 != null)
                {
                    this.deletingServer = true;
                    String s = I18n.format("selectServer.deleteQuestion");
                    String s1 = "'" + s4 + "' " + I18n.format("selectServer.deleteWarning");
                    String s2 = I18n.format("selectServer.deleteButton");
                    String s3 = I18n.format("gui.cancel");
                    GuiYesNo guiyesno = new GuiYesNo(this, s, s1, s2, s3, this.serverListSelector.getSelected());
                    this.mc.displayGuiScreen(guiyesno);
                }
            }
            else if (button.id == 1)
            {
                this.connectToSelected();
            }
            else if (button.id == 4)
            {
                this.directConnect = true;
                this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false);
                this.mc.displayGuiScreen(new GuiScreenServerList(this, this.selectedServer));
            }
            else if (button.id == 3)
            {
                this.addingServer = true;
                this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false);
                this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.selectedServer));
            }
            else if (button.id == 7 && guilistextended$iguilistentry instanceof AunisServerListEntryNormal)
            {
                this.editingServer = true;
                ServerData serverdata = ((AunisServerListEntryNormal)guilistextended$iguilistentry).getServerData();
                this.selectedServer = new ServerData(serverdata.serverName, serverdata.serverIP, false);
                this.selectedServer.copyFrom(serverdata);
                this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.selectedServer));
            }
            else if (button.id == 0)
            {
                isUnloading = true;
            }
            else if (button.id == 8)
            {
                this.refreshServerList();
            }
        }
    }

    private void refreshServerList()
    {
        this.mc.displayGuiScreen(new AunisMultiPlayerGui(lastScreen, overlay));
    }

    public void confirmClicked(boolean result, int id)
    {
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry = this.serverListSelector.getSelected() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelected());

        if (this.deletingServer)
        {
            this.deletingServer = false;

            if (result && guilistextended$iguilistentry instanceof AunisServerListEntryNormal)
            {
                this.savedServerList.removeServerData(this.serverListSelector.getSelected());
                this.savedServerList.saveServerList();
                this.serverListSelector.setSelectedSlotIndex(-1);
                this.serverListSelector.updateOnlineServers(this.savedServerList);
            }

            this.mc.displayGuiScreen(this);
        }
        else if (this.directConnect)
        {
            this.directConnect = false;

            if (result)
            {
                this.connectToServer(this.selectedServer);
            }
            else
            {
                this.mc.displayGuiScreen(this);
            }
        }
        else if (this.addingServer)
        {
            this.addingServer = false;

            if (result)
            {
                this.savedServerList.addServerData(this.selectedServer);
                this.savedServerList.saveServerList();
                this.serverListSelector.setSelectedSlotIndex(-1);
                this.serverListSelector.updateOnlineServers(this.savedServerList);
            }

            this.mc.displayGuiScreen(this);
        }
        else if (this.editingServer)
        {
            this.editingServer = false;

            if (result && guilistextended$iguilistentry instanceof AunisServerListEntryNormal)
            {
                ServerData serverdata = ((AunisServerListEntryNormal)guilistextended$iguilistentry).getServerData();
                serverdata.serverName = this.selectedServer.serverName;
                serverdata.serverIP = this.selectedServer.serverIP;
                serverdata.copyFrom(this.selectedServer);
                this.savedServerList.saveServerList();
                this.serverListSelector.updateOnlineServers(this.savedServerList);
            }

            this.mc.displayGuiScreen(this);
        }
    }

    @Override
    public void connectToSelected()
    {
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry = this.serverListSelector.getSelected() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelected());

        if (guilistextended$iguilistentry instanceof AunisServerListEntryNormal)
        {
            this.connectToServer(((AunisServerListEntryNormal)guilistextended$iguilistentry).getServerData());
        }
        else if (guilistextended$iguilistentry instanceof AunisServerListEntryLanDetected)
        {
            LanServerInfo lanserverinfo = ((AunisServerListEntryLanDetected)guilistextended$iguilistentry).getServerData();
            this.connectToServer(new ServerData(lanserverinfo.getServerMotd(), lanserverinfo.getServerIpPort(), true));
        }
    }

    private void connectToServer(ServerData server) {
        net.minecraftforge.fml.client.FMLClientHandler.instance().connectToServer(this, server);
    }

    @Override
    public void selectServer(int index)
    {
        this.serverListSelector.setSelectedSlotIndex(index);
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry = index < 0 ? null : this.serverListSelector.getListEntry(index);
        this.btnSelectServer.enabled = false;
        this.btnEditServer.enabled = false;
        this.btnDeleteServer.enabled = false;

        if (guilistextended$iguilistentry != null && !(guilistextended$iguilistentry instanceof ServerListEntryLanScan))
        {
            this.btnSelectServer.enabled = true;

            if (guilistextended$iguilistentry instanceof AunisServerListEntryNormal)
            {
                this.btnEditServer.enabled = true;
                this.btnDeleteServer.enabled = true;
            }
        }
    }

    public ServerPinger getOldServerPinger()
    {
        return this.oldServerPinger;
    }

    @Override
    public void setHoveringText(String p_146793_1_)
    {
        this.hoveringText = p_146793_1_;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (mouseButton == 0)
        {
            for (int i = 0; i < this.aunisButtonList.size(); ++i)
            {
                GuiButton guibutton = this.aunisButtonList.get(i);

                if (guibutton.mousePressed(this.mc, mouseX, mouseY))
                {
                    net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, this.aunisButtonList);
                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                        break;
                    guibutton = event.getButton();
                    this.selectedButton = guibutton;
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(guibutton);
                    if (this.equals(this.mc.currentScreen))
                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), this.aunisButtonList));
                }
            }
        }
        this.serverListSelector.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.selectedButton != null && state == 0)
        {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
        this.serverListSelector.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public ServerList getServerList()
    {
        return this.savedServerList;
    }

    @Override
    public boolean canMoveUp(ServerListEntryNormal p_175392_1_, int p_175392_2_)
    {
        return p_175392_2_ > 0;
    }

    @Override
    public boolean canMoveDown(ServerListEntryNormal p_175394_1_, int p_175394_2_)
    {
        return p_175394_2_ < this.savedServerList.countServers() - 1;
    }

    @Override
    public void moveServerUp(ServerListEntryNormal p_175391_1_, int p_175391_2_, boolean p_175391_3_)
    {
        int i = p_175391_3_ ? 0 : p_175391_2_ - 1;
        this.savedServerList.swapServers(p_175391_2_, i);

        if (this.serverListSelector.getSelected() == p_175391_2_)
        {
            this.selectServer(i);
        }

        this.serverListSelector.updateOnlineServers(this.savedServerList);
    }

    @Override
    public void moveServerDown(ServerListEntryNormal p_175393_1_, int p_175393_2_, boolean p_175393_3_)
    {
        int i = p_175393_3_ ? this.savedServerList.countServers() - 1 : p_175393_2_ + 1;
        this.savedServerList.swapServers(p_175393_2_, i);

        if (this.serverListSelector.getSelected() == p_175393_2_)
        {
            this.selectServer(i);
        }

        this.serverListSelector.updateOnlineServers(this.savedServerList);
    }
}
