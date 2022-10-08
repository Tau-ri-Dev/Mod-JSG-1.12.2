package tauri.dev.jsg.block.transportrings;

import tauri.dev.jsg.raycaster.RaycasterRingsGoauldController;
import tauri.dev.jsg.tileentity.transportrings.TRControllerAbstractTile;
import tauri.dev.jsg.tileentity.transportrings.TRControllerGoauldTile;
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
