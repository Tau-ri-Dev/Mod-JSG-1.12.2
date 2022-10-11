package tauri.dev.jsg.gui.mainmenu;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.gui.base.JSGGuiButton;
import tauri.dev.jsg.gui.mainmenu.screens.JSGModListGui;
import tauri.dev.jsg.gui.mainmenu.screens.JSGOptionsGui;
import tauri.dev.jsg.gui.mainmenu.screens.options.JSGLanguageOptions;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.renderer.stargate.ChevronEnum;
import tauri.dev.jsg.renderer.stargate.StargateRendererStatic;
import tauri.dev.jsg.sound.JSGSoundHelperClient;
import tauri.dev.jsg.sound.SoundPositionedEnum;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static tauri.dev.jsg.gui.mainmenu.GetUpdate.*;

@SideOnly(Side.CLIENT)
public class JSGMainMenu extends GuiMainMenu {

    // ------------------------------------------
    // DEFINE VARIABLES

    protected static final String WIKI_URL = "https://justsgmod.eu/wiki/";

    // ---------------------------------------------------
    // VERSION
    protected static final String VERSION = JSG.MOD_VERSION.replace("1.12.2-", "");

    // ---------------------------------------------------

    protected static float animationStage = 0;
    protected static final float ringAnimationStepSetting = 0.3f;
    protected static float ringAnimationStep = 0.0f;
    protected static float ringAnimationSpeed = 1.0f;
    protected static boolean speedUpGate = true;

    protected float chevronLastAnimationStage = 0;
    protected boolean chevronsActive = false;
    protected boolean chevronShout = false;
    protected boolean chevronShoutColapsing = false;
    protected int chevronShoutTiming = 0;
    protected static final int chevronShoutTimingSetting = 16;
    protected boolean chevronSound1 = false;
    protected boolean chevronSound2 = false;
    protected boolean chevronSound3 = false;
    protected BiomeOverlayEnum[] overlays = {
            BiomeOverlayEnum.AGED,
            BiomeOverlayEnum.FROST,
            BiomeOverlayEnum.MOSSY,
            BiomeOverlayEnum.SOOTY,
            BiomeOverlayEnum.NORMAL
    };
    protected BiomeOverlayEnum overlay = getNextBiomeOverlay(tauri.dev.jsg.config.JSGConfig.mainMenuConfig.changingGateOverlay, false);
    protected float screenCenterHeight = (((float) height) / 2f);
    protected float screenCenterWidth = ((float) width) / 2f;
    protected List<GuiButton> jsgButtonList = new ArrayList<>();
    protected List<GuiButton> versionButtons = new ArrayList<>();
    protected List<GuiButton> afterDownloadButtons = new ArrayList<>();
    protected List<GuiButton> anyButton = new ArrayList<>();
    protected static final ResourceLocation BACKGROUND_TEXTURE = tauri.dev.jsg.config.JSGConfig.mainMenuConfig.disableJSGMainMenu ? null : new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/background.jpg");
    protected static ResourceLocation EVENT_HORIZON_TEXTURE = new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/event_horizon.jpg");

    protected static final String LATEST = checkForUpdate(VERSION);
    protected static int showVersionAlert = 0;
    protected String nextEvent = "------";

    // render kawoosh and event horizon
    protected boolean renderKawoosh = false;
    protected boolean renderButtonsAndStuff = true;
    protected float renderButtonsAlpha = 0.0f;
    protected float kawooshState = 0f;
    protected float gateZoom = 1f;
    protected int clickedButton = 0;
    static StargateRendererStatic.InnerCircle innerCircle = new StargateRendererStatic.InnerCircle();
    static List<StargateRendererStatic.QuadStrip> quadStrips = new ArrayList<StargateRendererStatic.QuadStrip>();


    // DOWNLOADING FILE
    protected double percentDownloaded = 0.0;

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
    public void deleteEvent() {
        nextEvent = "------";
    }

