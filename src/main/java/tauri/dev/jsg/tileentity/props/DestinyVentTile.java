package tauri.dev.jsg.tileentity.props;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateTypeEnum;

import javax.annotation.Nonnull;

public class DestinyVentTile extends DestinyBearingTile {

    public DestinyVentRenderState rendererState = new DestinyVentRenderState(false, -1);

    public static class DestinyVentRenderState extends DestinyBearingRenderState {

        public DestinyVentRenderState(boolean isActive, long animationStart) {
            super(isActive);
            this.animationStart = animationStart;
        }


        public DestinyVentRenderState animate(boolean open, long animationStart) {
            setActive(open);
            this.animationStart = animationStart;
            return this;
        }

        public long animationStart;

        @Override
        public void toBytes(ByteBuf buf) {
            super.toBytes(buf);
            buf.writeLong(animationStart);
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            super.fromBytes(buf);
            animationStart = buf.readLong();
        }
    }

    public long animationStart = -1;

    @Override
    public DestinyVentRenderState getRendererState() {
        return rendererState;
    }

    // CLIENT ONLY
    public void startAnimation() {
        this.animationStart = world.getTotalWorldTime();
        markDirty();
        sendState(StateTypeEnum.RENDERER_STATE, getState(StateTypeEnum.RENDERER_STATE));
    }

    // Server
    @Override
    public State getState(StateTypeEnum stateType) {
        if (stateType == StateTypeEnum.RENDERER_STATE) {
            return getRendererState().animate(isActive, animationStart);
        }
        return null;
    }

    // Server
    @Override
    public State createState(StateTypeEnum stateType) {
        if (stateType == StateTypeEnum.RENDERER_STATE) {
            return getRendererState().animate(isActive, animationStart);
        }
        return null;
    }

    // Client
    @Override
    public void setState(StateTypeEnum stateType, State state) {
        if (stateType == StateTypeEnum.RENDERER_STATE) {
            this.animationStart = ((DestinyVentRenderState) state).animationStart;
        }
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        this.animationStart = compound.getLong("animationStart");
        super.readFromNBT(compound);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setLong("animationStart", animationStart);
        return super.writeToNBT(compound);
    }
}
