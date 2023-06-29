package tauri.dev.jsg.tileentity.machine;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import tauri.dev.jsg.block.machine.PCBFabricatorBlock;
import tauri.dev.jsg.gui.container.machine.pcbfabricator.PCBFabricatorContainerGuiUpdate;
import tauri.dev.jsg.machine.AbstractMachineRecipe;
import tauri.dev.jsg.machine.pcbfabricator.PCBFabricatorRecipe;
import tauri.dev.jsg.machine.pcbfabricator.PCBFabricatorRecipes;
import tauri.dev.jsg.power.general.SmallEnergyStorage;
import tauri.dev.jsg.renderer.machine.AbstractMachineRendererState;
import tauri.dev.jsg.renderer.machine.PCBFabricatorRendererState;
import tauri.dev.jsg.sound.JSGSoundHelper;
import tauri.dev.jsg.sound.SoundEventEnum;
import tauri.dev.jsg.sound.SoundPositionedEnum;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.util.JSGItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class PCBFabricatorTile extends AbstractMachineTile {

    public PCBFabricatorRendererState rendererState = new PCBFabricatorRendererState();
    public static final int CONTAINER_SIZE = 10;
    protected final JSGItemStackHandler itemStackHandler = new JSGItemStackHandler(CONTAINER_SIZE) {

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return slot != 9; // output slot
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
            sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
        }
    };
    protected final SmallEnergyStorage energyStorage = new SmallEnergyStorage(PCBFabricatorBlock.MAX_ENERGY, PCBFabricatorBlock.MAX_ENERGY_TRANSFER) {
        @Override
        protected void onEnergyChanged() {
            markDirty();
        }
    };
    protected final FluidTank fluidHandler = new FluidTank(PCBFabricatorBlock.FLUID_CAPACITY) {
        @Override
        public boolean canFillFluidType(FluidStack fluid) {
            return fluid != null;
        }

        @Override
        protected void onContentsChanged() {
            markDirty();
            sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
        }
    };

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

    @Override
    public AbstractMachineRecipe getRecipeIfPossible() {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < 9; i++)
            stacks.add(itemStackHandler.getStackInSlot(i));

        for (PCBFabricatorRecipe recipe : PCBFabricatorRecipes.RECIPES) {
            if (itemStackHandler.insertItem(9, recipe.getResult(), true).equals(recipe.getResult())) continue;
            if (fluidHandler.getFluid() == null) continue;
            if (recipe.isOk(energyStorage.getEnergyStored(), new FluidStack(fluidHandler.getFluid(), fluidHandler.getFluidAmount()), stacks))
                return recipe;
        }
        return null;
    }

    protected void workIsDone() {
        if (!isWorking || currentRecipe == null) return;
        PCBFabricatorRecipe currentRecipe = (PCBFabricatorRecipe) this.currentRecipe;
        itemStackHandler.insertItem(9, currentRecipe.getResult(), false);
        fluidHandler.drainInternal(currentRecipe.getSubFluidStack().amount, true);
        for (int i = 0; i < 9; i++) {
            int amount = 0;
            if (currentRecipe.getPattern().size() > i && currentRecipe.getPattern().get(i) != null)
                amount = currentRecipe.getPattern().get(i).getCount();
            itemStackHandler.extractItem(i, amount, false);
        }
        super.workIsDone();
    }

    @Override
    public SmallEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidHandler);

        return super.getCapability(capability, facing);
    }

    @Override
    public State getState(StateTypeEnum stateType) {
        switch (stateType) {
            case GUI_UPDATE:
                return new PCBFabricatorContainerGuiUpdate(energyStorage.getEnergyStored(), (fluidHandler.getFluid() != null ? new FluidStack(fluidHandler.getFluid(), fluidHandler.getFluidAmount()) : null), energyTransferedLastTick, machineStart, machineEnd);
            case RENDERER_UPDATE:
                ItemStack stack = currentRecipe != null ? ((PCBFabricatorRecipe) currentRecipe).getResult() : itemStackHandler.getStackInSlot(9);
                float[] colors = currentRecipe != null ? ((PCBFabricatorRecipe) currentRecipe).getBeamColors() : new float[]{1f, 1f, 1f};
                return new PCBFabricatorRendererState(workStateChanged, machineProgress, isWorking, stack, colors);
        }
        return null;
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {
            case GUI_UPDATE:
                return new PCBFabricatorContainerGuiUpdate();
            case RENDERER_UPDATE:
                return new PCBFabricatorRendererState();
        }
        return null;
    }

    @Override
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {
            case GUI_UPDATE:
                PCBFabricatorContainerGuiUpdate guiUpdate = (PCBFabricatorContainerGuiUpdate) state;
                energyStorage.setEnergyStored(guiUpdate.energyStored);
                energyTransferedLastTick = guiUpdate.energyTransferedLastTick;
                machineStart = guiUpdate.machineStart;
                machineEnd = guiUpdate.machineEnd;
                fluidHandler.setFluid(guiUpdate.fluidStack);
                markDirty();
                break;
            case RENDERER_UPDATE:
                rendererState = (PCBFabricatorRendererState) state;
                this.machineProgress = rendererState.machineProgress;
                this.isWorking = rendererState.isWorking;
                markDirty();
                break;
        }
    }

    public AbstractMachineRendererState getRendererState() {
        return rendererState;
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound fluidHandlerCompound = new NBTTagCompound();
        fluidHandler.writeToNBT(fluidHandlerCompound);
        compound.setTag("fluidHandler", fluidHandlerCompound);

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        fluidHandler.readFromNBT(compound.getCompoundTag("fluidHandler"));

        super.readFromNBT(compound);
    }
}
