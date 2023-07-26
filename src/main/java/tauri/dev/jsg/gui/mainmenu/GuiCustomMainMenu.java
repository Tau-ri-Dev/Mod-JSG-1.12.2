package tauri.dev.jsg.gui.mainmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.gui.element.IconButton;
import tauri.dev.jsg.loader.ReloadListener;
import tauri.dev.jsg.sound.JSGSoundHelperClient;
import tauri.dev.jsg.sound.SoundPositionedEnum;
import tauri.dev.jsg.util.JSGMinecraftHelper;
import tauri.dev.jsg.util.updater.GetUpdate;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static tauri.dev.jsg.gui.element.GuiHelper.isPointInRegion;
import static tauri.dev.jsg.gui.mainmenu.MainMenuNotifications.BACKGROUND_HEIGHT;
import static tauri.dev.jsg.gui.mainmenu.MainMenuNotifications.BUTTONS_ID_START;
import static tauri.dev.jsg.util.updater.GetUpdate.DOWNLOAD_URL_USER;
import static tauri.dev.jsg.util.updater.GetUpdate.openWebsiteToClient;

@SideOnly(Side.CLIENT)
public class GuiCustomMainMenu extends GuiScreen {

    public static final String WEBSITE = "https://justsgmod.eu/";

    public static final String WIKI_RAM_ALLOCATION_URL = "https://justsgmod.eu/wiki/?category=general&topic=start#Allocating%20more%20RAM";
    public static final String GITHUB = "https://github.com/Tau-ri-Dev";
    public static final String MINECRAFT_SITES = "https://minecraft.net/en-us";
    public static final String JSG_RUNNING_TEXT = "Just Stargate Mod v" + JSG.MOD_VERSION.replaceAll(JSG.MC_VERSION + "-", "");

    public static class MainMenuBackground {
        protected static final String PATH = "textures/gui/mainmenu/background";
        protected static final String END = ".jpg";

        public static ResourceLocation get(int id){
            return new ResourceLocation(JSG.MOD_ID, PATH + id + END);
        }
    }
    public static final ResourceLocation LOGO_TAURI = new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/tauri_dev_logo.png");
    public static final ResourceLocation LOGO_MOJANG = new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/mojang_logo.png");
    public static final ResourceLocation LOGO_JSG = new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/jsg_logo.png");
    public static final int BACKGROUNDS_COUNT = JSGConfig.General.mainMenuConfig.backgroundImagesCount;
    public static final long FIRST_TRANSITION_LENGTH = 7 * 20; // in relative ticks
    public static final int PADDING = 10;
    public static final MainMenuNotifications NOTIFIER = MainMenuNotifications.getManager();
    private static final int BACKGROUND_CHANGE_ANIMATION_LENGTH = 60; //ticks
    private static final int BACKGROUND_STAY_TIME = 400; //ticks
    public static GetUpdate.UpdateResult UPDATER_RESULT = GetUpdate.checkForUpdate();
    public static long menuDisplayed = -1;
    public static boolean menuWasDisplayed = false;
    public static double firstTransitionStart = 0;
    // Stores the larges number of FPS
    private static int bestFPS = 0;
    private static int currentButton = 0;
    private static boolean menuWasDisplayedIgnoredFPS = false;
    private static int updaterNotification = -1;

