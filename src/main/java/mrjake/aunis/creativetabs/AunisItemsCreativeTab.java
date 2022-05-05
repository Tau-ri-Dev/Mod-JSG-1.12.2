package mrjake.aunis.creativetabs;

import mrjake.aunis.item.AunisItems;
import net.minecraft.item.ItemStack;

public class AunisItemsCreativeTab extends AunisAbstractCreativeTab {

	public AunisItemsCreativeTab() {
		super("aunis_items");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(AunisItems.NAQUADAH_ALLOY);
	}
}
