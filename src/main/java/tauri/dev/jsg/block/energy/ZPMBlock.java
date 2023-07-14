package tauri.dev.jsg.block.energy;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGAbstractCustomItemBlock;
import tauri.dev.jsg.capability.CapabilityEnergyZPM;
import tauri.dev.jsg.creativetabs.JSGCreativeTabsHandler;
import tauri.dev.jsg.item.energy.ZPMItemBlock;
import tauri.dev.jsg.power.zpm.IEnergyStorageZPM;
import tauri.dev.jsg.power.zpm.ZPMEnergyStorage;
import tauri.dev.jsg.power.zpm.ZPMItemEnergyStorage;
import tauri.dev.jsg.renderer.zpm.ZPMRenderer;
import tauri.dev.jsg.tileentity.energy.ZPMTile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ZPMBlock extends JSGAbstractCustomItemBlock {

    public static final String BLOCK_NAME = "zpm";

    public ZPMBlock(boolean registered) {
        super(Material.GLASS);
        if (!registered) {
            setRegistryName(JSG.MOD_ID + ":" + BLOCK_NAME);
            setUnlocalizedName(JSG.MOD_ID + "." + BLOCK_NAME);
        }

        setSoundType(SoundType.GLASS);
        setCreativeTab(JSGCreativeTabsHandler.JSG_ENERGY_CREATIVE_TAB);

        setHardness(3.0f);
        setHarvestLevel("pickaxe", 3);
    }

    // ------------------------------------------------------------------------
    // Block actions

    @Override
    public void onBlockPlacedBy(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase placer, ItemStack stack) {
        IEnergyStorageZPM energyStorage = stack.getCapability(CapabilityEnergyZPM.ENERGY, null);

        ZPMEnergyStorage zpmStorage = (ZPMEnergyStorage) Objects.requireNonNull(world.getTileEntity(pos)).getCapability(CapabilityEnergyZPM.ENERGY, null);
        if (energyStorage != null) {
            Objects.requireNonNull(zpmStorage).setEnergyStored(energyStorage.getEnergyStored());
        }
    }


    @Nonnull
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
        ZPMEnergyStorage capacitorEnergyStorage = (ZPMEnergyStorage) world.getTileEntity(pos).getCapability(CapabilityEnergyZPM.ENERGY, null);

        ItemStack stack = new ItemStack(this);
        ((ZPMItemEnergyStorage) stack.getCapability(CapabilityEnergyZPM.ENERGY, null)).setEnergyStored(capacitorEnergyStorage.getEnergyStored());

        return Collections.singletonList(stack);
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
        return ZPMTile.class;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new ZPMTile();
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
    public ItemBlock getItemBlock() {
        return new ZPMItemBlock(this, false);
    }

    @Override
    public boolean renderHighlight(IBlockState blockState) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TileEntitySpecialRenderer<? extends TileEntity> getTESR() {
        return new ZPMRenderer();
    }
}
