package tauri.dev.jsg.renderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import tauri.dev.jsg.beamer.BeamerBeam;
import tauri.dev.jsg.beamer.BeamerModeEnum;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.tileentity.BeamerTile;

import javax.annotation.Nonnull;

public class BeamerRenderer extends TileEntitySpecialRenderer<BeamerTile> {

    @Override
    public void render(@Nonnull BeamerTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

        if (JSGConfig.General.debug.renderBoundingBoxes) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            te.getRenderBoxForDisplay().render();
            GlStateManager.popMatrix();
        }

        if (te.getMode() != BeamerModeEnum.NONE && te.beamRadiusClient > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
            GlStateManager.rotate(180 - te.getFacing().getHorizontalAngle(), 0, 1, 0);

            BeamerBeam beam = BeamerBeam.getBeam(te.beamOffsetFromTargetZ, te.beamOffsetFromGateTarget, te.beamOffsetFromTargetX, te.beamOffsetFromTargetY, te.beamRadiusClient, BeamerTile.BEAMER_BEAM_MAX_RADIUS, te.getFacing());
            beam.render(partialTicks, te.getWorld().getTotalWorldTime(), te.getRole(), te.getMode().colors, (te.getMode() == BeamerModeEnum.FLUID), te.lastFluidTransferred);
            GlStateManager.popMatrix();
            GlStateManager.enableCull();
        }
    }

    @Override
    public boolean isGlobalRenderer(@Nonnull BeamerTile te) {
        return true;
    }
}
