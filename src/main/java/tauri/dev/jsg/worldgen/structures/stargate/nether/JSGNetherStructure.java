package tauri.dev.jsg.worldgen.structures.stargate.nether;

import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.worldgen.structures.JSGStructure;
import tauri.dev.jsg.worldgen.util.EnumGenerationHeight;
import tauri.dev.jsg.worldgen.util.GeneratedStargate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Handles generating a structure in the Nether.
 *
 * @author MrJake222
 * @editedby MineDragonCZ_
 */
public class JSGNetherStructure extends JSGStructure {

    /**
     * Directions check definitions
     */
    private final List<StargateGenerationHelper.Direction> DIRECTIONS;

    /**
     * Allowed blocks
     */
    private final List<BlockMatcher> ALLOWED_BLOCKS_BELOW;

    private static final int MINIMAL_FRONT_SPACE = 16;
    private static final int MINIMAL_SIDE_SPACE = 5;

    public JSGNetherStructure(String structureName, int yNegativeOffset, boolean isStargateStructure, boolean isRingsStructure, SymbolTypeEnum symbolType, int structureSizeX, int structureSizeZ, int airUp, int dimensionToSpawn, boolean findOptimalRotation, @Nullable ITemplateProcessor templateProcessor, Rotation rotationToNorth, double terrainFlatPercents, double topBlockMatchPercent, @Nonnull EnumGenerationHeight genHeight) {
        super(structureName, yNegativeOffset, isStargateStructure, isRingsStructure, symbolType, structureSizeX, structureSizeZ, airUp, dimensionToSpawn, findOptimalRotation, templateProcessor, rotationToNorth, terrainFlatPercents, topBlockMatchPercent, genHeight);
        DIRECTIONS = Arrays.asList(
                new StargateGenerationHelper.Direction(EnumFacing.UP, false, 0).setRequiredMinimum(this.airUp).setIgnoreInMaximum(),
                new StargateGenerationHelper.Direction(EnumFacing.NORTH, true, 2),
                new StargateGenerationHelper.Direction(EnumFacing.SOUTH, true, 2),
                new StargateGenerationHelper.Direction(EnumFacing.WEST, true, 2),
                new StargateGenerationHelper.Direction(EnumFacing.EAST, true, 2));
        ALLOWED_BLOCKS_BELOW = Arrays.asList(
                BlockMatcher.forBlock(Blocks.NETHERRACK),
                BlockMatcher.forBlock(Blocks.QUARTZ_ORE),
                BlockMatcher.forBlock(Blocks.SOUL_SAND));
    }

    @Override
    public boolean canDHDDespawn(){
        return false;
    }

    /**
     * Searches for a place and spawns the structure.
     */
    @Override
    public GeneratedStargate generateStructure(World executedInWorld, BlockPos posIn, Random random, @Nullable WorldServer worldToSpawn) {
        MinecraftServer mcServer = executedInWorld.getMinecraftServer();
        JSG.info("Nether Structure " + structureName + " generation started!");
        if (mcServer == null) return null;
        worldToSpawn = (worldToSpawn == null ? mcServer.getWorld(dimensionToSpawn) : worldToSpawn);

        BlockPos start = posIn;
        BlockPos current = start;
        int count = 0;
        int pass = 1;

        BlockPos found = null;
        Map.Entry<EnumFacing, StargateGenerationHelper.DirectionResult> frontResult = null;

        while (found == null) {
            //JSG.debug("JSGNetherStructure: count: " + count + ", pass: " + pass + ", current: " + current);

            for (MutableBlockPos pos : MutableBlockPos.getAllInBoxMutable(current, current.add(16, 16, 16))) {
                if (worldToSpawn.isAirBlock(pos.down()))
                    continue;

                StargateGenerationHelper.FreeSpace freeSpace = StargateGenerationHelper.getFreeSpaceInDirections(worldToSpawn, pos, DIRECTIONS, 16, ALLOWED_BLOCKS_BELOW);

                if (freeSpace != null) {
                    if (freeSpace.getMaxDistance().getValue().distance >= MINIMAL_FRONT_SPACE) {
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
                    start = posIn.add(32 * pass, 0, 32 * pass);
                    pass++;
                } else {
                    start = start.add(0, 16, 0);
                }

                current = start;
            }

            count++;

            if (count > 3)
                count = 0;
        }

        //JSG.debug("JSGNetherStructure: /tp " + found.getX() + " " + found.getY() + " " + found.getZ());

        Rotation rotation;

        switch (frontResult.getKey()) {
            case SOUTH:
                rotation = Rotation.CLOCKWISE_180;
                break;
            case WEST:
                rotation = Rotation.COUNTERCLOCKWISE_90;
                break;
            case EAST:
                rotation = Rotation.CLOCKWISE_90;
                break;
            default:
                rotation = Rotation.NONE;
                break;
        }

        int y = Math.min(frontResult.getValue().ydiff, 0);
        y -= 2;

        BlockPos translate = new BlockPos(-7, y, -16).rotate(rotation);
        found = found.add(translate);
        return super.generateStructure(executedInWorld, found, random, worldToSpawn, rotation);
    }
}
