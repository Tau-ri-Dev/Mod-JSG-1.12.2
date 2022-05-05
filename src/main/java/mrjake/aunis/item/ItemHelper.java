package mrjake.aunis.item;

import mrjake.aunis.Aunis;
import mrjake.aunis.creativetabs.AunisAbstractCreativeTab;
import net.minecraft.item.Item;

public class ItemHelper {

	public static Item createGenericItem(String name, AunisAbstractCreativeTab tab) {
		Item item = new Item();
		
		item.setRegistryName(Aunis.MOD_ID + ":" + name);
		item.setUnlocalizedName(Aunis.MOD_ID + "." + name);

		if(tab != null)
			item.setCreativeTab(tab);
		
		return item;
	}

}
