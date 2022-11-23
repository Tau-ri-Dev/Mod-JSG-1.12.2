package tauri.dev.jsg.block.ore;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlock;
import tauri.dev.jsg.creativetabs.JSGCreativeTabsHandler;
import tauri.dev.jsg.item.JSGItems;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class TriniumOreBlock extends JSGBlock {

	public TriniumOreBlock(String blockName) {
		super(Material.ROCK);
		
		setRegistryName(JSG.MOD_ID + ":" + blockName);
		setUnlocalizedName(JSG.MOD_ID + "." + blockName);
		
		setSoundType(SoundType.STONE); 
		setCreativeTab(JSGCreativeTabsHandler.JSG_ORES_CREATIVE_TAB);
		
		setHardness(4.5f);
		setHarvestLevel("pickaxe", 3);
	}

	
	@Override
	public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        Random rand = world instanceof World ? ((World)world).rand : new Random();
        return MathHelper.getInt(rand, 5, 10);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		Random random = world instanceof World ? ((World)world).rand : RANDOM;

		int quantity = 1 + random.nextInt(2) + (fortune * random.nextInt(3));

		drops.add(new ItemStack(JSGItems.TITANIUM_ORE_IMPURE, quantity));
	}
	
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
}
