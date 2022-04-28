package mrjake.aunis.capability;

import mrjake.aunis.stargate.power.StargateItemEnergyStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;

public final class ItemCapabilityProvider implements ICapabilityProvider {

	private final StargateItemEnergyStorage energyStorage;
	
	public ItemCapabilityProvider(final ItemStack stack, @Nullable NBTTagCompound nbt, int maxEnergy) {
		energyStorage = new StargateItemEnergyStorage(stack, maxEnergy);
		if(nbt != null && nbt.hasKey("Parent", Constants.NBT.TAG_COMPOUND))
			backwardsCompat(nbt.getCompoundTag("Parent"));
	}

	private final void backwardsCompat(NBTTagCompound nbt){
		if(nbt.hasKey("energy", Constants.NBT.TAG_INT))
			energyStorage.setEnergyStored(nbt.getInteger("energy"));
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityEnergy.ENERGY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return (capability == CapabilityEnergy.ENERGY ? CapabilityEnergy.ENERGY.cast(energyStorage) : null);
	}

}
