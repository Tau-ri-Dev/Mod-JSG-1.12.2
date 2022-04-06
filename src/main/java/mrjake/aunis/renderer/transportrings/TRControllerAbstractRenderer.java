package mrjake.aunis.renderer.transportrings;

import mrjake.aunis.AunisProps;
import mrjake.aunis.renderer.dialhomedevice.DHDAbstractRendererState;
import mrjake.aunis.tesr.RendererInterface;
import mrjake.aunis.tileentity.dialhomedevice.DHDAbstractTile;
import mrjake.aunis.tileentity.transportrings.TRControllerAbstractTile;
import mrjake.vector.Vector3f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;

public abstract class TRControllerAbstractRenderer extends TileEntitySpecialRenderer<TRControllerAbstractTile> {

	protected TRControllerAbstractTile controllerTile;
	protected EnumFacing facing;

	protected static final Vector3f NORTH_TRANSLATION = new Vector3f(0, 0, 0);
	protected static final Vector3f EAST_TRANSLATION = new Vector3f(1, 0, 0);
	protected static final Vector3f SOUTH_TRANSLATION = new Vector3f(1, 0, 1);
	protected static final Vector3f WEST_TRANSLATION = new Vector3f(0, 0, 1);
	
	public static Vector3f getTranslation(EnumFacing facing) {
		switch (facing) {
			case NORTH:
				return NORTH_TRANSLATION;
				
			case EAST:
				return EAST_TRANSLATION;
				
			case SOUTH:
				return SOUTH_TRANSLATION;
				
			case WEST:
				return WEST_TRANSLATION;
				
			default:
				return null;
		}
	}
	
	public static int getRotation(EnumFacing facing) {
		switch (facing) {
			case NORTH:
				return 0;
				
			case EAST:
				return 270;
				
			case SOUTH:
				return 180;
				
			case WEST:
				return 90;
	
			default:
				return 0;
		}
	}
	
	@Override
	public void render(TRControllerAbstractTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		TRControllerAbstractRendererState rendererState = te.getRendererState();
		IBlockState blockState = te.getWorld().getBlockState(te.getPos());
		facing = blockState.getValue(AunisProps.FACING_HORIZONTAL);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		
		Vector3f tr = getTranslation(facing);
		int rot = getRotation(facing);
		
		GlStateManager.translate(tr.x, tr.y, tr.z);
		GlStateManager.rotate(rot, 0, 1, 0);

		renderController(te, rendererState);
		
		GlStateManager.popMatrix();
	}

	public abstract void renderController(TRControllerAbstractTile te, TRControllerAbstractRendererState rendererState);
}
