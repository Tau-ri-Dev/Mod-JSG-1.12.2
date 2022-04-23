package mrjake.aunis.gui.entry;

import mrjake.aunis.stargate.network.StargateAddress;
import mrjake.aunis.stargate.network.SymbolTypeEnum;
import mrjake.aunis.transportrings.SymbolTypeTransportRingsEnum;
import mrjake.aunis.transportrings.TransportRingsAddress;
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
