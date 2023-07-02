package tauri.dev.jsg.block.props;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlock;
import tauri.dev.jsg.creativetabs.JSGCreativeTabsHandler;
import tauri.dev.jsg.gui.AncientSignEditGui;
import tauri.dev.jsg.renderer.props.AncientSignRenderer;
import tauri.dev.jsg.tileentity.props.AncientSignTile;
import tauri.dev.jsg.util.JSGColorUtil;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class AncientSignBlock extends JSGBlock {
    private static final String BLOCK_NAME = "ancient_sign";

    protected static final AxisAlignedBB SIGN_EAST_AABB = new AxisAlignedBB(0.0D, 0, 0.0D, 0.025D, 1, 1.0D);
    protected static final AxisAlignedBB SIGN_WEST_AABB = new AxisAlignedBB(0.975D, 0, 0.0D, 1.0D, 1, 1.0D);
    protected static final AxisAlignedBB SIGN_SOUTH_AABB = new AxisAlignedBB(0.0D, 0, 0.0D, 1.0D, 1, 0.025D);
    protected static final AxisAlignedBB SIGN_NORTH_AABB = new AxisAlignedBB(0.0D, 0, 0.975D, 1.0D, 1, 1.0D);

    public AncientSignBlock() {
        super(Material.IRON);

        setRegistryName(JSG.MOD_ID + ":" + BLOCK_NAME);
        setUnlocalizedName(JSG.MOD_ID + "." + BLOCK_NAME);

        setSoundType(SoundType.METAL);
        setCreativeTab(JSGCreativeTabsHandler.JSG_PROPS_CREATIVE_TAB);

        setDefaultState(blockState.getBaseState().withProperty(JSGProps.FACING_HORIZONTAL, EnumFacing.NORTH));

        setLightLevel(5 / 15f);
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
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new AncientSignTile();
    }

    public Class<? extends TileEntity> getTileEntityClass() {
        return AncientSignTile.class;
    }

    @SideOnly(Side.CLIENT)
    public TileEntitySpecialRenderer<? extends TileEntity> getTESR() {
        return new AncientSignRenderer();
    }

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

    @Override
    public boolean canPlaceBlockOnSide(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
        return super.canPlaceBlockOnSide(worldIn, pos, side) && JSGProps.FACING_HORIZONTAL.getAllowedValues().contains(side);
    }

    @Nonnull
    @Override
    public IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, @Nonnull EnumHand hand) {
        return getDefaultState().withProperty(JSGProps.FACING_HORIZONTAL, facing);
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
        switch (state.getValue(JSGProps.FACING_HORIZONTAL)) {
            case NORTH:
            default:
                return SIGN_NORTH_AABB;
            case SOUTH:
                return SIGN_SOUTH_AABB;
            case WEST:
                return SIGN_WEST_AABB;
            case EAST:
                return SIGN_EAST_AABB;
        }
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
        switch (blockState.getValue(JSGProps.FACING_HORIZONTAL)) {
            case NORTH:
            default:
                return SIGN_NORTH_AABB;
            case SOUTH:
                return SIGN_SOUTH_AABB;
            case WEST:
                return SIGN_WEST_AABB;
            case EAST:
                return SIGN_EAST_AABB;
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
        EnumFacing enumfacing = state.getValue(JSGProps.FACING_HORIZONTAL);

        if (!worldIn.getBlockState(pos.offset(enumfacing.getOpposite())).getMaterial().isSolid()) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }

        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    public static boolean interact(@Nonnull World worldIn, @Nonnull BlockPos blockPos, @Nonnull EntityLivingBase entity, @Nullable EnumHand hand) {
        if (!(entity instanceof EntityPlayer)) return false;
        EntityPlayer player = (EntityPlayer) entity;

        TileEntity tile = worldIn.getTileEntity(blockPos);
        if (tile instanceof AncientSignTile) {
            AncientSignTile casted = ((AncientSignTile) tile);
            if (hand != null) {
                ItemStack stack = player.getHeldItem(hand);
                if (stack.getItem() instanceof ItemDye) {
                    if (!worldIn.isRemote) {
                        int newColor = JSGColorUtil.blendColors(casted.color, JSGColorUtil.getColorFromDyeItem(stack), 0.25f);
                        if (newColor != casted.color) {
                            casted.color = newColor;
                            tile.markDirty();
                            if (!player.isCreative() && !player.isSpectator()) {
                                stack.shrink(1);
                                player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);
                            }
                        }
                    }
                    return true;
                }
            }

            if (worldIn.isRemote)
                displayGui(casted);
            return true;
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    private static void displayGui(AncientSignTile tile) {
        Minecraft.getMinecraft().displayGuiScreen(new AncientSignEditGui(tile));
    }

    @Override
    public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase placer, @Nonnull ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound != null && compound.hasKey("BlockEntityTag")) {
                TileEntity tile = world.getTileEntity(pos);
                if (tile instanceof AncientSignTile) {
                    AncientSignTile casted = (AncientSignTile) tile;
                    NBTTagCompound c = compound.getCompoundTag("BlockEntityTag");
                    casted.fromItemStack(c);
                    casted.markDirty();
                    return;
                }
            }
        }
        interact(world, pos, placer, null);
    }

    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        return interact(worldIn, pos, playerIn, hand);
    }

    public boolean canPlaceBlockAt(@Nonnull World worldIn, @Nonnull BlockPos pos) {
        return !this.hasInvalidNeighbor(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos);
    }

    protected boolean hasInvalidNeighbor(World worldIn, BlockPos pos) {
        return this.isInvalidNeighbor(worldIn, pos, EnumFacing.NORTH) || this.isInvalidNeighbor(worldIn, pos, EnumFacing.SOUTH) || this.isInvalidNeighbor(worldIn, pos, EnumFacing.WEST) || this.isInvalidNeighbor(worldIn, pos, EnumFacing.EAST);
    }

    protected boolean isInvalidNeighbor(World worldIn, BlockPos pos, EnumFacing facing) {
        return worldIn.getBlockState(pos.offset(facing)).getMaterial() == Material.CACTUS;
    }

    @Override
    public boolean isPassable(@Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
        return true;
    }
}
