package tauri.dev.jsg.fluid;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class JSGFluids {
    public static final MoltenFluid SILICON_MOLTEN_BLACK = new MoltenFluid("silicon_molten_black");
    public static final MoltenFluid SILICON_MOLTEN_RED = new MoltenFluid("silicon_molten_red");
    public static final MoltenFluid SILICON_MOLTEN_BLUE = new MoltenFluid("silicon_molten_blue");
    public static final MoltenFluid SILICON_MOLTEN_ENDER = new MoltenFluid("silicon_molten_ender");
    public static final MoltenFluid SILICON_MOLTEN_PEGASUS = new MoltenFluid("silicon_molten_pegasus");
    public static final MoltenFluid SILICON_MOLTEN_YELLOW = new MoltenFluid("silicon_molten_yellow");
    public static final MoltenFluid SILICON_MOLTEN_WHITE = new MoltenFluid("silicon_molten_white");
    public static final MoltenFluid NAQUADAH_MOLTEN_RAW = new MoltenFluid("naquadah_molten_raw");
    public static final MoltenMaterial NAQUADAH_MOLTEN_REFINED = new MoltenMaterial("naquadah_molten_refined", "liquidNaquadahRefined");
    public static final MoltenMaterial MOLTEN_NAQUADAH_ALLOY = new MoltenMaterial("naquadah_molten_alloy", "liquidNaquadahAlloy");
    public static final MoltenMaterial MOLTEN_TITANIUM = new MoltenMaterial("titanium_molten", "liquidTitanium");
    public static final MoltenMaterial MOLTEN_TRINIUM = new MoltenMaterial("trinium_molten", "liquidTrinium");

    public static final Map<String, JSGBlockFluid> blockFluidMap = new HashMap<>();
    private static final Fluid[] FLUIDS = {
            SILICON_MOLTEN_BLACK,
            SILICON_MOLTEN_RED,
            SILICON_MOLTEN_BLUE,
            SILICON_MOLTEN_ENDER,
            SILICON_MOLTEN_PEGASUS,
            SILICON_MOLTEN_YELLOW,
            SILICON_MOLTEN_WHITE,

            NAQUADAH_MOLTEN_RAW,
            NAQUADAH_MOLTEN_REFINED,
            MOLTEN_NAQUADAH_ALLOY,

            MOLTEN_TITANIUM,
            MOLTEN_TRINIUM
    };

    public static void registerFluids() {
        registerFluids(FLUIDS);
    }

    private static void registerFluids(Fluid[] fluids) {
        for (Fluid fluid : fluids) {
            FluidRegistry.registerFluid(fluid);
            FluidRegistry.addBucketForFluid(fluid);

            JSGBlockFluid blockFluid = new JSGBlockFluid(fluid, fluid.getName());
            ForgeRegistries.BLOCKS.register(blockFluid);
            blockFluidMap.put(fluid.getName(), blockFluid);
        }
    }
}
