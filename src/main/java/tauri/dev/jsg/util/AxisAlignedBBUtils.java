package tauri.dev.jsg.util;

import net.minecraft.util.math.AxisAlignedBB;

/**
 * @author matousss
 */
public class AxisAlignedBBUtils {

    public static AxisAlignedBB rotateHorziontally(AxisAlignedBB axis, int angle) {
        switch (angle) {
            case 0:
                return new AxisAlignedBB(axis.minX, axis.minY, axis.minZ, axis.maxX, axis.maxY, axis.maxZ);
            case 90:
                return new AxisAlignedBB(axis.minZ, axis.minY, axis.maxX, axis.maxZ,axis.maxY, axis.minX);
            case 180:
                return new AxisAlignedBB(axis.maxX, axis.minY, axis.maxZ, axis.minX, axis.minY, axis.minZ);
            case 270:
                return new AxisAlignedBB(axis.maxZ, axis.minY, axis.minX, axis.minZ, axis.maxY, axis.maxX);
            default:
                throw new UnsupportedOperationException("Angle must be 0/90/180/270");
        }
    }
}
