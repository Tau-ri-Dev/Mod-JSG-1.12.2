package tauri.dev.jsg.renderer.props;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.renderer.AncientTimeRenderer;
import tauri.dev.jsg.tileentity.props.DestinyCountDownTile;
import tauri.dev.jsg.util.FacingToRotation;
import tauri.dev.jsg.util.JSGTextureLightningHelper;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;
import java.time.LocalTime;
import java.time.temporal.ChronoField;

public class DestinyCountDownRenderer extends TileEntitySpecialRenderer<DestinyCountDownTile> {

    @Override
    public void render(@Nonnull DestinyCountDownTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        DestinyCountDownRendererState rs = te.getRendererState();
        if (rs != null) {
            boolean clock = (te.getConfig().getOption(DestinyCountDownTile.ConfigOptions.SWITCH_TO_CLOCK.id).getBooleanValue() && te.countdownTo == -1);

            long ticks = te.getCountdownTicks();

            if (clock) {
                ticks = Math.round(((double) LocalTime.now().getLong(ChronoField.MILLI_OF_DAY) / 1000L) * 20);
            }

            if (ticks < 0) ticks = 0;

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
            AncientTimeRenderer.renderClock(ticks, clock, te.countdownTo, false);
            GlStateManager.popMatrix();
            GlStateManager.popMatrix();
            GlStateManager.popMatrix();
        }
    }
}
