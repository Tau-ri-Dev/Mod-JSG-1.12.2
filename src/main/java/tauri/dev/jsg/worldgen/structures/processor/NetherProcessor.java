package tauri.dev.jsg.worldgen.structures.processor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.state.IBlockState;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.block.dialhomedevice.DHDAbstractBlock;
import tauri.dev.jsg.block.stargate.StargateAbstractBaseBlock;
import tauri.dev.jsg.block.stargate.StargateAbstractMemberBlock;
import tauri.dev.jsg.stargate.merging.StargateMilkyWayMergeHelper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.Template.BlockInfo;

import javax.annotation.Nonnull;

public class NetherProcessor implements ITemplateProcessor {

	@Override
	public BlockInfo processBlock(@Nonnull World world, @Nonnull BlockPos pos, BlockInfo blockInfoIn) {
		Block blockToPlace = blockInfoIn.blockState.getBlock();
		IBlockState blockInWorldState = world.getBlockState(pos);
		if (blockToPlace instanceof StargateAbstractBaseBlock
				|| blockToPlace instanceof StargateAbstractMemberBlock
				|| blockToPlace instanceof DHDAbstractBlock
				|| blockToPlace instanceof BlockChest
				|| blockToPlace instanceof BlockStructure)
			return blockInfoIn;

		if ((blockToPlace == Blocks.NETHERRACK || blockToPlace == Blocks.QUARTZ_ORE) && world.isAirBlock(pos.down()))
			return null;

		if (world.isAirBlock(pos) || (blockInWorldState.getBlock() == Blocks.LAVA && blockInWorldState.getValue(BlockLiquid.LEVEL) > 0))
			return blockInfoIn;

		return null;
	}
}
