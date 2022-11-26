package tauri.dev.jsg.integration.jei.category;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.block.machine.CrystalChamberBlock;
import tauri.dev.jsg.integration.jei.AbstractJEIRecipe;

import javax.annotation.Nonnull;

public class JEIPCBFabricatorRecipeCategory implements IRecipeCategory<AbstractJEIRecipe> {

    public static final String UID = "jsg_pcb_fabricator";
    public static final ResourceLocation BACK_TEXTURE = new ResourceLocation(JSG.MOD_ID, "textures/gui/container_pcb_fabricator_jei.png");

    public final IDrawable background;
    public final IDrawable icon;
    public final IDrawable progressBar;
    public final IDrawable fluidMeter;

    public JEIPCBFabricatorRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(BACK_TEXTURE, 0, 0, 137, 55);
        this.fluidMeter = helper.createDrawable(BACK_TEXTURE, 176, 32, 16, 54);
        this.icon = helper.createDrawableIngredient(new ItemStack(JSGBlocks.MACHINE_PCB_FABRICATOR));
        this.progressBar = helper.createAnimatedDrawable(helper.createDrawable(BACK_TEXTURE, 176, 0, 216 - 176, 15), 40, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Nonnull
    @Override
    public String getUid() {
        return UID;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return I18n.format("tile.jsg.pcb_fabricator_block.name");
    }

    @Nonnull
    @Override
    public String getModName() {
        return JSG.MOD_NAME;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft) {
        fluidMeter.draw(minecraft, 64, 0);
        progressBar.draw(minecraft, 58, 19);
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull AbstractJEIRecipe recipe, @Nonnull IIngredients ingredients) {
        // input slots
        int i = 0;
        for (int y1 = 0; y1 < 3; y1++) {
            for (int x1 = 0; x1 < 3; x1++) {
                recipeLayout.getItemStacks().init(i, true, (18 * x1), 1 + (18 * y1));
                i++;
            }
        }
        // output slot
        recipeLayout.getItemStacks().init(9, false, 116, 18);
        recipeLayout.getItemStacks().set(ingredients);
        // fluid
        recipeLayout.getFluidStacks().init(10, true, 65, 1, 16, 53, CrystalChamberBlock.FLUID_CAPACITY, false, fluidMeter);
        recipeLayout.getFluidStacks().set(10, recipe.getSubFluidStack());
    }
}
