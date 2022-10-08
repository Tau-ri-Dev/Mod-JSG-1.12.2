package tauri.dev.jsg.fluid;

import tauri.dev.jsg.JSG;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class JSGBlockFluid extends BlockFluidClassic {
	
	public JSGBlockFluid(Fluid fluid, String name) {
		super(fluid, Material.LAVA);
		
		setRegistryName(new ResourceLocation(JSG.MOD_ID, name));
		setUnlocalizedName(getRegistryName().toString());
	}	
}
