package mrjake.aunis.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mrjake.aunis.Aunis;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.crafting.thermalreplace.CrystalRedRecipe;
import mrjake.aunis.crafting.thermalreplace.ThermalAbstractRecipe;
import mrjake.aunis.item.AunisItems;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

@JEIPlugin
public final class JEIIntegration implements IModPlugin {

    public static final CrystalRedRecipe CRYSTAL_RED_RECIPE = new CrystalRedRecipe();

    public static final ThermalAbstractRecipe[] RECIPES = {
            CRYSTAL_RED_RECIPE
    };

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

        for(ThermalAbstractRecipe recipe : RECIPES){
            recipes.add(new JEIThermalRecipes(recipe.OUTPUT_ITEM, recipe.PATTERN_LIST));
        }

        registry.addRecipes(recipes, VanillaRecipeCategoryUid.CRAFTING);
    }
}
