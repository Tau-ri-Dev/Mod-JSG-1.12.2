package mrjake.aunis.creativetabs;

import mrjake.aunis.block.AunisBlocks;
import net.minecraft.item.ItemStack;

public class AunisRingsCreativeTab extends AunisAbstractCreativeTab {

	public AunisRingsCreativeTab() {
		super("aunis_rings");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(AunisBlocks.TRANSPORT_RINGS_GOAULD_BLOCK);
	}
}
