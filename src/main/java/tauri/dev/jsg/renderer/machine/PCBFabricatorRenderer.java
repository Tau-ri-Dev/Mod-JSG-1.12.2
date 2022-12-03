package tauri.dev.jsg.renderer.machine;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.beamer.BeamerBeam;
import tauri.dev.jsg.beamer.BeamerRoleEnum;
import tauri.dev.jsg.particle.ParticleBlenderSmoke;
import tauri.dev.jsg.renderer.BlockRenderer;
import tauri.dev.jsg.tileentity.machine.PCBFabricatorTile;
import tauri.dev.jsg.util.FacingToRotation;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class PCBFabricatorRenderer extends TileEntitySpecialRenderer<PCBFabricatorTile> {

    @Override
    public void render(@Nonnull PCBFabricatorTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        PCBFabricatorRendererState rendererState = (PCBFabricatorRendererState) te.getRendererState();
        if (rendererState != null) {
            long tick = te.getWorld().getTotalWorldTime();

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.color(1, 1, 1, 1f);
            if (rendererState.craftingStack != null) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.5f, 0.4f, 0.5f);
                if (rendererState.isWorking) {
                    GlStateManager.pushMatrix();

                    BeamerBeam beam = new BeamerBeam(90f, 0, 0.6f, 0.02f);
                    float[] colors = new float[]{
                            rendererState.colors.get(0),
                            rendererState.colors.get(1),
                            rendererState.colors.get(2)
                    };
                    beam.render(partialTicks, tick, BeamerRoleEnum.RECEIVE, colors, false, null);

                    GlStateManager.popMatrix();
                }

                GlStateManager.pushMatrix();
                GlStateManager.scale(0.8f, 0.8f, 0.8f);

                EnumFacing facing = te.getWorld().getBlockState(te.getPos()).getValue(JSGProps.FACING_HORIZONTAL);
                int rotation = FacingToRotation.getIntRotation(facing);

                GlStateManager.rotate(rotation, 0, 1, 0);
                GlStateManager.rotate(90, 1, 0, 0);

                GlStateManager.translate(0, -0.13, 0.26);

                BlockRenderer.renderItemOnGround(rendererState.craftingStack);
                GlStateManager.popMatrix();
                GlStateManager.popMatrix();
                if (rendererState.isWorking) {
                    new ParticleBlenderSmoke(-1f + ((float) Math.random() * 2), -1f + ((float) Math.random() * 2), 2f, 7, 9, 0, 0, true, (motion) -> {
                        motion.x = 0;
                        motion.z = 0;
                    }).spawn(te.getWorld(), te.getPos(), 90, true);
                }

            }
            GlStateManager.popMatrix();
        }
    }
}
