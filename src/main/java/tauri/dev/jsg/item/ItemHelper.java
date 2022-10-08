package tauri.dev.jsg.item;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.creativetabs.JSGAbstractCreativeTab;
import net.minecraft.item.Item;

public class ItemHelper {

	public static Item createGenericItem(String name, JSGAbstractCreativeTab tab) {
		Item item = new Item();
		
		item.setRegistryName(JSG.MOD_ID + ":" + name);
		item.setUnlocalizedName(JSG.MOD_ID + "." + name);

		if(tab != null)
			item.setCreativeTab(tab);
		
		return item;
	}

}
