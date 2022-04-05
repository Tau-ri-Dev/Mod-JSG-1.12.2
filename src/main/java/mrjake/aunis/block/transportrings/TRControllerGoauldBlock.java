package mrjake.aunis.block.transportrings;

import mrjake.aunis.tileentity.transportrings.TRControllerAbstractTile;
import mrjake.aunis.tileentity.transportrings.TRControllerGoauldTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

public class TRControllerGoauldBlock extends TRControllerAbstractBlock {

  private static final String BLOCK_NAME = "transportrings_controller_block";

  public TRControllerGoauldBlock() {
    super(BLOCK_NAME);
  }
  @Override
  public TRControllerAbstractTile createTileEntity(World world, IBlockState state) {
    return new TRControllerGoauldTile();
  }
}
