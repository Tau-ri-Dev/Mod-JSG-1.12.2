package tauri.dev.jsg.gui.entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.util.Constants.NBT;
import tauri.dev.jsg.item.notebook.PageNotebookItem;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.transportrings.SymbolTypeTransportRingsEnum;
import tauri.dev.jsg.transportrings.TransportRingsAddress;

/**
 * Class handles universal screen shown when editing Notebook or Universe Dialer
 * saved addresses.
 * 
 * @author MrJake222
 * 
 */
public class NotebookEntryChangeGui extends AbstractAddressEntryChangeGui {

	public NotebookEntryChangeGui(EnumHand hand, NBTTagCompound compound) {
		super(hand, compound);
	}

	@Override
	protected void generateEntries() {
		if(mainCompound == null) return;
		NBTTagList list = mainCompound.getTagList("addressList", NBT.TAG_COMPOUND);
		
		for (int i=0; i<list.tagCount(); i++) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			
			String name = PageNotebookItem.getNameFromCompound(compound);
			NotebookEntry entry;
			if(compound.hasKey("transportRings")){
				SymbolTypeTransportRingsEnum symbolType = SymbolTypeTransportRingsEnum.valueOf(compound.getInteger("symbolType"));
				TransportRingsAddress address = new TransportRingsAddress(compound.getCompoundTag("address"));

				entry = new NotebookEntry(mc, i, list.tagCount(), hand, name, (action, index) -> performAction(action, index), null, symbolType, null, address, address.size());
			}
			else {
				SymbolTypeEnum symbolType = SymbolTypeEnum.valueOf(compound.getInteger("symbolType"));
				StargateAddress stargateAddress = new StargateAddress(compound.getCompoundTag("address"));
				int maxSymbols = symbolType.getMaxSymbolsDisplay(compound.getBoolean("hasUpgrade"));

				entry = new NotebookEntry(mc, i, list.tagCount(), hand, name, (action, index) -> performAction(action, index), symbolType, null, stargateAddress, null, maxSymbols);
			}
			entries.add(entry);
		}
	}

	@Override
	protected void generateSections() {
		sections.add(new Section(NotebookEntry.ADDRESS_WIDTH, "item.jsg.gui.address"));
		sections.add(new Section(100, "item.jsg.gui.name"));
		sections.add(new Section(NotebookEntry.BUTTON_COUNT*25 - 5, ""));
	}

	@Override
	protected int getEntryBottomMargin() {
		return 2;
	}

	@Override
	public int getAddressWidth(){ return NotebookEntry.ADDRESS_WIDTH; }
}
