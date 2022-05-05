package mrjake.aunis.creativetabs;

import mrjake.aunis.block.AunisBlocks;
import net.minecraft.item.ItemStack;

public class AunisEnergyCreativeTab extends AunisAbstractCreativeTab {

	public AunisEnergyCreativeTab() {
		super("aunis_energy");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(AunisBlocks.CAPACITOR_BLOCK);
	}
}
