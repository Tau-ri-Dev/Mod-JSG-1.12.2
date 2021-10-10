package mrjake.aunis.block;

import mrjake.aunis.Aunis;
import mrjake.aunis.AunisProps;
import mrjake.aunis.util.AunisAxisAlignedBB;
import mrjake.aunis.util.AxisAlignedBBUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

/**
 * @author matousss
 */

public class IrisBlock extends InvisibleBlock {
    private static final String blockName = "iris_block";
    public static final AxisAlignedBB boundingBox = new AunisAxisAlignedBB(0, 0, .45, 1, 1, .55);

    public IrisBlock() {
        super(Material.AIR);

        setRegistryName(Aunis.ModID + ":" + blockName);
        setUnlocalizedName(Aunis.ModID + "." + InvisibleBlock.blockName);

        setDefaultState(blockState.getBaseState()
                .withProperty(AunisProps.FACING_HORIZONTAL, EnumFacing.NORTH));


    }


    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AunisProps.FACING_HORIZONTAL);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {

        return getBoundingBox(state, worldIn, pos);

    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AunisProps.FACING_HORIZONTAL).getHorizontalIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(AunisProps.FACING_HORIZONTAL, EnumFacing.getHorizontal(meta));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

        return AxisAlignedBBUtils.rotateHorziontally(boundingBox, (int) state.getValue(AunisProps.FACING_HORIZONTAL).getHorizontalAngle());

    }

    // pak odstranit
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.SOLID;
    }
}
