package tauri.dev.jsg.item.linkable;

import tauri.dev.jsg.capability.endpoint.ItemEndpointCapability;
import tauri.dev.jsg.capability.endpoint.ItemEndpointImpl;
import tauri.dev.jsg.capability.endpoint.ItemEndpointInterface;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class LinkAbleCapabilityProvider implements ICapabilityProvider {
	
	private ItemEndpointInterface itemEndpoint = new ItemEndpointImpl();
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == ItemEndpointCapability.ENDPOINT_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == ItemEndpointCapability.ENDPOINT_CAPABILITY)
			return ItemEndpointCapability.ENDPOINT_CAPABILITY.cast(itemEndpoint);
		
		return null;
	}

}
