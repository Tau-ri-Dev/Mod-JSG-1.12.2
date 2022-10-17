package tauri.dev.jsg.renderer.machine;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import tauri.dev.jsg.machine.ArmPos;
import tauri.dev.jsg.state.State;

public class StargateAssemblerRendererState extends State {
    public StargateAssemblerRendererState() {}
    public StargateAssemblerRendererState(boolean isWorking, long animationStart, float animationLength) {
        this.isWorking = isWorking;
        this.animationStart = animationStart;
        this.animationLength = animationLength;
    }

    public StargateAssemblerRendererState initClient(BlockPos pos) {
        this.pos = pos;
        return this;
    }

    public StargateAssemblerRendererState startAnimation(long animationStart, float animationLength, ArmPos[] positions) {
        this.animationStart = animationStart;
        this.animationLength = animationLength;
        this.isWorking = true;
        this.positions = positions;
        return this;
    }

    public StargateAssemblerRendererState stopAnimation(){
        this.animationStart = -1;
        this.animationLength = -1;
        this.isWorking = false;
        return this;
    }

    public BlockPos pos;
    public ArmPos[] positions = {};


    public long animationStart = -1;
    public float animationLength = -1;
    public boolean isWorking;

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(animationStart);
        buf.writeFloat(animationLength);

        buf.writeBoolean(isWorking);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        animationStart = buf.readLong();
        animationLength = buf.readFloat();

        isWorking = buf.readBoolean();
    }
}
