package mrjake.aunis.creativetabs;

import mrjake.aunis.block.AunisBlocks;
import net.minecraft.item.ItemStack;

public class AunisPropsCreativeTab extends AunisAbstractCreativeTab {

    public AunisPropsCreativeTab() {
        super("aunis_props");
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(AunisBlocks.TR_PLATFORM_BLOCK);
    }
}
