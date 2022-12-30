package tauri.dev.jsg.tileentity.props;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.renderer.props.AncientSignRendererState;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateProviderInterface;
import tauri.dev.jsg.state.StateTypeEnum;

import javax.annotation.Nonnull;

public class AncientSignTile extends TileEntitySign implements StateProviderInterface, ITickable {
    public static final int LINES = 7;
    public String[] ancientText = getNewLines();

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            targetPoint = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
            markDirty();
            sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
        }
    }

    @Override
    public void update(){
        if(!world.isRemote){
            if(targetPoint == null){
                targetPoint = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
                markDirty();
                sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
            }

            if(lastColor != color){
                lastColor = color;
                markDirty();
                sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
            }
        }
    }

    public static String[] getNewLines(){
        return new String[]{
                "", "",
                "", "",
                "", "",
                ""
        };
    }

    public int color = 0xffffff;
    private int lastColor = -1;

    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        for (int i = 0; i < LINES; ++i) {
            compound.setString("AncientText" + (i + 1), this.ancientText[i]);
        }

        compound.setInteger("color", color);

        return super.writeToNBT(compound);
    }

    public void fromItemStack(@Nonnull NBTTagCompound compound) {
        for (int i = 0; i < LINES; ++i) {
            this.ancientText[i] = compound.getString("AncientText" + (i + 1));
        }
        color = compound.getInteger("color");
        markDirty();
        sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
    }

    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);

        for (int i = 0; i < LINES; ++i) {
            this.ancientText[i] = compound.getString("AncientText" + (i + 1));
        }

        color = compound.getInteger("color");
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

    @Override
    public State getState(StateTypeEnum stateType) {
        if(stateType == StateTypeEnum.RENDERER_UPDATE)
            return new AncientSignRendererState(this.ancientText, this.color);
        return null;
    }


    @Override
    public State createState(StateTypeEnum stateType) {
        if(stateType == StateTypeEnum.RENDERER_UPDATE)
            return new AncientSignRendererState();
        return null;
    }

    @Override
    public void setState(StateTypeEnum stateType, State state) {
        if(stateType == StateTypeEnum.RENDERER_UPDATE){
            AncientSignRendererState s = (AncientSignRendererState) state;
            this.ancientText = s.lines;
            this.color = s.color;
            markDirty();
        }

    }
}
