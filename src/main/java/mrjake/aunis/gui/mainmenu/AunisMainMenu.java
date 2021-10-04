package mrjake.aunis.gui.mainmenu;

import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.gui.AunisGuiButton;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.stargate.ChevronEnum;
import mrjake.aunis.sound.AunisSoundHelperClient;
import mrjake.aunis.sound.SoundPositionedEnum;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static mrjake.aunis.gui.mainmenu.GetUpdate.getTextFromGithub;

@SideOnly(Side.CLIENT)
public class AunisMainMenu extends GuiMainMenu {
    @SubscribeEvent
    public static void onSounds(PlaySoundEvent event)
    {
        event.setResultSound(null);
    }

    // define variables
    protected float animationStage = 0;
    protected float chevronLastAnimationStage = 0;
    protected boolean chevronsActive = true;
    protected boolean playingSound = false;
    protected boolean chevronShout = true;
    protected boolean chevronShoutColapsing = false;
    protected BiomeOverlayEnum[] overlays = {
            BiomeOverlayEnum.AGED,
            BiomeOverlayEnum.FROST,
            BiomeOverlayEnum.MOSSY,
            BiomeOverlayEnum.SOOTY,
            BiomeOverlayEnum.NORMAL
    };
    protected BiomeOverlayEnum overlay = getNextBiomeOverlay(AunisConfig.mainMenuConfig.changingGateOverlay);
    protected float screenCenterHeight = (((float) height) / 2f);
    protected float screenCenterWidth = ((float) width) / 2f;
    protected List<GuiButton> aunisButtonList = new ArrayList<>();
    protected List<GuiButton> versionButtons = new ArrayList<>();
    static ResourceLocation BACKGROUND_TEXTURE = AunisConfig.mainMenuConfig.disableAunisMainMenu ? null : new ResourceLocation(Aunis.ModID, "textures/gui/mainmenu/background.jpg");;


    public static final String Version = "A4.6";
    public static final String Latest = getTextFromGithub("https://raw.githubusercontent.com/MineDragonCZ/Aunis1/master/version.txt");
    //string.substring(0, string.length() - 1);
    public static int showVersionAlert = 0;

    /**
     * ------------------------------------------
     * showVersionAlert indexes:
     *
     * 0 -> Version is good
     * 1 -> Alert is open
     * 2 -> Alert closed by client
     *
     * ------------------------------------------
     */

    // animation of top chevron
    public void updateLastChevron() {
        if (chevronShout) {
            if (this.chevronLastAnimationStage >= 0.15f || this.chevronShoutColapsing) {
                this.chevronShoutColapsing = true;
                this.chevronLastAnimationStage -= 0.035f;
            } else {
                if (this.chevronLastAnimationStage >= 0.149f) this.chevronLastAnimationStage += 0.0005f;
                else this.chevronLastAnimationStage += 0.035f;
            }

            if (this.chevronLastAnimationStage < 0) {
                this.chevronLastAnimationStage = 0;
                this.chevronShout = false;
                this.chevronShoutColapsing = false;
            }
        }
    }

    // next overlay
    public BiomeOverlayEnum getNextBiomeOverlay(boolean doIt) {
        if (doIt)
            this.overlay = overlays[new Random().nextInt(overlays.length)];
        else
            this.overlay = BiomeOverlayEnum.NORMAL;

        return this.overlay;
    }
    // play sound
    public void updateSound() {
        AunisSoundHelperClient.playPositionedSoundClientSide(new BlockPos(1,0,0), SoundPositionedEnum.MAINMENU_MUSIC, AunisConfig.mainMenuConfig.playMusic);
        AunisSoundHelperClient.playPositionedSoundClientSide(new BlockPos(0,0,1), SoundPositionedEnum.MAINMENU_RINGROLL, AunisConfig.mainMenuConfig.gateRotation);
    }

