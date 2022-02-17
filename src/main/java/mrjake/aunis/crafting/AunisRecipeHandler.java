package mrjake.aunis.crafting;

import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.crafting.thermalreplace.CrystalRedRecipe;
import mrjake.aunis.crafting.thermalreplace.ThermalAbstractRecipe;
import mrjake.aunis.item.AunisItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class AunisRecipeHandler {

	public static final CrystalRedRecipe CRYSTAL_RED_RECIPE = new CrystalRedRecipe();

	public static final ThermalAbstractRecipe[] RECIPES = {
			CRYSTAL_RED_RECIPE
	};

	@SubscribeEvent
	public static void onRecipeRegister(Register<IRecipe> event) {

		// register furnace
		FurnaceRecipes.instance().addSmeltingRecipeForBlock(AunisBlocks.ORE_TITANIUM_BLOCK, new ItemStack(AunisItems.TITANIUM_INGOT), 2f);
		FurnaceRecipes.instance().addSmeltingRecipeForBlock(AunisBlocks.ORE_TRINIUM_BLOCK, new ItemStack(AunisItems.TRINIUM_INGOT), 4f);

		// normal recipes
		event.getRegistry().register(new NotebookRecipe());
		event.getRegistry().register(new NotebookPageCloneRecipe());
		event.getRegistry().register(new UniverseDialerCloneRecipe());
		event.getRegistry().register(new UniverseDialerRepairRecipe());

		// thermal recipes
		for(ThermalAbstractRecipe recipe : RECIPES){
			event.getRegistry().register(recipe);
		}
	}
}
