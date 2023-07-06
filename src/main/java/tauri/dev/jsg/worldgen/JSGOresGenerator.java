package tauri.dev.jsg.worldgen;

import com.google.common.base.Predicate;
import tauri.dev.jsg.block.JSGBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;
import tauri.dev.jsg.config.JSGConfig;

import java.util.Random;

/**
 * Class handling OreGen for Just Stargate Mod
 */
public class JSGOresGenerator implements IWorldGenerator {
	
	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		switch(world.provider.getDimension()) {
			case -1:
				if (JSGConfig.WorldGen.ores.naquadahEnable) {
					runGenerator(JSGBlocks.ORE_NAQUADAH_BLOCK.getDefaultState(),
							tauri.dev.jsg.config.JSGConfig.WorldGen.ores.naquadahVeinSize,
							tauri.dev.jsg.config.JSGConfig.WorldGen.ores.naquadahMaxVeinInChunk, 0, 128,
							BlockMatcher.forBlock(Blocks.NETHERRACK), world, rand, chunkX, chunkZ);
				}
				break;
			case 0:
				if (JSGConfig.WorldGen.ores.titaniumEnable) {
					runGenerator(JSGBlocks.ORE_TITANIUM_BLOCK.getDefaultState(),
							tauri.dev.jsg.config.JSGConfig.WorldGen.ores.titaniumVeinSize,
							tauri.dev.jsg.config.JSGConfig.WorldGen.ores.titaniumMaxVeinInChunk, 0, 25,
							BlockMatcher.forBlock(Blocks.STONE), world, rand, chunkX, chunkZ);
				}
				break;
			case 1:
				if (tauri.dev.jsg.config.JSGConfig.WorldGen.ores.triniumEnabled) {
					runGenerator(JSGBlocks.ORE_TRINIUM_BLOCK.getDefaultState(),
							tauri.dev.jsg.config.JSGConfig.WorldGen.ores.triniumVeinSize,
							tauri.dev.jsg.config.JSGConfig.WorldGen.ores.triniumMaxVeinInChunk, 0, 128,
							BlockMatcher.forBlock(Blocks.END_STONE), world, rand, chunkX, chunkZ);
				}
				break;
			default:
				break;
		}
	}

	private void runGenerator(IBlockState blockToGen, int blockAmount, int chancesToSpawn, int minHeight, int maxHeight, Predicate<IBlockState> blockToReplace, World world, Random rand, int chunkX, int chunkZ) {	
		
		if (minHeight < 0 || maxHeight > 256 || minHeight > maxHeight)
			throw new IllegalArgumentException("Illegal height arguments for JSGOresGenerator::runGenerator()");
		
		WorldGenMinable generator = new WorldGenMinable(blockToGen, blockAmount, blockToReplace);
		
		int heightDiff = maxHeight - minHeight;
		int bx = chunkX * 16 + 2;
		int bz = chunkZ * 16 + 2;
				
		for(int i=0; i<chancesToSpawn; i++) {
			int x = bx + rand.nextInt(16-4);
			int y = minHeight + rand.nextInt(heightDiff);
			int z = bz + rand.nextInt(16-4);
			
			generator.generate(world, rand, new BlockPos(x,y,z));
		}
	}
}
