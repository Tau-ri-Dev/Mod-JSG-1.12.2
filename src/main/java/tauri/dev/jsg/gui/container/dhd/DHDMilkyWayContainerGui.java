package tauri.dev.jsg.gui.container.dhd;

import tauri.dev.jsg.JSG;
import net.minecraft.util.ResourceLocation;

public class DHDMilkyWayContainerGui extends DHDAbstractContainerGui {

	public DHDMilkyWayContainerGui(DHDAbstractContainer container) {
		super(container);
	}

	@Override
	public ResourceLocation getBackgroundTexture(){
		return new ResourceLocation(JSG.MOD_ID, "textures/gui/container_dhd.png");
	}
}
