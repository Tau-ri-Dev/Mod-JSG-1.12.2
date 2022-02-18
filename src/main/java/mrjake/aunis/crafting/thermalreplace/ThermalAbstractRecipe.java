package mrjake.aunis.crafting.thermalreplace;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

import java.util.HashMap;

public abstract class ThermalAbstractRecipe extends Impl<IRecipe> implements IRecipe {

    // CONFIGURATION
    public int MINIMAL_SLOTS;
    public int MAXIMAL_SLOTS;
    public String NAME;
    public ItemStack OUTPUT_ITEM;

    // items / patterns
    public HashMap<ItemStack, int[]> PATTERN_LIST;


    // CODE
    public ThermalAbstractRecipe(String name, int minSlots, int maxSlots, ItemStack outItem) {
        NAME = name;
        MINIMAL_SLOTS = minSlots;
        MAXIMAL_SLOTS = maxSlots;
        OUTPUT_ITEM = outItem;
        setRegistryName(NAME);
    }

    public void setPatterns(HashMap<ItemStack, int[]> patternList){
        PATTERN_LIST = patternList;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {

        int itemsCount = 0;
        int foundItems = 0;

        // calculate count of items
        for (int i = 0; i < MAXIMAL_SLOTS; i++) {
            for (ItemStack key : PATTERN_LIST.keySet()) {
                int[] slots = PATTERN_LIST.get(key);
                if (slots[i] == 1)
                    itemsCount++;
            }
        }

        // check slots
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (i >= MAXIMAL_SLOTS) return false;
            ItemStack stack = inv.getStackInSlot(i);
            Item item = stack.getItem();

            boolean found = false;
            for (ItemStack key : PATTERN_LIST.keySet()) {
                Item itemKey = key.getItem();
                int meta = key.getMetadata();
                int[] slots = PATTERN_LIST.get(key);
                if (itemKey == item && (meta == stack.getMetadata() || meta == -1) && slots[i] == 1) {
                    found = true;
                    break;
                }
            }
            if (!found && !stack.isEmpty())
                return false;
            else if (found)
                foundItems++;
        }
        return foundItems == itemsCount;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return OUTPUT_ITEM;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= MINIMAL_SLOTS;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return OUTPUT_ITEM;
    }
}
