package tauri.dev.jsg.worldgen.structures;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.util.FacingToRotation;
import tauri.dev.jsg.worldgen.structures.stargate.nether.JSGNetherStructure;
import tauri.dev.jsg.worldgen.util.GeneratedStargate;
import tauri.dev.jsg.worldgen.util.JSGStructurePos;
import tauri.dev.jsg.worldgen.util.JSGWorldTopBlock;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import static tauri.dev.jsg.worldgen.util.JSGWorldTopBlock.getTopBlock;

/**
 * @author MrJake222
 */
public class JSGStructuresGenerator implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        for (EnumStructures structure : EnumStructures.values()) {
            if(world.getWorldType() == WorldType.FLAT) return;
            if (structure.getActualStructure(world.provider.getDimension()).dimensionToSpawn == world.provider.getDimension()) {
                if (structure.randomGeneratorEnabled()) {
                    if (structure.getChance() > 0 && (random.nextFloat() < structure.getChance())) {
                        JSGStructuresGenerator.generateStructure(structure, world, random, chunkX, chunkZ, false);
                        return; // - if not -> causing cascading worldgen lag
                    }
                }
            }
        }
    }

    public static GeneratedStargate generateStructure(EnumStructures structure, World world, Random random, int chunkX, int chunkZ, boolean notRandomGen) {
        return generateStructure(structure, world, random, chunkX, chunkZ, notRandomGen, true, structure.getActualStructure(0).dimensionToSpawn);
    }
    public static GeneratedStargate generateStructure(EnumStructures structure, World world, Random random, int chunkX, int chunkZ, boolean notRandomGen, boolean notCommandGen, int dimId) {
        WorldServer worldToSpawn = Objects.requireNonNull(world.getMinecraftServer()).getWorld(dimId);

        if(structure.getActualStructure(dimId) instanceof JSGNetherStructure){
            return structure.getActualStructure(dimId).generateStructure(world, new BlockPos((chunkX * 16), 32, (chunkZ * 16)), random, worldToSpawn);
        }

        int x = (chunkX * 16) + (notCommandGen ? random.nextInt(15) : 0);
        int z = (chunkZ * 16) + (notCommandGen ? random.nextInt(15) : 0);
        JSGStructurePos structurePos = checkForPlace(worldToSpawn, chunkX, chunkZ, structure, dimId);
        if (notRandomGen && notCommandGen) {
            int tries = 0;
            while ((structurePos == null || structurePos.foundPos == null) && tries < 50) {
                if (structurePos != null && structurePos.bestAttemptPos != null) {
                    x = structurePos.bestAttemptPos.getX();
                    z = structurePos.bestAttemptPos.getZ();

                    if (x / 16 == chunkX) {
                        if (tries % 2 == 0)
                            x += (16 * (x < 0 ? 1 : -1));
                    }
                    if (z / 16 == chunkZ) {
                        if (tries % 2 == 0)
                            z += (-16 * (z < 0 ? 1 : -1));
                        else
                            z += 16 * (z < 0 ? 1 : -1);
                    }
                } else if (tries > 0) {
                    x += (random.nextInt(15) * 16) * (random.nextBoolean() ? -1 : 1);
                    z += (random.nextInt(15) * 16) * (random.nextBoolean() ? -1 : 1);
                }

                chunkX = x / 16;
                chunkZ = z / 16;

                structurePos = JSGStructuresGenerator.checkForPlace(worldToSpawn, chunkX, chunkZ, structure, dimId);
                tries++;
            }
        }

        if (structurePos != null && structurePos.foundPos != null && structurePos.foundPos.getY() > 0) {
            String biome = Objects.requireNonNull(worldToSpawn.getBiome(structurePos.foundPos).getRegistryName()).getResourcePath();
            if (worldToSpawn.getWorldType() != WorldType.FLAT || !notRandomGen) {
                boolean contains = (structure.allowedInBiomes == null);
                if (!contains) {
                    for (String s : structure.allowedInBiomes) {
                        if (biome.contains(s)) {
                            contains = true;
                            break;
                        }
                    }
                }
                if (contains) {
                    if (notRandomGen || (structure.getActualStructure(dimId).isStargateStructure && JSGConfig.WorldGen.structures.stargateRandomGeneratorEnabled) ||
                            (!structure.getActualStructure(dimId).isStargateStructure && JSGConfig.WorldGen.structures.structuresRandomGeneratorEnabled)) {
                        return structure.getActualStructure(dimId).generateStructure(world, structurePos.foundPos, random, worldToSpawn);
                    }
                }
            }
        } else if(notRandomGen){
            JSG.error("Can not generate structure " + structure.getActualStructure(dimId).structureName + "; StPos: " + structurePos);
        }
        return null;
    }

    public static JSGStructurePos checkForPlace(World world, int chunkX, int chunkZ, EnumStructures structure, int dimensionId) {
        if (!world.getChunkProvider().isChunkGeneratedAt(chunkX, chunkZ)) {
            world.getChunkProvider().provideChunk(chunkX, chunkZ);
        }
        int x = chunkX * 16;
        int z = chunkZ * 16;

        int structureSizeX = structure.getActualStructure(dimensionId).structureSizeX;
        int structureSizeZ = structure.getActualStructure(dimensionId).structureSizeZ;


        int lowestY = 100;
        int highestY = 0;

        BlockPos pos = new BlockPos(x, 50, z);
        ArrayList<BlockPos> bestPositions = new ArrayList<>();
        int topBlocksOk = 0;
        Rotation rotation = FacingToRotation.get(findOptimalRotation(world, pos));
        for (int xx = -1; xx <= (structureSizeX + 1); xx++) {
            for (int zz = -1; zz <= (structureSizeZ + 1); zz++) {
                BlockPos newPos = pos.add((new BlockPos(xx, 0, zz).rotate(rotation)));
                if (world.getChunkProvider().isChunkGeneratedAt(newPos.getX() / 16, newPos.getZ() / 16)) {
                    JSGWorldTopBlock topBlock = getTopBlock(world, newPos.getX(), newPos.getZ(), structure.getActualStructure(dimensionId).airUp, dimensionId);
                    if (topBlock != null) {
                        if (topBlock.y < lowestY) lowestY = topBlock.y;
                        if (topBlock.y > highestY) highestY = topBlock.y;
                        int step = Math.abs(topBlock.y - lowestY);
                        if (structure.allowedOnBlocks == null || structure.allowedOnBlocks.contains(topBlock.topBlock)) {
                            topBlocksOk++;
                            if (step <= 4) {
                                bestPositions.add(newPos);
                            } else if (step >= 15)
                                return null;
                        }
                    }
                } else
                    world.getChunkProvider().provideChunk(newPos.getX() / 16, newPos.getZ() / 16);
            }
        }

        int bestXSuma = 0;
        int bestYSuma = 0;
        int bestZSuma = 0;
        for (BlockPos bestPos : bestPositions) {
            bestXSuma += bestPos.getX();
            bestYSuma += bestPos.getY();
            bestZSuma += bestPos.getZ();
        }
        BlockPos bestAttemptPos = null;
        if (bestPositions.size() > 0) {
            int bestX = bestXSuma / bestPositions.size();
            int bestY = bestYSuma / bestPositions.size();
            int bestZ = bestZSuma / bestPositions.size();

            bestAttemptPos = new BlockPos(bestX, bestY, bestZ);
        }

        double successPercent = (bestPositions.size() / ((double) (structureSizeX * structureSizeZ)));

        if (bestAttemptPos != null && (successPercent < structure.getActualStructure(dimensionId).terrainFlatPercents))
            return new JSGStructurePos(null, bestAttemptPos);
        if (successPercent >= structure.getActualStructure(dimensionId).terrainFlatPercents && ((topBlocksOk / (double) (structureSizeX * structureSizeZ)) >= structure.getActualStructure(dimensionId).topBlockMatchPercent))
            return new JSGStructurePos(new BlockPos(x, structure.getActualStructure(dimensionId).genHeight.getHeight(lowestY, highestY), z), null);
        return null;
    }

    public static EnumFacing findOptimalRotation(World world, BlockPos pos) {
        final int MAX_CHECK = 100;

        BlockPos start = pos.add(0, 5, 5);
        int max = -1;
        EnumFacing maxFacing = EnumFacing.EAST;

        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            RayTraceResult rayTraceResult = world.rayTraceBlocks(new Vec3d(start), new Vec3d(start.offset(facing, MAX_CHECK)));

            if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                int distance = (int) rayTraceResult.getBlockPos().distanceSq(start);
                if (distance > max) {
                    max = distance;
                    maxFacing = facing;
                }
            } else {
                max = 100000;
                maxFacing = facing;
            }
        }

        return maxFacing;
    }
}
