package mrjake.aunis.block;

import mrjake.aunis.Aunis;
import mrjake.aunis.item.AunisItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class TriniumOreBlock extends Block {

	public TriniumOreBlock(String blockName) {
		super(Material.ROCK);
		
		setRegistryName(Aunis.ModID + ":" + blockName);
		setUnlocalizedName(Aunis.ModID + "." + blockName);
		
		setSoundType(SoundType.STONE); 
		setCreativeTab(Aunis.aunisCreativeTab);
		
		setHardness(4.5f);
		setHarvestLevel("pickaxe", 3);
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		Random random = world instanceof World ? ((World)world).rand : RANDOM;
		
		int quantity = 1 + (fortune * random.nextInt(2));
		
		drops.add(new ItemStack(AunisItems.TRINIUM_INGOT, quantity));
	}
	
	@Override
	public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        Random rand = world instanceof World ? ((World)world).rand : new Random();
        return MathHelper.getInt(rand, 5, 10);
	}
	
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
}
