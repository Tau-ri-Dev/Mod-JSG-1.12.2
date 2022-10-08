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
		switch(world.provider.getDimensionType()) {
			case NETHER:
				if (tauri.dev.jsg.config.JSGConfig.oreGeneratorConfig.naquadahEnable) {
					runGenerator(JSGBlocks.ORE_NAQUADAH_BLOCK.getDefaultState(),
							tauri.dev.jsg.config.JSGConfig.oreGeneratorConfig.naquadahVeinSize,
							tauri.dev.jsg.config.JSGConfig.oreGeneratorConfig.naquadahMaxVeinInChunk, 0, 128,
							BlockMatcher.forBlock(Blocks.NETHERRACK), world, rand, chunkX, chunkZ);
				}
				break;
			case OVERWORLD:
				if (JSGConfig.oreGeneratorConfig.titaniumEnable) {
					runGenerator(JSGBlocks.ORE_TITANIUM_BLOCK.getDefaultState(),
							tauri.dev.jsg.config.JSGConfig.oreGeneratorConfig.titaniumVeinSize,
							tauri.dev.jsg.config.JSGConfig.oreGeneratorConfig.titaniumMaxVeinInChunk, 0, 25,
							BlockMatcher.forBlock(Blocks.STONE), world, rand, chunkX, chunkZ);
				}
				break;
			case THE_END:
				if (tauri.dev.jsg.config.JSGConfig.oreGeneratorConfig.triniumEnabled) {
					runGenerator(JSGBlocks.ORE_TRINIUM_BLOCK.getDefaultState(),
							tauri.dev.jsg.config.JSGConfig.oreGeneratorConfig.triniumVeinSize,
							tauri.dev.jsg.config.JSGConfig.oreGeneratorConfig.triniumMaxVeinInChunk, 0, 128,
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
