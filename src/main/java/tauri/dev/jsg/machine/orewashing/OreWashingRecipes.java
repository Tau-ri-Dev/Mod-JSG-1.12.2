package tauri.dev.jsg.machine.orewashing;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import tauri.dev.jsg.config.craftings.CraftingConfig;
import tauri.dev.jsg.item.JSGItems;

public class OreWashingRecipes {
    public static final OreWashingRecipe PURIFIED_TRINIUM = new OreWashingRecipe() {
        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(FluidRegistry.WATER, 1000);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.TRINIUM_ORE_PURIFIED);
        }

        @Override
        public ItemStack getItemNeeded() {
            return new ItemStack(JSGItems.TRINIUM_ORE_IMPURE);
        }

        @Override
        public int getWorkingTime() {
            return 210;
        }

        @Override
        public int getEnergyPerTick() {
            return 120;
        }
    };
    public static final OreWashingRecipe RAW_TRINIUM = new OreWashingRecipe() {
        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(FluidRegistry.WATER, 1000);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.TRINIUM_ORE_RAW);
        }

        @Override
        public ItemStack getItemNeeded() {
            return new ItemStack(JSGItems.TRINIUM_ORE_PURIFIED);
        }

        @Override
        public int getWorkingTime() {
            return 210;
        }

        @Override
        public int getEnergyPerTick() {
            return 120;
        }
    };

    public static final OreWashingRecipe PURIFIED_TITANIUM = new OreWashingRecipe() {
        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(FluidRegistry.WATER, 1000);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.TITANIUM_ORE_PURIFIED);
        }

        @Override
        public ItemStack getItemNeeded() {
            return new ItemStack(JSGItems.TITANIUM_ORE_IMPURE);
        }

        @Override
        public int getWorkingTime() {
            return 180;
        }

        @Override
        public int getEnergyPerTick() {
            return 120;
        }
    };
    public static final OreWashingRecipe RAW_TITANIUM = new OreWashingRecipe() {
        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(FluidRegistry.WATER, 1000);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.TITANIUM_ORE_RAW);
        }

        @Override
        public ItemStack getItemNeeded() {
            return new ItemStack(JSGItems.TITANIUM_ORE_PURIFIED);
        }

        @Override
        public int getWorkingTime() {
            return 180;
        }

        @Override
        public int getEnergyPerTick() {
            return 120;
        }
    };

    public static final OreWashingRecipe PURIFIED_NAQUADAH_RAW = new OreWashingRecipe() {
        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(FluidRegistry.WATER, 1000);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.NAQUADAH_ORE_PURIFIED);
        }

        @Override
        public ItemStack getItemNeeded() {
            return new ItemStack(JSGItems.NAQUADAH_ORE_IMPURE);
        }

        @Override
        public int getWorkingTime() {
            return 210;
        }

        @Override
        public int getEnergyPerTick() {
            return 120;
        }
    };
    public static final OreWashingRecipe RAW_NAQUADAH_RAW = new OreWashingRecipe() {
        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(FluidRegistry.WATER, 1000);
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.NAQUADAH_ORE_RAW);
        }

        @Override
        public ItemStack getItemNeeded() {
            return new ItemStack(JSGItems.NAQUADAH_ORE_PURIFIED);
        }

        @Override
        public int getWorkingTime() {
            return 210;
        }

        @Override
        public int getEnergyPerTick() {
            return 120;
        }
    };

    public static final OreWashingRecipe[] RECIPES = {
            PURIFIED_TITANIUM,
            RAW_TITANIUM,
            PURIFIED_NAQUADAH_RAW,
            RAW_NAQUADAH_RAW,
            PURIFIED_TRINIUM,
            RAW_TRINIUM
    };

    public static void addToConfig() {
        CraftingConfig config = new CraftingConfig(OreWashingRecipe.ID);
        for (OreWashingRecipe recipe : RECIPES) {
            config.addKey(recipe.getResult().getItem().getRegistryName());
        }
        CraftingConfig.addConfig(config);
    }
}