    // animation of top chevron
    public void updateLastChevron() {
        if (chevronShout) {
            nextEvent = "Animate chevron";
            if (!chevronSound3) {
                JSGSoundHelperClient.playPositionedSoundClientSide(new BlockPos(0, 2, 0), SoundPositionedEnum.MAINMENU_CHEVRON_SHUT, true);
                chevronSound3 = true;
            }
            if (this.chevronLastAnimationStage >= 0.15f || this.chevronShoutColapsing) {
                // chevron is going up (after timings)
                if (chevronShoutTiming > chevronShoutTimingSetting) {
                    this.chevronShoutColapsing = true;
                    this.chevronLastAnimationStage -= 0.020f;
                    nextEvent = "Top chevron up";
                } else if (chevronShoutTiming == chevronShoutTimingSetting / 2) {
                    if (!chevronSound1) {
                        JSGSoundHelperClient.playPositionedSoundClientSide(new BlockPos(0, 2, 0), SoundPositionedEnum.MAINMENU_CHEVRON_OPEN, true);
                        chevronSound1 = true;
                        nextEvent = "Top chevron waiting";
                    }
                    chevronShoutTiming++;
                } else chevronShoutTiming++;
            } else {
                // chevron is going down
                this.chevronLastAnimationStage += 0.020f;
                nextEvent = "Top chevron down";
            }

            if (this.chevronLastAnimationStage < 0) {
                if (!chevronSound2) {
                    JSGSoundHelperClient.playPositionedSoundClientSide(new BlockPos(1, 2, 0), SoundPositionedEnum.MAINMENU_CHEVRON_SHUT, true);
                    chevronSound2 = true;
                }
                nextEvent = "Top chevron reset";
                if (!chevronsActive) getNextBiomeOverlay(tauri.dev.jsg.config.JSGConfig.mainMenuConfig.changingGateOverlay, true);
                this.chevronShoutTiming = 0;
                this.chevronLastAnimationStage = 0;
                this.chevronShout = false;
                this.chevronSound1 = false;
                this.chevronSound2 = false;
                this.chevronSound3 = false;
                this.chevronShoutColapsing = false;
                if (kawooshState == 0) speedUpGate = true;
                deleteEvent();
            }
        }
    }

    // slow down and speed up gate ring
    public void updateRingSpeed() {
        if (!tauri.dev.jsg.config.JSGConfig.mainMenuConfig.gateRotation) return;
        if (!speedUpGate) {
            if (ringAnimationSpeed > 0.00f) ringAnimationSpeed -= 0.02f;
            if (ringAnimationSpeed < 0.00f) ringAnimationSpeed = 0.0f;
            if (ringAnimationSpeed < 0.05f && kawooshState == 0 && !chevronShout) {
                chevronShout = true;
            }
        } else {
            if (ringAnimationSpeed < 1.0f) ringAnimationSpeed += 0.02f;
        }
        ringAnimationStep = ringAnimationStepSetting * ringAnimationSpeed;
        animationStage += ringAnimationStep;
    }

    // next overlay
    public BiomeOverlayEnum getNextBiomeOverlay(boolean doIt, boolean updateGui) {
        nextEvent = "Changing overlay";
        BiomeOverlayEnum over = this.overlay;
        if (doIt) {
            while(over == overlay)
                over = overlays[new Random().nextInt(overlays.length)];
            this.overlay = over;
        }
        else if(this.overlay == null)
            this.overlay = BiomeOverlayEnum.NORMAL;

        if(updateGui) initGui();
        return this.overlay;
    }

    // play sound
    public void updateSound() {

        if (this.mc.gameSettings.getKeyBinding(GameSettings.Options.AUTO_JUMP).equals("Auto-Jump: ON"))
            this.mc.gameSettings.setOptionValue(GameSettings.Options.AUTO_JUMP, 1);

        JSGSoundHelperClient.playPositionedSoundClientSide(new BlockPos(1, 0, 0), SoundPositionedEnum.MAINMENU_MUSIC,
                tauri.dev.jsg.config.JSGConfig.mainMenuConfig.playMusic);
        JSGSoundHelperClient.playPositionedSoundClientSide(new BlockPos(0, 0, 1), SoundPositionedEnum.MAINMENU_RING_ROLL,
                (tauri.dev.jsg.config.JSGConfig.mainMenuConfig.gateRotation && kawooshState == 0 && ringAnimationSpeed > 0.0f));
    }

