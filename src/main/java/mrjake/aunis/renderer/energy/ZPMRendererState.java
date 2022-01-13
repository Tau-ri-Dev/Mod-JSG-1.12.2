package mrjake.aunis.renderer.energy;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.state.State;
import net.minecraft.util.math.BlockPos;

public class ZPMRendererState extends State {

    public ZPMRendererState() {}

    public ZPMRendererState(int charge) {
        this.charge = charge;
    }

    public ZPMRendererState initClient(BlockPos pos, int powerLevel) {
        this.pos = pos;
        this.charge = powerLevel;

        // todo register and save zpms state

        return this;
    }


    // Global
    // Not saved
    //private final Map<Integer, Integer> ZMP_STATE_MAP = new HashMap<>(3);
    public BlockPos pos;

    // zpmsActive
    // Saved
    public int charge;

    public BiomeOverlayEnum getBiomeOverlay() {
        return BiomeOverlayEnum.NORMAL;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(charge);
    }

    public void fromBytes(ByteBuf buf) {
        charge = buf.readInt();
    }
}
