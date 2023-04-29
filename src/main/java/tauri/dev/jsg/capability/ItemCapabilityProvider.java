package tauri.dev.jsg.capability;

import tauri.dev.jsg.power.stargate.ItemEnergyStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemCapabilityProvider implements ICapabilityProvider {

	protected ItemEnergyStorage energyStorage;

	public ItemCapabilityProvider(final ItemStack stack, @Nullable NBTTagCompound nbt, int maxEnergy) {
		this(stack, nbt, maxEnergy, false);
	}
	public ItemCapabilityProvider(final ItemStack stack, @Nullable NBTTagCompound nbt, int maxEnergy, boolean creative) {
		energyStorage = new ItemEnergyStorage(stack, maxEnergy){
			@Override
			public void setEnergyStored(int energy){
				super.setEnergyStored(creative? maxEnergyStored : energy);
			}
			@Override
			public int getEnergyStored(){
				if(creative)
					return maxEnergyStored;
				return super.getEnergyStored();
			}
			@Override
			public int extractEnergy(int max, boolean simulate){
				if(creative) {
					setEnergyStored(maxEnergyStored);
					return max;
				}
				return super.extractEnergy(max, simulate);
			}
			@Override
			public int receiveEnergy(int max, boolean simulate){
				if(creative) {
					setEnergyStored(maxEnergyStored);
					return max;
				}
				return super.receiveEnergy(max, simulate);
			}

			@Override
			public boolean canReceive(){
				if(creative) {
					// Creative item should not receive any energy...
					return false;
				}
				return super.canReceive();
			}
		};
		if(nbt != null && nbt.hasKey("Parent", Constants.NBT.TAG_COMPOUND))
			backwardsCompat(nbt.getCompoundTag("Parent"));
	}

	private void backwardsCompat(NBTTagCompound nbt){
		if(nbt.hasKey("energy", Constants.NBT.TAG_INT))
			energyStorage.setEnergyStored(nbt.getInteger("energy"));
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityEnergy.ENERGY;
	}

	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
		return (capability == CapabilityEnergy.ENERGY ? CapabilityEnergy.ENERGY.cast(energyStorage) : null);
	}

}
