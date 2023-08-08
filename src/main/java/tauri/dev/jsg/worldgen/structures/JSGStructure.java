package tauri.dev.jsg.worldgen.structures;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.capability.CapabilityEnergyZPM;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.ingame.JSGTileEntityConfig;
import tauri.dev.jsg.config.stargate.StargateSizeEnum;
import tauri.dev.jsg.fluid.JSGFluids;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.item.notebook.PageNotebookItem;
import tauri.dev.jsg.power.general.LargeEnergyStorage;
import tauri.dev.jsg.power.zpm.IEnergyStorageZPM;
import tauri.dev.jsg.power.zpm.ZPMItemEnergyStorage;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.StargateNetwork;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDAbstractTile;
import tauri.dev.jsg.tileentity.energy.ZPMHubTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsAbstractTile;
import tauri.dev.jsg.util.FacingHelper;
import tauri.dev.jsg.util.LinkingHelper;
import tauri.dev.jsg.worldgen.util.EnumGenerationHeight;
import tauri.dev.jsg.worldgen.util.GeneratedStargate;
import tauri.dev.jsg.worldgen.util.JSGWorldTopBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import static tauri.dev.jsg.worldgen.structures.JSGStructuresGenerator.findOptimalRotation;


public class JSGStructure extends WorldGenerator {
    protected final PlacementSettings defaultSettings = new PlacementSettings().setIgnoreStructureBlock(false).setRotation(Rotation.NONE).setIgnoreEntities(false);

    public String structureName;
    public int yNegativeOffset;
    public int dimensionToSpawn;
    boolean isStargateStructure;
    boolean isRingsStructure;
    SymbolTypeEnum symbolType;
    public boolean findOptimalRotation;

    boolean isMilkyWayGate;
    boolean isPegasusGate;
    boolean isUniverseGate;

    public int structureSizeX;
    public int structureSizeZ;
    public int airUp;

    public final ITemplateProcessor templateProcessor;
    public final Rotation rotationToNorth;

    public double terrainFlatPercents;
    public double topBlockMatchPercent;

    public EnumGenerationHeight genHeight;

    public JSGStructure(String structureName, int yNegativeOffset, boolean isStargateStructure, boolean isRingsStructure, SymbolTypeEnum symbolType, int structureSizeX, int structureSizeZ, int airUp, int dimensionToSpawn, boolean findOptimalRotation, @Nullable ITemplateProcessor templateProcessor, Rotation rotationToNorth, double terrainFlatPercents, double topBlockMatchPercent, @Nonnull EnumGenerationHeight genHeight) {
        this.structureName = structureName + (isStargateStructure ? (tauri.dev.jsg.config.JSGConfig.Stargate.stargateSize == StargateSizeEnum.LARGE ? "_large" : "_small") : "");
        this.yNegativeOffset = yNegativeOffset;
        this.isStargateStructure = isStargateStructure;
        this.isRingsStructure = isRingsStructure;
        this.symbolType = symbolType;
        this.structureSizeX = structureSizeX;
        this.structureSizeZ = structureSizeZ;
        this.dimensionToSpawn = dimensionToSpawn;
        this.findOptimalRotation = findOptimalRotation;
        this.templateProcessor = templateProcessor;
        this.rotationToNorth = rotationToNorth;
        this.terrainFlatPercents = terrainFlatPercents;
        this.topBlockMatchPercent = topBlockMatchPercent;
        this.genHeight = genHeight;
        this.airUp = airUp;


        this.isMilkyWayGate = (symbolType == SymbolTypeEnum.MILKYWAY);
        this.isPegasusGate = (symbolType == SymbolTypeEnum.PEGASUS);
        this.isUniverseGate = (symbolType == SymbolTypeEnum.UNIVERSE);
    }

    @Override
    public boolean generate(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos position) {
        return true;
    }

    @Nullable
    public GeneratedStargate generateStructure(World executedInWorld, BlockPos pos, Random random, @Nullable WorldServer worldToSpawn) {
        return generateStructure(executedInWorld, pos, random, worldToSpawn, null);
    }

