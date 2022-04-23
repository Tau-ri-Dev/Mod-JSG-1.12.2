package mrjake.aunis.gui.entry;

import mrjake.aunis.stargate.network.StargateAddress;
import mrjake.aunis.stargate.network.SymbolInterface;
import mrjake.aunis.transportrings.TransportRingsAddress;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

import java.util.ArrayList;
import java.util.List;

import static mrjake.aunis.gui.element.GuiHelper.isPointInRegion;
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

			if (isPointInRegion(dispx, dy, getAddressWidth(), e.getHeight(), mouseX, mouseY)) {
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