    // update ring rotation and overlay
    public void updateAnimation() {
        if (animationStage > 360) animationStage = 0f;
        if ((animationStage > 352 && animationStage < 353) || (animationStage > 172 && animationStage < 173))
            speedUpGate = false;
        updateRingSpeed();
    }

    // EVENT HORIZON RENDER
    public void loadGame() {
        speedUpGate = false;
        if (ringAnimationSpeed <= 0.05f) {
            renderButtonsAndStuff = false;
            chevronsActive = true;
            float step = 0.008f;
            if (kawooshState == 0) {
                // disable spin and start opening
                nextEvent = "Switching screens...";
                kawooshState += (step);
                JSGSoundHelperClient.playPositionedSoundClientSide(new BlockPos(0, 0, 1), SoundPositionedEnum.MAINMENU_RING_ROLL, false);
            } else if (kawooshState < 0.51f) kawooshState += (step);
            else if (kawooshState > 0.5f && kawooshState < 0.81f) {
                // making kawoosh
                kawooshState += step;

                renderEventHorizon(true);

                switch (clickedButton) {
                    case 1:
                        nextEvent = "Switching to singleplayer...";
                        break;
                    case 2:
                        nextEvent = "Switching to multiplayer...";
                        break;
                    case 4:
                        nextEvent = "Shutting down minecraft client...";
                        break;
                    default:
                        nextEvent = "Button not recognized! (" + clickedButton + ")";
                        break;
                }

                JSGSoundHelperClient.playPositionedSoundClientSide(new BlockPos(0, 0, 0), SoundPositionedEnum.MAINMENU_GATE_OPEN, true);
            } else if (kawooshState > 0.8f && kawooshState < 1.61f) {
                // render event horizon
                JSGSoundHelperClient.playPositionedSoundClientSide(new BlockPos(1, 0, 1), SoundPositionedEnum.WORMHOLE_LOOP, true);

                renderEventHorizon(false);

                kawooshState += (step + 0.008f);
                gateZoom += 0.10f;
            } else if (kawooshState > 1.6f && kawooshState < 2.0f) {
                // going to gate and still render horizon
                renderEventHorizon(false);

                kawooshState += step;
                gateZoom += (gateZoom * 2) / 10;
            } else if (kawooshState > 1.99f && kawooshState < 2.2) {
                // turn off everything and render gui

                switch (clickedButton) {
                    case 1:
                        //this.mc.displayGuiScreen(new JSGSinglePlayerGui(this, overlay));
                        this.mc.displayGuiScreen(new GuiWorldSelection(this));
                        break;
                    case 2:
                        //this.mc.displayGuiScreen(new JSGMultiPlayerGui(this, overlay));
                        this.mc.displayGuiScreen(new GuiMultiplayer(this));
                        break;
                    case 4:
                        this.mc.shutdown();
                        break;
                    default:
                        JSG.logger.error("Wrong button clicked!!! This is a bug! (" + clickedButton + ")");
                        break;
                }

                kawooshState = 2.2f;
                renderKawoosh = false;
                JSGSoundHelperClient.playPositionedSoundClientSide(new BlockPos(1, 0, 1), SoundPositionedEnum.WORMHOLE_LOOP, false);
                JSGSoundHelperClient.playPositionedSoundClientSide(new BlockPos(0, 0, 0), SoundPositionedEnum.MAINMENU_GATE_GO, true);
                renderButtonsAndStuff = true;
                clickedButton = 0;
            }
            else if(kawooshState == 2.2f) kawooshState = 0;
        }
    }

