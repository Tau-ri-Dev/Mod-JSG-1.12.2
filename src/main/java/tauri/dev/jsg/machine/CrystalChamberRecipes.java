package tauri.dev.jsg.machine;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tauri.dev.jsg.fluid.JSGFluids;
import tauri.dev.jsg.item.JSGItems;

public class CrystalChamberRecipes {

    public static final CrystalChamberRecipe CRYSTAL_BLUE = new CrystalChamberRecipe(){

        @Override
        public int getWorkingTime() {
            return 200;
        }

        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(JSGFluids.moltenSiliconBlue, 250);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.CRYSTAL_BLUE);
        }

        @Override
        public int getEnergyPerTick() {
            return 250;
        }
    };

    public static final CrystalChamberRecipe[] RECIPES = {
        CRYSTAL_BLUE
    };
}
