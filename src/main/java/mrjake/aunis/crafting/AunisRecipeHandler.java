package mrjake.aunis.crafting;

import mrjake.aunis.Aunis;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.crafting.thermalreplace.blocks.BlockCapacitor;
import mrjake.aunis.crafting.thermalreplace.blocks.BlockOrlinBase;
import mrjake.aunis.crafting.thermalreplace.blocks.BlockOrlinRing;
import mrjake.aunis.crafting.thermalreplace.circuits.CircuitCrystalRecipe;
import mrjake.aunis.crafting.thermalreplace.circuits.CircuitNaquadahRecipe;
import mrjake.aunis.crafting.thermalreplace.crystals.*;
import mrjake.aunis.crafting.thermalreplace.ThermalAbstractRecipe;
import mrjake.aunis.crafting.thermalreplace.ingots.IngotNaquadahAlloy;
import mrjake.aunis.crafting.thermalreplace.ingots.IngotNaquadahAlloyRafinered;
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

	/**
	 * Ingots
	 */
	public static final IngotNaquadahAlloy INGOT_NAQUADAH_ALLOY = new IngotNaquadahAlloy();
	public static final IngotNaquadahAlloyRafinered INGOT_NAQUADAH_ALLOY_RAFINERED = new IngotNaquadahAlloyRafinered();

	/**
	 * Blocks
	 */
	public static final BlockCapacitor BLOCK_CAPACITOR = new BlockCapacitor();
	public static final BlockOrlinBase BLOCK_ORLIN_BASE = new BlockOrlinBase();
	public static final BlockOrlinRing BLOCK_ORLIN_RING = new BlockOrlinRing();

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

			// Ingots
			INGOT_NAQUADAH_ALLOY,
			INGOT_NAQUADAH_ALLOY_RAFINERED,

			// Blocks
			BLOCK_CAPACITOR,
			BLOCK_ORLIN_BASE,
			BLOCK_ORLIN_RING

	};

	public static boolean convertRecipes(){
		if(AunisConfig.recipesConfig.bypassThermal) {
			Aunis.info("Bypassing thermal expansion recipes...");
			return true; // load recipes
		}
		else if(AunisConfig.recipesConfig.convertThermal && !Aunis.isThermalLoaded) {
			Aunis.info("Thermal expansion is not loaded. Creating secondary crafting recipes...");
			return true; // load recipes
		}
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
