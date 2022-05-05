package mrjake.aunis.block.ore;

import mrjake.aunis.Aunis;
import mrjake.aunis.block.AunisBlock;
import mrjake.aunis.util.main.loader.AunisCreativeTabsHandler;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class TitaniumOreBlock extends AunisBlock {

	public TitaniumOreBlock(String blockName) {
		super(Material.ROCK);
		
		setRegistryName(Aunis.MOD_ID + ":" + blockName);
		setUnlocalizedName(Aunis.MOD_ID + "." + blockName);
		
		setSoundType(SoundType.STONE); 
		setCreativeTab(AunisCreativeTabsHandler.aunisOresCreativeTab);
		
		setHardness(4.5f);
		setHarvestLevel("pickaxe", 2);
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
