package mrjake.aunis.gui.container.dhd;

import mrjake.aunis.Aunis;
import net.minecraft.util.ResourceLocation;

public class DHDMilkyWayContainerGui extends DHDAbstractContainerGui {

	public DHDMilkyWayContainerGui(DHDAbstractContainer container) {
		super(container);
	}

	@Override
	public ResourceLocation getBackgroundTexture(){
		return new ResourceLocation(Aunis.MOD_ID, "textures/gui/container_dhd.png");
	}
}
