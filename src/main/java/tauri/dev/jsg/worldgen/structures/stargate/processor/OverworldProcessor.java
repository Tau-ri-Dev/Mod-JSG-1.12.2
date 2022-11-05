package tauri.dev.jsg.worldgen.structures.stargate.processor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.Template.BlockInfo;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.block.dialhomedevice.DHDAbstractBlock;
import tauri.dev.jsg.block.stargate.StargateAbstractBaseBlock;
import tauri.dev.jsg.block.stargate.StargateAbstractMemberBlock;
import tauri.dev.jsg.stargate.merging.StargateMilkyWayMergeHelper;

import javax.annotation.Nonnull;

public class OverworldProcessor implements ITemplateProcessor {

	@Override
	public BlockInfo processBlock(@Nonnull World world, @Nonnull BlockPos pos, BlockInfo blockInfoIn) {
		Block block = blockInfoIn.blockState.getBlock();
		IBlockState placedBlockState = world.getBlockState(pos);
		if (block instanceof StargateAbstractBaseBlock || block instanceof StargateAbstractMemberBlock || block instanceof DHDAbstractBlock)
			return blockInfoIn;

		if(block != Blocks.AIR && placedBlockState.getBlock().isLeaves(placedBlockState, world, pos))
			return blockInfoIn;
		if(block != Blocks.AIR && (placedBlockState.getBlock() == Blocks.LOG || placedBlockState.getBlock() == Blocks.LOG2))
			return blockInfoIn;
		if(placedBlockState.getMaterial().isReplaceable() && !world.isAirBlock(pos.down()))
			return blockInfoIn;
		
		if (world.isAirBlock(pos) || ((world.getBlockState(pos).getBlock() == Blocks.LAVA || world.getBlockState(pos).getBlock() == Blocks.WATER) && world.getBlockState(pos).getValue(BlockLiquid.LEVEL) > 0))
			return blockInfoIn;
				
		return null;
	}
}
