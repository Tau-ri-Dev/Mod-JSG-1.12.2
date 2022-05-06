package mrjake.aunis.block.props;

import mrjake.aunis.Aunis;
import mrjake.aunis.block.AunisAbstractCustomMetaItemBlock;
import mrjake.aunis.item.props.TRPlatformItem;
import mrjake.aunis.util.main.AunisProps;
import mrjake.aunis.util.main.loader.AunisCreativeTabsHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public abstract class AunisAbstractProp extends AunisAbstractCustomMetaItemBlock {
    public AunisAbstractProp(Material materialIn) {
        super(materialIn);
        setCreativeTab(AunisCreativeTabsHandler.aunisPropsCreativeTab);
        setLightOpacity(0);
        setDefaultState(blockState.getBaseState().withProperty(AunisProps.PROP_VARIANT, 0));
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(IBlockState state, @Nonnull RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
        return new ItemStack(this, 1, getMetaFromState(getDefaultState().withProperty(AunisProps.PROP_VARIANT, state.getValue(AunisProps.PROP_VARIANT))));
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, IBlockState state, int fortune) {
        drops.add(new ItemStack(this, 1, getMetaFromState(getDefaultState().withProperty(AunisProps.PROP_VARIANT, state.getValue(AunisProps.PROP_VARIANT)))));
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AunisProps.PROP_VARIANT);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AunisProps.PROP_VARIANT);
    }

    @Override
    public void onBlockPlacedBy(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase placer, @Nonnull ItemStack stack) {
        if (!world.isRemote) {
            state = state.withProperty(AunisProps.PROP_VARIANT, stack.getMetadata());
            world.setBlockState(pos, state);
        }
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(AunisProps.PROP_VARIANT, meta);
    }

    @Override
    public Map<Integer, String> getAllMetaTypes() {
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < getAllMetasCount(); i++)
            map.put(i, Aunis.MOD_ID + ":" + getBlockName());
        return map;
    }

    public abstract int getAllMetasCount();
    public abstract String getBlockName();
}
