package mrjake.aunis.tileentity.transportrings;

import mrjake.aunis.stargate.EnumScheduledTask;
import mrjake.aunis.tileentity.util.ScheduledTaskExecutorInterface;
import net.minecraft.nbt.NBTTagCompound;

public class TransportRingsOriTile extends TransportRingsAbstractTile implements ScheduledTaskExecutorInterface {

    @Override
    public int getSupportedCapacitors() {
        return 3;
    }

    @Override
    public void executeTask(EnumScheduledTask scheduledTask, NBTTagCompound customData) {
        super.executeTask(scheduledTask, customData);
    }
}
