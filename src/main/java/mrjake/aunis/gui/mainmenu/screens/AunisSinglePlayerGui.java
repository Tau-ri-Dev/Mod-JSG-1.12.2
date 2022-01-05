package mrjake.aunis.gui.mainmenu.screens;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.gui.base.AunisGuiButton;
import mrjake.aunis.gui.base.AunisGuiSlider;
import mrjake.aunis.gui.mainmenu.screens.worldselection.AunisGuiListWorldSelection;
import mrjake.aunis.gui.mainmenu.screens.worldselection.AunisGuiListWorldSelectionEntry;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.stargate.ChevronEnum;
import mrjake.aunis.sound.AunisSoundHelperClient;
import mrjake.aunis.sound.SoundPositionedEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AunisSinglePlayerGui extends GuiWorldSelection {
    public AunisSinglePlayerGui(GuiScreen screenIn, BiomeOverlayEnum overlay) {
        super(screenIn);
        this.prevScreen = screenIn;
        this.overlay = overlay;
    }

    protected GuiScreen prevScreen;
    protected String title = "Select world";
    private String worldVersTooltip;
    private AunisGuiButton deleteButton;
    private AunisGuiButton selectButton;
    private AunisGuiButton renameButton;
    private AunisGuiButton copyButton;
    private AunisGuiListWorldSelection selectionList;
    protected int eventButton;
    protected int touchValue;
    protected long lastMouseEvent;

    @Override
    public void initGui() {
        this.title = I18n.format("selectWorld.title");
        this.selectionList = new AunisGuiListWorldSelection(this, this.mc, this.width, this.height, 32, this.height - 64, 36);
        this.postInit();
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
        this.selectionList.handleMouseInput();
    }

    @Override
    public void postInit() {
        aunisButtonList.clear();
        selectButton = new AunisGuiButton(1, this.width / 2 - 154, this.height - 52, 150, 20, I18n.format("selectWorld.select"));
        aunisButtonList.add(new AunisGuiButton(3, this.width / 2 + 4, this.height - 52, 150, 20, I18n.format("selectWorld.create")));
        renameButton = new AunisGuiButton(4, this.width / 2 - 154, this.height - 28, 72, 20, I18n.format("selectWorld.edit"));
        deleteButton = new AunisGuiButton(2, this.width / 2 - 76, this.height - 28, 72, 20, I18n.format("selectWorld.delete"));
        copyButton = new AunisGuiButton(5, this.width / 2 + 4, this.height - 28, 72, 20, I18n.format("selectWorld.recreate"));
        aunisButtonList.add(new AunisGuiButton(0, this.width / 2 + 82, this.height - 28, 72, 20, I18n.format("gui.cancel")));
        selectButton.enabled = false;
        deleteButton.enabled = false;
        renameButton.enabled = false;
        copyButton.enabled = false;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            AunisGuiListWorldSelectionEntry guilistworldselectionentry = this.selectionList.getSelectedWorld();

            if (button.id == 2) {
                if (guilistworldselectionentry != null) {
                    guilistworldselectionentry.deleteWorld();
                }
            } else if (button.id == 1) {
                if (guilistworldselectionentry != null) {
                    guilistworldselectionentry.joinWorld();
                }
            } else if (button.id == 3) {
                this.mc.displayGuiScreen(new GuiCreateWorld(this));
            } else if (button.id == 4) {
                if (guilistworldselectionentry != null) {
                    guilistworldselectionentry.editWorld();
                }
            } else if (button.id == 0) {
                this.mc.displayGuiScreen(this.prevScreen);
            } else if (button.id == 5 && guilistworldselectionentry != null) {
                guilistworldselectionentry.recreateWorld();
            }
        }
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
    public void updateRingSpeed() {
        if (!AunisConfig.mainMenuConfig.gateRotation) return;
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

        if (gateZoom == 0.0f) gateZoom = ((((float) height) / 10f) - 3);
        if (gatePos == 0.0f) gatePos = ((float) width) / 2f;

        float step = 8f;
        if (!isUnloading) {
            if (this.gatePos < (this.width - 54f)) this.gatePos += step * 4f;
            if (this.gateZoom < (this.width / 6f)) this.gateZoom += step;

            if (this.gatePos > (this.width - 57f)) this.gatePos -= step * 4f;
            if (this.gateZoom > ((this.width / 6f) + 5f)) this.gateZoom -= step;
        } else {
            if (this.gatePos > 0.0f) this.gatePos -= step * 4f;
            if (this.gateZoom > 0.0f) this.gateZoom -= step;

            if (this.gatePos + ((step * 4) + 25) <= ((float) width) / 2f) this.mc.displayGuiScreen(this.prevScreen);
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

        if (!isUnloading) {

            GlStateManager.enableTexture2D();
            this.worldVersTooltip = null;
            this.selectionList.drawScreen(mouseX, mouseY, partialTicks);
            this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 20, 16777215);
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

            deleteButton.drawButton(this.mc, mouseX, mouseY, partialTicks);
            selectButton.drawButton(this.mc, mouseX, mouseY, partialTicks);
            renameButton.drawButton(this.mc, mouseX, mouseY, partialTicks);
            copyButton.drawButton(this.mc, mouseX, mouseY, partialTicks);



            GlStateManager.popMatrix();
            if (this.worldVersTooltip != null) {
                this.drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.worldVersTooltip)), mouseX, mouseY);
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

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
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
        this.selectionList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.selectedButton != null && state == 0)
        {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
        this.selectionList.mouseReleased(mouseX, mouseY, state);
    }

    public void setVersionTooltip(String p_184861_1_) {
        this.worldVersTooltip = p_184861_1_;
    }

    public void selectWorld(@Nullable AunisGuiListWorldSelectionEntry entry) {
        boolean flag = entry != null;
        this.selectButton.enabled = flag;
        this.deleteButton.enabled = flag;
        this.renameButton.enabled = flag;
        this.copyButton.enabled = flag;
    }
}
