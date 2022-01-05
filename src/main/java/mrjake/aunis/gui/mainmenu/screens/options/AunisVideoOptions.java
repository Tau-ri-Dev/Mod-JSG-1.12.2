package mrjake.aunis.gui.mainmenu.screens.options;

import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.gui.base.AunisGuiButton;
import mrjake.aunis.gui.base.AunisGuiSlider;
import mrjake.aunis.gui.base.AunisOptionButton;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.stargate.ChevronEnum;
import mrjake.aunis.sound.AunisSoundHelperClient;
import mrjake.aunis.sound.SoundPositionedEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;

public class AunisVideoOptions extends GuiVideoSettings {

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

    protected final GuiScreen parent;

    protected BiomeOverlayEnum overlay;
    protected float screenCenterHeight = (((float) height) / 2f);
    protected float screenCenterWidth = ((float) width) / 2f;
    protected java.util.List<GuiButton> aunisButtonList = new ArrayList<>();
    protected java.util.List<GuiButton> aunisButtonSliders = new ArrayList<>();
    protected static final ResourceLocation BACKGROUND_TEXTURE =
            AunisConfig.mainMenuConfig.disableAunisMainMenu ? null :
                    new ResourceLocation(Aunis.ModID, "textures/gui/mainmenu/background.jpg");

    // render kawoosh and event horizon
    protected float kawooshState = 0f;
    protected float gateZoom = 0.0f;
    protected float gatePos = 0.0f;
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
    protected String title = "Video Settings";
    protected final GameSettings guiGameSettings;
    protected static final GameSettings.Options[] VIDEO_OPTIONS = new GameSettings.Options[]{
            GameSettings.Options.GRAPHICS,
            GameSettings.Options.RENDER_DISTANCE,
            GameSettings.Options.AMBIENT_OCCLUSION,
            GameSettings.Options.FRAMERATE_LIMIT,
            GameSettings.Options.ANAGLYPH,
            GameSettings.Options.VIEW_BOBBING,
            GameSettings.Options.GUI_SCALE,
            GameSettings.Options.ATTACK_INDICATOR,
            GameSettings.Options.GAMMA,
            GameSettings.Options.RENDER_CLOUDS,
            GameSettings.Options.PARTICLES,
            GameSettings.Options.USE_FULLSCREEN,
            GameSettings.Options.ENABLE_VSYNC,
            GameSettings.Options.MIPMAP_LEVELS,
            GameSettings.Options.USE_VBO,
            GameSettings.Options.ENTITY_SHADOWS
    };
    private int eventButton;
    private long lastMouseEvent;
    private int touchValue;

    public AunisVideoOptions(GuiScreen parentScreenIn, GameSettings gameSettingsIn, BiomeOverlayEnum overlay) {
        super(parentScreenIn, gameSettingsIn);
        this.parent = parentScreenIn;
        this.guiGameSettings = gameSettingsIn;
        this.overlay = overlay;
    }

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

        if(gateZoom == 0.0f) gateZoom = this.width / 6f;
        if(gatePos == 0.0f) gatePos = (this.width - 54f);

        float step = 8f;
        if(!isUnloading) {
            if (this.gatePos < 54f) this.gatePos += step * 4f;

            if (this.gatePos > 57f) this.gatePos -= step * 4f;
        }
        else{
            if (this.gatePos < (this.width - 54f)) this.gatePos += step * 4f;

            if (this.gatePos+((step*4) + 25) >= (this.width - 54f)) this.mc.displayGuiScreen(this.parent);
        }
        if (animationStage > 360) animationStage = 0f;
        updateRingSpeed();
    }

    // RENDER GUI
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
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
            this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 15, 16777215);
            GlStateManager.disableTexture2D();
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

            GlStateManager.popMatrix();
        }
    }

    @Override
    public void initGui(){
        title = I18n.format("options.videoTitle");
        aunisButtonList.clear();
        aunisButtonSliders.clear();
        aunisButtonList.add(new AunisGuiButton(200, this.width / 2 - 100, this.height - 27, I18n.format("gui.done")));

        int i = 0;
        int o;
        for (GameSettings.Options gamesettings$options : VIDEO_OPTIONS) {
            if(i % 2 == 0) o = 5;
            else o = -205;

            int x = this.width/2 + o;
            int y = this.height / 6 - 12 + 24 * (i >> 1);
            if (gamesettings$options.isFloat())
                aunisButtonSliders.add(new AunisGuiSlider(gamesettings$options.getOrdinal(), x, y, gamesettings$options));
            else
                aunisButtonList.add(new AunisOptionButton(gamesettings$options.getOrdinal() + 400, x, y, gamesettings$options, Minecraft.getMinecraft().gameSettings.getKeyBinding(gamesettings$options)));
            i++;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        int y = this.guiGameSettings.guiScale;
        if (mouseButton == 0){
            for (int i = 0; i < this.aunisButtonList.size(); ++i){
                GuiButton guibutton = this.aunisButtonList.get(i);

                if (guibutton.mousePressed(this.mc, mouseX, mouseY)){
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

            for (int i = 0; i < this.aunisButtonSliders.size(); ++i){
                GuiButton guibutton = this.aunisButtonSliders.get(i);

                if (guibutton.mousePressed(this.mc, mouseX, mouseY)){
                    net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, this.aunisButtonSliders);
                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                        break;
                    guibutton = event.getButton();
                    this.selectedButton = guibutton;
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(guibutton);
                    if (this.equals(this.mc.currentScreen))
                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), this.aunisButtonSliders));
                }
            }
        }
        if (this.guiGameSettings.guiScale != y){
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            int j = scaledresolution.getScaledWidth();
            int k = scaledresolution.getScaledHeight();
            this.setWorldAndResolution(this.mc, j, k);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        int i = this.guiGameSettings.guiScale;
        if (this.selectedButton != null && state == 0)
        {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }

        if (this.guiGameSettings.guiScale != i)
        {
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            int j = scaledresolution.getScaledWidth();
            int k = scaledresolution.getScaledHeight();
            this.setWorldAndResolution(this.mc, j, k);
        }
    }

    @Override
    public void onGuiClosed()
    {
        this.mc.gameSettings.onGuiClosed();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled){
            if (button.id == 200){
                this.mc.gameSettings.saveOptions();
                isUnloading = true;
            }
            if(button.id >= 400){
                this.mc.gameSettings.setOptionValue(((AunisOptionButton) button).getOption(), 1);
                button.displayString = this.mc.gameSettings.getKeyBinding(((AunisOptionButton) button).getOption());
                this.mc.gameSettings.saveOptions();
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
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
    }
}
