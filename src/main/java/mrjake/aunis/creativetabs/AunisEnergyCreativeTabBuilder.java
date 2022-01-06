package mrjake.aunis.creativetabs;

import mrjake.aunis.block.AunisBlocks;
import net.minecraft.item.ItemStack;

public class AunisEnergyCreativeTabBuilder extends AunisAbstractCreativeTabBuilder {

	public AunisEnergyCreativeTabBuilder() {
		super("aunis_energy");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(AunisBlocks.CAPACITOR_BLOCK);
	}
}
