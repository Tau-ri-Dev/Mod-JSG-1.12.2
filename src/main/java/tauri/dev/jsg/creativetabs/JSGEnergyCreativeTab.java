package tauri.dev.jsg.creativetabs;

import tauri.dev.jsg.block.JSGBlocks;
import net.minecraft.item.ItemStack;

public class JSGEnergyCreativeTab extends JSGAbstractCreativeTab {

	public JSGEnergyCreativeTab() {
		super("jsg_energy");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(JSGBlocks.CAPACITOR_BLOCK);
	}
}
