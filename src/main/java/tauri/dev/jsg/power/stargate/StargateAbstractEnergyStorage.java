package tauri.dev.jsg.power.stargate;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;
import tauri.dev.jsg.config.JSGConfig;

public class StargateAbstractEnergyStorage extends EnergyStorage implements INBTSerializable<NBTTagCompound> {

	public StargateAbstractEnergyStorage() {
		super(JSGConfig.powerConfig.stargateEnergyStorage/4, tauri.dev.jsg.config.JSGConfig.powerConfig.stargateMaxEnergyTransfer, 0);
	}
	
	public StargateAbstractEnergyStorage(int capacity, int maxTransfer) {
		super(capacity, maxTransfer);
	}

	public StargateAbstractEnergyStorage(int capacity) {
		super(capacity, tauri.dev.jsg.config.JSGConfig.powerConfig.stargateMaxEnergyTransfer);
	}
	
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		
		tagCompound.setInteger("energy", this.energy);
		
		return tagCompound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt != null) {
			if (nbt.hasKey("energy")) {
				this.energy = nbt.getInteger("energy");
			}
		}
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int energyReceived = super.receiveEnergy(maxReceive, simulate);
		
		if (energyReceived > 0)
			onEnergyChanged();
		
		return energyReceived;
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		
		if (!simulate) {
			energy -= maxExtract;
			if(energy < 0) energy = 0;
			onEnergyChanged();
		}
		else if (maxExtract > energy) {
			maxExtract = energy;
		}
		return maxExtract;
	}
	
    public int receiveEnergyInternal(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate && energyReceived > 0) {
			energy += energyReceived;
			onEnergyChanged();
		}
        return energyReceived;
    }
	
	public void setEnergyStored(int energyStored) {
		this.energy = Math.min(energyStored, capacity);
		
		onEnergyChanged();
	}
	
	protected void onEnergyChanged() {}
}
