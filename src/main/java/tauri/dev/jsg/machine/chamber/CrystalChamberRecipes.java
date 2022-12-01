package tauri.dev.jsg.machine.chamber;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tauri.dev.jsg.config.craftings.CraftingConfig;
import tauri.dev.jsg.fluid.JSGFluids;
import tauri.dev.jsg.item.JSGItems;

import static tauri.dev.jsg.Constants.ONE_INGOT_IN_FLUID_MB;

public class CrystalChamberRecipes {
    public static final CrystalChamberRecipe CRYSTAL_BLUE = new CrystalChamberRecipe() {

        @Override
        public int getWorkingTime() {
            return 200;
        }

        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(JSGFluids.SILICON_MOLTEN_BLUE, ONE_INGOT_IN_FLUID_MB);
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
            return new FluidStack(JSGFluids.SILICON_MOLTEN_RED, ONE_INGOT_IN_FLUID_MB);
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
            return new FluidStack(JSGFluids.SILICON_MOLTEN_ENDER, ONE_INGOT_IN_FLUID_MB);
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
            return new FluidStack(JSGFluids.SILICON_MOLTEN_YELLOW, ONE_INGOT_IN_FLUID_MB);
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
            return new FluidStack(JSGFluids.SILICON_MOLTEN_WHITE, ONE_INGOT_IN_FLUID_MB);
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
            return new FluidStack(JSGFluids.SILICON_MOLTEN_PEGASUS, ONE_INGOT_IN_FLUID_MB);
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

    public static void addToConfig(){
        CraftingConfig config = new CraftingConfig(CrystalChamberRecipe.ID);
        for(CrystalChamberRecipe recipe : RECIPES){
            config.addKey(recipe.getResult().getItem().getRegistryName());
        }
        CraftingConfig.addConfig(config);
    }
}
