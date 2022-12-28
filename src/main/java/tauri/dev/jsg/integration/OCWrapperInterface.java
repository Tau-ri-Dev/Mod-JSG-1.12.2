package tauri.dev.jsg.integration;

import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Network;
import li.cil.oc.api.network.Node;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public interface OCWrapperInterface {
	
	/**
	 * Sends signal to all computers in the network
	 * 
	 * @param node - sender node
	 * @param context 
	 * @param name - name of the signal
	 * @param params - params of the signal
	 */
	void sendSignalToReachable(Node node, Context invoker, String name, Object... params);
	
	/**
	 * Creates a new Node when OpenComputers is loaded.
	 * Otherwise returns null.
	 * 
	 * @param tileEntity {@link TileEntity} instance of the parent block.
	 * @param componentName {@link String} representing the component.
	 * @return {@link Node} or null.
	 */
	@Nullable
	Node createNode(TileEntity tileEntity, String componentName);

	/**
	 * Joins the {@link TileEntity} to the {@link Network}.
	 * 
	 * @param tileEntity {@link TileEntity} to be linked.
	 */
	void joinOrCreateNetwork(TileEntity tileEntity);

	/**
	 * @return {@code True} if the OpenComputers mod is loaded, {@code false} otherwise.
	 */
	boolean isModLoaded();
	
	void sendWirelessPacketPlayer(String packetPrefix, EntityPlayer player, ItemStack stack, String address, short port, Object[] data);

	void joinWirelessNetwork(Object endpoint);
	void leaveWirelessNetwork(Object endpoint);
	void updateWirelessNetwork(Object endpoint);

}
