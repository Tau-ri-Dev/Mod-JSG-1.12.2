package mrjake.aunis.state;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.stargate.network.SymbolMilkyWayEnum;
import mrjake.aunis.stargate.network.SymbolPegasusEnum;

public class DHDActivateButtonPegasusState extends State {
	public DHDActivateButtonPegasusState() {}

	public SymbolPegasusEnum symbol;
	public boolean clearAll = false;

	public DHDActivateButtonPegasusState(boolean clearAll) {
		this.clearAll = clearAll;
	}

	public DHDActivateButtonPegasusState(SymbolPegasusEnum symbol) {
		this.symbol = symbol;
	}

    @Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(clearAll);
		
		if (!clearAll) {
			buf.writeInt(symbol.getId());
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		clearAll = buf.readBoolean();
		
		if (!clearAll) {
			symbol = SymbolPegasusEnum.valueOf(buf.readInt());
		}
	}
}
