package tauri.dev.jsg.stargate.merging;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.util.JSGAxisAlignedBB;
import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.block.stargate.StargateMilkyWayBaseBlock;
import tauri.dev.jsg.block.stargate.StargateMilkyWayMemberBlock;
import tauri.dev.jsg.config.stargate.StargateSizeEnum;
import tauri.dev.jsg.stargate.EnumMemberVariant;
import tauri.dev.jsg.tileentity.stargate.StargateMilkyWayBaseTile;
import tauri.dev.jsg.util.FacingToRotation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StargateMilkyWayMergeHelper extends StargateClassicMergeHelper {
	
	public static final StargateMilkyWayMergeHelper INSTANCE = new StargateMilkyWayMergeHelper();
	
	/**
	 * Bounding box used for {@link StargateMilkyWayBaseTile} search.
	 * Searches 3 blocks to the left/right and 7 blocks down.
	 */
	private static final JSGAxisAlignedBB BASE_SEARCH_BOX_SMALL = new JSGAxisAlignedBB(-3, -7, 0, 3, 0, 0);
	private static final JSGAxisAlignedBB BASE_SEARCH_BOX_LARGE = new JSGAxisAlignedBB(-5, -9, 0, 5, 0, 0);
	
	public static final BlockMatcher BASE_MATCHER = BlockMatcher.forBlock(JSGBlocks.STARGATE_MILKY_WAY_BASE_BLOCK);
	public static final BlockMatcher MEMBER_MATCHER = BlockMatcher.forBlock(JSGBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK);
		
	private static final List<BlockPos> RING_BLOCKS_SMALL = Arrays.asList(
			new BlockPos(1, 7, 0),
			new BlockPos(3, 5, 0),
			new BlockPos(3, 3, 0),
			new BlockPos(2, 1, 0),
			new BlockPos(-2, 1, 0),
			new BlockPos(-3, 3, 0),
			new BlockPos(-3, 5, 0),
			new BlockPos(-1, 7, 0));
	
	private static final List<BlockPos> CHEVRON_BLOCKS_SMALL = Arrays.asList(
			new BlockPos(2, 6, 0),
			new BlockPos(3, 4, 0),
			new BlockPos(3, 2, 0),
			new BlockPos(-3, 2, 0),
			new BlockPos(-3, 4, 0),
			new BlockPos(-2, 6, 0),
			new BlockPos(1, 0, 0),
			new BlockPos(-1, 0, 0),
			new BlockPos(0, 7, 0));
	
	private static final List<BlockPos> RING_BLOCKS_LARGE = Arrays.asList(
			new BlockPos(-1, 0, 0), 
			new BlockPos(-3, 1, 0),
			new BlockPos(-4, 3, 0), 
			new BlockPos(-5, 4, 0), 
			new BlockPos(-4, 6, 0), 
			new BlockPos(-4, 7, 0), 
			new BlockPos(-2, 9, 0), 
			new BlockPos(-1, 9, 0), 
			new BlockPos(1, 9, 0), 
			new BlockPos(2, 9, 0), 
			new BlockPos(4, 7, 0), 
			new BlockPos(4, 6, 0), 
			new BlockPos(5, 4, 0), 
			new BlockPos(4, 3, 0), 
			new BlockPos(3, 1, 0), 
			new BlockPos(1, 0, 0));
	
	private static final List<BlockPos> CHEVRON_BLOCKS_LARGE = Arrays.asList(
			new BlockPos(3, 8, 0), 
			new BlockPos(5, 5, 0), 
			new BlockPos(4, 2, 0), 
			new BlockPos(-4, 2, 0), 
			new BlockPos(-5, 5, 0), 
			new BlockPos(-3, 8, 0), 
			new BlockPos(2, 0, 0),
			new BlockPos(-2, 0, 0), 
			new BlockPos(0, 9, 0));
	
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
		if (!(stack.getItem() instanceof ItemBlock))
			return null;
		
		// No need to use .equals() because blocks are singletons
		if (((ItemBlock) stack.getItem()).getBlock() != JSGBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK)
			return null;
		
		int meta = stack.getMetadata();
		
		if (meta == JSGBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK.RING_META)
			return EnumMemberVariant.RING;
		
		if (meta == JSGBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK.CHEVRON_META)
			return EnumMemberVariant.CHEVRON;
		
		return null;
	}
	
	@Override
	public JSGAxisAlignedBB getBaseSearchBox() {
		switch (tauri.dev.jsg.config.JSGConfig.stargateSize) {
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
	public StargateMilkyWayMemberBlock getMemberBlock() {
		return JSGBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK;
	}
	
	/**
	 * Converts merged Stargate from old pattern (1.5)
	 * to new pattern (1.6).
	 * 
	 * @param world {@link World} instance.
	 * @param basePos Position of {@link StargateMilkyWayBaseBlock} the tiles should be linked to.
	 * @param baseFacing Facing of {@link StargateMilkyWayBaseBlock}.
	 * @param currentStargateSize Current Stargate size as read from NBT.
	 * @param targetStargateSize Target Stargate size as defined in config.
	 */
	public void convertToPattern(World world, BlockPos basePos, EnumFacing baseFacing, StargateSizeEnum currentStargateSize, StargateSizeEnum targetStargateSize) {
		JSG.logger.debug(basePos + ": Converting Stargate from " + currentStargateSize + " to " + targetStargateSize);
		List<BlockPos> oldPatternBlocks = new ArrayList<BlockPos>();
		
		switch (currentStargateSize) {
			case SMALL:
			case MEDIUM:
				oldPatternBlocks.addAll(RING_BLOCKS_SMALL);
				oldPatternBlocks.addAll(CHEVRON_BLOCKS_SMALL);
				break;
				
			case LARGE:
				oldPatternBlocks.addAll(RING_BLOCKS_LARGE);
				oldPatternBlocks.addAll(CHEVRON_BLOCKS_LARGE);
				break;
		}
		
		for (BlockPos pos : oldPatternBlocks)
			world.setBlockToAir(pos.rotate(FacingToRotation.get(baseFacing)).add(basePos));
		
		IBlockState memberState = JSGBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK.getDefaultState()
				.withProperty(JSGProps.FACING_HORIZONTAL, baseFacing)
				.withProperty(JSGProps.RENDER_BLOCK, false);
		
		for (BlockPos pos : getRingBlocks())
			world.setBlockState(pos.rotate(FacingToRotation.get(baseFacing)).add(basePos), memberState.withProperty(JSGProps.MEMBER_VARIANT, EnumMemberVariant.RING));
		
		for (BlockPos pos : getChevronBlocks())
			world.setBlockState(pos.rotate(FacingToRotation.get(baseFacing)).add(basePos), memberState.withProperty(JSGProps.MEMBER_VARIANT, EnumMemberVariant.CHEVRON));
	}
}
