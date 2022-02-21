package mrjake.aunis.crafting;

import mrjake.aunis.Aunis;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.crafting.thermalreplace.circuits.CircuitCrystalRecipe;
import mrjake.aunis.crafting.thermalreplace.circuits.CircuitNaquadahRecipe;
import mrjake.aunis.crafting.thermalreplace.circuits.CircuitZpmRecipe;
import mrjake.aunis.crafting.thermalreplace.crystals.*;
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

	/**
	 * Crystals
	 */
	public static final CrystalRedRecipe CRYSTAL_RED = new CrystalRedRecipe();
	public static final CrystalEnderRecipe CRYSTAL_ENDER = new CrystalEnderRecipe();
	public static final CrystalBlueRecipe CRYSTAL_BLUE = new CrystalBlueRecipe();
	public static final CrystalYellowRecipe CRYSTAL_YELLOW = new CrystalYellowRecipe();
	public static final CrystalWhiteRecipe CRYSTAL_WHITE = new CrystalWhiteRecipe();

	/**
	 * Crystal seed
	 */
	public static final CrystalSeedRecipe CRYSTAL_SEED = new CrystalSeedRecipe();

	/**
	 * Circuits
	 */
	public static final CircuitCrystalRecipe CIRCUIT_CONTROL_CRYSTAL = new CircuitCrystalRecipe();
	public static final CircuitNaquadahRecipe CIRCUIT_CONTROL_NAQUADAH = new CircuitNaquadahRecipe();
	public static final CircuitZpmRecipe CIRCUIT_CONTROL_ZPM = new CircuitZpmRecipe();

	public static ThermalAbstractRecipe[] RECIPES = {
			// Crystals
			CRYSTAL_RED,
			CRYSTAL_ENDER,
			CRYSTAL_BLUE,
			CRYSTAL_YELLOW,
			CRYSTAL_WHITE,

			// Seed
			CRYSTAL_SEED,

			// Circuits
			CIRCUIT_CONTROL_CRYSTAL,
			CIRCUIT_CONTROL_NAQUADAH,
			CIRCUIT_CONTROL_ZPM,
	};

	public static boolean convertRecipes(){
		if(AunisConfig.recipesConfig.bypassThermal)
			return true; // load recipes
		else if(AunisConfig.recipesConfig.convertThermal && !Aunis.isThermalLoaded)
			return true; // load recipes
		return false;
	}

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
		if(convertRecipes()) {
			for (ThermalAbstractRecipe recipe : RECIPES) {
				event.getRegistry().register(recipe);
			}
		}
	}
}
