package mrjake.aunis.renderer.mainmenu;

import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.stargate.ChevronEnum;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class AunisMainMenu extends GuiMainMenu{

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

    private static final ResourceLocation MINECRAFT_TITLE_TEXTURES = new ResourceLocation("textures/gui/title/minecraft.png");
    protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");
    public int bwidth;
    public int bheight;
    public int x;
    public int y;
    public String displayString;
    public int id;
    public boolean enabled;
    public boolean visible;
    protected boolean hovered;
    public int packedFGColour; //FML

    protected int getHoverState(boolean mouseOver)
    {
        int i = 1;

        if (!this.enabled)
        {
            i = 0;
        }
        else if (mouseOver)
        {
            i = 2;
        }

        return i;
    }

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
            //this.mc.getSoundHandler().playSound((ISound) SoundPositionedEnum.MILKYWAY_RING_ROLL);
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

        drawDefaultBackground();

        // ------------------------------
        // DRAWING WHOLE GATE MODEL

        GlStateManager.pushMatrix();
        // background gradient
        this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
        this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
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
        GlStateManager.translate(6, (((float) height) - 26f), 0);
        GlStateManager.scale(0.8, 0.8, 0.8);
        GlStateManager.translate(0, 0, 0);
        drawString(fontRenderer, "Music credits: STARGATE SG-1 - Full Original Soundtrack OST", 0, 0, 0xffffff);
        GlStateManager.translate(0, 10, 0);
        drawString(fontRenderer, "Aunis mod by: MrJake, MineDragonCZ_ and Matousss", 0, 0, 0xffffff);
        GlStateManager.translate(0, 10, 0);
        drawString(fontRenderer, "Note that the gate cannot be rendered perfectly here!", 0, 0, 0xffffff);
        GlStateManager.disableTexture2D();
        GlStateManager.popMatrix();

        // ------------------------------
        // DRAWING MAIN TITLE

        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.translate((((float) width)/2), 10, 0);
        this.mc.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURES);
        GlStateManager.disableTexture2D();
        GlStateManager.popMatrix();

        // ------------------------------
        // DRAWING BUTTONS

        for (int l = 0; l < this.buttonList.size(); ++l) {


            this.bwidth = buttonList.get(l).width;
            this.bheight = buttonList.get(l).height;
            this.enabled = true;
            this.visible = true;
            this.id = l;
            this.x = buttonList.get(l).x;
            this.y = buttonList.get(l).y;
            this.displayString = buttonList.get(l).displayString;



            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.bwidth && mouseY < this.y + this.bheight;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.bwidth / 2, this.bheight);
            this.drawTexturedModalRect(this.x + this.bwidth / 2, this.y, 200 - this.bwidth / 2, 46 + i * 20, this.bwidth / 2, this.bheight);
            int j = 14737632;

            if (packedFGColour != 0)
            {
                j = packedFGColour;
            }
            else
            if (!this.enabled)
            {
                j = 10526880;
            }
            else if (this.hovered)
            {
                j = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.bwidth / 2, this.y + (this.bheight - 8) / 2, j);
        }

        for (int j = 0; j < this.labelList.size(); ++j) {
            (this.labelList.get(j)).drawLabel(this.mc, mouseX, mouseY);
        }
    }
    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
