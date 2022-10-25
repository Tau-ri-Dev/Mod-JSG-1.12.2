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
import tauri.dev.jsg.beamer.BeamerStatusEnum;
import tauri.dev.jsg.block.machine.AssemblerBlock;
import tauri.dev.jsg.gui.container.machine.AssemblerContainerGuiUpdate;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.machine.AssemblerRecipe;
import tauri.dev.jsg.machine.AssemblerRecipes;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.renderer.machine.AssemblerRendererState;
import tauri.dev.jsg.sound.JSGSoundHelper;
import tauri.dev.jsg.sound.JSGSoundHelperClient;
import tauri.dev.jsg.sound.SoundEventEnum;
import tauri.dev.jsg.sound.SoundPositionedEnum;
import tauri.dev.jsg.stargate.EnumScheduledTask;
import tauri.dev.jsg.stargate.power.StargateAbstractEnergyStorage;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateProviderInterface;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.tileentity.util.IUpgradable;
import tauri.dev.jsg.util.JSGItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;

import static tauri.dev.jsg.item.JSGItems.*;

public class AssemblerTile extends TileEntity implements IUpgradable, StateProviderInterface, ITickable {

    public AssemblerRendererState rendererState = new AssemblerRendererState();

    public static final int CONTAINER_SIZE = 12;

    public static Item[] getAllowedSchematics() {
        return new Item[]{
                SCHEMATIC_MILKYWAY,
                SCHEMATIC_PEGASUS,
                SCHEMATIC_UNIVERSE,
                SCHEMATIC_TR_GOAULD,
                SCHEMATIC_TR_ORI,
                SCHEMATIC_TR_ANCIENT
        };
    }

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
            sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
            markDirty();
        }
    };
    protected final StargateAbstractEnergyStorage energyStorage = new StargateAbstractEnergyStorage(AssemblerBlock.MAX_ENERGY, AssemblerBlock.MAX_ENERGY_TRANSFER) {
        @Override
        protected void onEnergyChanged() {
            markDirty();
        }
    };
    protected int energyStoredLastTick = 0;
    protected int energyTransferedLastTick = 0;
    protected int machineProgress = 0;
    protected int machineProgressLast = 0;
    protected long machineStart = -1;
    protected long machineEnd = -1;
    protected boolean isWorking = false;
    protected boolean isWorkingLast = false;

    protected AssemblerRecipe currentRecipe = null;

    public int getEnergyTransferedLastTick() {
        return energyTransferedLastTick;
    }

    public StargateAbstractEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            targetPoint = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
            sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
        }
    }

    public long getMachineStart() {
        return machineStart;
    }

    public long getMachineEnd() {
        return machineEnd;
    }

    public int getMachineProgress() {
        return machineProgress;
    }

    public AssemblerRecipe getRecipeIfPossible() {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (int i = 1; i < 10; i++)
            stacks.add(itemStackHandler.getStackInSlot(i));

        Item scheme = itemStackHandler.getStackInSlot(0).getItem();
        ItemStack subStack = itemStackHandler.getStackInSlot(10);

        for (AssemblerRecipe recipe : AssemblerRecipes.RECIPES) {
            if (itemStackHandler.insertItem(11, recipe.getResult(), true).equals(recipe.getResult())) continue;
            if (recipe.isOk(energyStorage.getEnergyStored(), scheme, stacks, subStack)) return recipe;
        }

        return null;
    }

    protected void workIsDone() {
        itemStackHandler.insertItem(11, currentRecipe.getResult(), false);
        for (int i = 1; i < 10; i++) {
            int amount = 0;
            if (currentRecipe.getPattern().size() > (i - 1) && currentRecipe.getPattern().get(i - 1) != null)
                amount = currentRecipe.getPattern().get(i - 1).getCount();
            itemStackHandler.extractItem(i, amount, false);
        }
        if (currentRecipe.removeSubItem())
            itemStackHandler.extractItem(10, currentRecipe.getSubItemStack().getCount(), false);
        else if (currentRecipe.removeDurabilitySubItem() && itemStackHandler.getStackInSlot(10).getItem().isDamageable())
            itemStackHandler.getStackInSlot(10).setItemDamage(itemStackHandler.getStackInSlot(10).getItemDamage() + 1);

        currentRecipe = getRecipeIfPossible();
        if(currentRecipe != null){
            machineStart = this.world.getTotalWorldTime();
            machineEnd = this.world.getTotalWorldTime() + currentRecipe.getWorkingTime();
            machineProgress = 0;
            isWorking = true;
        }
        else{
            machineStart = -1;
            machineEnd = -1;
            machineProgress = 0;
            isWorking = false;
        }
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
                    JSGSoundHelper.playSoundEvent(world, pos, SoundEventEnum.BEAMER_START);
                } else {
                    if (machineStart == machineEnd) machineProgress = 0;
                    else
                        machineProgress = (int) Math.round((((double) (this.world.getTotalWorldTime() - machineStart)) / ((double) (machineEnd - machineStart))) * 100); // returns % of done work
                    energyStorage.extractEnergy(currentRecipe.getEnergyPerTick(), false);

                    /*JSG.info("progress: " + machineProgress);
                    JSG.info("start: " + machineStart);
                    JSG.info("stop: " + machineEnd);*/

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
                JSGSoundHelper.playSoundEvent(world, pos, SoundEventEnum.BEAMER_STOP);
            }

            if (isWorking != isWorkingLast || machineProgress != machineProgressLast) {
                isWorkingLast = isWorking;
                machineProgressLast = machineProgress;
                sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
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

    @Override
    public State getState(StateTypeEnum stateType) {
        switch (stateType) {
            case GUI_UPDATE:
                return new AssemblerContainerGuiUpdate(energyStorage.getEnergyStored(), energyTransferedLastTick, machineStart, machineEnd);
            case RENDERER_UPDATE:
                //ItemStack stack = itemStackHandler.getStackInSlot(11);
                ItemStack stack = currentRecipe != null ? currentRecipe.getResult() : null;
                return new AssemblerRendererState(machineProgress, isWorking, stack);
        }
        return null;
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {
            case GUI_UPDATE:
                return new AssemblerContainerGuiUpdate();
            case RENDERER_UPDATE:
                return new AssemblerRendererState();
        }
        return null;
    }

    @Override
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {
            case GUI_UPDATE:
                AssemblerContainerGuiUpdate guiUpdate = (AssemblerContainerGuiUpdate) state;
                energyStorage.setEnergyStored(guiUpdate.energyStored);
                energyTransferedLastTick = guiUpdate.energyTransferedLastTick;
                machineStart = guiUpdate.machineStart;
                machineEnd = guiUpdate.machineEnd;
                markDirty();
                break;
            case RENDERER_UPDATE:
                rendererState = (AssemblerRendererState) state;
                this.machineProgress = rendererState.machineProgress;
                this.isWorking = rendererState.isWorking;
                JSGSoundHelperClient.playPositionedSoundClientSide(pos, SoundPositionedEnum.BEAMER_LOOP, isWorking);
                break;
        }
    }

    public AssemblerRendererState getRendererState() {
        return rendererState;
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