    // EVENT HORIZON RENDER
    public void renderEventHorizon(boolean doKawoosh) {

        if (!doKawoosh) {
            quadStrips.clear();
            for (int i = 0; i < 16; i++) {
                quadStrips.add(new StargateRendererStatic.QuadStrip(i));
            }
            GlStateManager.pushMatrix();
            this.mc.getTextureManager().bindTexture(EVENT_HORIZON_TEXTURE);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            innerCircle.render(1.0f, false, 1.0f, 0, (byte) -1);
            for (StargateRendererStatic.QuadStrip strip : quadStrips) {
                strip.render(1.0f, false, 1.0f, 0, (byte) -1);
            }
            GlStateManager.popMatrix();
        } else {
            quadStrips.clear();
            for (int i = 0; i < 16; i++) {
                quadStrips.add(new StargateRendererStatic.QuadStrip(i));
            }
            GlStateManager.pushMatrix();
            this.mc.getTextureManager().bindTexture(EVENT_HORIZON_TEXTURE);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            innerCircle.render(1.0f, true, 1.0f, 0, (byte) -1);
            for (StargateRendererStatic.QuadStrip strip : quadStrips) {
                strip.render(1.0f, true, 1.0f, 0, (byte) -1);
            }
            GlStateManager.popMatrix();
        }
    }

    public void checkDownloaded(){
        if(showVersionAlert != 4) return;
        percentDownloaded = getPercents();
        if(percentDownloaded < 0) percentDownloaded = 0;
        if(percentDownloaded > 100) percentDownloaded = 100;
    }

    // RENDER MAIN MENU
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.screenCenterHeight = (((float) height) / 2f);
        this.screenCenterWidth = ((float) width) / 2f;

        checkDownloaded();

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
        GlStateManager.scale(gateZoom + ((height / 10f) - 3), gateZoom + ((height / 10f) - 3), gateZoom + ((height / 10f) - 3));
        GlStateManager.rotate(-180f, 0f, 0f, 1f);
        GlStateManager.rotate(180f, 0f, 1f, 0f);

        // ------------------------------
        // DRAWING EVENT HORIZON

        if (renderKawoosh) loadGame();
        else{
            kawooshState = 0;
            gateZoom = 1f;
            chevronsActive = false;
        }

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
                if (i == 8 && ((chevronShoutTiming > (chevronShoutTimingSetting / 2)) && chevronShout)) {
                    ElementEnum.MILKYWAY_CHEVRON_LIGHT_ACTIVE_MAINMENU.bindTextureAndRender(this.overlay);
                    GlStateManager.translate(0, -2 * chevronOffset, 0);
                    ElementEnum.MILKYWAY_CHEVRON_MOVING_ACTIVE_MAINMENU.bindTextureAndRender(this.overlay);
                } else {
                    ElementEnum.MILKYWAY_CHEVRON_LIGHT_MAINMENU.bindTextureAndRender(this.overlay);
                    GlStateManager.translate(0, -2 * chevronOffset, 0);
                    ElementEnum.MILKYWAY_CHEVRON_MOVING_MAINMENU.bindTextureAndRender(this.overlay);
                }
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

        String versionInfo = "JSG version: " + VERSION;

