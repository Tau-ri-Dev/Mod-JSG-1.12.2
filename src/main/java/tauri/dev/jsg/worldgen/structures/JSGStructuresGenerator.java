package tauri.dev.jsg.worldgen.structures;

import net.minecraft.block.Block;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.util.FacingToRotation;
import tauri.dev.jsg.worldgen.structures.stargate.GeneratedStargate;
import tauri.dev.jsg.worldgen.structures.stargate.StargateGenerationHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class JSGStructuresGenerator implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        for (EnumStructures structure : EnumStructures.values()) {
            if (structure.structure.dimensionToSpawn == world.provider.getDimension()) {
                if (structure.randomGenEnable) {
                    JSGStructuresGenerator.generateStructure(structure, world, random, chunkX, chunkZ, false, false);
                }
            }
        }
    }

    public static GeneratedStargate generateStructure(EnumStructures structure, World world, Random random, int chunkX, int chunkZ, boolean ignoreGeneratedChunks, boolean notRandomGen) {
        if (notRandomGen || (structure.chance > 0 && (random.nextFloat() < (structure.chance / 10000)))) {
            BlockPos pos = new BlockPos((chunkX * 16) + random.nextInt(15), 10, (chunkZ * 16) + random.nextInt(15));

            Random rand = new Random();
            BlockPos newPos = null;
            int tries = 0;
            int x;
            int z;
            do {
                x = (int) (pos.getX() + (rand.nextFloat() * (2000)) * (rand.nextBoolean() ? -1 : 1));
                z = (int) (pos.getZ() + (rand.nextFloat() * (2000)) * (rand.nextBoolean() ? -1 : 1));

                if (!ignoreGeneratedChunks && world.isChunkGeneratedAt(x / 16, z / 16))
                    continue;

                newPos = checkForPlace(world, x / 16, z / 16, structure.structure.structureSizeX, structure.structure.structureSizeZ, structure.structure.dimensionToSpawn);
                if (newPos != null) {
                    if (!checkTopBlock(world, newPos.getX(), newPos.getZ(), structure, structure.structure.dimensionToSpawn)) {
                        newPos = null;
                    }
                }
                tries++;
            } while (newPos == null && tries < 100);

            if (newPos != null && newPos.getY() > 0) {
                String biome = world.provider.getBiomeForCoords(newPos).getBiomeName();
                if (world.getWorldType() != WorldType.FLAT) {
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
                        (!structure.structure.isStargateStructure && JSGConfig.stargateGeneratorConfig.structuresRandomGeneratorEnabled)){
                            return structure.structure.generateStructure(world, newPos, random, null);
                        }
                    }
                }
            }
            else{
                JSG.error("Can not generate structure " + structure.structure.structureName + " in " + world + " at " + newPos + "(old: " + pos + ")");
            }
        }
        return null;
    }

    public static BlockPos checkForPlace(World world, int chunkX, int chunkZ, int structureSizeX, int structureSizeZ, int dimensionId) {
        if (dimensionId == -1) return checkForPlaceNether(world, chunkX, chunkZ, structureSizeX, structureSizeZ);

        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        int y = chunk.getHeightValue(8, 8);

        if (y > 240)
            return null;

        BlockPos pos = new BlockPos(chunkX * 16, y, chunkZ * 16);

        int y1 = chunk.getHeightValue(0, 0);
        int y2 = chunk.getHeightValue(structureSizeX, structureSizeZ);

        int y3 = chunk.getHeightValue(structureSizeX, 0);
        int y4 = chunk.getHeightValue(0, structureSizeZ);

        // No steep hill
        if (Math.abs(y1 - y2) <= 1 && Math.abs(y3 - y4) <= 1) {
            return pos.subtract(new BlockPos(0, 1, 0));
        }
        return null;
    }

    /**
     * @author MrJake222
     */
    public static BlockPos checkForPlaceNether(World world, int chunkX, int chunkZ, int structureSizeX, int structureSizeZ) {
        final List<StargateGenerationHelper.Direction> DIRECTIONS = Arrays.asList(
                new StargateGenerationHelper.Direction(EnumFacing.UP, false, 0).setRequiredMinimum(12).setIgnoreInMaximum(),
                new StargateGenerationHelper.Direction(EnumFacing.NORTH, true, 2),
                new StargateGenerationHelper.Direction(EnumFacing.SOUTH, true, 2),
                new StargateGenerationHelper.Direction(EnumFacing.WEST, true, 2),
                new StargateGenerationHelper.Direction(EnumFacing.EAST, true, 2));
        final List<BlockMatcher> ALLOWED_BLOCKS_BELOW = Arrays.asList(
                BlockMatcher.forBlock(Blocks.NETHERRACK),
                BlockMatcher.forBlock(Blocks.QUARTZ_ORE),
                BlockMatcher.forBlock(Blocks.SOUL_SAND));

        final int MINIMAL_SIDE_SPACE = structureSizeX/2;

        BlockPos posIn = new BlockPos(chunkX * 16, 32, chunkZ * 16);
        BlockPos start = posIn;
        BlockPos current = start;
        int count = 0;
        int pass = 1;

        BlockPos found = null;
        Map.Entry<EnumFacing, StargateGenerationHelper.DirectionResult> frontResult = null;

        while (found == null) {
            JSG.logger.info("StargateGeneratorNether: count: " + count + ", pass: " + pass + ", current: " + current);

            for (BlockPos.MutableBlockPos pos : BlockPos.MutableBlockPos.getAllInBoxMutable(current, current.add(16, 16, 16))) {
                if (world.isAirBlock(pos.down()))
                    continue;

                StargateGenerationHelper.FreeSpace freeSpace = StargateGenerationHelper.getFreeSpaceInDirections(world, pos, DIRECTIONS, 16, ALLOWED_BLOCKS_BELOW);

                //TODO(Mine): Fix this null (every loop is null)
                if (freeSpace != null) {
                    if (freeSpace.getMaxDistance().getValue().distance >= structureSizeZ) {
                        int left = freeSpace.getDistance(freeSpace.getMaxDistance().getKey().rotateYCCW()).distance;
                        int right = freeSpace.getDistance(freeSpace.getMaxDistance().getKey().rotateY()).distance;

                        if (left >= MINIMAL_SIDE_SPACE && right >= MINIMAL_SIDE_SPACE) {
                            found = pos.toImmutable();
                            frontResult = freeSpace.getMaxDistance();

                            break;
                        }
                    }
                }
            }

            if (count == 0)
                current = start.add(16, 0, 0);
            else if (count == 1)
                current = start.add(0, 0, 16);
            else if (count == 2)
                current = start.add(16, 0, 16);
            else if (count == 3) {
                if (start.getY() > 100) {
                    start = posIn.add(32*pass, 0, 32*pass);
                    pass++;
                }

                else {
                    start = start.add(0, 16, 0);
                }

                current = start;
            }

            count++;

            if (count > 3)
                count = 0;
        }
        int y = Math.min(frontResult.getValue().ydiff, 0);
        y -= 2;

        BlockPos translate = new BlockPos(-7, y, -16).rotate(FacingToRotation.get(frontResult.getKey()));
        return found.add(translate);
    }

    public static boolean checkTopBlock(World world, int x, int z, EnumStructures structure, int dimensionId) {
        if (dimensionId == -1) return true;
        int y = world.getHeight();

        for (int i = 0; i < 4; i++) {
            int xn = ((i < 2) ? x : (x + structure.structure.structureSizeX));
            int zn = ((i % 2 == 0) ? z : (z + structure.structure.structureSizeZ));

            boolean found = false;
            while (y > 0 && !found) {
                Block block = world.getBlockState(new BlockPos(xn, y, zn)).getBlock();
                found = (structure.allowedOnBlocks == null || structure.allowedOnBlocks.contains(block));
                if (block != Blocks.AIR && !found) {
                    return false;
                }
                y--;
            }
        }
        return true;
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
