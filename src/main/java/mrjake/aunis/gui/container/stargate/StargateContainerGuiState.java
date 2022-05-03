package mrjake.aunis.gui.container.stargate;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.config.ingame.AunisTileEntityConfig;
import mrjake.aunis.stargate.network.StargateAddress;
import mrjake.aunis.stargate.network.SymbolTypeEnum;
import mrjake.aunis.state.State;

import java.util.HashMap;
import java.util.Map;

public class StargateContainerGuiState extends State {
	public StargateContainerGuiState() {}
	
	public Map<SymbolTypeEnum, StargateAddress> gateAdddressMap;
	public AunisTileEntityConfig config;
	
	public StargateContainerGuiState(Map<SymbolTypeEnum, StargateAddress> gateAdddressMap, AunisTileEntityConfig config) {
		this.gateAdddressMap = gateAdddressMap;
		this.config = config;
	}

	@Override
	public void toBytes(ByteBuf buf) {		
		for (SymbolTypeEnum symbolType : SymbolTypeEnum.values()) {
			gateAdddressMap.get(symbolType).toBytes(buf);
		}

		config.toBytes(buf);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		gateAdddressMap = new HashMap<>(3);
		
		for (SymbolTypeEnum symbolType : SymbolTypeEnum.values()) {
			StargateAddress address = new StargateAddress(symbolType);
			address.fromBytes(buf);
			gateAdddressMap.put(symbolType, address);
		}

		config = new AunisTileEntityConfig(buf);
	}
}
