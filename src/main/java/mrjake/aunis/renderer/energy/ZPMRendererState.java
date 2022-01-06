package mrjake.aunis.renderer.energy;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.state.State;
import net.minecraft.util.math.BlockPos;

public class ZPMRendererState extends State {

    public ZPMRendererState() {}

    public ZPMRendererState(boolean isCharged) {
        this.isCharged = isCharged;
    }

    public ZPMRendererState initClient(BlockPos pos, float horizontalRotation) {
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
    public boolean isCharged;

    public BiomeOverlayEnum getBiomeOverlay() {
        return BiomeOverlayEnum.NORMAL;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(isCharged);
    }

    public void fromBytes(ByteBuf buf) {
        isCharged = buf.readBoolean();
    }
}
