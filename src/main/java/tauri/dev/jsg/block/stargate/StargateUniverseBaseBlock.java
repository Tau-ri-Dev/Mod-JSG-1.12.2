package tauri.dev.jsg.block.stargate;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.renderer.stargate.StargateOrlinRenderer;
import tauri.dev.jsg.renderer.stargate.StargateUniverseRenderer;
import tauri.dev.jsg.tileentity.stargate.StargateUniverseBaseTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public final class StargateUniverseBaseBlock extends StargateClassicBaseBlock {

	public static final String BLOCK_NAME = "stargate_universe_base_block";
	
	public StargateUniverseBaseBlock() {
		super(BLOCK_NAME);
		setResistance(40.0f);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new StargateUniverseBaseTile();
	}

	@Override
	public Class<? extends TileEntity> getTileEntityClass() {
		return StargateUniverseBaseTile.class;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TileEntitySpecialRenderer<? extends TileEntity> getTESR() {
		return new StargateUniverseRenderer();
	}
}
