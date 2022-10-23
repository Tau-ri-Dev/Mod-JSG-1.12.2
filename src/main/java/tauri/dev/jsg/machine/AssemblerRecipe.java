package tauri.dev.jsg.machine;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AssemblerRecipe implements IRecipeWrapper {
    public abstract int getWorkingTime(); // in ticks

    public abstract int getEnergyPerTick();

    public abstract Item getSchematic();

    public abstract ArrayList<ItemStack> getPattern();

    public abstract ItemStack getSubItemStack();

    public abstract ItemStack getResult();

    public abstract String getUnlocalizedName();

    public boolean removeSubItem() {
        return true;
    }

    public boolean removeDurabilitySubItem() {
        return false;
    }

    public boolean isOk(int energyStored, Item schematic, ArrayList<ItemStack> stacks, ItemStack subStack) {
        if (energyStored < getEnergyPerTick()) return false;
        if (getSchematic() != schematic) return false;
        int i = 0;
        for (ItemStack s : getPattern()) {
            if (s != null) {
                if (stacks.get(i).getItem() != s.getItem()) return false;
                if (stacks.get(i).getCount() < s.getCount()) return false;
            } else {
                if (!(stacks.get(i).isEmpty())) return false;
            }
            i++;
        }
        if (subStack.getItem() != getSubItemStack().getItem()) return false;
        return subStack.getCount() >= getSubItemStack().getCount();
    }

    @Override
    public void getIngredients(@Nonnull IIngredients iIngredients) {
        List<List<ItemStack>> list = new ArrayList<>();

        list.add(Collections.singletonList(new ItemStack(getSchematic(), 1)));
        for(ItemStack s : getPattern())
            list.add(Collections.singletonList(s));
        list.add(Collections.singletonList(getSubItemStack()));

        iIngredients.setInputLists(VanillaTypes.ITEM, list);

        iIngredients.setOutput(VanillaTypes.ITEM, getResult());
    }
}
