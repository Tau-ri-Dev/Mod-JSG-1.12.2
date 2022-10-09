package tauri.dev.jsg.gui.mainmenu;

import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.gui.base.JSGGuiButton;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.renderer.stargate.ChevronEnum;
import tauri.dev.jsg.sound.JSGSoundHelperClient;
import tauri.dev.jsg.sound.SoundPositionedEnum;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class JSGMainMenuOnLoad extends JSGMainMenu {

    public static boolean isGameLoaded = false;

    // next overlay
    @Override
    public BiomeOverlayEnum getNextBiomeOverlay(boolean doIt, boolean updateGui) {
        if(this.overlay == null)
            this.overlay = BiomeOverlayEnum.NORMAL;

        if(updateGui) initGui();
        return this.overlay;
    }

    // play sound
    @Override
    public void updateSound() {

        if (this.mc.gameSettings.getKeyBinding(GameSettings.Options.AUTO_JUMP).equals("Auto-Jump: ON"))
            this.mc.gameSettings.setOptionValue(GameSettings.Options.AUTO_JUMP, 1);

        JSGSoundHelperClient.playPositionedSoundClientSide(new BlockPos(1, 0, 0), SoundPositionedEnum.MAINMENU_MUSIC,
                false);
        JSGSoundHelperClient.playPositionedSoundClientSide(new BlockPos(0, 0, 1), SoundPositionedEnum.MAINMENU_RING_ROLL,
                (tauri.dev.jsg.config.JSGConfig.mainMenuConfig.gateRotation && kawooshState == 0 && ringAnimationSpeed > 0.0f));
    }

    @Override
    public void updateAnimation() {
        if (animationStage > 360) animationStage = 0f;
        updateRingSpeed();
    }

    // RENDER MAIN MENU
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // prevents from escape
        if(isGameLoaded)
            this.mc.displayGuiScreen(new JSGMainMenu());

        this.screenCenterHeight = (((float) height) / 2f);
        this.screenCenterWidth = ((float) width) / 2f;
        // ------------------------------
        // ANIMATIONS AND SOUNDS

        updateAnimation();
        updateSound();
        updateLastChevron();

        // ------------------------------
        // DRAWING BACKGROUND BUTTON TO CONTINUE

        for (GuiButton guiButton : this.anyButton) {
            ((JSGGuiButton) guiButton).drawButton(this.mc, mouseX, mouseY, partialTicks);
        }

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
        // DRAWING MAIN TITLE

        GlStateManager.pushMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.translate(screenCenterWidth, screenCenterHeight - 48, 0);
        GlStateManager.scale(4, 4, 4);
        drawCenteredString(fontRenderer, "JSG Mod", 0, 0, 0xffffff);
        GlStateManager.disableTexture2D();
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.translate(screenCenterWidth, screenCenterHeight - 48 + (81 - 48), 0);
        GlStateManager.scale(1.5f, 1.5f, 1.5f);
        drawCenteredString(fontRenderer, "Just Stargate Mod", 0, 0, 0xa4a4a4);
        GlStateManager.disableTexture2D();
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.translate(screenCenterWidth, screenCenterHeight + 100, 0);
        GlStateManager.scale(1.5f, 1.5f, 1.5f);
        drawCenteredString(fontRenderer, "Press any key to continue...", 0, 0, 0xa4a4a4);
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
        // DRAWING DEBUG INFO

        if (JSGConfig.mainMenuConfig.debugMode) {
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

    @Override
    public void initGui() {

        this.anyButton.clear();
        // toggle
        this.anyButton.add(new JSGGuiButton(0, 0, 0, width, height, "Continue"));

        this.jsgButtonList.clear();
        // quit
        this.jsgButtonList.add(new JSGGuiButton(4, this.width - 6 - 98, this.height - 6 - 20, 98, 20, I18n.format("menu.quit")));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            // main menu screens
            case 0:
                this.mc.displayGuiScreen(new JSGMainMenu());
                isGameLoaded = true;
                break;
            case 4:
                this.mc.shutdown();
                break;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0 || mouseButton == 1 || mouseButton == 2) {
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
                    return;
                }
            }
            for (int i = 0; i < this.anyButton.size(); ++i) {
                GuiButton guibutton = this.anyButton.get(i);
                net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, this.anyButton);
                if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                    break;
                guibutton = event.getButton();
                this.selectedButton = guibutton;
                guibutton.playPressSound(this.mc.getSoundHandler());
                this.actionPerformed(guibutton);
                if (this.equals(this.mc.currentScreen))
                    net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), this.anyButton));
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode){
        for (int i = 0; i < this.anyButton.size(); ++i) {
            GuiButton guibutton = this.anyButton.get(i);
            net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, this.anyButton);
            if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                break;
            guibutton = event.getButton();
            this.selectedButton = guibutton;
            guibutton.playPressSound(this.mc.getSoundHandler());
            this.actionPerformed(guibutton);
            if (this.equals(this.mc.currentScreen))
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), this.anyButton));
        }
    }
}
