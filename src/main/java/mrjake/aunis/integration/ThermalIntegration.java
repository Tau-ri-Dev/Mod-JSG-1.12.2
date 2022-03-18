package mrjake.aunis.integration;

import cofh.api.util.ThermalExpansionHelper;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.fluid.AunisFluids;
import mrjake.aunis.item.AunisItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class ThermalIntegration {

	public static final int oneIngot = 144; //mBuckets per ingot
	public static final int defaultEnergy = 1000; //RF

	public static void registerRecipes() {
		// Silicons
		ThermalExpansionHelper.addCrucibleRecipe(defaultEnergy, new ItemStack(Blocks.SAND), new FluidStack(AunisFluids.moltenSiliconBlack, oneIngot/8));
		
		ThermalExpansionHelper.addBrewerRecipe(defaultEnergy*3, new ItemStack(Items.REDSTONE), new FluidStack(AunisFluids.moltenSiliconBlack, oneIngot/5), new FluidStack(AunisFluids.moltenSiliconRed, oneIngot/4));
		ThermalExpansionHelper.addBrewerRecipe(defaultEnergy*3, new ItemStack(Items.DYE, 1, 4), new FluidStack(AunisFluids.moltenSiliconBlack, oneIngot/5), new FluidStack(AunisFluids.moltenSiliconBlue, oneIngot/4));
		ThermalExpansionHelper.addBrewerRecipe(defaultEnergy*3, new ItemStack(Items.ENDER_PEARL), new FluidStack(AunisFluids.moltenSiliconBlack, oneIngot/5*4), new FluidStack(AunisFluids.moltenSiliconEnder, oneIngot));
		ThermalExpansionHelper.addBrewerRecipe(defaultEnergy*3, new ItemStack(Items.GLOWSTONE_DUST), new FluidStack(AunisFluids.moltenSiliconBlack, oneIngot/5), new FluidStack(AunisFluids.moltenSiliconYellow, oneIngot/4));
		ThermalExpansionHelper.addRefineryRecipe(defaultEnergy, new FluidStack(AunisFluids.moltenSiliconBlack, oneIngot/4), new FluidStack(AunisFluids.moltenSiliconWhite, oneIngot/4), ItemStack.EMPTY);

		
		// Crystals
		ThermalExpansionHelper.addCompactorRecipe(defaultEnergy*5, new ItemStack(Items.QUARTZ, 3), new ItemStack(AunisItems.CRYSTAL_SEED));
		
		ThermalExpansionHelper.addTransposerFill(defaultEnergy*7, new ItemStack(AunisItems.CRYSTAL_SEED), new ItemStack(AunisItems.CRYSTAL_RED), new FluidStack(AunisFluids.moltenSiliconRed, oneIngot), false);
		ThermalExpansionHelper.addTransposerFill(defaultEnergy*7, new ItemStack(AunisItems.CRYSTAL_SEED), new ItemStack(AunisItems.CRYSTAL_BLUE), new FluidStack(AunisFluids.moltenSiliconBlue, oneIngot), false);
		ThermalExpansionHelper.addTransposerFill(defaultEnergy*7, new ItemStack(AunisItems.CRYSTAL_SEED), new ItemStack(AunisItems.CRYSTAL_ENDER), new FluidStack(AunisFluids.moltenSiliconEnder, oneIngot), false);
		ThermalExpansionHelper.addTransposerFill(defaultEnergy*7, new ItemStack(AunisItems.CRYSTAL_SEED), new ItemStack(AunisItems.CRYSTAL_YELLOW), new FluidStack(AunisFluids.moltenSiliconYellow, oneIngot), false);
		ThermalExpansionHelper.addTransposerFill(defaultEnergy*7, new ItemStack(AunisItems.CRYSTAL_SEED), new ItemStack(AunisItems.CRYSTAL_WHITE), new FluidStack(AunisFluids.moltenSiliconWhite, oneIngot), false);
		
		
		// Naquadah
		ThermalExpansionHelper.addCrucibleRecipe(defaultEnergy*12, new ItemStack(AunisBlocks.ORE_NAQUADAH_BLOCK), new FluidStack(AunisFluids.moltenNaquadahRaw, (oneIngot * 2)));
		ThermalExpansionHelper.addCrucibleRecipe(defaultEnergy*12, new ItemStack(AunisBlocks.ORE_NAQUADAH_BLOCK_STONE), new FluidStack(AunisFluids.moltenNaquadahRaw, (oneIngot * 2)));
		ThermalExpansionHelper.addCrucibleRecipe(defaultEnergy*2, new ItemStack(AunisItems.NAQUADAH_SHARD), new FluidStack(AunisFluids.moltenNaquadahRaw, (oneIngot/2)));
		ThermalExpansionHelper.addCrucibleRecipe(defaultEnergy*12, new ItemStack(AunisBlocks.NAQUADAH_BLOCK_RAW), new FluidStack(AunisFluids.moltenNaquadahRaw, (oneIngot * 2)));
		ThermalExpansionHelper.addRefineryRecipe(defaultEnergy*2, new FluidStack(AunisFluids.moltenNaquadahRaw, (oneIngot)), new FluidStack(AunisFluids.moltenNaquadahRefined, (oneIngot)), ItemStack.EMPTY);
		ThermalExpansionHelper.addTransposerFill(defaultEnergy*4, new ItemStack(Items.IRON_INGOT), new ItemStack(AunisItems.NAQUADAH_ALLOY_RAW, 2), new FluidStack(AunisFluids.moltenNaquadahRaw, oneIngot), true);
		ThermalExpansionHelper.addTransposerFill(defaultEnergy*4, new ItemStack(Items.IRON_INGOT), new ItemStack(AunisItems.NAQUADAH_ALLOY, 2), new FluidStack(AunisFluids.moltenNaquadahRefined, oneIngot), true);
		
		ThermalExpansionHelper.addCrucibleRecipe(defaultEnergy*2, new ItemStack(AunisItems.NAQUADAH_ALLOY), new FluidStack(AunisFluids.moltenNaquadahAlloy, oneIngot));

		// Titanium / trinium
		ThermalExpansionHelper.addCrucibleRecipe(defaultEnergy*2, new ItemStack(AunisItems.TITANIUM_INGOT), new FluidStack(AunisFluids.moltenTitanium, oneIngot));
		ThermalExpansionHelper.addCrucibleRecipe(defaultEnergy*2, new ItemStack(AunisItems.TRINIUM_INGOT), new FluidStack(AunisFluids.moltenTrinium, oneIngot));
		ThermalExpansionHelper.addCrucibleRecipe(defaultEnergy*2, new ItemStack(AunisBlocks.ORE_TITANIUM_BLOCK), new FluidStack(AunisFluids.moltenTitanium, oneIngot));
		ThermalExpansionHelper.addCrucibleRecipe(defaultEnergy*2, new ItemStack(AunisBlocks.ORE_TRINIUM_BLOCK), new FluidStack(AunisFluids.moltenTrinium, oneIngot));

		// Circuits
		ThermalExpansionHelper.addTransposerFill(defaultEnergy*5, new ItemStack(AunisItems.CIRCUIT_CONTROL_BASE), new ItemStack(AunisItems.CIRCUIT_CONTROL_CRYSTAL), new FluidStack(AunisFluids.moltenSiliconWhite, oneIngot), false);
		ThermalExpansionHelper.addTransposerFill(defaultEnergy*5, new ItemStack(AunisItems.CIRCUIT_CONTROL_BASE), new ItemStack(AunisItems.CIRCUIT_CONTROL_NAQUADAH), new FluidStack(AunisFluids.moltenNaquadahAlloy, oneIngot), false);
		ThermalExpansionHelper.addTransposerFill(defaultEnergy*5, new ItemStack(AunisItems.CIRCUIT_CONTROL_BASE), new ItemStack(AunisItems.CIRCUIT_CONTROL_ZPM), new FluidStack(AunisFluids.moltenTitanium, oneIngot), false);

		// Capacitor
		ThermalExpansionHelper.addTransposerFill(defaultEnergy*15, new ItemStack(AunisBlocks.CAPACITOR_BLOCK_EMPTY), new ItemStack(AunisBlocks.CAPACITOR_BLOCK), new FluidStack(AunisFluids.moltenSiliconRed, (oneIngot * 8)), false);
	}
}
