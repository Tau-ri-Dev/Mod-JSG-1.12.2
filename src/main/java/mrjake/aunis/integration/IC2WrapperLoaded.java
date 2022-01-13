package mrjake.aunis.integration;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.machine.Machine;
import li.cil.oc.api.network.*;
import mrjake.aunis.capability.endpoint.ItemEndpointCapability;
import mrjake.aunis.capability.endpoint.ItemEndpointInterface;
import mrjake.aunis.item.oc.ItemWirelessEndpoint;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class IC2WrapperLoaded implements IC2WrapperInterface {
	
	public void sendSignalToReachable(Node node, Context invoker, String name, Object... params) {
		if (node == null) return;
		for (Node targetNode : node.reachableNodes()) {
			
			if (targetNode.host() instanceof Machine) {
				Machine machine = (Machine) targetNode.host();
				
				// If the receiving machine was a sender 
				boolean caller = machine == invoker;
				
				Object[] array = new Object[params.length + 2];
				array[0] = node.address();
				array[1] = caller;
				
				for (int i=0; i<params.length; i++)
					array[i+2] = params[i];
				
				machine.signal(name, array);
			}
		}
	}
	
	@Override
	public Node createNode(TileEntity environment, String componentName) {
		return Network.newNode((Environment) environment, Visibility.Network).withComponent(componentName, Visibility.Network).create();
	}
	
	@Override
	public void joinOrCreateNetwork(TileEntity tileEntity) {
		Network.joinOrCreateNetwork(tileEntity);
	}
	
	@Override
	public boolean isModLoaded() {
		return true;
	}
	
	@Override
	public void sendWirelessPacketPlayer(EntityPlayer player, ItemStack stack, String address, short port, Object[] data) {
		ItemEndpointInterface endpointStack = stack.getCapability(ItemEndpointCapability.ENDPOINT_CAPABILITY, null);
		
		if (endpointStack.hasEndpoint()) {
			endpointStack.resetEndpointCounter(player.getEntityWorld().getTotalWorldTime());
		}
		
		else {
			ItemWirelessEndpoint endpoint = new ItemWirelessEndpoint(player);
			Network.joinWirelessNetwork(endpoint);
			
			endpointStack.setEndpoint(endpoint, player.getEntityWorld().getTotalWorldTime());
		}
		
		// Broadcast
		if (address.isEmpty())
			address = null;
		
		Packet packet = Network.newPacket("unv-dialer-"+player.getName(), address, port, data);
//		Aunis.info("sending packet with endpoint: " + endpointStack.getEndpoint());
		
		Network.sendWirelessPacket((WirelessEndpoint) endpointStack.getEndpoint(), 20, packet);
	}
	
	@Override
	public void joinWirelessNetwork(Object endpoint) {
		Network.joinWirelessNetwork((WirelessEndpoint) endpoint);
	}
	
	@Override
	public void leaveWirelessNetwork(Object endpoint) {
		Network.leaveWirelessNetwork((WirelessEndpoint) endpoint);
	}
	
	@Override
	public void updateWirelessNetwork(Object endpoint) {
		Network.updateWirelessNetwork((WirelessEndpoint) endpoint);
	}
}
