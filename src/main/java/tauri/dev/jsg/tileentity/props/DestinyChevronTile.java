package tauri.dev.jsg.tileentity.props;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateTypeEnum;

import javax.annotation.Nonnull;

public class DestinyChevronTile extends DestinyBearingTile {

    public DestinyChevronRenderState rendererState = new DestinyChevronRenderState(false, BiomeOverlayEnum.NORMAL);

    public static class DestinyChevronRenderState extends DestinyBearingRenderState {

        public DestinyChevronRenderState(boolean isActive, BiomeOverlayEnum overlay) {
            super(isActive);
            this.overlay = overlay;
        }


        public DestinyChevronRenderState setOverlay(BiomeOverlayEnum overlay) {
            this.overlay = overlay;
            return this;
        }

        public BiomeOverlayEnum overlay;

        @Override
        public void toBytes(ByteBuf buf) {
            super.toBytes(buf);
            buf.writeInt(overlay.id);
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            super.fromBytes(buf);
            overlay = BiomeOverlayEnum.byId(buf.readInt());
        }
    }

    public BiomeOverlayEnum overlay = BiomeOverlayEnum.NORMAL;

    @Override
    public DestinyChevronRenderState getRendererState() {
        return rendererState;
    }

    // CLIENT ONLY
    public void updateOverlay(BiomeOverlayEnum overlay) {
        if (this.overlay != overlay) {
            this.overlay = overlay;
            markDirty();
        }
    }

    // Server
    @Override
    public State getState(StateTypeEnum stateType) {
        if (stateType == StateTypeEnum.RENDERER_STATE) {
            return getRendererState().setOverlay(overlay).setActive(isActive);
        }
        return null;
    }

    // Server
    @Override
    public State createState(StateTypeEnum stateType) {
        if (stateType == StateTypeEnum.RENDERER_STATE) {
            return getRendererState().setActive(isActive);
        }
        return null;
    }

    // Client
    @Override
    public void setState(StateTypeEnum stateType, State state) {
        if (stateType == StateTypeEnum.RENDERER_STATE) {
            this.isActive = ((DestinyBearingRenderState) state).isActive;
        }
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        this.overlay = BiomeOverlayEnum.byId(compound.getInteger("overlay"));
        super.readFromNBT(compound);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setInteger("overlay", overlay.id);
        return super.writeToNBT(compound);
    }
}
