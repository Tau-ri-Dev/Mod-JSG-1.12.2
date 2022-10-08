package tauri.dev.jsg.state.beamer;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.beamer.BeamerRendererAction;
import tauri.dev.jsg.state.State;

public class BeamerRendererActionState extends State {
	public BeamerRendererActionState() {}
	
	public BeamerRendererAction action;
	
	public BeamerRendererActionState(BeamerRendererAction action) {
		this.action = action;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(action.id);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		action = BeamerRendererAction.valueOf(buf.readInt());
	}
}
