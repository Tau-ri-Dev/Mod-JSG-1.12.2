package tauri.dev.jsg.block.props;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlock;
import tauri.dev.jsg.creativetabs.JSGCreativeTabsHandler;
import tauri.dev.jsg.renderer.props.DestinyVentRenderer;
import tauri.dev.jsg.tileentity.props.DestinyVentTile;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class DestinyVentBlock extends JSGBlock {
    public static final String BLOCK_NAME = "destiny_vent_block";

    public DestinyVentBlock() {
        super(Material.IRON);

        setRegistryName(JSG.MOD_ID + ":" + BLOCK_NAME);
        setUnlocalizedName(JSG.MOD_ID + "." + BLOCK_NAME);

        setSoundType(SoundType.METAL);
        setCreativeTab(JSGCreativeTabsHandler.JSG_PROPS_CREATIVE_TAB);

        setDefaultState(blockState.getBaseState().withProperty(JSGProps.FACING_HORIZONTAL, EnumFacing.NORTH));

        setHardness(2.5f);
        setResistance(15.0f);
        setHarvestLevel("pickaxe", 2);
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

    @Override
    public void onBlockPlacedBy(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, EntityLivingBase placer, @Nonnull ItemStack stack) {
        EnumFacing facing = placer.getHorizontalFacing().getOpposite();
        state = state.withProperty(JSGProps.FACING_HORIZONTAL, facing);
        world.setBlockState(pos, state);
    }

    // --------------------------------------------------------------------------------------

    @Override
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new DestinyVentTile();
    }

    public Class<? extends TileEntity> getTileEntityClass() {
        return DestinyVentTile.class;
    }

    @SideOnly(Side.CLIENT)
    public TileEntitySpecialRenderer<? extends TileEntity> getTESR() {
        return new DestinyVentRenderer();
    }

    public boolean renderHighlight(IBlockState blockState) {
        return false;
    }


    // --------------------------------------------------------------------------------------
    // Rendering

    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(@Nonnull IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
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


    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
        return new AxisAlignedBB(0, 0, 0, 1, 0.02D, 1);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
        return new AxisAlignedBB(0, 0, 0, 1, 0.02D, 1);
    }
}
