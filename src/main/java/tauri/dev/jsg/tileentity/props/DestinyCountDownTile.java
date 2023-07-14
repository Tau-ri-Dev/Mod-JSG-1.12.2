package tauri.dev.jsg.tileentity.props;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.ingame.*;
import tauri.dev.jsg.gui.container.countdown.CountDownContainerGuiUpdate;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.packet.StateUpdateRequestToServer;
import tauri.dev.jsg.renderer.props.DestinyCountDownRendererState;
import tauri.dev.jsg.sound.SoundEventEnum;
import tauri.dev.jsg.stargate.EnumStargateState;
import tauri.dev.jsg.stargate.NearbyGate;
import tauri.dev.jsg.stargate.StargateClosedReasonEnum;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateProviderInterface;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.tileentity.stargate.StargateUniverseBaseTile;
import tauri.dev.jsg.tileentity.util.PreparableInterface;
import tauri.dev.jsg.util.LinkingHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static tauri.dev.jsg.sound.JSGSoundHelper.playSoundEvent;
import static tauri.dev.jsg.state.StateTypeEnum.RENDERER_UPDATE;
import static tauri.dev.jsg.tileentity.props.DestinyCountDownTile.ConfigOptions.ENABLE_GATE_CLOSING;
import static tauri.dev.jsg.tileentity.props.DestinyCountDownTile.ConfigOptions.ENABLE_GATE_OPENING;

@Optional.Interface(iface = "li.cil.oc.api.network.Environment", modid = "opencomputers")
public class DestinyCountDownTile extends TileEntity implements ICapabilityProvider, ITickable, Environment, StateProviderInterface, PreparableInterface, ITileConfig {

    public long countdownTo = -1; // in ticks!

    /**
     * @return countdown in TICKS!
     */
    public long getCountdownTicks() {
        if (countdownTo == -1) return 0;
        return countdownTo - world.getTotalWorldTime();
    }

    @SuppressWarnings("all")
    private long countStart = -1;
    private long gateIdleFrom = -1;
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
            initConfig();
            if (!addedToNetwork) {
                addedToNetwork = true;
                JSG.ocWrapper.joinOrCreateNetwork(this);
            }
            if (targetPoint == null) {
                targetPoint = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
                markDirty();
            }

            long i = getCountdownTicks();

            if (i < -(20L * JSGConfig.General.countdownConfig.zeroDelay)) {
                countdownTo = -1;
                markDirty();
                sendSignal(null, "countdown_reset", new Object[]{i});
                sendState(StateTypeEnum.RENDERER_UPDATE, getState(StateTypeEnum.RENDERER_UPDATE));
            }

