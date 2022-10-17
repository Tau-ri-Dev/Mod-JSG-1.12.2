package tauri.dev.jsg.machine;

public class ArmPos {
    public final float x;
    public final float y;
    public final float z;

    public final float speed;

    /**
     * Position of arm to go to
     *
     * @param x - x coord
     * @param y - y coord
     * @param z - z coord
     * @param speed - speed of the arm
     */
    public ArmPos(float x, float y, float z, float speed){
        this.x = x;
        this.y = y;
        this.z = z;

        this.speed = speed;
    }
}
