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
import tauri.dev.jsg.machine.assembler.AssemblerRecipe;

import javax.annotation.Nonnull;

public class JEIAssemblerRecipeCategory implements IRecipeCategory<AssemblerRecipe> {

    public static final String UID = "jsg_assembler";
    public static final ResourceLocation BACK_TEXTURE = new ResourceLocation(JSG.MOD_ID, "textures/gui/container_assembler_jei.png");

    public final IDrawable background;
    public final IDrawable icon;
    public final IDrawable progressBar;

    public JEIAssemblerRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(BACK_TEXTURE, 0, 0, 175, 124);
        this.icon = helper.createDrawableIngredient(new ItemStack(JSGBlocks.MACHINE_ASSEMBLER));
        this.progressBar = helper.createAnimatedDrawable(helper.createDrawable(BACK_TEXTURE, 176, 128, 216 - 176, 142 - 128), 40, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Nonnull
    @Override
    public String getUid() {
        return UID;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return I18n.format("gui.assembler.name");
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
    public void drawExtras(@Nonnull Minecraft minecraft){
        progressBar.draw(minecraft, 95, 65);
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull AssemblerRecipe assemblerRecipe, @Nonnull IIngredients ingredients) {
        // input slots
        recipeLayout.getItemStacks().init(0, true, 9, 64);
        int i = 0;
        for (int y1 = 0; y1 < 3; y1++) {
            for (int x1 = 0; x1 < 3; x1++) {
                recipeLayout.getItemStacks().init(1 + i, true, 33 + (18 * x1), 46 + (18 * y1));
                i++;
            }
        }
        recipeLayout.getItemStacks().init(++i, true, 101, 64);

        // output slot
        recipeLayout.getItemStacks().init(++i, false, 145, 64);
        recipeLayout.getItemStacks().set(ingredients);
    }
}
