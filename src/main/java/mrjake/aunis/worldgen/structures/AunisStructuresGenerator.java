package mrjake.aunis.worldgen.structures;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.*;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class AunisStructuresGenerator implements IWorldGenerator {
    public static final AunisStructure MINESHAFT = new AunisStructure("mineshaft", 14);

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        switch(world.provider.getDimensionType()) {
            case OVERWORLD:
                this.generateStructure(MINESHAFT, world, random, chunkX, chunkZ, 250, Blocks.GRASS, BiomeForest.class, BiomePlains.class, BiomeSwamp.class);
                break;
            default:
                break;
        }
    }

    private void generateStructure(WorldGenerator generator, World world, Random random, int chunkX, int chunkZ, int chance, Block topBlock, Class<?>... classes) {
        if (chance > 0 && random.nextInt(chance) == 0) {
            ArrayList<Class<?>> classesList = new ArrayList<>(Arrays.asList(classes));
            int x = (chunkX * 16) + random.nextInt(15);
            int z = (chunkZ * 16) + random.nextInt(15);
            int y = calcHeight(world, x, z, topBlock);
            if (y > 0) {
                BlockPos pos = new BlockPos(x, y, z);
                Class<?> biome = world.provider.getBiomeForCoords(pos).getClass();
                if (world.getWorldType() != WorldType.FLAT) {
                    if (classesList.contains(biome)) {
                        generator.generate(world, random, pos);
                    }
                }
            }
        }
    }

    private static int calcHeight(World world, int x, int z, Block topBlock){
        int y = world.getHeight();
        boolean found = false;
        while(y > 0 && !found){
            Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
            found = (block == topBlock);
            y--;
        }
        return y;
    }
}
