package tauri.dev.jsg.tileentity.props;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.stargate.StargateDimensionConfig;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.packet.StateUpdateRequestToServer;
import tauri.dev.jsg.renderer.props.DestinyCountDownRendererState;
import tauri.dev.jsg.sound.SoundEventEnum;
import tauri.dev.jsg.stargate.EnumStargateState;
import tauri.dev.jsg.stargate.StargateClosedReasonEnum;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateProviderInterface;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.tileentity.stargate.StargateUniverseBaseTile;
import tauri.dev.jsg.tileentity.util.PreparableInterface;
import tauri.dev.jsg.util.ILinkable;
import tauri.dev.jsg.util.LinkingHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static tauri.dev.jsg.sound.JSGSoundHelper.playSoundEvent;
import static tauri.dev.jsg.state.StateTypeEnum.RENDERER_UPDATE;

@Optional.InterfaceList({@Optional.Interface(iface = "li.cil.oc.api.network.Environment", modid = "opencomputers")})
public class DestinyCountDownTile extends TileEntity implements ICapabilityProvider, ITickable, Environment, StateProviderInterface, ILinkable, PreparableInterface {

    public long countdownTo = -1; // in ticks!

    /**
     * @return countdown in TICKS!
     */
    public long getCountdownTicks() {
        if (countdownTo == -1) return 0;
        return countdownTo - world.getTotalWorldTime();
    }

    private long countStart = -1;
    private boolean gateOpenedThisRound = false;

    public void setCountDown(long countToTime) {
        this.countdownTo = countToTime;
        countStart = world.getTotalWorldTime();
        gateOpenedThisRound = false;
        markDirty();
        if (getCountdownTicks() > 0) {
            playSound(EnumCountDownEventType.START);
            sendSignal(null, "countdown_start", new Object[]{getCountdownTicks()});
        }
        sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
    }


