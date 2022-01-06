package mrjake.aunis.renderer.energy;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.state.State;
import net.minecraft.util.math.BlockPos;

public class ZPMHubRendererState extends State {

    public ZPMHubRendererState() {}

    public ZPMHubRendererState(int zpmsActive) {
        this.zpmsActive = zpmsActive;
    }

    public ZPMHubRendererState initClient(BlockPos pos, float horizontalRotation) {
        this.pos = pos;
        this.horizontalRotation = horizontalRotation;

        // todo register and save zpms state

        return this;
    }


    // Global
    // Not saved
    //private final Map<Integer, Integer> ZMP_STATE_MAP = new HashMap<>(3);
    public BlockPos pos;
    public float horizontalRotation;

    // zpmsActive
    // Saved
    public int zpmsActive;

    public BiomeOverlayEnum getBiomeOverlay() {
        return BiomeOverlayEnum.NORMAL;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(zpmsActive);
    }

    public void fromBytes(ByteBuf buf) {
        zpmsActive = buf.readInt();
    }
}
