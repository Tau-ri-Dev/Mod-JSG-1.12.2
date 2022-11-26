package tauri.dev.jsg.renderer.props;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.loader.texture.TextureLoader;
import tauri.dev.jsg.tileentity.props.DestinyCountDownTile;
import tauri.dev.jsg.util.FacingToRotation;
import tauri.dev.jsg.util.JSGTextureLightningHelper;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;

import static tauri.dev.jsg.Constants.*;

public class DestinyCountDownRenderer extends TileEntitySpecialRenderer<DestinyCountDownTile> {

    @Override
    public void render(@Nonnull DestinyCountDownTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        DestinyCountDownRendererState rs = te.getRendererState();
        if (rs != null) {
            long ticks = te.getCountdownTicks();

            if (ticks < 0) ticks = 0;

            int hours = (int) Math.floor((double) ticks / HOUR);
            int minutes = (int) Math.floor((double) (ticks % HOUR) / MINUTE);
            int seconds = (int) Math.floor((double) (ticks % MINUTE) / SECOND);
            int ticksDisplay = (int) ((Math.floor((double) (ticks % SECOND)) / 20) * 60); // convert ticks to "milliseconds"

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.translate(0.5, 0.5, 0.5);

            IBlockState blockState = te.getWorld().getBlockState(te.getPos());
            EnumFacing facing = blockState.getValue(JSGProps.FACING_HORIZONTAL);
            GlStateManager.pushMatrix();
            int rot = FacingToRotation.getIntRotation(facing);
            GlStateManager.rotate(rot + 180, 0, 1, 0);

            GlStateManager.translate(0, 0, -0.5);

            GlStateManager.pushMatrix();
            GlStateManager.scale(0.03, 0.03, 0.03);
            GlStateManager.rotate(90, 1, 0, 0);
            ElementEnum.DESTINY_COUNTDOWN.bindTextureAndRender();
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            JSGTextureLightningHelper.lightUpTexture(1f);

            GlStateManager.translate(0.007, 0.05, 0.07);
            GlStateManager.scale(0.01, 0.01, 0.01);

            GlStateManager.color(1, 1, 1);

            if (hours > 0) {
                GlStateManager.translate(HOURS_START_X, 0, 0);
                renderTime(hours, minutes, seconds, ticksDisplay);
            } else if (minutes > 0) {
                GlStateManager.translate(MINUTES_START_X, 0, 0);
                renderTime(minutes, seconds, ticksDisplay);
            } else if (te.countdownTo != -1) {
                GlStateManager.color(1, 0, 0);
                if (seconds > 0) {
                    GlStateManager.translate(SECONDS_START_X, 0, 0);
                    renderTime(seconds, ticksDisplay);
                } else {
                    GlStateManager.translate(TICKS_START_X, 0, 0);
                    renderTime(ticksDisplay);
                }
            }
            GlStateManager.popMatrix();
            GlStateManager.popMatrix();
            GlStateManager.popMatrix();
        }
    }

    private static final double ONE_DIGIT_X = 5;
    private static final double SPACE_BETWEEN_X = (ONE_DIGIT_X / 3);
    private static final double HOURS_START_X = -(ONE_DIGIT_X * 4) - (SPACE_BETWEEN_X * 1.5);
    private static final double MINUTES_START_X = -(ONE_DIGIT_X * 3) - (SPACE_BETWEEN_X);
    private static final double SECONDS_START_X = -(ONE_DIGIT_X * 2) - (SPACE_BETWEEN_X / 2);
    private static final double TICKS_START_X = -ONE_DIGIT_X;

    // hours
    private void renderTime(int hours, int minutes, int seconds, int ticks) {
        // ten
        GlStateManager.pushMatrix();
        renderNumber((int) Math.floor((double) hours / 10));
        GlStateManager.popMatrix();
        GlStateManager.translate(ONE_DIGIT_X, 0, 0);

        // one
        GlStateManager.pushMatrix();
        renderNumber(hours % 10);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(SPACE_BETWEEN_X * 2, 0, 0);
        renderNumber(-1);
        GlStateManager.popMatrix();

        GlStateManager.translate(ONE_DIGIT_X + SPACE_BETWEEN_X, 0, 0);
        renderTime(minutes, seconds, ticks);

    }

    // minutes
    private void renderTime(int minutes, int seconds, int ticks) {
        // ten
        GlStateManager.pushMatrix();
        renderNumber((int) Math.floor((double) minutes / 10));
        GlStateManager.popMatrix();
        GlStateManager.translate(ONE_DIGIT_X, 0, 0);

        // one
        GlStateManager.pushMatrix();
        renderNumber(minutes % 10);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(SPACE_BETWEEN_X * 2, 0, 0);
        renderNumber(-1);
        GlStateManager.popMatrix();

        GlStateManager.translate(ONE_DIGIT_X + SPACE_BETWEEN_X, 0, 0);
        renderTime(seconds, ticks);

    }

    // seconds
    private void renderTime(int seconds, int ticks) {
        // ten
        GlStateManager.pushMatrix();
        renderNumber((int) Math.floor((double) seconds / 10));
        GlStateManager.popMatrix();
        GlStateManager.translate(ONE_DIGIT_X, 0, 0);

        // one
        GlStateManager.pushMatrix();
        renderNumber(seconds % 10);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(SPACE_BETWEEN_X * 2, 0, 0);
        renderNumber(-2);
        GlStateManager.popMatrix();

        GlStateManager.translate(ONE_DIGIT_X + SPACE_BETWEEN_X, 0, 0);
        renderTime(ticks);

    }

    // ticks
    private void renderTime(int ticks) {
        // ten
        GlStateManager.pushMatrix();
        renderNumber((int) Math.floor((double) ticks / 10));
        GlStateManager.popMatrix();
        GlStateManager.translate(ONE_DIGIT_X, 0, 0);

        // one
        GlStateManager.pushMatrix();
        renderNumber(ticks % 10);
        GlStateManager.popMatrix();
    }

    public void renderNumber(int number) {
        if (number < -2) number = -2;
        if (number > 9) number = 9;
        GlStateManager.pushMatrix();
        GlStateManager.rotate(180, 0, 1, 0);
        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.pushMatrix();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();

        String path = "ancient/numbers/" + (number >= 0 ? number : ("_" + Math.abs(number))) + ".png";
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
