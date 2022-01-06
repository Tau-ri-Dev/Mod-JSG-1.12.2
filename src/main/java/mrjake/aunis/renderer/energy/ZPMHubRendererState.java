package mrjake.aunis.renderer.energy;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.Aunis;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.dialhomedevice.DHDMilkyWayRendererState;
import mrjake.aunis.stargate.network.StargateAddressDynamic;
import mrjake.aunis.stargate.network.SymbolMilkyWayEnum;
import mrjake.aunis.stargate.network.SymbolTypeEnum;
import mrjake.aunis.state.State;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class ZPMHubRendererState extends State {

    public ZPMHubRendererState() {}

    private static final String ZMP_TEXTURE_BASE = "textures/tesr/zpm/item/zpm";

    private static class TextureContainer {
        public final Map<Integer, ResourceLocation> ZMP_TEXTURE_BASE = new HashMap<>();
    }

    static {
        ZPMHubRendererState.TextureContainer container = new ZPMHubRendererState.TextureContainer();

        for (int i=0; i<3; i++) {
            container.ZMP_TEXTURE_BASE.put(i, new ResourceLocation(Aunis.ModID, ZMP_TEXTURE_BASE + "_" + i + ".png"));
        }
    }

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

    // Symbols
    // Saved
    public int zpmsActive;

    // Biome Override
    // Saved
    public BiomeOverlayEnum biomeOverride;

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
