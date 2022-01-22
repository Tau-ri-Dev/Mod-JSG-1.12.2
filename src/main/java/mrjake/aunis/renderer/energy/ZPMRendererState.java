package mrjake.aunis.renderer.energy;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.state.State;
import net.minecraft.util.math.BlockPos;

public class ZPMRendererState extends State {

    public ZPMRendererState() {}

    public ZPMRendererState(int powerLevel) {
        this.powerLevel = powerLevel;
    }

    public ZPMRendererState initClient(BlockPos pos, int powerLevel) {
        this.pos = pos;
        this.powerLevel = powerLevel;
        return this;
    }


    // Global
    // Not saved
    public BlockPos pos;

    // Saved
    public int powerLevel;

    public BiomeOverlayEnum getBiomeOverlay() {
        return BiomeOverlayEnum.NORMAL;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(powerLevel);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        powerLevel = buf.readInt();
    }
}
