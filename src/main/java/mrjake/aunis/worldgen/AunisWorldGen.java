package mrjake.aunis.worldgen;

import com.google.common.base.Predicate;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.config.AunisConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Objects;
import java.util.Random;

/**
 * Class handling WorldGen for The AUNIS Mod
 */
public class AunisWorldGen implements IWorldGenerator {

	private static final Random random = new Random();
	
	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		switch(world.provider.getDimensionType()) {
			// todo(Mine): Fix gate generator (error in console, but working)
			case NETHER:
				if (AunisConfig.worldgenConfig.naquadahEnable) {
					runGenerator(AunisBlocks.ORE_NAQUADAH_BLOCK.getDefaultState(),
							AunisConfig.worldgenConfig.naquadahVeinSize,
							AunisConfig.worldgenConfig.naquadahMaxVeinInChunk, 0, 128,
							BlockMatcher.forBlock(Blocks.NETHERRACK), world, rand, chunkX, chunkZ);
				}
				if(AunisConfig.worldgenConfig.chanceOfGateNether != 0 && random.nextInt(1000) < AunisConfig.worldgenConfig.chanceOfGateNether)
					StargateGeneratorNether.place(Objects.requireNonNull(world.getMinecraftServer()).getWorld(DimensionType.NETHER.getId()), new BlockPos(chunkX/8, 32, chunkZ/8));
				break;
			case OVERWORLD:
				if (AunisConfig.worldgenConfig.titaniumEnable) {
					runGenerator(AunisBlocks.ORE_TITANIUM_BLOCK.getDefaultState(),
							AunisConfig.worldgenConfig.titaniumVeinSize,
							AunisConfig.worldgenConfig.titaniumMaxVeinInChunk, 0, 25,
							BlockMatcher.forBlock(Blocks.STONE), world, rand, chunkX, chunkZ);
				}
				if(AunisConfig.worldgenConfig.chanceOfGateWorld != 0 && random.nextInt(1000) < AunisConfig.worldgenConfig.chanceOfGateWorld)
					StargateGenerator.generateStargateNear(world, chunkX, chunkZ);
				break;
			case THE_END:
				if (AunisConfig.worldgenConfig.triniumEnabled) {
					runGenerator(AunisBlocks.ORE_TRINIUM_BLOCK.getDefaultState(),
							AunisConfig.worldgenConfig.triniumVeinSize,
							AunisConfig.worldgenConfig.triniumMaxVeinInChunk, 0, 128,
							BlockMatcher.forBlock(Blocks.END_STONE), world, rand, chunkX, chunkZ);
				}
				//if(AunisConfig.worldgenConfig.chanceOfGateEnd != 0 && random.nextInt(1000) < AunisConfig.worldgenConfig.chanceOfGateEnd)
				//	StargateGenerator.generateStargateNearEnd(world, chunkX, chunkZ);
				break;
			default:
				break;
		}
	}

	private void runGenerator(IBlockState blockToGen, int blockAmount, int chancesToSpawn, int minHeight, int maxHeight, Predicate<IBlockState> blockToReplace, World world, Random rand, int chunkX, int chunkZ) {	
		
		if (minHeight < 0 || maxHeight > 256 || minHeight > maxHeight)
			throw new IllegalArgumentException("Illegal height arguments for AunisWorldGen::runGenerator()");
		
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
