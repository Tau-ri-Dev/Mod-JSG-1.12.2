package tauri.dev.jsg.creativetabs;

import tauri.dev.jsg.item.JSGItems;
import net.minecraft.item.ItemStack;

public class JSGToolsCreativeTab extends JSGAbstractCreativeTab {

    public JSGToolsCreativeTab() {
        super("jsg_tools");
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(JSGItems.JSG_SCREWDRIVER);
    }
}
