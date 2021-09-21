package mrjake.aunis.renderer.mainmenu;

import com.google.common.collect.Lists;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.List;

@SideOnly(Side.CLIENT)
public class AunisMainMenu extends GuiMainMenu {

    // define variables
    protected float animationStage = 0;
    protected float chevronLastAnimationStage = 0;
    protected boolean chevronsActive = true;
    protected boolean playingSound = false;
    protected boolean chevronShout = true;
    protected boolean chevronShoutColapsing = false;
    protected BiomeOverlayEnum overlay = BiomeOverlayEnum.NORMAL;
    protected float screenCenterHeight = (((float) height) / 2f);
    protected float screenCenterWidth = ((float) width) / 2f;
    protected List<AunisGuiButton> aunisButtonList = Lists.newArrayList();
    protected ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Aunis.ModID, "textures/gui/mainmenu/background.jpg");

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
    public void getNextBiomeOverlay(boolean doIt) {
        if (doIt) {
            switch (this.overlay) {
                case NORMAL:
                    this.overlay = BiomeOverlayEnum.AGED;
                    break;
                case AGED:
                    this.overlay = BiomeOverlayEnum.FROST;
                    break;
                case FROST:
                    this.overlay = BiomeOverlayEnum.MOSSY;
                    break;
                case MOSSY:
                    this.overlay = BiomeOverlayEnum.SOOTY;
                    break;
                case SOOTY:
                default:
                    this.overlay = BiomeOverlayEnum.NORMAL;
                    break;
            }
        } else this.overlay = BiomeOverlayEnum.NORMAL;
    }

    // play sound
    public void updateSound() {
        if (!playingSound) {
            AunisSoundHelperClient.playPositionedSoundClientSide(new BlockPos(1,0,0), SoundPositionedEnum.MAINMENU_MUSIC, AunisConfig.mainMenuConfig.playMusic);
            AunisSoundHelperClient.playPositionedSoundClientSide(new BlockPos(0,0,1), SoundPositionedEnum.MAINMENU_RINGROLL, AunisConfig.mainMenuConfig.gateRotation);
            playingSound = true;
        }
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
        this.drawScaledCustomSizeModalRect(0, 0, 0, 0, width, height, width, height, width, height);

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

        // make it 3d
        //GlStateManager.rotate(125, 1, 0, 0);
        //GlStateManager.rotate(-5, 0, 1, 0);
        // ---

        // ------------------------------
        // DRAWING GATE

        GlStateManager.pushMatrix();
        ElementEnum.MILKYWAY_GATE.bindTextureAndRender(this.overlay);
        GlStateManager.popMatrix();

        // ------------------------------
        // DRAWING RING

        GlStateManager.pushMatrix();
        GlStateManager.translate(0f, 0f, -3f);
        // ring rotation animation
        GlStateManager.rotate(animationStage, 0, 0, 1);
        // -----
        //ElementEnum.MILKYWAY_RING_MAIN_MENU.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
        ElementEnum.MILKYWAY_RING.bindTextureAndRender(this.overlay);
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
            ElementEnum.MILKYWAY_CHEVRON_FRAME.bindTextureAndRender(this.overlay);
            GlStateManager.translate(0, chevronOffset, 0);
            if ((i == 6 || i == 7) || !this.chevronsActive) {
                ElementEnum.MILKYWAY_CHEVRON_LIGHT.bindTextureAndRender(this.overlay);
                GlStateManager.translate(0, -2 * chevronOffset, 0);
                ElementEnum.MILKYWAY_CHEVRON_MOVING.bindTextureAndRender(this.overlay);
            } else {
                ElementEnum.MILKYWAY_CHEVRON_LIGHT_ACTIVE.bindTextureAndRender(this.overlay);
                GlStateManager.translate(0, -2 * chevronOffset, 0);
                ElementEnum.MILKYWAY_CHEVRON_MOVING_ACTIVE.bindTextureAndRender(this.overlay);
            }
            GlStateManager.popMatrix();
            // back side
            /*
            GlStateManager.pushMatrix();
            GlStateManager.rotate(180, 0, 0, 1);
            GlStateManager.rotate(ChevronEnum.valueOf(i).rotation, 0, 0, 1);
            ElementEnum.MILKYWAY_CHEVRON_FRAME.bindTextureAndRender(overlay);
            ElementEnum.MILKYWAY_CHEVRON_BACK.bindTextureAndRender(overlay);
            GlStateManager.popMatrix();
            */
        }
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableTexture2D();
        GlStateManager.popMatrix();

        // ------------------------------
        // DRAWING TEXTS

        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.translate(6, (((float) height) - 36f), 0);
        GlStateManager.scale(0.8, 0.8, 0.8);
        GlStateManager.translate(0, 0, 0);
        drawString(fontRenderer, "Music credits: STARGATE SG-1 - Full Original Soundtrack OST", 0, 0, 0xffffff);
        GlStateManager.translate(0, 10, 0);
        drawString(fontRenderer, "Aunis mod by: MrJake, MineDragonCZ_ and Matousss", 0, 0, 0xffffff);
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

        for (int l = 0; l < this.aunisButtonList.size(); ++l) {
            this.aunisButtonList.get(l).drawButton(this.mc, mouseX, mouseY, partialTicks);
        }

        for (int j = 0; j < this.labelList.size(); ++j) {
            (this.labelList.get(j)).drawLabel(this.mc, mouseX, mouseY);
        }
    }

    @Override
    public void initGui() {
        this.aunisButtonList.clear();
        int j = this.height / 4 + 48;

        // single
        this.aunisButtonList.add(new AunisGuiButton(1, this.width / 2 - 100, j, I18n.format("menu.singleplayer")));
        // multi
        this.aunisButtonList.add(new AunisGuiButton(2, this.width / 2 - 100, j + 24, I18n.format("menu.multiplayer")));
        // mods list
        this.aunisButtonList.add(new AunisGuiButton(6, this.width - 6 - 98, 6, 98, 20, I18n.format("fml.menu.mods")));
        // options
        this.aunisButtonList.add(new AunisGuiButton(0, 6, 6, 98, 20, I18n.format("menu.options")));
        // quit
        this.aunisButtonList.add(new AunisGuiButton(4, this.width - 6 - 98, this.height - 6 - 20, 98, 20, I18n.format("menu.quit")));

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
                break;
            case 14:
                RealmsBridge realmsbridge = new RealmsBridge();
                realmsbridge.switchToRealms(this);
                break;
            // other screens
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
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            for (int i = 0; i < this.aunisButtonList.size(); ++i) {
                GuiButton guibutton = this.aunisButtonList.get(i);

                if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
                    net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, (List) this.aunisButtonList);
                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                        break;
                    guibutton = event.getButton();
                    this.selectedButton = guibutton;
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(guibutton);
                    if (this.equals(this.mc.currentScreen))
                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), (List) this.aunisButtonList));
                }
            }
        }

        /*if (mouseX > this.widthCopyrightRest && mouseX < this.widthCopyrightRest + this.widthCopyright && mouseY > this.height - 10 && mouseY < this.height)
        {
            this.mc.displayGuiScreen(new GuiWinGame(false, Runnables.doNothing()));
        }*/
    }
}
