package tauri.dev.jsg.machine.chamber;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tauri.dev.jsg.fluid.JSGFluids;
import tauri.dev.jsg.item.JSGItems;

public class CrystalChamberRecipes {

    private static final int MB_PER_ONE_INGOT = 144;

    public static final CrystalChamberRecipe CRYSTAL_BLUE = new CrystalChamberRecipe() {

        @Override
        public int getWorkingTime() {
            return 200;
        }

        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(JSGFluids.SILICON_MOLTEN_BLUE, MB_PER_ONE_INGOT);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.CRYSTAL_BLUE);
        }

        @Override
        public int getEnergyPerTick() {
            return 512;
        }
    };
    public static final CrystalChamberRecipe CRYSTAL_RED = new CrystalChamberRecipe() {

        @Override
        public int getWorkingTime() {
            return 200;
        }

        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(JSGFluids.SILICON_MOLTEN_RED, MB_PER_ONE_INGOT);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.CRYSTAL_RED);
        }

        @Override
        public int getEnergyPerTick() {
            return 512;
        }
    };
    public static final CrystalChamberRecipe CRYSTAL_ENDER = new CrystalChamberRecipe() {

        @Override
        public int getWorkingTime() {
            return 200;
        }

        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(JSGFluids.SILICON_MOLTEN_ENDER, MB_PER_ONE_INGOT);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.CRYSTAL_ENDER);
        }

        @Override
        public int getEnergyPerTick() {
            return 512;
        }
    };
    public static final CrystalChamberRecipe CRYSTAL_YELLOW = new CrystalChamberRecipe() {

        @Override
        public int getWorkingTime() {
            return 200;
        }

        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(JSGFluids.SILICON_MOLTEN_YELLOW, MB_PER_ONE_INGOT);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.CRYSTAL_YELLOW);
        }

        @Override
        public int getEnergyPerTick() {
            return 512;
        }
    };
    public static final CrystalChamberRecipe CRYSTAL_WHITE = new CrystalChamberRecipe() {

        @Override
        public int getWorkingTime() {
            return 200;
        }

        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(JSGFluids.SILICON_MOLTEN_WHITE, MB_PER_ONE_INGOT);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.CRYSTAL_WHITE);
        }

        @Override
        public int getEnergyPerTick() {
            return 512;
        }
    };
    public static final CrystalChamberRecipe CRYSTAL_BLUE_PEGASUS = new CrystalChamberRecipe() {

        @Override
        public int getWorkingTime() {
            return 200;
        }

        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(JSGFluids.SILICON_MOLTEN_PEGASUS, MB_PER_ONE_INGOT);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.CRYSTAL_BLUE_PEGASUS);
        }

        @Override
        public int getEnergyPerTick() {
            return 512;
        }
    };

    public static final CrystalChamberRecipe[] RECIPES = {
            CRYSTAL_BLUE,
            CRYSTAL_RED,
            CRYSTAL_ENDER,
            CRYSTAL_YELLOW,
            CRYSTAL_WHITE,
            CRYSTAL_BLUE_PEGASUS
    };
}
