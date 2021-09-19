package mrjake.aunis.renderer;

import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.loader.texture.TextureLoader;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.stargate.StargateMilkyWayRenderer;
import mrjake.aunis.renderer.stargate.StargateMilkyWayRendererState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class AunisMainMenu extends GuiScreen {

    protected float animationStage;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        animationStage += 0.5f;
        if(animationStage >= 360) animationStage = 0f;

        drawDefaultBackground();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        // GATE
        GlStateManager.pushMatrix();
        GlStateManager.translate(250, 250, 0f);
        GlStateManager.scale(50, 50, 50);
        GlStateManager.rotate(180, 1, 0, 0);
        ElementEnum.MILKYWAY_GATE.bindTexture(BiomeOverlayEnum.NORMAL);
        ElementEnum.MILKYWAY_GATE.render();
        GlStateManager.popMatrix();
        // RING
        GlStateManager.translate(250, 250, 0.1f);
        GlStateManager.pushMatrix();
        GlStateManager.scale(50, 50, 50);
        GlStateManager.rotate(180, 1, 0, 0);

        GlStateManager.rotate(animationStage, 0, 0, 1);

        ElementEnum.MILKYWAY_RING.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
        GlStateManager.popMatrix();
        // CHEVRONS
        GlStateManager.pushMatrix();
        GlStateManager.translate(250, 250, 2);
        GlStateManager.rotate(180, 0, 1, 0);
        GlStateManager.pushMatrix();
        GlStateManager.scale(50, 50, 50);
        ElementEnum.MILKYWAY_CHEVRON_LIGHT.render();
        ElementEnum.MILKYWAY_CHEVRON_MOVING.render();
        GlStateManager.popMatrix();
        ElementEnum.MILKYWAY_CHEVRON_FRAME.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
        ElementEnum.MILKYWAY_CHEVRON_BACK.render();
        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
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
