package mrjake.aunis.block.zpm;

import mrjake.aunis.Aunis;
import mrjake.aunis.AunisProps;
import mrjake.aunis.tileentity.energy.ZPMTile;
import mrjake.aunis.util.AunisAxisAlignedBB;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ZPMBlock extends Block {

    private static final String blockName = "zpm";

    public ZPMBlock() {
        super(Material.IRON);

        setRegistryName(Aunis.ModID + ":" + blockName);
        setUnlocalizedName(Aunis.ModID + "." + blockName);

        setSoundType(SoundType.GLASS);
        setCreativeTab(Aunis.aunisEnergyCreativeTab);

        setLightOpacity(0);

        setHardness(3.0f);
        setHarvestLevel("pickaxe", 3);
    }
    // ------------------------------------------------------------------------
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AunisProps.ROTATION_HORIZONTAL, AunisProps.SNOWY);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AunisProps.ROTATION_HORIZONTAL);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(AunisProps.ROTATION_HORIZONTAL, meta);
    }

    public final static BlockMatcher SNOW_MATCHER = BlockMatcher.forBlock(Blocks.SNOW_LAYER);

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.withProperty(AunisProps.SNOWY, isSnowAroundBlock(world, pos));
    }

    public static boolean isSnowAroundBlock(IBlockAccess world, BlockPos inPos) {

        // Check if 4 adjacent blocks are snow layers
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos pos = inPos.offset(facing);
            if (!SNOW_MATCHER.apply(world.getBlockState(pos))) {
                return false;
            }
        }

        return true;
    }

    // ------------------------------------------------------------------------
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        // Server side
        if (!world.isRemote) {
            int facing = MathHelper.floor((double) ((placer.rotationYaw) * 16.0F / 360.0F) + 0.5D) & 0x0F;
            world.setBlockState(pos, state.withProperty(AunisProps.ROTATION_HORIZONTAL, facing), 3);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        // todo zpm gui open
        return false;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        // todo drop inserted zpms
        super.breakBlock(world, pos, state);
    }

    private int getPower(IBlockAccess world, BlockPos pos) {
        // todo return power by counting zpms
        return 0;
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return getPower(world, pos);
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return getPower(world, pos);
    }

    // ------------------------------------------------------------------------
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new ZPMTile();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return new AunisAxisAlignedBB(0.25, 0, 0.25, 0.75, 1, 0.75);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return new AunisAxisAlignedBB(0.25, 0, 0.25, 0.75, 1, 0.75);
    }
}
