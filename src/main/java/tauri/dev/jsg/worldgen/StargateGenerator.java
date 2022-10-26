package tauri.dev.jsg.worldgen;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.stargate.StargateSizeEnum;
import tauri.dev.jsg.fluid.JSGFluids;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDAbstractTile;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.util.LinkingHelper;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static tauri.dev.jsg.worldgen.structures.JSGStructuresGenerator.checkTopBlock;


/**
 * TODO Unify this and {@link StargateGeneratorNether} (replace this)
 *
 * @author MrJake222
 * @editedby MineDragonCZ_
 */
public class StargateGenerator {

    public static GeneratedStargate generateStargate(World world, SymbolTypeEnum symbolType, int dimensionToSpawn) {
        Random rand = new Random();

        ArrayList<Block> blocks = new ArrayList<>();
        blocks.add(Blocks.GRASS);
        blocks.add(Blocks.GRAVEL);
        blocks.add(Blocks.DIRT);
        blocks.add(Blocks.SAND);
        blocks.add(Blocks.SANDSTONE);
        blocks.add(Blocks.END_STONE);

        BlockPos pos = null;
        int tries = 0;
        WorldServer worldToSpawn = world.getMinecraftServer().getWorld(dimensionToSpawn);
        int x;
        int z;
        do {
            x = (int) (tauri.dev.jsg.config.JSGConfig.mysteriousConfig.minOverworldCoords + (rand.nextFloat() * (tauri.dev.jsg.config.JSGConfig.mysteriousConfig.maxOverworldCoords - tauri.dev.jsg.config.JSGConfig.mysteriousConfig.minOverworldCoords))) * (rand.nextBoolean() ? -1 : 1);
            z = (int) (JSGConfig.mysteriousConfig.minOverworldCoords + (rand.nextFloat() * (tauri.dev.jsg.config.JSGConfig.mysteriousConfig.maxOverworldCoords - tauri.dev.jsg.config.JSGConfig.mysteriousConfig.minOverworldCoords))) * (rand.nextBoolean() ? -1 : 1);

            if(checkTopBlock(worldToSpawn, x, z, blocks)) {
                pos = StargateGenerator.checkForPlace(worldToSpawn, x / 16, z / 16);
            }
            tries++;
        } while (pos == null && tries < 100);
        if (tries == 100) {
            JSG.logger.debug("StargateGenerator: Failed to find place - normal gate");
            return null;
        }
        if (pos == null) {
            JSG.logger.debug("StargateGenerator: Pos is null - normal gate");
            return null;
        }

        Biome biome = world.getBiome(pos);
        boolean desert = biome.getRegistryName().getResourcePath().contains("desert");
        String templateName = "sg_";
        templateName += desert ? "desert" : "plains";
        templateName += tauri.dev.jsg.config.JSGConfig.stargateSize == StargateSizeEnum.LARGE ? "_large" : "_small";

        switch (symbolType) {
            case PEGASUS:
                templateName += "_peg";
                break;
            case UNIVERSE:
                templateName += "_uni";
                break;
            default:
                break;
        }
        return generateStargate(world, pos, templateName, symbolType, dimensionToSpawn);
    }

