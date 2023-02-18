package tauri.dev.jsg.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class FacingHelper {
    private static final Map<EnumFacing, Rotation> ROTATION_MAP = new HashMap<>();

    static {
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            switch (facing) {
                case SOUTH:
                    ROTATION_MAP.put(facing, Rotation.NONE);
                    break;
                case WEST:
                    ROTATION_MAP.put(facing, Rotation.CLOCKWISE_90);
                    break;
                case NORTH:
                    ROTATION_MAP.put(facing, Rotation.CLOCKWISE_180);
                    break;
                case EAST:
                    ROTATION_MAP.put(facing, Rotation.COUNTERCLOCKWISE_90);
                    break;
                default:
                    break;
            }
        }
    }

    public static Rotation getRotation(EnumFacing facing) {
        return ROTATION_MAP.get(facing);
    }

    public static int getIntRotation(EnumFacing facing, boolean inverted) {
        switch (facing) {
            case EAST:
			case DOWN:
                return (inverted ? 90 : 270);

            case SOUTH:
                return (!inverted ? 180 : 0);

            case WEST:
			case UP:
                return (!inverted ? 90 : 270);

            default:
                return (inverted ? 180 : 0);
        }
    }

    public static EnumFacing getVerticalFacingFromPitch(float pitch) {
        if (pitch < -45f)
            return EnumFacing.DOWN;
        if (pitch > 45f)
            return EnumFacing.UP;
        return EnumFacing.SOUTH;
    }

    public static BlockPos rotateBlock(BlockPos pos, EnumFacing horizontalFacing, EnumFacing verticalFacing) {
		BlockPos newPos = pos;
		switch(verticalFacing){
			case UP:
				newPos = new BlockPos(pos.getX(), pos.getZ(), -pos.getY());
				break;
			case DOWN:
				newPos = new BlockPos(pos.getX(), -pos.getZ(), pos.getY());
				break;
			default:
				break;
		}
		switch(horizontalFacing){
			default:
				break;
			case WEST:
				return new BlockPos(-newPos.getZ(), newPos.getY(), newPos.getX());
			case NORTH:
				return new BlockPos(-newPos.getX(), newPos.getY(), -newPos.getZ());
			case EAST:
				return new BlockPos(newPos.getZ(), newPos.getY(), -newPos.getX());
		}
		return newPos;
    }
}
