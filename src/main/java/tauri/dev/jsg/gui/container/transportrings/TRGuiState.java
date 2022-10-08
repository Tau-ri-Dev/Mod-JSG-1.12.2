package tauri.dev.jsg.gui.container.transportrings;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.config.ingame.JSGTileEntityConfig;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.transportrings.SymbolTypeTransportRingsEnum;
import tauri.dev.jsg.transportrings.TransportRingsAddress;

import java.util.HashMap;
import java.util.Map;

public class TRGuiState extends State {
	public TRGuiState() {}
	
	public Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> trAdddressMap;
	public JSGTileEntityConfig config;
	
	public TRGuiState(Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> adddressMap, JSGTileEntityConfig config) {
		this.trAdddressMap = adddressMap;
		this.config = config;
	}

	@Override
	public void toBytes(ByteBuf buf) {		
		for (SymbolTypeTransportRingsEnum symbolType : SymbolTypeTransportRingsEnum.values()) {
			trAdddressMap.get(symbolType).toBytes(buf);
		}

		config.toBytes(buf);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		trAdddressMap = new HashMap<>();
		
		for (SymbolTypeTransportRingsEnum symbolType : SymbolTypeTransportRingsEnum.values()) {
			TransportRingsAddress address = new TransportRingsAddress(symbolType);
			address.fromBytes(buf);
			trAdddressMap.put(symbolType, address);
		}

		config = new JSGTileEntityConfig(buf);
	}
}
