package tauri.dev.jsg.worldgen.structures;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
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
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.stargate.StargateSizeEnum;
import tauri.dev.jsg.fluid.JSGFluids;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.stargate.power.StargateClassicEnergyStorage;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDAbstractTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.util.FacingToRotation;
import tauri.dev.jsg.util.LinkingHelper;
import tauri.dev.jsg.worldgen.structures.stargate.GeneratedStargate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

import static tauri.dev.jsg.worldgen.structures.JSGStructuresGenerator.findOptimalRotation;


public class JSGStructure extends WorldGenerator {
    private final PlacementSettings defaultSettings = new PlacementSettings().setIgnoreStructureBlock(false).setRotation(Rotation.NONE).setIgnoreEntities(false);

    public String structureName;
    public int yNegativeOffset;
    public int dimensionToSpawn;
    boolean isStargateStructure;
    SymbolTypeEnum symbolType;
    boolean findOptimalRotation;

    boolean isMilkyWayGate;
    boolean isPegasusGate;
    boolean isUniverseGate;

    public int structureSizeX;
    public int structureSizeZ;

    public final ITemplateProcessor templateProcessor;
    public final Rotation rotationToNorth;

    public JSGStructure(String structureName, int yNegativeOffset, boolean isStargateStructure, SymbolTypeEnum symbolType, int structureSizeX, int structureSizeZ, int dimensionToSpawn, boolean findOptimalRotation, @Nullable ITemplateProcessor templateProcessor, Rotation rotationToNorth) {
        this.structureName = structureName + (isStargateStructure ? (tauri.dev.jsg.config.JSGConfig.stargateSize == StargateSizeEnum.LARGE ? "_large" : "_small") : "");
        this.yNegativeOffset = yNegativeOffset;
        this.isStargateStructure = isStargateStructure;
        this.symbolType = symbolType;
        this.structureSizeX = structureSizeX;
        this.structureSizeZ = structureSizeZ;
        this.dimensionToSpawn = dimensionToSpawn;
        this.findOptimalRotation = findOptimalRotation;
        this.templateProcessor = templateProcessor;
        this.rotationToNorth = rotationToNorth;

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
        pos = pos.down(yNegativeOffset);
        MinecraftServer mcServer = executedInWorld.getMinecraftServer();
        JSG.info("Structure " + structureName + " generation started!");
        if (mcServer == null) return null;
        worldToSpawn = (worldToSpawn == null ? mcServer.getWorld(dimensionToSpawn) : worldToSpawn);
        worldToSpawn.getChunkProvider().loadChunk(pos.getX()/16, pos.getZ()/16);
        TemplateManager manager = worldToSpawn.getStructureTemplateManager();
        ResourceLocation resourceLocation = new ResourceLocation(JSG.MOD_ID, structureName);
        Template template = manager.getTemplate(mcServer, resourceLocation);

        IBlockState state = worldToSpawn.getBlockState(pos);
        Biome biome = worldToSpawn.getBiome(pos);
        worldToSpawn.notifyBlockUpdate(pos, state, state, 3);
        EnumFacing facing = (findOptimalRotation ? findOptimalRotation(worldToSpawn, pos) : EnumFacing.NORTH);
        Rotation rotation = FacingToRotation.get(facing);
        rotation = rotation.add(rotationToNorth);
        template.addBlocksToWorld(worldToSpawn, pos, templateProcessor, defaultSettings.setRotation(rotation), 3);

        Map<BlockPos, String> dataBlocks = template.getDataBlocks(pos, defaultSettings);
        BlockPos lootPos;

        BlockPos gatePos = null;
        BlockPos dhdPos = null;
        StargateClassicBaseTile gateTile = null;

        boolean hasUpgrade = (dimensionToSpawn != executedInWorld.provider.getDimension());

        JSG.info("Structure " + structureName + " generated at " + pos + " in world " + worldToSpawn);

        for (BlockPos dataPos : dataBlocks.keySet()) {
            String name = dataBlocks.get(dataPos);

            switch (name) {
                // stargate
                case "base":
                    worldToSpawn.setBlockToAir(dataPos);
                    gatePos = dataPos.down(3);
                    gateTile = (StargateClassicBaseTile) worldToSpawn.getTileEntity(gatePos);
                    if (gateTile == null) break;
                    IItemHandler gateContainer = gateTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                    if (gateContainer != null) {
                        if (!isUniverseGate || JSGConfig.powerConfig.universeCapacitors > 0) {
                            // is not uni gate OR capacitors are enabled for these gates
                            ItemStack capacitor = new ItemStack(JSGBlocks.CAPACITOR_BLOCK);
                            if (isUniverseGate) {
                                // is uni gate -> add energy to capacitor (uni gate doesn't have DHD to power up itself)
                                IEnergyStorage storage = capacitor.getCapability(CapabilityEnergy.ENERGY, null);
                                if (storage != null)
                                    storage.receiveEnergy(((int) (storage.getMaxEnergyStored() * 0.5)), false);
                            }
                            gateContainer.insertItem(4, capacitor, false); // insert capacitor
                            gateContainer.insertItem(0, new ItemStack(JSGItems.CRYSTAL_GLYPH_MILKYWAY), false); // insert glyph crystal for mw gates
                            if (hasUpgrade)
                                gateContainer.insertItem(1, new ItemStack(JSGItems.CRYSTAL_GLYPH_STARGATE), false);
                        }
                        // insert power to the gate itself
                        IEnergyStorage gateEnergy = gateTile.getCapability(CapabilityEnergy.ENERGY, null);
                        if (gateEnergy != null)
                            gateEnergy.receiveEnergy(((int) (((StargateClassicEnergyStorage) gateEnergy).getMaxEnergyStoredInternally() * 0.75)), false);
                    }
                    gateTile.getMergeHelper().updateMembersBasePos(worldToSpawn, gatePos, facing);
                    break;
                case "dhd":
                    worldToSpawn.setBlockToAir(dataPos);
                    dhdPos = dataPos.down();

                    // set the DHD to the topBlock
                    JSGWorldTopBlock topBlock = JSGWorldTopBlock.getTopBlock(worldToSpawn, dhdPos.getX(), dhdPos.getZ(), 0, worldToSpawn.provider.getDimension());
                    if(topBlock != null && (topBlock.y != dhdPos.getY())){
                        IBlockState dhd = worldToSpawn.getBlockState(dhdPos);
                        worldToSpawn.setBlockState(dhdPos, topBlock.topBlockState, 3);
                        dhdPos = new BlockPos(dhdPos.getX(), (topBlock.y + 1), dhdPos.getZ());
                        worldToSpawn.setBlockState(dhdPos, dhd, 3);
                    }

                    if (random.nextFloat() < JSGConfig.mysteriousConfig.despawnDhdChance) {
                        worldToSpawn.setBlockToAir(dhdPos);
                        break;
                    }
                    DHDAbstractTile dhdTile = (DHDAbstractTile) worldToSpawn.getTileEntity(dhdPos);
                    if (dhdTile == null) break;

                    final int fluid = JSGConfig.powerConfig.stargateEnergyStorage / JSGConfig.dhdConfig.energyPerNaquadah;
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
            gateTile.getMergeHelper().updateMembersMergeStatus(worldToSpawn, gateTile.getPos(), gateTile.getFacing(), true);
            gateTile.markDirty();

            StargateAddress address = gateTile.getStargateAddress(symbolType);

            if (address != null && !gateTile.getNetwork().isStargateInNetwork(address))
                gateTile.getNetwork().addStargate(address, new StargatePos(worldToSpawn.provider.getDimensionType().getId(), gatePos, address));

            ResourceLocation biomePath = biome.getRegistryName();
            return new GeneratedStargate(address, (biomePath == null ? null : biomePath.getResourcePath()), hasUpgrade);
        }
        return null;
    }

    private static void generateLoot(World world, BlockPos chestPos, Random random, String lootTableName) {
        TileEntity tile = world.getTileEntity(chestPos);
        if (tile instanceof TileEntityChest) {
            TileEntityChest chest = (TileEntityChest) tile;
            chest.setLootTable(new ResourceLocation(JSG.MOD_ID, lootTableName), random.nextLong());
            //chest.fillWithLoot(null);
        }
    }
}
