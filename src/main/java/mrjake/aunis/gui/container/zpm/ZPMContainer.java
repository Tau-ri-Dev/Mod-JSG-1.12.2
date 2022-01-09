package mrjake.aunis.gui.container.zpm;

import mrjake.aunis.gui.util.ContainerHelper;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.stargate.power.StargateAbstractEnergyStorage;
import mrjake.aunis.state.StateTypeEnum;
import mrjake.aunis.tileentity.energy.CapacitorTile;
import mrjake.aunis.tileentity.energy.ZPMTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;

public class ZPMContainer extends Container {

    public ZPMTile zpmTile;

    private BlockPos pos;
    private int lastEnergyStored;
    private int energyTransferedLastTick;

    public ZPMContainer(IInventory playerInventory, World world, int x, int y, int z) {
        pos = new BlockPos(x, y, z);
        zpmTile = (ZPMTile) world.getTileEntity(pos);

        for (Slot slot : ContainerHelper.generatePlayerSlots(playerInventory, 86))
            addSlotToContainer(slot);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        StargateAbstractEnergyStorage energyStorage = (StargateAbstractEnergyStorage) zpmTile.getCapability(CapabilityEnergy.ENERGY, null);

        if (lastEnergyStored != energyStorage.getEnergyStored() || energyTransferedLastTick != zpmTile.getEnergyTransferedLastTick()) {
            for (IContainerListener listener : listeners) {
                if (listener instanceof EntityPlayerMP) {
                    AunisPacketHandler.INSTANCE.sendTo(new StateUpdatePacketToClient(pos, StateTypeEnum.GUI_UPDATE, zpmTile.getState(StateTypeEnum.GUI_UPDATE)), (EntityPlayerMP) listener);
                }
            }

            lastEnergyStored = energyStorage.getEnergyStored();
            energyTransferedLastTick = zpmTile.getEnergyTransferedLastTick();
        }
    }
}
