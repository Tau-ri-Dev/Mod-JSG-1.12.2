package mrjake.aunis.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.crafting.AunisRecipeHandler;
import mrjake.aunis.item.AunisItems;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

import static mrjake.aunis.crafting.AunisRecipeHandler.convertRecipes;

@JEIPlugin
public final class JEIIntegration implements IModPlugin {
    public JEIIntegration(){}

    @Override
    public void register(IModRegistry registry) {
        // Hide invisible blocks in JEI
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(AunisBlocks.INVISIBLE_BLOCK, 1, OreDictionary.WILDCARD_VALUE));
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(AunisBlocks.IRIS_BLOCK, 1, OreDictionary.WILDCARD_VALUE));

        // Hide Notebook from JEI
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(AunisItems.NOTEBOOK_ITEM, 1));

        // Tab handling
        registry.addAdvancedGuiHandlers(new JEIAdvancedGuiHandler());

        List<IRecipeWrapper> recipes = new ArrayList<>();
        recipes.addAll(JEINotebookRecipe.genAll());
        recipes.add(new JEIUniverseDialerCloneRecipe());
        recipes.add(new JEINotebookCloneRecipe());
        recipes.add(new JEIUniverseDialerRepairRecipe());
        List<JEIThermalRecipes> thermalRecipes = JEIThermalRecipes.genAll();
        if(convertRecipes())
            recipes.addAll(thermalRecipes);

        registry.addRecipes(recipes, VanillaRecipeCategoryUid.CRAFTING);
    }
}
