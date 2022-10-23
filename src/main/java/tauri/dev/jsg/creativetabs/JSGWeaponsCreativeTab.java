package tauri.dev.jsg.creativetabs;

import net.minecraft.item.ItemStack;
import tauri.dev.jsg.item.JSGItems;

public class JSGWeaponsCreativeTab extends JSGAbstractCreativeTab {

    public JSGWeaponsCreativeTab() {
        super("jsg_weapons");
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(JSGItems.ZAT);
    }
}
