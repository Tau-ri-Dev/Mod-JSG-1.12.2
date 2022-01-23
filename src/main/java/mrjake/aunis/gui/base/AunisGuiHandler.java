package mrjake.aunis.gui.base;

import mrjake.aunis.gui.GuiIdEnum;
import mrjake.aunis.gui.container.*;
import mrjake.aunis.gui.container.zpm.ZPMContainer;
import mrjake.aunis.gui.container.zpm.ZPMContainerGui;
import mrjake.aunis.gui.container.zpmhub.ZPMHubContainer;
import mrjake.aunis.gui.container.zpmhub.ZPMHubContainerGui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class AunisGuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (GuiIdEnum.valueOf(ID)) {
			case GUI_DHD:
				return new DHDContainer(player.inventory, world, x, y ,z);

			case GUI_PEGASUS_DHD:
				return new DHDPegasusContainer(player.inventory, world, x, y ,z);
			
			case GUI_STARGATE:
				return new StargateContainer(player.inventory, world, x, y ,z);
				
			case GUI_CAPACITOR:
				return new CapacitorContainer(player.inventory, world, x, y ,z);
				
			case GUI_BEAMER:
				return new BeamerContainer(player.inventory, world, x, y ,z);

			case GUI_ZPM:
				return new ZPMContainer(player.inventory, world, x, y ,z);

			case GUI_ZPMHUB:
				return new ZPMHubContainer(player.inventory, world, x, y ,z);
		}
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (GuiIdEnum.valueOf(ID)) {
			case GUI_DHD:
				return new DHDContainerGui(new DHDContainer(player.inventory, world, x, y ,z));

			case GUI_PEGASUS_DHD:
				return new DHDPegasusContainerGui(new DHDPegasusContainer(player.inventory, world, x, y ,z));
			
			case GUI_STARGATE:	
				return new StargateContainerGui(new BlockPos(x, y, z), new StargateContainer(player.inventory, world, x, y ,z));
				
			case GUI_CAPACITOR:	
				return new CapacitorContainerGui(new CapacitorContainer(player.inventory, world, x, y ,z));
				
			case GUI_BEAMER:
				return new BeamerContainerGui(new BeamerContainer(player.inventory, world, x, y ,z));

			case GUI_ZPM:
				return new ZPMContainerGui(new ZPMContainer(player.inventory, world, x, y ,z));

			case GUI_ZPMHUB:
				return new ZPMHubContainerGui(new ZPMHubContainer(player.inventory, world, x, y ,z));
				
		}
		
		return null;
	}

}
