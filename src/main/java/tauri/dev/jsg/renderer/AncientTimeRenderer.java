package tauri.dev.jsg.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import tauri.dev.jsg.loader.texture.TextureLoader;

import static tauri.dev.jsg.Constants.*;

public class AncientTimeRenderer {

    public static final double ONE_DIGIT_X = 5;
    public static final double SPACE_BETWEEN_X = (ONE_DIGIT_X / 3);
    public static final double HOURS_START_X = -(ONE_DIGIT_X * 4) - (SPACE_BETWEEN_X * 1.5);
    public static final double MINUTES_START_X = -(ONE_DIGIT_X * 3) - (SPACE_BETWEEN_X);
    public static final double SECONDS_START_X = -(ONE_DIGIT_X * 2) - (SPACE_BETWEEN_X / 2);
    public static final double TICKS_START_X = -ONE_DIGIT_X;

    public static void renderClock(long ticks, boolean renderWholeTime, long countToTicks, boolean twoDim) {
        int hours = (int) Math.floor((double) ticks / HOUR);
        int minutes = (int) Math.floor((double) (ticks % HOUR) / MINUTE);
        int seconds = (int) Math.floor((double) (ticks % MINUTE) / SECOND);
        int ticksDisplay = (int) ((Math.floor((double) (ticks % SECOND)) / 20) * 60); // convert ticks to "milliseconds"
        renderClock(hours, minutes, seconds, ticksDisplay, renderWholeTime, countToTicks, twoDim);
    }
    public static void renderClock(int hours, int minutes, int seconds, int ticks, boolean renderWholeTime, long countToTicks, boolean twoDim) {
        if (hours > 0 || renderWholeTime) {
            GlStateManager.translate(HOURS_START_X, 0, 0);
            renderTime(hours, minutes, seconds, ticks, twoDim);
        } else if (minutes > 0) {
            GlStateManager.translate(MINUTES_START_X, 0, 0);
            renderTime(minutes, seconds, ticks, twoDim);
        } else if (countToTicks != -1) {
            GlStateManager.color(1, 0, 0);
            if (seconds > 0) {
                GlStateManager.translate(SECONDS_START_X, 0, 0);
                renderTime(seconds, ticks, twoDim);
            } else {
                GlStateManager.translate(TICKS_START_X, 0, 0);
                renderTime(ticks, twoDim);
            }
        }
    }

    // hours
    public static void renderTime(int hours, int minutes, int seconds, int ticks, boolean twoDim) {
        // ten
        GlStateManager.pushMatrix();
        renderNumber((int) Math.floor((double) hours / 10), twoDim);
        GlStateManager.popMatrix();
        GlStateManager.translate(ONE_DIGIT_X, 0, 0);

        // one
        GlStateManager.pushMatrix();
        renderNumber(hours % 10, twoDim);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(SPACE_BETWEEN_X * 2, 0, 0);
        renderNumber(-1, twoDim);
        GlStateManager.popMatrix();

        GlStateManager.translate(ONE_DIGIT_X + SPACE_BETWEEN_X, 0, 0);
        renderTime(minutes, seconds, ticks, twoDim);

    }

    // minutes
    public static void renderTime(int minutes, int seconds, int ticks, boolean twoDim) {
        // ten
        GlStateManager.pushMatrix();
        renderNumber((int) Math.floor((double) minutes / 10), twoDim);
        GlStateManager.popMatrix();
        GlStateManager.translate(ONE_DIGIT_X, 0, 0);

        // one
        GlStateManager.pushMatrix();
        renderNumber(minutes % 10, twoDim);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(SPACE_BETWEEN_X * 2, 0, 0);
        renderNumber(-1, twoDim);
        GlStateManager.popMatrix();

        GlStateManager.translate(ONE_DIGIT_X + SPACE_BETWEEN_X, 0, 0);
        renderTime(seconds, ticks, twoDim);

    }

    // seconds
    public static void renderTime(int seconds, int ticks, boolean twoDim) {
        // ten
        GlStateManager.pushMatrix();
        renderNumber((int) Math.floor((double) seconds / 10), twoDim);
        GlStateManager.popMatrix();
        GlStateManager.translate(ONE_DIGIT_X, 0, 0);

        // one
        GlStateManager.pushMatrix();
        renderNumber(seconds % 10, twoDim);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(SPACE_BETWEEN_X * 2, 0, 0);
        renderNumber(-2, twoDim);
        GlStateManager.popMatrix();

        GlStateManager.translate(ONE_DIGIT_X + SPACE_BETWEEN_X, 0, 0);
        renderTime(ticks, twoDim);

    }

    // ticks
    public static void renderTime(int ticks, boolean twoDim) {
        // ten
        GlStateManager.pushMatrix();
        renderNumber((int) Math.floor((double) ticks / 10), twoDim);
        GlStateManager.popMatrix();
        GlStateManager.translate(ONE_DIGIT_X, 0, 0);

        // one
        GlStateManager.pushMatrix();
        renderNumber(ticks % 10, twoDim);
        GlStateManager.popMatrix();
    }

    public static void renderNumber(int number, boolean twoDim) {
        if (number < -2) number = -2;
        if (number > 9) number = 9;

        String path = "ancient/numbers/" + (number >= 0 ? number : ("_" + Math.abs(number))) + ".png";

        GlStateManager.pushMatrix();
        if (!twoDim) {
            GlStateManager.rotate(180, 0, 1, 0);
            GlStateManager.rotate(180, 0, 0, 1);
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureLoader.getTextureResource(path));

        int uvSizeX = 3;
        int uvSizeY = 10;

        drawTexturedModalRect(0, 0, 0, 0, uvSizeX, uvSizeY, uvSizeX, uvSizeY);

        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    public static void drawTexturedModalRect(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, (y + height), 0.0D).tex((u * f), ((v + (float) height) * f1)).endVertex();
        bufferbuilder.pos((x + width), (y + height), 0.0D).tex(((u + (float) width) * f), ((v + (float) height) * f1)).endVertex();
        bufferbuilder.pos((x + width), y, 0.0D).tex(((u + (float) width) * f), (v * f1)).endVertex();
        bufferbuilder.pos(x, y, 0.0D).tex((u * f), (v * f1)).endVertex();
        tessellator.draw();
    }
}
