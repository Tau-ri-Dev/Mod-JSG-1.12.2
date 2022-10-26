package tauri.dev.jsg.block.dialhomedevice;

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
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.CapabilityItemHandler;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlock;
import tauri.dev.jsg.gui.GuiIdEnum;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDAbstractTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.util.ItemHandlerHelper;
import tauri.dev.jsg.util.JSGAxisAlignedBB;
import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.jsg.util.main.loader.JSGCreativeTabsHandler;

import javax.annotation.Nullable;

public abstract class DHDAbstractBlock extends JSGBlock {

    public DHDAbstractBlock(String blockName) {
        super(Material.IRON);

        setRegistryName(JSG.MOD_ID + ":" + blockName);
        setUnlocalizedName(JSG.MOD_ID + "." + blockName);

        setSoundType(SoundType.METAL);
        setCreativeTab(JSGCreativeTabsHandler.jsgGatesCreativeTab);

        setDefaultState(blockState.getBaseState().withProperty(JSGProps.ROTATION_HORIZONTAL, 0).withProperty(JSGProps.SNOWY, false));

        setLightOpacity(0);

        setHardness(3.0f);
        setResistance(20.0f);
        setHarvestLevel("pickaxe", 3);
    }

    // ------------------------------------------------------------------------

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        // Server side
        if (!world.isRemote) {
            int facing = MathHelper.floor((double) ((placer.rotationYaw) * 16.0F / 360.0F) + 0.5D) & 0x0F;
            world.setBlockState(pos, state.withProperty(JSGProps.ROTATION_HORIZONTAL, facing), 3);

            DHDAbstractTile dhdTile = (DHDAbstractTile) world.getTileEntity(pos);
            dhdTile.updateLinkStatus(world, pos);
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, JSGProps.ROTATION_HORIZONTAL, JSGProps.SNOWY);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(JSGProps.ROTATION_HORIZONTAL);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(JSGProps.ROTATION_HORIZONTAL, meta);
    }

    public final static BlockMatcher SNOW_MATCHER = BlockMatcher.forBlock(Blocks.SNOW_LAYER);

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.withProperty(JSGProps.SNOWY, isSnowAroundBlock(world, pos));
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

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        EnumFacing dhdFacingOpposite = EnumFacing.getHorizontal(Math.round(state.getValue(JSGProps.ROTATION_HORIZONTAL) / 4.0f));
        boolean backActivation = (facing == dhdFacingOpposite);

        if (!world.isRemote) {
            // Server

            if (!player.isSneaking() && backActivation) {
                // Not sneaking and activating from the back
                // Try: fluid interaction, upgrade insertion, gui opening

                if (!FluidUtil.interactWithFluidHandler(player, hand, world, pos, null)) {
                    DHDAbstractTile tile = (DHDAbstractTile) world.getTileEntity(pos);
                    if (!tile.tryInsertUpgrade(player, hand)) {
                        player.openGui(JSG.instance, getGui().id, world, pos.getX(), pos.getY(), pos.getZ());
                    }
                }
            }
        }

        // Only activate when not sneaking and activating from the back
        return !player.isSneaking() && backActivation;
    }

    public abstract GuiIdEnum getGui();

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        DHDAbstractTile tile = (DHDAbstractTile) world.getTileEntity(pos);

        if (!world.isRemote) {
            StargateClassicBaseTile gateTile = (StargateClassicBaseTile) tile.getLinkedGate(world);

            if (gateTile != null) gateTile.setLinkedDHD(null, -1);

            ItemHandlerHelper.dropInventoryItems(world, pos, tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));
        }

        super.breakBlock(world, pos, state);
    }

    private int getPower(IBlockAccess world, BlockPos pos) {
        DHDAbstractTile tile = (DHDAbstractTile) world.getTileEntity(pos);
        if (!tile.isLinked()) return 0;

        StargateClassicBaseTile gateTile = (StargateClassicBaseTile) tile.getLinkedGate(world);

        if (gateTile == null) {
            return 0;
        }

        if (gateTile.getStargateState().engaged() || gateTile.getStargateState().unstable()) return 15;
        return gateTile.getDialedAddress().size() > 0 ? gateTile.getDialedAddress().size() + 3 : 0;
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side != EnumFacing.DOWN && side != EnumFacing.UP;
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
    public boolean renderHighlight(IBlockState state) {
        return false;
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
        int rotation = (int) (state.getValue(JSGProps.ROTATION_HORIZONTAL) * 22.5f);

        if (rotation % 90 == 0)
            return new JSGAxisAlignedBB(-0.5, 0, -0.25, 0.5, 1, 0.25).rotate(rotation).offset(0.5, 0, 0.5);
        else return new JSGAxisAlignedBB(0.25, 0, 0.25, 0.75, 1, 0.75);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        int rotation = (int) (state.getValue(JSGProps.ROTATION_HORIZONTAL) * 22.5f);

        if (rotation % 90 == 0)
            return new JSGAxisAlignedBB(-0.5, 0, -0.25, 0.5, 1, 0.25).rotate(rotation).offset(0.5, 0, 0.5);
        else return new JSGAxisAlignedBB(0.25, 0, 0.25, 0.75, 1, 0.75);
    }
}
