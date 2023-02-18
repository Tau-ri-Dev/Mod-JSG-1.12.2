package tauri.dev.jsg.renderer.transportrings.controller;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import tauri.dev.jsg.block.transportrings.controller.TRControllerAbstractBlock;
import tauri.dev.jsg.tileentity.transportrings.controller.TRControllerAbstractTile;
import tauri.dev.jsg.util.FacingHelper;
import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.vector.Vector3f;

public abstract class TRControllerAbstractRenderer extends TileEntitySpecialRenderer<TRControllerAbstractTile> {

    protected EnumFacing facing;

    protected static final Vector3f NORTH_TRANSLATION = new Vector3f(0, 0, 0);
    protected static final Vector3f EAST_TRANSLATION = new Vector3f(1, 0, 0);
    protected static final Vector3f SOUTH_TRANSLATION = new Vector3f(1, 0, 1);
    protected static final Vector3f WEST_TRANSLATION = new Vector3f(0, 0, 1);

    public static Vector3f getTranslation(EnumFacing facing) {
        switch (facing) {
			case EAST:
                return EAST_TRANSLATION;

            case SOUTH:
                return SOUTH_TRANSLATION;

            case WEST:
                return WEST_TRANSLATION;

            default:
                return NORTH_TRANSLATION;
        }
    }

    @Override
    public void render(TRControllerAbstractTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        TRControllerAbstractRendererState rendererState = te.getRendererState();
        if (rendererState == null) return;
        if (!(te.getWorld().getBlockState(te.getPos()).getBlock() instanceof TRControllerAbstractBlock)) return;
        rendererState.iterate(getWorld(), partialTicks);
        IBlockState blockState = te.getWorld().getBlockState(te.getPos());
        facing = blockState.getValue(JSGProps.FACING_HORIZONTAL);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        Vector3f tr = getTranslation(facing);
        int rot = FacingHelper.getIntRotation(facing, false);

        GlStateManager.translate(tr.x, tr.y, tr.z);
        GlStateManager.rotate(rot, 0, 1, 0);

        renderController(te, rendererState);

        GlStateManager.popMatrix();
    }

    public abstract void renderController(TRControllerAbstractTile te, TRControllerAbstractRendererState rendererState);
}
