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
        BlockPos pos = null;
        int tries = 0;
        WorldServer worldToSpawn = Objects.requireNonNull(pWorld.getMinecraftServer()).getWorld(dimensionToSpawn);
        EnumStructures structure = null;

        int min = tauri.dev.jsg.config.JSGConfig.mysteriousConfig.minOverworldCoords;
        int max = tauri.dev.jsg.config.JSGConfig.mysteriousConfig.maxOverworldCoords;

        BlockPos pPos = playerIn.getPosition();
        int x = (Math.abs(pPos.getX()) + (min + (int) (rand.nextFloat() * max)));
        int z = (Math.abs(pPos.getZ()) + (min + (int) (rand.nextFloat() * max)));

        if(pPos.getX() < 0) x *= -1;
        if(pPos.getZ() < 0) z *= -1;
        do {

            if(tries % 2 == 0){
                x += (16 * (x < 0 ? -1 : 1));
                z += (-16 * (z < 0 ? -1 : 1));
            }
            else
                z += 16 * (z < 0 ? -1 : 1);

            int chunkX = x / 16;
            int chunkZ = z / 16;
            worldToSpawn.getChunkProvider().loadChunk(chunkX, chunkZ);

            Chunk chunk = worldToSpawn.getChunkFromChunkCoords(chunkX, chunkZ);
            int y = chunk.getHeightValue(8, 8);
            if (y > 240)
                continue;

            String biomeName = Objects.requireNonNull(worldToSpawn.getBiome(new BlockPos(x, y, z)).getRegistryName()).getResourcePath();
            structure = EnumStructures.getStargateStructureByBiome(biomeName, symbolType, dimensionToSpawn);
            if (structure != null) {
                pos = JSGStructuresGenerator.checkForPlace(worldToSpawn, chunkX, chunkZ, structure, dimensionToSpawn);
            }
            tries++;
        } while (pos == null && tries < 50);
        if (structure == null || pos == null) {
            JSG.error("(" + playerIn.getDisplayNameString() + ") StargateGenerator: Failed to find place - myst page: Tries:" + tries + "; Structure:" + (structure != null));
            return null;
        }

        return structure.structure.generateStructure(pWorld, pos, rand, worldToSpawn);
    }
}
