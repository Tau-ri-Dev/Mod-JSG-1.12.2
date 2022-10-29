package tauri.dev.jsg.state.energy;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.state.State;

public class ZPMHubRendererUpdate extends State {
    public ZPMHubRendererUpdate(){}

    public long animationStart;
    public boolean isAnimating;
    public boolean slidingUp;
    public int zpm1Level;
    public int zpm2Level;
    public int zpm3Level;

    public ZPMHubRendererUpdate(long animationStart, boolean isAnimating, boolean slidingUp, int zpm1Level, int zpm2Level, int zpm3Level){
        this.animationStart = animationStart;
        this.isAnimating = isAnimating;
        this.slidingUp = slidingUp;
        this.zpm1Level = zpm1Level;
        this.zpm2Level = zpm2Level;
        this.zpm3Level = zpm3Level;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(animationStart);
        buf.writeBoolean(isAnimating);
        buf.writeBoolean(slidingUp);
        buf.writeInt(zpm1Level);
        buf.writeInt(zpm2Level);
        buf.writeInt(zpm3Level);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.animationStart = buf.readLong();
        this.isAnimating = buf.readBoolean();
        this.slidingUp = buf.readBoolean();
        this.zpm1Level = buf.readInt();
        this.zpm2Level = buf.readInt();
        this.zpm3Level = buf.readInt();
    }
}
