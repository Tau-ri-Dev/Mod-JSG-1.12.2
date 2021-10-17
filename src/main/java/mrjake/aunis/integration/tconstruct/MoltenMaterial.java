package mrjake.aunis.integration.tconstruct;

import mrjake.aunis.fluid.MoltenFluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;

/**
 * @author matousss
 */


public class MoltenMaterial extends Fluid {
    public final String ORE_DICT;
    public final boolean TOOL_FORGE;

    public final int COLOR;

    public MoltenMaterial(String name, String oreDict, boolean toolForge, int color) {
        super(name, new ResourceLocation("tconstruct:blocks/fluids/molten_metal"), new ResourceLocation("tconstruct:blocks/fluids/molten_metal_flow"));
        setDensity(2000);
        setViscosity(6000);
        setTemperature(1000);
        setLuminosity(4);
        ORE_DICT = oreDict;
        TOOL_FORGE = toolForge;
        COLOR = color;
    }
    public MoltenMaterial(String name, String oreDict, boolean toolForge) {
        super(name, new ResourceLocation("tconstruct:blocks/fluids/molten_metal"), new ResourceLocation("tconstruct:blocks/fluids/molten_metal_flow"));
        setDensity(2000);
        setViscosity(6000);
        setTemperature(1000);
        setLuminosity(4);
        ORE_DICT = oreDict;
        TOOL_FORGE = toolForge;
        COLOR = super.color;
    }

    @Override
    public int getColor() {
        return COLOR;
    }
}
