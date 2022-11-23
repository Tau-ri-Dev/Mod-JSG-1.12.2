package tauri.dev.jsg.block.props;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGAbstractCustomMetaItemBlock;
import tauri.dev.jsg.creativetabs.JSGCreativeTabsHandler;
import tauri.dev.jsg.util.main.JSGProps;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
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

public abstract class JSGAbstractProp extends JSGAbstractCustomMetaItemBlock {
    public JSGAbstractProp(Material materialIn) {
        super(materialIn);
        setCreativeTab(JSGCreativeTabsHandler.JSG_PROPS_CREATIVE_TAB);
        setLightOpacity(0);
        setDefaultState(blockState.getBaseState().withProperty(JSGProps.PROP_VARIANT, 0));
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(IBlockState state, @Nonnull RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
        return new ItemStack(this, 1, getMetaFromState(getDefaultState().withProperty(JSGProps.PROP_VARIANT, state.getValue(JSGProps.PROP_VARIANT))));
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, IBlockState state, int fortune) {
        drops.add(new ItemStack(this, 1, getMetaFromState(getDefaultState().withProperty(JSGProps.PROP_VARIANT, state.getValue(JSGProps.PROP_VARIANT)))));
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, JSGProps.PROP_VARIANT);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(JSGProps.PROP_VARIANT);
    }

    @Override
    public void onBlockPlacedBy(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase placer, @Nonnull ItemStack stack) {
        if (!world.isRemote) {
            state = state.withProperty(JSGProps.PROP_VARIANT, stack.getMetadata());
            world.setBlockState(pos, state);
        }
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(JSGProps.PROP_VARIANT, meta);
    }

    @Override
    public Map<Integer, String> getAllMetaTypes() {
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < getAllMetasCount(); i++)
            map.put(i, JSG.MOD_ID + ":" + getBlockName());
        return map;
    }

    public abstract int getAllMetasCount();
    public abstract String getBlockName();
}
