package tauri.dev.jsg.worldgen.structures.processor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.Template.BlockInfo;
import tauri.dev.jsg.block.JSGBlock;
import tauri.dev.jsg.block.dialhomedevice.DHDAbstractBlock;
import tauri.dev.jsg.block.stargate.StargateAbstractBaseBlock;
import tauri.dev.jsg.block.stargate.StargateAbstractMemberBlock;

import javax.annotation.Nonnull;

public class OverworldProcessor implements ITemplateProcessor {

    @Override
    public BlockInfo processBlock(@Nonnull World world, @Nonnull BlockPos pos, BlockInfo blockInfoIn) {
        Block blockToPlace = blockInfoIn.blockState.getBlock();
        IBlockState blockInWorldState = world.getBlockState(pos);
        if (blockToPlace instanceof StargateAbstractBaseBlock
                || blockToPlace instanceof StargateAbstractMemberBlock
                || blockToPlace instanceof DHDAbstractBlock
                || blockToPlace instanceof BlockChest
                || blockToPlace instanceof BlockStructure
                || blockToPlace instanceof JSGBlock
        )
            return blockInfoIn;

        if (blockToPlace != Blocks.STONEBRICK && blockToPlace != Blocks.STONE_BRICK_STAIRS && world.isAirBlock(pos.down()))
            return null;

        if (blockToPlace != Blocks.AIR && blockInWorldState.getBlock().isLeaves(blockInWorldState, world, pos))
            return blockInfoIn;
        if (blockToPlace != Blocks.AIR && (blockInWorldState.getBlock() == Blocks.LOG || blockInWorldState.getBlock() == Blocks.LOG2))
            return blockInfoIn;
        if (blockInWorldState.getMaterial().isReplaceable() && !world.isAirBlock(pos.down()))
            return blockInfoIn;

        if (world.isAirBlock(pos) || ((world.getBlockState(pos).getBlock() == Blocks.LAVA || world.getBlockState(pos).getBlock() == Blocks.WATER) && world.getBlockState(pos).getValue(BlockLiquid.LEVEL) > 0))
            return blockInfoIn;

        return null;
    }
}
