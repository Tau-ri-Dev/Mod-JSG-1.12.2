package tauri.dev.jsg.block.machine.lab;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlock;
import tauri.dev.jsg.creativetabs.JSGCreativeTabsHandler;
import tauri.dev.jsg.tileentity.machine.lab.LabConstructBlockTile;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
public class ChemLabConstructBlock extends JSGBlock {
    public static final String BLOCK_NAME = "construction_machine_block";

    public ChemLabConstructBlock() {
        super(Material.IRON);

        setRegistryName(JSG.MOD_ID + ":" + BLOCK_NAME);
        setUnlocalizedName(JSG.MOD_ID + "." + BLOCK_NAME);

        setSoundType(SoundType.METAL);
        setCreativeTab(JSGCreativeTabsHandler.JSG_MACHINES_CREATIVE_TAB);

        setDefaultState(blockState.getBaseState().withProperty(JSGProps.RENDER_BLOCK, true));

        setLightOpacity(0);
        setHardness(2.5f);
        setResistance(35.0f);
        setHarvestLevel("pickaxe", 2);
    }

    // --------------------------------------------------------------------------------------
    // Block states

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, JSGProps.RENDER_BLOCK);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(JSGProps.RENDER_BLOCK) ? 1 : 0);
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(JSGProps.RENDER_BLOCK, meta == 1);
    }

    // --------------------------------------------------------------------------------------
    // Tile Entity

    @Override
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new LabConstructBlockTile();
    }

    /**
     * Get class of tile entity - used for registry in client proxy
     *
     * @return class of tile entity
     */
    public Class<? extends TileEntity> getTileEntityClass() {
        return LabConstructBlockTile.class;
    }

    // --------------------------------------------------------------------------------------
    // GUI

    @Override
    @ParametersAreNonnullByDefault
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if(tile instanceof LabConstructBlockTile){
                if (!player.isSneaking()) {
                    ((LabConstructBlockTile) tile).showLabGui(player, hand, world);
                }
            }
        }

        return !player.isSneaking();
    }

    // --------------------------------------------------------------------------------------
    // Rendering

    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        if (state.getValue(JSGProps.RENDER_BLOCK))
            return EnumBlockRenderType.MODEL;
        else
            return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
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
    public boolean isTranslucent(@Nonnull IBlockState state) {
        return true;
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing facing) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean renderHighlight(IBlockState state) {
        return state.getValue(JSGProps.RENDER_BLOCK);
    }
}
