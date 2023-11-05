package tauri.dev.jsg.block.stargate;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.TechnicalBlock;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.creativetabs.JSGCreativeTabsHandler;
import tauri.dev.jsg.stargate.merging.StargateAbstractMergeHelper;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractMemberTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicMemberTile;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public abstract class StargateAbstractMemberBlock extends TechnicalBlock {

    public StargateAbstractMemberBlock(String blockName) {
        super(Material.IRON);

        setRegistryName(JSG.MOD_ID + ":" + blockName);
        setUnlocalizedName(JSG.MOD_ID + "." + blockName);

        setSoundType(SoundType.METAL);
        setCreativeTab(JSGCreativeTabsHandler.JSG_GATES_CREATIVE_TAB);

        setHardness(JSGConfig.Stargate.mechanics.enableGateDisassembleWrench ? -1 : 5);
        setResistance(60.0f);
        if(JSGConfig.Stargate.mechanics.enableGateDisassembleWrench)
            setHarvestLevel("wrench", -1);
        else
            setHarvestLevel("pickaxe", 2);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity){
        return false;
    }

    public void destroyAndGiveDrops(boolean isShifting, EntityPlayer player, World world, BlockPos pos, EnumHand hand, IBlockState state) {
        dropBlockAsItem(world, pos, state, -2);
        world.setBlockToAir(pos);
        player.getHeldItem(hand).damageItem(1, player);
    }

    protected abstract StargateAbstractMergeHelper getMergeHelper();

    // -----------------------------------
    // Explosions

    @Override
    public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, @Nonnull Explosion explosionIn) {
        worldIn.newExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 20, true, true).doExplosionA();
    }


    // --------------------------------------------------------------------------------------
    // Interactions

    @Override
    @ParametersAreNonnullByDefault
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        StargateAbstractMemberTile memberTile = (StargateAbstractMemberTile) world.getTileEntity(pos);
        if (memberTile == null) return;
        StargateAbstractBaseTile gateTile = memberTile.getBaseTile(world);

        if (gateTile == null) return;
        gateTile.updateMergeState(false, world.getBlockState(gateTile.getPos()).getValue(JSGProps.FACING_HORIZONTAL), world.getBlockState(gateTile.getPos()).getValue(JSGProps.FACING_VERTICAL));
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest) return true; //If it will harvest, delay deletion of the block until after getDrops
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack tool) {
        tryBreak(null, true, false, player, world, pos, player.getActiveHand(), state);
        super.harvestBlock(world, player, pos, state, te, tool);
        world.setBlockToAir(pos);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(tryBreak(player.getHeldItem(hand), false, player.isSneaking(), player, world, pos, hand, state)) return true;
        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    // --------------------------------------------------------------------------------------
    // TileEntity


    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public abstract TileEntity createTileEntity(World world, IBlockState state);


    // --------------------------------------------------------------------------------------
    // Rendering

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        if (state.getValue(JSGProps.RENDER_BLOCK))
            return EnumBlockRenderType.MODEL;
        else
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
    public boolean isTranslucent(IBlockState state) {
        return true;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing facing) {
        if (state.getValue(JSGProps.RENDER_BLOCK)) {
            // Rendering some block
            if (world.getTileEntity(pos) instanceof StargateClassicMemberTile) {
                StargateClassicMemberTile memberTile = (StargateClassicMemberTile) world.getTileEntity(pos);
                if (memberTile != null && memberTile.getCamoState() != null) {
                    return memberTile.getCamoState().getBlockFaceShape(world, pos, facing);
                }
            }
        }

        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean renderHighlight(IBlockState state) {
        return state.getValue(JSGProps.RENDER_BLOCK);
    }
}
