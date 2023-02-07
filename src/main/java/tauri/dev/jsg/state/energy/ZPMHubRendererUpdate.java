package tauri.dev.jsg.state.energy;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.renderer.zpm.ZPMRenderer;
import tauri.dev.jsg.state.State;

public class ZPMHubRendererUpdate extends State {
    public ZPMHubRendererUpdate(){}

    public long animationStart;
    public boolean isAnimating;
    public boolean slidingUp;

    public int zpm1Level;
    public int zpm2Level;
    public int zpm3Level;

    public ZPMRenderer.ZPMModelType zpm1Type = ZPMRenderer.ZPMModelType.NORMAL;
    public ZPMRenderer.ZPMModelType zpm2Type = ZPMRenderer.ZPMModelType.NORMAL;
    public ZPMRenderer.ZPMModelType zpm3Type = ZPMRenderer.ZPMModelType.NORMAL;

    public float facing;

    public ZPMHubRendererUpdate(long animationStart, boolean isAnimating, boolean slidingUp, int zpm1Level, int zpm2Level, int zpm3Level, ZPMRenderer.ZPMModelType zpm1Type, ZPMRenderer.ZPMModelType zpm2Type, ZPMRenderer.ZPMModelType zpm3Type, float facing){
        this.animationStart = animationStart;
        this.isAnimating = isAnimating;
        this.slidingUp = slidingUp;

        this.zpm1Level = zpm1Level;
        this.zpm2Level = zpm2Level;
        this.zpm3Level = zpm3Level;

        this.zpm1Type = zpm1Type;
        this.zpm2Type = zpm2Type;
        this.zpm3Type = zpm3Type;

        this.facing = facing;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(animationStart);
        buf.writeBoolean(isAnimating);
        buf.writeBoolean(slidingUp);

        buf.writeInt(zpm1Level);
        buf.writeInt(zpm2Level);
        buf.writeInt(zpm3Level);

        buf.writeInt(zpm1Type.id);
        buf.writeInt(zpm2Type.id);
        buf.writeInt(zpm3Type.id);

        buf.writeFloat(facing);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.animationStart = buf.readLong();
        this.isAnimating = buf.readBoolean();
        this.slidingUp = buf.readBoolean();

        this.zpm1Level = buf.readInt();
        this.zpm2Level = buf.readInt();
        this.zpm3Level = buf.readInt();

        this.zpm1Type = ZPMRenderer.ZPMModelType.byId(buf.readInt());
        this.zpm2Type = ZPMRenderer.ZPMModelType.byId(buf.readInt());
        this.zpm3Type = ZPMRenderer.ZPMModelType.byId(buf.readInt());

        this.facing = buf.readFloat();
    }
}
