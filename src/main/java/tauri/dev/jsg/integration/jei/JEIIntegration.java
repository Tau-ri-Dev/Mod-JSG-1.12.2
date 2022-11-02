package tauri.dev.jsg.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.integration.jei.category.JEIAssemblerRecipeCategory;
import tauri.dev.jsg.integration.jei.category.JEIChamberRecipeCategory;
import tauri.dev.jsg.integration.jei.recipe.JEINotebookCloneRecipe;
import tauri.dev.jsg.integration.jei.recipe.JEINotebookRecipe;
import tauri.dev.jsg.integration.jei.recipe.JEIUniverseDialerCloneRecipe;
import tauri.dev.jsg.integration.jei.recipe.JEIUniverseDialerRepairRecipe;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.machine.assembler.AssemblerRecipe;
import tauri.dev.jsg.machine.assembler.AssemblerRecipes;
import tauri.dev.jsg.machine.chamber.CrystalChamberRecipe;
import tauri.dev.jsg.machine.chamber.CrystalChamberRecipes;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@JEIPlugin
public final class JEIIntegration implements IModPlugin {
    public JEIIntegration() {
    }

    @Override
    public void register(IModRegistry registry) {
        // Hide things in JEI
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(JSGBlocks.INVISIBLE_BLOCK, 1, OreDictionary.WILDCARD_VALUE));
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(JSGBlocks.IRIS_BLOCK, 1, OreDictionary.WILDCARD_VALUE));
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(JSGItems.NOTEBOOK_ITEM, 1));
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(JSGBlocks.ORE_NAQUADAH_BLOCK_STONE, 1));

        // Tab handling
        registry.addAdvancedGuiHandlers(new JEIAdvancedGuiHandler());

        List<IRecipeWrapper> recipes = new ArrayList<>(JEINotebookRecipe.genAll());

        // Crafting recipes with NBT tags (usually)
        recipes.add(new JEIUniverseDialerCloneRecipe());
        recipes.add(new JEINotebookCloneRecipe());
        recipes.add(new JEIUniverseDialerRepairRecipe());

        registry.addRecipes(recipes, VanillaRecipeCategoryUid.CRAFTING);

        recipes.clear();
        // Assembler recipes
        for(AssemblerRecipe recipe : AssemblerRecipes.RECIPES){
            AbstractJEIRecipe newRecipe = new AbstractJEIRecipe(){
                @Override
                public void getIngredients(@Nonnull IIngredients iIngredients) {
                    List<List<ItemStack>> list = new ArrayList<>();

                    list.add(Collections.singletonList(new ItemStack(recipe.getSchematic(), 1)));
                    for (ItemStack s : recipe.getPattern())
                        list.add(Collections.singletonList(s));
                    list.add(Collections.singletonList(recipe.getSubItemStack()));

                    iIngredients.setInputLists(VanillaTypes.ITEM, list);

                    iIngredients.setOutput(VanillaTypes.ITEM, recipe.getResult());
                }
            };
            recipes.add(newRecipe);
        }
        registry.addRecipes(recipes, JEIAssemblerRecipeCategory.UID);

        recipes.clear();
        // Chamber recipes
        for(CrystalChamberRecipe recipe : CrystalChamberRecipes.RECIPES){
            AbstractJEIRecipe newRecipe = new AbstractJEIRecipe(){
                @Override
                public FluidStack getSubFluidStack() {
                    return recipe.getSubFluidStack();
                }

                @Override
                public void getIngredients(@Nonnull IIngredients iIngredients) {
                    iIngredients.setInput(VanillaTypes.ITEM, new ItemStack(JSGItems.CRYSTAL_SEED, recipe.getNeededSeeds()));
                    iIngredients.setOutput(VanillaTypes.ITEM, recipe.getResult());
                }
            };
            recipes.add(newRecipe);
        }
        registry.addRecipes(recipes, JEIChamberRecipeCategory.UID);
    }

    @Override
    public void registerCategories(@Nonnull IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new JEIAssemblerRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(new JEIChamberRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }
}
