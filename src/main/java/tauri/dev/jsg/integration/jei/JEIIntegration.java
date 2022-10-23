package tauri.dev.jsg.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.machine.AssemblerRecipes;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JEIPlugin
public final class JEIIntegration implements IModPlugin {
    public JEIIntegration() {
    }

    @Override
    public void register(IModRegistry registry) {
        // Hide invisible blocks in JEI
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(JSGBlocks.INVISIBLE_BLOCK, 1, OreDictionary.WILDCARD_VALUE));
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(JSGBlocks.IRIS_BLOCK, 1, OreDictionary.WILDCARD_VALUE));

        // Hide Notebook from JEI
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(JSGItems.NOTEBOOK_ITEM, 1));

        // Tab handling
        registry.addAdvancedGuiHandlers(new JEIAdvancedGuiHandler());

        List<IRecipeWrapper> recipes = new ArrayList<>(JEINotebookRecipe.genAll());
        recipes.add(new JEIUniverseDialerCloneRecipe());
        recipes.add(new JEINotebookCloneRecipe());
        recipes.add(new JEIUniverseDialerRepairRecipe());

        registry.addRecipes(recipes, VanillaRecipeCategoryUid.CRAFTING);

        recipes.clear();
        recipes.addAll(Arrays.asList(AssemblerRecipes.RECIPES));
        registry.addRecipes(recipes, JEIAssemblerRecipeCategory.UID);
    }

    @Override
    public void registerCategories(@Nonnull IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new JEIAssemblerRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }
}
