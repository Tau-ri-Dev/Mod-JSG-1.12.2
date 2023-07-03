package tauri.dev.jsg.renderer.props;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.particle.ParticleBlenderCOBlast;
import tauri.dev.jsg.tileentity.props.DestinyVentTile;
import tauri.dev.jsg.util.FacingHelper;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;

public class DestinyVentRenderer extends TileEntitySpecialRenderer<DestinyVentTile> {

    public static final int ANIMATION_DELAY_BEFORE = 20;
    public static final int OPEN_ANIMATION_LENGTH = 40;
    public static final int ANIMATION_DELAY_BETWEEN = 120;

    @Override
    public void render(@Nonnull DestinyVentTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

        long animationStart = te.animationStart;
        double tick = getWorld().getTotalWorldTime() + partialTicks;
        double time = Math.max((tick - animationStart - ANIMATION_DELAY_BEFORE), 0);
        float animationStage = 0;
        if (time <= (ANIMATION_DELAY_BETWEEN + OPEN_ANIMATION_LENGTH * 2))
            animationStage = (float) Math.max(0, Math.min(1, (Math.sin(Math.min((time / OPEN_ANIMATION_LENGTH), 1) * Math.PI) * ((float) ANIMATION_DELAY_BETWEEN / OPEN_ANIMATION_LENGTH))));

        boolean fireParticles = (animationStage > 0.6f);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.translate(0.5, 0f, 0.5);

        IBlockState blockState = te.getWorld().getBlockState(te.getPos());
        EnumFacing facing = blockState.getValue(JSGProps.FACING_HORIZONTAL);

        GlStateManager.pushMatrix();
        int rot = FacingHelper.getIntRotation(facing, false);
        GlStateManager.rotate(rot, 0, 1, 0);

        ElementEnum.DESTINY_VENT_HOLE.bindTextureAndRender();
        GlStateManager.pushMatrix();
        GlStateManager.rotate((45f * animationStage), 1, 0, 0);
        ElementEnum.DESTINY_VENT_MOVING.bindTextureAndRender();
        if (fireParticles) {
            for(int i = 0; i < 50; i++) {
                new ParticleBlenderCOBlast((float) (-0.5f + Math.random()), 0, (float) (-0.3f + Math.random() * 0.6f) - 0.5f, 2, 2, 0, 0.4f + (-0.1f + (float) (Math.random() * 0.2f)), -(0.3f + (-0.1f + (float) (Math.random() * 0.2f))), (motion) -> {}).spawn(te.getWorld(), te.getPos(), rot, false);
            }
        }
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    @Override
    public boolean isGlobalRenderer(@Nonnull DestinyVentTile te) {
        return true;
    }
}
