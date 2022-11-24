package tauri.dev.jsg.tileentity.props;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.renderer.props.DestinyCountDownRendererState;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateProviderInterface;
import tauri.dev.jsg.state.StateTypeEnum;

import javax.annotation.Nonnull;

import static tauri.dev.jsg.state.StateTypeEnum.RENDERER_UPDATE;

@Optional.InterfaceList({@Optional.Interface(iface = "li.cil.oc.api.network.Environment", modid = "opencomputers")})
public class DestinyCountDownTile extends TileEntity implements ICapabilityProvider, ITickable, Environment, StateProviderInterface {

    private long countdownTo = 0; // in ticks!

    /**
     * @return countdown in TICKS!
     */
    public long getCountdownTicks(){
        return countdownTo - world.getTotalWorldTime();
    }

    public void setCountDown(long countToTime){
        this.countdownTo = countToTime;
        markDirty();
        sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
    }


    private boolean addedToNetwork = false;
    protected NetworkRegistry.TargetPoint targetPoint;
    public DestinyCountDownRendererState rendererState = new DestinyCountDownRendererState();
    @Override
    public void onLoad() {
        if (!world.isRemote) {
            targetPoint = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
            sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
        }
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            if (!addedToNetwork) {
                addedToNetwork = true;
                JSG.ocWrapper.joinOrCreateNetwork(this);
            }
            if (targetPoint == null) {
                targetPoint = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
                markDirty();
            }
        }
    }

    protected void sendState(StateTypeEnum type, State state) {
        if (world.isRemote) return;
        if (targetPoint != null) {
            JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, type, state), targetPoint);
        } else {
            JSG.logger.debug("targetPoint was null trying to send " + type + " from " + this.getClass().getCanonicalName());
        }
    }

    /**
     * Server-side method. Called on {@link TileEntity} to get specified {@link State}.
     *
     * @param stateType {@link StateTypeEnum} State to be collected/returned
     * @return {@link State} instance
     */
    @Override
    public State getState(StateTypeEnum stateType) {
        if (stateType == RENDERER_UPDATE) {
            return new DestinyCountDownRendererState(countdownTo);
        }
        return null;
    }

    /**
     * Client-side method. Called on {@link TileEntity} to get specified {@link State} instance
     * to recreate State by deserialization
     *
     * @param stateType {@link StateTypeEnum} State to be deserialized
     * @return deserialized {@link State}
     */
    @Override
    public State createState(StateTypeEnum stateType) {
        if (stateType == RENDERER_UPDATE) {
            return new DestinyCountDownRendererState();
        }
        return null;
    }

    /**
     * Client-side method. Sets appropriate fields in client-side tile entity for it
     * to mirror the server-side tile entity
     *
     * @param stateType {@link StateTypeEnum} State to be applied
     * @param state     {@link State} instance obtained from packet
     */
    @Override
    public void setState(StateTypeEnum stateType, State state) {
        if (stateType == RENDERER_UPDATE) {
            rendererState = (DestinyCountDownRendererState) state;
            this.countdownTo = rendererState.countdownTo;
            markDirty();
        }
    }

    public DestinyCountDownRendererState getRendererState() {
        return rendererState;
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setLong("countdown", countdownTo);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        countdownTo = compound.getLong("countdown");
        super.readFromNBT(compound);
    }

    // ------------------------------------------------------------
    // Node-related work
    private final Node node = JSG.ocWrapper.createNode(this, "countdown");

    @Override
    public void onChunkUnload() {
        if (node != null) node.remove();
        super.onChunkUnload();
    }

    @Override
    public void invalidate() {
        if (node != null) node.remove();
        super.invalidate();
    }

    @Override
    @Optional.Method(modid = "opencomputers")
    public Node node() {
        return node;
    }

    @Override
    @Optional.Method(modid = "opencomputers")
    public void onConnect(Node node) {
    }

    @Override
    @Optional.Method(modid = "opencomputers")
    public void onDisconnect(Node node) {
    }

    @Override
    @Optional.Method(modid = "opencomputers")
    public void onMessage(Message message) {
    }

    public void sendSignal(Object context, String name, Object... params) {
        JSG.ocWrapper.sendSignalToReachable(node, (Context) context, name, params);
    }

    // ------------------------------------------------------------
    // Methods

    @net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getJSGVersion(Context context, Arguments args) {
        return new Object[]{JSG.MOD_VERSION};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function(long) -- set countdown to a time in ticks")
    public Object[] setCountdown(Context context, Arguments args) {
        if(!args.isInteger(0)) return new Object[]{false, "Please, insert a number as first argument!"};
        long time = args.checkInteger(0);
        setCountDown(this.world.getTotalWorldTime() + time);
        return new Object[]{true, "Countdown set to " + time + " ticks!"};
    }
}
