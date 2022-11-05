package tauri.dev.jsg.worldgen.structures;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class JSGWorldTopBlock {
    public int y;
    public Block topBlock;
    public IBlockState topBlockState;
    public JSGWorldTopBlock(int y, Block topBlock, IBlockState state){
        this.y = y;
        this.topBlock = topBlock;
        this.topBlockState = state;
    }

    public static JSGWorldTopBlock getTopBlock(World world, int x, int z, int airCountUp, int dimensionId) {
        int y = (dimensionId == -1 ? 0 : world.getHeight());
        while (((dimensionId != -1 && y > 0) || (dimensionId == -1 && y < world.getHeight()))) {
            BlockPos pos = new BlockPos(x, y, z);
            Block block = world.getBlockState(pos).getBlock();
            boolean isAirUp = true;
            for (int i = 1; i <= airCountUp; i++) {
                Block airBlock = world.getBlockState(new BlockPos(x, (y + i), z)).getBlock();
                if (airBlock != Blocks.SNOW_LAYER && !block.isReplaceable(world, pos) && airBlock != Blocks.AIR && airBlock != Blocks.LEAVES && airBlock != Blocks.LEAVES2 && airBlock != Blocks.LOG && airBlock != Blocks.LOG2) {
                    isAirUp = false;
                    break;
                }
            }
            if (block != Blocks.SNOW_LAYER && block != Blocks.AIR && !block.isReplaceable(world, pos) && isAirUp && block != Blocks.BEDROCK && y < 240)
                return new JSGWorldTopBlock(y, block, world.getBlockState(pos));
            y += (dimensionId == -1 ? 1 : -1);
        }
        return null;
    }
}
