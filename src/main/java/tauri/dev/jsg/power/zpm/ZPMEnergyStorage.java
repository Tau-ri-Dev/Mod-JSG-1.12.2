package tauri.dev.jsg.power.zpm;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import tauri.dev.jsg.config.JSGConfig;

public class ZPMEnergyStorage implements IEnergyStorageZPM, INBTSerializable<NBTTagCompound> {

    protected long energy;
    protected long capacity;
    public int maxReceive;
    public int maxExtract;

	public ZPMEnergyStorage(long capacity, int maxTransfer)
	{
		this(capacity, maxTransfer, maxTransfer, 0);
	}
	public ZPMEnergyStorage(long capacity, int maxReceive, int maxExtract, int energy)
	{
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
		this.energy = Math.max(0 , Math.min(capacity, energy));
	}

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tagCompound = new NBTTagCompound();

        tagCompound.setLong("energy", this.energy);

        return tagCompound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if (nbt != null) {
            if (nbt.hasKey("energy")) {
                this.energy = nbt.getInteger("energy");
            }
            if (nbt.hasKey("longEnergy")) {
                this.energy = nbt.getLong("longEnergy");
            }
        }
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
		if (!canReceive())
			return 0;

		int energyReceived = (int) Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
		if (!simulate)
			energy += energyReceived;

        if (energyReceived > 0)
            onEnergyChanged();

        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
		if (!canExtract())
			return 0;

        if (!simulate) {
            energy -= maxExtract;
            if (energy < 0) energy = 0;
            onEnergyChanged();
        } else if (maxExtract > energy) {
            maxExtract = (int) energy;
        }
        return maxExtract;
    }

	@Override
	public long getEnergyStored() {
		return energy;
	}

	@Override
	public long getMaxEnergyStored() {
		return capacity;
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return false;
	}

	public void setEnergyStored(long energyStored) {
        this.energy = Math.min(energyStored, capacity);
        onEnergyChanged();
    }

    protected void onEnergyChanged() {
    }
}
