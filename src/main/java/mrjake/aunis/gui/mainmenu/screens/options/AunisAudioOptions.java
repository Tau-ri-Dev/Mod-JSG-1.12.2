package mrjake.aunis.gui.mainmenu.screens.options;

import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.gui.AunisGuiButton;
import mrjake.aunis.gui.AunisGuiSlider;
import mrjake.aunis.gui.AunisOptionButton;
import mrjake.aunis.gui.GuiBase;
import mrjake.aunis.gui.AunisGuiSliderSounds;
import mrjake.aunis.gui.mainmenu.screens.resourcepacks.AunisGuiResourcePackSelected;
import mrjake.aunis.gui.mainmenu.screens.resourcepacks.AunisGuiResourcePacksAvailable;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.stargate.ChevronEnum;
import mrjake.aunis.sound.AunisSoundHelperClient;
import mrjake.aunis.sound.SoundPositionedEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.ArrayList;

public class AunisAudioOptions extends GuiScreenOptionsSounds {

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

    protected AunisLanguageOptions.List list;

    protected final GuiScreen parent;
    protected java.util.List<ResourcePackListEntry> availableResourcePacks;
    protected java.util.List<ResourcePackListEntry> selectedResourcePacks;
    protected AunisGuiResourcePacksAvailable availableResourcePacksList;
    protected AunisGuiResourcePackSelected selectedResourcePacksList;
    protected boolean changed;

    protected BiomeOverlayEnum overlay;
    protected float screenCenterHeight = (((float) height) / 2f);
    protected float screenCenterWidth = ((float) width) / 2f;
    protected java.util.List<GuiButton> aunisButtonList = new ArrayList<>();
    protected java.util.List<GuiButton> aunisButtonSliders = new ArrayList<>();
    protected static final ResourceLocation BACKGROUND_TEXTURE = AunisConfig.mainMenuConfig.disableAunisMainMenu ? null : new ResourceLocation(Aunis.ModID, "textures/gui/mainmenu/background.jpg");

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

    private final GameSettings game_settings_4;
    protected String title = "Options";
    private String offDisplayString;

    public AunisAudioOptions(GuiScreen parentIn, GameSettings settingsIn, BiomeOverlayEnum overlay){
        super(parentIn, settingsIn);
        this.parent = parentIn;
        this.game_settings_4 = settingsIn;
        this.overlay = overlay;
    }

    public void initGui()
    {
        this.title = I18n.format("options.sounds.title");
        this.offDisplayString = I18n.format("options.off");
        int i = 0;
        aunisButtonList.clear();
        this.aunisButtonList.add(new AunisAudioOptions.Button(SoundCategory.MASTER.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), SoundCategory.MASTER, true));
        i = i + 2;

        for (SoundCategory soundcategory : SoundCategory.values())
        {
            if (soundcategory != SoundCategory.MASTER)
            {
                this.aunisButtonList.add(new AunisAudioOptions.Button(soundcategory.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), soundcategory, false));
                ++i;
            }
        }

        int k = this.height / 6 - 12;
        ++i;
        this.aunisButtonList.add(new AunisOptionButton(201, this.width / 2 - 100, k + 24 * (i >> 1), GameSettings.Options.SHOW_SUBTITLES, this.game_settings_4.getKeyBinding(GameSettings.Options.SHOW_SUBTITLES)));
        this.aunisButtonList.add(new AunisGuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")));
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1)
        {
            this.mc.gameSettings.saveOptions();
        }

        super.keyTyped(typedChar, keyCode);
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 200)
            {
                this.mc.gameSettings.saveOptions();
                this.isUnloading = true;
            }
            else if (button.id == 201)
            {
                this.mc.gameSettings.setOptionValue(GameSettings.Options.SHOW_SUBTITLES, 1);
                button.displayString = this.mc.gameSettings.getKeyBinding(GameSettings.Options.SHOW_SUBTITLES);
                this.mc.gameSettings.saveOptions();
            }
        }
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

    protected String getDisplayString(SoundCategory category)
    {
        float f = this.game_settings_4.getSoundLevel(category);
        return f == 0.0F ? this.offDisplayString : (int)(f * 100.0F) + "%";
    }

    @SideOnly(Side.CLIENT)
    class Button extends AunisGuiSliderSounds
    {
        private final SoundCategory category;
        private final String categoryName;
        public float volume = 1.0F;
        public boolean pressed;

        public Button(int buttonId, int x, int y, SoundCategory categoryIn, boolean master)
        {
            super(buttonId, x, y, master ? 310 : 150, 20, "");
            this.category = categoryIn;
            this.x = x;
            this.y = y;
            this.categoryName = I18n.format("soundCategory." + categoryIn.getName());
            this.displayString = this.categoryName + ": " + AunisAudioOptions.this.getDisplayString(categoryIn);
            this.volume = AunisAudioOptions.this.game_settings_4.getSoundLevel(categoryIn);
        }

        protected int getHoverState(boolean mouseOver)
        {
            return 0;
        }

        protected void mouseDragged(Minecraft mc, int mouseX, int mouseY)
        {
            if (this.visible)
            {
                if (this.pressed)
                {
                    this.volume = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
                    this.volume = MathHelper.clamp(this.volume, 0.0F, 1.0F);
                    mc.gameSettings.setSoundLevel(this.category, this.volume);
                    mc.gameSettings.saveOptions();
                    this.displayString = this.categoryName + ": " + AunisAudioOptions.this.getDisplayString(this.category);
                }

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                //this.drawTexturedModalRect(this.x + (int)(this.volume * (float)(this.width - 8)), this.y, 0, 66, 4, 20);
                drawRect(this.x + (int)(this.volume * (float)(this.width - 8)), this.y, this.x + (int)(this.volume * (float)(this.width - 8)) + 8, this.y + 20, GuiBase.FRAME_COLOR);
            }
        }

        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
        {
            if (super.mousePressed(mc, mouseX, mouseY))
            {
                this.volume = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
                this.volume = MathHelper.clamp(this.volume, 0.0F, 1.0F);
                mc.gameSettings.setSoundLevel(this.category, this.volume);
                mc.gameSettings.saveOptions();
                this.displayString = this.categoryName + ": " + AunisAudioOptions.this.getDisplayString(this.category);
                this.pressed = true;
                return true;
            }
            else
            {
                return false;
            }
        }

        public void playPressSound(SoundHandler soundHandlerIn)
        {
        }

        public void mouseReleased(int mouseX, int mouseY)
        {
            if (this.pressed)
            {
                AunisAudioOptions.this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }

            this.pressed = false;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException{
        if (mouseButton == 0)
        {
            for (int i = 0; i < this.aunisButtonList.size(); ++i){
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
            for (int i = 0; i < this.aunisButtonSliders.size(); ++i){
                GuiButton guibutton = this.aunisButtonSliders.get(i);

                if (guibutton.mousePressed(this.mc, mouseX, mouseY))
                {
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
