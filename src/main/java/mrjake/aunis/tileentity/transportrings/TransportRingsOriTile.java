package mrjake.aunis.tileentity.transportrings;

import mrjake.aunis.renderer.transportrings.TransportRingsAbstractRenderer;
import mrjake.aunis.renderer.transportrings.TransportRingsOriRenderer;
import mrjake.aunis.stargate.EnumScheduledTask;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

public class TransportRingsOriTile extends TransportRingsAbstractTile implements ITickable {
    @Override
    public TransportRingsAbstractRenderer getNewRenderer(){
        return new TransportRingsOriRenderer();
    }

    @Override
    public int getSupportedCapacitors() {
        return 3;
    }

    @Override
    public void executeTask(EnumScheduledTask scheduledTask, NBTTagCompound customData) {
        super.executeTask(scheduledTask, customData);
    }
}
