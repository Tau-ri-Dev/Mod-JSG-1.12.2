package tauri.dev.jsg.block.stargate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.renderer.stargate.StargateMilkyWayRenderer;
import tauri.dev.jsg.tileentity.stargate.StargateMilkyWayBaseTile;

public class StargateMilkyWayBaseBlock extends StargateClassicBaseBlock {

	public static final String BLOCK_NAME = "stargate_milkyway_base_block";
	
	public StargateMilkyWayBaseBlock() {
		super(BLOCK_NAME);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new StargateMilkyWayBaseTile();
	}

	@Override
	public Class<? extends TileEntity> getTileEntityClass() {
		return StargateMilkyWayBaseTile.class;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TileEntitySpecialRenderer<? extends TileEntity> getTESR(){
		return new StargateMilkyWayRenderer();
	}
}
