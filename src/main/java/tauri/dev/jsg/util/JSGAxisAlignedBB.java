package tauri.dev.jsg.util;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import tauri.dev.vector.Vector3f;

public class JSGAxisAlignedBB extends AxisAlignedBB {
	public JSGAxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2) {
        super(x1, y1, z1, x2, y2, z2);
    }
	
	public JSGAxisAlignedBB(BlockPos pos1, BlockPos pos2) {
        this(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
    }

	public JSGAxisAlignedBB(Vector3f pos1, Vector3f pos2) {
		this(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
	}

    public BlockPos getCenterBlockPos(){
		return new BlockPos(getCenter().x, getCenter().y, getCenter().z);
	}

	public JSGAxisAlignedBB rotate(int angle) {
		switch (angle) {
			case 0:
				return this;
				
			case 90:
				return new JSGAxisAlignedBB(-minZ, minY, minX, -maxZ, maxY, maxX);
			
			case 180:
				return new JSGAxisAlignedBB(-minX, minY, -minZ, -maxX, maxY, -maxZ);
				
			case 270:
			case -90:
				return new JSGAxisAlignedBB(minZ, minY, -minX, maxZ, maxY, -maxX);
				
			default:
				throw new IllegalArgumentException("Angle not one of [0, 90, 180, 270, -90]");
		}
	}
	
	public JSGAxisAlignedBB rotate(EnumFacing facing) {
		if(facing != EnumFacing.UP && facing != EnumFacing.DOWN)
			return rotate((int) facing.getHorizontalAngle());

		switch (facing) {

			case UP:
				return new JSGAxisAlignedBB(minX, minZ, -minY, maxX, maxZ, -maxY);
			case DOWN:
				return new JSGAxisAlignedBB(minX, -minZ, minY, maxX, -maxZ, maxY);
			default:
				break;
		}
		return this;
	}
	
	@Override
	public JSGAxisAlignedBB offset(double x, double y, double z) {
        return new JSGAxisAlignedBB(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }
	
	@Override
	public JSGAxisAlignedBB offset(BlockPos pos) {
		return offset(pos.getX(), pos.getY(), pos.getZ());
	}
	
	public JSGAxisAlignedBB grow(double x, double y, double z) {
        double d0 = this.minX - x;
        double d1 = this.minY - y;
        double d2 = this.minZ - z;
        double d3 = this.maxX + x;
        double d4 = this.maxY + y;
        double d5 = this.maxZ + z;
        return new JSGAxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }
	
	public BlockPos getMinBlockPos() {
		return new BlockPos(minX, minY, minZ);
	}
	
	public BlockPos getMaxBlockPos() {
		return new BlockPos(maxX, maxY, maxZ);
	}
	
	@SideOnly(Side.CLIENT)
	public void render() {
		GlStateManager.color(1.0f, 0, 0);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 15 * 16, 15 * 16);
				
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK,GL11.GL_LINE);
		GlStateManager.glBegin(GL11.GL_QUADS);
		
		GL11.glColor3f(0.0f,1.0f,0.0f);
	    GL11.glVertex3d(maxX, maxY, minZ);
	    GL11.glVertex3d(minX, maxY, minZ);
	    GL11.glVertex3d(minX, maxY, maxZ);
	    GL11.glVertex3d(maxX, maxY, maxZ);
	    
	    GL11.glColor3f(1.0f,0.5f,0.0f);
	    GL11.glVertex3d(maxX, minY, maxZ);
	    GL11.glVertex3d(minX, minY, maxZ);
	    GL11.glVertex3d(minX, minY, minZ);
	    GL11.glVertex3d(maxX, minY, minZ);
	    
	    GL11.glColor3f(1.0f,0.0f,0.0f);
	    GL11.glVertex3d(maxX, maxY, maxZ);
	    GL11.glVertex3d(minX, maxY, maxZ);
	    GL11.glVertex3d(minX, minY, maxZ);
	    GL11.glVertex3d(maxX, minY, maxZ);
	    
	    GL11.glColor3f(1.0f,1.0f,0.0f);
	    GL11.glVertex3d(maxX, minY, minZ);
	    GL11.glVertex3d(minX, minY, minZ);
	    GL11.glVertex3d(minX, maxY, minZ);
	    GL11.glVertex3d(maxX, maxY, minZ);
	    
	    GL11.glColor3f(0.0f,0.0f,1.0f);
	    GL11.glVertex3d(minX, maxY, maxZ);
	    GL11.glVertex3d(minX, maxY, minZ);
	    GL11.glVertex3d(minX, minY, minZ);
	    GL11.glVertex3d(minX, minY, maxZ);
	    
	    GL11.glColor3f(1.0f,0.0f,1.0f);
	    GL11.glVertex3d(maxX, maxY, minZ);
	    GL11.glVertex3d(maxX, maxY, maxZ);
	    GL11.glVertex3d(maxX, minY, maxZ);
	    GL11.glVertex3d(maxX, minY, minZ);
		
		GlStateManager.glEnd();
		
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK,GL11.GL_FILL);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
	}
}
