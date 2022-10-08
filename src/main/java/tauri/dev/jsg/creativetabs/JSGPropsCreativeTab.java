package tauri.dev.jsg.creativetabs;

import tauri.dev.jsg.block.JSGBlocks;
import net.minecraft.item.ItemStack;

public class JSGPropsCreativeTab extends JSGAbstractCreativeTab {

    public JSGPropsCreativeTab() {
        super("jsg_props");
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(JSGBlocks.TR_PLATFORM_BLOCK);
    }
}
