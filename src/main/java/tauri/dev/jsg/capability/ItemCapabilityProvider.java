package tauri.dev.jsg.capability;

import tauri.dev.jsg.power.stargate.StargateItemEnergyStorage;
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

	protected StargateItemEnergyStorage energyStorage;
	
	public ItemCapabilityProvider(final ItemStack stack, @Nullable NBTTagCompound nbt, int maxEnergy) {
		energyStorage = new StargateItemEnergyStorage(stack, maxEnergy);
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
