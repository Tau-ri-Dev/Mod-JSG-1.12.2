package tauri.dev.jsg.creativetabs;

import tauri.dev.jsg.block.JSGBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public abstract class JSGAbstractCreativeTab extends CreativeTabs {
	public JSGAbstractCreativeTab(String label) {
		super(label);
	}
	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(JSGBlocks.STARGATE_MILKY_WAY_BASE_BLOCK);
	}
}