    static {
        if (JSGConfig.General.mainMenuConfig.debugMode) {
            NOTIFIER.add(new MainMenuNotifications.Notification(new ArrayList<GuiButton>() {{
                // Close (21)
                String close = I18n.format("menu.updater.close");
                int width = Minecraft.getMinecraft().fontRenderer.getStringWidth(close) + 20;
                add(new GuiButton(BUTTONS_ID_START + 1, -width / 2, 0, width, 20, close));
            }}, "Report from startup:",
                    "",
                    "Total errors/warning: " + ReloadListener.LoadingStats.errors + "/" + ReloadListener.LoadingStats.warnings,
                    "Total textures loaded/not: " + ReloadListener.LoadingStats.loadedTextures + "/" + ReloadListener.LoadingStats.notLoadedTextures,
                    "Total models loaded/not: " + ReloadListener.LoadingStats.loadedModels + "/" + ReloadListener.LoadingStats.notLoadedModels,
                    "Loaded animated EH: " + ReloadListener.LoadingStats.loadedAnimatedEHs,
                    "Loaded new kawoosh: " + ReloadListener.LoadingStats.loadedNewKawoosh
            ) {
                @Override
                public void render(int mouseX, int mouseY, int width, int height, int rectX, int rectY, GuiScreen parentScreen) {
                    super.renderText(mouseX, mouseY, width, height, rectX, rectY, parentScreen);
                    int xCenter = getCenterPos(0, 0, width, height)[0];

                    buttons.get(0).y = rectY + BACKGROUND_HEIGHT - 30;
                    buttons.get(0).x = xCenter - (buttons.get(0).width / 2);
                    buttons.get(0).drawButton(parentScreen.mc, mouseX, mouseY, 0);
                }

                @Override
                public void actionPerformed(@Nonnull GuiButton button) {
                    if (button.id == BUTTONS_ID_START + 1) {
                        dismiss();
                    }
                }
            });
        }
    }

    public double tick;
    public boolean isMusicPlaying = false;
    public EnumMainMenuGateType gateType = EnumMainMenuGateType.random(null);
    private long backgroundChangeStart = 0;
    private long lastGateChange = 0;
    /**
     * Used to draw background texture (panorama)
     */
    private double backgroundScale = 1;
    private int currentBackground = 0;
    private EnumMainMenuTips tipEnum = EnumMainMenuTips.random(null);

    public GuiCustomMainMenu() {
        tick = JSGMinecraftHelper.getClientTickPrecise();
        createFadeIn();
    }

    public static void playMusic(boolean play) {
        JSGSoundHelperClient.playPositionedSoundClientSide(JSG.lastPlayerPosInWorld, SoundPositionedEnum.MAINMENU_MUSIC, play);
    }

    public static int[] getCenterPos(int rectWidth, int rectHeight, int winWidth, int winHeight) {
        return new int[]{((winWidth - rectWidth) / 2), ((winHeight - rectHeight) / 2)};
    }

    public void tick() {
        // If sync is enabled then sync local ticks with mc ticks
        if (JSGConfig.General.mainMenuConfig.syncEnabled) {
            tick = JSGMinecraftHelper.getClientTickPrecise();
        } else {
            int currentFPS = Minecraft.getDebugFPS();
            if (currentFPS > bestFPS || bestFPS > 30) bestFPS = currentFPS;
            tick += (bestFPS > 0 ? ((30D / (double) bestFPS) * (20D / 30D)) : 1D);
        }
    }

    public void createFadeIn() {
        backgroundChangeStart = (long) (tick - (BACKGROUND_CHANGE_ANIMATION_LENGTH / 3));
    }

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

    public void updateMusic() {
        if ((tick - menuDisplayed) <= 20 * 7 || (Minecraft.getDebugFPS() < 28 && (tick - menuDisplayed) <= 20 * 30))
            return; // wait some seconds before first play

        if ((tick - menuDisplayed) > 20 * 30)
            isMusicPlaying = JSGSoundHelperClient.getRecord(SoundPositionedEnum.MAINMENU_MUSIC, JSG.lastPlayerPosInWorld).isPlaying();

        if (!isMusicPlaying && JSGConfig.General.mainMenuConfig.playMusic) {
            isMusicPlaying = true;
            playMusic(true);
        }
        if (!JSGConfig.General.mainMenuConfig.playMusic && isMusicPlaying)
            playMusic(false);
    }

    public int[] getCenterPos(int rectWidth, int rectHeight) {
        return getCenterPos(rectWidth, rectHeight, width, height);
    }

