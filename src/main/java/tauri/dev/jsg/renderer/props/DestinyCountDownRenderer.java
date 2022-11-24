package tauri.dev.jsg.renderer.props;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.loader.texture.TextureLoader;
import tauri.dev.jsg.tileentity.props.DestinyCountDownTile;

import javax.annotation.Nonnull;

public class DestinyCountDownRenderer extends TileEntitySpecialRenderer<DestinyCountDownTile> {

    private static final int SECOND = 20;
    private static final int MINUTE = SECOND * 60;
    private static final int HOUR = MINUTE * 60;

    @Override
    public void render(@Nonnull DestinyCountDownTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        DestinyCountDownRendererState rs = te.getRendererState();
        if (rs != null) {
            ONE_DIGIT_X = JSGConfig.devConfig.sz;

            long ticks = te.getCountdownTicks();

            int hours = (int) Math.floor((double) ticks / HOUR);
            int minutes = (int) Math.floor((double) (ticks % HOUR) / MINUTE);
            int seconds = (int) Math.floor((double) (ticks % MINUTE) / SECOND);

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.translate(0.5, 0.5, 0.5);

            GlStateManager.pushMatrix();
            GlStateManager.scale(0.03, 0.03, 0.03);
            GlStateManager.rotate(90, 1, 0, 0);
            ElementEnum.DESTINY_COUNTDOWN.bindTextureAndRender();
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            JSGConfig.rescaleToConfig();

            if (hours > 1) {
                GlStateManager.translate(HOURS_START_X, 0, 0);
                renderTime(hours, minutes, seconds);
            } else if (minutes > 1) {
                GlStateManager.translate(MINUTES_START_X, 0, 0);
                renderTime(minutes, seconds);
            } else {
                GlStateManager.translate(SECONDS_START_X, 0, 0);
                renderTime(seconds);
            }
            GlStateManager.popMatrix();

            GlStateManager.popMatrix();
        }
    }

    private static double ONE_DIGIT_X = JSGConfig.devConfig.sz;
    private static final double HOURS_START_X = -(ONE_DIGIT_X * 4);
    private static final double MINUTES_START_X = -(ONE_DIGIT_X * 2);
    private static final double SECONDS_START_X = -ONE_DIGIT_X;

    private void renderTime(int hours, int minutes, int seconds) {
        // ten
        GlStateManager.pushMatrix();
        renderNumber((int) Math.floor((double) hours / 10));
        GlStateManager.popMatrix();
        GlStateManager.translate(ONE_DIGIT_X, 0, 0);

        // one
        GlStateManager.pushMatrix();
        renderNumber(hours % 10);
        GlStateManager.popMatrix();
        GlStateManager.translate(ONE_DIGIT_X, 0, 0);
        renderTime(minutes, seconds);

    }

    private void renderTime(int minutes, int seconds) {
        // ten
        GlStateManager.pushMatrix();
        renderNumber((int) Math.floor((double) minutes / 10));
        GlStateManager.popMatrix();
        GlStateManager.translate(ONE_DIGIT_X, 0, 0);

        // one
        GlStateManager.pushMatrix();
        renderNumber(minutes % 10);
        GlStateManager.popMatrix();
        GlStateManager.translate(ONE_DIGIT_X, 0, 0);
        renderTime(seconds);

    }

    private void renderTime(int seconds) {
        // ten
        GlStateManager.pushMatrix();
        renderNumber((int) Math.floor((double) seconds / 10));
        GlStateManager.popMatrix();
        GlStateManager.translate(ONE_DIGIT_X, 0, 0);

        // one
        GlStateManager.pushMatrix();
        renderNumber(seconds % 10);
        GlStateManager.popMatrix();
    }

    public void renderNumber(int number) {
        if (number < 0) number = 0;
        if (number > 9) number = 9;
        GlStateManager.pushMatrix();
        GlStateManager.rotate(180, 0, 1, 0);
        //GlStateManager.rotate(90, 1, 0, 0);
        GlStateManager.pushMatrix();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();

        GlStateManager.color(1, 1, 1);

        String path = "ancient/numbers/" + number + ".png";
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureLoader.getTextureResource(path));

        int uvSizeX = 71;
        int uvSizeY = 187;

        drawTexturedModalRect(0, 0, 0, 0, uvSizeX, uvSizeY);

        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        int zLevel = 0;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((x), (y + height), zLevel).tex(((float) (textureX) * 0.00390625F), ((float) (textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((+width), (y + height), zLevel).tex(((float) (textureX + width) * 0.00390625F), ((float) (textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((x + width), (y), zLevel).tex(((float) (textureX + width) * 0.00390625F), ((float) (textureY) * 0.00390625F)).endVertex();
        bufferbuilder.pos((x), (y), zLevel).tex(((float) (textureX) * 0.00390625F), ((float) (textureY) * 0.00390625F)).endVertex();
        tessellator.draw();
    }
}
