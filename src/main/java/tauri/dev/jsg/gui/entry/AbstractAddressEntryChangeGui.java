package tauri.dev.jsg.gui.entry;

import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.transportrings.TransportRingsAddress;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import tauri.dev.jsg.gui.element.GuiHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAddressEntryChangeGui extends AbstractEntryChangeGui {

	public AbstractAddressEntryChangeGui(EnumHand hand, NBTTagCompound compound) {
		super(hand, compound);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		// render tooltips
		for (AbstractEntry entry : entries) {
			if(!(entry instanceof AbstractAddressEntry)) break;
			AbstractAddressEntry e = (AbstractAddressEntry) entry;

			int maxSymbols = e.getMaxSymbols();
			int dy = e.entryY;

			if (GuiHelper.isPointInRegion(dispx, dy, getAddressWidth(), e.getHeight(), mouseX, mouseY)) {
				List<String> text = new ArrayList<>();
				StargateAddress stargateAddress = e.getStargateAddress();
				TransportRingsAddress ringsAddress = e.getRingsAddress();
				for (int i = 0; i < maxSymbols; i++) {
					SymbolInterface symbol = null;
					if(stargateAddress != null)
						symbol = stargateAddress.get(i);
					else if(ringsAddress != null)
						symbol = ringsAddress.get(i);
					if(symbol != null)
						text.add(symbol.getEnglishName());
				}
				drawHoveringText(text, mouseX + 5, mouseY);
			}
		}
	}

	abstract public int getAddressWidth();
}
