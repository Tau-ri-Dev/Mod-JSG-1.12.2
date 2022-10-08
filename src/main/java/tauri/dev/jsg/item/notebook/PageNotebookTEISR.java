package tauri.dev.jsg.item.notebook;

import tauri.dev.jsg.item.JSGItems;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

public class PageNotebookTEISR extends TileEntityItemStackRenderer {
	
	@Override
	public void renderByItem(ItemStack itemStack) {
		if (itemStack.hasTagCompound()) {
			PageRenderer.renderByCompound(JSGItems.PAGE_NOTEBOOK_ITEM.getLastTransform(), itemStack.getTagCompound());
		}
	}
}
