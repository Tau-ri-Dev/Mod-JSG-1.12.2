package tauri.dev.jsg.tileentity.props;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import tauri.dev.jsg.sound.JSGSoundHelper;
import tauri.dev.jsg.sound.SoundEventEnum;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateTypeEnum;

import javax.annotation.Nonnull;

public class DestinyVentTile extends DestinyBearingTile {

    public float getAnimationStage(double tick){
        double time = Math.max((tick - animationStart - ANIMATION_DELAY_BEFORE), 0);
        float animationStage = 0;
        if (time <= (ANIMATION_DELAY_BETWEEN + OPEN_ANIMATION_LENGTH * 2))
            animationStage = (float) Math.max(0, Math.min(1, (Math.sin(Math.min((time / OPEN_ANIMATION_LENGTH), 1) * Math.PI) * ((float) ANIMATION_DELAY_BETWEEN / OPEN_ANIMATION_LENGTH))));

        return animationStage;
    }

    public static final int ANIMATION_DELAY_BEFORE = 10;
    public static final int OPEN_ANIMATION_LENGTH = 80;
    public static final int ANIMATION_DELAY_BETWEEN = 240;

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

    public boolean soundPlayed = false;

    @Override
    public DestinyVentRenderState getRendererState() {
        return rendererState;
    }

    @Override
    public void update() {
        super.update();

        if(!world.isRemote) {
            boolean isOpen = (getAnimationStage(world.getTotalWorldTime()) == 1);

            if (!isOpen && soundPlayed) {
                soundPlayed = false;
                markDirty();
            } else if(isOpen && !soundPlayed){
                soundPlayed = true;
                markDirty();
                JSGSoundHelper.playSoundEvent(world, pos, SoundEventEnum.DESTINY_BLASTER);
            }
        }
    }

    // CLIENT ONLY
    public void startAnimation() {
        this.animationStart = world.getTotalWorldTime();
        this.soundPlayed = false;
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
