package tauri.dev.jsg.stargate.merging;

import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.block.stargate.StargateUniverseMemberBlock;
import tauri.dev.jsg.stargate.EnumMemberVariant;
import tauri.dev.jsg.tileentity.stargate.StargateMilkyWayBaseTile;
import tauri.dev.jsg.util.JSGAxisAlignedBB;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class StargateUniverseMergeHelper extends StargateClassicMergeHelper {
	
	public static final StargateUniverseMergeHelper INSTANCE = new StargateUniverseMergeHelper();
	
	/**
	 * Bounding box used for {@link StargateMilkyWayBaseTile} search.
	 * Searches 3 blocks to the left/right and 7 blocks down.
	 */
	private static final JSGAxisAlignedBB BASE_SEARCH_BOX = new JSGAxisAlignedBB(-3, -7, 0, 3, 0, 0);
	
	public static final BlockMatcher BASE_MATCHER = BlockMatcher.forBlock(JSGBlocks.STARGATE_UNIVERSE_BASE_BLOCK);
	public static final BlockMatcher MEMBER_MATCHER = BlockMatcher.forBlock(JSGBlocks.STARGATE_UNIVERSE_MEMBER_BLOCK);
		
	private static final List<BlockPos> RING_BLOCKS = Arrays.asList(
			new BlockPos(1, 7, 0),
			new BlockPos(3, 5, 0),
			new BlockPos(3, 3, 0),
			new BlockPos(2, 1, 0),
			new BlockPos(-2, 1, 0),
			new BlockPos(-3, 3, 0),
			new BlockPos(-3, 5, 0),
			new BlockPos(-1, 7, 0));
	
	private static final List<BlockPos> CHEVRON_BLOCKS = Arrays.asList(
			new BlockPos(2, 6, 0),
			new BlockPos(3, 4, 0),
			new BlockPos(3, 2, 0),
			new BlockPos(-3, 2, 0),
			new BlockPos(-3, 4, 0),
			new BlockPos(-2, 6, 0),
			new BlockPos(1, 0, 0),
			new BlockPos(-1, 0, 0),
			new BlockPos(0, 7, 0));
	
	@Override
	public List<BlockPos> getRingBlocks() {
		return RING_BLOCKS;
	}
	
	@Override
	public List<BlockPos> getChevronBlocks() {
		return CHEVRON_BLOCKS;
	}
	
	@Override
	@Nullable
	public EnumMemberVariant getMemberVariantFromItemStack(ItemStack stack) {
		if (!(stack.getItem() instanceof ItemBlock))
			return null;
		
		// No need to use .equals() because blocks are singletons
		if (((ItemBlock) stack.getItem()).getBlock() != JSGBlocks.STARGATE_UNIVERSE_MEMBER_BLOCK)
			return null;
		
		int meta = stack.getMetadata();
		
		if (meta == JSGBlocks.STARGATE_UNIVERSE_MEMBER_BLOCK.RING_META)
			return EnumMemberVariant.RING;
		
		if (meta == JSGBlocks.STARGATE_UNIVERSE_MEMBER_BLOCK.CHEVRON_META)
			return EnumMemberVariant.CHEVRON;
		
		return null;
	}
	
	@Override
	public JSGAxisAlignedBB getBaseSearchBox() {
		return BASE_SEARCH_BOX;
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
	public StargateUniverseMemberBlock getMemberBlock() {
		return JSGBlocks.STARGATE_UNIVERSE_MEMBER_BLOCK;
	}
}
