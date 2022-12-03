package tauri.dev.jsg.gui.container.countdown;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.gui.container.OpenTabHolderInterface;
import tauri.dev.jsg.gui.util.ContainerHelper;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.tileentity.props.DestinyCountDownTile;

import javax.annotation.Nonnull;

public class CountDownContainer extends Container implements OpenTabHolderInterface {

    public DestinyCountDownTile tile;
    public boolean isOperator;

    private final BlockPos pos;
    private int openTabId = -1;

    public CountDownContainer(IInventory playerInventory, World world, int x, int y, int z, boolean isOperator) {
        this.isOperator = isOperator;
        pos = new BlockPos(x, y, z);
        tile = (DestinyCountDownTile) world.getTileEntity(pos);
        for (Slot slot : ContainerHelper.generatePlayerSlots(playerInventory, 86))
            addSlotToContainer(slot);
    }

    @Override
    public int getOpenTabId() {
        return openTabId;
    }

    @Override
    public void setOpenTabId(int tabId) {
        openTabId = tabId;
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
        return true;
    }

    @Override
    public void addListener(@Nonnull IContainerListener listener) {
        super.addListener(listener);

        if (listener instanceof EntityPlayerMP) {
            JSGPacketHandler.INSTANCE.sendTo(new StateUpdatePacketToClient(pos, StateTypeEnum.GUI_STATE, tile.getState(StateTypeEnum.GUI_STATE)), (EntityPlayerMP) listener);
        }
    }
}