    public boolean canDHDDespawn() {
        return true;
    }

    public GeneratedStargate generateStructure(World executedInWorld, BlockPos pos, Random random, @Nullable WorldServer worldToSpawn, @Nullable Rotation rotationOverride) {
        pos = pos.down(yNegativeOffset);
        MinecraftServer mcServer = executedInWorld.getMinecraftServer();
        if (mcServer == null) return null;
        JSG.info("Structure " + structureName + " generation started!");
        worldToSpawn = (worldToSpawn == null ? mcServer.getWorld(dimensionToSpawn) : worldToSpawn);
        worldToSpawn.getChunkProvider().loadChunk(pos.getX() / 16, pos.getZ() / 16);
        TemplateManager manager = worldToSpawn.getStructureTemplateManager();
        ResourceLocation resourceLocation = new ResourceLocation(JSG.MOD_ID, structureName);
        Template template = manager.getTemplate(mcServer, resourceLocation);

        IBlockState state = worldToSpawn.getBlockState(pos);
        Biome biome = worldToSpawn.getBiome(pos);
        worldToSpawn.notifyBlockUpdate(pos, state, state, 3);
        EnumFacing facing = (findOptimalRotation ? findOptimalRotation(worldToSpawn, pos) : EnumFacing.SOUTH);
        Rotation rotation = FacingHelper.getRotation(facing);
        rotation = rotation.add(rotationToNorth);

        if (rotationOverride != null)
            rotation = rotationOverride;
        template.addBlocksToWorld(worldToSpawn, pos, templateProcessor, defaultSettings.setRotation(rotation), 3);

        Map<BlockPos, String> dataBlocks = template.getDataBlocks(pos, defaultSettings);
        BlockPos lootPos;

        BlockPos gatePos = null;
        BlockPos dhdPos = null;
        StargateClassicBaseTile gateTile = null;
        ArrayList<TransportRingsAbstractTile> ringsTiles = new ArrayList<>();

        boolean hasUpgrade = (dimensionToSpawn != executedInWorld.provider.getDimension());

        JSG.info("Structure " + structureName + " generated at " + pos + " in world " + worldToSpawn.provider.getDimension() + " (" + worldToSpawn.provider.getDimensionType().getName() + ")");

        for (BlockPos dataPos : dataBlocks.keySet()) {
            String name = dataBlocks.get(dataPos);

            switch (name) {
                // stargate
                case "base":
                    worldToSpawn.setBlockToAir(dataPos);
                    gatePos = dataPos.down(3);
                    try {
                        gateTile = (StargateClassicBaseTile) worldToSpawn.getTileEntity(gatePos);
                        if (gateTile == null) break;
                        IItemHandler gateContainer = gateTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                        if (gateContainer != null) {
                            if (!isUniverseGate || JSGConfig.Stargate.power.universeCapacitors > 0) {
                                // is not uni gate OR capacitors are enabled for these gates
                                ItemStack capacitor = new ItemStack(JSGBlocks.CAPACITOR_BLOCK);
                                if (isUniverseGate) {
                                    // is uni gate -> add energy to capacitor (uni gate doesn't have DHD to power up itself)
                                    IEnergyStorage storage = capacitor.getCapability(CapabilityEnergy.ENERGY, null);
                                    if (storage != null)
                                        storage.receiveEnergy(((int) (storage.getMaxEnergyStored() * 0.5)), false);
                                }
                                gateContainer.insertItem(4, capacitor, false); // insert capacitor

                                if (isMilkyWayGate)
                                    gateContainer.insertItem(0, new ItemStack(JSGItems.CRYSTAL_GLYPH_MILKYWAY), false);
                                if (isPegasusGate)
                                    gateContainer.insertItem(0, new ItemStack(JSGItems.CRYSTAL_GLYPH_PEGASUS), false);
                                if (isUniverseGate)
                                    gateContainer.insertItem(0, new ItemStack(JSGItems.CRYSTAL_GLYPH_UNIVERSE), false);

                                if (hasUpgrade)
                                    gateContainer.insertItem(1, new ItemStack(JSGItems.CRYSTAL_GLYPH_STARGATE), false);
                            }
                        }
                        // insert power to the gate itself
                        IEnergyStorage gateEnergy = gateTile.getCapability(CapabilityEnergy.ENERGY, null);
                        if (gateEnergy != null)
                            gateEnergy.receiveEnergy(((int) (((LargeEnergyStorage) gateEnergy).getMaxEnergyStoredInternally() * 0.75)), false);
                        gateTile.getMergeHelper().updateMembersBasePos(worldToSpawn, gatePos, facing, EnumFacing.SOUTH);
                    } catch (Exception e) {
                        JSG.error("Error while generating structure " + structureName + ":", e);
                        gateTile = null;
                    }
                    break;
                case "dhd":
                    try {
                        worldToSpawn.setBlockState(dataPos, worldToSpawn.getBlockState(dataPos.east()));
                        dhdPos = dataPos.down();

                        // set the DHD to the topBlock
                        JSGWorldTopBlock topBlock = JSGWorldTopBlock.getTopBlock(worldToSpawn, dhdPos.getX(), dhdPos.getZ(), 3, worldToSpawn.provider.getDimension());
                        if (topBlock != null && (topBlock.y != dhdPos.getY()) && (Math.abs(topBlock.y - dhdPos.getY()) < 12)) {
                            IBlockState dhd = worldToSpawn.getBlockState(dhdPos);
                            worldToSpawn.setBlockState(dhdPos, topBlock.topBlockState, 3);
                            dhdPos = new BlockPos(dhdPos.getX(), (topBlock.y + 1), dhdPos.getZ());
                            worldToSpawn.setBlockState(dhdPos, dhd, 3);
                        }

                        if (canDHDDespawn() && random.nextFloat() < JSGConfig.WorldGen.mystPage.despawnDhdChance) {
                            worldToSpawn.setBlockToAir(dhdPos);
                            break;
                        }
                        DHDAbstractTile dhdTile = (DHDAbstractTile) worldToSpawn.getTileEntity(dhdPos);
                        if (dhdTile == null) break;

                        final int fluid = JSGConfig.Stargate.power.stargateEnergyStorage / JSGConfig.DialHomeDevice.power.energyPerNaquadah;
                        final ItemStack crystal = new ItemStack(isPegasusGate ? JSGItems.CRYSTAL_CONTROL_PEGASUS_DHD : JSGItems.CRYSTAL_CONTROL_MILKYWAY_DHD);
                        IItemHandler dhdContainer = dhdTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                        IFluidHandler dhdFluidTank = dhdTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

                        if (dhdContainer != null) {
                            dhdContainer.insertItem(0, crystal, false);
                            if (hasUpgrade)
                                dhdContainer.insertItem(1, new ItemStack(JSGItems.CRYSTAL_GLYPH_DHD), false);
                        }

                        if (dhdFluidTank instanceof FluidTank)
                            ((FluidTank) dhdFluidTank).fillInternal(new FluidStack(JSGFluids.NAQUADAH_MOLTEN_REFINED, fluid), true);
                    } catch (Exception e) {
                        JSG.error("Error while generating structure " + structureName + ":", e);
                    }
                    break;
                // rings
                case "rings":
                case "rings_top":
                    try {
                        boolean top = (name.equals("rings_top"));

                        worldToSpawn.setBlockToAir(dataPos);
                        BlockPos ringsPos = dataPos.down(2);

                        if (top) {
                            JSGWorldTopBlock trTopBlock = JSGWorldTopBlock.getTopBlock(worldToSpawn, ringsPos.getX(), ringsPos.getZ(), 3, worldToSpawn.provider.getDimension());
                            if (trTopBlock != null && (trTopBlock.y != ringsPos.getY()) && (Math.abs(trTopBlock.y - ringsPos.getY()) < 12)) {
                                IBlockState bState = worldToSpawn.getBlockState(ringsPos);
                                worldToSpawn.setBlockState(ringsPos, trTopBlock.topBlockState, 3);
                                ringsPos = new BlockPos(ringsPos.getX(), (trTopBlock.y - 1), ringsPos.getZ());
                                worldToSpawn.setBlockState(ringsPos, bState, 3);
                            }
                        }

                        TransportRingsAbstractTile ringsTile = (TransportRingsAbstractTile) worldToSpawn.getTileEntity(ringsPos);
                        if (ringsTile == null) break;
                        IEnergyStorage ringsEnergy = ringsTile.getCapability(CapabilityEnergy.ENERGY, null);
                        if (ringsEnergy != null)
                            ringsEnergy.receiveEnergy(((int) (((LargeEnergyStorage) ringsEnergy).getMaxEnergyStoredInternally() * 0.75)), false);

                        ringsTiles.add(ringsTile);
                    } catch (Exception e) {
                        JSG.error("Error while generating structure " + structureName + ":", e);
                    }
                    break;
                // global
                case "structure":
                    // remove structure block
                    worldToSpawn.setBlockToAir(dataPos);
                    worldToSpawn.setBlockToAir(dataPos.down()); // save block
                    break;
                case "loot_ov":
                case "loot_nether":
                case "loot_zpm":
                case "loot_end":
                case "loot_obelisk":
                    lootPos = dataPos.down();
                    generateLoot(worldToSpawn, lootPos, random, name);
                    worldToSpawn.setBlockToAir(dataPos);
                    break;
                default:
                    break;
            }
        }

        if (isStargateStructure && gateTile != null) {
            if (dhdPos != null)
                LinkingHelper.updateLinkedGate(worldToSpawn, gatePos, dhdPos);
            gateTile.refresh();
            gateTile.getMergeHelper().updateMembersMergeStatus(worldToSpawn, gateTile.getPos(), gateTile.getFacing(), gateTile.getFacingVertical(), true);
            gateTile.markDirty();

            double unstableChance = JSGConfig.WorldGen.mystPage.forcedUnstableGateChance;
            if (unstableChance > 0 && (random.nextFloat() < unstableChance)) {
                JSGTileEntityConfig config = gateTile.getConfig();
                config.getOption(StargateClassicBaseTile.ConfigOptions.FORCE_UNSTABLE_EH.id).setBooleanValue(true);
                gateTile.setConfigAndUpdate(config);
            }

            StargateAddress address = gateTile.getStargateAddress(symbolType);

            if (address != null && !gateTile.getNetwork().isStargateInNetwork(address))
                gateTile.getNetwork().addStargate(address, new StargatePos(worldToSpawn.provider.getDimensionType().getId(), gatePos, address, gateTile.getSymbolType()));

            ResourceLocation biomePath = biome.getRegistryName();
            return new GeneratedStargate(address, (biomePath == null ? null : biomePath.getResourcePath()), hasUpgrade, gateTile.getOriginId());
        }
        if (isRingsStructure && ringsTiles.size() > 0) {
            for (TransportRingsAbstractTile ringsTile : ringsTiles) {
                ringsTile.generateAddress(true);
                ringsTile.updateLinkStatus();
                ringsTile.updatePlatformStatus();
                ringsTile.updateRingsDistance();
                ringsTile.markDirty();
                ringsTile.clearObstructedRadius();
            }
        }
        return null;
    }

