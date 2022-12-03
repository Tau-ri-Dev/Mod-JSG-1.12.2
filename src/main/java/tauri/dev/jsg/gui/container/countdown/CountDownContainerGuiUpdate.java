package tauri.dev.jsg.gui.container.countdown;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.config.ingame.JSGTileEntityConfig;
import tauri.dev.jsg.state.State;

public class CountDownContainerGuiUpdate extends State {
    public JSGTileEntityConfig config;

    public CountDownContainerGuiUpdate(){}

    public CountDownContainerGuiUpdate(JSGTileEntityConfig config){
        this.config = config;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        config.toBytes(buf);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        config = new JSGTileEntityConfig(buf);
    }
}
