package mrjake.aunis.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mrjake.aunis.item.AunisItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class JEIThermalRecipes implements IRecipeWrapper {

    // CONFIGURATION
    public ItemStack OUTPUT_ITEM;
    public HashMap<ItemStack, int[]> PATTERN_LIST;
    public ArrayList<ItemStack> INPUTS = new ArrayList<>();

    public static final int MAXIMAL_SLOTS = 9;

    public JEIThermalRecipes(ItemStack outItem, HashMap<ItemStack, int[]> patterns){
        OUTPUT_ITEM = outItem;
        PATTERN_LIST = patterns;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        if(PATTERN_LIST == null) return;
        for (int i = 0; i < MAXIMAL_SLOTS; i++) {

            for (ItemStack key : PATTERN_LIST.keySet()) {
                Item itemKey = key.getItem();
                int[] slots = PATTERN_LIST.get(key);
                if (slots[i] == 1) {
                    INPUTS.add(new ItemStack(itemKey));
                    break;
                }
            }
        }

        ingredients.setInputs(VanillaTypes.ITEM, INPUTS);
        ingredients.setOutput(VanillaTypes.ITEM, OUTPUT_ITEM);
    }
}
