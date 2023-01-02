package tauri.dev.jsg.gui.mainmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.gui.element.IconButton;
import tauri.dev.jsg.sound.JSGSoundHelperClient;
import tauri.dev.jsg.sound.SoundPositionedEnum;
import tauri.dev.jsg.util.GetUpdate;
import tauri.dev.jsg.util.JSGMinecraftHelper;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static tauri.dev.jsg.gui.element.GuiHelper.isPointInRegion;
import static tauri.dev.jsg.util.GetUpdate.DOWNLOAD_URL_USER;
import static tauri.dev.jsg.util.GetUpdate.openWebsiteToClient;

@SideOnly(Side.CLIENT)
public class GuiCustomMainMenu extends GuiScreen {

    public static final String WEBSITE = "https://justsgmod.eu/";
    public static final String GITHUB = "https://github.com/Tau-ri-Dev";
    public static final String MINECRAFT_SITES = "https://minecraft.net/en-us";
    public static final String JSG_RUNNING_TEXT = "Just Stargate Mod v" + JSG.MOD_VERSION.replaceAll(JSG.MC_VERSION + "-", "");

    public double tick;

    // Stores the larges number of FPS
    private static int bestFPS = 0;

    public void tick(){
        // If sync is enabled then sync local ticks with mc ticks
        if(JSGConfig.mainMenuConfig.syncEnabled) {
            tick = JSGMinecraftHelper.getClientTickPrecise();
        }
        else {
            int currentFPS = Minecraft.getDebugFPS();
            if(currentFPS > bestFPS) bestFPS = currentFPS;
            tick += (bestFPS > 0 ? ((30D / (double) bestFPS) * (20D / (double) bestFPS)) : 1D);
        }
    }

    public void createFadeIn() {
        backgroundChangeStart = (long) (tick - (BACKGROUND_CHANGE_ANIMATION_LENGTH / 3));
    }
    public GuiCustomMainMenu() {
        tick = JSGMinecraftHelper.getClientTickPrecise();
        createFadeIn();
    }

