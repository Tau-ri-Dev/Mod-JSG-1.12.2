package tauri.dev.jsg.machine.chamber;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tauri.dev.jsg.item.JSGItems;

import javax.annotation.Nonnull;

public abstract class CrystalChamberRecipe implements IRecipeWrapper {
    public abstract int getWorkingTime();

    public abstract FluidStack getSubFluidStack();

    public abstract ItemStack getResult();

    public abstract int getEnergyPerTick();

    public int getNeededSeeds() {
        return 1;
    }

    public boolean isOk(int energyStored, FluidStack fluidStored, ItemStack seeds) {
        if (energyStored < getEnergyPerTick()) return false;
        if (!(fluidStored.isFluidEqual(getSubFluidStack()))) return false;
        if (fluidStored.amount < getSubFluidStack().amount) return false;
        if (seeds.getItem() != JSGItems.CRYSTAL_SEED) return false;
        return seeds.getCount() >= getNeededSeeds();
    }

    @Override
    public void getIngredients(@Nonnull IIngredients iIngredients) {
        iIngredients.setInput(VanillaTypes.ITEM, new ItemStack(JSGItems.CRYSTAL_SEED, getNeededSeeds()));
        iIngredients.setOutput(VanillaTypes.ITEM, getResult());
    }
}
