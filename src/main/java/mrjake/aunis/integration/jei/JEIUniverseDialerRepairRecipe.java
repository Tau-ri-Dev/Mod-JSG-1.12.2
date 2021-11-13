package mrjake.aunis.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mrjake.aunis.item.AunisItems;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Arrays;
import java.util.List;

/**
 * @author matousss
 */
public class JEIUniverseDialerRepairRecipe implements IRecipeWrapper {

    static final ItemStack BROKEN_DIALER = new ItemStack(AunisItems.UNIVERSE_DIALER, 1, 1);
    static final ItemStack GLASS = new ItemStack(Item.getItemFromBlock(Blocks.GLASS));
    static final ItemStack NEW_DIALER = new ItemStack(AunisItems.UNIVERSE_DIALER, 1, 0);

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(BROKEN_DIALER, GLASS));
        ingredients.setOutput(VanillaTypes.ITEM, NEW_DIALER);
    }


}
