package mrjake.aunis.creativetabs;

import mrjake.aunis.block.AunisBlocks;
import net.minecraft.item.ItemStack;

public class AunisGatesCreativeTabBuilder extends AunisAbstractCreativeTabBuilder {

	public AunisGatesCreativeTabBuilder() {
		super("aunis_gates");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(AunisBlocks.STARGATE_MILKY_WAY_BASE_BLOCK);
	}
}
