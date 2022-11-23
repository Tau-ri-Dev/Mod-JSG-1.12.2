package tauri.dev.jsg.block.beamer;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.beamer.BeamerLinkingHelper;
import tauri.dev.jsg.block.JSGBlock;
import tauri.dev.jsg.creativetabs.JSGCreativeTabsHandler;
import tauri.dev.jsg.gui.GuiIdEnum;
import tauri.dev.jsg.renderer.BeamerRenderer;
import tauri.dev.jsg.sound.JSGSoundHelper;
import tauri.dev.jsg.sound.SoundPositionedEnum;
import tauri.dev.jsg.tileentity.BeamerTile;
import tauri.dev.jsg.util.ItemHandlerHelper;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;

public class BeamerBlock extends JSGBlock {

    public static final String BLOCK_NAME = "beamer_block";

    public BeamerBlock() {
        super(Material.IRON);

        setRegistryName(JSG.MOD_ID + ":" + BLOCK_NAME);
        setUnlocalizedName(JSG.MOD_ID + "." + BLOCK_NAME);

        setSoundType(SoundType.METAL);
        setCreativeTab(JSGCreativeTabsHandler.JSG_GATES_CREATIVE_TAB);

        setDefaultState(blockState.getBaseState()
                .withProperty(JSGProps.FACING_HORIZONTAL, EnumFacing.NORTH));

        setHardness(3.0f);
        setHarvestLevel("pickaxe", 3);
    }

    // ------------------------------------------------------------------------
    // Block states

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, JSGProps.FACING_HORIZONTAL, JSGProps.BEAMER_MODE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(JSGProps.FACING_HORIZONTAL).getHorizontalIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
                .withProperty(JSGProps.FACING_HORIZONTAL, EnumFacing.getHorizontal(meta & 0x03));
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        BeamerTile beamerTile = (BeamerTile) world.getTileEntity(pos);
        return state.withProperty(JSGProps.BEAMER_MODE, beamerTile.getMode());
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        BeamerTile beamerTile = (BeamerTile) world.getTileEntity(pos);
        return beamerTile.getComparatorOutput();
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    // ------------------------------------------------------------------------
    // Block actions

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        if (!world.isRemote && !player.isSneaking()) {
            if (!FluidUtil.interactWithFluidHandler(player, hand, world, pos, null)) {
                BeamerTile tile = (BeamerTile) world.getTileEntity(pos);
                if (!tile.tryInsertUpgrade(player, hand)) {
                    player.openGui(JSG.instance, GuiIdEnum.GUI_BEAMER.id, world, pos.getX(), pos.getY(), pos.getZ());
                }
            }
        }

        return !player.isSneaking();
    }

    @Override
    public void onBlockPlacedBy(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, EntityLivingBase placer, @Nonnull ItemStack stack) {
        EnumFacing facing = placer.getHorizontalFacing().getOpposite();
        state = state.withProperty(JSGProps.FACING_HORIZONTAL, facing);
        world.setBlockState(pos, state);

        if (!world.isRemote) {
            BeamerLinkingHelper.findGateInFrontAndLink(world, pos, facing);
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        BeamerTile beamerTile = (BeamerTile) world.getTileEntity(pos);

        if (!world.isRemote) {
            ItemHandlerHelper.dropInventoryItems(world, pos, beamerTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));

            if (beamerTile.isLinked())
                beamerTile.getLinkedGateTile().removeLinkedBeamer(pos);

            beamerTile.clearTargetBeamerPos();

            JSGSoundHelper.playPositionedSound(world, pos, SoundPositionedEnum.BEAMER_LOOP, false);
        }

        super.breakBlock(world, pos, state);
    }


    // ------------------------------------------------------------------------
    // Tile Entity

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new BeamerTile();
    }

    @Override
    public Class<? extends TileEntity> getTileEntityClass() {
        return BeamerTile.class;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TileEntitySpecialRenderer<? extends TileEntity> getTESR() {
        return new BeamerRenderer();
    }


    // ------------------------------------------------------------------------
    // Rendering

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
