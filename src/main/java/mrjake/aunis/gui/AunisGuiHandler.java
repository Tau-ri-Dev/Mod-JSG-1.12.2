package mrjake.aunis.gui;

import mrjake.aunis.gui.container.*;
import net.minecraft.entity.player.EntityPlayer;
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
				return new StargateContainerGui(new StargateContainer(player.inventory, world, x, y ,z));
				
			case GUI_CAPACITOR:	
				return new CapacitorContainerGui(new CapacitorContainer(player.inventory, world, x, y ,z));
				
			case GUI_BEAMER:
				return new BeamerContainerGui(new BeamerContainer(player.inventory, world, x, y ,z));
				
		}
		
		return null;
	}

}
