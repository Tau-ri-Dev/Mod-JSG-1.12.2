package mrjake.aunis.block.transportrings;

import mrjake.aunis.tileentity.transportrings.TransportRingsAbstractTile;
import mrjake.aunis.tileentity.transportrings.TransportRingsOriTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

public class TransportRingsOriBlock extends TransportRingsAbstractBlock {

  private static final String blockName = "transportrings_ori_block";

  public TransportRingsOriBlock() {
    super(blockName);
  }

  @Override
  public TransportRingsAbstractTile createTileEntity(World world, IBlockState state) {
    return new TransportRingsOriTile();
  }
}
