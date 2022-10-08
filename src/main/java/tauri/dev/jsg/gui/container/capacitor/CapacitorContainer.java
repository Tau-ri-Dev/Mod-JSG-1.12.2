package tauri.dev.jsg.gui.container.capacitor;

import tauri.dev.jsg.gui.util.ContainerHelper;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.stargate.power.StargateAbstractEnergyStorage;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.tileentity.energy.CapacitorTile;
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

public class CapacitorContainer extends Container {

    public CapacitorTile capTile;
    public Slot slot;
    private BlockPos pos;
    private int lastEnergyStored;
    private int energyTransferedLastTick;

    public CapacitorContainer(IInventory playerInventory, World world, int x, int y, int z) {
        pos = new BlockPos(x, y, z);
        capTile = (CapacitorTile) world.getTileEntity(pos);
        IItemHandler itemHandler = capTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        for (Slot slot : ContainerHelper.generatePlayerSlots(playerInventory, 86))
            addSlotToContainer(slot);

        slot = new SlotItemHandler(itemHandler, 0, 80, 35);
        addSlotToContainer(slot);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack stack = getSlot(index).getStack();

        // Transferring from Capacitor to player's inventory
        if (index < 1) {
            if (!mergeItemStack(stack, 1, inventorySlots.size(), false)) {
                return ItemStack.EMPTY;
            }

            putStackInSlot(index, ItemStack.EMPTY);
        }

        // Transferring from player's inventory to Capacitor
        else {
            if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
                if (!slot.getHasStack()) {
                    ItemStack stack1 = stack.copy();
                    stack1.setCount(1);
					slot.putStack(stack1);

                    stack.shrink(1);

                    return ItemStack.EMPTY;
                }
            }

            return ItemStack.EMPTY;
        }

        return stack;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        StargateAbstractEnergyStorage energyStorage = (StargateAbstractEnergyStorage) capTile.getCapability(CapabilityEnergy.ENERGY, null);

        if (lastEnergyStored != energyStorage.getEnergyStored() || energyTransferedLastTick != capTile.getEnergyTransferedLastTick()) {
            for (IContainerListener listener : listeners) {
                if (listener instanceof EntityPlayerMP) {
                    JSGPacketHandler.INSTANCE.sendTo(new StateUpdatePacketToClient(pos, StateTypeEnum.GUI_UPDATE, capTile.getState(StateTypeEnum.GUI_UPDATE)), (EntityPlayerMP) listener);
                }
            }

            lastEnergyStored = energyStorage.getEnergyStored();
            energyTransferedLastTick = capTile.getEnergyTransferedLastTick();
        }
    }
}
