package mrjake.aunis.creativetabs;

import mrjake.aunis.block.AunisBlocks;
import net.minecraft.item.ItemStack;

public class AunisOresCreativeTabBuilder extends AunisAbstractCreativeTabBuilder {

	public AunisOresCreativeTabBuilder() {
		super("aunis_ores");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(AunisBlocks.ORE_NAQUADAH_BLOCK);
	}
}
