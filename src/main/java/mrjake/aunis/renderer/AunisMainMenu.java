package mrjake.aunis.renderer;

import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.stargate.ChevronEnum;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.io.IOException;

@SideOnly(Side.CLIENT)
public class AunisMainMenu extends GuiScreen {

    protected float animationStage;
    protected boolean chevronsActive = true;
    protected boolean playingSound = false;
    protected BiomeOverlayEnum overlay = BiomeOverlayEnum.NORMAL;

    public void getNextBiomeOverlay(boolean doIt) {
        if(doIt) {
            switch (overlay) {
                case NORMAL:
                    overlay = BiomeOverlayEnum.AGED;
                    break;
                case AGED:
                    overlay = BiomeOverlayEnum.FROST;
                    break;
                case FROST:
                    overlay = BiomeOverlayEnum.MOSSY;
                    break;
                case MOSSY:
                    overlay = BiomeOverlayEnum.SOOTY;
                    break;
                case SOOTY:
                default:
                    overlay = BiomeOverlayEnum.NORMAL;
                    break;
            }
        }
        else overlay = BiomeOverlayEnum.NORMAL;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // ------------------------------
        // ANIMATIONS AND SOUNDS

        if(AunisConfig.mainMenuConfig.gateRotation) animationStage += (float) AunisConfig.mainMenuConfig.gateSpeed;
        if(animationStage >= 360) animationStage = 0f;
        switch((int) animationStage){
            case 359:
            case 270:
            case 180:
            case 90:

                getNextBiomeOverlay(AunisConfig.mainMenuConfig.changingGateOverlay);
        }
        if(!playingSound){
            playingSound = true;
        }

        // ------------------------------
        // DRAWING BACKGROUND

        drawDefaultBackground();

        // ------------------------------
        // DRAWING WHOLE GATE MODEL

        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.translate((((float) width)/2f), (((float) height)/2f), 0f);
        GlStateManager.scale(30, 30, 30);
        GlStateManager.rotate(-180f, 0f, 0f, 1f);

        // make it 3d
        //GlStateManager.rotate(125, 1, 0, 0);
        //GlStateManager.rotate(-5, 0, 1, 0);
        // ---

        // ------------------------------
        // DRAWING GATE

        GlStateManager.pushMatrix();
        ElementEnum.MILKYWAY_GATE.bindTextureAndRender(overlay);
        GlStateManager.popMatrix();

        // ------------------------------
        // DRAWING RING

        GlStateManager.pushMatrix();
        // ring rotation animation
        GlStateManager.rotate(animationStage, 0, 0, 1);
        // -----
        //ElementEnum.MILKYWAY_RING_MAIN_MENU.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
        GlStateManager.enableTexture2D();
        ElementEnum.MILKYWAY_RING.bindTextureAndRender(overlay);
        GlStateManager.popMatrix();

        // ------------------------------
        // DRAWING CHEVRONS

        for(int i = 0; i < 9; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 0.03f);
            GlStateManager.rotate(ChevronEnum.valueOf(i).rotation, 0, 0, 1);
            // generates chevron lock animation
            float chevronOffset = 0;
            // ---------
            ElementEnum.MILKYWAY_CHEVRON_FRAME.bindTextureAndRender(overlay);
            GlStateManager.translate(0, chevronOffset, 0);
            if ((i == 6 || i == 7) || !this.chevronsActive) {
                ElementEnum.MILKYWAY_CHEVRON_LIGHT.bindTextureAndRender(overlay);
                GlStateManager.translate(0, -2 * chevronOffset, 0);
                ElementEnum.MILKYWAY_CHEVRON_MOVING.bindTextureAndRender(overlay);
            } else {
                ElementEnum.MILKYWAY_CHEVRON_LIGHT_ACTIVE.bindTextureAndRender(overlay);
                GlStateManager.translate(0, -2 * chevronOffset, 0);
                ElementEnum.MILKYWAY_CHEVRON_MOVING_ACTIVE.bindTextureAndRender(overlay);
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
        GlStateManager.enableAlpha();
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
        super.drawScreen(mouseX, mouseY, partialTicks);
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
