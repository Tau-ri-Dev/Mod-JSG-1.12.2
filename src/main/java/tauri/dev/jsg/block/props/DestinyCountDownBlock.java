package tauri.dev.jsg.block.props;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlock;
import tauri.dev.jsg.creativetabs.JSGCreativeTabsHandler;
import tauri.dev.jsg.gui.GuiIdEnum;
import tauri.dev.jsg.renderer.props.DestinyCountDownRenderer;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDAbstractTile;
import tauri.dev.jsg.tileentity.props.DestinyCountDownTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateUniverseBaseTile;
import tauri.dev.jsg.util.ItemHandlerHelper;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DestinyCountDownBlock extends JSGBlock {
    private static final String BLOCK_NAME = "destiny_countdown_block";

    public DestinyCountDownBlock() {
        super(Material.IRON);

        setRegistryName(JSG.MOD_ID + ":" + BLOCK_NAME);
        setUnlocalizedName(JSG.MOD_ID + "." + BLOCK_NAME);

        setSoundType(SoundType.METAL);
        setCreativeTab(JSGCreativeTabsHandler.JSG_PROPS_CREATIVE_TAB);

        setDefaultState(blockState.getBaseState().withProperty(JSGProps.FACING_HORIZONTAL, EnumFacing.NORTH));

        setLightLevel(5/15f);
        setHardness(2.5f);
        setResistance(15.0f);
        setHarvestLevel("pickaxe", 2);
    }

    @Override
    public boolean onBlockActivated(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (!player.isSneaking()) {
                player.openGui(JSG.instance, GuiIdEnum.GUI_COUNTDOWN.id, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }

        return !player.isSneaking();
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

        DestinyCountDownTile tile = (DestinyCountDownTile) world.getTileEntity(pos);
        if(tile != null)
            tile.updateLinkStatus();
    }

    @Override
    public void breakBlock(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        DestinyCountDownTile tile = (DestinyCountDownTile) world.getTileEntity(pos);

        if (!world.isRemote && tile != null) {
            StargateUniverseBaseTile gateTile = tile.getLinkedGate(world);
            if (gateTile != null) gateTile.setLinkedCountdown(null, -1);
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean canPlaceBlockOnSide(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
        return super.canPlaceBlockOnSide(worldIn, pos, side) && JSGProps.FACING_HORIZONTAL.getAllowedValues().contains(side);
    }

    @Nonnull
    @Override
    public IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(JSGProps.FACING_HORIZONTAL, facing);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, @Nonnull Block block, @Nonnull BlockPos fromPos) {
        EnumFacing backFacing = state.getValue(JSGProps.FACING_HORIZONTAL).getOpposite();

        if (world.isAirBlock(pos.offset(backFacing))) {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    // --------------------------------------------------------------------------------------

    @Override
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new DestinyCountDownTile();
    }

    public Class<? extends TileEntity> getTileEntityClass() {
        return DestinyCountDownTile.class;
    }

    @SideOnly(Side.CLIENT)
    public TileEntitySpecialRenderer<? extends TileEntity> getTESR() {
        return new DestinyCountDownRenderer();
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
    public AxisAlignedBB getBoundingBox(IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
        switch (state.getValue(JSGProps.FACING_HORIZONTAL)) {
            case NORTH:
                return new AxisAlignedBB(-0.1, 0.2, 0.9, 1.1, 0.8, 1);

            case SOUTH:
                return new AxisAlignedBB(-0.1, 0.2, 0, 1.1, 0.8, 0.1);

            case WEST:
                return new AxisAlignedBB(0.9, 0.2, -0.1, 1, 0.8, 1.1);

            default:
                return new AxisAlignedBB(0, 0.2, -0.1, 0.1, 0.8, 1.1);
        }
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
        switch (blockState.getValue(JSGProps.FACING_HORIZONTAL)) {
            case NORTH:
                return new AxisAlignedBB(-0.1, 0.2, 0.9, 1.1, 0.8, 1);

            case SOUTH:
                return new AxisAlignedBB(-0.1, 0.2, 0, 1.1, 0.8, 0.1);

            case WEST:
                return new AxisAlignedBB(0.9, 0.2, -0.1, 1, 0.8, 1.1);

            default:
                return new AxisAlignedBB(0, 0.2, -0.1, 0.1, 0.8, 1.1);
        }
    }
}
