package tauri.dev.jsg.block.stargate;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlock;
import tauri.dev.jsg.stargate.merging.StargateAbstractMergeHelper;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractMemberTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicMemberTile;
import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.jsg.util.main.loader.JSGCreativeTabsHandler;

import javax.annotation.Nullable;

public abstract class StargateAbstractMemberBlock extends JSGBlock {

    public StargateAbstractMemberBlock(String blockName) {
        super(Material.IRON);

        setRegistryName(JSG.MOD_ID + ":" + blockName);
        setUnlocalizedName(JSG.MOD_ID + "." + blockName);

        setSoundType(SoundType.METAL);
        setCreativeTab(JSGCreativeTabsHandler.jsgGatesCreativeTab);

        setHardness(3.0f);
        setResistance(60.0f);
        setHarvestLevel("pickaxe", 3);
    }

    protected abstract StargateAbstractMergeHelper getMergeHelper();

    // -----------------------------------
    // Explosions

    @Override
    public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn) {
        worldIn.newExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 20, true, true).doExplosionA();
    }


    // --------------------------------------------------------------------------------------
    // Interactions

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        StargateAbstractMemberTile memberTile = (StargateAbstractMemberTile) world.getTileEntity(pos);
        StargateAbstractBaseTile gateTile = memberTile.getBaseTile(world);

        if (gateTile != null) {
            gateTile.updateMergeState(false, world.getBlockState(gateTile.getPos()).getValue(JSGProps.FACING_HORIZONTAL));
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest) return true; //If it will harvest, delay deletion of the block until after getDrops
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack tool) {
        super.harvestBlock(world, player, pos, state, te, tool);
        world.setBlockToAir(pos);
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
