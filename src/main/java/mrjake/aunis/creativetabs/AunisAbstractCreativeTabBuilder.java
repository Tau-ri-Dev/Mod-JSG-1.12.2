package mrjake.aunis.creativetabs;

import mrjake.aunis.block.AunisBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public abstract class AunisAbstractCreativeTabBuilder extends CreativeTabs {
	public AunisAbstractCreativeTabBuilder(String label) {
		super(label);
	}
	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(AunisBlocks.STARGATE_MILKY_WAY_BASE_BLOCK);
	}
}
