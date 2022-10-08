package tauri.dev.jsg.block.transportrings;

import tauri.dev.jsg.tileentity.transportrings.TransportRingsAbstractTile;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsOriTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TransportRingsOriBlock extends TransportRingsAbstractBlock {

  private static final String BLOCK_NAME = "transportrings_ori_block";

  public TransportRingsOriBlock() {
    super(BLOCK_NAME);
  }

  @Override
  public TransportRingsAbstractTile createTileEntity(World world, IBlockState state) {
    return new TransportRingsOriTile();
  }

  @Override
  public Class<? extends TileEntity> getTileEntityClass() {
    return TransportRingsOriTile.class;
  }
}
