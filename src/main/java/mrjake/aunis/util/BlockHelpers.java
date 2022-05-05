package mrjake.aunis.util;

import mrjake.aunis.Aunis;
import mrjake.aunis.block.AunisBlock;
import mrjake.aunis.creativetabs.AunisAbstractCreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.List;

public class BlockHelpers {
	
	public static boolean isBlockDirectlyUnderSky(IBlockAccess world, BlockPos pos) {
		while (pos.getY() < 255) {
			pos = pos.up();
			
			IBlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			
			if (!world.isAirBlock(pos) && block != Blocks.LEAVES && block != Blocks.LEAVES2)
				return false;
		}
		
		return true;
	}
	
	/**
	 * Returns {@link BlockPos} with largest Y-coord value.
	 * 
	 * @param list List of positions.
	 * @return largest Y-coord {@link BlockPos}. {@code null} if list empty.
	 */
	public static BlockPos getHighest(List<BlockPos> list) {
		int maxy = -1;
		BlockPos top = null;
		
		for (BlockPos pos : list) {
			if (pos.getY() > maxy) {
				maxy = pos.getY();
				top = pos;
			}
		}
		
		return top;
	}

	public static AunisBlock createSimpleBlock(String id, Material material, AunisAbstractCreativeTab tab) {
		AunisBlock block = new AunisBlock(material);
		block.setRegistryName(Aunis.MOD_ID, id);
		block.setUnlocalizedName(Aunis.MOD_ID + "." + id);
		block.setCreativeTab(tab);
		return block;
	}
}
