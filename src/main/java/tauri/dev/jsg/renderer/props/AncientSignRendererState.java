package tauri.dev.jsg.renderer.props;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.tileentity.props.AncientSignTile;

import java.nio.charset.StandardCharsets;

public class AncientSignRendererState extends State {
    public AncientSignRendererState(){}

    public String[] lines = AncientSignTile.getNewLines();
    public int color = 0xffffff;

    public AncientSignRendererState(String[] lines, int color){
        this.lines = lines;
        this.color = color;
    }


    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(color);
        for(int i = 0; i < AncientSignTile.LINES; i++){
            buf.writeInt(lines[i].length());
            buf.writeCharSequence(lines[i], StandardCharsets.UTF_8);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        color = buf.readInt();
        for(int i = 0; i < AncientSignTile.LINES; i++){
            int len = buf.readInt();
            lines[i] = buf.readCharSequence(len, StandardCharsets.UTF_8).toString();
        }
    }
}
