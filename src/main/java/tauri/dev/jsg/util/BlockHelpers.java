package tauri.dev.jsg.util;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlock;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

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
	public static BlockPos getHighestWithXZCords(List<BlockPos> list, int x, int z) {
		int maxy = -1;
		BlockPos top = null;

		for (BlockPos pos : list) {
			if (pos.getY() > maxy && pos.getX() == x & pos.getZ() == z) {
				maxy = pos.getY();
				top = pos;
			}
		}

		return top;
	}

	public static JSGBlock createSimpleBlock(String id, Material material, CreativeTabs tab) {
		JSGBlock block = new JSGBlock(material);
		block.setRegistryName(JSG.MOD_ID, id);
		block.setUnlocalizedName(JSG.MOD_ID + "." + id);
		block.setCreativeTab(tab);
		return block;
	}

	public static JSGBlock createSimpleBlock(String id, Material material, CreativeTabs tab, Item itemDrop, int quantity, int quantityExtra, int efficiencyCoefficient) {
		JSGBlock block = new JSGBlock(material){
			@Override
			public void getDrops(NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
				Random random = world instanceof World ? ((World)world).rand : RANDOM;
				drops.add(new ItemStack(itemDrop, (quantity + random.nextInt(quantityExtra) + (fortune * random.nextInt(efficiencyCoefficient)))));
			}
		};
		block.setRegistryName(JSG.MOD_ID, id);
		block.setUnlocalizedName(JSG.MOD_ID + "." + id);
		block.setCreativeTab(tab);
		return block;
	}
}
