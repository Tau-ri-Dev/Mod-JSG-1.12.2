package tauri.dev.jsg.renderer.dialhomedevice;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.config.ingame.JSGTileEntityConfig;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.stargate.network.StargateAddressDynamic;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.state.State;

public abstract class DHDAbstractRendererState extends State {
    public DHDAbstractRendererState() {
    }

    public DHDAbstractRendererState(StargateAddressDynamic addressDialed, boolean brbActive, BiomeOverlayEnum biomeOverride, boolean stargateIsConnected, JSGTileEntityConfig gateConfig) {
        this.addressDialed = addressDialed;
        this.brbActive = brbActive;
        this.biomeOverride = biomeOverride;
        this.stargateIsConnected = stargateIsConnected;
		this.gateConfig = gateConfig;
    }

    public DHDAbstractRendererState initClient(BlockPos pos, float horizontalRotation, BiomeOverlayEnum biomeOverlay, boolean stargateIsConnected) {
        this.pos = pos;
        this.horizontalRotation = horizontalRotation;
        this.biomeOverlay = biomeOverlay;
        this.stargateIsConnected = stargateIsConnected;
        return this;
    }

    public void setIsConnected(boolean connected) {
        stargateIsConnected = connected;
    }

    public BlockPos pos;
    public float horizontalRotation;
    protected BiomeOverlayEnum biomeOverlay;
    public boolean stargateIsConnected;
    public StargateAddressDynamic addressDialed;
    public boolean brbActive;
    public BiomeOverlayEnum biomeOverride;

	public JSGTileEntityConfig gateConfig = new JSGTileEntityConfig();

    public BiomeOverlayEnum getBiomeOverlay() {
        if (biomeOverride != null)
            return biomeOverride;

        return biomeOverlay;
    }

    public void setBiomeOverlay(BiomeOverlayEnum biomeOverlay) {
        this.biomeOverlay = biomeOverlay;
    }

    public abstract void iterate(World world, double partialTicks);

    public abstract boolean isButtonActive(SymbolInterface symbol);

    public abstract int getActivatedButtons();


    public void toBytes(ByteBuf buf) {
        addressDialed.toBytes(buf);
        buf.writeBoolean(brbActive);

        if (biomeOverride != null) {
            buf.writeBoolean(true);
            buf.writeInt(biomeOverride.ordinal());
        } else {
            buf.writeBoolean(false);
        }

        buf.writeBoolean(stargateIsConnected);
		gateConfig.toBytes(buf);
    }

    public void fromBytes(ByteBuf buf) {
        addressDialed.fromBytes(buf);
        brbActive = buf.readBoolean();

        if (buf.readBoolean()) {
            biomeOverride = BiomeOverlayEnum.values()[buf.readInt()];
        }
        stargateIsConnected = buf.readBoolean();
		gateConfig.fromBytes(buf);
    }
}