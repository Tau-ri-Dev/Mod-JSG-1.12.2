package tauri.dev.jsg.power.stargate;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.IEnergyStorage;
import tauri.dev.jsg.config.JSGConfig;

public class StargateItemEnergyStorage implements IEnergyStorage {
    protected final ItemStack stack;
    protected final int maxEnergyStored;

    public StargateItemEnergyStorage(ItemStack stack, int maxEnergyStored) {
        this.stack = stack;
        this.maxEnergyStored = maxEnergyStored;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyStored = getEnergyStored();
        int energyReceived = Math.min(getMaxEnergyStored() - energyStored, Math.min(JSGConfig.Stargate.power.stargateMaxEnergyTransfer, maxReceive));
        if (!simulate)
            setEnergyStored(energyStored + energyReceived);
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (maxExtract > getEnergyStored()) maxExtract = getEnergyStored();
        if (!simulate) {
            setEnergyStored(getEnergyStored() - maxExtract);
        }
        return maxExtract;
    }

    public void setEnergyStored(int energy){
        getOrCreateCompound(stack).setInteger("energy", energy);
    }

    @Override
    public int getEnergyStored() {
        return getOrCreateCompound(stack).getInteger("energy");
    }

    @Override
    public int getMaxEnergyStored() {
        return maxEnergyStored;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    protected NBTTagCompound getOrCreateCompound(ItemStack stack) {
        if(!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
        return stack.getTagCompound();
    }
}
