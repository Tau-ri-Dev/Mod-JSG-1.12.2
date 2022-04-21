package mrjake.aunis.block.transportrings;

import mrjake.aunis.tileentity.transportrings.TransportRingsAbstractTile;
import mrjake.aunis.tileentity.transportrings.TransportRingsGoauldTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

public class TransportRingsGoauldBlock extends TransportRingsAbstractBlock {

  private static final String BLOCK_NAME = "transportrings_goauld_block";

  public TransportRingsGoauldBlock() {
    super(BLOCK_NAME);
  }

  @Override
  public TransportRingsAbstractTile createTileEntity(World world, IBlockState state) {
    return new TransportRingsGoauldTile();
  }
}
