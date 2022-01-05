package mrjake.aunis.creativetabs;

import mrjake.aunis.block.AunisBlocks;
import net.minecraft.item.ItemStack;

public class AunisRingsCreativeTabBuilder extends AunisAbstractCreativeTabBuilder {

	public AunisRingsCreativeTabBuilder() {
		super("aunis_rings");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(AunisBlocks.TRANSPORT_RINGS_BLOCK);
	}
}
