package mrjake.aunis.block.capacitor;

import mrjake.aunis.Aunis;
import mrjake.aunis.util.main.AunisProps;
import mrjake.aunis.block.AunisAbstractCustomItemBlock;
import mrjake.aunis.gui.GuiIdEnum;
import mrjake.aunis.item.stargate.CapacitorItemBlock;
import mrjake.aunis.stargate.power.StargateAbstractEnergyStorage;
import mrjake.aunis.stargate.power.StargateItemEnergyStorage;
import mrjake.aunis.tileentity.energy.CapacitorTile;
import mrjake.aunis.util.main.loader.AunisCreativeTabsHandler;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class CapacitorBlock extends AunisAbstractCustomItemBlock {
	
	public static final String BLOCK_NAME = "capacitor_block";
	
	public CapacitorBlock() {		
		super(Material.IRON);
		
		setRegistryName(Aunis.MOD_ID + ":" + BLOCK_NAME);
		setUnlocalizedName(Aunis.MOD_ID + "." + BLOCK_NAME);
		
		setSoundType(SoundType.METAL); 
		setCreativeTab(AunisCreativeTabsHandler.aunisEnergyCreativeTab);
		
		setDefaultState(blockState.getBaseState()
				.withProperty(AunisProps.FACING_HORIZONTAL, EnumFacing.NORTH));
		
		setHardness(3.0f);
		setHarvestLevel("pickaxe", 3);
	}
	
	
	// ------------------------------------------------------------------------
	// Block states
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, AunisProps.FACING_HORIZONTAL, AunisProps.LEVEL);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {		
		return state.getValue(AunisProps.FACING_HORIZONTAL).getHorizontalIndex();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {		
		return getDefaultState()
				.withProperty(AunisProps.FACING_HORIZONTAL, EnumFacing.getHorizontal(meta & 0x03));
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		CapacitorTile capTile = (CapacitorTile) world.getTileEntity(pos);
		
		return state.withProperty(AunisProps.LEVEL, capTile.getPowerLevel());
	}
	
	// ------------------------------------------------------------------------
	// Block actions

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		
		if (!player.isSneaking()) {
			player.openGui(Aunis.instance, GuiIdEnum.GUI_CAPACITOR.id, world, pos.getX(), pos.getY(), pos.getZ());
		}
				
		return !player.isSneaking();
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		EnumFacing facing = placer.getHorizontalFacing().getOpposite();
		state = state.withProperty(AunisProps.FACING_HORIZONTAL, facing);
		world.setBlockState(pos, state); 
		
		IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);
		
		StargateAbstractEnergyStorage capacitorEnergyStorage = (StargateAbstractEnergyStorage) world.getTileEntity(pos).getCapability(CapabilityEnergy.ENERGY, null);
		capacitorEnergyStorage.setEnergyStored(energyStorage.getEnergyStored());
	}
	
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		StargateAbstractEnergyStorage capacitorEnergyStorage = (StargateAbstractEnergyStorage) world.getTileEntity(pos).getCapability(CapabilityEnergy.ENERGY, null);

		ItemStack stack = new ItemStack(this);
		((StargateItemEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null)).setEnergyStored(capacitorEnergyStorage.getEnergyStored());

		return Arrays.asList(stack);
	}
	
	@Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest) return true; //If it will harvest, delay deletion of the block until after getDrops
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }
	
    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack tool) {
        super.harvestBlock(world, player, pos, state, te, tool);
        world.setBlockToAir(pos);
    }
	
	// ------------------------------------------------------------------------
	// Tile Entity
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public Class<? extends TileEntity> getTileEntityClass() {
		return CapacitorTile.class;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new CapacitorTile();
	}
	
	
	// ------------------------------------------------------------------------
	// Rendering
	
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public ItemBlock getItemBlock() {
		return new CapacitorItemBlock(this);
	}
}
