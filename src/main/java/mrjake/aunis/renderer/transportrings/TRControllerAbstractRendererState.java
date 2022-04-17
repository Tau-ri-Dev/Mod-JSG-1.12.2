package mrjake.aunis.renderer.transportrings;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.stargate.network.SymbolInterface;
import mrjake.aunis.state.State;
import mrjake.aunis.tileentity.transportrings.TransportRingsAbstractTile;
import mrjake.aunis.transportrings.TransportRingsAddress;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TRControllerAbstractRendererState extends State {
    // Global
    // Not saved
    public BlockPos pos;
    // Saved
    public boolean ringsAreConnected;
    // Symbols
    // Not saved
    // Saved
    public TransportRingsAddress addressDialed;
    // Biome Override
    // Saved
    protected BiomeOverlayEnum biomeOverlay;

    public TRControllerAbstractRendererState(TransportRingsAddress addressDialed, BiomeOverlayEnum biomeOverride, boolean ringsAreConnected) {
        this.addressDialed = addressDialed;
        this.biomeOverlay = biomeOverride;
        this.ringsAreConnected = ringsAreConnected;
    }

    public TRControllerAbstractRendererState(){}

    public TRControllerAbstractRendererState initClient(BlockPos pos, BiomeOverlayEnum biomeOverlay, TransportRingsAbstractTile rings) {
        this.pos = pos;
        this.biomeOverlay = biomeOverlay;
        this.ringsAreConnected = rings != null && rings.isBusy();
        return this;
    }

    public void setIsConnected(boolean connected) {
        ringsAreConnected = connected;
    }

    public BiomeOverlayEnum getBiomeOverlay() {
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
        buf.writeBoolean(ringsAreConnected);
    }

    public void fromBytes(ByteBuf buf) {
        addressDialed.fromBytes(buf);
        ringsAreConnected = buf.readBoolean();
    }
}
