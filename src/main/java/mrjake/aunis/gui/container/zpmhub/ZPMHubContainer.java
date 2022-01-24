package mrjake.aunis.gui.container.zpmhub;

import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.gui.container.DHDContainerGuiUpdate;
import mrjake.aunis.gui.container.zpm.ZPMContainerGuiUpdate;
import mrjake.aunis.gui.util.ContainerHelper;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.state.StateTypeEnum;
import mrjake.aunis.tileentity.energy.ZPMHubTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.ArrayList;

public class ZPMHubContainer extends Container {
	protected ZPMHubTile zpmHubTile;
	protected ArrayList<SlotItemHandler> zpmSlots = new ArrayList<SlotItemHandler>(3);
	protected int zpmsLastCount = 0;

	protected long energyTransferedLastTick;

	private BlockPos pos;

	public ZPMHubContainer(IInventory playerInventory, World world, int x, int y, int z) {
		pos = new BlockPos(x, y, z);
		zpmHubTile = (ZPMHubTile) world.getTileEntity(pos);
		IItemHandler itemHandler = zpmHubTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		
		// ZPM Slots 0-2 (3 slots)
		for(int i = 0; i < 3; i++) {
			zpmSlots.add(new SlotItemHandler(itemHandler, i, 49 + (i*26), 35));
			addSlotToContainer(zpmSlots.get(i));
		}
		
		for (Slot slot : ContainerHelper.generatePlayerSlots(playerInventory, 86))
			addSlotToContainer(slot);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack stack = getSlot(index).getStack();
		
		// Transfering from ZPMHub to player's inventory
        if (index < 3) {
        	if (!mergeItemStack(stack, 3, inventorySlots.size(), false)) {
        		return ItemStack.EMPTY;
        	}
        	
        	putStackInSlot(index, ItemStack.EMPTY);
        }

        else {
        	if (stack.getItem() == Item.getItemFromBlock(AunisBlocks.ZPM)) {
				for(int i = 0; i < 3; i++) {
					if (!zpmSlots.get(i).getHasStack()) {
						ItemStack stack1 = stack.copy();
						stack1.setCount(1);
						zpmSlots.get(i).putStack(stack1);

						stack.shrink(1);

						return ItemStack.EMPTY;
					}
				}
        	}
        	
        	return ItemStack.EMPTY;
        }
        
        return stack;
    }
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int zpmsCount = 0;
		for(int i = 0; i < 3; i++){
			if(!zpmSlots.get(i).getStack().isEmpty())
				zpmsCount++;
		}
		long energyTransferedLastTick = zpmHubTile.getEnergyTransferedLastTick();
		for (IContainerListener listener : listeners) {
			if (listener instanceof EntityPlayerMP) {
				AunisPacketHandler.INSTANCE.sendTo(new StateUpdatePacketToClient(pos, StateTypeEnum.GUI_UPDATE, new ZPMHubContainerGuiUpdate(zpmsCount, energyTransferedLastTick)), ((EntityPlayerMP) listener));
			}
		}
		this.energyTransferedLastTick = energyTransferedLastTick;
		this.zpmsLastCount = zpmsCount;
	}
}
