package mrjake.aunis.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mrjake.aunis.crafting.AunisRecipeHandler;
import mrjake.aunis.crafting.thermalreplace.ThermalAbstractRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JEIThermalRecipes implements IRecipeWrapper {

    // CONFIGURATION
    public ItemStack OUTPUT_ITEM;
    public HashMap<ItemStack, int[]> PATTERN_LIST;
    public Map<Integer, ItemStack> INPUTS = new HashMap<>();

    public static final int MAXIMAL_SLOTS = 9;

    public JEIThermalRecipes(ItemStack outItem, HashMap<ItemStack, int[]> patterns){
        OUTPUT_ITEM = outItem;
        PATTERN_LIST = patterns;
        if(PATTERN_LIST == null) return;
        for (int i = 0; i < MAXIMAL_SLOTS; i++) {

            for (ItemStack key : PATTERN_LIST.keySet()) {
                int[] slots = PATTERN_LIST.get(key);
                if (slots[i] == 1) {
                    INPUTS.put(i, key);
                    break;
                }
            }

            if(!INPUTS.containsKey(i))
                INPUTS.put(i, ItemStack.EMPTY);
        }
    }

    public static List<JEIThermalRecipes> genAll() {
        List<JEIThermalRecipes> list = new ArrayList<>();
        for (ThermalAbstractRecipe recipe : AunisRecipeHandler.RECIPES) {
            list.add(new JEIThermalRecipes(recipe.OUTPUT_ITEM, recipe.PATTERN_LIST));
        }
        return list;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, new ArrayList<>(INPUTS.values()));
        ingredients.setOutput(VanillaTypes.ITEM, OUTPUT_ITEM);
    }
}
