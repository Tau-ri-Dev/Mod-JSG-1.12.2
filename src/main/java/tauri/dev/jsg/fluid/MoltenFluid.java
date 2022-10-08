package tauri.dev.jsg.fluid;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class MoltenFluid extends Fluid {
	
	public MoltenFluid(String name) {		
		super(name, new ResourceLocation("jsg:fluids/" + name + "_still"), new ResourceLocation("jsg:fluids/" + name + "_flow"));
		
		setDensity(6000);
		setViscosity(6000);
		setTemperature(2600);
		setLuminosity(4);
	}
}
