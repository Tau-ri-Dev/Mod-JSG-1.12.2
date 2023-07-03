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

    @Override
    public void render(@Nonnull DestinyVentTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        double tick = getWorld().getTotalWorldTime() + partialTicks;
        float animationStage = te.getAnimationStage(tick);
        boolean fireParticles = (animationStage == 1f);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.translate(0.5, 0f, 0.5);

        IBlockState blockState = te.getWorld().getBlockState(te.getPos());
        EnumFacing facing = blockState.getValue(JSGProps.FACING_HORIZONTAL);

        GlStateManager.pushMatrix();
        int rot = FacingHelper.getIntRotation(facing, false);
        GlStateManager.rotate(rot, 0, 1, 0);
        GlStateManager.translate(0, 0, 0.25f);

        ElementEnum.DESTINY_VENT_HOLE.bindTextureAndRender();
        GlStateManager.pushMatrix();
        GlStateManager.rotate((70f * animationStage), 1, 0, 0);
        ElementEnum.DESTINY_VENT_MOVING.bindTextureAndRender();
        if (fireParticles) {
            for (int i = 0; i < 50; i++) {
                boolean orange = (i < 25);
                float coef = orange ? (float) (0.5f * Math.random()) : 1;
                new ParticleBlenderCOBlast((float) (-0.5f + Math.random()), 0, (float) (-0.3f + Math.random() * 0.6f), 2, 2, (-0.1f + (float) (Math.random() * 0.2f)) * (orange ? 0.25f : 1), (0.4f + (-0.1f + (float) (Math.random() * 0.2f))) * coef, (-(0.2f + (-0.1f + (float) (Math.random() * 0.2f)))) * coef, orange, (motion) -> {
                }).spawn(te.getWorld(), te.getPos(), rot, false);
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
