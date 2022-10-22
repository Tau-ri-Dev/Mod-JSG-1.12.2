package tauri.dev.jsg.gui.container.machine.sgassembler;

import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;
import tauri.dev.jsg.gui.container.machine.AbstractAssemblerContainer;

public class SGAssemblerContainer extends AbstractAssemblerContainer {
    public SGAssemblerContainer(IInventory playerInventory, World world, int x, int y, int z) {
        super(playerInventory, world, x, y, z);
    }
}
