package mrjake.aunis.item;

import mrjake.aunis.Aunis;
import mrjake.aunis.creativetabs.AunisAbstractCreativeTabBuilder;
import net.minecraft.item.Item;

public class ItemHelper {

	public static Item createGenericItem(String name, AunisAbstractCreativeTabBuilder tab) {
		Item item = new Item();
		
		item.setRegistryName(Aunis.ModID + ":" + name);
		item.setUnlocalizedName(Aunis.ModID + "." + name);

		if(tab != null)
			item.setCreativeTab(tab);
		
		return item;
	}

}
