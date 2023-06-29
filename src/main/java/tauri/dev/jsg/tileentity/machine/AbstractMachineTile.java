package tauri.dev.jsg.tileentity.machine;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.machine.AbstractMachineRecipe;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.power.general.SmallEnergyStorage;
import tauri.dev.jsg.renderer.machine.AbstractMachineRendererState;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateProviderInterface;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.tileentity.util.IUpgradable;
import tauri.dev.jsg.util.JSGItemStackHandler;

import javax.annotation.Nonnull;

public abstract class AbstractMachineTile extends TileEntity implements IUpgradable, StateProviderInterface, ITickable {
    protected NetworkRegistry.TargetPoint targetPoint;
    protected int energyStoredLastTick = 0;
    protected int energyTransferedLastTick = 0;
    protected int machineProgress = 0;
    protected int machineProgressLast = -1;
    protected long machineStart = -1;
    protected long machineEnd = -1;
    protected boolean isWorking = false;
    protected boolean isWorkingLast = false;
    protected long workStateChanged;

    protected AbstractMachineRecipe currentRecipe = null;
    protected AbstractMachineRecipe currentRecipeLast = null;

    public int getEnergyTransferedLastTick() {
        return energyTransferedLastTick;
    }

    public abstract JSGItemStackHandler getJSGItemHandler();

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            targetPoint = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
            sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
        }
    }

    protected abstract void playLoopSound(boolean stop);

    protected abstract void playSound(boolean start);

    public void onBreak() {
        isWorking = false;
        currentRecipe = null;
        markDirty();
        playLoopSound(true);
    }

    public long getMachineStart() {
        return machineStart;
    }

    public long getMachineEnd() {
        return machineEnd;
    }

    public abstract AbstractMachineRecipe getRecipeIfPossible();

    protected void workIsDone() {
        if (!isWorking) return;
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
            playSound(false);
        }
        markDirty();
    }

    public abstract SmallEnergyStorage getEnergyStorage();


    @Override
    public void update() {
        if (!world.isRemote) {
            // -------------------------------
            // ENERGY UPDATING

            energyTransferedLastTick = getEnergyStorage().getEnergyStored() - energyStoredLastTick;
            energyStoredLastTick = getEnergyStorage().getEnergyStored();


            currentRecipe = getRecipeIfPossible();
            if (isWorking) {
                if (currentRecipe == null) {
                    isWorking = false;
                    workStateChanged = world.getTotalWorldTime();
                    machineProgress = 0;
                    machineStart = -1;
                    machineEnd = -1;
                    markDirty();
                    sendState(StateTypeEnum.GUI_UPDATE, getState(StateTypeEnum.GUI_UPDATE));
                    playSound(false);
                } else {
                    if (machineStart == machineEnd) machineProgress = 0;
                    else
                        machineProgress = (int) Math.round((((double) (this.world.getTotalWorldTime() - machineStart)) / ((double) (machineEnd - machineStart))) * 100); // returns % of done work
                    getEnergyStorage().extractEnergy(currentRecipe.getEnergyPerTick(), false);

                    if (machineProgress >= 100) {
                        workIsDone();
                    }
                }
            } else if (currentRecipe != null) {
                isWorking = true;
                workStateChanged = world.getTotalWorldTime();
                machineStart = this.world.getTotalWorldTime();
                machineEnd = currentRecipe.getWorkingTime() + this.world.getTotalWorldTime();
                markDirty();
                sendState(StateTypeEnum.GUI_UPDATE, getState(StateTypeEnum.GUI_UPDATE));
                playSound(true);
            }

            if (isWorking != isWorkingLast || machineProgress != machineProgressLast || currentRecipe != currentRecipeLast) {
                isWorkingLast = isWorking;
                machineProgressLast = machineProgress;
                playLoopSound(!isWorking);
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
            JSG.debug("targetPoint was null trying to send " + type + " from " + this.getClass().getCanonicalName());
        }
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityEnergy.ENERGY)
                || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY)
            return CapabilityEnergy.ENERGY.cast(getEnergyStorage());

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getJSGItemHandler());

        return super.getCapability(capability, facing);
    }

    public abstract AbstractMachineRendererState getRendererState();

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("energyStorage", getEnergyStorage().serializeNBT());
        compound.setTag("itemStackHandler", getJSGItemHandler().serializeNBT());

        compound.setBoolean("isWorking", isWorking);
        compound.setLong("machineStart", machineStart);
        compound.setLong("machineEnd", machineEnd);
        compound.setInteger("progress", machineProgress);
        compound.setLong("workStart", workStateChanged);

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        getEnergyStorage().deserializeNBT(compound.getCompoundTag("energyStorage"));
        getJSGItemHandler().deserializeNBT(compound.getCompoundTag("itemStackHandler"));

        isWorking = compound.getBoolean("isWorking");
        machineStart = compound.getLong("machineStart");
        machineEnd = compound.getLong("machineEnd");
        machineProgress = compound.getInteger("machineProgress");
        workStateChanged = compound.getLong("workStart");

        super.readFromNBT(compound);
    }
}
