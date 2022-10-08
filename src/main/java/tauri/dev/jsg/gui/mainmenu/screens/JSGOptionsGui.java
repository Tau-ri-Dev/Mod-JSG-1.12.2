package tauri.dev.jsg.gui.mainmenu.screens;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.gui.base.JSGGuiButton;
import tauri.dev.jsg.gui.base.JSGGuiSlider;
import tauri.dev.jsg.gui.mainmenu.screens.options.*;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.renderer.stargate.ChevronEnum;
import tauri.dev.jsg.sound.JSGSoundHelperClient;
import tauri.dev.jsg.sound.SoundPositionedEnum;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSGOptionsGui extends GuiOptions {

    public JSGOptionsGui(GuiScreen lastScreen, GameSettings settings, BiomeOverlayEnum overlay) {
        super(lastScreen, settings);
        this.lastScreen = lastScreen;
        this.settings = settings;
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
    protected float ringAnimationSpeed = 1.0f;
    protected final boolean speedUpGate = true;

    protected BiomeOverlayEnum overlay;
    protected float screenCenterHeight = (((float) height) / 2f);
    protected float screenCenterWidth = ((float) width) / 2f;
    protected List<GuiButton> jsgButtonList = new ArrayList<>();
    protected List<GuiButton> jsgButtonSliders = new ArrayList<>();
    protected static final ResourceLocation BACKGROUND_TEXTURE = tauri.dev.jsg.config.JSGConfig.mainMenuConfig.disableJSGMainMenu ? null : new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/background.jpg");

    // render kawoosh and event horizon
    protected float kawooshState = 0f;
    protected float gateZoom = 0.0f;
    protected float gatePos = 0.0f;

    // options vars
    private static final GameSettings.Options[] SCREEN_OPTIONS = new GameSettings.Options[] {GameSettings.Options.FOV};
    private final GuiScreen lastScreen;
    private final GameSettings settings;
    private GuiButton difficultyButton;
    private GuiLockIconButton lockButton;
    protected String title = "Options";
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
        if(!tauri.dev.jsg.config.JSGConfig.mainMenuConfig.gateRotation) return;
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
        JSGSoundHelperClient.playPositionedSoundClientSide(new BlockPos(1, 0, 0), SoundPositionedEnum.MAINMENU_MUSIC, JSGConfig.mainMenuConfig.playMusic);
        JSGSoundHelperClient.playPositionedSoundClientSide(new BlockPos(0, 0, 1), SoundPositionedEnum.MAINMENU_RING_ROLL,
                (tauri.dev.jsg.config.JSGConfig.mainMenuConfig.gateRotation && kawooshState == 0 && ringAnimationSpeed > 0.0f));
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
            GlStateManager.pushMatrix();
            for (GuiButton guiButton : this.jsgButtonList) {
                ((JSGGuiButton) guiButton).drawButton(this.mc, mouseX, mouseY, partialTicks);
            }

            for (GuiButton guiButton : this.jsgButtonSliders) {
                ((JSGGuiSlider) guiButton).drawButton(this.mc, mouseX, mouseY, partialTicks);
            }

            for (GuiLabel guiLabel : this.labelList) {
                guiLabel.drawLabel(this.mc, mouseX, mouseY);
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
    public void initGui(){
        screenCenterHeight = (((float) height) / 2f);
        screenCenterWidth = ((float) width) / 2f;

        jsgButtonList.clear();
        jsgButtonSliders.clear();
        this.title = I18n.format("options.title");
        int i = 1;
        for (GameSettings.Options gamesettings$options : SCREEN_OPTIONS)
        {
            if (gamesettings$options.isFloat()){
                this.jsgButtonSliders.add(new JSGGuiSlider(gamesettings$options.getOrdinal(), this.width / 2 + 5, this.height / 6 - 24, gamesettings$options));
            }
            ++i;
        }
        if (this.mc.world != null){
            EnumDifficulty enumdifficulty = this.mc.world.getDifficulty();
            this.difficultyButton = new JSGGuiButton(108, this.width / 2 - 155, this.height / 6 - 24, 150, 20, this.getDifficultyText(enumdifficulty));
            this.jsgButtonList.add(this.difficultyButton);

//            if (this.mc.isSingleplayer() && !this.mc.world.getWorldInfo().isHardcoreModeEnabled()){
//                this.difficultyButton.setWidth(this.difficultyButton.getButtonWidth() - 20);
//                this.lockButton = new JSGGuiLockIconButton(109, this.difficultyButton.x + this.difficultyButton.getButtonWidth(), this.difficultyButton.y);
//                this.jsgButtonList.add(this.lockButton);
//                this.lockButton.setLocked(this.mc.world.getWorldInfo().isDifficultyLocked());
//                this.lockButton.enabled = !this.lockButton.isLocked();
//                this.difficultyButton.enabled = !this.lockButton.isLocked();
//            }

            this.difficultyButton.enabled = this.mc.isSingleplayer() && !this.mc.world.getWorldInfo().isHardcoreModeEnabled();
        }
        else{
            EnumDifficulty enumdifficulty = EnumDifficulty.NORMAL;
            this.difficultyButton = new JSGGuiButton(108, this.width / 2 - 205 + i % 2 * 160, this.height / 6 - 24, 200, 20, this.getDifficultyText(enumdifficulty));
            this.jsgButtonList.add(this.difficultyButton);
            this.difficultyButton.enabled = false;
        }

        this.jsgButtonList.add(new JSGGuiButton(101, this.width / 2 - 205, this.height / 6 + 48 - 6, 410, 20, I18n.format("options.video")));
        this.jsgButtonList.add(new JSGGuiButton(106, this.width / 2 + 5, this.height / 6 + 72 - 6, 200, 20, I18n.format("options.sounds")));
        this.jsgButtonList.add(new JSGGuiButton(100, this.width / 2 - 205, this.height / 6 + 72 - 6, 200, 20, I18n.format("options.controls")));
        this.jsgButtonList.add(new JSGGuiButton(102, this.width / 2 + 5, this.height / 6 + 96 - 6, 200, 20, I18n.format("options.language")));
        this.jsgButtonList.add(new JSGGuiButton(105, this.width / 2 - 205, this.height / 6 + 96 - 6, 200, 20, I18n.format("options.resourcepack")));
        this.jsgButtonList.add(new JSGGuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException{
        if (button.enabled)
        {
            if (button.id < 100 && button instanceof GuiOptionButton)
            {
                GameSettings.Options gamesettings$options = ((GuiOptionButton)button).getOption();
                this.settings.setOptionValue(gamesettings$options, 1);
                button.displayString = this.settings.getKeyBinding(GameSettings.Options.byOrdinal(button.id));
            }

            if (button.id == 108)
            {
                this.mc.world.getWorldInfo().setDifficulty(EnumDifficulty.getDifficultyEnum(this.mc.world.getDifficulty().getDifficultyId() + 1));
                this.difficultyButton.displayString = this.getDifficultyText(this.mc.world.getDifficulty());
            }

            if (button.id == 109)
            {
                this.mc.displayGuiScreen(new GuiYesNo(this, (new TextComponentTranslation("difficulty.lock.title", new Object[0])).getFormattedText(), (new TextComponentTranslation("difficulty.lock.question", new Object[] {new TextComponentTranslation(this.mc.world.getWorldInfo().getDifficulty().getDifficultyResourceKey(), new Object[0])})).getFormattedText(), 109));
            }

            if (button.id == 110)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiCustomizeSkin(this));
            }

            if (button.id == 101)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new JSGVideoOptions(this, this.settings, overlay));
            }

            if (button.id == 100)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new JSGBindingOptions(this, this.settings, overlay));
            }

            if (button.id == 102)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new JSGLanguageOptions(this, this.settings, this.mc.getLanguageManager(), overlay));
            }

            if (button.id == 103)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new ScreenChatOptions(this, this.settings));
            }

            if (button.id == 104)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiSnooper(this, this.settings));
            }

            if (button.id == 200)
            {
                this.mc.gameSettings.saveOptions();
                this.isUnloading = true;
            }

            if (button.id == 105)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new JSGResourcePacksOptions(this, overlay));
            }

            if (button.id == 106)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new JSGAudioOptions(this, this.settings, overlay));
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException{
        if (mouseButton == 0)
        {
            for (int i = 0; i < this.jsgButtonList.size(); ++i){
                GuiButton guibutton = this.jsgButtonList.get(i);

                if (guibutton.mousePressed(this.mc, mouseX, mouseY))
                {
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
            for (int i = 0; i < this.jsgButtonSliders.size(); ++i){
                GuiButton guibutton = this.jsgButtonSliders.get(i);

                if (guibutton.mousePressed(this.mc, mouseX, mouseY))
                {
                    net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, this.jsgButtonSliders);
                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                        break;
                    guibutton = event.getButton();
                    this.selectedButton = guibutton;
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(guibutton);
                    if (this.equals(this.mc.currentScreen))
                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), this.jsgButtonSliders));
                }
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.selectedButton != null && state == 0)
        {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
    }
}
