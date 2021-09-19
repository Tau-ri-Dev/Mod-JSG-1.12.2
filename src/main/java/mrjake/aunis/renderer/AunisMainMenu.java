package mrjake.aunis.renderer;

import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.stargate.ChevronEnum;
import mrjake.aunis.sound.AunisSoundHelper;
import mrjake.aunis.sound.AunisSoundHelperClient;
import mrjake.aunis.sound.SoundEventEnum;
import mrjake.aunis.sound.SoundPositionedEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class AunisMainMenu extends GuiScreen {

    protected float animationStage;
    protected boolean playingSound = false;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        animationStage += 0.5f;
        if(animationStage >= 360) animationStage = 0f;

        if(!playingSound){
            Minecraft.getMinecraft().getSoundHandler().playSound();
            playingSound = true;
        }

        drawDefaultBackground();
        GlStateManager.pushMatrix();
        GlStateManager.disableBlend();
        GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
        GL11.glEnable(2903 /* GL_COLOR_MATERIAL */);
        //GlStateManager.translate(350, 150, 0f);
        GlStateManager.translate(275, 200, 0f);
        GlStateManager.scale(40, 40, 50);
        GlStateManager.rotate(-180f, 0f, 0f, 1f);

        // make it 3d
        //GlStateManager.rotate(125, 1, 0, 0);
        //GlStateManager.rotate(-5, 0, 1, 0);
        // ---

        // GATE
        GlStateManager.pushMatrix();
        ElementEnum.MILKYWAY_GATE.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
        GlStateManager.popMatrix();

        // RING
        GlStateManager.pushMatrix();
        // ring rotation animation
        GlStateManager.rotate(animationStage, 0, 0, 1);
        // -----
        //ElementEnum.MILKYWAY_RING_MAIN_MENU.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
        ElementEnum.MILKYWAY_RING.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
        GlStateManager.popMatrix();

        // CHEVRONS
        for(int i = 0; i < 9; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 0.03f);
            GlStateManager.rotate(ChevronEnum.valueOf(i).rotation, 0, 0, 1);
            // generates chevron lock animation
            float chevronOffset = 0;
            // ---------
            ElementEnum.MILKYWAY_CHEVRON_FRAME.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
            GlStateManager.translate(0, chevronOffset, 0);
            ElementEnum.MILKYWAY_CHEVRON_LIGHT.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
            GlStateManager.translate(0, -2 * chevronOffset, 0);
            ElementEnum.MILKYWAY_CHEVRON_MOVING.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
            GlStateManager.popMatrix();

            /*
            GlStateManager.pushMatrix();
            GlStateManager.rotate(180, 0, 0, 1);
            GlStateManager.rotate(ChevronEnum.valueOf(i).rotation, 0, 0, 1);
            ElementEnum.MILKYWAY_CHEVRON_FRAME.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
            ElementEnum.MILKYWAY_CHEVRON_BACK.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
            GlStateManager.popMatrix();
            */
        }

        GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT */);
        GL11.glDisable(2903 /* GL_COLOR_MATERIAL */);
        GlStateManager.popMatrix();
        //GlStateManager.disableBlend();
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
