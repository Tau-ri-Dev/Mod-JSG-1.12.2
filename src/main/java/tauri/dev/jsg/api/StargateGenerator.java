package tauri.dev.jsg.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlock;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.config.ingame.JSGTileEntityConfig;
import tauri.dev.jsg.fluid.JSGFluids;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.stargate.EnumIrisMode;
import tauri.dev.jsg.stargate.EnumMemberVariant;
import tauri.dev.jsg.stargate.merging.StargateClassicMergeHelper;
import tauri.dev.jsg.stargate.merging.StargateMilkyWayMergeHelper;
import tauri.dev.jsg.stargate.merging.StargatePegasusMergeHelper;
import tauri.dev.jsg.stargate.merging.StargateUniverseMergeHelper;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDAbstractTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateMilkyWayBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargatePegasusBaseTile;
import tauri.dev.jsg.util.BlockHelpers;
import tauri.dev.jsg.util.FacingHelper;
import tauri.dev.jsg.util.ItemHandlerHelper;
import tauri.dev.jsg.util.LinkingHelper;
import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.jsg.worldgen.util.GeneratedStargate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class StargateGenerator {
    public enum StargateUpgradesEnum {
        GLYPH_CRYSTAL_TYPE,
        GLYPH_CRYSTAL_STARGATE,
        GLYPH_CRYSTAL_MW,
        GLYPH_CRYSTAL_PEG,
        GLYPH_CRYSTAL_UNI,
        UPGRADE_IRIS_TITANIUM,
        UPGRADE_IRIS_TRINIUM,
        UPGRADE_IRIS_CREATIVE,
        UPGRADE_SHIELD,

        GLYPH_CRYSTAL_DHD,
        UPGRADE_EFFICIENCY_DHD,
        UPGRADE_CAPACITY_DHD
    }

    public static class PlacementConfig {
        // Global
        public World world;

        @Nonnull
        public List<StargateUpgradesEnum> upgrades = new ArrayList<>();
        @Nonnull
        public BiomeOverlayEnum overlay = BiomeOverlayEnum.NORMAL;
        @Nonnull
        public SymbolTypeEnum addressSymbolTypeToReturn = SymbolTypeEnum.MILKYWAY;

        // Gate
        public BlockPos gateBasePos;
        @Nonnull
        public EnumFacing gateFacing = EnumFacing.NORTH;
        @Nonnull
        public EnumFacing gateVerticalFacing = EnumFacing.SOUTH;
        @Nonnull
        public SymbolTypeEnum gateType = SymbolTypeEnum.MILKYWAY;
        @Nonnull
        public List<Map.Entry<Integer, Boolean>> capacitors = new ArrayList<>(); // List<Entry<[capacity], [isCreative]>>
        public int stargateEnergyInternal = -1;
        public JSGTileEntityConfig stargateConfig = null;
        @Nonnull
        public EnumIrisMode irisMode = EnumIrisMode.OPENED;
        @Nonnull
        public String irisCode = "";

        // DHD
        public BlockPos dhdPos;
        public int dhdRotation = -1;
        public int dhdFluid = -1;
    }

    /**
     * Generates merged gate with base block at specified position in config
     * @param conf - your placement configuration
     * @return generated gate
     */
    @Nullable
    public static GeneratedStargate generateStargate(@Nonnull PlacementConfig conf) {
        if (conf.world == null) return null;
        if (conf.gateBasePos == null) return null;

        StargateClassicMergeHelper mergeHelper;
        JSGBlock dhdBlock;
        IBlockState gateBaseBlockState;
        Item crystalGlyphUpgrade;
        switch (conf.gateType) {
            case UNIVERSE:
                mergeHelper = StargateUniverseMergeHelper.INSTANCE;
                gateBaseBlockState = JSGBlocks.STARGATE_UNIVERSE_BASE_BLOCK.getDefaultState();
                dhdBlock = null;
                crystalGlyphUpgrade = JSGItems.CRYSTAL_GLYPH_UNIVERSE;
                break;
            case PEGASUS:
                mergeHelper = StargatePegasusMergeHelper.INSTANCE;
                gateBaseBlockState = JSGBlocks.STARGATE_PEGASUS_BASE_BLOCK.getDefaultState();
                dhdBlock = JSGBlocks.DHD_PEGASUS_BLOCK;
                crystalGlyphUpgrade = JSGItems.CRYSTAL_GLYPH_PEGASUS;
                break;
            default:
                mergeHelper = StargateMilkyWayMergeHelper.INSTANCE;
                gateBaseBlockState = JSGBlocks.STARGATE_MILKY_WAY_BASE_BLOCK.getDefaultState();
                dhdBlock = JSGBlocks.DHD_BLOCK;
                crystalGlyphUpgrade = JSGItems.CRYSTAL_GLYPH_MILKYWAY;
                break;
        }

        // Place base block
        conf.world.setBlockState(conf.gateBasePos, gateBaseBlockState.withProperty(JSGProps.FACING_HORIZONTAL, conf.gateFacing).withProperty(JSGProps.FACING_VERTICAL, conf.gateVerticalFacing));

        StargateClassicBaseTile gateTile;
        try {
            gateTile = (StargateClassicBaseTile) conf.world.getTileEntity(conf.gateBasePos);
        } catch (Exception e) {
            JSG.error("Error while generating gate at " + BlockHelpers.blockPosToBetterString(conf.gateBasePos) + " in " + conf.world.provider.getDimensionType().getName(), e);
            return null;
        }
        if (gateTile == null) return null;

        // Place member blocks
        for (EnumMemberVariant variant : EnumMemberVariant.values()) {
            List<BlockPos> posList = mergeHelper.getAbsentBlockPositions(conf.world, conf.gateBasePos, conf.gateFacing, conf.gateVerticalFacing, variant);
            IBlockState memberState = mergeHelper.getMemberBlock().getDefaultState().withProperty(JSGProps.MEMBER_VARIANT, variant).withProperty(JSGProps.FACING_HORIZONTAL, conf.gateFacing).withProperty(JSGProps.FACING_VERTICAL, conf.gateVerticalFacing);

            if (posList.isEmpty()) continue;
            for (BlockPos pos : posList) {
                conf.world.setBlockState(pos, memberState);
            }
        }
        gateTile.updateMergeState(true, conf.gateFacing, conf.gateVerticalFacing);
        gateTile.refresh();
        gateTile.markDirty();

        if (conf.stargateConfig != null) {
            gateTile.initConfig();
            gateTile.setConfigAndUpdate(conf.stargateConfig);
        }

        int nextSlot = 0;
        boolean isTypeCrystalIn = false;
        ItemHandlerHelper.clearInventory(gateTile.getItemHandler());
        if (conf.upgrades.contains(StargateUpgradesEnum.GLYPH_CRYSTAL_STARGATE))
            gateTile.getItemHandler().insertItem(nextSlot++, new ItemStack(JSGItems.CRYSTAL_GLYPH_STARGATE, 1), false);
        if (conf.upgrades.contains(StargateUpgradesEnum.GLYPH_CRYSTAL_MW)) {
            gateTile.getItemHandler().insertItem(nextSlot++, new ItemStack(JSGItems.CRYSTAL_GLYPH_MILKYWAY, 1), false);
            if (conf.gateType == SymbolTypeEnum.MILKYWAY) isTypeCrystalIn = true;
        }
        if (conf.upgrades.contains(StargateUpgradesEnum.GLYPH_CRYSTAL_PEG)) {
            gateTile.getItemHandler().insertItem(nextSlot++, new ItemStack(JSGItems.CRYSTAL_GLYPH_PEGASUS, 1), false);
            if (conf.gateType == SymbolTypeEnum.PEGASUS) isTypeCrystalIn = true;
        }
        if (conf.upgrades.contains(StargateUpgradesEnum.GLYPH_CRYSTAL_UNI)) {
            gateTile.getItemHandler().insertItem(nextSlot++, new ItemStack(JSGItems.CRYSTAL_GLYPH_UNIVERSE, 1), false);
            if (conf.gateType == SymbolTypeEnum.UNIVERSE) isTypeCrystalIn = true;
        }

        if (!isTypeCrystalIn && nextSlot < 4 && conf.upgrades.contains(StargateUpgradesEnum.GLYPH_CRYSTAL_TYPE)) {
            gateTile.getItemHandler().insertItem(nextSlot, new ItemStack(crystalGlyphUpgrade, 1), false);
        }

        if (conf.upgrades.contains(StargateUpgradesEnum.UPGRADE_IRIS_TITANIUM))
            gateTile.getItemHandler().insertItem(11, new ItemStack(JSGItems.UPGRADE_IRIS, 1), false);
        if (conf.upgrades.contains(StargateUpgradesEnum.UPGRADE_IRIS_TRINIUM))
            gateTile.getItemHandler().insertItem(11, new ItemStack(JSGItems.UPGRADE_IRIS_TRINIUM, 1), false);
        if (conf.upgrades.contains(StargateUpgradesEnum.UPGRADE_SHIELD))
            gateTile.getItemHandler().insertItem(11, new ItemStack(JSGItems.UPGRADE_SHIELD, 1), false);
        if (conf.upgrades.contains(StargateUpgradesEnum.UPGRADE_IRIS_CREATIVE))
            gateTile.getItemHandler().insertItem(11, new ItemStack(JSGItems.UPGRADE_IRIS_CREATIVE, 1), false);

        nextSlot = 4;
        for (Map.Entry<Integer, Boolean> e : conf.capacitors) {
            if(nextSlot >= 7) break;
            ItemStack capacitor;
            if(e.getValue()) {
                capacitor = new ItemStack(JSGBlocks.CAPACITOR_BLOCK_CREATIVE);
            }
            else {
                if(e.getKey() < 0) continue;
                capacitor = new ItemStack(JSGBlocks.CAPACITOR_BLOCK);
                IEnergyStorage storage = capacitor.getCapability(CapabilityEnergy.ENERGY, null);
                if (storage != null)
                    storage.receiveEnergy(e.getKey(), false);
            }
            gateTile.getItemHandler().insertItem(nextSlot++, capacitor, false);
        }

        gateTile.setIrisMode(conf.irisMode);
        gateTile.setIrisCode(conf.irisCode);


        // DHD
        if (dhdBlock != null && conf.dhdPos != null) {
            int dhdRotation = conf.dhdRotation;
            if (dhdRotation < 0) {
                dhdRotation = FacingHelper.getIntDHDRotationFromFacing(conf.gateFacing, (conf.gateFacing == EnumFacing.WEST || conf.gateFacing == EnumFacing.EAST));
            }
            IBlockState dhdBlockState = dhdBlock.getDefaultState().withProperty(JSGProps.ROTATION_HORIZONTAL, dhdRotation);
            conf.world.setBlockState(conf.dhdPos, dhdBlockState);
            ItemStack crystal = new ItemStack(conf.gateType == SymbolTypeEnum.PEGASUS ? JSGItems.CRYSTAL_CONTROL_PEGASUS_DHD : JSGItems.CRYSTAL_CONTROL_MILKYWAY_DHD);
            DHDAbstractTile dhdTile = ((DHDAbstractTile) Objects.requireNonNull(conf.world.getTileEntity(conf.dhdPos)));
            dhdTile.getItemStackHandler().setStackInSlot(0, crystal);

            if (conf.upgrades.contains(StargateUpgradesEnum.GLYPH_CRYSTAL_DHD))
                dhdTile.getItemStackHandler().setStackInSlot(1, new ItemStack(JSGItems.CRYSTAL_GLYPH_DHD, 1));
            if (conf.upgrades.contains(StargateUpgradesEnum.UPGRADE_CAPACITY_DHD))
                dhdTile.getItemStackHandler().setStackInSlot(2, new ItemStack(JSGItems.CRYSTAL_UPGRADE_CAPACITY, 1));
            if (conf.upgrades.contains(StargateUpgradesEnum.UPGRADE_EFFICIENCY_DHD))
                dhdTile.getItemStackHandler().setStackInSlot(3, new ItemStack(JSGItems.CRYSTAL_UPGRADE_EFFICIENCY, 1));

            if (conf.dhdFluid >= 0) {
                dhdTile.getFluidHandler().setFluid(new FluidStack(JSGFluids.NAQUADAH_MOLTEN_REFINED, conf.dhdFluid));
            }

            LinkingHelper.updateLinkedGate(conf.world, conf.gateBasePos, conf.dhdPos);
        }


        ResourceLocation biomePath = conf.world.getBiome(conf.gateBasePos).getRegistryName();
        StargateAddress gateAddress = gateTile.getStargateAddress(conf.addressSymbolTypeToReturn);
        return new GeneratedStargate(gateAddress, (biomePath == null ? null : biomePath.getResourcePath()), true, gateTile.getOriginId());
    }

    /**
     * Switch types of gate
     */
    @Nullable
    @SuppressWarnings("all")
    public static GeneratedStargate switchGateType(@Nonnull World world, @Nonnull BlockPos gatePos, @Nonnull SymbolTypeEnum newGateType, boolean keepNBT) {
        TileEntity te = world.getTileEntity(gatePos);
        if (!(te instanceof StargateClassicBaseTile)) return null;

        StargateClassicBaseTile gateTile = (StargateClassicBaseTile) te;
        if (!gateTile.isMerged()) return null;
        if (gateTile.getSymbolType() == newGateType) return null;

        DHDAbstractTile dhdTile = null;
        if (gateTile instanceof StargateMilkyWayBaseTile) {
            dhdTile = ((StargateMilkyWayBaseTile) gateTile).getLinkedDHD(world);
        } else if (gateTile instanceof StargatePegasusBaseTile) {
            dhdTile = ((StargatePegasusBaseTile) gateTile).getLinkedDHD(world);
        }

        NBTTagCompound oldStargateNBT = gateTile.serializeNBT();
        ItemHandlerHelper.clearInventory(gateTile.getItemHandler());

        NBTTagCompound oldDhdNBT = (dhdTile != null ? dhdTile.serializeNBT() : null);
        if (dhdTile != null)
            ItemHandlerHelper.clearInventory(dhdTile.getItemHandler());
        int dhdRotation = (dhdTile != null ? world.getBlockState(dhdTile.getPos()).getValue(JSGProps.ROTATION_HORIZONTAL) : -1);

        PlacementConfig newConfig = new PlacementConfig();
        newConfig.world = world;
        newConfig.gateBasePos = gatePos;
        newConfig.gateType = newGateType;
        newConfig.addressSymbolTypeToReturn = newGateType;
        newConfig.gateFacing = gateTile.getFacing();
        newConfig.gateVerticalFacing = gateTile.getFacingVertical();
        newConfig.overlay = gateTile.getBiomeOverlayWithOverride(false);

        newConfig.dhdPos = (dhdTile != null ? dhdTile.getPos() : null);
        newConfig.dhdRotation = dhdRotation;

        GeneratedStargate genStargate = generateStargate(newConfig);
        try {
            StargateClassicBaseTile gateTileNew = (StargateClassicBaseTile) world.getTileEntity(gatePos);
            if(keepNBT)
                Objects.requireNonNull(gateTileNew).deserializeNBT(oldStargateNBT);
            gateTileNew.updatePosSymbolType();
            gateTileNew.markDirty();
            gateTileNew.refresh();
        } catch (Exception ignored) {
        }

        if (newGateType == SymbolTypeEnum.UNIVERSE && dhdTile != null) {
            world.setBlockToAir(dhdTile.getPos());
        } else if (dhdTile != null) {
            try {
                ItemStack crystal = new ItemStack(newGateType == SymbolTypeEnum.PEGASUS ? JSGItems.CRYSTAL_CONTROL_PEGASUS_DHD : JSGItems.CRYSTAL_CONTROL_MILKYWAY_DHD);
                DHDAbstractTile dhdTileNew = (DHDAbstractTile) world.getTileEntity(dhdTile.getPos());
                if(keepNBT)
                    Objects.requireNonNull(dhdTileNew).deserializeNBT(oldDhdNBT);
                Objects.requireNonNull(dhdTileNew).getItemStackHandler().setStackInSlot(0, crystal);
                dhdTileNew.markDirty();
            } catch (Exception ignored) {
            }
        }

        return genStargate;
    }
}
