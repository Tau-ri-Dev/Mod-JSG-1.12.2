package tauri.dev.jsg.worldgen.util;

import net.minecraft.util.math.BlockPos;

public class JSGStructurePos {

    public BlockPos foundPos;
    public BlockPos bestAttemptPos;

    public JSGStructurePos(BlockPos foundPos, BlockPos bestAttemptPos){
        this.bestAttemptPos = bestAttemptPos;
        this.foundPos = foundPos;
    }
}
