package mrjake.aunis.crafting;

import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.item.AunisItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class AunisRecipeHandler {

	@SubscribeEvent
	public static void onRecipeRegister(Register<IRecipe> event) {
		event.getRegistry().register(new NotebookRecipe());
		event.getRegistry().register(new NotebookPageCloneRecipe());
		event.getRegistry().register(new UniverseDialerCloneRecipe());
		FurnaceRecipes.instance().addSmeltingRecipeForBlock(AunisBlocks.ORE_TITANIUM_BLOCK, new ItemStack(AunisItems.TITANIUM_INGOT), 2f);
		FurnaceRecipes.instance().addSmeltingRecipeForBlock(AunisBlocks.ORE_TRINIUM_BLOCK, new ItemStack(AunisItems.TRINIUM_INGOT), 4f);
	}
}
