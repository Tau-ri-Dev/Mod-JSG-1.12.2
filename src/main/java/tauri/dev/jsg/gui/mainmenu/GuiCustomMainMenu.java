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
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static tauri.dev.jsg.gui.element.GuiHelper.isPointInRegion;

@SideOnly(Side.CLIENT)
public class GuiCustomMainMenu extends GuiScreen {

    public static final String WIKI_URL = "https://justsgmod.eu/wiki";

    public long tick;

    public static final ArrayList<ResourceLocation> BACKGROUNDS = new ArrayList<ResourceLocation>() {{
        add(new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/background0.png"));
        add(new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/background1.png"));
        add(new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/background2.png"));
        add(new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/background3.png"));
        add(new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/background4.png"));
        add(new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/background5.png"));
    }};

    public static final ResourceLocation ICONS = new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/icons.png");
    public static final ResourceLocation LOGO_TAURI = new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/tauri_dev_logo.png");
    public static final ResourceLocation LOGO_MOJANG = new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/mojang_logo.png");
    public static final ResourceLocation NOTIFICATION = new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/popup.png");

    public static final GetUpdate.UpdateResult UPDATER_RESULT = GetUpdate.checkForUpdate();
    public static final boolean updateChecked = false;

    public static final int BACKGROUNDS_COUNT = BACKGROUNDS.size();

    public static boolean isMusicPlaying = false;

    public static long menuDisplayed = -1;

    public static final int PADDING = 18;

    public static void playMusic(boolean play) {
        isMusicPlaying = play;
        JSGSoundHelperClient.playPositionedSoundClientSide(new BlockPos(0, 0, 0), SoundPositionedEnum.MAINMENU_MUSIC, play);
    }

    public int[] getCenterPos(int rectWidth, int rectHeight) {
        return new int[]{((width - rectWidth) / 2), ((height - rectHeight) / 2)};
    }

    @Override
    public void initGui() {
        super.initGui();
        if (!isMusicPlaying && JSGConfig.mainMenuConfig.playMusic)
            playMusic(true);

        buttonList.clear();
        labelList.clear();

        // buttons
        int id = -1;
        final int texSize = 128;
        buttonList.add(new IconButton(++id, 0, 0, ICONS, texSize, 32, 32, 32, 32, false, I18n.format("menu.singleplayer")));
        buttonList.add(new IconButton(++id, 0, 0, ICONS, texSize, 64, 32, 32, 32, false, I18n.format("menu.multiplayer")));
        buttonList.add(new IconButton(++id, 0, 0, ICONS, texSize, 32, 0, 32, 32, false, I18n.format("menu.options")));
        buttonList.add(new IconButton(++id, 0, 0, ICONS, texSize, 0, 32, 32, 32, false, I18n.format("menu.quit")));
        buttonList.add(new IconButton(++id, 0, 0, ICONS, texSize, 0, 0, 32, 32, false, I18n.format("menu.about")));
        buttonList.add(new IconButton(++id, 0, 0, ICONS, texSize, 64, 0, 32, 32, false, I18n.format("fml.menu.mods")));
    }

    private static int currentButton = 0;

    private static final int BACKGROUND_CHANGE_ANIMATION_LENGTH = 80; //ticks
    private static final int BACKGROUND_STAY_TIME = BACKGROUND_CHANGE_ANIMATION_LENGTH * 5; //ticks
    private static long backgroundChangeStart = -BACKGROUND_CHANGE_ANIMATION_LENGTH;

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

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        tick = (long) JSGMinecraftHelper.getClientTickPrecise();
        if (menuDisplayed == -1) menuDisplayed = tick;
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
        }
    }

    @Override
    protected void actionPerformed(@Nonnull GuiButton button) {
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
                try {
                    Class<?> oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop").invoke(null);
                    oclass.getMethod("browse", URI.class).invoke(object, new URI(WIKI_URL));
                } catch (Exception e) {
                    JSG.debug("Couldn't open link", e);
                }
                break;
            case 5:
                this.mc.displayGuiScreen(new GuiModList(this));
                break;
        }
    }

    /**
     * Used to draw buttons
     */
    private final ArrayList<Integer> buttonsRendered = new ArrayList<>();

    public void drawButtons(int mouseX, int mouseY) {
        buttonsRendered.clear();
        for (GuiButton button : buttonList) {
            // just make sure that Button will not be activated by any chance
            button.x = -500;
        }
        for (int i = -2; i <= 2; i++) {
            GlStateManager.enableBlend();
            int btn = getButtonForDisplay(i);
            IconButton button = (IconButton) buttonList.get(btn);
            int x = (width - (button.width + PADDING));
            int y = getCenterPos(button.width, button.height)[1] + (i * (button.height + 10));
            button.x = x;
            button.y = y;
            button.drawButton(mouseX, mouseY);
            if (i == 0) {
                button.drawButton(mouseX, mouseY);
                button.drawButton(mouseX, mouseY);
                button.drawButton(mouseX, mouseY);
            }
            if (i == -1 || i == 1) {
                button.drawButton(mouseX, mouseY);
            }
            buttonsRendered.add(btn);
            GlStateManager.disableBlend();
        }
    }

    /**
     * Used to draw background texture (panorama)
     */
    public void drawBackground() {

        int currentBackground = (int) (Math.floor((double) tick / BACKGROUND_STAY_TIME) % BACKGROUNDS_COUNT);

        double timeHere = tick % BACKGROUND_STAY_TIME;
        if (timeHere > (BACKGROUND_STAY_TIME - ((double) BACKGROUND_CHANGE_ANIMATION_LENGTH / 2))) {
            if (backgroundChangeStart == -1) {
                backgroundChangeStart = tick;
            }
        }

        float scale = 1f + (float) ((timeHere / BACKGROUND_STAY_TIME) * 0.2f);

        double coef = 0;
        double current = (double) tick - backgroundChangeStart;
        if (backgroundChangeStart != -1) {
            if (current <= BACKGROUND_CHANGE_ANIMATION_LENGTH)
                coef = Math.sin((current / BACKGROUND_CHANGE_ANIMATION_LENGTH) * Math.PI);
            else
                backgroundChangeStart = -1;
        }

        GlStateManager.pushMatrix();
        int[] center = getCenterPos(0, 0);
        GlStateManager.translate(center[0], center[1], 0);
        GlStateManager.scale(scale, scale, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUNDS.get(currentBackground));
        drawScaledCustomSizeModalRect(-(width / 2), -(height / 2), 0, 0, 1921, 1018, width, height, 1920, 1017);
        GlStateManager.popMatrix();

        drawRect(0, 0, width, height, new Color(255, 255, 255, (int) (255 * coef)).getRGB());

        gateType.renderGate(width + 55, getCenterPos(0, 0)[1], 45, tick, 0);
    }

    public EnumMainMenuGateType gateType = EnumMainMenuGateType.random();

    /**
     * Used to draw texts on the screen
     */
    public void drawTitles() {
        if (JSGConfig.mainMenuConfig.debugMode) {
            // Debug
            fontRenderer.drawString("timeHere: " + (tick % BACKGROUND_STAY_TIME), PADDING, 90, 0xffffff);
            fontRenderer.drawString("currentButton: " + currentButton, PADDING, 100, 0xffffff);
        }


        int sizeXTauri = width / 10;
        int sizeYTauri = (230 * sizeXTauri) / 411;
        int sizeXMojang = width / 10;
        int sizeYMojang = (52 * sizeXMojang) / 300;

        int sizeX = width / 7;
        int sizeY = (230 * sizeX) / 411;

        int[] center = getCenterPos(sizeX, sizeY);
        int x = center[0] - 80;
        int y = center[1] - 60;

        // Tauri dev logo
        GlStateManager.enableBlend();
        Minecraft.getMinecraft().getTextureManager().bindTexture(LOGO_TAURI);
        drawScaledCustomSizeModalRect(PADDING, height - PADDING - sizeYTauri - sizeYMojang - 8, 0, 0, 411, 230, sizeXTauri, sizeYTauri, 410, 229);
        GlStateManager.disableBlend();

        // Mojang logo
        GlStateManager.enableBlend();
        Minecraft.getMinecraft().getTextureManager().bindTexture(LOGO_MOJANG);
        drawScaledCustomSizeModalRect(PADDING, height - PADDING - sizeYMojang, 0, 0, 301, 53, sizeXMojang, sizeYMojang, 300, 52);
        GlStateManager.disableBlend();

        // JSG logo
        GlStateManager.enableBlend();
        Minecraft.getMinecraft().getTextureManager().bindTexture(LOGO_TAURI);
        drawScaledCustomSizeModalRect(x, y, 0, 0, 411, 230, sizeX, sizeY, 410, 229);
        GlStateManager.disableBlend();
    }

    /**
     * Used to draw hovered texts & updater notification
     */
    public void drawFg(int mouseX, int mouseY) {
        for (GuiButton b : buttonList) {
            if (b instanceof IconButton && buttonsRendered.contains(b.id))
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


        // updater notification
        if(!updateChecked && UPDATER_RESULT.result == GetUpdate.EnumUpdateResult.NEWER_AVAILABLE) {
            int sizeX = width / 2;
            int sizeY = (139 * sizeX) / 176;

            int[] center = getCenterPos(sizeX, sizeY);
            int x = center[0];
            int y = center[1];
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, 0);
            GlStateManager.enableBlend();

            center = getCenterPos(0, 0);
            center[0] = center[0] - x;
            center[1] = center[1] - y;

            // background
            Minecraft.getMinecraft().getTextureManager().bindTexture(NOTIFICATION);
            drawScaledCustomSizeModalRect(0, 0, 0, 0, 177, 140, sizeX, sizeY, 176, 139);

            // title
            GlStateManager.pushMatrix();
            GlStateManager.translate(center[0] - (fontRenderer.getStringWidth("New update is available!") / 2f), 20, 0);
            GlStateManager.scale(2, 2, 2);
            fontRenderer.drawString("New update is available!", 0, 0, 0x404040);
            GlStateManager.popMatrix();

            fontRenderer.drawString("You can update to version " + UPDATER_RESULT.newest, 8, 45, 0x404040);

            // Todo(Mine): buttons for updater

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }

    }
}
