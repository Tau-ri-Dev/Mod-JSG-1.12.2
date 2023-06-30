package tauri.dev.jsg.tileentity.props;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateProviderInterface;
import tauri.dev.jsg.state.StateTypeEnum;

import javax.annotation.Nonnull;

public class DestinyBearingTile extends TileEntity implements ITickable, StateProviderInterface {

    public static class DestinyBearingRenderState extends State {

        public DestinyBearingRenderState(boolean isActive){
            this.isActive = isActive;
        }

        public boolean isActive;

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeBoolean(isActive);
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            isActive = buf.readBoolean();
        }
    }

    public void updateState(boolean activate){
        if(isActive != activate){
            this.isActive = activate;
            markDirty();
            sendState(StateTypeEnum.RENDERER_STATE, getState(StateTypeEnum.RENDERER_STATE));
        }
    }

    public boolean isActive = false;

    @Override
    public void update() {
        if(!world.isRemote){
            if(targetPoint == null){
                targetPoint = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
                markDirty();
                sendState(StateTypeEnum.RENDERER_STATE, getState(StateTypeEnum.RENDERER_STATE));
            }
        }
    }

    protected NetworkRegistry.TargetPoint targetPoint;
    public void sendState(StateTypeEnum type, State state) {
        if (world.isRemote) return;
        if (targetPoint != null) {
            JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, type, state), targetPoint);
        } else {
            JSG.debug("targetPoint was null trying to send " + type + " from " + this.getClass().getCanonicalName());
        }
    }

    // Server
    @Override
    public State getState(StateTypeEnum stateType) {
        if(stateType == StateTypeEnum.RENDERER_STATE){
            return new DestinyBearingRenderState(isActive);
        }
        return null;
    }

    // Server
    @Override
    public State createState(StateTypeEnum stateType) {
        if(stateType == StateTypeEnum.RENDERER_STATE){
            return new DestinyBearingRenderState(isActive);
        }
        return null;
    }

    // Client
    @Override
    public void setState(StateTypeEnum stateType, State state) {
        if(stateType == StateTypeEnum.RENDERER_STATE){
            this.isActive = ((DestinyBearingRenderState) state).isActive;
        }
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        this.isActive = compound.getBoolean("active");
        super.readFromNBT(compound);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setBoolean("active", isActive);
        return super.writeToNBT(compound);
    }
}
