package tauri.dev.jsg.gui.container.zpmslot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import tauri.dev.jsg.gui.container.zpmhub.ZPMHubContainer;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class ZPMSlotContainer extends ZPMHubContainer {

    @Override
    @Nonnull
    public ArrayList<Slot> getSlots(IItemHandler itemHandler) {
        ArrayList<Slot> slots = new ArrayList<>();
        slots.add(new SlotItemHandler(itemHandler, 0, 80, 25));
        return slots;
    }

    public ZPMSlotContainer(IInventory playerInventory, World world, int x, int y, int z) {
        super(playerInventory, world, x, y, z);
    }
}