            /*
             * OPENING THE GATE
             */
            if (getConfig().getOption(ENABLE_GATE_OPENING.id).getBooleanValue() && !gateOpenedThisRound && i > 1300 && (world.getTotalWorldTime() % 40 == 0)) {
                StargateUniverseBaseTile gate = getNearestGate();
                if (gate != null) {
                    EnumStargateState state = gate.getStargateState();
                    if(state != null){
                        if(!state.idle() && gateIdleFrom != -1){
                            gateIdleFrom = -1;
                            markDirty();
                        }
                        if (state.idle() && gate.isMerged()) {
                            if((world.getTotalWorldTime() - gateIdleFrom) > (20L * JSGConfig.General.countdownConfig.dialStartDelay)) {
                                NearbyGate found = gate.getRandomNearbyGate();
                                if (found != null) {
                                    if (gateIdleFrom == -1) {
                                        gateIdleFrom = this.world.getTotalWorldTime();
                                        markDirty();
                                    } else {
                                        StargateAddress foundAddress = found.address;
                                        int symbols = (found.symbolsNeeded - 1);
                                        if (foundAddress != null) {
                                            gate.dialAddress(foundAddress, symbols, false);
                                            gateOpenedThisRound = true;
                                            markDirty();
                                        }
                                    }
                                }
                            }
                        }
                        else if(state.engaged()){
                            // gate is already open while countdown counts (by dialer or OC or incoming)
                            gateOpenedThisRound = true;
                            markDirty();
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
                    gateIdleFrom = -1;
                    markDirty();

                    /*
                     * CLOSING THE GATE
                     */
                    if (getConfig().getOption(ENABLE_GATE_CLOSING.id).getBooleanValue()) {
                        StargateUniverseBaseTile gate = getNearestGate();
                        if (gate != null) {
                            EnumStargateState state = gate.getStargateState();
                            if(state != null){
                                if (state.incoming() || state.engaged())
                                    gate.attemptClose(StargateClosedReasonEnum.AUTOCLOSE);
                                else if (!state.idle() && (gate.connectedToGate || gate.connectingToGate))
                                    gate.abortDialingSequence();
                            }
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
        } else {
            if (!isClientUpdated && getCountdownTicks() <= 0) {
                // probably not loaded correctly
                isClientUpdated = true;
                markDirty();
                JSGPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, RENDERER_UPDATE));
            }
        }
    }

    public StargateUniverseBaseTile getNearestGate(){
        ArrayList<BlockPos> blacklist = new ArrayList<>();
        BlockPos nearest;
        do {
            nearest = LinkingHelper.findClosestPos(world, pos, LinkingHelper.getDhdRange(), new Block[]{JSGBlocks.STARGATE_UNIVERSE_BASE_BLOCK}, blacklist);
            blacklist.add(nearest);
            if (nearest != null) {
                TileEntity te = world.getTileEntity(nearest);
                if (te instanceof StargateUniverseBaseTile) {
                    StargateUniverseBaseTile uniTile = ((StargateUniverseBaseTile) te);
                    if(uniTile.isMerged())
                        return uniTile;
                }
            }
        } while (nearest != null);
        return null;
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
            JSG.debug("targetPoint was null trying to send " + type + " from " + this.getClass().getCanonicalName());
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
        switch (stateType) {
            case RENDERER_UPDATE:
                return new DestinyCountDownRendererState(countdownTo, getConfig());
            case GUI_STATE:
            case GUI_UPDATE:
                return new CountDownContainerGuiUpdate(getConfig());
            default:
                break;
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
        switch (stateType) {
            case RENDERER_UPDATE:
                return new DestinyCountDownRendererState();
            case GUI_STATE:
            case GUI_UPDATE:
                return new CountDownContainerGuiUpdate();
            default:
                break;
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
    @SideOnly(Side.CLIENT)
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {
            case RENDERER_UPDATE:
                rendererState = (DestinyCountDownRendererState) state;
                this.countdownTo = rendererState.countdownTo;
                this.config = rendererState.config;
                break;
            case GUI_STATE:
            case GUI_UPDATE:
                CountDownContainerGuiUpdate guiState = (CountDownContainerGuiUpdate) state;
                config = guiState.config;
                break;
            default:
                break;
        }
        markDirty();
    }

    public DestinyCountDownRendererState getRendererState() {
        return rendererState;
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setLong("countdown", countdownTo);
        compound.setBoolean("gateOpenedThisRound", gateOpenedThisRound);

        if (node != null) {
            NBTTagCompound nodeCompound = new NBTTagCompound();
            node.save(nodeCompound);

            compound.setTag("node", nodeCompound);
        }
        compound.setTag("config", config.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        countdownTo = compound.getLong("countdown");
        gateOpenedThisRound = compound.getBoolean("gateOpenedThisRound");

        if (node != null && compound.hasKey("node")) node.load(compound.getCompoundTag("node"));
        config.deserializeNBT(compound.getCompoundTag("config"));
        markDirty();
        super.readFromNBT(compound);
    }

    @Override
    public boolean prepare(ICommandSender sender, ICommand command) {
        return true;
    }


    // -----------------------------------------------------------------
    // Tile entity config

    protected JSGTileEntityConfig config = new JSGTileEntityConfig();

    public enum ConfigOptions implements ITileConfigEntry {
        ENABLE_GATE_LINK(
                0, "enableGateLink", JSGConfigOptionTypeEnum.BOOLEAN, "true",
                "Enable linking to a universe gate?"
        ),
        ENABLE_GATE_OPENING(
                1, "enableGateOpening", JSGConfigOptionTypeEnum.BOOLEAN, "true",
                "Enable opening linked gate after countdown set?",
                "This option needs enabled \"enableGateLink\" option!"
        ),
        ENABLE_GATE_CLOSING(
                2, "enableGateClosing", JSGConfigOptionTypeEnum.BOOLEAN, "true",
                "Enable closing linked gate after countdown reach zero?",
                "This option needs enabled \"enableGateLink\" option!"
        ),
        SWITCH_TO_CLOCK(
                3, "switchToClock", JSGConfigOptionTypeEnum.BOOLEAN, "false",
                "Switch countdown to clock when its turned off"
        );

        public final int id;
        public final String label;
        public final String[] comment;
        public final JSGConfigOptionTypeEnum type;
        public final String defaultValue;
        public List<JSGConfigEnumEntry> possibleValues;

        public final int minInt;
        public final int maxInt;

        ConfigOptions(int optionId, String label, JSGConfigOptionTypeEnum type, String defaultValue, String... comment) {
            this(optionId, label, type, defaultValue, -1, -1, comment);
        }

        ConfigOptions(int optionId, String label, JSGConfigOptionTypeEnum type, String defaultValue, int minInt, int maxInt, String... comment) {
            this.id = optionId;
            this.label = label;
            this.type = type;
            this.defaultValue = defaultValue;
            this.minInt = minInt;
            this.maxInt = maxInt;
            this.comment = comment;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String getLabel() {
            return label;
        }

        @Override
        public String[] getComment() {
            return comment;
        }

        @Override
        public JSGConfigOptionTypeEnum getType() {
            return type;
        }

        @Override
        public String getDefaultValue() {
            return defaultValue;
        }

        @Override
        public List<JSGConfigEnumEntry> getPossibleValues() {
            return possibleValues;
        }

        @Override
        public int getMin() {
            return minInt;
        }

        @Override
        public int getMax() {
            return maxInt;
        }
    }

    @Override
    public JSGTileEntityConfig getConfig() {
        return this.config;
    }

    @Override
    public void setConfig(JSGTileEntityConfig config) {
        for (JSGConfigOption o : config.getOptions()) {
            this.config.getOption(o.id).setValue(o.getStringValue());
        }
        markDirty();
    }

    @Override
    public void setConfigAndUpdate(JSGTileEntityConfig config) {
        setConfig(config);
        sendState(StateTypeEnum.GUI_STATE, getState(StateTypeEnum.GUI_STATE));
    }

    @Override
    public void initConfig() {
        JSGTileEntityConfig.initConfig(getConfig(), ConfigOptions.values());
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

    @Optional.Method(modid = "opencomputers")
    @Callback
    @SuppressWarnings("unused")
    public Object[] getJSGVersion(Context context, Arguments args) {
        return new Object[]{JSG.MOD_VERSION};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function(long) -- set countdown to a time in ticks")
    @SuppressWarnings("unused")
    public Object[] setCountdown(Context context, Arguments args) {
        if (!args.isInteger(0)) return new Object[]{false, "Please, insert a number as first argument!"};
        long time = args.checkInteger(0);
        setCountDown(this.world.getTotalWorldTime() + time);
        return new Object[]{true, "Countdown set to " + time + " ticks!"};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(getter = true)
    @SuppressWarnings("unused")
    public Object[] remainingTicks(Context context, Arguments args) {
        return new Object[]{getCountdownTicks()};
    }
}