    private boolean addedToNetwork = false;
    protected NetworkRegistry.TargetPoint targetPoint;
    public DestinyCountDownRendererState rendererState = new DestinyCountDownRendererState();

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            targetPoint = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
            markDirty();
            updateLinkStatus();
            sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
        } else {
            isClientUpdated = false;
            markDirty();
        }
    }

    private boolean isClientUpdated = false;

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

            long i = getCountdownTicks();

            if (i < -(20L * JSGConfig.countdownConfig.zeroDelay)) {
                countdownTo = -1;
                markDirty();
                sendSignal(null, "countdown_reset", new Object[]{i});
                sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
            }

            if (!gateOpenedThisRound && getCountdownTicks() > 1300 && (world.getTotalWorldTime() - countStart) > (20L * JSGConfig.countdownConfig.dialStartDelay) && (world.getTotalWorldTime() % 40 == 0)) {
                if (isLinked()) {
                    StargateUniverseBaseTile gate = getLinkedGate(world);
                    if (gate != null) {
                        EnumStargateState state = gate.getStargateState();
                        if (state.idle() && gate.isMerged()) {
                            StargateAddress foundAddress = gate.getRandomNearbyGate();
                            if (foundAddress != null) {

                                int size = 6;
                                StargatePos relPos = gate.getNetwork().getStargate(foundAddress);
                                if (relPos != null) {
                                    if (!StargateDimensionConfig.isGroupEqual(DimensionManager.getProviderType(relPos.dimensionID), gate.getWorld().provider.getDimensionType()))
                                        size = 7;

                                    gate.dialAddress(foundAddress, size);
                                    gateOpenedThisRound = true;
                                    markDirty();
                                }
                            }
                        }
                    }
                }
            }

            if (countdownTo != -1) {
                // zero
                if (i == 0) {
                    playSound(EnumCountDownEventType.ZERO);
                    sendSignal(null, "countdown_zero", new Object[]{0});
                    gateOpenedThisRound = false;
                    markDirty();

                    if (isLinked()) {
                        StargateUniverseBaseTile gate = getLinkedGate(world);
                        if (gate != null) {
                            EnumStargateState state = gate.getStargateState();
                            if (state.unstable() || state.incoming() || state.engaged())
                                gate.attemptClose(StargateClosedReasonEnum.AUTOCLOSE);
                            else if (!state.idle())
                                gate.abortDialingSequence();
                        }
                    }

                }
                // one minute left
                if (i == (20 * 60)) {
                    playSound(EnumCountDownEventType.ONE_MINUTE);
                    sendSignal(null, "countdown_one_minute", new Object[]{getCountdownTicks()});
                } else if (i == 20 * 10) {
                    sendSignal(null, "countdown_ten_seconds", new Object[]{getCountdownTicks()});
                } else if (i % (20 * 60) == 0) {
                    sendSignal(null, "countdown_minute_pass", new Object[]{getCountdownTicks()});
                }
            }

            //if(world.getTotalWorldTime() % 40 == 0)
            //    sendState(StateTypeEnum.RENDERER_UPDATE, new DestinyCountDownRendererState(countdownTo));
        } else {
            if (!isClientUpdated && getCountdownTicks() <= 0) {
                // probably not loaded correctly
                isClientUpdated = true;
                markDirty();
                JSGPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, RENDERER_UPDATE));
            }
        }
    }

    public enum EnumCountDownEventType {
        START,
        ONE_MINUTE,
        ZERO
    }

    public void playSound(EnumCountDownEventType type) {
        switch (type) {
            case START:
                playSoundEvent(world, pos, SoundEventEnum.DESTINY_COUNTDOWN_START);
                break;
            case ZERO:
                playSoundEvent(world, pos, SoundEventEnum.DESTINY_COUNTDOWN_STOP);
                break;
            case ONE_MINUTE:
                playSoundEvent(world, pos, SoundEventEnum.DESTINY_COUNTDOWN_ONE_MINUTE);
                break;
            default:
                break;
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
            //JSG.info("Client got new countdown: " + countdownTo);
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
        compound.setBoolean("gateOpenedThisRound", gateOpenedThisRound);

        if (isLinked()) {
            compound.setLong("linkedGate", gatePos.toLong());
            compound.setInteger("linkId", linkId);
        }

        if (node != null) {
            NBTTagCompound nodeCompound = new NBTTagCompound();
            node.save(nodeCompound);

            compound.setTag("node", nodeCompound);
        }
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        countdownTo = compound.getLong("countdown");
        gateOpenedThisRound = compound.getBoolean("gateOpenedThisRound");

        if (compound.hasKey("linkedGate")) this.gatePos = BlockPos.fromLong(compound.getLong("linkedGate"));
        if (compound.hasKey("linkId")) this.linkId = compound.getInteger("linkId");

        if (node != null && compound.hasKey("node")) node.load(compound.getCompoundTag("node"));
        markDirty();
        super.readFromNBT(compound);
    }

    // Linking

    @Override
    public boolean canLinkTo() {
        return !isLinked();
    }

    private BlockPos gatePos;
    private int linkId = -1;

    @Nullable
    public StargateUniverseBaseTile getLinkedGate(World world) {
        if (gatePos == null) return null;

        return (StargateUniverseBaseTile) world.getTileEntity(gatePos);
    }

    public boolean isLinked() {
        return gatePos != null && world.getTileEntity(gatePos) instanceof StargateUniverseBaseTile;
    }

    public void setLinkedGate(BlockPos dhdPos, int linkId) {
        this.gatePos = dhdPos;
        this.linkId = linkId;

        markDirty();
    }

    public void updateLinkStatus() {
        BlockPos closestUnlinked = LinkingHelper.findClosestUnlinked(world, pos, LinkingHelper.getDhdRange(), JSGBlocks.STARGATE_UNIVERSE_BASE_BLOCK, this.getLinkId());
        int linkId = LinkingHelper.getLinkId();

        if (closestUnlinked != null) {
            StargateUniverseBaseTile stargateUniverseBaseTile = (StargateUniverseBaseTile) world.getTileEntity(closestUnlinked);
            if (stargateUniverseBaseTile != null) {
                stargateUniverseBaseTile.setLinkedCountdown(pos, linkId);
                setLinkedGate(closestUnlinked, linkId);
                markDirty();
            }
        }
    }

    @Override
    public boolean prepare(ICommandSender sender, ICommand command) {
        setLinkedGate(null, -1);
        return true;
    }

    @Override
    public int getLinkId() {
        return linkId;
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
        if (!args.isInteger(0)) return new Object[]{false, "Please, insert a number as first argument!"};
        long time = args.checkInteger(0);
        setCountDown(this.world.getTotalWorldTime() + time);
        return new Object[]{true, "Countdown set to " + time + " ticks!"};
    }
}
