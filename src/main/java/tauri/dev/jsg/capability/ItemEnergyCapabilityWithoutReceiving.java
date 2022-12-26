package tauri.dev.jsg.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import tauri.dev.jsg.stargate.power.StargateItemEnergyStorage;

import javax.annotation.Nullable;

public class ItemEnergyCapabilityWithoutReceiving extends ItemCapabilityProvider implements ICapabilityProvider {

    public ItemEnergyCapabilityWithoutReceiving(final ItemStack stack, @Nullable NBTTagCompound nbt, int maxEnergy) {
        super(stack, nbt, maxEnergy);
        energyStorage = new StargateItemEnergyStorage(stack, maxEnergy) {
            @Override
            public int receiveEnergy(int max, boolean simulate) {
                return 0;
            }
        };
    }
}