    @Override
    public void initGui() {
        if (JSGConfig.General.devConfig.enableDevMode) {
            UPDATER_RESULT = new GetUpdate.UpdateResult((JSGConfig.General.devConfig.t1 ? GetUpdate.EnumUpdateResult.ERROR : GetUpdate.EnumUpdateResult.NEWER_AVAILABLE), (JSGConfig.General.devConfig.t1 ? "Test error" : UPDATER_RESULT.response));
            updaterNotification = -1;
        }
        if (JSGConfig.General.mainMenuConfig.debugMode)
            menuWasDisplayed = false;
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
        // Notifier about updates
        initUpdaterNotifier();

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

    public void updateGateType() {
        if ((tick - lastGateChange) < 30 * 20) return;
        lastGateChange = (long) tick;
        // Every 30 seconds

        if (JSGConfig.General.mainMenuConfig.enableGateChanging) {
            gateType = EnumMainMenuGateType.random(gateType);
        }

        tipEnum = EnumMainMenuTips.random(tipEnum);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        tick();
        if (menuDisplayed == -1) {
            menuDisplayed = (long) tick;
            createFadeIn();
        }
        updateMusic();
        updateGateType();
        drawBackground();
        drawButtons(mouseX, mouseY);
        drawTitles();
        drawFg(mouseX, mouseY);
        if (!menuWasDisplayed) {
            firstTransitionStart = tick;
            if (Minecraft.getDebugFPS() >= 25)
                menuWasDisplayed = true;
        }
        drawFirstAnimation();
        if (!menuWasDisplayedIgnoredFPS) {
            firstInit();
            menuWasDisplayedIgnoredFPS = true;
        }
    }

    public void drawFirstAnimation() {
        if (!JSGConfig.General.mainMenuConfig.enableLogo) return;
        double current = (tick - firstTransitionStart);
        if (current > FIRST_TRANSITION_LENGTH) return;

        double step = FIRST_TRANSITION_LENGTH / 5D;

        double alpha = 1 - Math.min(1, (Math.max(0, current - (4.5D * step)) / (step / 2)));
        double alpha2 = 1 - Math.min(1, (Math.max(0, current - step) / step));
        double coef = Math.min(1, (Math.max(0, current - (4 * step)) / (step / 2)));

        final float sizeCoefEnd = 10f;

        float sizeCoef = (float) Math.max(coef * sizeCoefEnd, 3D);

        int sizeXJSG = (int) (width / sizeCoef);
        int sizeYJSG = (230 * sizeXJSG) / 411;

        int sizeXMojang = (int) (width / sizeCoef);
        int sizeYMojang = (52 * sizeXMojang) / 300;

        int[] center = getCenterPos(sizeXJSG, sizeYJSG);
        int xEnd = PADDING;
        int yEnd = (height - PADDING - sizeYJSG - sizeYMojang - 8);

        int xStart = center[0];
        int yStart = center[1];

        double xSum = (xStart - xEnd);
        double ySum = (yStart - yEnd);

        int x = (int) (xEnd + (1f - coef) * xSum);
        int y = (int) (yEnd + (1f - coef) * ySum);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 52);
        GlStateManager.pushMatrix();
        drawRect(0, 0, width, height, new Color(0, 0, 0, (int) (255 * alpha)).getRGB());
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, 1);

        GlStateManager.enableBlend();
        // Tauri dev logo
        Minecraft.getMinecraft().getTextureManager().bindTexture(LOGO_TAURI);
        drawScaledCustomSizeModalRect(x, y, 0, 0, 410, 229, sizeXJSG, sizeYJSG, 411, 230);

        center = getCenterPos(0, 0);
        if (alpha > 0.75)
            drawCenteredString(fontRenderer, "We are not associated with Mojang.", center[0], height - PADDING - 10, 0xFFFFFF, true);

