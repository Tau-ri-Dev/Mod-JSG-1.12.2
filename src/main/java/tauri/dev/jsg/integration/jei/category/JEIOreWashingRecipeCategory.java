package tauri.dev.jsg.integration.jei.category;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.block.machine.OreWashingBlock;
import tauri.dev.jsg.integration.jei.AbstractJEIRecipe;

import javax.annotation.Nonnull;

public class JEIOreWashingRecipeCategory extends JEIChamberRecipeCategory {

    public static final String UID = "jsg_ore_washing";

    public JEIOreWashingRecipeCategory(IGuiHelper helper) {
        super(helper);
        this.icon = helper.createDrawableIngredient(new ItemStack(JSGBlocks.MACHINE_ORE_WASHING));
    }

    @Nonnull
    @Override
    public String getUid() {
        return UID;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return I18n.format("tile.jsg.ore_washing_machine_block.name");
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull AbstractJEIRecipe crystalChamberRecipe, @Nonnull IIngredients ingredients) {
        // input slots
        recipeLayout.getItemStacks().init(0, true, 0, 18);
        // output slot
        recipeLayout.getItemStacks().init(1, false, 98, 18);
        recipeLayout.getItemStacks().set(ingredients);
        // fluid
        recipeLayout.getFluidStacks().init(2, true, 47, 1, 16, 53, OreWashingBlock.FLUID_CAPACITY, false, fluidMeter);
        recipeLayout.getFluidStacks().set(2, crystalChamberRecipe.getSubFluidStack());
    }
}
