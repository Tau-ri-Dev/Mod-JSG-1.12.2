package tauri.dev.jsg.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

public class JSGMinecraftHelper {
    /**
     * Get current tick on client side.
     * !IT DOES NOT PAUSE IF YOU PAUSE THE GAME!
     *
     * @return current tick (long)
     */
    public static long getClientTick() {
        return (long) Math.floor((Minecraft.getSystemTime() / (double) 1000) * 20);
    }

    public static double getClientTickPrecise() {
        return ((Minecraft.getSystemTime() / 1000D) * 20D);
    }

    @SideOnly(Side.CLIENT)
    public static long getPlayerTickClientSide() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) return 0;
        return player.getEntityWorld().getTotalWorldTime();
    }


    @ParametersAreNonnullByDefault
    public static boolean isEntityCollidingWithBlock(Entity entity, IBlockState searchBlock) {
        AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox();
        BlockPos.PooledMutableBlockPos mutableBlockPos = BlockPos.PooledMutableBlockPos.retain(axisalignedbb.minX + 0.001D, axisalignedbb.minY + 0.001D, axisalignedbb.minZ + 0.001D);
        BlockPos.PooledMutableBlockPos mutableBlockPos1 = BlockPos.PooledMutableBlockPos.retain(axisalignedbb.maxX - 0.001D, axisalignedbb.maxY - 0.001D, axisalignedbb.maxZ - 0.001D);
        BlockPos.PooledMutableBlockPos mutableBlockPos2 = BlockPos.PooledMutableBlockPos.retain();

        boolean isTrue = false;

        if (entity.world.isAreaLoaded(mutableBlockPos, mutableBlockPos1)) {
            for (int i = mutableBlockPos.getX(); i <= mutableBlockPos1.getX(); ++i) {
                if(isTrue) break;
                for (int j = mutableBlockPos.getY(); j <= mutableBlockPos1.getY(); ++j) {
                    if(isTrue) break;
                    for (int k = mutableBlockPos.getZ(); k <= mutableBlockPos1.getZ(); ++k) {
                        mutableBlockPos2.setPos(i, j, k);
                        IBlockState iBlockState = entity.world.getBlockState(mutableBlockPos2);
                        if(iBlockState.getBlock() == searchBlock.getBlock() && iBlockState.getBlock().getMetaFromState(iBlockState) == searchBlock.getBlock().getMetaFromState(searchBlock)){
                            isTrue = true;
                            break;
                        }
                    }
                }
            }
        }

        mutableBlockPos.release();
        mutableBlockPos1.release();
        mutableBlockPos2.release();
        return isTrue;
    }
}
