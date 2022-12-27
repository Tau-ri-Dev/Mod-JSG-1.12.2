package tauri.dev.jsg.gui.entry;

import tauri.dev.jsg.packet.gui.entry.EntryDataTypeEnum;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.stargate.network.SymbolUniverseEnum;
import tauri.dev.jsg.transportrings.SymbolAncientEnum;
import tauri.dev.jsg.transportrings.SymbolGoauldEnum;
import tauri.dev.jsg.transportrings.SymbolTypeTransportRingsEnum;
import tauri.dev.jsg.transportrings.TransportRingsAddress;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumHand;

public class NotebookEntry extends AbstractAddressEntry {
	
	public static final int ADDRESS_WIDTH = 160;
	public static final int BUTTON_COUNT = 3;
	
	public NotebookEntry(Minecraft mc, int index, int maxIndex, EnumHand hand, String name, ActionListener reloadListener, SymbolTypeEnum type, SymbolTypeTransportRingsEnum ringsType, StargateAddress addr, TransportRingsAddress ringsAddr, int maxSymbols) {
		super(mc, index, maxIndex, hand, name, reloadListener, type, ringsType, addr,  ringsAddr, maxSymbols);
	}
	
	@Override
	public void renderAt(int dx, int dy, int mouseX, int mouseY, float partialTicks) {
		final int size = 20;
		int sizeX = size;
		if(stargateAddress != null && stargateAddress.getSymbolType() == SymbolTypeEnum.UNIVERSE)
			sizeX /= 2;
		if(ringsAddress != null && ringsAddress.getSymbolType() == SymbolTypeTransportRingsEnum.ANCIENT)
			sizeX /= 2;

		int x = dx+(ADDRESS_WIDTH-sizeX*(maxSymbols))/2;
		
		for (int i=0; i<maxSymbols; i++) {
			SymbolInterface symbol = null;
			if(stargateAddress != null)
				symbol = stargateAddress.get(i);
			else if(ringsAddress != null)
				symbol = ringsAddress.get(i);

			if(symbol != null)
				renderSymbol(x, dy, sizeX, size, mouseX, mouseY, symbol);
			x += sizeX;
		}
		super.renderAt(dx+ADDRESS_WIDTH+10, dy, mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected int getHeight() {
		return 20;
	}
	
	@Override
	protected int getMaxNameLength() {
		return 11;
	}
	
	@Override
	protected EntryDataTypeEnum getEntryDataType() {
		return EntryDataTypeEnum.PAGE;
	}
}
