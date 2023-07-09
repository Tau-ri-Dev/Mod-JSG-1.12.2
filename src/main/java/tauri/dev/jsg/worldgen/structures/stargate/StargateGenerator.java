package tauri.dev.jsg.worldgen.structures.stargate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.worldgen.structures.EnumStructures;
import tauri.dev.jsg.worldgen.structures.JSGStructuresGenerator;
import tauri.dev.jsg.worldgen.util.GeneratedStargate;
import tauri.dev.jsg.worldgen.util.JSGStructurePos;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Random;


/**
 * @author MrJake222
 * @editedby MineDragonCZ_
 */
public class StargateGenerator {

    /**
     * Method used to generate stargate in random position by mysterious page
     */
    public static GeneratedStargate mystPageGeneration(World pWorld, SymbolTypeEnum symbolType, int dimensionToSpawn, @Nonnull EntityPlayer playerIn) {
        Random rand = new Random();
        int tries = 0;
        WorldServer worldToSpawn = Objects.requireNonNull(pWorld.getMinecraftServer()).getWorld(dimensionToSpawn);
        EnumStructures structure = null;

        int min = JSGConfig.WorldGen.mystPage.minOverworldCoords;
        int max = JSGConfig.WorldGen.mystPage.maxOverworldCoords;

        BlockPos pPos = playerIn.getPosition();
        int x = (Math.abs(pPos.getX()) + (min + (int) (rand.nextFloat() * max)));
        int z = (Math.abs(pPos.getZ()) + (min + (int) (rand.nextFloat() * max)));

        if (pPos.getX() < 0) x *= -1;
        if (pPos.getZ() < 0) z *= -1;

        JSGStructurePos structurePos = null;
        int chunkX = x / 16;
        int chunkZ = z / 16;
        int bestCount = 0;
        do {

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
            } else if(tries > 0){
                x = (Math.abs(pPos.getX()) + (min + (int) (rand.nextFloat() * max)));
                z = (Math.abs(pPos.getZ()) + (min + (int) (rand.nextFloat() * max)));
            }


            chunkX = x / 16;
            chunkZ = z / 16;

            Chunk chunk = worldToSpawn.getChunkFromChunkCoords(chunkX, chunkZ);
            int y = chunk.getHeightValue(8, 8);
            if (y > 240)
                continue;

            String biomeName = Objects.requireNonNull(worldToSpawn.getBiome(new BlockPos(x, y, z)).getRegistryName()).getResourcePath();
            structure = EnumStructures.getStargateStructureByBiome(biomeName, symbolType, dimensionToSpawn);
            if (structure != null) {
                structurePos = JSGStructuresGenerator.checkForPlace(worldToSpawn, chunkX, chunkZ, structure, dimensionToSpawn);
            }
            if(structurePos != null && structurePos.bestAttemptPos != null)
                bestCount++;
            tries++;
        } while ((structurePos == null || structurePos.foundPos == null) && tries < 50);
        if (structure == null || structurePos == null || structurePos.foundPos == null) {
            JSG.error("(" + playerIn.getDisplayNameString() + ") StargateGenerator: Failed to find place - myst page: Tries:" + tries + "; Structure:" + (structure != null));
            JSG.error("Best places count: " + bestCount);
            return null;
        }

        return structure.getActualStructure(dimensionToSpawn).generateStructure(pWorld, structurePos.foundPos, rand, worldToSpawn);
    }

    public static GeneratedStargate mystPageGeneration(World pWorld, EnumStructures structure, int dimensionToSpawn, @Nonnull BlockPos pPos) {
        Random rand = new Random();
        int tries = 0;
        WorldServer worldToSpawn = Objects.requireNonNull(pWorld.getMinecraftServer()).getWorld(dimensionToSpawn);

        int min = JSGConfig.WorldGen.mystPage.minOverworldCoords;
        int max = JSGConfig.WorldGen.mystPage.maxOverworldCoords;

        int x = (Math.abs(pPos.getX()) + (min + (int) (rand.nextFloat() * max)));
        int z = (Math.abs(pPos.getZ()) + (min + (int) (rand.nextFloat() * max)));

        if (pPos.getX() < 0) x *= -1;
        if (pPos.getZ() < 0) z *= -1;

        JSGStructurePos structurePos = null;
        int chunkX = x / 16;
        int chunkZ = z / 16;
        int bestCount = 0;
        do {

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
            } else if(tries > 0){
                x = (Math.abs(pPos.getX()) + (min + (int) (rand.nextFloat() * max)));
                z = (Math.abs(pPos.getZ()) + (min + (int) (rand.nextFloat() * max)));
            }


            chunkX = x / 16;
            chunkZ = z / 16;

            Chunk chunk = worldToSpawn.getChunkFromChunkCoords(chunkX, chunkZ);
            int y = chunk.getHeightValue(8, 8);
            if (y > 240)
                continue;

            if (structure != null) {
                structurePos = JSGStructuresGenerator.checkForPlace(worldToSpawn, chunkX, chunkZ, structure, dimensionToSpawn);
            }
            if(structurePos != null && structurePos.bestAttemptPos != null)
                bestCount++;
            tries++;
        } while ((structurePos == null || structurePos.foundPos == null) && tries < 50);
        if (structure == null || structurePos == null || structurePos.foundPos == null) {
            JSG.error("StargateGenerator: Failed to find place - myst page: Tries:" + tries + "; Structure:" + (structure != null));
            JSG.error("Best places count: " + bestCount);
            return null;
        }

        return structure.getActualStructure(dimensionToSpawn).generateStructure(pWorld, structurePos.foundPos, rand, worldToSpawn);
    }
}