        if (renderButtonsAndStuff) {
            if (renderButtonsAlpha < 1.0f && showVersionAlert != 1) renderButtonsAlpha += 0.05f;

            if (!LATEST.equals("false") && !LATEST.equals(ERROR_STRING) && tauri.dev.jsg.config.JSGConfig.enableAutoUpdater) {
                versionInfo += " - Latest build: " + LATEST;
                if (showVersionAlert != 2 && showVersionAlert != 4) showVersionAlert = 1;
            }

            GlStateManager.pushMatrix();
            GlStateManager.enableTexture2D();
            GlStateManager.translate(6, (((float) height) - 46f), 0);
            GlStateManager.scale(0.8, 0.8, 0.8);
            GlStateManager.translate(0, 0, 0);
            drawString(fontRenderer, "Music credits: STARGATE SG-1 - Full Original Soundtrack OST", 0, 0, 0xffffff);
            GlStateManager.translate(0, 10, 0);
            drawString(fontRenderer, "Just Stargate Mod by: MineDragonCZ_ and Matousss", 0, 0, 0xffffff);
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

            GlStateManager.pushMatrix();
            GlStateManager.enableTexture2D();
            GlStateManager.translate(screenCenterWidth, 48, 0);
            GlStateManager.scale(4, 4, 4);
            drawCenteredString(fontRenderer, "JSG Mod", 0, 0, 0xffffff);
            GlStateManager.disableTexture2D();
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            GlStateManager.enableTexture2D();
            GlStateManager.translate(screenCenterWidth, 81, 0);
            GlStateManager.scale(1.5f, 1.5f, 1.5f);
            drawCenteredString(fontRenderer, "Just Stargate Mod", 0, 0, 0xa4a4a4);
            GlStateManager.disableTexture2D();
            GlStateManager.popMatrix();

            // ------------------------------
            // DRAWING BUTTONS

            for (GuiButton guiButton : this.jsgButtonList) {
                ((JSGGuiButton) guiButton).drawButton(this.mc, mouseX, mouseY, partialTicks);
            }

            for (GuiLabel guiLabel : this.labelList) {
                guiLabel.drawLabel(this.mc, mouseX, mouseY);
            }

            GlStateManager.popMatrix();

            // ------------------------------
            // DRAWING VERSION ALERT

            if (showVersionAlert == 1) {
                GlStateManager.pushMatrix();

                GlStateManager.pushMatrix();
                frame(0, 0, width, height, 3, 0xFF181A1F, 0xFF272B33);
                GlStateManager.popMatrix();

                GlStateManager.pushMatrix();

                GlStateManager.pushMatrix();
                GlStateManager.translate(screenCenterWidth, screenCenterHeight - 60, 0);
                GlStateManager.scale(1.5, 1.5, 1.5);

                if (!LATEST.equals(ERROR_STRING)){
                    drawCenteredString(fontRenderer, "You are using out of date version of JSG mod.", 0, 0, 0xffffff);
                    GlStateManager.popMatrix();

                    GlStateManager.pushMatrix();
                    GlStateManager.scale(1.0, 1.0, 1.0);
                    GlStateManager.translate(screenCenterWidth, screenCenterHeight - 40, 0);
                    drawCenteredString(fontRenderer, "For your comfort, please update", 0, 0, 0xffffff);
                    GlStateManager.translate(0, 10, 0);
                    drawCenteredString(fontRenderer, "your mod in your mods folder.", 0, 0, 0xffffff);
                }


                GlStateManager.popMatrix();

                GlStateManager.popMatrix();

                for (GuiButton guiButton : this.versionButtons) {
                    ((JSGGuiButton) guiButton).drawButton(this.mc, mouseX, mouseY, partialTicks);
                }

                GlStateManager.popMatrix();
            }

            // ------------------------------
            // DRAWING DOWNLOADING ALERT

            if (showVersionAlert == 4) {
                if(percentDownloaded >= 100) {
                    GlStateManager.pushMatrix();

                    GlStateManager.pushMatrix();
                    frame(0, 0, width, height, 3, 0xFF181A1F, 0xFF272B33);
                    GlStateManager.popMatrix();

                    GlStateManager.pushMatrix();

                    GlStateManager.pushMatrix();
                    GlStateManager.translate(screenCenterWidth, screenCenterHeight - 60, 0);
                    GlStateManager.scale(1.5, 1.5, 1.5);

                    drawCenteredString(fontRenderer, "Downloading JSG", 0, 0, 0xffffff);
                    GlStateManager.popMatrix();

                    GlStateManager.pushMatrix();
                    GlStateManager.scale(1.0, 1.0, 1.0);
                    GlStateManager.translate(screenCenterWidth, screenCenterHeight - 40, 0);
                    drawCenteredString(fontRenderer, "Downloading completed", 0, 0, 0xffffff);
                    GlStateManager.translate(0, 20, 0);
                    drawCenteredString(fontRenderer, "Please restart minecraft:", 0, 0, 0xffffff);

                    GlStateManager.popMatrix();

                    GlStateManager.popMatrix();

                    for (GuiButton guiButton : this.afterDownloadButtons) {
                        ((JSGGuiButton) guiButton).drawButton(this.mc, mouseX, mouseY, partialTicks);
                    }
                }
                else{
                    GlStateManager.pushMatrix();

                    GlStateManager.pushMatrix();
                    frame(0, 0, width, height, 3, 0xFF181A1F, 0xFF272B33);
                    GlStateManager.popMatrix();

                    GlStateManager.pushMatrix();

                    GlStateManager.pushMatrix();
                    GlStateManager.translate(screenCenterWidth, screenCenterHeight - 60, 0);
                    GlStateManager.scale(1.5, 1.5, 1.5);

                    drawCenteredString(fontRenderer, "Downloading JSG", 0, 0, 0xffffff);
                    GlStateManager.popMatrix();

                    GlStateManager.pushMatrix();
                    GlStateManager.scale(1.0, 1.0, 1.0);
                    GlStateManager.translate(screenCenterWidth, screenCenterHeight - 40, 0);
                    drawCenteredString(fontRenderer, "Please do not quit minecraft", 0, 0, 0xffffff);
                    GlStateManager.translate(0, 10, 0);
                    drawCenteredString(fontRenderer, "before JSG installs.", 0, 0, 0xffffff);
                    GlStateManager.translate(0, 10, 0);
                    drawCenteredString(fontRenderer, "This process take usually about 5 minutes...", 0, 0, 0xffffff);
                    GlStateManager.translate(0, 20, 0);
                    drawCenteredString(fontRenderer, "Downloaded: " + percentDownloaded + "%", 0, 0, 0xffffff);

                    GlStateManager.popMatrix();

                    GlStateManager.popMatrix();
                }

                GlStateManager.popMatrix();
            }
        }

