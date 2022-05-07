package mrjake.aunis.crafting.thermalreplace;

import mrjake.aunis.Aunis;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

import javax.annotation.Nonnull;
import java.util.HashMap;

/**
 * TRC - Thermal Recipe Convertor
 *
 * @author MineDragonCZ_
 */

public abstract class ThermalAbstractRecipe extends Impl<IRecipe> implements IRecipe {

    // CONFIGURATION
    public final int MINIMAL_SLOTS;
    public final int MAXIMAL_SLOTS;
    public final String NAME;
    public final ItemStack OUTPUT_ITEM;

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

    public void setPatterns(HashMap<ItemStack, int[]> patternList) {
        PATTERN_LIST = patternList;
    }

    public int getSlot(int x, int y, int columns) {
        return y * columns + x;
    }

    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {

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

        int height = inv.getHeight();
        int width = inv.getWidth();
        int sqrt = (int) Math.floor(Math.sqrt(MAXIMAL_SLOTS));
        int patternWidth = 0;
        int patternHeight = 0;
        int itemsCountTemp = 0;
        int itemsInCrafting = 0;
        for(int i = 0; i < inv.getSizeInventory(); i++)
            itemsInCrafting += inv.getStackInSlot(i).isEmpty() ? 0 : 1;

        for (ItemStack key : PATTERN_LIST.keySet()) {
            int[] slots = PATTERN_LIST.get(key);
            for (int p = 0; p < slots.length; p++) {
                int y2 = p / sqrt;
                int x2 = p - (sqrt * y2);
                if (slots[p] == 1) {
                    itemsCountTemp++;
                    if (patternWidth < x2)
                        patternWidth = x2;
                    if (patternHeight < y2)
                        patternHeight = y2;
                }
            }
        }

        for (int z = 0; z < MAXIMAL_SLOTS; z++) {
            int itemsFoundTemp = 0;
            for (ItemStack key : PATTERN_LIST.keySet()) {
                Item itemKey = key.getItem();
                int meta = key.getMetadata();
                int[] slots = PATTERN_LIST.get(key);
                // try offsets
                for (int p = 0; p < slots.length; p++) {
                    int slot = p + z;
                    int y2 = slot / sqrt;
                    int x2 = slot - (sqrt * y2);

                    if (x2 >= width || y2 >= height) continue;

                    int s = getSlot(x2, y2, width);

                    ItemStack stack = inv.getStackInSlot(s);
                    Item item = stack.getItem();
                    if (slots[p] == 1) {
                        if (itemKey == item && (meta == stack.getMetadata() || meta == -1)) {
                            itemsFoundTemp++;
                        }
                    }
                }
                if (itemsCountTemp == itemsFoundTemp) {
                    foundItems += itemsFoundTemp;
                }
            }
        }
        return foundItems == itemsCount && foundItems == itemsInCrafting;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
        return new ItemStack(OUTPUT_ITEM.getItem());
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= MINIMAL_SLOTS;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(OUTPUT_ITEM.getItem());
    }
}
