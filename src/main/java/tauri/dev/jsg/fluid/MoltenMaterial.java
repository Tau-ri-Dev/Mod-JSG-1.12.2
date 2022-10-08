package tauri.dev.jsg.fluid;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;


public class MoltenMaterial extends Fluid {
    public final String ORE_DICT;
    public final boolean TOOL_FORGE;

    public final int COLOR;

    public MoltenMaterial(String name, String oreDict, int temp) {
        super(name, new ResourceLocation("jsg:fluids/" + name + "_still"), new ResourceLocation("jsg:fluids/" + name + "_flow"));
        setDensity(6000);
        setViscosity(6000);
        setTemperature(temp);
        setLuminosity(4);
        ORE_DICT = oreDict;
        TOOL_FORGE = true;
        COLOR = super.color;
    }

    public MoltenMaterial(String name, String oreDict) {
        this(name, oreDict, 1000);
    }

    @Override
    public int getColor() {
        return COLOR;
    }
}
