package tauri.dev.jsg.stargate.merging;

import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.jsg.block.stargate.StargateMilkyWayBaseBlock;
import tauri.dev.jsg.block.stargate.StargateMilkyWayMemberBlock;
import tauri.dev.jsg.stargate.EnumMemberVariant;
import tauri.dev.jsg.tileentity.stargate.StargateClassicMemberTile;
import tauri.dev.jsg.tileentity.stargate.StargateMilkyWayMemberTile;
import tauri.dev.jsg.util.FacingToRotation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class StargateClassicMergeHelper extends StargateAbstractMergeHelper {

  protected boolean checkMemberBlock(IBlockAccess blockAccess, BlockPos pos, EnumFacing facing, EnumMemberVariant variant) {
    IBlockState state = blockAccess.getBlockState(pos);

    return matchMember(state) && state.getValue(JSGProps.FACING_HORIZONTAL) == facing && state.getValue(JSGProps.MEMBER_VARIANT) == variant;
  }

  protected void updateMemberMergeStatus(World world, BlockPos checkPos, BlockPos basePos, EnumFacing baseFacing, boolean shouldBeMerged) {
    checkPos = checkPos.rotate(FacingToRotation.get(baseFacing)).add(basePos);
    IBlockState state = world.getBlockState(checkPos);

    if (matchMember(state)) {
      StargateClassicMemberTile memberTile = (StargateClassicMemberTile) world.getTileEntity(checkPos);

      if ((shouldBeMerged && !memberTile.isMerged()) || (memberTile.isMerged() && memberTile.getBasePos().equals(basePos))) {

        ItemStack camoStack = memberTile.getCamoItemStack();
        if (camoStack != null) {
          InventoryHelper.spawnItemStack(world, checkPos.getX(), checkPos.getY(), checkPos.getZ(), camoStack);
        }

        if (memberTile.getCamoState() != null) {
          memberTile.setCamoState(null);
        }

        // This also sets merge status
        memberTile.setBasePos(shouldBeMerged ? basePos : null);

        world.setBlockState(checkPos, state.withProperty(JSGProps.RENDER_BLOCK, !shouldBeMerged), 3);
      }
    }
  }

  /**
   * Updates the {@link StargateMilkyWayBaseBlock} position of the
   * {@link StargateMilkyWayMemberTile}.
   *
   * @param blockAccess Usually {@link World}.
   * @param pos         Position of the currently updated {@link StargateMilkyWayMemberBlock}.
   * @param basePos     Position of {@link StargateMilkyWayBaseBlock} the tiles should be linked to.
   * @param baseFacing  Facing of {@link StargateMilkyWayBaseBlock}.
   */
  private void updateMemberBasePos(IBlockAccess blockAccess, BlockPos pos, BlockPos basePos, EnumFacing baseFacing) {
    IBlockState state = blockAccess.getBlockState(pos);

    if (matchMember(state)) {
      StargateClassicMemberTile memberTile = (StargateClassicMemberTile) blockAccess.getTileEntity(pos);

      memberTile.setBasePos(basePos);
    }
  }

  /**
   * Updates all {@link StargateMilkyWayMemberTile} to contain
   * correct {@link StargateMilkyWayBaseBlock} position.
   *
   * @param blockAccess Usually {@link World}.
   * @param basePos     Position of {@link StargateMilkyWayBaseBlock} the tiles should be linked to.
   * @param baseFacing  Facing of {@link StargateMilkyWayBaseBlock}.
   */
  @Override
  public void updateMembersBasePos(IBlockAccess blockAccess, BlockPos basePos, EnumFacing baseFacing) {
    for (BlockPos pos : getRingBlocks())
      updateMemberBasePos(blockAccess, pos.rotate(FacingToRotation.get(baseFacing)).add(basePos), basePos, baseFacing);

    for (BlockPos pos : getChevronBlocks())
      updateMemberBasePos(blockAccess, pos.rotate(FacingToRotation.get(baseFacing)).add(basePos), basePos, baseFacing);
  }
}
