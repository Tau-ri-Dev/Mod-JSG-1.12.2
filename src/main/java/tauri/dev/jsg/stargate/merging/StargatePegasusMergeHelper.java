package tauri.dev.jsg.stargate.merging;

import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.block.stargate.StargatePegasusMemberBlock;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.stargate.EnumMemberVariant;
import tauri.dev.jsg.tileentity.stargate.StargatePegasusBaseTile;
import tauri.dev.jsg.util.JSGAxisAlignedBB;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import tauri.dev.jsg.config.stargate.StargateSizeEnum;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class StargatePegasusMergeHelper extends StargateClassicMergeHelper {

  public static final StargatePegasusMergeHelper INSTANCE = new StargatePegasusMergeHelper();

  /**
   * Bounding box used for {@link StargatePegasusBaseTile} search.
   * Searches 3 blocks to the left/right and 7 blocks down.
   */
  private static final JSGAxisAlignedBB BASE_SEARCH_BOX_SMALL = new JSGAxisAlignedBB(-3, -7, 0, 3, 0, 0);
  private static final JSGAxisAlignedBB BASE_SEARCH_BOX_LARGE = new JSGAxisAlignedBB(-5, -9, 0, 5, 0, 0);

  public static final BlockMatcher BASE_MATCHER = BlockMatcher.forBlock(JSGBlocks.STARGATE_PEGASUS_BASE_BLOCK);
  public static final BlockMatcher MEMBER_MATCHER = BlockMatcher.forBlock(JSGBlocks.STARGATE_PEGASUS_MEMBER_BLOCK);

  private static final List<BlockPos> RING_BLOCKS_SMALL = Arrays.asList(new BlockPos(1, 7, 0), new BlockPos(3, 5, 0), new BlockPos(3, 3, 0), new BlockPos(2, 1, 0), new BlockPos(-2, 1, 0), new BlockPos(-3, 3, 0), new BlockPos(-3, 5, 0), new BlockPos(-1, 7, 0));

  private static final List<BlockPos> CHEVRON_BLOCKS_SMALL = Arrays.asList(new BlockPos(2, 6, 0), new BlockPos(3, 4, 0), new BlockPos(3, 2, 0), new BlockPos(-3, 2, 0), new BlockPos(-3, 4, 0), new BlockPos(-2, 6, 0), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 7, 0));

  private static final List<BlockPos> RING_BLOCKS_LARGE = Arrays.asList(new BlockPos(-1, 0, 0), new BlockPos(-3, 1, 0), new BlockPos(-4, 3, 0), new BlockPos(-5, 4, 0), new BlockPos(-4, 6, 0), new BlockPos(-4, 7, 0), new BlockPos(-2, 9, 0), new BlockPos(-1, 9, 0), new BlockPos(1, 9, 0), new BlockPos(2, 9, 0), new BlockPos(4, 7, 0), new BlockPos(4, 6, 0), new BlockPos(5, 4, 0), new BlockPos(4, 3, 0), new BlockPos(3, 1, 0), new BlockPos(1, 0, 0));

  private static final List<BlockPos> CHEVRON_BLOCKS_LARGE = Arrays.asList(new BlockPos(3, 8, 0), new BlockPos(5, 5, 0), new BlockPos(4, 2, 0), new BlockPos(-4, 2, 0), new BlockPos(-5, 5, 0), new BlockPos(-3, 8, 0), new BlockPos(2, 0, 0), new BlockPos(-2, 0, 0), new BlockPos(0, 9, 0));

  @Override
  public List<BlockPos> getRingBlocks() {
    switch (JSGConfig.stargateSize) {
      case SMALL:
      case MEDIUM:
        return RING_BLOCKS_SMALL;

      case LARGE:
        return RING_BLOCKS_LARGE;

      default:
        return null;
    }
  }

  @Override
  public List<BlockPos> getChevronBlocks() {
    switch (tauri.dev.jsg.config.JSGConfig.stargateSize) {
      case SMALL:
      case MEDIUM:
        return CHEVRON_BLOCKS_SMALL;

      case LARGE:
        return CHEVRON_BLOCKS_LARGE;

      default:
        return null;
    }
  }

  @Override
  @Nullable
  public EnumMemberVariant getMemberVariantFromItemStack(ItemStack stack) {
    if (!(stack.getItem() instanceof ItemBlock)) return null;

    // No need to use .equals() because blocks are singletons
    if (((ItemBlock) stack.getItem()).getBlock() != JSGBlocks.STARGATE_PEGASUS_MEMBER_BLOCK) return null;

    int meta = stack.getMetadata();

    if (meta == JSGBlocks.STARGATE_PEGASUS_MEMBER_BLOCK.RING_META) return EnumMemberVariant.RING;

    if (meta == JSGBlocks.STARGATE_PEGASUS_MEMBER_BLOCK.CHEVRON_META) return EnumMemberVariant.CHEVRON;

    return null;
  }

  @Override
  public JSGAxisAlignedBB getBaseSearchBox() {
    switch (JSGConfig.stargateSize) {
      case SMALL:
      case MEDIUM:
        return BASE_SEARCH_BOX_SMALL;

      case LARGE:
        return BASE_SEARCH_BOX_LARGE;

      default:
        return null;
    }
  }

  @Override
  public boolean matchBase(IBlockState state) {
    return BASE_MATCHER.apply(state);
  }

  @Override
  public boolean matchMember(IBlockState state) {
    return MEMBER_MATCHER.apply(state);
  }

  @Override
  public StargatePegasusMemberBlock getMemberBlock() {
    return JSGBlocks.STARGATE_PEGASUS_MEMBER_BLOCK;
  }
}
