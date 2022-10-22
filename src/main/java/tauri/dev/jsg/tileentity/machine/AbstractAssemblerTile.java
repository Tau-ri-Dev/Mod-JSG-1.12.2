package tauri.dev.jsg.tileentity.machine;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.machine.StargateAssemblerBlock;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.machine.SGAssemblerRecipe;
import tauri.dev.jsg.machine.SGAssemblerRecipes;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.stargate.power.StargateAbstractEnergyStorage;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateProviderInterface;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.tileentity.util.IUpgradable;
import tauri.dev.jsg.util.JSGItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public abstract class AbstractAssemblerTile extends TileEntity implements IUpgradable, StateProviderInterface, ITickable {

    public static final int CONTAINER_SIZE = 12;

    protected NetworkRegistry.TargetPoint targetPoint;
    protected final ItemStackHandler itemStackHandler = new JSGItemStackHandler(CONTAINER_SIZE) {

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot == 11) return false; // output slot
            if (slot == 0) {
                return JSGItems.isInItemsArray(stack.getItem(), getAllowedSchematics());
            }
            return true;
        }

        @Override
        protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
            if (slot == 0) return 1;
            return 64;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    };
    protected final StargateAbstractEnergyStorage energyStorage = new StargateAbstractEnergyStorage(StargateAssemblerBlock.MAX_ENERGY, StargateAssemblerBlock.MAX_ENERGY_TRANSFER) {
        @Override
        protected void onEnergyChanged() {
            markDirty();
        }
    };
    protected int energyStoredLastTick = 0;
    protected int energyTransferedLastTick = 0;
    protected int machineProgress = 0;
    protected long machineStart = -1;
    protected long machineEnd = -1;
    protected boolean isWorking = false;

    protected SGAssemblerRecipe currentRecipe = null;

    public int getEnergyTransferedLastTick() {
        return energyTransferedLastTick;
    }

    public StargateAbstractEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public abstract Item[] getAllowedSchematics();

    @Override
    public void onLoad() {
        if (!world.isRemote)
            targetPoint = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
    }

    public long getMachineStart() {
        return machineStart;
    }

    public long getMachineEnd() {
        return machineEnd;
    }

    public SGAssemblerRecipe getRecipeIfPossible() {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (int i = 1; i < 10; i++)
            stacks.add(itemStackHandler.getStackInSlot(i));

        Item scheme = itemStackHandler.getStackInSlot(0).getItem();
        ItemStack subStack = itemStackHandler.getStackInSlot(10);

        for (SGAssemblerRecipe recipe : SGAssemblerRecipes.RECIPES) {
            if (itemStackHandler.insertItem(11, recipe.getResult(), true).equals(recipe.getResult())) continue;
            if (recipe.isOk(energyStorage.getEnergyStored(), scheme, stacks, subStack)) return recipe;
        }

        return null;
    }

    protected void workIsDone() {
        itemStackHandler.insertItem(11, currentRecipe.getResult(), false);
        for (int i = 1; i < 10; i++) {
            int amount = 0;
            if (currentRecipe.getPattern().size() > (i - 1)) amount = currentRecipe.getPattern().get(i - 1).getCount();
            itemStackHandler.extractItem(i, amount, false);
        }
        if (currentRecipe.removeSubItem())
            itemStackHandler.extractItem(10, currentRecipe.getSubItemStack().getCount(), false);
        else if (currentRecipe.removeDurabilitySubItem() && itemStackHandler.getStackInSlot(10).getItem().isDamageable())
            itemStackHandler.getStackInSlot(10).setItemDamage(itemStackHandler.getStackInSlot(10).getItemDamage() + 1);

        currentRecipe = null;
        machineStart = -1;
        machineEnd = -1;
        isWorking = false;
        markDirty();
    }


    @Override
    public void update() {
        if (!world.isRemote) {
            // -------------------------------
            // ENERGY UPDATING

            energyTransferedLastTick = energyStorage.getEnergyStored() - energyStoredLastTick;
            energyStoredLastTick = energyStorage.getEnergyStored();


            currentRecipe = getRecipeIfPossible();
            if (isWorking) {
                if (currentRecipe == null) {
                    isWorking = false;
                    machineProgress = 0;
                    machineStart = -1;
                    machineEnd = -1;
                    markDirty();
                    sendState(StateTypeEnum.GUI_UPDATE, getState(StateTypeEnum.GUI_UPDATE));
                } else {
                    if (machineStart == machineEnd) machineProgress = 0;
                    else
                        machineProgress = (int) Math.round((((double) (this.world.getTotalWorldTime() - machineStart)) / ((double) (machineEnd - machineStart))) * 100); // returns % of done work
                    energyStorage.extractEnergy(currentRecipe.getEnergyPerTick(), false);

                    JSG.info("progress: " + machineProgress);
                    JSG.info("start: " + machineStart);
                    JSG.info("stop: " + machineEnd);

                    if (machineProgress >= 100) {
                        workIsDone();
                    }
                }
            } else if (currentRecipe != null) {
                isWorking = true;
                machineStart = this.world.getTotalWorldTime();
                machineEnd = currentRecipe.getWorkingTime() + this.world.getTotalWorldTime();
                markDirty();
                sendState(StateTypeEnum.GUI_UPDATE, getState(StateTypeEnum.GUI_UPDATE));
            }
            markDirty();
        }
    }

    protected void sendState(StateTypeEnum type, State state) {
        if (world.isRemote) return;
        if (targetPoint != null) {
            JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, type, state), targetPoint);
        } else {
            JSG.logger.debug("targetPoint was null trying to send " + type + " from " + this.getClass().getCanonicalName());
        }
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityEnergy.ENERGY) || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY)
            return CapabilityEnergy.ENERGY.cast(getEnergyStorage());

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemStackHandler);

        return super.getCapability(capability, facing);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("energyStorage", getEnergyStorage().serializeNBT());
        compound.setTag("itemStackHandler", itemStackHandler.serializeNBT());

        compound.setBoolean("isWorking", isWorking);
        compound.setLong("machineStart", machineStart);
        compound.setLong("machineEnd", machineEnd);
        compound.setInteger("progress", machineProgress);

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        getEnergyStorage().deserializeNBT(compound.getCompoundTag("energyStorage"));
        itemStackHandler.deserializeNBT(compound.getCompoundTag("itemStackHandler"));

        isWorking = compound.getBoolean("isWorking");
        machineStart = compound.getLong("machineStart");
        machineEnd = compound.getLong("machineEnd");
        machineProgress = compound.getInteger("machineProgress");

        super.readFromNBT(compound);
    }
}
