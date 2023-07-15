package tauri.dev.jsg.integration.jei.recipe;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;

import java.util.Arrays;

public class JEIDialerPageRecipe implements IRecipeWrapper {

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(JEINotebookRecipe.PAGE3, JEIUniverseDialerCloneRecipe.DIALER1));
        ingredients.setOutput(VanillaTypes.ITEM, JEIUniverseDialerCloneRecipe.DIALER_OUT_PAGE);
    }
}
