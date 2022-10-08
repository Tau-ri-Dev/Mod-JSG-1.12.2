package tauri.dev.jsg.creativetabs;

import tauri.dev.jsg.item.JSGItems;
import net.minecraft.item.ItemStack;

public class JSGItemsCreativeTab extends JSGAbstractCreativeTab {

	public JSGItemsCreativeTab() {
		super("jsg_items");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(JSGItems.NAQUADAH_ALLOY);
	}
}
