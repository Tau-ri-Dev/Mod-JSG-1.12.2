package tauri.dev.jsg.block.energy.capacitor;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.jsg.block.JSGBlock;
import tauri.dev.jsg.util.main.loader.JSGCreativeTabsHandler;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CapacitorBlockEmpty extends JSGBlock {
	
	public static final String BLOCK_NAME = "capacitor_block_empty";
	
	public CapacitorBlockEmpty() {		
		super(Material.IRON);
		
		setRegistryName(JSG.MOD_ID + ":" + BLOCK_NAME);
		setUnlocalizedName(JSG.MOD_ID + "." + BLOCK_NAME);
		
		setSoundType(SoundType.METAL); 
		setCreativeTab(JSGCreativeTabsHandler.jsgEnergyCreativeTab);
		
		setDefaultState(blockState.getBaseState()
				.withProperty(JSGProps.FACING_HORIZONTAL, EnumFacing.NORTH));
		
		setHardness(3.0f);
		setHarvestLevel("pickaxe", 3);
	}
	
	
	// ------------------------------------------------------------------------
	// Block states
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, JSGProps.FACING_HORIZONTAL);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {		
		return state.getValue(JSGProps.FACING_HORIZONTAL).getHorizontalIndex();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {		
		return getDefaultState()
				.withProperty(JSGProps.FACING_HORIZONTAL, EnumFacing.getHorizontal(meta & 0x03));
	}
	
	
	// ------------------------------------------------------------------------
	// Block actions
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		EnumFacing facing = placer.getHorizontalFacing().getOpposite();
		state = state.withProperty(JSGProps.FACING_HORIZONTAL, facing);
		world.setBlockState(pos, state); 
	}
	
	
	// ------------------------------------------------------------------------
	// Rendering
	
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
}
