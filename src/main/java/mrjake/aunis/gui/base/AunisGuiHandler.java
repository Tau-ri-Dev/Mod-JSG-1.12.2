package mrjake.aunis.gui.base;

import mrjake.aunis.gui.GuiIdEnum;
import mrjake.aunis.gui.container.beamer.BeamerContainer;
import mrjake.aunis.gui.container.beamer.BeamerContainerGui;
import mrjake.aunis.gui.container.capacitor.CapacitorContainer;
import mrjake.aunis.gui.container.capacitor.CapacitorContainerGui;
import mrjake.aunis.gui.container.dhd.DHDMilkyWayContainerGui;
import mrjake.aunis.gui.container.dhd.DHDMilkyWayContainer;
import mrjake.aunis.gui.container.dhd.DHDPegasusContainer;
import mrjake.aunis.gui.container.dhd.DHDPegasusContainerGui;
import mrjake.aunis.gui.container.stargate.StargateContainer;
import mrjake.aunis.gui.container.stargate.StargateContainerGui;
import mrjake.aunis.gui.container.transportrings.TRContainer;
import mrjake.aunis.gui.container.transportrings.TRGui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class AunisGuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (GuiIdEnum.valueOf(ID)) {
			case GUI_DHD:
				return new DHDMilkyWayContainer(player.inventory, world, x, y ,z);

			case GUI_PEGASUS_DHD:
				return new DHDPegasusContainer(player.inventory, world, x, y ,z);
			
			case GUI_STARGATE:
				return new StargateContainer(player.inventory, world, x, y ,z);
				
			case GUI_CAPACITOR:
				return new CapacitorContainer(player.inventory, world, x, y ,z);
				
			case GUI_BEAMER:
				return new BeamerContainer(player.inventory, world, x, y ,z);

			case GUI_RINGS:
				return new TRContainer(player.inventory, world, x, y ,z);
		}
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (GuiIdEnum.valueOf(ID)) {
			case GUI_DHD:
				return new DHDMilkyWayContainerGui(new DHDMilkyWayContainer(player.inventory, world, x, y ,z));

			case GUI_PEGASUS_DHD:
				return new DHDPegasusContainerGui(new DHDPegasusContainer(player.inventory, world, x, y ,z));
			
			case GUI_STARGATE:	
				return new StargateContainerGui(new BlockPos(x, y, z), new StargateContainer(player.inventory, world, x, y ,z));
				
			case GUI_CAPACITOR:	
				return new CapacitorContainerGui(new CapacitorContainer(player.inventory, world, x, y ,z));
				
			case GUI_BEAMER:
				return new BeamerContainerGui(new BeamerContainer(player.inventory, world, x, y ,z));

			case GUI_RINGS:
				return new TRGui(new BlockPos(x, y, z), new TRContainer(player.inventory, world, x, y ,z));
				
		}
		
		return null;
	}

}
