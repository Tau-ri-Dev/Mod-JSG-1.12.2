package tauri.dev.jsg.worldgen.util;

public enum EnumGenerationHeight {
    LOW,
    MIDDLE,
    HEIGHT;

    public int getHeight(int lowestY, int highestY){
        switch(this){
            case HEIGHT:
                return highestY;
            case MIDDLE:
                return ((lowestY + highestY) / 2);
            case LOW:
            default:
                return lowestY;
        }
    }
}
