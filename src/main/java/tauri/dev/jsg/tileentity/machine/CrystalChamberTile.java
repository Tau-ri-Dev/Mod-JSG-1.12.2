package tauri.dev.jsg.tileentity.machine;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.machine.CrystalChamberBlock;
import tauri.dev.jsg.gui.container.machine.crystalchamber.CrystalChamberContainerGuiUpdate;
import tauri.dev.jsg.machine.CrystalChamberRecipe;
import tauri.dev.jsg.machine.CrystalChamberRecipes;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.renderer.machine.CrystalChamberRendererState;
import tauri.dev.jsg.sound.JSGSoundHelper;
import tauri.dev.jsg.sound.JSGSoundHelperClient;
import tauri.dev.jsg.sound.SoundEventEnum;
import tauri.dev.jsg.sound.SoundPositionedEnum;
import tauri.dev.jsg.stargate.power.StargateAbstractEnergyStorage;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateProviderInterface;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.tileentity.util.IUpgradable;
import tauri.dev.jsg.util.JSGItemStackHandler;

import javax.annotation.Nonnull;

import static tauri.dev.jsg.item.JSGItems.CRYSTAL_SEED;

public class CrystalChamberTile extends TileEntity implements IUpgradable, StateProviderInterface, ITickable {

    public CrystalChamberRendererState rendererState = new CrystalChamberRendererState();

    public static final int CONTAINER_SIZE = 2;

    protected NetworkRegistry.TargetPoint targetPoint;
    protected final ItemStackHandler itemStackHandler = new JSGItemStackHandler(CONTAINER_SIZE) {

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot == 1) return false; // output slot
            if (slot == 0) {
                return stack.getItem() == CRYSTAL_SEED;
            }
            return true;
        }

        @Override
        protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
            return 64;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
            markDirty();
        }
    };
    protected final StargateAbstractEnergyStorage energyStorage = new StargateAbstractEnergyStorage(CrystalChamberBlock.MAX_ENERGY, CrystalChamberBlock.MAX_ENERGY_TRANSFER) {
        @Override
        protected void onEnergyChanged() {
            markDirty();
        }
    };
    protected final FluidTank fluidHandler = new FluidTank(CrystalChamberBlock.FLUID_CAPACITY) {
        @Override
        public boolean canFillFluidType(FluidStack fluid) {
            return fluid != null;
        }

        @Override
        protected void onContentsChanged() {
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

    protected CrystalChamberRecipe currentRecipe = null;

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

    public void onBreak(){
        isWorking = false;
        currentRecipe = null;
        markDirty();
        sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
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

    public CrystalChamberRecipe getRecipeIfPossible() {
        for (CrystalChamberRecipe recipe : CrystalChamberRecipes.RECIPES) {
            if (itemStackHandler.insertItem(1, recipe.getResult(), true).equals(recipe.getResult())) continue;
            if (fluidHandler.getFluid() == null) continue;
            if (recipe.isOk(energyStorage.getEnergyStored(), new FluidStack(fluidHandler.getFluid(), fluidHandler.getFluidAmount()), itemStackHandler.getStackInSlot(0)))
                return recipe;
        }
        return null;
    }

    protected void workIsDone() {
        if (!isWorking) return;
        itemStackHandler.insertItem(1, currentRecipe.getResult(), false);
        itemStackHandler.extractItem(0, currentRecipe.getNeededSeeds(), false);
        fluidHandler.drainInternal(currentRecipe.getSubFluidStack().amount, true);
        currentRecipe = getRecipeIfPossible();
        if (currentRecipe != null) {
            machineStart = this.world.getTotalWorldTime();
            machineEnd = this.world.getTotalWorldTime() + currentRecipe.getWorkingTime();
            machineProgress = 0;
            isWorking = true;
        } else {
            machineStart = -1;
            machineEnd = -1;
            machineProgress = 0;
            isWorking = false;
            JSGSoundHelper.playSoundEvent(world, pos, SoundEventEnum.BEAMER_STOP);
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
                    JSGSoundHelper.playSoundEvent(world, pos, SoundEventEnum.BEAMER_STOP);
                } else {
                    if (machineStart == machineEnd) machineProgress = 0;
                    else
                        machineProgress = (int) Math.round((((double) (this.world.getTotalWorldTime() - machineStart)) / ((double) (machineEnd - machineStart))) * 100); // returns % of done work
                    energyStorage.extractEnergy(currentRecipe.getEnergyPerTick(), false);

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
                JSGSoundHelper.playSoundEvent(world, pos, SoundEventEnum.BEAMER_START);
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
        return (capability == CapabilityEnergy.ENERGY)
                || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY)
            return CapabilityEnergy.ENERGY.cast(getEnergyStorage());

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemStackHandler);

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidHandler);

        return super.getCapability(capability, facing);
    }

    @Override
    public State getState(StateTypeEnum stateType) {
        switch (stateType) {
            case GUI_UPDATE:
                return new CrystalChamberContainerGuiUpdate(energyStorage.getEnergyStored(), (fluidHandler.getFluid() != null ? new FluidStack(fluidHandler.getFluid(), fluidHandler.getFluidAmount()) : null), energyTransferedLastTick, machineStart, machineEnd);
            case RENDERER_UPDATE:
                ItemStack stack = currentRecipe != null ? currentRecipe.getResult() : itemStackHandler.getStackInSlot(1);
                return new CrystalChamberRendererState(machineProgress, isWorking, stack);
        }
        return null;
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {
            case GUI_UPDATE:
                return new CrystalChamberContainerGuiUpdate();
            case RENDERER_UPDATE:
                return new CrystalChamberRendererState();
        }
        return null;
    }

    @Override
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {
            case GUI_UPDATE:
                CrystalChamberContainerGuiUpdate guiUpdate = (CrystalChamberContainerGuiUpdate) state;
                energyStorage.setEnergyStored(guiUpdate.energyStored);
                energyTransferedLastTick = guiUpdate.energyTransferedLastTick;
                machineStart = guiUpdate.machineStart;
                machineEnd = guiUpdate.machineEnd;
                fluidHandler.setFluid(guiUpdate.fluidStack);
                markDirty();
                break;
            case RENDERER_UPDATE:
                rendererState = (CrystalChamberRendererState) state;
                this.machineProgress = rendererState.machineProgress;
                this.isWorking = rendererState.isWorking;
                JSGSoundHelperClient.playPositionedSoundClientSide(pos, SoundPositionedEnum.BEAMER_LOOP, isWorking);
                break;
        }
    }

    public CrystalChamberRendererState getRendererState() {
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

        NBTTagCompound fluidHandlerCompound = new NBTTagCompound();
        fluidHandler.writeToNBT(fluidHandlerCompound);
        compound.setTag("fluidHandler", fluidHandlerCompound);

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

        fluidHandler.readFromNBT(compound.getCompoundTag("fluidHandler"));

        super.readFromNBT(compound);
    }
}
