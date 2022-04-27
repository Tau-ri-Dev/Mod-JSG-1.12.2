package mrjake.aunis.creativetabs;

import mrjake.aunis.item.AunisItems;
import net.minecraft.item.ItemStack;

public class AunisToolsCreativeTabBuilder extends AunisAbstractCreativeTabBuilder {

    public AunisToolsCreativeTabBuilder() {
        super("aunis_tools");
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(AunisItems.ZAT);
    }
}
