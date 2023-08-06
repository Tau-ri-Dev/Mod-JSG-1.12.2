package tauri.dev.jsg.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RayTraceHelper {

    public static RayTraceResult rayTraceEntity(Entity e, double blockReachDistance, float partialTicks) {
        Vec3d vec3d = e.getPositionEyes(partialTicks);
        Vec3d vec3d1 = e.getLook(partialTicks);
        Vec3d vec3d2 = vec3d.addVector(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        return e.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
    }

    @Nullable
    public static TileEntity rayTraceTileEntity(@Nonnull EntityPlayer player) {
        return rayTraceTileEntity(player, 8);
    }
    @Nullable
    public static TileEntity rayTraceTileEntity(@Nonnull EntityPlayer player, int distance) {
        try {
            RayTraceResult rayTraceResult = rayTraceEntity(player, distance, 0);
            if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                return player.getEntityWorld().getTileEntity(rayTraceResult.getBlockPos());
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    @Nullable
    public static BlockPos rayTracePos(@Nonnull EntityPlayer player, int distance) {
        try {
            RayTraceResult rayTraceResult = rayTraceEntity(player, distance, 0);
            if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                return rayTraceResult.getBlockPos();
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
