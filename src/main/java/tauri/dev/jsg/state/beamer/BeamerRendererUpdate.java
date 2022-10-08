package tauri.dev.jsg.state.beamer;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.beamer.BeamerStatusEnum;
import tauri.dev.jsg.state.State;

public class BeamerRendererUpdate extends State {
	public BeamerRendererUpdate() {}
	
	public BeamerStatusEnum beamerStatus;

	public BeamerRendererUpdate(BeamerStatusEnum beamerStatus) {
		this.beamerStatus = beamerStatus;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(beamerStatus.getKey());
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		beamerStatus = BeamerStatusEnum.valueOf(buf.readInt());
	}

}
