package tauri.dev.jsg.creativetabs;

import tauri.dev.jsg.block.JSGBlocks;
import net.minecraft.item.ItemStack;

public class JSGOresCreativeTab extends JSGAbstractCreativeTab {

	public JSGOresCreativeTab() {
		super("jsg_ores");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(JSGBlocks.ORE_NAQUADAH_BLOCK);
	}
}
