package tauri.dev.jsg.capability;

import tauri.dev.jsg.capability.endpoint.ItemEndpointCapability;
import tauri.dev.jsg.capability.endpoint.ItemEndpointImpl;
import tauri.dev.jsg.capability.endpoint.ItemEndpointInterface;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class WeaponCapabilityProvider extends ItemCapabilityProvider {
    private final ItemEndpointInterface itemEndpoint = new ItemEndpointImpl();

    public WeaponCapabilityProvider(ItemStack stack, @Nullable NBTTagCompound nbt, int maxEnergy) {
        super(stack, nbt, maxEnergy);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == ItemEndpointCapability.ENDPOINT_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == ItemEndpointCapability.ENDPOINT_CAPABILITY)
            return ItemEndpointCapability.ENDPOINT_CAPABILITY.cast(itemEndpoint);

        return super.getCapability(capability, facing);
    }
}
