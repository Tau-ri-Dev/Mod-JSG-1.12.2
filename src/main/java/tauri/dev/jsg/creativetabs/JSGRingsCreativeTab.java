package tauri.dev.jsg.creativetabs;

import tauri.dev.jsg.block.JSGBlocks;
import net.minecraft.item.ItemStack;

public class JSGRingsCreativeTab extends JSGAbstractCreativeTab {

	public JSGRingsCreativeTab() {
		super("jsg_rings");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(JSGBlocks.TRANSPORT_RINGS_GOAULD_BLOCK);
	}
}
