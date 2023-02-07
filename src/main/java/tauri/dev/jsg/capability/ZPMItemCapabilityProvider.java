package tauri.dev.jsg.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import tauri.dev.jsg.power.zpm.ZPMItemEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ZPMItemCapabilityProvider implements ICapabilityProvider {

    protected ZPMItemEnergyStorage energyStorage;

    public ZPMItemCapabilityProvider(final ItemStack stack, @Nullable NBTTagCompound nbt, long maxEnergy, boolean creative) {
        energyStorage = new ZPMItemEnergyStorage(stack, maxEnergy){
            @Override
            public void setEnergyStored(long energy){
                super.setEnergyStored(creative? maxEnergyStored : energy);
            }
            @Override
            public long getEnergyStored(){
                if(creative)
                    return maxEnergyStored;
                return super.getEnergyStored();
            }
            @Override
            public int extractEnergy(int max, boolean simulate){
                if(creative)
                    return max;
                return super.extractEnergy(max, simulate);
            }
        };
        if (nbt != null && nbt.hasKey("Parent", Constants.NBT.TAG_COMPOUND))
            backwardsCompat(nbt.getCompoundTag("Parent"));
    }

    private void backwardsCompat(NBTTagCompound nbt) {
        if (nbt.hasKey("energy", Constants.NBT.TAG_INT))
            energyStorage.setEnergyStored(nbt.getInteger("energy"));
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityEnergyZPM.ENERGY;
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if(energyStorage == null) throw new RuntimeException("energyStorage was null!!!");
        return (capability == CapabilityEnergyZPM.ENERGY ? CapabilityEnergyZPM.ENERGY.cast(energyStorage) : null);
    }
}
