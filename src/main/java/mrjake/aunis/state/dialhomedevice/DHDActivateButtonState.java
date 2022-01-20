package mrjake.aunis.state.dialhomedevice;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.stargate.network.SymbolInterface;
import mrjake.aunis.stargate.network.SymbolMilkyWayEnum;
import mrjake.aunis.state.State;

public class DHDActivateButtonState extends State {
	public DHDActivateButtonState() {}
	
	public int symbol;
	public boolean clearAll = false;

	public DHDActivateButtonState(boolean clearAll) {
		this.clearAll = clearAll;
	}
	
	public DHDActivateButtonState(SymbolInterface symbol) {
		this.symbol = symbol.getId();
	}

    @Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(clearAll);
		
		if (!clearAll) {
			buf.writeInt(symbol);
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		clearAll = buf.readBoolean();
		
		if (!clearAll) {
			symbol = buf.readInt();
		}
	}
}