        GlStateManager.disableBlend();

        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        drawRect(0, 0, width, height, new Color(0, 0, 0, (int) (255 * alpha2)).getRGB());
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        NOTIFIER.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0 && NOTIFIER.currentDisplayed == null) {
            for (GuiButton guibutton : buttonList) {
                if (guibutton.mousePressed(mc, mouseX, mouseY)) {
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    actionPerformed(guibutton);
                }
            }

            // Clickable images/texts
            int sizeXTauri = width / 10;
            int sizeYTauri = (230 * sizeXTauri) / 411;
            int sizeYMojang = (52 * sizeXTauri) / 300;
            if (isPointInRegion(PADDING, height - PADDING - sizeYTauri - sizeYMojang - 8, sizeXTauri, sizeYTauri, mouseX, mouseY)) {
                openWebsiteToClient(GITHUB);
            }
            if (isPointInRegion(PADDING, height - PADDING - sizeYMojang, sizeXTauri, sizeYMojang, mouseX, mouseY)) {
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

            if (isPointInRegion(PADDING, PADDING, jsgSizeX, jsgSizeY, mouseX, mouseY) || isPointInRegion(x, y, sizeXJSG, sizeYJSG, mouseX, mouseY)) {
                openWebsiteToClient(WEBSITE);
            }
        }
    }

    @Override
    protected void actionPerformed(@Nonnull GuiButton button) {
        if (button.id < buttonList.size()) {
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
        }
    }

    /**
     * Used to draw buttons
     */
    public void drawButtons(int mouseX, int mouseY) {
        // General buttons
        for (GuiButton button : buttonList) {
            // just make sure that Button will not be activated by any chance
            button.visible = false;
            button.enabled = (NOTIFIER.currentDisplayed == null);
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
        Minecraft.getMinecraft().getTextureManager().bindTexture(MainMenuBackground.get(currentBackground));
        drawScaledCustomSizeModalRect(-(width / 2), -(height / 2), 0, 0, 1921, 1018, width, height, 1920, 1017);
        GlStateManager.popMatrix();

        drawRect(0, 0, width, height, new Color(0, 0, 0, (int) (255 * coef)).getRGB());

        gateType.renderGate(width + 20, getCenterPos(0, 0)[1], 45, tick);
    }

    /**
     * Used to draw texts on the screen
     */
    public void drawTitles() {
        if (JSGConfig.General.mainMenuConfig.debugMode) {
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

        // JSG logo - main
        Minecraft.getMinecraft().getTextureManager().bindTexture(LOGO_JSG);
        drawScaledCustomSizeModalRect(x, y, 0, 0, 1586, 603, sizeXJSG, sizeYJSG, 1586, 603);

        String[] tip = tipEnum.text;

        int startY = -(tip.length * 10);
        int i = 0;
        center = getCenterPos(0, 0);
        for (String s : tip) {
            drawCenteredString(fontRenderer, I18n.format(s), center[0], height - PADDING + startY + i * 10, 0xCEAD28, true);
            i++;
        }

        GlStateManager.disableBlend();
    }

    /**
     * Used to draw hovered texts & updater notification
     */
    public void drawFg(int mouseX, int mouseY) {
        NOTIFIER.update();
        if (NOTIFIER.currentDisplayed == null) {
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
        } else {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 50);
            drawGradientRect(0, 0, width, height, -1072689136, -804253680);
            NOTIFIER.render(mouseX, mouseY, width, height, this);
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

    public void initUpdaterNotifier() {
        if (UPDATER_RESULT.result == GetUpdate.EnumUpdateResult.NEWER_AVAILABLE || UPDATER_RESULT.result == GetUpdate.EnumUpdateResult.ERROR) {
            boolean error = UPDATER_RESULT.result == GetUpdate.EnumUpdateResult.ERROR;
            if (updaterNotification == -1 || NOTIFIER.get(updaterNotification) == null) {
                if (!error) {
                    updaterNotification = NOTIFIER.add(new MainMenuNotifications.Notification(new ArrayList<GuiButton>() {{
                        // Download (20)
                        String update = I18n.format("menu.updater.download");
                        int width = fontRenderer.getStringWidth(update) + 20;
                        add(new GuiButton(BUTTONS_ID_START, -width - (PADDING / 2), 0, width, 20, update));

                        // Close (21)
                        update = I18n.format("menu.updater.close");
                        width = fontRenderer.getStringWidth(update) + 20;
                        add(new GuiButton(BUTTONS_ID_START + 1, (PADDING / 2), 0, width, 20, update));
                    }}, "New update is available!",
                            "",
                            "",
                            "You can update to version " + UPDATER_RESULT.response,
                            "It is highly recommended to update to this version!",
                            "Some dangerous bugs should be fixed in this version."
                    ) {
                        @Override
                        public void render(int mouseX, int mouseY, int width, int height, int rectX, int rectY, GuiScreen parentScreen) {
                            super.renderText(mouseX, mouseY, width, height, rectX, rectY, parentScreen);
                            int xCenter = getCenterPos(0, 0)[0];

                            buttons.get(0).y = rectY + BACKGROUND_HEIGHT - 30;
                            buttons.get(0).x = xCenter - buttons.get(0).width - (PADDING / 2);
                            buttons.get(0).drawButton(mc, mouseX, mouseY, 0);
                            buttons.get(1).x = xCenter + (PADDING / 2);
                            buttons.get(1).y = buttons.get(0).y;
                            buttons.get(1).drawButton(mc, mouseX, mouseY, 0);
                        }

                        @Override
                        public void actionPerformed(@Nonnull GuiButton button) {
                            switch (button.id) {
                                case BUTTONS_ID_START:
                                    openWebsiteToClient(DOWNLOAD_URL_USER);
                                    break;
                                case BUTTONS_ID_START + 1:
                                    dismiss();
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                } else {
                    updaterNotification = NOTIFIER.add(new MainMenuNotifications.Notification(new ArrayList<GuiButton>() {{
                        // Close (21)
                        String close = I18n.format("menu.updater.close");
                        int width = fontRenderer.getStringWidth(close) + 20;
                        add(new GuiButton(BUTTONS_ID_START + 1, -width / 2, 0, width, 20, close));
                    }}, "Error while checking update!",
                            UPDATER_RESULT.response,
                            "",
                            "Can not get response from the server!",
                            "Please check your internet connection.",
                            "Problem can also be on our side."
                    ) {
                        @Override
                        public void render(int mouseX, int mouseY, int width, int height, int rectX, int rectY, GuiScreen parentScreen) {
                            super.renderText(mouseX, mouseY, width, height, rectX, rectY, parentScreen);
                            int xCenter = getCenterPos(0, 0)[0];

                            buttons.get(0).y = rectY + BACKGROUND_HEIGHT - 30;
                            buttons.get(0).x = xCenter - (buttons.get(0).width / 2);
                            buttons.get(0).drawButton(mc, mouseX, mouseY, 0);
                        }

                        @Override
                        public void actionPerformed(@Nonnull GuiButton button) {
                            if (button.id == BUTTONS_ID_START + 1) {
                                dismiss();
                            }
                        }
                    });
                }
            }
        }
    }

    public void firstInit() {

        if (JSG.memoryTotal < 6L * 1024 * 1024 * 1024) {
            // Insert notification about low RAM
            NOTIFIER.add(new MainMenuNotifications.Notification(new ArrayList<GuiButton>() {{
                // Wiki
                String update = I18n.format("menu.ram.help");
                int width = fontRenderer.getStringWidth(update) + 20;
                add(new GuiButton(BUTTONS_ID_START + 10, -width - (PADDING / 2), 0, width, 20, update));

                // Close
                update = I18n.format("menu.updater.close");
                width = fontRenderer.getStringWidth(update) + 20;
                add(new GuiButton(BUTTONS_ID_START + 11, (PADDING / 2), 0, width, 20, update));
            }}, "Allocate more RAM!",
                    "",
                    "Recommended minimum RAM for JSG mod is 6GB!",
                    "By ignoring this fact, you can",
                    "run into troubles with this mod."
            ) {
                @Override
                public void render(int mouseX, int mouseY, int width, int height, int rectX, int rectY, GuiScreen parentScreen) {
                    super.renderText(mouseX, mouseY, width, height, rectX, rectY, parentScreen);
                    int xCenter = getCenterPos(0, 0)[0];

                    buttons.get(0).y = rectY + BACKGROUND_HEIGHT - 30;
                    buttons.get(0).x = xCenter - buttons.get(0).width - (PADDING / 2);
                    buttons.get(0).drawButton(mc, mouseX, mouseY, 0);
                    buttons.get(1).x = xCenter + (PADDING / 2);
                    buttons.get(1).y = buttons.get(0).y;
                    buttons.get(1).drawButton(mc, mouseX, mouseY, 0);
                }

                @Override
                public void actionPerformed(@Nonnull GuiButton button) {
                    switch (button.id) {
                        case BUTTONS_ID_START + 10:
                            openWebsiteToClient(WIKI_RAM_ALLOCATION_URL);
                            break;
                        case BUTTONS_ID_START + 11:
                            dismiss();
                            break;
                        default:
                            break;
                    }
                }
            });
            NOTIFIER.update();
        }
    }
}
