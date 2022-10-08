package tauri.dev.jsg.block.invisible;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.util.JSGAxisAlignedBB;
import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.jsg.util.AxisAlignedBBUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
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
    public static final AxisAlignedBB boundingBox = new JSGAxisAlignedBB(0, 0, .45, 1, 1, .55);

    public IrisBlock() {
        super(Material.AIR);

        setRegistryName(JSG.MOD_ID + ":" + blockName);
        setUnlocalizedName(JSG.MOD_ID + "." + InvisibleBlock.blockName);

        setDefaultState(blockState.getBaseState()
                .withProperty(JSGProps.FACING_HORIZONTAL, EnumFacing.NORTH));


    }


    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, JSGProps.FACING_HORIZONTAL);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {

        return getBoundingBox(state, worldIn, pos);

    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(JSGProps.FACING_HORIZONTAL).getHorizontalIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(JSGProps.FACING_HORIZONTAL, EnumFacing.getHorizontal(meta));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

        return AxisAlignedBBUtils.rotateHorziontally(boundingBox, (int) state.getValue(JSGProps.FACING_HORIZONTAL).getHorizontalAngle());

    }

}
