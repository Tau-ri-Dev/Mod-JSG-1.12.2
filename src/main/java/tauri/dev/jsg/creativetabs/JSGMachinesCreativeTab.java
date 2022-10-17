package tauri.dev.jsg.creativetabs;

import net.minecraft.item.ItemStack;
import tauri.dev.jsg.item.JSGItems;

public class JSGMachinesCreativeTab extends JSGAbstractCreativeTab {

    public JSGMachinesCreativeTab() {
        super("jsg_machines");
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(JSGItems.DHD_BBB);
    }
}
