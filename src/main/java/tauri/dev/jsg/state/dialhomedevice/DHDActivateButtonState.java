package tauri.dev.jsg.state.dialhomedevice;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.state.State;

public class DHDActivateButtonState extends State {
	public DHDActivateButtonState() {}
	
	public int symbol;
	public boolean clearAll = false;
	public boolean deactivate = false;

	public DHDActivateButtonState(boolean clearAll) {
		this.clearAll = clearAll;
	}
	
	public DHDActivateButtonState(SymbolInterface symbol) {
		this.symbol = symbol.getId();
	}
	public DHDActivateButtonState(int symbolId, boolean deactivate) {
		this.symbol = symbolId;
		this.deactivate = deactivate;
	}

    @Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(clearAll);
		
		if (!clearAll) {
			buf.writeInt(symbol);
		}
		buf.writeBoolean(deactivate);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		clearAll = buf.readBoolean();
		
		if (!clearAll) {
			symbol = buf.readInt();
		}
		deactivate = buf.readBoolean();
	}
}
