package mrjake.aunis.entity.trading;

import net.minecraft.entity.IMerchant;
import net.minecraft.village.MerchantRecipeList;

import java.util.Random;

public interface ITradeList {
    void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random);
}
