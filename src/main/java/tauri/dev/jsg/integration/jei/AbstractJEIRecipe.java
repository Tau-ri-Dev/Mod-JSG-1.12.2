package tauri.dev.jsg.integration.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraftforge.fluids.FluidStack;

public abstract class AbstractJEIRecipe implements IRecipeWrapper {
    public FluidStack getSubFluidStack(){
        return null;
    }
}
