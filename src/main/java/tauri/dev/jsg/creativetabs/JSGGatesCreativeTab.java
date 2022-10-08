package tauri.dev.jsg.creativetabs;

import tauri.dev.jsg.block.JSGBlocks;
import net.minecraft.item.ItemStack;

public class JSGGatesCreativeTab extends JSGAbstractCreativeTab {

	public JSGGatesCreativeTab() {
		super("jsg_gates");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(JSGBlocks.STARGATE_MILKY_WAY_BASE_BLOCK);
	}
}
