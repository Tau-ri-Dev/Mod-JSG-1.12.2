package tauri.dev.jsg.worldgen.structures;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import tauri.dev.jsg.config.JSGConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class JSGStructuresGenerator implements IWorldGenerator {
    public static final JSGStructure PLAINS = new JSGStructure("plains", 0, true, false, 11, 11, 0);
    public static final JSGStructure DESERT = new JSGStructure("desert", 0, true, false, 12, 13, 0);

    public static final JSGStructure NETHER = new JSGStructure("nether", 0, true, false, 8, 8, -1);

    public static final JSGStructure THE_END = new JSGStructure("desert", 0, true, false, 11, 11, 1);

    public static final JSGStructure NAQUADAH_MINE = new JSGStructure("naquadah_mine", 9, false, false, 15, 15, 0);

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        ArrayList<Block> blocks = new ArrayList<>();
        switch(world.provider.getDimensionType()) {
            case OVERWORLD:
                blocks.add(Blocks.GRASS);
                blocks.add(Blocks.DIRT);
                blocks.add(Blocks.STONE);
                if(JSGConfig.stargateGeneratorConfig.stargateRGChancePlains != 0) {
                    this.generateStructure(
                            PLAINS,
                            world, random, chunkX, chunkZ,
                            2000 - tauri.dev.jsg.config.JSGConfig.stargateGeneratorConfig.stargateRGChancePlains,
                            blocks,
                            BiomeForest.class, BiomePlains.class, BiomeSwamp.class, BiomeHills.class, BiomeSnow.class, BiomeTaiga.class,
                            BiomeJungle.class, BiomeForestMutated.class, BiomeSavannaMutated.class, BiomeSavanna.class

                    );
                }

                blocks.clear();
                blocks.add(Blocks.GRASS);
                this.generateStructure(
                        NAQUADAH_MINE,
                        world, random, chunkX, chunkZ,
                        2000 - 100,
                        blocks,
                        BiomeForest.class, BiomePlains.class
                );

                blocks.clear();
                blocks.add(Blocks.SAND);
                blocks.add(Blocks.SANDSTONE);
                if(tauri.dev.jsg.config.JSGConfig.stargateGeneratorConfig.stargateRGChanceDesert != 0) {
                    this.generateStructure(
                            DESERT,
                            world, random, chunkX, chunkZ,
                            2000 - tauri.dev.jsg.config.JSGConfig.stargateGeneratorConfig.stargateRGChanceDesert,
                            blocks,
                            BiomeDesert.class, BiomeMesa.class
                    );
                }
                break;
            /*case NETHER:
                blocks.add(Blocks.NETHERRACK);
                blocks.add(Blocks.SOUL_SAND);
                blocks.add(Blocks.QUARTZ_ORE);
                if(JSGConfig.stargateGeneratorConfig.stargateRGChanceNether == 0) break;
                this.generateStructure(
                        NETHER,
                        world, random, chunkX, chunkZ,
                        2000 - JSGConfig.stargateGeneratorConfig.stargateRGChanceNether,
                        blocks,
                        BiomeHell.class
                );
                break;*/
            case THE_END:
                blocks.add(Blocks.END_STONE);
                blocks.add(Blocks.END_BRICKS);
                if(tauri.dev.jsg.config.JSGConfig.stargateGeneratorConfig.stargateRGChanceTheEnd == 0) break;
                this.generateStructure(
                        THE_END,
                        world, random, chunkX, chunkZ,
                        2000 - tauri.dev.jsg.config.JSGConfig.stargateGeneratorConfig.stargateRGChanceTheEnd,
                        blocks,
                        BiomeEnd.class
                );
                break;
            default:
                break;
        }
        blocks.clear();
    }

    private void generateStructure(WorldGenerator generator, World world, Random random, int chunkX, int chunkZ, int chance, List<Block> topBlocks, Class<?>... classes) {
        if (chance > 0 && random.nextInt(chance) == 0) {
            ArrayList<Class<?>> classesList = new ArrayList<>(Arrays.asList(classes));
            BlockPos pos = new BlockPos((chunkX * 16) + random.nextInt(15), 10, (chunkZ * 16) + random.nextInt(15));

            Random rand = new Random();
            BlockPos newPos;
            int tries = 0;
            int x;
            int z;
            do {
                x = (int) (pos.getX() + (rand.nextFloat() * (800)) * (rand.nextBoolean() ? -1 : 1));
                z = (int) (pos.getZ() + (rand.nextFloat() * (800)) * (rand.nextBoolean() ? -1 : 1));

                newPos = checkForPlace(world, x/16, z/16, ((JSGStructure) generator).structureSizeX, ((JSGStructure) generator).structureSizeZ);
                if(newPos != null) {
                    if (!checkTopBlock(world, newPos.getX(), newPos.getZ(), topBlocks)) {
                        newPos = null;
                    }
                }
                tries++;
            } while (newPos == null && tries < 100);

            if (newPos != null && newPos.getY() > 0) {
                Class<?> biome = world.provider.getBiomeForCoords(newPos).getClass();
                if (world.getWorldType() != WorldType.FLAT) {
                    if (classesList.contains(biome)) {
                        generator.generate(world, random, newPos);
                    }
                }
            }
        }
    }

    private static BlockPos checkForPlace(World world, int chunkX, int chunkZ, int structureSizeX, int structureSizeY) {
        if (world.isChunkGeneratedAt(chunkX, chunkZ))
            return null;

        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        int y = chunk.getHeightValue(8, 8);

        if (y > 240)
            return null;

        BlockPos pos = new BlockPos(chunkX*16, y, chunkZ*16);

        int y1 = chunk.getHeightValue(0, 0);
        int y2 = chunk.getHeightValue(structureSizeX, structureSizeY);

        int y3 = chunk.getHeightValue(structureSizeX, 0);
        int y4 = chunk.getHeightValue(0, structureSizeY);

        // No steep hill
        if (Math.abs(y1 - y2) <= 1 && Math.abs(y3 - y4) <= 1) {
            return pos.subtract(new BlockPos(0, 1, 0));
        }
        return null;
    }

    private static boolean checkTopBlock(World world, int x, int z, List<Block> topBlocks){
        int y = world.getHeight();
        boolean found = false;
        while(y > 0 && !found){
            Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
            found = (topBlocks.contains(block));
            if(block != Blocks.AIR){
                return found;
            }
            y--;
        }
        return found;
    }
}
