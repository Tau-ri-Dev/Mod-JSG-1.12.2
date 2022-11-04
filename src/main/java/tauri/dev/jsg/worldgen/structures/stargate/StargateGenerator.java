package tauri.dev.jsg.worldgen.structures.stargate;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.worldgen.structures.EnumStructures;
import tauri.dev.jsg.worldgen.structures.JSGStructure;
import tauri.dev.jsg.worldgen.structures.JSGStructuresGenerator;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import static tauri.dev.jsg.worldgen.structures.JSGStructuresGenerator.checkTopBlock;


/**
 * @author MrJake222
 * @editedby MineDragonCZ_
 */
public class StargateGenerator {

    /**
     * Method used to generate stargate in random position by mysterious page
     */
    public static GeneratedStargate mystPageGeneration(World world, SymbolTypeEnum symbolType, int dimensionToSpawn) {
        Random rand = new Random();

        ArrayList<Block> blocks = new ArrayList<>();
        blocks.add(Blocks.GRASS);
        blocks.add(Blocks.GRAVEL);
        blocks.add(Blocks.DIRT);
        blocks.add(Blocks.SAND);
        blocks.add(Blocks.SANDSTONE);
        blocks.add(Blocks.END_STONE);
        blocks.add(Blocks.SNOW_LAYER);
        blocks.add(Blocks.SNOW);

        BlockPos pos = null;
        int tries = 0;
        WorldServer worldToSpawn = Objects.requireNonNull(world.getMinecraftServer()).getWorld(dimensionToSpawn);
        EnumStructures structure = null;
        int x;
        int z;
        do {
            x = (int) (tauri.dev.jsg.config.JSGConfig.mysteriousConfig.minOverworldCoords + (rand.nextFloat() * (tauri.dev.jsg.config.JSGConfig.mysteriousConfig.maxOverworldCoords - tauri.dev.jsg.config.JSGConfig.mysteriousConfig.minOverworldCoords))) * (rand.nextBoolean() ? -1 : 1);
            z = (int) (JSGConfig.mysteriousConfig.minOverworldCoords + (rand.nextFloat() * (tauri.dev.jsg.config.JSGConfig.mysteriousConfig.maxOverworldCoords - tauri.dev.jsg.config.JSGConfig.mysteriousConfig.minOverworldCoords))) * (rand.nextBoolean() ? -1 : 1);

            int chunkX = x / 16;
            int chunkZ = z / 16;

            if (world.isChunkGeneratedAt(chunkX, chunkZ))
                continue;

            Chunk chunk = worldToSpawn.getChunkFromChunkCoords(chunkX, chunkZ);
            int y = chunk.getHeightValue(8, 8);
            if (y > 240)
                continue;

            String biomeName = worldToSpawn.getBiome(new BlockPos(x, y, z)).getBiomeName();
            structure = EnumStructures.getStargateStructureByBiome(biomeName, symbolType, dimensionToSpawn);
            if (structure != null) {
                JSG.info("Structure != null");
                if (checkTopBlock(worldToSpawn, x, z, structure, dimensionToSpawn)) {
                    JSG.info("Top block: OK");
                    pos = JSGStructuresGenerator.checkForPlace(worldToSpawn, chunkX, chunkZ, structure.structure.structureSizeX, structure.structure.structureSizeZ, dimensionToSpawn);
                    JSG.info("Pos: " + pos);
                }
            }
            tries++;
        } while (pos == null && tries < 100);
        if (structure == null || tries == 100) {
            JSG.logger.info("StargateGenerator: Failed to find place - myst page: T:" + tries + " S:" + (structure != null));
            return null;
        }

        return structure.structure.generateStructure(world, pos, rand, worldToSpawn);
    }
}
