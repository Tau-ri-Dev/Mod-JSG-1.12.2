package tauri.dev.jsg.state.stargate;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.stargate.network.SymbolUniverseEnum;
import tauri.dev.jsg.state.State;

public class StargateUniverseSymbolState extends State {
	public StargateUniverseSymbolState() {}
	
	public SymbolUniverseEnum symbol;
	public boolean dimAll;
	
	public StargateUniverseSymbolState(SymbolUniverseEnum symbol, boolean dimAll) {
		this.symbol = symbol;
		this.dimAll = dimAll;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(symbol.id);
		buf.writeBoolean(dimAll);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		symbol = SymbolUniverseEnum.valueOf(buf.readInt());
		dimAll = buf.readBoolean();
	}

}
