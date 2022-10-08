package tauri.dev.jsg.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.item.JSGItems;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import tauri.dev.jsg.crafting.JSGRecipeHandler;

import java.util.ArrayList;
import java.util.List;

@JEIPlugin
public final class JEIIntegration implements IModPlugin {
    public JEIIntegration(){}

    @Override
    public void register(IModRegistry registry) {
        // Hide invisible blocks in JEI
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(JSGBlocks.INVISIBLE_BLOCK, 1, OreDictionary.WILDCARD_VALUE));
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(JSGBlocks.IRIS_BLOCK, 1, OreDictionary.WILDCARD_VALUE));

        // Hide Notebook from JEI
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(JSGItems.NOTEBOOK_ITEM, 1));

        // Hide ORI thing
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(JSGItems.CRYSTAL_GLYPH_ORI, 1));

        // Tab handling
        registry.addAdvancedGuiHandlers(new JEIAdvancedGuiHandler());

        List<IRecipeWrapper> recipes = new ArrayList<>();
        recipes.addAll(JEINotebookRecipe.genAll());
        recipes.add(new JEIUniverseDialerCloneRecipe());
        recipes.add(new JEINotebookCloneRecipe());
        recipes.add(new JEIUniverseDialerRepairRecipe());
        List<JEIThermalRecipes> thermalRecipes = JEIThermalRecipes.genAll();
        if(JSGRecipeHandler.convertRecipes() && thermalRecipes != null)
            recipes.addAll(thermalRecipes);

        registry.addRecipes(recipes, VanillaRecipeCategoryUid.CRAFTING);
    }
}