    // update ring rotation and overlay
    public void updateAnimation() {
        if (AunisConfig.mainMenuConfig.gateRotation) animationStage += 0.3f;
        if (animationStage >= 360) animationStage = 0f;
        switch ((int) animationStage) {
            case 359:
                //case 270:
            case 180:
                //case 90:
                this.chevronShout = true;
                getNextBiomeOverlay(AunisConfig.mainMenuConfig.changingGateOverlay);
                break;
        }
    }

    // RENDER MAIN MENU
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.screenCenterHeight = (((float) height) / 2f);
        this.screenCenterWidth = ((float) width) / 2f;
        // ------------------------------
        // ANIMATIONS AND SOUNDS

        updateAnimation();
        updateSound();
        updateLastChevron();

        // ------------------------------
        // DRAWING BACKGROUND

        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        this.mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawScaledCustomSizeModalRect(0, 0, 0, 0, width, height, width, height, width, height);

        // background gradient
        //this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
        //this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);

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
        GlStateManager.translate(screenCenterWidth, screenCenterHeight, 0f);
        GlStateManager.scale(30, 30, 30);
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
            // generates chevron lock animation
            float chevronOffset = 0;
            if (i == 8) chevronOffset = this.chevronLastAnimationStage;
            // ---------
            ElementEnum.MILKYWAY_CHEVRON_FRAME_MAINMENU.bindTextureAndRender(this.overlay);
            GlStateManager.translate(0, chevronOffset, 0);
            if ((i == 6 || i == 7) || !this.chevronsActive) {
                ElementEnum.MILKYWAY_CHEVRON_LIGHT_MAINMENU.bindTextureAndRender(this.overlay);
                GlStateManager.translate(0, -2 * chevronOffset, 0);
                ElementEnum.MILKYWAY_CHEVRON_MOVING_MAINMENU.bindTextureAndRender(this.overlay);
            } else {
                ElementEnum.MILKYWAY_CHEVRON_LIGHT_ACTIVE_MAINMENU.bindTextureAndRender(this.overlay);
                GlStateManager.translate(0, -2 * chevronOffset, 0);
                ElementEnum.MILKYWAY_CHEVRON_MOVING_ACTIVE_MAINMENU.bindTextureAndRender(this.overlay);
            }
            GlStateManager.popMatrix();
        }
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableTexture2D();
        GlStateManager.popMatrix();

        // ------------------------------
        // DRAWING TEXTS

        String versionInfo = "Aunis version: " + Version;
        if(Version != Latest){
            versionInfo += " Latest build: " + Latest;
            if(showVersionAlert != 2) showVersionAlert = 1;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.translate(6, (((float) height) - 46f), 0);
        GlStateManager.scale(0.8, 0.8, 0.8);
        GlStateManager.translate(0, 0, 0);
        drawString(fontRenderer, "Music credits: STARGATE SG-1 - Full Original Soundtrack OST", 0, 0, 0xffffff);
        GlStateManager.translate(0, 10, 0);
        drawString(fontRenderer, "Aunis mod by: MrJake, MineDragonCZ_ and Matousss", 0, 0, 0xffffff);
        GlStateManager.translate(0, 10, 0);
        drawString(fontRenderer, versionInfo, 0, 0, 0xffffff);
        GlStateManager.translate(0, 10, 0);
        drawString(fontRenderer, "Note that the gate cannot be rendered perfectly here!", 0, 0, 0xffffff);
        GlStateManager.translate(0, 10, 0);
        drawString(fontRenderer, "Copyright Mojang AB. Do not distribute!", 0, 0, 0xffffff);
        GlStateManager.disableTexture2D();
        GlStateManager.popMatrix();

        // ------------------------------
        // DRAWING MAIN TITLE

        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.translate(screenCenterWidth, 48, 0);
        GlStateManager.scale(4, 4, 4);
        drawCenteredString(fontRenderer, "Aunis", 0, 0, 0xffffff);
        GlStateManager.disableTexture2D();
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.translate(screenCenterWidth, 81, 0);
        GlStateManager.scale(1.5f, 1.5f, 1.5f);
        drawCenteredString(fontRenderer, "All you need is stargate", 0, 0, 0xa4a4a4);
        GlStateManager.disableTexture2D();
        GlStateManager.popMatrix();

        // ------------------------------
        // DRAWING BUTTONS

        for (GuiButton guiButton : this.aunisButtonList) {
            ((AunisGuiButton) guiButton).drawButton(this.mc, mouseX, mouseY, partialTicks);
        }

        for (GuiLabel guiLabel : this.labelList) {
            guiLabel.drawLabel(this.mc, mouseX, mouseY);
        }

        // ------------------------------
        // DRAWING VERSION ALERT

        if(showVersionAlert == 1){
            GlStateManager.pushMatrix();

            GlStateManager.pushMatrix();
            frame(0, 0, width, height, 3, 0xFF181A1F, 0xFF272B33);
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();

            GlStateManager.pushMatrix();
            GlStateManager.translate(screenCenterWidth, screenCenterHeight - 60, 0);
            GlStateManager.scale(1.5, 1.5, 1.5);
            drawCenteredString(fontRenderer, "You are using out of date version of Aunis mod.", 0, 0, 0xffffff);
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            GlStateManager.scale(1.0, 1.0, 1.0);
            GlStateManager.translate(screenCenterWidth, screenCenterHeight - 40, 0);
            drawCenteredString(fontRenderer, "For your comfort, please update", 0, 0, 0xffffff);
            GlStateManager.translate(0, 10, 0);
            drawCenteredString(fontRenderer, "it by download it from our", 0, 0, 0xffffff);
            GlStateManager.translate(0, 10, 0);
            drawCenteredString(fontRenderer, "unofficial discord server!", 0, 0, 0xffffff);
            GlStateManager.popMatrix();

            GlStateManager.popMatrix();

            for (GuiButton guiButton : this.versionButtons) {
                ((AunisGuiButton) guiButton).drawButton(this.mc, mouseX, mouseY, partialTicks);
            }

            GlStateManager.popMatrix();
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
    public void initGui() {

        this.versionButtons.clear();
        this.versionButtons.add(new AunisGuiButton(21, width / 2 - 100, height - 75, 98, 20, "OK"));
        this.versionButtons.add(new AunisGuiButton(22, width / 2 + 2, height - 75, 98, 20, "Our Discord"));

        this.aunisButtonList.clear();
        int j = this.height / 4 + 48;

        if(!AunisConfig.mainMenuConfig.disablePosButtons) {
            // single
            this.aunisButtonList.add(new AunisGuiButton(1, this.width / 2 - 100, j, I18n.format("menu.singleplayer")));
            // multi
            this.aunisButtonList.add(new AunisGuiButton(2, this.width / 2 - 100, j + 24, I18n.format("menu.multiplayer")));
            // about mod
            this.aunisButtonList.add(new AunisGuiButton(20, this.width / 2 - 100, j + 48, I18n.format("menu.about")));
            // mods list
            this.aunisButtonList.add(new AunisGuiButton(6, this.width - 6 - 98, 6, 98, 20, I18n.format("fml.menu.mods")));
            // aunis config
            //this.aunisButtonList.add(new AunisGuiButton(7, this.width - 6 - 98, 9 + 20, 98, 20, I18n.format("menu.config")));
            // options
            this.aunisButtonList.add(new AunisGuiButton(0, 6, 6, 98, 20, I18n.format("menu.options")));
            // quit
            this.aunisButtonList.add(new AunisGuiButton(4, this.width - 6 - 98, this.height - 6 - 20, 98, 20, I18n.format("menu.quit")));
        }
        else{
            // single
            this.aunisButtonList.add(new AunisGuiButton(1, this.width / 2 - 100, j, I18n.format("menu.singleplayer")));
            // multi
            this.aunisButtonList.add(new AunisGuiButton(2, this.width / 2 - 100, j + 24, I18n.format("menu.multiplayer")));
            // about mod
            this.aunisButtonList.add(new AunisGuiButton(20, this.width / 2 - 100, j + 48, 98, 20, I18n.format("menu.about")));
            // mods list
            this.aunisButtonList.add(new AunisGuiButton(6, this.width / 2 + 2, j + 48, 98, 20, I18n.format("fml.menu.mods")));
            // aunis config
            //this.aunisButtonList.add(new AunisGuiButton(7, this.width - 6 - 98, 6, 98, 20, I18n.format("menu.config")));
            // options
            this.aunisButtonList.add(new AunisGuiButton(0, this.width / 2 - 100, j + 72, 98, 20, I18n.format("menu.options")));
            // quit
            this.aunisButtonList.add(new AunisGuiButton(4, this.width / 2 + 2, j + 72, 98, 20, I18n.format("menu.quit")));
        }
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            // main menu screens
            case 0:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;
            case 1:
                this.mc.displayGuiScreen(new GuiWorldSelection(this));
                break;
            case 2:
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            case 4:
                this.mc.shutdown();
                break;
            case 5:
                this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
                break;
            case 6:
                this.mc.displayGuiScreen(new net.minecraftforge.fml.client.GuiModList(this));
                //this.mc.displayGuiScreen(new AunisModListMenu(this));
                break;
            case 14:
                RealmsBridge realmsbridge = new RealmsBridge();
                realmsbridge.switchToRealms(this);
                break;
            // demo world
            case 11:
                this.mc.launchIntegratedServer("Demo_World", "Demo_World", WorldServerDemo.DEMO_WORLD_SETTINGS);
                break;
            case 12:
                ISaveFormat isaveformat = this.mc.getSaveLoader();
                WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");
                if (worldinfo != null) {
                    this.mc.displayGuiScreen(new GuiYesNo(this, I18n.format("selectWorld.deleteQuestion"), "'" + worldinfo.getWorldName() + "' " + I18n.format("selectWorld.deleteWarning"), I18n.format("selectWorld.deleteButton"), I18n.format("gui.cancel"), 12));
                }
                break;
            // open wiki
            case 20:
                try{
                    Class<?> oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop").invoke((Object)null);
                    oclass.getMethod("browse", URI.class).invoke(object, new URI("https://github.com/MineDragonCZ/Aunis1/wiki"));
                }
                catch (Throwable throwable){
                    System.out.println("Couldn't open link");
                }
                break;
            // close alert
            case 21:
                showVersionAlert = 2;
                break;
            // open our discord
            case 22:
                try{
                    Class<?> oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop").invoke((Object)null);
                    oclass.getMethod("browse", URI.class).invoke(object, new URI("https://discord.gg/qU7fuNDxAs"));
                }
                catch (Throwable throwable){
                    System.out.println("Couldn't open link");
                }
                break;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            if(showVersionAlert != 1) {
                for (int i = 0; i < this.aunisButtonList.size(); ++i) {
                    GuiButton guibutton = this.aunisButtonList.get(i);

                    if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
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
            for (int i = 0; i < this.versionButtons.size(); ++i) {
                GuiButton guibutton = this.versionButtons.get(i);

                if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
                    net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, this.versionButtons);
                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                        break;
                    guibutton = event.getButton();
                    this.selectedButton = guibutton;
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(guibutton);
                    if (this.equals(this.mc.currentScreen))
                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), this.versionButtons));
                }
            }
        }

        /*if (mouseX > this.widthCopyrightRest && mouseX < this.widthCopyrightRest + this.widthCopyright && mouseY > this.height - 10 && mouseY < this.height)
        {
            this.mc.displayGuiScreen(new GuiWinGame(false, Runnables.doNothing()));
        }*/
    }
}