    private static void generateLoot(World world, BlockPos chestPos, Random random, String lootTableName) {
        TileEntity tile = world.getTileEntity(chestPos);
        if (tile instanceof TileEntityChest) {
            TileEntityChest chest = (TileEntityChest) tile;
            chest.setLootTable(new ResourceLocation(JSG.MOD_ID, lootTableName), random.nextLong());
            chest.fillWithLoot(null);

            // Set ZPM energy
            if (lootTableName.equalsIgnoreCase("loot_obelisk")) {
                spawnZPMInChest(chest, true, null, false);
            }

            // Set sus page address
            spawnSusPageInChest(chest, true, false);
        }
    }

    public static void spawnZPMInChest(@Nonnull TileEntityChest chest, boolean findAlreadySpawned, @Nullable Float energyPercent, boolean stopOnFound) {
        spawnZPMInHandler(chest.getSingleChestHandler(), findAlreadySpawned, energyPercent, stopOnFound);
    }

    public static void spawnZPMInZPMHub(@Nonnull ZPMHubTile zpmHub, boolean findAlreadySpawned, @Nullable Float energyPercent, boolean stopOnFound) {
        spawnZPMInHandler(zpmHub.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), findAlreadySpawned, energyPercent, stopOnFound);
    }
    public static void spawnZPMInHandler(@Nullable IItemHandler handler, boolean findAlreadySpawned, @Nullable Float energyPercent, boolean stopOnFound) {
        if(handler == null) return;
        if(energyPercent == null) energyPercent = (float) ((Math.random() * 0.7f) + 0.1f);
        if(energyPercent < 0) energyPercent = 0f;

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            IEnergyStorageZPM energyStorage = stack.getCapability(CapabilityEnergyZPM.ENERGY, null);
            if (findAlreadySpawned) {
                if(!stack.isEmpty()){
                    if ((energyStorage instanceof ZPMItemEnergyStorage)) {
                        ZPMItemEnergyStorage energyCasted = (ZPMItemEnergyStorage) energyStorage;
                        energyCasted.setEnergyStored((long) (energyCasted.getMaxEnergyStored() * energyPercent));
                        if(stopOnFound) return;
                    }
                }
            } else {
                if(stack.isEmpty()){
                    stack = new ItemStack(JSGBlocks.ZPM, 1);
                    energyStorage = stack.getCapability(CapabilityEnergyZPM.ENERGY, null);
                    if ((energyStorage instanceof ZPMItemEnergyStorage)) {
                        ZPMItemEnergyStorage energyCasted = (ZPMItemEnergyStorage) energyStorage;
                        energyCasted.setEnergyStored((long) (energyCasted.getMaxEnergyStored() * energyPercent));
                        handler.insertItem(i, stack, false);
                        if(stopOnFound) return;
                    }
                }
            }
        }
    }
    public static void spawnSusPageInChest(@Nonnull TileEntityChest chest, boolean findAlreadySpawned, boolean stopOnFound) {
        StargateNetwork sgn = StargateNetwork.get(chest.getWorld());
        IItemHandler handler = chest.getSingleChestHandler();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if ((findAlreadySpawned && !stack.isEmpty() && stack.getItem() == JSGItems.PAGE_NOTEBOOK_ITEM) || (!findAlreadySpawned && stack.isEmpty())) {
                if(!findAlreadySpawned) stack = new ItemStack(JSGItems.PAGE_NOTEBOOK_ITEM, 1);
                Map.Entry<StargatePos, Map<SymbolTypeEnum, StargateAddress>> gotAddressMap = sgn.getRandomNotGeneratedStargate();
                if (gotAddressMap == null) {
                    // Got no stargate -> remove page from the chest
                    stack.setCount(0);
                    handler.insertItem(i, new ItemStack(Blocks.WEB, 1), false);
                    continue;
                }
                SymbolTypeEnum symbolTypeEnum = SymbolTypeEnum.getRandom();
                StargateAddress address = gotAddressMap.getValue().get(symbolTypeEnum);
                StargatePos pos = gotAddressMap.getKey();

                String biome = ((pos.getWorld() == null || pos.gatePos == null) ? "plains" : PageNotebookItem.getRegistryPathFromWorld(pos.getWorld(), pos.gatePos));
                int origin = StargateClassicBaseTile.getOriginId(null, pos.dimensionID, -1);

                NBTTagCompound sgCompound = PageNotebookItem.getCompoundFromAddress(address, true, true, true, biome, origin);
                stack.setTagCompound(sgCompound);
                stack.setItemDamage(1);
                stack.setStackDisplayName("Suspicious page");
                if(!findAlreadySpawned) handler.insertItem(i, stack, false);
                if(stopOnFound) return;
            }
        }
    }
}
