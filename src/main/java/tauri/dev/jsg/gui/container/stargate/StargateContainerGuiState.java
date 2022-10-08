package tauri.dev.jsg.gui.container.stargate;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.config.ingame.JSGTileEntityConfig;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.state.State;

import java.util.HashMap;
import java.util.Map;

public class StargateContainerGuiState extends State {
	public StargateContainerGuiState() {}
	
	public Map<SymbolTypeEnum, StargateAddress> gateAdddressMap;
	public JSGTileEntityConfig config;
	
	public StargateContainerGuiState(Map<SymbolTypeEnum, StargateAddress> gateAdddressMap, JSGTileEntityConfig config) {
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

		config = new JSGTileEntityConfig(buf);
	}
}
