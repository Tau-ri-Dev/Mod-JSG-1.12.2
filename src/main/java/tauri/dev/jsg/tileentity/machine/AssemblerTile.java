package tauri.dev.jsg.tileentity.machine;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tauri.dev.jsg.block.machine.AssemblerBlock;
import tauri.dev.jsg.gui.container.machine.assembler.AssemblerContainerGuiUpdate;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.machine.AbstractMachineRecipe;
import tauri.dev.jsg.machine.assembler.AssemblerRecipe;
import tauri.dev.jsg.machine.assembler.AssemblerRecipes;
import tauri.dev.jsg.power.general.SmallEnergyStorage;
import tauri.dev.jsg.renderer.machine.AbstractMachineRendererState;
import tauri.dev.jsg.renderer.machine.AssemblerRendererState;
import tauri.dev.jsg.sound.JSGSoundHelper;
import tauri.dev.jsg.sound.SoundEventEnum;
import tauri.dev.jsg.sound.SoundPositionedEnum;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.util.JSGItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;

import static tauri.dev.jsg.item.JSGItems.*;

public class AssemblerTile extends AbstractMachineTile {
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
    protected final JSGItemStackHandler itemStackHandler = new JSGItemStackHandler(CONTAINER_SIZE) {

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
            return super.getStackLimit(slot, stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
            onItemHandlerChange();
            sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
        }
    };
    protected final SmallEnergyStorage energyStorage = new SmallEnergyStorage(AssemblerBlock.MAX_ENERGY, AssemblerBlock.MAX_ENERGY_TRANSFER) {
        @Override
        protected void onEnergyChanged() {
            markDirty();
        }
    };
    public SmallEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public JSGItemStackHandler getJSGItemHandler() {
        return itemStackHandler;
    }

    @Override
    protected void playLoopSound(boolean stop) {
        JSGSoundHelper.playPositionedSound(world, pos, SoundPositionedEnum.BEAMER_LOOP, !stop);
    }

    @Override
    protected void playSound(boolean start) {
        if (!start)
            JSGSoundHelper.playSoundEvent(world, pos, SoundEventEnum.BEAMER_STOP);
        else
            JSGSoundHelper.playSoundEvent(world, pos, SoundEventEnum.BEAMER_START);
    }

    public AbstractMachineRecipe getRecipeIfPossible() {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (int i = 1; i < 10; i++)
            stacks.add(itemStackHandler.getStackInSlot(i));

        Item scheme = itemStackHandler.getStackInSlot(0).getItem();
        ItemStack subStack = itemStackHandler.getStackInSlot(10);

        if(currentRecipe instanceof AssemblerRecipe){
            AssemblerRecipe recipe = (AssemblerRecipe) currentRecipe;
            if (!itemStackHandler.insertItem(11, recipe.getResult(), true).equals(ItemStack.EMPTY)) return null;
            if (recipe.isOk(energyStorage.getEnergyStored(), scheme, stacks, subStack)) return recipe;
            return null;
        }

        for (AssemblerRecipe recipe : AssemblerRecipes.RECIPES) {
            if (!itemStackHandler.insertItem(11, recipe.getResult(), true).equals(ItemStack.EMPTY)) continue;
            if (recipe.isOk(energyStorage.getEnergyStored(), scheme, stacks, subStack)) return recipe;
        }

        return null;
    }

    protected void workIsDone() {
        if (!isWorking) return;
        AssemblerRecipe currentRecipe = (AssemblerRecipe) this.currentRecipe;
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
        super.workIsDone();
    }

    @Override
    public State getState(StateTypeEnum stateType) {
        switch (stateType) {
            case GUI_UPDATE:
                return new AssemblerContainerGuiUpdate(energyStorage.getEnergyStored(), energyTransferedLastTick, machineStart, machineEnd);
            case RENDERER_UPDATE:
                ItemStack stack = currentRecipe != null ? ((AssemblerRecipe) currentRecipe).getResult() : itemStackHandler.getStackInSlot(11);
                return new AssemblerRendererState(workStateChanged, machineProgress, isWorking, stack);
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
                markDirty();
                break;
        }
    }

    public AbstractMachineRendererState getRendererState() {
        return rendererState;
    }
}
