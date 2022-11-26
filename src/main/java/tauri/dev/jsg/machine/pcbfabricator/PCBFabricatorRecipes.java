package tauri.dev.jsg.machine.pcbfabricator;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tauri.dev.jsg.config.craftings.CraftingConfig;
import tauri.dev.jsg.fluid.JSGFluids;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.util.FluidColors;

import java.util.ArrayList;

import static tauri.dev.jsg.Constants.ONE_INGOT_IN_FLUID_MB;

public class PCBFabricatorRecipes {

    public static PCBFabricatorRecipe CIRCUIT_CONTROL_CRYSTAL = new PCBFabricatorRecipe() {
        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(JSGFluids.SILICON_MOLTEN_WHITE, ONE_INGOT_IN_FLUID_MB);
        }

        @Override
        public int getWorkingTime() {
            return 200;
        }

        @Override
        public int getEnergyPerTick() {
            return 232;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            return new ArrayList<ItemStack>() {{
                add(null);
                add(null);
                add(null);

                add(new ItemStack(JSGItems.TRINIUM_DUST));
                add(new ItemStack(JSGItems.CIRCUIT_CONTROL_BASE));
                add(new ItemStack(JSGItems.TRINIUM_DUST));

                add(new ItemStack(Items.QUARTZ));
                add(new ItemStack(Items.QUARTZ));
                add(new ItemStack(Items.QUARTZ));
            }};
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.CIRCUIT_CONTROL_CRYSTAL);
        }

        @Override
        public float[] getBeamColors() {
            FluidColors.FloatColors fluidColors = FluidColors.getAverageColor(JSGFluids.SILICON_MOLTEN_WHITE);
            if (fluidColors != null) {
                return fluidColors.colors;
            }
            return new float[]{1f, 1f, 1f};
        }
    };
    public static PCBFabricatorRecipe CIRCUIT_CONTROL_NAQUADAH = new PCBFabricatorRecipe() {
        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(JSGFluids.MOLTEN_NAQUADAH_ALLOY, ONE_INGOT_IN_FLUID_MB);
        }

        @Override
        public int getWorkingTime() {
            return 200;
        }

        @Override
        public int getEnergyPerTick() {
            return 232;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            return new ArrayList<ItemStack>() {{
                add(null);
                add(null);
                add(null);

                add(new ItemStack(JSGItems.TITANIUM_DUST));
                add(new ItemStack(JSGItems.CIRCUIT_CONTROL_BASE));
                add(new ItemStack(JSGItems.TITANIUM_DUST));

                add(new ItemStack(JSGItems.PLATE_NAQUADAH));
                add(new ItemStack(JSGItems.PLATE_NAQUADAH));
                add(new ItemStack(JSGItems.PLATE_NAQUADAH));
            }};
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.CIRCUIT_CONTROL_NAQUADAH);
        }

        @Override
        public float[] getBeamColors() {
            FluidColors.FloatColors fluidColors = FluidColors.getAverageColor(JSGFluids.MOLTEN_NAQUADAH_ALLOY);
            if (fluidColors != null) {
                return fluidColors.colors;
            }
            return new float[]{1f, 1f, 1f};
        }
    };

    public static PCBFabricatorRecipe SG_CRYSTAL_GLYPH = new PCBFabricatorRecipe() {
        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(JSGFluids.SILICON_MOLTEN_WHITE, ONE_INGOT_IN_FLUID_MB);
        }

        @Override
        public int getWorkingTime() {
            return 200;
        }

        @Override
        public int getEnergyPerTick() {
            return 232;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            return new ArrayList<ItemStack>() {{
                add(new ItemStack(JSGItems.CRYSTAL_YELLOW));
                add(new ItemStack(JSGItems.CRYSTAL_RED));
                add(new ItemStack(JSGItems.CRYSTAL_ENDER));

                add(new ItemStack(JSGItems.TITANIUM_DUST));
                add(new ItemStack(JSGItems.CIRCUIT_CONTROL_CRYSTAL));
                add(new ItemStack(JSGItems.TITANIUM_DUST));

                add(new ItemStack(Items.QUARTZ));
                add(new ItemStack(Items.QUARTZ));
                add(new ItemStack(Items.QUARTZ));
            }};
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.CRYSTAL_GLYPH_STARGATE);
        }

        @Override
        public float[] getBeamColors() {
            FluidColors.FloatColors fluidColors = FluidColors.getAverageColor(JSGFluids.SILICON_MOLTEN_WHITE);
            if (fluidColors != null) {
                return fluidColors.colors;
            }
            return new float[]{1f, 1f, 1f};
        }
    };
    public static PCBFabricatorRecipe SG_CRYSTAL_MILKYWAY = new PCBFabricatorRecipe() {
        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(JSGFluids.SILICON_MOLTEN_WHITE, ONE_INGOT_IN_FLUID_MB);
        }

        @Override
        public int getWorkingTime() {
            return 200;
        }

        @Override
        public int getEnergyPerTick() {
            return 232;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            return new ArrayList<ItemStack>() {{
                add(null);
                add(new ItemStack(JSGItems.CRYSTAL_RED));
                add(null);

                add(new ItemStack(JSGItems.CRYSTAL_RED));
                add(new ItemStack(JSGItems.CIRCUIT_CONTROL_CRYSTAL));
                add(new ItemStack(JSGItems.CRYSTAL_RED));

                add(new ItemStack(Items.QUARTZ));
                add(new ItemStack(Items.QUARTZ));
                add(new ItemStack(Items.QUARTZ));
            }};
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.CRYSTAL_GLYPH_MILKYWAY);
        }

        @Override
        public float[] getBeamColors() {
            FluidColors.FloatColors fluidColors = FluidColors.getAverageColor(JSGFluids.SILICON_MOLTEN_WHITE);
            if (fluidColors != null) {
                return fluidColors.colors;
            }
            return new float[]{1f, 1f, 1f};
        }
    };
    public static PCBFabricatorRecipe SG_CRYSTAL_PEGASUS = new PCBFabricatorRecipe() {
        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(JSGFluids.SILICON_MOLTEN_WHITE, ONE_INGOT_IN_FLUID_MB);
        }

        @Override
        public int getWorkingTime() {
            return 200;
        }

        @Override
        public int getEnergyPerTick() {
            return 232;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            return new ArrayList<ItemStack>() {{
                add(null);
                add(new ItemStack(JSGItems.CRYSTAL_BLUE_PEGASUS));
                add(null);

                add(new ItemStack(JSGItems.CRYSTAL_BLUE));
                add(new ItemStack(JSGItems.CIRCUIT_CONTROL_CRYSTAL));
                add(new ItemStack(JSGItems.CRYSTAL_BLUE));

                add(new ItemStack(Items.QUARTZ));
                add(new ItemStack(Items.QUARTZ));
                add(new ItemStack(Items.QUARTZ));
            }};
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.CRYSTAL_GLYPH_PEGASUS);
        }

        @Override
        public float[] getBeamColors() {
            FluidColors.FloatColors fluidColors = FluidColors.getAverageColor(JSGFluids.SILICON_MOLTEN_WHITE);
            if (fluidColors != null) {
                return fluidColors.colors;
            }
            return new float[]{1f, 1f, 1f};
        }
    };
    public static PCBFabricatorRecipe SG_CRYSTAL_UNIVERSE = new PCBFabricatorRecipe() {
        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(JSGFluids.SILICON_MOLTEN_WHITE, ONE_INGOT_IN_FLUID_MB);
        }

        @Override
        public int getWorkingTime() {
            return 200;
        }

        @Override
        public int getEnergyPerTick() {
            return 232;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            return new ArrayList<ItemStack>() {{
                add(null);
                add(new ItemStack(JSGItems.CRYSTAL_WHITE));
                add(null);

                add(new ItemStack(JSGItems.CRYSTAL_WHITE));
                add(new ItemStack(JSGItems.CIRCUIT_CONTROL_CRYSTAL));
                add(new ItemStack(JSGItems.CRYSTAL_WHITE));

                add(new ItemStack(Items.QUARTZ));
                add(new ItemStack(Items.QUARTZ));
                add(new ItemStack(Items.QUARTZ));
            }};
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.CRYSTAL_GLYPH_UNIVERSE);
        }

        @Override
        public float[] getBeamColors() {
            FluidColors.FloatColors fluidColors = FluidColors.getAverageColor(JSGFluids.SILICON_MOLTEN_WHITE);
            if (fluidColors != null) {
                return fluidColors.colors;
            }
            return new float[]{1f, 1f, 1f};
        }
    };

    public static PCBFabricatorRecipe TR_CRYSTAL_GOAULD = new PCBFabricatorRecipe() {
        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(JSGFluids.NAQUADAH_MOLTEN_RAW, ONE_INGOT_IN_FLUID_MB);
        }

        @Override
        public int getWorkingTime() {
            return 200;
        }

        @Override
        public int getEnergyPerTick() {
            return 232;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            return new ArrayList<ItemStack>() {{
                add(new ItemStack(JSGItems.CRYSTAL_YELLOW));
                add(new ItemStack(JSGItems.TITANIUM_DUST));
                add(new ItemStack(JSGItems.CRYSTAL_ENDER));

                add(new ItemStack(JSGItems.CRYSTAL_RED));
                add(new ItemStack(JSGItems.CIRCUIT_CONTROL_CRYSTAL));
                add(new ItemStack(JSGItems.CRYSTAL_BLUE));

                add(new ItemStack(Items.QUARTZ));
                add(new ItemStack(Items.QUARTZ));
                add(new ItemStack(Items.QUARTZ));
            }};
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.CRYSTAL_GLYPH_GOAULD);
        }

        @Override
        public float[] getBeamColors() {
            FluidColors.FloatColors fluidColors = FluidColors.getAverageColor(JSGFluids.NAQUADAH_MOLTEN_RAW);
            if (fluidColors != null) {
                return fluidColors.colors;
            }
            return new float[]{1f, 1f, 1f};
        }
    };
    public static PCBFabricatorRecipe TR_CRYSTAL_ORI = new PCBFabricatorRecipe() {
        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(JSGFluids.SILICON_MOLTEN_WHITE, ONE_INGOT_IN_FLUID_MB);
        }

        @Override
        public int getWorkingTime() {
            return 200;
        }

        @Override
        public int getEnergyPerTick() {
            return 232;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            return new ArrayList<ItemStack>() {{
                add(null);
                add(new ItemStack(JSGItems.TRINIUM_DUST));
                add(null);

                add(new ItemStack(JSGItems.CRYSTAL_TOKRA));
                add(new ItemStack(JSGItems.CIRCUIT_CONTROL_CRYSTAL));
                add(new ItemStack(JSGItems.CRYSTAL_TOKRA));

                add(new ItemStack(Items.QUARTZ));
                add(new ItemStack(Items.QUARTZ));
                add(new ItemStack(Items.QUARTZ));
            }};
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.CRYSTAL_GLYPH_ORI);
        }

        @Override
        public float[] getBeamColors() {
            FluidColors.FloatColors fluidColors = FluidColors.getAverageColor(JSGFluids.SILICON_MOLTEN_WHITE);
            if (fluidColors != null) {
                return fluidColors.colors;
            }
            return new float[]{1f, 1f, 1f};
        }
    };
    public static PCBFabricatorRecipe TR_CRYSTAL_ANCIENT = new PCBFabricatorRecipe() {
        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(JSGFluids.SILICON_MOLTEN_WHITE, ONE_INGOT_IN_FLUID_MB);
        }

        @Override
        public int getWorkingTime() {
            return 200;
        }

        @Override
        public int getEnergyPerTick() {
            return 232;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            return new ArrayList<ItemStack>() {{
                add(new ItemStack(JSGItems.TRINIUM_DUST));
                add(new ItemStack(JSGItems.CRYSTAL_BLUE));
                add(new ItemStack(JSGItems.TRINIUM_DUST));

                add(new ItemStack(JSGItems.CRYSTAL_BLUE_PEGASUS));
                add(new ItemStack(JSGItems.CIRCUIT_CONTROL_CRYSTAL));
                add(new ItemStack(JSGItems.CRYSTAL_BLUE_PEGASUS));

                add(new ItemStack(Items.QUARTZ));
                add(new ItemStack(Items.QUARTZ));
                add(new ItemStack(Items.QUARTZ));
            }};
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.CRYSTAL_GLYPH_ANCIENT);
        }

        @Override
        public float[] getBeamColors() {
            FluidColors.FloatColors fluidColors = FluidColors.getAverageColor(JSGFluids.SILICON_MOLTEN_WHITE);
            if (fluidColors != null) {
                return fluidColors.colors;
            }
            return new float[]{1f, 1f, 1f};
        }
    };

    public static PCBFabricatorRecipe DHD_CRYSTAL_GLYPH = new PCBFabricatorRecipe() {
        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(JSGFluids.SILICON_MOLTEN_WHITE, ONE_INGOT_IN_FLUID_MB);
        }

        @Override
        public int getWorkingTime() {
            return 200;
        }

        @Override
        public int getEnergyPerTick() {
            return 232;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            return new ArrayList<ItemStack>() {{
                add(new ItemStack(JSGItems.CRYSTAL_BLUE_PEGASUS));
                add(new ItemStack(JSGItems.CRYSTAL_ENDER));
                add(new ItemStack(JSGItems.CRYSTAL_RED));

                add(new ItemStack(JSGItems.NAQUADAH_DUST));
                add(new ItemStack(JSGItems.CIRCUIT_CONTROL_CRYSTAL));
                add(new ItemStack(JSGItems.NAQUADAH_DUST));

                add(new ItemStack(Items.QUARTZ));
                add(new ItemStack(Items.QUARTZ));
                add(new ItemStack(Items.QUARTZ));
            }};
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.CRYSTAL_GLYPH_DHD);
        }

        @Override
        public float[] getBeamColors() {
            FluidColors.FloatColors fluidColors = FluidColors.getAverageColor(JSGFluids.SILICON_MOLTEN_WHITE);
            if (fluidColors != null) {
                return fluidColors.colors;
            }
            return new float[]{1f, 1f, 1f};
        }
    };
    public static PCBFabricatorRecipe DHD_CRYSTAL_CONTROL_PEGASUS = new PCBFabricatorRecipe() {
        @Override
        public FluidStack getSubFluidStack() {
            return new FluidStack(JSGFluids.SILICON_MOLTEN_WHITE, ONE_INGOT_IN_FLUID_MB);
        }

        @Override
        public int getWorkingTime() {
            return 200;
        }

        @Override
        public int getEnergyPerTick() {
            return 232;
        }

        @Override
        public ArrayList<ItemStack> getPattern() {
            return new ArrayList<ItemStack>() {{
                add(new ItemStack(JSGItems.NAQUADAH_DUST));
                add(new ItemStack(JSGItems.CRYSTAL_BLUE_PEGASUS));
                add(new ItemStack(JSGItems.NAQUADAH_DUST));

                add(new ItemStack(JSGItems.CRYSTAL_RED));
                add(new ItemStack(JSGItems.CIRCUIT_CONTROL_NAQUADAH));
                add(new ItemStack(JSGItems.CRYSTAL_ENDER));

                add(new ItemStack(Items.QUARTZ));
                add(new ItemStack(Items.QUARTZ));
                add(new ItemStack(Items.QUARTZ));
            }};
        }

        @Override
        public ItemStack getResult() {
            return new ItemStack(JSGItems.CRYSTAL_CONTROL_PEGASUS_DHD);
        }

        @Override
        public float[] getBeamColors() {
            FluidColors.FloatColors fluidColors = FluidColors.getAverageColor(JSGFluids.SILICON_MOLTEN_WHITE);
            if (fluidColors != null) {
                return fluidColors.colors;
            }
            return new float[]{1f, 1f, 1f};
        }
    };

    public static final PCBFabricatorRecipe[] RECIPES = {
            CIRCUIT_CONTROL_CRYSTAL,
            CIRCUIT_CONTROL_NAQUADAH,

            SG_CRYSTAL_GLYPH,
            SG_CRYSTAL_MILKYWAY,
            SG_CRYSTAL_PEGASUS,
            SG_CRYSTAL_UNIVERSE,

            TR_CRYSTAL_GOAULD,
            TR_CRYSTAL_ORI,
            TR_CRYSTAL_ANCIENT,

            DHD_CRYSTAL_GLYPH,
            DHD_CRYSTAL_CONTROL_PEGASUS
    };

    public static void addToConfig() {
        CraftingConfig config = new CraftingConfig(PCBFabricatorRecipe.ID);
        for (PCBFabricatorRecipe recipe : RECIPES) {
            config.addKey(recipe.getResult().getItem().getRegistryName());
        }
        CraftingConfig.addConfig(config);
    }
}
