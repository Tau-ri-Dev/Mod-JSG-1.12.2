package tauri.dev.jsg.power.zpm;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ZPMItemEnergyStorage implements IEnergyStorageZPM {
    private final ItemStack stack;
    protected final long maxEnergyStored;

    public ZPMItemEnergyStorage(ItemStack stack, long maxEnergyStored) {
        this.stack = stack;
        this.maxEnergyStored = maxEnergyStored;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) return 0;
        if (maxExtract > getEnergyStored()) maxExtract = (int) getEnergyStored();
        if (!simulate) {
            setEnergyStored(getEnergyStored() - maxExtract);
        }
        return maxExtract;
    }

    public void setEnergyStored(long energy) {
        getOrCreateCompound(stack).setLong("longEnergy", energy);
    }

    @Override
    public long getEnergyStored() {
        NBTTagCompound tag = getOrCreateCompound(stack);
        if (tag.hasKey("energy")) {
            return tag.getInteger("energy");
        }
        return tag.getLong("longEnergy");
    }

    @Override
    public long getMaxEnergyStored() {
        return maxEnergyStored;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    private NBTTagCompound getOrCreateCompound(ItemStack stack) {
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
        return stack.getTagCompound();
    }
}
