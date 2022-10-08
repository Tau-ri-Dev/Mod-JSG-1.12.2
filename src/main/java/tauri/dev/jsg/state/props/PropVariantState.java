package tauri.dev.jsg.state.props;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.state.State;

public class PropVariantState extends State {
    public int variant = 0;

    public PropVariantState() {
    }

    public PropVariantState(int variant) {
        this.variant = variant;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(variant);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        variant = buf.readInt();
    }
}
