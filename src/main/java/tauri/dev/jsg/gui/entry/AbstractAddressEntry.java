package tauri.dev.jsg.gui.entry;

import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.transportrings.SymbolTypeTransportRingsEnum;
import tauri.dev.jsg.transportrings.TransportRingsAddress;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumHand;

public abstract class AbstractAddressEntry extends AbstractEntry {
	
	protected SymbolTypeEnum stargateSymbolType;
	protected SymbolTypeTransportRingsEnum ringsSymbolType;
	protected StargateAddress stargateAddress;
	protected TransportRingsAddress ringsAddress;
	protected int maxSymbols;
	
	public AbstractAddressEntry(Minecraft mc, int index, int maxIndex, EnumHand hand, String name, ActionListener actionListener, SymbolTypeEnum stargateSymbolType, SymbolTypeTransportRingsEnum ringsSymbolType, StargateAddress stargateAddress, TransportRingsAddress ringsAddress, int maxSymbols) {
		super(mc, index, maxIndex, hand, name, actionListener);
		
		this.stargateSymbolType = stargateSymbolType;
		this.ringsSymbolType = ringsSymbolType;
		this.stargateAddress = stargateAddress;
		this.ringsAddress = ringsAddress;
		this.maxSymbols = maxSymbols;
	}

	public SymbolTypeEnum getStargateSymbolType() { return stargateSymbolType; }
	public SymbolTypeTransportRingsEnum getRingsSymbolType() { return ringsSymbolType; }
	public int getMaxSymbols() { return maxSymbols; }
	public StargateAddress getStargateAddress() { return stargateAddress; }
	public TransportRingsAddress getRingsAddress() { return ringsAddress; }
}
