package mrjake.aunis.block;

import mrjake.aunis.Aunis;
import mrjake.aunis.AunisProps;
import mrjake.aunis.util.AunisAxisAlignedBB;
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
    public static final AunisAxisAlignedBB boundingBox = new AunisAxisAlignedBB(0, 0, .45, 1, 1, .55);

    public IrisBlock() {
        super(Material.AIR);

        setRegistryName(Aunis.ModID + ":" + blockName);
        setUnlocalizedName(Aunis.ModID + "." + InvisibleBlock.blockName);

        setDefaultState(blockState.getBaseState()
                .withProperty(AunisProps.FACING_HORIZONTAL, EnumFacing.NORTH)
                .withProperty(AunisProps.HAS_COLLISIONS, true));

        setLightLevel(1.0f);
        setLightOpacity(0);
    }


    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AunisProps.FACING_HORIZONTAL, AunisProps.HAS_COLLISIONS);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if (state.getValue(AunisProps.HAS_COLLISIONS))
            return boundingBox.rotate(state.getValue(AunisProps.FACING_HORIZONTAL));
        else
            return null;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(AunisProps.HAS_COLLISIONS) ? 0x04 : 0) | state.getValue(AunisProps.FACING_HORIZONTAL).getHorizontalIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(AunisProps.HAS_COLLISIONS, (meta & 0x04) != 0).withProperty(AunisProps.FACING_HORIZONTAL, EnumFacing.getHorizontal(meta & 0x03));
    }
}
