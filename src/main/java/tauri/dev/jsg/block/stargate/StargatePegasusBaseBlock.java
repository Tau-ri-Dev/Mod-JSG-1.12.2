package tauri.dev.jsg.block.stargate;

import tauri.dev.jsg.tileentity.stargate.StargatePegasusBaseTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public final class StargatePegasusBaseBlock extends StargateClassicBaseBlock {

  public static final String BLOCK_NAME = "stargate_pegasus_base_block";

  public StargatePegasusBaseBlock() {
    super(BLOCK_NAME);
  }

  @Override
  public TileEntity createTileEntity(World world, IBlockState state) {
    return new StargatePegasusBaseTile();
  }

  @Override
  public Class<? extends TileEntity> getTileEntityClass() {
    return StargatePegasusBaseTile.class;
  }
}
