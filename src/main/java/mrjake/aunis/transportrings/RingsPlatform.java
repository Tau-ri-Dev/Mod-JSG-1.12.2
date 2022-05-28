package mrjake.aunis.transportrings;

import mrjake.aunis.block.props.TRPlatformBlock;
import mrjake.aunis.sound.SoundEventEnum;
import net.minecraft.util.math.BlockPos;

public class RingsPlatform {
    public int x;
    public int y;
    public int z;

    public TRPlatformBlock platformBlock;
    public SoundEventEnum openSound;
    public SoundEventEnum closeSound;

    public RingsPlatform(BlockPos pos, TRPlatformBlock platformBlock){
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();

        this.platformBlock = platformBlock;
        this.openSound = openSound;
        this.closeSound = closeSound;
    }
}
