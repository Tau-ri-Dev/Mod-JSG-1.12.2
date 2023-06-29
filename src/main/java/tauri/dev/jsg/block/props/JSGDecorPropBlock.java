package tauri.dev.jsg.block.props;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGAbstractCustomMetaItemBlock;
import tauri.dev.jsg.creativetabs.JSGCreativeTabsHandler;
import tauri.dev.jsg.item.props.DecorPropItem;
import tauri.dev.jsg.renderer.props.DecorPropRenderer;
import tauri.dev.jsg.tileentity.props.DecorPropTile;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public class JSGDecorPropBlock extends JSGAbstractCustomMetaItemBlock {

    public static final String BASE = "decor_";
    public static final String END = "_block";
    public static final String BLOCK_NAME = BASE + "prop" + END;

    public JSGDecorPropBlock() {
        super(Material.IRON);
        setCreativeTab(JSGCreativeTabsHandler.JSG_PROPS_CREATIVE_TAB);
        setLightOpacity(0);
        setDefaultState(blockState.getBaseState().withProperty(JSGProps.PROP_VARIANT, 0));

        setRegistryName(JSG.MOD_ID + ":" + BLOCK_NAME);
        setUnlocalizedName(JSG.MOD_ID + ":" + BLOCK_NAME);

        setSoundType(SoundType.STONE);

        setHardness(3.0f);
        setHarvestLevel("pickaxe", 3);
    }

    @Override
    public void getSubBlocks(@Nonnull CreativeTabs creativeTabs, @Nonnull NonNullList<ItemStack> items) {
        for (DecorPropItem.PropVariants variant : DecorPropItem.PropVariants.values()) {
            items.add(new ItemStack(this, 1, getMetaFromState(getDefaultState().withProperty(JSGProps.PROP_VARIANT, variant.id))));
        }
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
        DecorPropItem.PropVariants variant = DecorPropItem.PropVariants.byId(stack.getMetadata());
        if (!world.isRemote) {
            state = state.withProperty(JSGProps.PROP_VARIANT, variant.id);
            world.setBlockState(pos, state);
        }
        variant.abstractBlock.onBlockPlacedBy(world, pos, state, placer, stack);
    }

    @Override
    public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        DecorPropItem.PropVariants variant = DecorPropItem.PropVariants.byId(state.getValue(JSGProps.PROP_VARIANT));
        variant.abstractBlock.breakBlock(world, pos, state);
    }

    @Override
    public int getLightValue(@Nonnull IBlockState state) {
        DecorPropItem.PropVariants variant = DecorPropItem.PropVariants.byId(state.getValue(JSGProps.PROP_VARIANT));
        return variant.abstractBlock.getLightValue(state);
    }

    @Override
    public void neighborChanged(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockPos fromPos) {
        DecorPropItem.PropVariants variant = DecorPropItem.PropVariants.byId(state.getValue(JSGProps.PROP_VARIANT));
        variant.abstractBlock.neighborChanged(state, world, pos, block, fromPos);
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
        DecorPropItem.PropVariants variant = DecorPropItem.PropVariants.byId(state.getValue(JSGProps.PROP_VARIANT));
        return variant.abstractBlock.getBoundingBox(state, worldIn, pos);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
        DecorPropItem.PropVariants variant = DecorPropItem.PropVariants.byId(state.getValue(JSGProps.PROP_VARIANT));
        return variant.abstractBlock.getCollisionBoundingBox(state, worldIn, pos);
    }

    @Override
    public boolean renderHighlight(IBlockState state) {
        DecorPropItem.PropVariants variant = DecorPropItem.PropVariants.byId(state.getValue(JSGProps.PROP_VARIANT));
        return variant.abstractBlock.renderHighlight(state);
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(JSGProps.PROP_VARIANT, meta);
    }

    @Override
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return true;
    }


    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new DecorPropTile();
    }

    @Override
    public Class<? extends TileEntity> getTileEntityClass() {
        return DecorPropTile.class;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TileEntitySpecialRenderer<? extends TileEntity> getTESR() {
        return new DecorPropRenderer();
    }

    public Map<Integer, String> getAllMetaTypes() {
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < getAllMetasCount(); i++)
            map.put(i, JSG.MOD_ID + ":" + getBlockName());
        return map;
    }

    public int getAllMetasCount() {
        return DecorPropItem.PropVariants.values().length;
    }

    public String getBlockName() {
        return BLOCK_NAME;
    }

    @Override
    public ItemBlock getItemBlock() {
        return new DecorPropItem(this);
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
}