    public static final ArrayList<ResourceLocation> BACKGROUNDS = new ArrayList<ResourceLocation>() {{
        add(new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/background0.jpg"));
        add(new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/background1.jpg"));
        add(new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/background2.jpg"));
        add(new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/background3.jpg"));
        add(new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/background4.jpg"));
        add(new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/background5.jpg"));
        add(new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/background6.jpg"));
    }};

    public static final ResourceLocation LOGO_TAURI = new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/tauri_dev_logo.png");
    public static final ResourceLocation LOGO_MOJANG = new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/mojang_logo.png");
    public static final ResourceLocation LOGO_JSG = new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/jsg_logo.png");
    public static final ResourceLocation NOTIFICATION = new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/popup.png");

    public ResourceLocation getIconsTexture() {
        switch (gateType) {
            case MILKYWAY:
            default:
                return new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/icons_mw.png");
            case PEGASUS:
                return new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/icons_pg.png");
            case UNIVERSE:
                return new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/icons_uni.png");
        }
    }

    private static int currentButton = 0;

    private static final int BACKGROUND_CHANGE_ANIMATION_LENGTH = 60; //ticks
    private static final int BACKGROUND_STAY_TIME = 400; //ticks
    private long backgroundChangeStart = 0;

    public static GetUpdate.UpdateResult UPDATER_RESULT = GetUpdate.checkForUpdate();
    public static boolean updateChecked = false;

    public static final int BACKGROUNDS_COUNT = BACKGROUNDS.size();

    public static boolean isMusicPlaying = false;

    public static long menuDisplayed = -1;

    public static final int PADDING = 10;

    public static void playMusic(boolean play) {
        isMusicPlaying = play;
        JSGSoundHelperClient.playPositionedSoundClientSide(new BlockPos(0, 0, 0), SoundPositionedEnum.MAINMENU_MUSIC, play);
    }

    public void updateMusic() {
        isMusicPlaying = JSGSoundHelperClient.getRecord(SoundPositionedEnum.MAINMENU_MUSIC, new BlockPos(0, 0, 0)).isPlaying();
        if (!isMusicPlaying && (tick - menuDisplayed) > 20 * 2 && JSGConfig.mainMenuConfig.playMusic) { // wait 2 seconds before first play
            playMusic(false); // reset it
            playMusic(true);
        }
        if (!JSGConfig.mainMenuConfig.playMusic && isMusicPlaying)
            playMusic(false);
    }

    public int[] getCenterPos(int rectWidth, int rectHeight) {
        return new int[]{((width - rectWidth) / 2), ((height - rectHeight) / 2)};
    }

    public final ArrayList<GuiButton> updaterButtons = new ArrayList<>();

    /*--@Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();

        if (wheel != 0) {
            GuiButton firstBtn = buttonList.get(getPreviousButton(2));
            GuiButton lastBtn = buttonList.get(getNextButton(2));
            int x = firstBtn.x;
            int y = firstBtn.y;
            int width = firstBtn.width;
            int height = (lastBtn.x - firstBtn.x);
            if (isPointInRegion(x, y, width, height, Mouse.getEventX(), Mouse.getEventY())) {
                JSG.info("Lol: " + wheel);
                if (wheel < 0) currentButton = getNextButton(1);
                if (wheel > 0) currentButton = getPreviousButton(1);
            }
        }
    }*/

    @Override
    public void initGui() {
        if (JSGConfig.devConfig.enableDevMode) {
            UPDATER_RESULT = new GetUpdate.UpdateResult((JSGConfig.devConfig.t1 ? GetUpdate.EnumUpdateResult.ERROR : GetUpdate.EnumUpdateResult.NEWER_AVAILABLE), (JSGConfig.devConfig.t1 ? "Test error" : UPDATER_RESULT.response));
            updateChecked = false;
        }
        super.initGui();
        createFadeIn();
        buttonList.clear();
        labelList.clear();

        // buttons
        int id = -1;
        final int texSize = 128;
        buttonList.add(new IconButton(++id, 0, 0, getIconsTexture(), texSize, 32, 32, 32, 32, false, I18n.format("menu.singleplayer")));
        buttonList.add(new IconButton(++id, 0, 0, getIconsTexture(), texSize, 64, 32, 32, 32, false, I18n.format("menu.multiplayer")));
        buttonList.add(new IconButton(++id, 0, 0, getIconsTexture(), texSize, 32, 0, 32, 32, false, I18n.format("menu.options")));
        buttonList.add(new IconButton(++id, 0, 0, getIconsTexture(), texSize, 0, 32, 32, 32, false, I18n.format("menu.quit")));
        buttonList.add(new IconButton(++id, 0, 0, getIconsTexture(), texSize, 0, 0, 32, 32, false, I18n.format("menu.about")));
        buttonList.add(new IconButton(++id, 0, 0, getIconsTexture(), texSize, 64, 0, 32, 32, false, I18n.format("fml.menu.mods")));

        // ------------------------
        // Updater

        // Download (20)
        String update = I18n.format("menu.updater.download");
        int width = fontRenderer.getStringWidth(update) + 20;
        updaterButtons.add(new GuiButton(20, -width - (PADDING / 2), 0, width, 20, update));

        // Close (21)
        update = I18n.format("menu.updater.close");
        width = fontRenderer.getStringWidth(update) + 20;
        updaterButtons.add(new GuiButton(21, (PADDING / 2), 0, width, 20, update));
    }

    public int getButtonForDisplay(int offset) {
        if (offset > 0)
            return getNextButton(offset);
        if (offset < 0)
            return getPreviousButton(offset * -1);
        return currentButton;
    }

    private int getNextButton(int offset) {
        return (currentButton + offset) % buttonList.size();
    }

    private int getPreviousButton(int offset) {
        int id = currentButton;
        for (int i = 0; i < offset; i++) {
            id--;
            if (id < 0)
                id = (buttonList.size() - 1);
        }
        return id;
    }

    public EnumMainMenuGateType gateType = EnumMainMenuGateType.random();

    private long lastGateChange = 0;

    public void updateGateType() {
        if (JSGConfig.mainMenuConfig.enableGateChanging && (tick - lastGateChange) > 30 * 20) {
            // Every 30 seconds
            lastGateChange = (long) tick;
            gateType = EnumMainMenuGateType.random();
        }
    }

    public boolean updateNotificationRendered = false;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        tick();
        if (menuDisplayed == -1) {
            menuDisplayed = (long) tick;
            createFadeIn();
        }
        updateNotificationRendered = ((UPDATER_RESULT.result == GetUpdate.EnumUpdateResult.NEWER_AVAILABLE || UPDATER_RESULT.result == GetUpdate.EnumUpdateResult.ERROR) && !updateChecked);
        updateMusic();
        updateGateType();
        drawBackground();
        drawButtons(mouseX, mouseY);
        drawTitles();
        drawFg(mouseX, mouseY);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            for (GuiButton guibutton : buttonList) {
                if (guibutton.mousePressed(mc, mouseX, mouseY)) {
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    actionPerformed(guibutton);
                }
            }
            for (GuiButton guibutton : updaterButtons) {
                if (guibutton.mousePressed(mc, mouseX, mouseY)) {
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    actionPerformed(guibutton);
                }
            }

            // Clickable images/texts
            int sizeXTauri = width / 10;
            int sizeYTauri = (230 * sizeXTauri) / 411;
            int sizeYMojang = (52 * sizeXTauri) / 300;
            if(isPointInRegion(PADDING, height - PADDING - sizeYTauri - sizeYMojang - 8, sizeXTauri, sizeYTauri, mouseX, mouseY)){
                openWebsiteToClient(GITHUB);
            }
            if(isPointInRegion(PADDING, height - PADDING - sizeYMojang, sizeXTauri, sizeYMojang, mouseX, mouseY)){
                openWebsiteToClient(MINECRAFT_SITES);
            }

            // JSG text
            int jsgSizeX = fontRenderer.getStringWidth(JSG_RUNNING_TEXT);
            int jsgSizeY = 10;

            // JSG Logo
            int sizeXJSG = (int) (width / 2.33);
            int sizeYJSG = (603 * sizeXJSG) / 1586;

            int[] center = getCenterPos(sizeXJSG, sizeYJSG);
            int x = (int) (center[0] * 0.25);
            int y = (int) (center[1] * 0.5);

            if(isPointInRegion(PADDING, PADDING, jsgSizeX, jsgSizeY, mouseX, mouseY) || isPointInRegion(x, y, sizeXJSG, sizeYJSG, mouseX, mouseY)){
                openWebsiteToClient(WEBSITE);
            }
        }
    }

    @Override
    protected void actionPerformed(@Nonnull GuiButton button) {
        if (button.id < 20) {
            if (button.id != currentButton) {
                currentButton = button.id;
                return;
            }
            switch (button.id) {
                // main menu screens
                case 0:
                    this.mc.displayGuiScreen(new GuiWorldSelection(this));
                    break;
                case 1:
                    this.mc.displayGuiScreen(new GuiMultiplayer(this));
                    break;
                case 2:
                    this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                    break;
                case 3:
                    this.mc.shutdown();
                    break;
                case 4:
                    openWebsiteToClient(WEBSITE);
                    break;
                case 5:
                    this.mc.displayGuiScreen(new GuiModList(this));
                    break;
            }
        } else {
            // Probably updater button
            switch (button.id) {
                case 20:
                    openWebsiteToClient(DOWNLOAD_URL_USER);
                    break;
                case 21:
                    updateChecked = true;
                    break;
            }
        }
    }

    /**
     * Used to draw buttons
     */
    public void drawButtons(int mouseX, int mouseY) {
        // Updater buttons
        if (!updateNotificationRendered) {
            for (GuiButton button : updaterButtons) {
                button.visible = false;
                button.enabled = false;
            }
        }

        // General buttons
        for (GuiButton button : buttonList) {
            // just make sure that Button will not be activated by any chance
            button.visible = false;
            button.enabled = !updateNotificationRendered;
            if (button instanceof IconButton)
                ((IconButton) button).texture = getIconsTexture();
        }
        for (int i = -2; i <= 2; i++) {
            GlStateManager.enableBlend();
            int btn = getButtonForDisplay(i);
            IconButton button = (IconButton) buttonList.get(btn);
            int x = (width - (button.width + PADDING * 2));
            int y = getCenterPos(button.width, button.height)[1] + (i * (button.height + 10));
            button.x = x;
            button.y = y;
            button.visible = true;
            button.drawButton(mouseX, mouseY);
            if (i == 0) {
                button.drawButton(mouseX, mouseY);
                button.drawButton(mouseX, mouseY);
                button.drawButton(mouseX, mouseY);

                String[] label = button.label;
                int labelHigh = label.length * 10;
                int syDefault = (button.y + (button.height / 2));
                int syStart = syDefault - (labelHigh / 2);
                int color = 0xffffff;
                for (int ii = 0; ii < label.length; ii++) {
                    fontRenderer.drawString(label[ii], button.x - PADDING / 2 - fontRenderer.getStringWidth(label[ii]), syStart + ii * 10, color);
                    color = 0x404040;
                }
            }
            if (i == -1 || i == 1) {
                button.drawButton(mouseX, mouseY);
            }
            GlStateManager.disableBlend();
        }
    }

    /**
     * Used to draw background texture (panorama)
     */
    private double backgroundScale = 1;
    private int currentBackground = 0;
    public void drawBackground() {

        currentBackground = (int) (Math.floor(tick / BACKGROUND_STAY_TIME) % BACKGROUNDS_COUNT);

        double timeHere = tick % BACKGROUND_STAY_TIME;
        if (timeHere > (BACKGROUND_STAY_TIME - ((double) BACKGROUND_CHANGE_ANIMATION_LENGTH / 2))) {
            if (backgroundChangeStart == -1) {
                backgroundChangeStart = (long) tick;
            }
        }

        float scale = 1f + (float) ((timeHere / BACKGROUND_STAY_TIME) * 0.2f);

        double coef = 0;
        double current = tick - backgroundChangeStart;
        if (backgroundChangeStart != -1) {
            if (current <= BACKGROUND_CHANGE_ANIMATION_LENGTH)
                coef = Math.min(Math.sin((current / BACKGROUND_CHANGE_ANIMATION_LENGTH) * Math.PI) * 1.1, 1);
            else
                backgroundChangeStart = -1;
        }

        GlStateManager.pushMatrix();
        int[] center = getCenterPos(0, 0);
        GlStateManager.translate(center[0], center[1], 0);
        GlStateManager.scale(scale, scale, 1);
        backgroundScale = scale;
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUNDS.get(currentBackground));
        drawScaledCustomSizeModalRect(-(width / 2), -(height / 2), 0, 0, 1921, 1018, width, height, 1920, 1017);
        GlStateManager.popMatrix();

        drawRect(0, 0, width, height, new Color(0, 0, 0, (int) (255 * coef)).getRGB());

        gateType.renderGate(width + 20, getCenterPos(0, 0)[1], 45, tick);
    }

    /**
     * Used to draw texts on the screen
     */
    public void drawTitles() {
        if (JSGConfig.mainMenuConfig.debugMode) {
            int[] center = getCenterPos(0, 0);
            center[1] -= 30;
            // Debug
            int i = 0;
            fontRenderer.drawString("FPS: " + Minecraft.getDebugFPS(), PADDING, center[1] + (10 * (++i)), 0xffffff);
            fontRenderer.drawString("width: " + width, PADDING, center[1] + (10 * (++i)), 0xffffff);
            fontRenderer.drawString("height: " + height, PADDING, center[1] + (10 * (++i)), 0xffffff);
            fontRenderer.drawString("time: " + String.format("%.4f", tick), PADDING, center[1] + (10 * (++i)), 0xffffff);
            fontRenderer.drawString("timeHere: " + String.format("%.4f", (tick % BACKGROUND_STAY_TIME)), PADDING, center[1] + (10 * (++i)), 0xffffff);
            fontRenderer.drawString("backgroundScale: " + String.format("%.4f", backgroundScale), PADDING, center[1] + (10 * (++i)), 0xffffff);
            fontRenderer.drawString("backgroundChangeStart: " + backgroundChangeStart, PADDING, center[1] + (10 * (++i)), 0xffffff);
            fontRenderer.drawString("currentBackground: " + currentBackground, PADDING, center[1] + (10 * (++i)), 0xffffff);
            fontRenderer.drawString("gateType: " + gateType.toString(), PADDING, center[1] + (10 * (++i)), 0xffffff);
            fontRenderer.drawString("currentButton: " + currentButton, PADDING, center[1] + (10 * (++i)), 0xffffff);
            fontRenderer.drawString("updater: (Status: " + UPDATER_RESULT.result.toString() + "; Got: " + UPDATER_RESULT.response + ")", PADDING, center[1] + (10 * (++i)), 0xffffff);
        }


        fontRenderer.drawString(JSG_RUNNING_TEXT, PADDING, PADDING, 0xffffff);
        fontRenderer.drawString("Running on Minecraft Java " + JSG.MC_VERSION, PADDING, PADDING + 10, 0xffffff);


        int sizeXTauri = width / 10;
        int sizeYTauri = (230 * sizeXTauri) / 411;
        int sizeXMojang = width / 10;
        int sizeYMojang = (52 * sizeXMojang) / 300;

        int sizeXJSG = (int) (width / 2.33);
        int sizeYJSG = (603 * sizeXJSG) / 1586;

        int[] center = getCenterPos(sizeXJSG, sizeYJSG);
        int x = (int) (center[0] * 0.25);
        int y = (int) (center[1] * 0.5);

        GlStateManager.enableBlend();

        // Tauri dev logo
        Minecraft.getMinecraft().getTextureManager().bindTexture(LOGO_TAURI);
        drawScaledCustomSizeModalRect(PADDING, height - PADDING - sizeYTauri - sizeYMojang - 8, 0, 0, 411, 230, sizeXTauri, sizeYTauri, 410, 229);

        // Mojang logo
        Minecraft.getMinecraft().getTextureManager().bindTexture(LOGO_MOJANG);
        drawScaledCustomSizeModalRect(PADDING, height - PADDING - sizeYMojang, 0, 0, 301, 53, sizeXMojang, sizeYMojang, 300, 52);

        // JSG logo
        Minecraft.getMinecraft().getTextureManager().bindTexture(LOGO_JSG);
        drawScaledCustomSizeModalRect(x, y, 0, 0, 1586, 603, sizeXJSG, sizeYJSG, 1586, 603);

        GlStateManager.disableBlend();
    }

    /**
     * Used to draw hovered texts & updater notification
     */
    public void drawFg(int mouseX, int mouseY) {
        if (!updateNotificationRendered) {
            for (GuiButton b : buttonList) {
                if (b instanceof IconButton && b.visible && b.id != currentButton)
                    ((IconButton) b).drawFg(mouseX, mouseY, this);
            }

            int sizeXTauri = width / 10;
            int sizeYTauri = (230 * sizeXTauri) / 411;
            int sizeYMojang = (52 * sizeXTauri) / 300;
            if (isPointInRegion(PADDING, height - PADDING - sizeYTauri - sizeYMojang - 8, sizeXTauri, sizeYTauri, mouseX, mouseY)) {
                List<String> power = Arrays.asList(
                        TextFormatting.WHITE.toString() + TextFormatting.BOLD + "Just Stargate Mod developed by:",
                        TextFormatting.GRAY + "MineDragonCZ_",
                        TextFormatting.GRAY + "matousss",
                        TextFormatting.GRAY + "Fredyman_95",
                        TextFormatting.GRAY + "Harald De Luca",
                        TextFormatting.GRAY + "",
                        TextFormatting.WHITE.toString() + TextFormatting.BOLD + "Base of this mod by:",
                        TextFormatting.GRAY + "MrJake222"
                );
                drawHoveringText(power, mouseX, mouseY);
            }
        }


        // updater notification
        if (updateNotificationRendered) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 50);
            drawGradientRect(0, 0, width, height, -1072689136, -804253680);

            int backgroundWidth = 300;
            int backgroundHeight = 140;

            int[] center = getCenterPos(backgroundWidth, backgroundHeight);
            int x = center[0];
            int y = center[1];

            int[] center1 = getCenterPos(0, 0);
            int xCenter = center1[0];

            // Background
            Minecraft.getMinecraft().getTextureManager().bindTexture(NOTIFICATION);
            drawModalRectWithCustomSizedTexture(x, y, 0, 0, backgroundWidth, backgroundHeight, backgroundWidth, backgroundHeight);

            boolean error = UPDATER_RESULT.result == GetUpdate.EnumUpdateResult.ERROR;

            // Titles
            updaterButtons.get(0).y = y + backgroundHeight - 30;
            if (!error) {
                drawCenteredString(fontRenderer, "New update is available!", xCenter, y + 20, 0x404040, false);

                drawCenteredString(fontRenderer, "You can update to version " + UPDATER_RESULT.response, xCenter, y + 50, 0x404040, false);
                drawCenteredString(fontRenderer, "It is highly recommended to update to this version!", xCenter, y + 60, 0x404040, false);
                drawCenteredString(fontRenderer, "Some dangerous bugs should be fixed in this version.", xCenter, y + 70, 0x404040, false);

                updaterButtons.get(0).x = xCenter - updaterButtons.get(0).width - (PADDING / 2);
                updaterButtons.get(0).enabled = true;
                updaterButtons.get(0).drawButton(mc, mouseX, mouseY, 0);
            } else {
                drawCenteredString(fontRenderer, "Error while checking update!", xCenter, y + 20, 0x404040, false);
                drawCenteredString(fontRenderer, UPDATER_RESULT.response, xCenter, y + 30, 0x404040, false);

                drawCenteredString(fontRenderer, "Can not get response from the server!", xCenter, y + 50, 0x404040, false);
                drawCenteredString(fontRenderer, "Please check your internet connection.", xCenter, y + 60, 0x404040, false);
                updaterButtons.get(0).enabled = false;
            }

            updaterButtons.get(1).enabled = true;
            updaterButtons.get(1).x = xCenter + (PADDING / 2);
            if (error)
                updaterButtons.get(1).x = xCenter - (updaterButtons.get(1).width / 2);
            updaterButtons.get(1).y = updaterButtons.get(0).y;
            updaterButtons.get(1).drawButton(mc, mouseX, mouseY, 0);

            GlStateManager.popMatrix();
        }

    }

    public void drawCenteredString(FontRenderer fontRendererIn, @Nonnull String text, int x, int y, int color, boolean shadow) {
        if (shadow) {
            super.drawCenteredString(fontRendererIn, text, x, y, color);
            return;
        }
        fontRendererIn.drawString(text, (x - fontRendererIn.getStringWidth(text) / 2), y, color);
    }
}
