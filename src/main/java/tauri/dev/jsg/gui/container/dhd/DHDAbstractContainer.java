package tauri.dev.jsg.gui.container.dhd;

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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import tauri.dev.jsg.fluid.JSGFluids;
import tauri.dev.jsg.gui.container.OpenTabHolderInterface;
import tauri.dev.jsg.gui.util.ContainerHelper;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDAbstractTile;
import tauri.dev.jsg.tileentity.util.ReactorStateEnum;

public abstract class DHDAbstractContainer extends Container implements OpenTabHolderInterface {

    public Slot slotCrystal;
    public FluidTank tankNaquadah;
    public DHDAbstractTile dhdTile;

    protected BlockPos pos;
    protected int tankLastAmount;
    protected ReactorStateEnum lastReactorState;
    protected boolean lastLinked;
    protected int openTabId = -1;

    @Override
    public int getOpenTabId() {
        return openTabId;
    }

    @Override
    public void setOpenTabId(int tabId) {
        openTabId = tabId;
    }

    public DHDAbstractContainer(IInventory playerInventory, World world, int x, int y, int z) {
        pos = new BlockPos(x, y, z);
        dhdTile = (DHDAbstractTile) world.getTileEntity(pos);
        IItemHandler itemHandler = dhdTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        // Crystal slot (index 0)
        slotCrystal = new SlotItemHandler(itemHandler, 0, 81, 40);
        addSlotToContainer(slotCrystal);

        tankNaquadah = (FluidTank) dhdTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

        // Upgrades (index 1-3)
        for (int col = 0; col < 3; col++) {
            addSlotToContainer(new SlotItemHandler(itemHandler, col + 1, 9 + 18 * col, 40));
        }

        // Bucket (index 4)
        addSlotToContainer(new SlotItemHandler(itemHandler, 4, 116, 23));

        // Biome overlay slot (index 5)
        addSlotToContainer(new SlotItemHandler(itemHandler, 5, 0, 0));

        for (Slot slot : ContainerHelper.generatePlayerSlots(playerInventory, 91))
            addSlotToContainer(slot);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = getSlot(index).getStack();

        // Transfering from DHD to player's inventory
        if (index < 6) {
            if (!mergeItemStack(stack, 5, inventorySlots.size(), false)) {
                return ItemStack.EMPTY;
            }

            putStackInSlot(index, ItemStack.EMPTY);
        }

        // Transfering from player's inventory to DHD
        else {
            if (stack.getItem() == getControlCrystal()) {
                if (!slotCrystal.getHasStack()) {
                    ItemStack stack1 = stack.copy();
                    stack1.setCount(1);
                    slotCrystal.putStack(stack1);

                    stack.shrink(1);

                    return ItemStack.EMPTY;
                }
            } else if (DHDAbstractTile.SUPPORTED_UPGRADES.contains(stack.getItem()) && !dhdTile.hasUpgrade(stack.getItem())) {
                for (int i = 1; i < 4; i++) {
                    if (!getSlot(i).getHasStack()) {
                        ItemStack stack1 = stack.copy();
                        stack1.setCount(1);

                        putStackInSlot(i, stack1);
                        stack.shrink(1);

                        return stack;
                    }
                }
            } else if (stack.getItem() instanceof UniversalBucket) {
                FluidStack fluid = ((UniversalBucket) stack.getItem()).getFluid(stack);
                if (fluid != null) {
                    if (fluid.getFluid() == JSGFluids.NAQUADAH_MOLTEN_REFINED) {
                        if (!getSlot(4).getHasStack()) {
                            ItemStack stack1 = stack.copy();
                            stack1.setCount(1);

                            putStackInSlot(4, stack1);
                            stack.shrink(1);

                            return stack;
                        }
                    }
                }
            }

            // Biome override blocks
            else if (openTabId == 0 && getSlot(5).isItemValid(stack)) {
                if (!getSlot(5).getHasStack()) {
                    ItemStack stack1 = stack.copy();
                    stack1.setCount(1);

                    putStackInSlot(5, stack1);
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

        if (tankLastAmount != tankNaquadah.getFluidAmount() || lastReactorState != dhdTile.getReactorState() || lastLinked != dhdTile.isLinked()) {
            for (IContainerListener listener : listeners) {
                if (listener instanceof EntityPlayerMP) {
                    JSGPacketHandler.INSTANCE.sendTo(new StateUpdatePacketToClient(pos, StateTypeEnum.GUI_UPDATE, new DHDContainerGuiUpdate(tankNaquadah.getFluidAmount(), tankNaquadah.getCapacity(), dhdTile.getReactorState(), dhdTile.isLinked())), (EntityPlayerMP) listener);
                }
            }

            tankLastAmount = tankNaquadah.getFluidAmount();
            lastReactorState = dhdTile.getReactorState();
            lastLinked = dhdTile.isLinked();
        }
    }

    public abstract Item getControlCrystal();
}
