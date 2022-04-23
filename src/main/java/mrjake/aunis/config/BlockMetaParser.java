package mrjake.aunis.config;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class BlockMetaParser {
	
	/**
	 * Parses array of configured blocks.
	 * 
	 * @param config Array of single lines
	 * @return List of {@link IBlockState}s or empty list.
	 */
	@Nonnull
	static Map<IBlockState, Boolean> parseConfig(String[] config) {
		Map<IBlockState, Boolean> map = new HashMap<>();

		for (String line : config) {
			String[] parts = line.trim().split(":", 3);
			Block block = Block.getBlockFromName(parts[0] + ":" + parts[1]);

			if (block != null && block != Blocks.AIR) {
				if (parts.length == 2){
					map.put(block.getDefaultState(), Boolean.FALSE);
				}
				else if(parts[2].equals("*")){
					map.put(block.getDefaultState(), Boolean.TRUE);
				}
				else
					map.put(block.getStateFromMeta(Integer.parseInt(parts[2])), Boolean.FALSE);
			}
		}
		
		return map;
	}
}
