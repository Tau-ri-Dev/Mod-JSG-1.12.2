package tauri.dev.jsg.renderer.props;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.state.State;

public class DestinyCountDownRendererState extends State {
    public long countdownTo = 0;

    public DestinyCountDownRendererState(){}
    public DestinyCountDownRendererState(long countdownTo){
        this.countdownTo = countdownTo;
    }

    /**
     * Should write all parameters that matter to client-side renderer(ex. vortexState in StargateRenderer)
     * to a ByteBuf.
     * <p>
     * Data should be put and read in the same order!
     *
     * @param buf - Buffer object you write into.
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(countdownTo);
    }

    /**
     * Should set all parameters that matter to client-side renderer(ex. vortexState in StargateRenderer)
     *
     * @param buf - Buffer object you read from.
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        countdownTo = buf.readLong();
    }
}
