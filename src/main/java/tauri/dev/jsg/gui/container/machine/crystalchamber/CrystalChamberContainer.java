package tauri.dev.jsg.gui.container.machine.crystalchamber;

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
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import tauri.dev.jsg.gui.container.OpenTabHolderInterface;
import tauri.dev.jsg.gui.util.ContainerHelper;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.stargate.power.StargateAbstractEnergyStorage;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.tileentity.machine.CrystalChamberTile;

import javax.annotation.Nonnull;
import java.util.ArrayList;

import static tauri.dev.jsg.tileentity.machine.CrystalChamberTile.CONTAINER_SIZE;


public class CrystalChamberContainer extends Container implements OpenTabHolderInterface {

    public CrystalChamberTile tile;
    public FluidTank tank;

    public ArrayList<Slot> slots = new ArrayList<>();
    private final BlockPos pos;
    private int lastEnergyStored;
    private int lastFluidStored;
    private int energyTransferedLastTick;
    private int openTabId = -1;

    private long machineStart = 0;
    private long machineEnd = 0;

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
        return true;
    }

    public CrystalChamberContainer(IInventory playerInventory, World world, int x, int y, int z) {
        pos = new BlockPos(x, y, z);
        tile = (CrystalChamberTile) world.getTileEntity(pos);
        if (tile != null) {
            IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

            tank = (FluidTank) tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

            slots.add(new SlotItemHandler(itemHandler, 0, 34, 33));
            slots.add(new SlotItemHandler(itemHandler, 1, 132, 33));
            for (Slot slot : slots)
                addSlotToContainer(slot);

            for (Slot slot : ContainerHelper.generatePlayerSlots(playerInventory, 94))
                addSlotToContainer(slot);
        }
    }

    @Override
    public int getOpenTabId() {
        return openTabId;
    }

    @Override
    public void setOpenTabId(int tabId) {
        openTabId = tabId;
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(@Nonnull EntityPlayer playerIn, int slotId) {
        ItemStack returnStack = ItemStack.EMPTY;
        Slot slot = getSlot(slotId);
        if (slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            returnStack = stack.copy();

            if (slotId == 0) {
                if (stack.getItem() != JSGItems.CRYSTAL_SEED) return ItemStack.EMPTY;
            }

            if (slotId < CONTAINER_SIZE) {
                // to player
                if (!this.mergeItemStack(stack, CONTAINER_SIZE, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            // from player
            else if (!this.mergeItemStack(stack, 0, CONTAINER_SIZE, true)) {
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

        StargateAbstractEnergyStorage energyStorage = (StargateAbstractEnergyStorage) tile.getCapability(CapabilityEnergy.ENERGY, null);

        if (machineStart != tile.getMachineStart()
                || machineEnd != tile.getMachineEnd()
                || (energyStorage != null && (lastEnergyStored != energyStorage.getEnergyStored()
                || energyTransferedLastTick != tile.getEnergyTransferedLastTick()))
                || lastFluidStored != tank.getFluidAmount()
        ) {
            for (IContainerListener listener : listeners) {
                if (listener instanceof EntityPlayerMP) {
                    JSGPacketHandler.INSTANCE.sendTo(new StateUpdatePacketToClient(pos, StateTypeEnum.GUI_UPDATE, tile.getState(StateTypeEnum.GUI_UPDATE)), (EntityPlayerMP) listener);
                }
            }

            if(energyStorage == null) return;
            lastEnergyStored = energyStorage.getEnergyStored();
            energyTransferedLastTick = tile.getEnergyTransferedLastTick();
            machineStart = tile.getMachineStart();
            machineEnd = tile.getMachineEnd();
            lastFluidStored = tank.getFluidAmount();
        }
    }

}
