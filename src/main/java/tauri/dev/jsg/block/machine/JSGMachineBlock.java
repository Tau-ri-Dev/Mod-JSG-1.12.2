package tauri.dev.jsg.block.machine;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGAbstractCustomItemBlock;
import tauri.dev.jsg.block.JSGBlock;
import tauri.dev.jsg.stargate.power.StargateAbstractEnergyStorage;
import tauri.dev.jsg.stargate.power.StargateItemEnergyStorage;
import tauri.dev.jsg.tileentity.machine.AbstractAssemblerTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsAbstractTile;
import tauri.dev.jsg.util.ItemHandlerHelper;
import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.jsg.util.main.loader.JSGCreativeTabsHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public abstract class JSGMachineBlock extends JSGAbstractCustomItemBlock {
    public JSGMachineBlock(String blockName) {
        super(Material.IRON);

        setRegistryName(JSG.MOD_ID + ":" + blockName);
        setUnlocalizedName(JSG.MOD_ID + "." + blockName);

        setSoundType(SoundType.METAL);
        setCreativeTab(JSGCreativeTabsHandler.jsgMachinesCreativeTab);

        setDefaultState(blockState.getBaseState().withProperty(JSGProps.FACING_HORIZONTAL, EnumFacing.NORTH));

        setLightOpacity(0);
        setHardness(2.5f);
        setResistance(35.0f);
        setHarvestLevel("pickaxe", 3);
    }


    // --------------------------------------------------------------------------------------
    // Block states

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, JSGProps.FACING_HORIZONTAL);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(JSGProps.FACING_HORIZONTAL).getHorizontalIndex();
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(JSGProps.FACING_HORIZONTAL, EnumFacing.getHorizontal(meta));
    }

    // ------------------------------------------------------------------------
    // Block behavior

    @Override
    public boolean onBlockActivated(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (!player.isSneaking()) {
                showGui(player, hand, world, pos);
            }
        }

        return !player.isSneaking();
    }

    protected abstract void showGui(EntityPlayer player, EnumHand hand, World world, BlockPos pos);

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        StargateAbstractEnergyStorage capacitorEnergyStorage = (StargateAbstractEnergyStorage) world.getTileEntity(pos).getCapability(CapabilityEnergy.ENERGY, null);

        ItemStack stack = new ItemStack(this);
        ((StargateItemEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null)).setEnergyStored(capacitorEnergyStorage.getEnergyStored());

        return Arrays.asList(stack);
    }

    @Override
    public void breakBlock(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if (!world.isRemote) {
            AbstractAssemblerTile tile = (AbstractAssemblerTile) world.getTileEntity(pos);
            if (tile != null) {
                ItemHandlerHelper.dropInventoryItems(world, pos, tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));
            }
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest) return true;
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack tool) {
        super.harvestBlock(world, player, pos, state, te, tool);
        world.setBlockToAir(pos);
    }

    @Override
    public void onBlockPlacedBy(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, EntityLivingBase placer, @Nonnull ItemStack stack) {
        EnumFacing facing = placer.getHorizontalFacing().getOpposite();
        state = state.withProperty(JSGProps.FACING_HORIZONTAL, facing);
        world.setBlockState(pos, state);

        IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);

        StargateAbstractEnergyStorage tileEnergyStorage = (StargateAbstractEnergyStorage) world.getTileEntity(pos).getCapability(CapabilityEnergy.ENERGY, null);
        tileEnergyStorage.setEnergyStored(energyStorage.getEnergyStored());
    }


    // --------------------------------------------------------------------------------------
    // TileEntity

    @Override
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return true;
    }

    @Override
    public abstract TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state);


    // --------------------------------------------------------------------------------------
    // Rendering

    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(@Nonnull IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaqueCube(@Nonnull IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(@Nonnull IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(@Nonnull IBlockState state) {
        return false;
    }

    @Nonnull
    @Override
    public BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess worldIn, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
}
