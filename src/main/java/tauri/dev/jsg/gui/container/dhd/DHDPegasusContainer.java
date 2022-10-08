package tauri.dev.jsg.gui.container.dhd;

import tauri.dev.jsg.item.JSGItems;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class DHDPegasusContainer extends DHDAbstractContainer{
	public DHDPegasusContainer(IInventory playerInventory, World world, int x, int y, int z) {
		super(playerInventory, world, x, y, z);
	}

	@Override
	public Item getControlCrystal() {
		return JSGItems.CRYSTAL_CONTROL_PEGASUS_DHD;
	}

}
