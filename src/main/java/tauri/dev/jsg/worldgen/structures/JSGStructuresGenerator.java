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
import tauri.dev.jsg.worldgen.structures.stargate.GeneratedStargate;

import java.util.Objects;
import java.util.Random;

import static tauri.dev.jsg.worldgen.structures.JSGWorldTopBlock.getTopBlock;

public class JSGStructuresGenerator implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        for (EnumStructures structure : EnumStructures.values()) {
            if (structure.structure.dimensionToSpawn == world.provider.getDimension()) {
                if (structure.randomGenEnable) {
                    if(!chunkProvider.isChunkGeneratedAt(chunkX, chunkZ)) return;
                    if(!chunkProvider.isChunkGeneratedAt(chunkX, chunkZ+1)) return;
                    if(!chunkProvider.isChunkGeneratedAt(chunkX, chunkZ-1)) return;
                    if(!chunkProvider.isChunkGeneratedAt(chunkX+1, chunkZ)) return;
                    if(!chunkProvider.isChunkGeneratedAt(chunkX-1, chunkZ)) return;
                    if(!chunkProvider.isChunkGeneratedAt(chunkX+1, chunkZ+1)) return;
                    if(!chunkProvider.isChunkGeneratedAt(chunkX-1, chunkZ-1)) return;
                    if(!chunkProvider.isChunkGeneratedAt(chunkX+1, chunkZ-1)) return;
                    if(!chunkProvider.isChunkGeneratedAt(chunkX-1, chunkZ+1)) return;
                    JSGStructuresGenerator.generateStructure(structure, world, random, chunkX, chunkZ, false);
                    return;
                }
            }
        }
    }

    public static GeneratedStargate generateStructure(EnumStructures structure, World world, Random random, int chunkX, int chunkZ, boolean notRandomGen) {
        if (notRandomGen || (structure.chance > 0 && (random.nextFloat() < structure.chance))) {
            WorldServer worldToSpawn = Objects.requireNonNull(world.getMinecraftServer()).getWorld(structure.structure.dimensionToSpawn);

            BlockPos pos = new BlockPos((chunkX * 16) + random.nextInt(15), 10, (chunkZ * 16) + random.nextInt(15));
            BlockPos newPos = checkForPlace(worldToSpawn, chunkX, chunkZ, structure, structure.structure.dimensionToSpawn);
            if(notRandomGen) {
                int tries = 0;
                int x = pos.getX();
                int z = pos.getZ();
                do {
                    if (tries % 2 == 0) {
                        x += (16 * (x < 0 ? -1 : 1));
                        z += (-16 * (z < 0 ? -1 : 1));
                    } else
                        z += 16 * (z < 0 ? -1 : 1);

                    int cX = x / 16;
                    int cZ = z / 16;

                    newPos = checkForPlace(worldToSpawn, cX, cZ, structure, structure.structure.dimensionToSpawn);
                    tries++;
                } while (newPos == null && tries < 50);
            }

            if (newPos != null && newPos.getY() > 0) {
                String biome = Objects.requireNonNull(worldToSpawn.getBiome(newPos).getRegistryName()).getResourcePath();
                if (worldToSpawn.getWorldType() != WorldType.FLAT) {
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
                        if (notRandomGen || (structure.structure.isStargateStructure && JSGConfig.stargateGeneratorConfig.stargateRandomGeneratorEnabled) ||
                                (!structure.structure.isStargateStructure && JSGConfig.stargateGeneratorConfig.structuresRandomGeneratorEnabled)) {
                            return structure.structure.generateStructure(world, newPos, random, worldToSpawn);
                        }
                    }
                }
            } else {
                JSG.error("Can not generate structure " + structure.structure.structureName + " in " + world + " at " + newPos + "(old: " + pos + ")");
            }
        }
        return null;
    }

    public static BlockPos checkForPlace(World world, int chunkX, int chunkZ, EnumStructures structure, int dimensionId) {
        int x = chunkX * 16;
        int z = chunkZ * 16;

        int structureSizeX = structure.structure.structureSizeX;
        int structureSizeZ = structure.structure.structureSizeZ;


        // TODO(Mine): Fix cascading worldgen lag issue here!
        JSGWorldTopBlock topBlockOrigin = getTopBlock(world, x, z, structure.airCountUp, dimensionId);
        if (topBlockOrigin == null) return null;
        int y = topBlockOrigin.y;
        int lowestY = y;
        int highestY = y;
        BlockPos pos = new BlockPos(x, y, z);
        Rotation rotation = FacingToRotation.get(findOptimalRotation(world, pos));
        for (int xx = -1; xx <= (structureSizeX + 1); xx++) {
            for (int zz = -1; zz <= (structureSizeZ + 1); zz++) {
                BlockPos newPos = pos.add((new BlockPos(xx, 0, zz).rotate(rotation)));
                JSGWorldTopBlock topBlock = getTopBlock(world, newPos.getX(), newPos.getZ(), structure.airCountUp, dimensionId);
                if (topBlock == null) return null;
                if (Math.abs(topBlock.y - y) > 2) return null;
                if (structure.allowedOnBlocks != null) {
                    if (!(structure.allowedOnBlocks.contains(topBlock.topBlock)))
                        return null;
                }
                if (topBlock.y < lowestY) lowestY = topBlock.y;
                if (topBlock.y > highestY) highestY = topBlock.y;
            }
        }
        return new BlockPos(chunkX * 16, lowestY, chunkZ * 16);
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
