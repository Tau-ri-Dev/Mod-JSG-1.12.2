package mrjake.aunis.gui.container.transportrings;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.config.ingame.AunisTileEntityConfig;
import mrjake.aunis.stargate.network.SymbolTypeEnum;
import mrjake.aunis.state.State;
import mrjake.aunis.transportrings.SymbolTypeTransportRingsEnum;
import mrjake.aunis.transportrings.TransportRingsAddress;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TRGuiState extends State {
	public TRGuiState() {}
	
	public Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> trAdddressMap;
	public AunisTileEntityConfig config;
	
	public TRGuiState(Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> adddressMap, AunisTileEntityConfig config) {
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

		config = new AunisTileEntityConfig(buf);
	}
}