    public static GeneratedStargate generateStargate(World world, BlockPos pos, String templateName, SymbolTypeEnum symbolType, int dimToSpawn) {
        final boolean isPegasusGate = symbolType == SymbolTypeEnum.PEGASUS;
        final boolean isMilkyWayGate = symbolType == SymbolTypeEnum.MILKYWAY;
        final boolean isUniverseGate = symbolType == SymbolTypeEnum.UNIVERSE;

        WorldServer worldServer = Objects.requireNonNull(world.getMinecraftServer()).getWorld(dimToSpawn);

        EnumFacing facing = findOptimalRotation(worldServer, pos);
        Rotation rotation;

        switch (facing) {
            case SOUTH:
                rotation = Rotation.CLOCKWISE_90;
                break;
            case WEST:
                rotation = Rotation.CLOCKWISE_180;
                break;
            case NORTH:
                rotation = Rotation.COUNTERCLOCKWISE_90;
                break;
            default:
                rotation = Rotation.NONE;
                break;
        }

        MinecraftServer server = worldServer.getMinecraftServer();
        Biome biome = worldServer.getBiome(pos);
        TemplateManager templateManager = worldServer.getStructureTemplateManager();
        Template template = templateManager.getTemplate(server, new ResourceLocation(JSG.MOD_ID, templateName));

        if (template != null) {
            Random rand = new Random();

            PlacementSettings settings = new PlacementSettings().setIgnoreStructureBlock(false).setRotation(rotation);
            template.addBlocksToWorld(worldServer, pos, settings);

            Map<BlockPos, String> datablocks = template.getDataBlocks(pos, settings);
            BlockPos gatePos = null;
            BlockPos dhdPos = null;
            System.out.println("name: " + templateName);

            for (BlockPos dataPos : datablocks.keySet()) {
                String name = datablocks.get(dataPos);

                if (name.equals("base")) {
                    gatePos = dataPos.add(0, -3, 0);
                    System.out.println("tile: " + (worldServer.getTileEntity(gatePos) == null ? "null" : "yes"));

                    if (!isUniverseGate || JSGConfig.powerConfig.universeCapacitors > 0)
                        worldServer.getTileEntity(gatePos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).insertItem(4, new ItemStack(JSGBlocks.CAPACITOR_BLOCK), false);

                    ((StargateAbstractBaseTile) worldServer.getTileEntity(gatePos)).getMergeHelper().updateMembersBasePos(worldServer, gatePos, facing);

                    worldServer.setBlockToAir(dataPos);
                    worldServer.setBlockToAir(dataPos.down()); // save block
                } else if (name.equals("dhd")) {
                    dhdPos = dataPos.down();

                    if (isUniverseGate || rand.nextFloat() < tauri.dev.jsg.config.JSGConfig.mysteriousConfig.despawnDhdChance) {
                        worldServer.setBlockToAir(dhdPos);
                    } else {
                        int fluid = tauri.dev.jsg.config.JSGConfig.powerConfig.stargateEnergyStorage / tauri.dev.jsg.config.JSGConfig.dhdConfig.energyPerNaquadah;

                        DHDAbstractTile dhdTile = (DHDAbstractTile) worldServer.getTileEntity(dhdPos);

                        ItemStack crystal = new ItemStack(isPegasusGate ? JSGItems.CRYSTAL_CONTROL_PEGASUS_DHD : JSGItems.CRYSTAL_CONTROL_MILKYWAY_DHD);

                        dhdTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).insertItem(0, crystal, false);
                        if (!isMilkyWayGate)
                            dhdTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).insertItem(1, new ItemStack(JSGItems.CRYSTAL_GLYPH_DHD), false);
                        ((FluidTank) dhdTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)).fillInternal(new FluidStack(JSGFluids.moltenNaquadahRefined, fluid), true);
                    }

                    worldServer.setBlockToAir(dataPos);
                }
            }

            LinkingHelper.updateLinkedGate(worldServer, gatePos, dhdPos);
            StargateClassicBaseTile gateTile = (StargateClassicBaseTile) worldServer.getTileEntity(gatePos);
            gateTile.refresh();
            gateTile.getMergeHelper().updateMembersMergeStatus(worldServer, gateTile.getPos(), gateTile.getFacing(), true);
            gateTile.markDirty();

            StargateAddress address = gateTile.getStargateAddress(symbolType);

            if (address != null && !gateTile.getNetwork().isStargateInNetwork(address))
                gateTile.getNetwork().addStargate(address, new StargatePos(worldServer.provider.getDimensionType().getId(), gatePos, address));

            return new GeneratedStargate(address, biome.getRegistryName().getResourcePath(), dimToSpawn != world.provider.getDimension());
        } else {
            JSG.logger.error("template null");
        }

        return null;

    }

    private static final int SG_SIZE_X = 15;
    private static final int SG_SIZE_Z = 15;

    private static final int SG_SIZE_X_PLAINS = 15;
    private static final int SG_SIZE_Z_PLAINS = 15;

    public static BlockPos checkForPlace(World world, int chunkX, int chunkZ) {
        if (world.isChunkGeneratedAt(chunkX, chunkZ))
            return null;

        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        int y = chunk.getHeightValue(8, 8);

        if (y > 240)
            return null;

        BlockPos pos = new BlockPos(chunkX * 16, y, chunkZ * 16);
        String biomeName = chunk.getBiome(pos, world.getBiomeProvider()).getRegistryName().getResourcePath();

        boolean desert = biomeName.contains("desert");

        if (!biomeName.contains("ocean") && !biomeName.contains("river") && !biomeName.contains("beach")) {
            int x = desert ? SG_SIZE_X : SG_SIZE_X_PLAINS;
            int z = desert ? SG_SIZE_Z : SG_SIZE_Z_PLAINS;

            int y1 = chunk.getHeightValue(0, 0);
            int y2 = chunk.getHeightValue(x, z);

            int y3 = chunk.getHeightValue(x, 0);
            int y4 = chunk.getHeightValue(0, z);

            // No steep hill
            if (Math.abs(y1 - y2) <= 2 && Math.abs(y3 - y4) <= 2) {
                return pos.subtract(new BlockPos(0, 1, 0));
            } else {
                JSG.logger.debug("StargateGenerator: too steep");
            }
        } else {
            JSG.logger.debug("StargateGenerator: failed, " + biomeName);
        }

        return null;
    }

    private static final int MAX_CHECK = 100;

    private static EnumFacing findOptimalRotation(World world, BlockPos pos) {
        BlockPos start = pos.add(0, 5, 5);
        int max = -1;
        EnumFacing maxFacing = EnumFacing.EAST;

        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            RayTraceResult rayTraceResult = world.rayTraceBlocks(new Vec3d(start), new Vec3d(start.offset(facing, MAX_CHECK)));

            if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                int distance = (int) rayTraceResult.getBlockPos().distanceSq(start);
//				JSG.info(facing.getName().toUpperCase() + ": distance: " + distance);

                if (distance > max) {
                    max = distance;
                    maxFacing = facing;
                }
            } else {
//				JSG.info(facing.getName().toUpperCase() + ": null");

                max = 100000;
                maxFacing = facing;
            }
        }

//		JSG.info("maxFacing: " + maxFacing.getName().toUpperCase());
        return maxFacing;
    }

    public static class GeneratedStargate {

        public StargateAddress address;
        public String path;
        public boolean hasUpgrade;

        public GeneratedStargate(StargateAddress address, String path, boolean upgrade) {
            this.address = address;
            this.path = path;
            this.hasUpgrade = upgrade;
        }

    }
}
