package tauri.dev.jsg.crafting;

import tauri.dev.jsg.item.JSGItems;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * @author matousss
 */
public class UniverseDialerRepairRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    public UniverseDialerRepairRecipe() {
        setRegistryName("dialer_repair");
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {

        boolean dialer = false;
        boolean glass = false;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            Item item = stack.getItem();

            if (item == JSGItems.UNIVERSE_DIALER && stack.getMetadata() == 1) {
                if (!dialer) dialer = true;
                else return false;
            }
            else if (item == Item.getItemFromBlock(Blocks.GLASS)) {
                if (!glass) glass = true;
                else return false;

            } else if (!stack.isEmpty())
                return false;
        }

        return dialer && glass && true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return new ItemStack(JSGItems.UNIVERSE_DIALER);
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(JSGItems.UNIVERSE_DIALER);
    }
}
