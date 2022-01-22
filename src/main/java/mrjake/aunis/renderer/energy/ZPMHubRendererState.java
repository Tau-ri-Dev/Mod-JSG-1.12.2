package mrjake.aunis.renderer.energy;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.state.State;
import net.minecraft.util.math.BlockPos;

public class ZPMHubRendererState extends State {

    public ZPMHubRendererState() {}

    public ZPMHubRendererState initClient(BlockPos pos, long tick, boolean isPutting, int zpmsCount, int zpmAnimated) {
        this.pos = pos;
        this.animationStart = tick;
        this.isPutting = isPutting;
        this.zpmsCount = zpmsCount;
        this.zpmAnimated = zpmAnimated;

        return this;
    }


    // Global
    // Not saved
    //private final Map<Integer, Integer> ZMP_STATE_MAP = new HashMap<>(3);
    public BlockPos pos;

    // zpmsActive
    // Saved
    public int zpmsActive;
    public long animationStart;
    public boolean isPutting;
    public int zpmsCount;
    public int zpmAnimated;

    public BiomeOverlayEnum getBiomeOverlay() {
        return BiomeOverlayEnum.NORMAL;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(zpmsActive);
        buf.writeLong(animationStart);
        buf.writeBoolean(isPutting);
        buf.writeInt(zpmsCount);
        buf.writeInt(zpmAnimated);
    }

    public void fromBytes(ByteBuf buf) {
        zpmsActive = buf.readInt();
        animationStart = buf.readLong();
        isPutting = buf.readBoolean();
        zpmsCount = buf.readInt();
        zpmAnimated = buf.readInt();
    }
}
