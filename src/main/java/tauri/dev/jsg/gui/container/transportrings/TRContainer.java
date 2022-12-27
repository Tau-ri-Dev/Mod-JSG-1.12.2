package tauri.dev.jsg.gui.container.transportrings;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.gui.container.OpenTabHolderInterface;
import tauri.dev.jsg.gui.util.ContainerHelper;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.stargate.power.StargateClassicEnergyStorage;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsAbstractTile;

import javax.annotation.Nonnull;
import java.util.Objects;

public class TRContainer extends Container implements OpenTabHolderInterface {

    public TransportRingsAbstractTile trTile;
    public boolean isOperator;

    private final BlockPos pos;
    private int lastEnergyStored;
    private int energyTransferedLastTick;
    private int ringsDistance;
    private String ringsName;
    private int lastProgress;
    private int openTabId = -1;

    @Override
    public int getOpenTabId() {
        return openTabId;
    }

    @Override
    public void setOpenTabId(int tabId) {
        openTabId = tabId;
    }

    public TRContainer(IInventory playerInventory, World world, int x, int y, int z, boolean isOperator) {
        this.isOperator = isOperator;
        pos = new BlockPos(x, y, z);
        trTile = (TransportRingsAbstractTile) world.getTileEntity(pos);
        IItemHandler itemHandler = Objects.requireNonNull(trTile).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        // Upgrades 1x4 (index 0-3)
        for (int col = 0; col < 4; col++) {
            addSlotToContainer(new SlotItemHandler(itemHandler, col, 9 + 18 * col, 27));
        }

        // Capacitors 1x3 (index 4-6)
        for (int col = 0; col < 3; col++) {
            final int capacitorIndex = col;

            addSlotToContainer(new SlotItemHandler(itemHandler, col + 4, 115 + 18 * col, 27) {
                @Override
                public boolean isEnabled() {
                    // getHasStack() is a compatibility thing for when players already had their capacitors in the gate.
                    return (capacitorIndex + 1 <= trTile.getSupportedCapacitors()) || getHasStack();
                }
            });
        }

        // Page slots (index 7-9)
        for (int i = 0; i < 3; i++) {
            addSlotToContainer(new SlotItemHandler(itemHandler, i + 7, -22, 89 + 22 * i));
        }
        for (Slot slot : ContainerHelper.generatePlayerSlots(playerInventory, 91))
            addSlotToContainer(slot);
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
        return true;
    }

    @Override
    public void updateProgressBar(int id, int data) {
        trTile.setPageProgress(data);
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(@Nonnull EntityPlayer player, int index) {
        ItemStack stack = getSlot(index).getStack();

        // Transferring from tile to player's inventory
        if (index < 10) {
            if (!mergeItemStack(stack, 10, inventorySlots.size(), false)) {
                return ItemStack.EMPTY;
            }

            putStackInSlot(index, ItemStack.EMPTY);
        }

        // Transferring from player's inventory to tile
        else {
            // Capacitors
            if (stack.getItem() == Item.getItemFromBlock(JSGBlocks.CAPACITOR_BLOCK)) {
                for (int i = 4; i < 7; i++) {
                    if (!getSlot(i).getHasStack() && getSlot(i).isItemValid(stack)) {
                        ItemStack stack1 = stack.copy();
                        stack1.setCount(1);

                        putStackInSlot(i, stack1);
                        stack.shrink(1);

                        return stack;
                    }
                }
            } else if (TransportRingsAbstractTile.TransportRingsUpgradeEnum.contains(stack.getItem()) && !trTile.hasUpgrade(stack.getItem())) {
                for (int i = 0; i < 4; i++) {
                    if (!getSlot(i).getHasStack() && TransportRingsAbstractTile.TransportRingsUpgradeEnum.valueOf(stack.getItem()).slotId == i) {
                        ItemStack stack1 = stack.copy();
                        stack1.setCount(1);

                        putStackInSlot(i, stack1);
                        stack.shrink(1);

                        return ItemStack.EMPTY;
                    }
                }
            } else if (openTabId >= 0 && openTabId <= 2 && getSlot(7 + openTabId).isItemValid(stack)) {
                if (!getSlot(7 + openTabId).getHasStack()) {
                    ItemStack stack1 = stack.copy();
                    stack1.setCount(1);

                    putStackInSlot(7 + openTabId, stack1);
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

        StargateClassicEnergyStorage energyStorage = (StargateClassicEnergyStorage) trTile.getCapability(CapabilityEnergy.ENERGY, null);

        if (lastEnergyStored != Objects.requireNonNull(energyStorage).getEnergyStoredInternally()
                || energyTransferedLastTick != trTile.getEnergyTransferedLastTick()
                || ringsName == null
                || !(ringsName.equals(trTile.getRings().getName()))
                || ringsDistance != trTile.getRings().getRingsDistance()
        ) {
            for (IContainerListener listener : listeners) {
                if (listener instanceof EntityPlayerMP) {
                    JSGPacketHandler.INSTANCE.sendTo(new StateUpdatePacketToClient(pos, StateTypeEnum.GUI_UPDATE, trTile.getState(StateTypeEnum.GUI_UPDATE)), (EntityPlayerMP) listener);
                }
            }

            lastEnergyStored = energyStorage.getEnergyStoredInternally();
            energyTransferedLastTick = trTile.getEnergyTransferedLastTick();
            ringsName = trTile.getRings().getName();
            ringsDistance = trTile.getRings().getRingsDistance();
        }

        if (lastProgress != trTile.getPageProgress()) {
            for (IContainerListener listener : listeners) {
                listener.sendWindowProperty(this, 0, trTile.getPageProgress());
            }

            lastProgress = trTile.getPageProgress();
        }
    }

    @Override
    public void addListener(@Nonnull IContainerListener listener) {
        super.addListener(listener);

        if (listener instanceof EntityPlayerMP) {
            JSGPacketHandler.INSTANCE.sendTo(new StateUpdatePacketToClient(pos, StateTypeEnum.GUI_STATE, trTile.getState(StateTypeEnum.GUI_STATE)), (EntityPlayerMP) listener);
        }
    }
}
