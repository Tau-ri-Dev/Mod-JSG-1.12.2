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

    public static int toInt(EnumFacing f){
        if(f == null) return 0;
        switch(f){
            case NORTH:
                return 1;
            case WEST:
                return 2;
            case EAST:
                return 3;
            case UP:
                return 4;
            case DOWN:
                return 5;
            default: break;
        }
        return 0;
    }

    public static EnumFacing fromInt(int i){
        switch(i){
            case 1:
                return EnumFacing.NORTH;
            case 2:
                return EnumFacing.WEST;
            case 3:
                return EnumFacing.EAST;
            case 4:
                return EnumFacing.UP;
            case 5:
                return EnumFacing.DOWN;
            default: break;
        }
        return EnumFacing.SOUTH;

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

    public static EnumFacing getFacingFromRotation(int rotation, boolean inverted) {
        if(!inverted){
            if(rotation < 45 || rotation >= (270+45))
                return EnumFacing.NORTH;
            if(rotation < 90+45)
                return EnumFacing.WEST;
            if(rotation < 180+45)
                return EnumFacing.SOUTH;
            return EnumFacing.EAST;
        }
        else{
            if(rotation < 45 || rotation >= (270+45))
                return EnumFacing.SOUTH;
            if(rotation < 90+45)
                return EnumFacing.EAST;
            if(rotation < 180+45)
                return EnumFacing.NORTH;
            return EnumFacing.WEST;
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

    public static int getIntDHDRotationFromFacing(EnumFacing facing, boolean inverted){
        return (int) Math.floor((getIntRotation(facing, inverted) / 360f) * 16);
    }

    public static EnumFacing getFacingFromDHDRotation(int rotation, boolean inverted){
        return getFacingFromRotation((int) (rotation / 16f * 360), inverted);
    }
}