        // ------------------------------
        // DRAWING DEBUG INFO

        if (tauri.dev.jsg.config.JSGConfig.mainMenuConfig.debugMode) {
            GlStateManager.pushMatrix();
            GlStateManager.enableTexture2D();
            GlStateManager.translate(6, screenCenterHeight - 30, 0);
            GlStateManager.scale(0.6, 0.6, 0.6);
            GlStateManager.translate(0, 0, 0);
            drawString(fontRenderer, "Ring rotation: " + animationStage, 0, 0, 0xffffff);
            GlStateManager.translate(0, 10, 0);
            drawString(fontRenderer, "Ring speed: " + ringAnimationStep + "deg/tick", 0, 0, 0xffffff);
            GlStateManager.translate(0, 10, 0);
            drawString(fontRenderer, "Screen resolution: " + width + "px x " + height + "px", 0, 0, 0xffffff);
            GlStateManager.translate(0, 10, 0);
            drawString(fontRenderer, "Screen center: " + screenCenterWidth + "px x " + screenCenterHeight + "px", 0, 0, 0xffffff);
            GlStateManager.translate(0, 10, 0);
            drawString(fontRenderer, "Version alert state: " + showVersionAlert, 0, 0, 0xffffff);
            GlStateManager.translate(0, 10, 0);
            drawString(fontRenderer, "Top chevron position: " + chevronLastAnimationStage, 0, 0, 0xffffff);
            GlStateManager.translate(0, 10, 0);
            drawString(fontRenderer, "Active overlay: " + overlay.name(), 0, 0, 0xffffff);
            GlStateManager.translate(0, 10, 0);
            drawString(fontRenderer, "Chevrons active? " + chevronsActive, 0, 0, 0xffffff);
            GlStateManager.translate(0, 10, 0);
            drawString(fontRenderer, "Event horizon state: " + kawooshState, 0, 0, 0xffffff);
            GlStateManager.translate(0, 10, 0);
            drawString(fontRenderer, "Next event: " + nextEvent, 0, 0, 0xffffff);
            GlStateManager.disableTexture2D();
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
        this.versionButtons.add(new JSGGuiButton(21, width / 2 - 100, height - 75, 98, 20, "OK"));
        this.versionButtons.add(new JSGGuiButton(22, width / 2 + 2, height - 75, 98, 20, "Download"));

        this.afterDownloadButtons.clear();
        this.afterDownloadButtons.add(new JSGGuiButton(23, width / 2 - 50, height - 75, 100, 20, I18n.format("menu.quit")));

        this.jsgButtonList.clear();
        int j = this.height / 4 + 48;

        if (!tauri.dev.jsg.config.JSGConfig.mainMenuConfig.disablePosButtons) {
            // single
            this.jsgButtonList.add(new JSGGuiButton(1, this.width / 2 - 100, j, I18n.format("menu.singleplayer")));
            // multi
            this.jsgButtonList.add(new JSGGuiButton(2, this.width / 2 - 100, j + 24, I18n.format("menu.multiplayer")));
            // about mod
            this.jsgButtonList.add(new JSGGuiButton(20, this.width / 2 - 100, j + 48, I18n.format("menu.about")));
            // mods list
            this.jsgButtonList.add(new JSGGuiButton(6, this.width - 6 - 98, 6, 98, 20, I18n.format("fml.menu.mods")));
            // jsg config
            //this.jsgButtonList.add(new JSGGuiButton(7, this.width - 6 - 98, 9 + 20, 98, 20, I18n.format("menu.config")));
            // options
            this.jsgButtonList.add(new JSGGuiButton(0, 6, 6, 98, 20, I18n.format("menu.options")));
            // quit
            this.jsgButtonList.add(new JSGGuiButton(4, this.width - 6 - 98, this.height - 6 - 20, 98, 20, I18n.format("menu.quit")));
            // change style of gate
            this.jsgButtonList.add(new JSGGuiButton(61, this.width - 6 - 98, this.height - 6 - 45, 98, 20, I18n.format("menu.gate_overlay") + ": " + overlay.name()));
            // toggle main menu music
            this.jsgButtonList.add(new JSGGuiButton(62, this.width - 6 - 98, this.height - 6 - 70, 98, 20, I18n.format("menu.music_toggle") + ": " + tauri.dev.jsg.config.JSGConfig.mainMenuConfig.playMusic));
        } else {
            // single
            this.jsgButtonList.add(new JSGGuiButton(1, this.width / 2 - 100, j, I18n.format("menu.singleplayer")));
            // multi
            this.jsgButtonList.add(new JSGGuiButton(2, this.width / 2 - 100, j + 24, I18n.format("menu.multiplayer")));
            // about mod
            this.jsgButtonList.add(new JSGGuiButton(20, this.width / 2 - 100, j + 48, 98, 20, I18n.format("menu.about")));
            // mods list
            this.jsgButtonList.add(new JSGGuiButton(6, this.width / 2 + 2, j + 48, 98, 20, I18n.format("fml.menu.mods")));
            // jsg config
            //this.jsgButtonList.add(new JSGGuiButton(7, this.width - 6 - 98, 6, 98, 20, I18n.format("menu.config")));
            // options
            this.jsgButtonList.add(new JSGGuiButton(0, this.width / 2 - 100, j + 72, 98, 20, I18n.format("menu.options")));
            // quit
            this.jsgButtonList.add(new JSGGuiButton(4, this.width / 2 + 2, j + 72, 98, 20, I18n.format("menu.quit")));
            // change style of gate
            this.jsgButtonList.add(new JSGGuiButton(61, this.width - 6 - 98, this.height - 6 - 20, 98, 20, I18n.format("menu.gate_overlay") + ": " + overlay.name()));
            // toggle main menu music
            this.jsgButtonList.add(new JSGGuiButton(62, this.width - 6 - 98, this.height - 6 - 45, 98, 20, I18n.format("menu.music_toggle") + ": " + JSGConfig.mainMenuConfig.playMusic));
        }
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            // main menu screens
            case 0:
                this.mc.displayGuiScreen(new JSGOptionsGui(this, this.mc.gameSettings, overlay));
                break;
            case 1:
                if (tauri.dev.jsg.config.JSGConfig.mainMenuConfig.enableEventHorizon) {
                    clickedButton = 1;
                    renderKawoosh = true;
                } else this.mc.displayGuiScreen(new GuiWorldSelection(this));
                break;
            case 2:
                if (tauri.dev.jsg.config.JSGConfig.mainMenuConfig.enableEventHorizon) {
                    clickedButton = 2;
                    renderKawoosh = true;
                } else this.mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            case 4:
                if (tauri.dev.jsg.config.JSGConfig.mainMenuConfig.enableEventHorizon) {
                    clickedButton = 4;
                    renderKawoosh = true;
                } else this.mc.shutdown();
                break;
            case 5:
                this.mc.displayGuiScreen(new JSGLanguageOptions(this, this.mc.gameSettings, this.mc.getLanguageManager(), overlay));
                break;
            case 6:
                this.mc.displayGuiScreen(new JSGModListGui(this, overlay));
                break;
            // open wiki
            case 20:
                try {
                    Class<?> oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop").invoke((Object) null);
                    oclass.getMethod("browse", URI.class).invoke(object, new URI(WIKI_URL));
                } catch (Exception e) {
                    JSG.logger.debug("Couldn't open link", e);
                }
                break;
            // close alert
            case 21:
                showVersionAlert = 2;
                break;
            // open download link
            case 22:
                if(updateMod(VERSION, "-alpha")){
                    JSG.warn("Updating JSG automatically...");
                    showVersionAlert = 4;
                    break;
                }
                JSG.warn("Could not update JSG automatically! Opening website...");
                try {
                    Class<?> oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop").invoke((Object) null);
                    oclass.getMethod("browse", URI.class).invoke(object, new URI(getSiteContent(GET_DOWNLOAD_URL)));
                } catch (Exception e) {
                    JSG.logger.debug("Couldn't open link", e);
                }
                break;
            case 23:
                this.mc.shutdown();
            // change overlay
            case 61:
                overlay = getNextBiomeOverlay(true, true);
                initGui();
                break;
            // toggle music
            case 62:
                tauri.dev.jsg.config.JSGConfig.mainMenuConfig.playMusic = !tauri.dev.jsg.config.JSGConfig.mainMenuConfig.playMusic;
                initGui();
                break;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0 && renderButtonsAndStuff) {
            if (showVersionAlert != 1 && showVersionAlert != 4) {
                for (int i = 0; i < this.jsgButtonList.size(); ++i) {
                    GuiButton guibutton = this.jsgButtonList.get(i);

                    if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
                        net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, this.jsgButtonList);
                        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                            break;
                        guibutton = event.getButton();
                        this.selectedButton = guibutton;
                        guibutton.playPressSound(this.mc.getSoundHandler());
                        this.actionPerformed(guibutton);
                        if (this.equals(this.mc.currentScreen))
                            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), this.jsgButtonList));
                    }
                }
            }
            if (showVersionAlert == 1) {
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
            if (showVersionAlert == 4 && percentDownloaded >= 100) {
                for (int i = 0; i < this.afterDownloadButtons.size(); ++i) {
                    GuiButton guibutton = this.afterDownloadButtons.get(i);

                    if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
                        net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, this.afterDownloadButtons);
                        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                            break;
                        guibutton = event.getButton();
                        this.selectedButton = guibutton;
                        guibutton.playPressSound(this.mc.getSoundHandler());
                        this.actionPerformed(guibutton);
                        if (this.equals(this.mc.currentScreen))
                            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), this.afterDownloadButtons));
                    }
                }
            }
        }
    }
}
