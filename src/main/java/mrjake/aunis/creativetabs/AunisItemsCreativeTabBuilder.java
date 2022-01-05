package mrjake.aunis.creativetabs;

import mrjake.aunis.item.AunisItems;
import net.minecraft.item.ItemStack;

public class AunisItemsCreativeTabBuilder extends AunisAbstractCreativeTabBuilder {

	public AunisItemsCreativeTabBuilder() {
		super("aunis_items");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(AunisItems.NAQUADAH_ALLOY);
	}
}
