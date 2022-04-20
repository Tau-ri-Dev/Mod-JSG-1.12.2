package mrjake.aunis.tileentity.transportrings;

import mrjake.aunis.renderer.transportrings.TransportRingsAbstractRenderer;
import mrjake.aunis.renderer.transportrings.TransportRingsGoauldRenderer;
import mrjake.aunis.stargate.EnumScheduledTask;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

public class TransportRingsGoauldTile extends TransportRingsAbstractTile implements ITickable {
    @Override
    public TransportRingsAbstractRenderer getNewRenderer() {
        return new TransportRingsGoauldRenderer();
    }

    @Override
    public int getSupportedCapacitors() {
        return 2;
    }

    @Override
    public void executeTask(EnumScheduledTask scheduledTask, NBTTagCompound customData) {
        super.executeTask(scheduledTask, customData);
    }
}
