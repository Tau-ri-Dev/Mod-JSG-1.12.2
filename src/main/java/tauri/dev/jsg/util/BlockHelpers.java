package tauri.dev.jsg.util;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlock;
import tauri.dev.jsg.creativetabs.JSGAbstractCreativeTab;
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

	public static JSGBlock createSimpleBlock(String id, Material material, JSGAbstractCreativeTab tab) {
		JSGBlock block = new JSGBlock(material);
		block.setRegistryName(JSG.MOD_ID, id);
		block.setUnlocalizedName(JSG.MOD_ID + "." + id);
		block.setCreativeTab(tab);
		return block;
	}
}
