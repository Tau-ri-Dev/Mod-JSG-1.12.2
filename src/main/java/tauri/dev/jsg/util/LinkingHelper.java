package tauri.dev.jsg.util;

import li.cil.oc.api.event.RackMountableRenderEvent.TileEntity;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDAbstractTile;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class LinkingHelper {
  private static int nextLinkId = 0;

  /**
   * Finds closest block of the given type within given radius.
   *
   * @param world        World instance.
   * @param startPos     Starting position.
   * @param radius       Radius. Subtracted and added to the startPos.
   * @param targetBlock  Searched block instance. Must provide {@link TileEntity} and {@link TileEntity} should implement {@link ILinkable}.
   *
   * @return Found block's {@link BlockPos}. Null if not found.
   */

  @Nullable
  public static BlockPos findClosestUnlinked(World world, BlockPos startPos, BlockPos radius, Block targetBlock, int linkId) {
    double closestDistance = Double.MAX_VALUE;
    BlockPos closest = null;

    for (BlockPos target : BlockPos.getAllInBoxMutable(startPos.subtract(radius), startPos.add(radius))) {
      if (world.getBlockState(target).getBlock() == targetBlock) {

        ILinkable linkedTile = (ILinkable) world.getTileEntity(target);

        if (linkedTile != null && (linkedTile.canLinkTo() || linkId == linkedTile.getLinkId())) {
          if(linkedTile instanceof StargateClassicBaseTile){
            if(!((StargateClassicBaseTile) linkedTile).isMerged()) continue;
          }
          double distanceSq = startPos.distanceSq(target);

          if (distanceSq < closestDistance) {
            closestDistance = distanceSq;
            closest = target.toImmutable();
          }
        }
      }
    }

    return closest;
  }

  @Nullable
  public static BlockPos findClosestUnlinked(World world, BlockPos startPos, BlockPos radius, Block[] targetBlocks, int linkId) {
    double closestDistance = Double.MAX_VALUE;
    BlockPos closest = null;

    for (BlockPos target : BlockPos.getAllInBoxMutable(startPos.subtract(radius), startPos.add(radius))) {
      for (Block targetBlock : targetBlocks) {
        if (world.getBlockState(target).getBlock() == targetBlock) {

          ILinkable linkedTile = (ILinkable) world.getTileEntity(target);

          if (linkedTile.canLinkTo() || linkId == linkedTile.getLinkId()) {
            double distanceSq = startPos.distanceSq(target);

            if (distanceSq < closestDistance) {
              closestDistance = distanceSq;
              closest = target.toImmutable();
            }
          }
        }
      }
    }

    return closest;
  }

  @Nullable
  public static BlockPos findClosestPos(World world, BlockPos startPos, BlockPos radius, Block[] targetBlocks, ArrayList<BlockPos> blacklist) {
    double closestDistance = Double.MAX_VALUE;
    BlockPos closest = null;

    for (BlockPos target : BlockPos.getAllInBoxMutable(startPos.subtract(radius), startPos.add(radius))) {
      for (Block targetBlock : targetBlocks) {
        if (world.getBlockState(target).getBlock() == targetBlock && !(blacklist.contains(target))) {
          double distanceSq = startPos.distanceSq(target);

          if (distanceSq < closestDistance) {
            closestDistance = distanceSq;
            closest = target.toImmutable();
          }
        }
      }
    }

    return closest;
  }

  /**
   * Returns proper DHD range.
   *
   * @return DHD range.
   */
  public static BlockPos getDhdRange() {
    int xz = JSGConfig.DialHomeDevice.mechanics.rangeFlat;
    int y = JSGConfig.DialHomeDevice.mechanics.rangeVertical;

    return new BlockPos(xz, y, xz);
  }

  public static void updateLinkedGate(World world, BlockPos gatePos, BlockPos dhdPos) {
    StargateAbstractBaseTile gateTile = (StargateAbstractBaseTile) world.getTileEntity(gatePos);
    DHDAbstractTile dhdTile = (DHDAbstractTile) world.getTileEntity(dhdPos);

    if (dhdTile != null && gateTile != null) {
      int linkId = getLinkId();
      dhdTile.setLinkedGate(gatePos, linkId);
      gateTile.setLinkedDHD(dhdPos, linkId);
    }
  }

  public static int getLinkId() {
    return ++nextLinkId;
  }
}
