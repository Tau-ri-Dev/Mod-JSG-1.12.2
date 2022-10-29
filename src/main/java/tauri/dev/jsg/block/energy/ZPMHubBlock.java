package tauri.dev.jsg.block.energy;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlock;
import tauri.dev.jsg.renderer.zpm.ZPMHubRenderer;
import tauri.dev.jsg.stargate.power.StargateAbstractEnergyStorage;
import tauri.dev.jsg.tileentity.energy.ZPMHubTile;
import tauri.dev.jsg.util.ItemHandlerHelper;
import tauri.dev.jsg.util.main.loader.JSGCreativeTabsHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ZPMHubBlock extends JSGBlock {

    public static final String BLOCK_NAME = "zpm_hub_block";

    public ZPMHubBlock() {
        super(Material.GLASS);
        setRegistryName(JSG.MOD_ID + ":" + BLOCK_NAME);
        setUnlocalizedName(JSG.MOD_ID + "." + BLOCK_NAME);

        setSoundType(SoundType.GLASS);
        setCreativeTab(JSGCreativeTabsHandler.jsgEnergyCreativeTab);

        setHardness(3.0f);
        setHarvestLevel("pickaxe", 3);
    }

    // ------------------------------------------------------------------------
    // Block actions

    @Override
    public void onBlockPlacedBy(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase placer, ItemStack stack) {
        IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);

        StargateAbstractEnergyStorage capacitorEnergyStorage = (StargateAbstractEnergyStorage) world.getTileEntity(pos).getCapability(CapabilityEnergy.ENERGY, null);
        capacitorEnergyStorage.setEnergyStored(energyStorage.getEnergyStored());
    }


    @Nonnull
    @Override
    public List<ItemStack> getDrops(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
        ItemStack stack = new ItemStack(this);
        return Collections.singletonList(stack);
    }

    @Override
    public void breakBlock(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if (!world.isRemote) {
            ZPMHubTile tile = (ZPMHubTile) world.getTileEntity(pos);
            if (tile != null) {
                ItemHandlerHelper.dropInventoryItems(world, pos, tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));
            }
        }
        super.breakBlock(world, pos, state);
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

    // ------------------------------------------------------------------------
    // Tile Entity

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public Class<? extends TileEntity> getTileEntityClass() {
        return ZPMHubTile.class;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new ZPMHubTile();
    }


    // ------------------------------------------------------------------------
    // Rendering

    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(@Nonnull IBlockState state) {
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
    public boolean renderHighlight(IBlockState blockState) {
        return false;
    }

    @Override
    public TileEntitySpecialRenderer<? extends TileEntity> getTESR() {
        return new ZPMHubRenderer();
    }
}
