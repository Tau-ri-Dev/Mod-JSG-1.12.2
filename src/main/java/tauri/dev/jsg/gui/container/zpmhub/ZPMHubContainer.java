package tauri.dev.jsg.gui.container.zpmhub;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import tauri.dev.jsg.gui.util.ContainerHelper;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.stargate.power.StargateClassicEnergyStorage;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.tileentity.energy.ZPMHubTile;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class ZPMHubContainer extends Container {

    public ZPMHubTile hubTile;
    public ArrayList<Slot> slots;
    private final BlockPos pos;
    private int lastEnergyStored;
    private int energyTransferedLastTick;

    @Nonnull
    public ArrayList<Slot> getSlots(IItemHandler itemHandler){
        ArrayList<Slot> slots = new ArrayList<>();
        slots.add(new SlotItemHandler(itemHandler, 0, 80, 27));
        slots.add(new SlotItemHandler(itemHandler, 1, 56, 51));
        slots.add(new SlotItemHandler(itemHandler, 2, 104, 51));
        return slots;
    }

    public ZPMHubContainer(IInventory playerInventory, World world, int x, int y, int z) {
        pos = new BlockPos(x, y, z);
        hubTile = (ZPMHubTile) world.getTileEntity(pos);
        IItemHandler itemHandler = null;
        if (hubTile != null) {
            itemHandler = hubTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        }

        slots = getSlots(itemHandler);

        for (Slot slot : slots)
            addSlotToContainer(slot);

        for (Slot slot : ContainerHelper.generatePlayerSlots(playerInventory, getInventoryY()))
            addSlotToContainer(slot);
    }

    public int getInventoryY() {
        return 97;
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer player) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(@Nonnull EntityPlayer playerIn, int slotId) {
        ItemStack returnStack = ItemStack.EMPTY;
        Slot slot = getSlot(slotId);
        if (slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            returnStack = stack.copy();

            if (slotId < slots.size()) {
                // to player
                if (!this.mergeItemStack(stack, slots.size(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            // from player
            else if (!this.mergeItemStack(stack, 0, slots.size(), true)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return returnStack;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        StargateClassicEnergyStorage energyStorage = (StargateClassicEnergyStorage) hubTile.getCapability(CapabilityEnergy.ENERGY, null);

        if (energyStorage != null && (lastEnergyStored != energyStorage.getEnergyStoredInternally() || energyTransferedLastTick != hubTile.getEnergyTransferedLastTick())) {
            for (IContainerListener listener : listeners) {
                if (listener instanceof EntityPlayerMP) {
                    JSGPacketHandler.INSTANCE.sendTo(new StateUpdatePacketToClient(pos, StateTypeEnum.GUI_UPDATE, hubTile.getState(StateTypeEnum.GUI_UPDATE)), (EntityPlayerMP) listener);
                }
            }

            lastEnergyStored = energyStorage.getEnergyStoredInternally();
            energyTransferedLastTick = hubTile.getEnergyTransferedLastTick();
        }
    }
}
