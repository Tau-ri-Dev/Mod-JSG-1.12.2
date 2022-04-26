package mrjake.aunis.block.transportrings;

import mrjake.aunis.raycaster.RaycasterRingsGoauldController;
import mrjake.aunis.tileentity.transportrings.TRControllerAbstractTile;
import mrjake.aunis.tileentity.transportrings.TRControllerGoauldTile;
import mrjake.aunis.tileentity.transportrings.TransportRingsGoauldTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TRControllerGoauldBlock extends TRControllerAbstractBlock {

  private static final String BLOCK_NAME = "transportrings_controller_goauld_block";

  public TRControllerGoauldBlock() {
    super(BLOCK_NAME);
  }

  @Override
  public TRControllerAbstractTile createTileEntity(World world, IBlockState state) {
    return new TRControllerGoauldTile();
  }

  @Override
  public void onRayCasterActivated(World world, BlockPos pos, EntityPlayer player) {
    RaycasterRingsGoauldController.INSTANCE.onActivated(world, pos, player);
  }

  @Override
  public Class<? extends TileEntity> getTileEntityClass() {
    return TRControllerGoauldTile.class;
  }
}
