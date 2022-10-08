package tauri.dev.jsg.fluid;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class JSGFluids {
    public static MoltenFluid moltenSiliconBlack = new MoltenFluid(
            "silicon_molten_black"
    );
    public static MoltenFluid moltenSiliconRed = new MoltenFluid(
            "silicon_molten_red"
    );
    public static MoltenFluid moltenSiliconBlue = new MoltenFluid(
            "silicon_molten_blue"
    );
    public static MoltenFluid moltenSiliconEnder = new MoltenFluid(
            "silicon_molten_ender"
    );
    public static MoltenFluid moltenSiliconYellow = new MoltenFluid(
            "silicon_molten_yellow"
    );
    public static MoltenFluid moltenSiliconWhite = new MoltenFluid(
            "silicon_molten_white"
    );
    public static MoltenFluid moltenNaquadahRaw = new MoltenFluid(
            "naquadah_molten_raw"
    );
    public static MoltenMaterial moltenNaquadahRefined = new MoltenMaterial(
            "naquadah_molten_refined",
            "NaquadahRaw"
    );
    public static MoltenMaterial moltenNaquadahAlloy = new MoltenMaterial(
            "naquadah_molten_alloy",
            "NaquadahRefined"
    );
    public static MoltenMaterial moltenTitanium = new MoltenMaterial(
            "titanium_molten",
            "Titanium"
    );
    public static MoltenMaterial moltenTrinium = new MoltenMaterial(
            "trinium_molten",
            "Trinium"
    );
    public static Map<String, JSGBlockFluid> blockFluidMap = new HashMap<>();
    private static Fluid[] fluids = {
            moltenSiliconBlack,
            moltenSiliconRed,
            moltenSiliconBlue,
            moltenSiliconEnder,
            moltenSiliconYellow,
            moltenSiliconWhite,

            moltenNaquadahRaw,
            moltenNaquadahRefined,
            moltenNaquadahAlloy,

            moltenTitanium,
            moltenTrinium
    };

    public static void registerFluids() {
        registerFluids(fluids);
    }

    // -------------------
    // WATER LOGGING - preparation for 1.13 and higher

    public static void registerFluids(Fluid[] fluids) {
        for (Fluid fluid : fluids) {
            FluidRegistry.registerFluid(fluid);
            FluidRegistry.addBucketForFluid(fluid);

            JSGBlockFluid blockFluid = new JSGBlockFluid(fluid, fluid.getName());
            ForgeRegistries.BLOCKS.register(blockFluid);
            blockFluidMap.put(fluid.getName(), blockFluid);
        }
    }
}
