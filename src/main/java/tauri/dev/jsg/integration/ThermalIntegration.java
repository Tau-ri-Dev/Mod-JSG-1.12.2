package tauri.dev.jsg.integration;

import cofh.api.util.ThermalExpansionHelper;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.fluid.JSGFluids;
import tauri.dev.jsg.item.JSGItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class ThermalIntegration {

	public static final int oneIngot = 144; //mBuckets per ingot
	public static final int defaultEnergy = 1000; //RF

	public static void registerRecipes() {
		// Silicons
		ThermalExpansionHelper.addCrucibleRecipe(defaultEnergy, new ItemStack(Blocks.SAND), new FluidStack(JSGFluids.moltenSiliconBlack, oneIngot/8));
		
		ThermalExpansionHelper.addBrewerRecipe(defaultEnergy*3, new ItemStack(Items.REDSTONE), new FluidStack(JSGFluids.moltenSiliconBlack, oneIngot/5), new FluidStack(JSGFluids.moltenSiliconRed, oneIngot/4));
		ThermalExpansionHelper.addBrewerRecipe(defaultEnergy*3, new ItemStack(Items.DYE, 1, 4), new FluidStack(JSGFluids.moltenSiliconBlack, oneIngot/5), new FluidStack(JSGFluids.moltenSiliconBlue, oneIngot/4));
		ThermalExpansionHelper.addBrewerRecipe(defaultEnergy*3, new ItemStack(Items.ENDER_PEARL), new FluidStack(JSGFluids.moltenSiliconBlack, oneIngot/5*4), new FluidStack(JSGFluids.moltenSiliconEnder, oneIngot));
		ThermalExpansionHelper.addBrewerRecipe(defaultEnergy*3, new ItemStack(Items.GLOWSTONE_DUST), new FluidStack(JSGFluids.moltenSiliconBlack, oneIngot/5), new FluidStack(JSGFluids.moltenSiliconYellow, oneIngot/4));
		ThermalExpansionHelper.addRefineryRecipe(defaultEnergy, new FluidStack(JSGFluids.moltenSiliconBlack, oneIngot/4), new FluidStack(JSGFluids.moltenSiliconWhite, oneIngot/4), ItemStack.EMPTY);

		
		// Crystals
		ThermalExpansionHelper.addCompactorRecipe(defaultEnergy*5, new ItemStack(Items.QUARTZ, 3), new ItemStack(JSGItems.CRYSTAL_SEED));
		
		ThermalExpansionHelper.addTransposerFill(defaultEnergy*7, new ItemStack(JSGItems.CRYSTAL_SEED), new ItemStack(JSGItems.CRYSTAL_RED), new FluidStack(JSGFluids.moltenSiliconRed, oneIngot), false);
		ThermalExpansionHelper.addTransposerFill(defaultEnergy*7, new ItemStack(JSGItems.CRYSTAL_SEED), new ItemStack(JSGItems.CRYSTAL_BLUE), new FluidStack(JSGFluids.moltenSiliconBlue, oneIngot), false);
		ThermalExpansionHelper.addTransposerFill(defaultEnergy*7, new ItemStack(JSGItems.CRYSTAL_SEED), new ItemStack(JSGItems.CRYSTAL_ENDER), new FluidStack(JSGFluids.moltenSiliconEnder, oneIngot), false);
		ThermalExpansionHelper.addTransposerFill(defaultEnergy*7, new ItemStack(JSGItems.CRYSTAL_SEED), new ItemStack(JSGItems.CRYSTAL_YELLOW), new FluidStack(JSGFluids.moltenSiliconYellow, oneIngot), false);
		ThermalExpansionHelper.addTransposerFill(defaultEnergy*7, new ItemStack(JSGItems.CRYSTAL_SEED), new ItemStack(JSGItems.CRYSTAL_WHITE), new FluidStack(JSGFluids.moltenSiliconWhite, oneIngot), false);
		
		
		// Naquadah
		ThermalExpansionHelper.addCrucibleRecipe(defaultEnergy*12, new ItemStack(JSGBlocks.ORE_NAQUADAH_BLOCK), new FluidStack(JSGFluids.moltenNaquadahRaw, (oneIngot * 2)));
		ThermalExpansionHelper.addCrucibleRecipe(defaultEnergy*12, new ItemStack(JSGBlocks.ORE_NAQUADAH_BLOCK_STONE), new FluidStack(JSGFluids.moltenNaquadahRaw, (oneIngot * 2)));
		ThermalExpansionHelper.addCrucibleRecipe(defaultEnergy*2, new ItemStack(JSGItems.NAQUADAH_SHARD), new FluidStack(JSGFluids.moltenNaquadahRaw, (oneIngot/2)));
		ThermalExpansionHelper.addCrucibleRecipe(defaultEnergy*12, new ItemStack(JSGBlocks.NAQUADAH_BLOCK_RAW), new FluidStack(JSGFluids.moltenNaquadahRaw, (oneIngot * 2)));
		ThermalExpansionHelper.addRefineryRecipe(defaultEnergy*2, new FluidStack(JSGFluids.moltenNaquadahRaw, (oneIngot)), new FluidStack(JSGFluids.moltenNaquadahRefined, (oneIngot)), ItemStack.EMPTY);
		ThermalExpansionHelper.addTransposerFill(defaultEnergy*4, new ItemStack(Items.IRON_INGOT), new ItemStack(JSGItems.NAQUADAH_ALLOY_RAW, 2), new FluidStack(JSGFluids.moltenNaquadahRaw, oneIngot), true);
		ThermalExpansionHelper.addTransposerFill(defaultEnergy*4, new ItemStack(Items.IRON_INGOT), new ItemStack(JSGItems.NAQUADAH_ALLOY, 2), new FluidStack(JSGFluids.moltenNaquadahRefined, oneIngot), true);
		
		ThermalExpansionHelper.addCrucibleRecipe(defaultEnergy*2, new ItemStack(JSGItems.NAQUADAH_ALLOY), new FluidStack(JSGFluids.moltenNaquadahAlloy, oneIngot));

		// Titanium / trinium
		ThermalExpansionHelper.addCrucibleRecipe(defaultEnergy*2, new ItemStack(JSGItems.TITANIUM_INGOT), new FluidStack(JSGFluids.moltenTitanium, oneIngot));
		ThermalExpansionHelper.addCrucibleRecipe(defaultEnergy*2, new ItemStack(JSGItems.TRINIUM_INGOT), new FluidStack(JSGFluids.moltenTrinium, oneIngot));
		ThermalExpansionHelper.addCrucibleRecipe(defaultEnergy*2, new ItemStack(JSGBlocks.ORE_TITANIUM_BLOCK), new FluidStack(JSGFluids.moltenTitanium, oneIngot));
		ThermalExpansionHelper.addCrucibleRecipe(defaultEnergy*2, new ItemStack(JSGBlocks.ORE_TRINIUM_BLOCK), new FluidStack(JSGFluids.moltenTrinium, oneIngot));

		// Circuits
		ThermalExpansionHelper.addTransposerFill(defaultEnergy*5, new ItemStack(JSGItems.CIRCUIT_CONTROL_BASE), new ItemStack(JSGItems.CIRCUIT_CONTROL_CRYSTAL), new FluidStack(JSGFluids.moltenSiliconWhite, oneIngot), false);
		ThermalExpansionHelper.addTransposerFill(defaultEnergy*5, new ItemStack(JSGItems.CIRCUIT_CONTROL_BASE), new ItemStack(JSGItems.CIRCUIT_CONTROL_NAQUADAH), new FluidStack(JSGFluids.moltenNaquadahAlloy, oneIngot), false);

		// Capacitor
		ThermalExpansionHelper.addTransposerFill(defaultEnergy*15, new ItemStack(JSGBlocks.CAPACITOR_BLOCK_EMPTY), new ItemStack(JSGBlocks.CAPACITOR_BLOCK), new FluidStack(JSGFluids.moltenSiliconRed, (oneIngot * 8)), false);
	}
}
