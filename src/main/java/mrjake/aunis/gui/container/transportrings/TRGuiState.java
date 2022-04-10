package mrjake.aunis.gui.container.transportrings;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.stargate.network.StargateAddress;
import mrjake.aunis.stargate.network.SymbolTypeEnum;
import mrjake.aunis.state.State;

import java.util.HashMap;
import java.util.Map;

public class TRGuiState extends State {
	public TRGuiState() {}
	
	public Map<SymbolTypeEnum, StargateAddress> gateAdddressMap;
	
	public TRGuiState(Map<SymbolTypeEnum, StargateAddress> gateAdddressMap) {
		this.gateAdddressMap = gateAdddressMap;
	}

	@Override
	public void toBytes(ByteBuf buf) {		
		for (SymbolTypeEnum symbolType : SymbolTypeEnum.values()) {
			gateAdddressMap.get(symbolType).toBytes(buf);
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		gateAdddressMap = new HashMap<>(3);
		
		for (SymbolTypeEnum symbolType : SymbolTypeEnum.values()) {
			StargateAddress address = new StargateAddress(symbolType);
			address.fromBytes(buf);
			gateAdddressMap.put(symbolType, address);
		}
	}
}
