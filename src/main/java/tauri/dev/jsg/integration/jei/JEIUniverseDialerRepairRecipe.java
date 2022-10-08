package tauri.dev.jsg.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import tauri.dev.jsg.item.JSGItems;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

/**
 * @author matousss
 */
public class JEIUniverseDialerRepairRecipe implements IRecipeWrapper {

    static final ItemStack BROKEN_DIALER = new ItemStack(JSGItems.UNIVERSE_DIALER, 1, 1);
    static final ItemStack GLASS = new ItemStack(Item.getItemFromBlock(Blocks.GLASS));
    static final ItemStack NEW_DIALER = new ItemStack(JSGItems.UNIVERSE_DIALER, 1, 0);

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(BROKEN_DIALER, GLASS));
        ingredients.setOutput(VanillaTypes.ITEM, NEW_DIALER);
    }


}
