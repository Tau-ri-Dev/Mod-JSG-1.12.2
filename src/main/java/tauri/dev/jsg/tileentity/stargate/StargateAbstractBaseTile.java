package tauri.dev.jsg.tileentity.stargate;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.DimensionType;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.api.event.*;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.block.dialhomedevice.DHDBlock;
import tauri.dev.jsg.chunkloader.ChunkManager;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.JSGConfigUtil;
import tauri.dev.jsg.config.stargate.StargateDimensionConfig;
import tauri.dev.jsg.config.stargate.StargateTimeLimitModeEnum;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.item.notebook.PageNotebookItem;
import tauri.dev.jsg.loader.OriginsLoader;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.packet.StateUpdateRequestToServer;
import tauri.dev.jsg.particle.ParticleWhiteSmoke;
import tauri.dev.jsg.power.general.EnergyRequiredToOperate;
import tauri.dev.jsg.power.general.SmallEnergyStorage;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.renderer.stargate.StargateAbstractRendererState;
import tauri.dev.jsg.renderer.stargate.StargateAbstractRendererState.StargateAbstractRendererStateBuilder;
import tauri.dev.jsg.sound.*;
import tauri.dev.jsg.stargate.*;
import tauri.dev.jsg.stargate.merging.StargateAbstractMergeHelper;
import tauri.dev.jsg.stargate.network.*;
import tauri.dev.jsg.stargate.teleportation.EventHorizon;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateProviderInterface;
import tauri.dev.jsg.state.StateSoundPositionedUpdate;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.state.stargate.StargateFlashState;
import tauri.dev.jsg.state.stargate.StargateRendererActionState;
import tauri.dev.jsg.state.stargate.StargateVaporizeBlockParticlesRequest;
import tauri.dev.jsg.tileentity.util.PreparableInterface;
import tauri.dev.jsg.tileentity.util.ScheduledTask;
import tauri.dev.jsg.tileentity.util.ScheduledTaskExecutorInterface;
import tauri.dev.jsg.util.JSGAdvancementsUtil;
import tauri.dev.jsg.util.JSGAxisAlignedBB;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Vector2f;
import java.util.*;

import static tauri.dev.jsg.stargate.network.internalgates.StargateAddressesEnum.tryDialInternal;
import static tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile.ConfigOptions.ALLOW_INCOMING;
import static tauri.dev.jsg.util.JSGAdvancementsUtil.tryTriggerRangedAdvancement;

@Optional.InterfaceList({@Optional.Interface(iface = "li.cil.oc.api.network.Environment", modid = "opencomputers"), @Optional.Interface(iface = "li.cil.oc.api.network.WirelessEndpoint", modid = "opencomputers")})
public abstract class StargateAbstractBaseTile extends TileEntity implements StateProviderInterface, ITickable, ICapabilityProvider, ScheduledTaskExecutorInterface, Environment, WirelessEndpoint, PreparableInterface {

    public StargateNetwork getNetwork() {
        if (network == null) network = StargateNetwork.get(world);
        return network;
    }

    // ------------------------------------------------------------------------
    // Stargate state

    protected EnumStargateState stargateState = EnumStargateState.IDLE;

    public final EnumStargateState getStargateState() {
        return stargateState;
    }

    private boolean isInitiating;

    private BlockPos lastPos = pos;

    protected void engageGate() {
        stargateState = isInitiating ? EnumStargateState.ENGAGED_INITIATING : EnumStargateState.ENGAGED;
        eventHorizon.reset();

        JSGSoundHelper.playPositionedSound(world, getGateCenterPos(), SoundPositionedEnum.WORMHOLE_LOOP, true);

        if (targetGatePos != null)
            new StargateOpenedEvent(this, targetGatePos.getTileEntity(), isInitiating).post();
        else if (randomIncomingIsActive)
            new StargateOpenedEvent(this, this, isInitiating).post();

        sendSignal(null, "stargate_wormhole_stabilized", new Object[]{isInitiating});

        markDirty();
    }

    @SuppressWarnings("all")
    protected void disconnectGate(boolean force) {
        disconnectGate();
        if (force)
            stargateState = EnumStargateState.IDLE;
        markDirty();
    }

    protected void disconnectGate() {

        if (stargateState != EnumStargateState.INCOMING)
            stargateState = EnumStargateState.IDLE;
        else
            isIncoming = false;
        getAutoCloseManager().reset();

        if (!(this instanceof StargateOrlinBaseTile)) dialedAddress.clear();

        new StargateClosedEvent(this).post();

        ChunkManager.unforceChunk(world, new ChunkPos(pos));
        sendSignal(null, "stargate_wormhole_closed_fully", new Object[]{isInitiating});

        connectedToGate = false;
        connectingToGate = false;

        markDirty();
    }

    protected void failGate() {
        if (stargateState != EnumStargateState.INCOMING) stargateState = EnumStargateState.IDLE;
        connectedToGate = false;
        connectingToGate = false;

        if (!(this instanceof StargateOrlinBaseTile)) dialedAddress.clear();

        markDirty();
    }

    public void onBlockBroken() {
        for (StargateAddress address : gateAddressMap.values())
            getNetwork().removeStargate(address);
    }

    protected void onGateBroken() {
        world.setBlockToAir(getGateCenterPos());
        resetTargetIncomingAnimation();

        if (stargateState.initiating() || (stargateState.engaged() && targetGatePos == null)) {
            attemptClose(StargateClosedReasonEnum.CONNECTION_LOST);
        } else if (stargateState.engaged()) {
            targetGatePos.getTileEntity().attemptClose(StargateClosedReasonEnum.CONNECTION_LOST);
        }

        dialedAddress.clear();
        connectedToGate = false;
        connectingToGate = false;
        targetGatePos = null;
        scheduledTasks.clear();
        stargateState = EnumStargateState.IDLE;
        sendRenderingUpdate(StargateRendererActionState.EnumGateAction.CLEAR_CHEVRONS, 0, false);

        ChunkManager.unforceChunk(world, new ChunkPos(pos));
        JSGSoundHelper.playPositionedSound(world, getGateCenterPos(), SoundPositionedEnum.WORMHOLE_LOOP, false);

        markDirty();
    }

    protected void onGateMerged() {
    }

    public boolean canAcceptConnectionFrom(StargatePos targetGatePos) {
        boolean allowConnectToDialing = JSGConfig.Stargate.mechanics.allowConnectToDialing;

        if (allowConnectToDialing) {
            if (isMerged) {
                switch (stargateState) {
                    case IDLE:
                    case DIALING:
                    case DIALING_COMPUTER:
                    case INCOMING:
                        return true;
                    default:
                        break;
                }
                return false;
            }
        } else
            return isMerged && (stargateState.idle() || stargateState.incoming());
        return false;
    }

    protected void sendRenderingUpdate(StargateRendererActionState.EnumGateAction gateAction, int chevronCount, boolean modifyFinal, EnumIrisType irisType, EnumIrisState irisState, long irisAnimation) {
        sendState(StateTypeEnum.RENDERER_UPDATE, new StargateRendererActionState(gateAction, chevronCount, modifyFinal, irisType, irisState, irisAnimation));
    }

    protected void sendRenderingUpdate(StargateRendererActionState.EnumGateAction gateAction, int chevronCount, boolean modifyFinal) {
        sendState(StateTypeEnum.RENDERER_UPDATE, new StargateRendererActionState(gateAction, chevronCount, modifyFinal));
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
     * Instance of the {@link EventHorizon} for teleporting entities.
     */
    protected EventHorizon eventHorizon;

    public JSGAxisAlignedBB getEventHorizonLocalBox() {
        return eventHorizon.getLocalBox();
    }

    /**
     * Get the bounding box of the horizon.
     *
     * @param server Calling side.
     * @return Horizon bounding box.
     */
    protected abstract JSGAxisAlignedBB getHorizonTeleportBox(boolean server);

    private AutoCloseManager autoCloseManager;

    private AutoCloseManager getAutoCloseManager() {
        if (autoCloseManager == null) autoCloseManager = new AutoCloseManager(this);

        return autoCloseManager;
    }

    public void setMotionOfPassingEntity(int entityId, Vector2f motionVector) {
        eventHorizon.setMotion(entityId, motionVector);
    }

    /**
     * Called to immediately teleport the entity (after entity has received motion from the client)
     */
    public void teleportEntity(int entityId) {
        eventHorizon.teleportEntity(entityId);
    }

    /**
     * Called when entity tries to come through the gate on the back side
     */
    public void removeEntity(int entityId) {
        eventHorizon.removeEntity(entityId);
    }


    // ------------------------------------------------------------------------
    // Stargate connection

    /**
     * Wrapper for {@link this#attemptOpenDialed()} which calls {@link this#dialingFailed(StargateOpenResult)}
     * when the checks fail.
     *
     * @return {@link StargateOpenResult} returned by {@link this#attemptOpenDialed()}.
     */
    public StargateOpenResult attemptOpenAndFail() {
        ResultTargetValid resultTarget = attemptOpenDialed();
        if (resultTarget == null) {
            return StargateOpenResult.ADDRESS_MALFORMED;
        }

        if (!resultTarget.result.ok()) {
            dialingFailed(resultTarget.result);
            if (resultTarget.targetVaild && getNetwork().getStargate(dialedAddress) != null && getNetwork().getStargate(dialedAddress).getTileEntity() != null) {
                // We can call dialing failed on the target gate
                getNetwork().getStargate(dialedAddress).getTileEntity().dialingFailed(StargateOpenResult.CALLER_HUNG_UP);
            }
        }

        return resultTarget.result;
    }

    public boolean isNoxDialing = false;

    /**
     * Attempts to open the connection to gate pointed by {@link StargateAbstractBaseTile#dialedAddress}.
     * This performs all the checks.
     */
    protected ResultTargetValid attemptOpenDialed() {
        boolean targetValid = false;
        StargateOpenResult result = checkAddressAndEnergy(dialedAddress);

        if (result.ok()) {
            targetValid = true;

            StargatePos targetGatePos = getNetwork().getStargate(dialedAddress);
            StargateAbstractBaseTile targetTile = targetGatePos.getTileEntity();

            if (new StargateOpeningEvent(this, targetGatePos.getTileEntity(), isInitiating).post()) {
                // Gate open cancelled by event
                return new ResultTargetValid(StargateOpenResult.ABORTED_BY_EVENT, targetValid);
            }

            if (!targetTile.canAcceptConnectionFrom(gatePosMap.get(getSymbolType())))
                return new ResultTargetValid(StargateOpenResult.ADDRESS_MALFORMED, targetValid);

            tryTriggerRangedAdvancement(this, JSGAdvancementsUtil.EnumAdvancementType.GATE_OPEN);

            openGate(targetGatePos, true, isNoxDialing);
            targetTile.openGate(gatePosMap.get(targetGatePos.symbolType), false, isNoxDialing);
            targetTile.dialedAddress.clear();
            targetTile.dialedAddress.addAll(gateAddressMap.get(targetGatePos.symbolType).subList(0, dialedAddress.size() - 1));
            targetTile.dialedAddress.addOrigin();
        }

        return new ResultTargetValid(result, targetValid);
    }

    /**
     * Checks if the address can be dialed.
     *
     * @param address Address to be checked.
     * @return {@code True} if the address parameter is valid and the dialed gate can be reached, {@code false} otherwise.
     */
    protected StargateOpenResult checkAddress(StargateAddressDynamic address) {
        if (!address.validate()) {
            return StargateOpenResult.ADDRESS_MALFORMED;
        }

        if (!canDialAddress(address)) return StargateOpenResult.ADDRESS_MALFORMED;

        StargateAbstractBaseTile targetTile = getNetwork().getStargate(address).getTileEntity();

        if (!targetTile.canAcceptConnectionFrom(gatePosMap.get(getSymbolType()))) {
            return StargateOpenResult.ADDRESS_MALFORMED;
        }

        return StargateOpenResult.OK;
    }

    /**
     * Checks if the address can be dialed and if the gate has power to do so.
     *
     * @param address Address to be checked.
     * @return {@code True} if the address parameter is valid and the dialed gate can be reached, {@code false} otherwise.
     */
    public StargateOpenResult checkAddressAndEnergy(StargateAddressDynamic address) {
        StargateOpenResult result = checkAddress(address);

        if (!result.ok()) return result;

        StargatePos targetGatePos = getNetwork().getStargate(address);

        if (targetGatePos == null) return StargateOpenResult.ADDRESS_MALFORMED;
        if (!hasEnergyToDial(targetGatePos)) return StargateOpenResult.NOT_ENOUGH_POWER;

        StargateAbstractBaseTile targetTile = targetGatePos.getTileEntity();
        if (targetTile instanceof StargateClassicBaseTile) {
            StargateClassicBaseTile classicTile = (StargateClassicBaseTile) targetTile;
            if (classicTile.isGateBuried())
                return StargateOpenResult.GATE_BURIED;
        }

        return StargateOpenResult.OK;
    }

    /**
     * Checks if given address points to
     * a valid target gate (and not to itself).
     *
     * @param address Address to check,
     * @return {@code True} if the gate can be reached, {@code false} otherwise.
     */
    public boolean canDialAddress(StargateAddressDynamic address) {
        StargatePos targetGatePos = getNetwork().getStargate(address);

        if (targetGatePos == null) {
            return false;
        }

        if (targetGatePos.equals(gatePosMap.get(getSymbolType()))) {
            return false;
        }

        if (checkAddressLength(address, targetGatePos)) {
            return false;
        }

        int additional = address.size() - 7;

        if (additional > 0) {
            if (!address.getAdditional().subList(0, additional).equals(targetGatePos.additionalSymbols.subList(0, additional))) {
                return false;
            }
        }

        return true;
    }

    protected boolean checkAddressLength(StargateAddressDynamic address, StargatePos targetGatePosition) {
        if (targetGatePosition == null || address == null) return false;
        boolean localDial = world.provider.getDimension() == targetGatePosition.dimensionID || StargateDimensionConfig.isGroupEqual(world.provider.getDimension(), targetGatePosition.dimensionID);

        return address.size() < getSymbolType().getMinimalSymbolCountTo(targetGatePosition.getGateSymbolType(), localDial);
    }

    public void attemptClose(StargateClosedReasonEnum reason) {
        if (reason == null) return;
        if (targetGatePos == null || targetGatePos.getTileEntity() == null) {
            closeGate(reason);
            resetRandomIncoming();
            clearDHDSymbols();
            return;
        }
        if ((new StargateClosingEvent(this, targetGatePos.getTileEntity(), isInitiating, reason).post() || new StargateClosingEvent(targetGatePos.getTileEntity(), this, !isInitiating, reason).post()) && reason.equals(StargateClosedReasonEnum.REQUESTED))
            return;

        if (targetGatePos != null) targetGatePos.getTileEntity().closeGate(reason);

        closeGate(reason);
        clearDHDSymbols();
    }

    protected static class ResultTargetValid {
        public final StargateOpenResult result;
        public final boolean targetVaild;

        public ResultTargetValid(StargateOpenResult result, boolean targetVaild) {
            this.result = result;
            this.targetVaild = targetVaild;
        }
    }

    // ------------------------------------------------------------------------
    // Stargate Network

    public abstract SymbolTypeEnum getSymbolType();

    /**
     * Contains instance of {@link StargateAddress} which holds address of this gate.
     */
    protected Map<SymbolTypeEnum, StargateAddress> gateAddressMap = new HashMap<>(3);
    protected Map<SymbolTypeEnum, StargatePos> gatePosMap = new HashMap<>(3);
    protected StargateAddressDynamic dialedAddress = new StargateAddressDynamic(getSymbolType());
    public StargatePos targetGatePos;
    public boolean connectedToGate = false;
    public boolean connectingToGate = false;
    protected StargatePos connectedToGatePos;
    protected boolean isIncoming = false;

    @Nullable
    public StargateAddress getStargateAddress(SymbolTypeEnum symbolType) {
        if (gateAddressMap == null) return null;

        return gateAddressMap.get(symbolType);
    }

    public void setGateAddress(SymbolTypeEnum symbolType, StargateAddress stargateAddress) {
        StargatePos oldPos = gatePosMap.get(symbolType);
        getNetwork().removeStargate(gateAddressMap.get(symbolType));

        StargatePos gatePos = new StargatePos(world.provider.getDimension(), pos, stargateAddress, getSymbolType());
        if (oldPos != null)
            gatePos.setName(oldPos.getName());
        gateAddressMap.put(symbolType, stargateAddress);
        gatePosMap.put(symbolType, gatePos);
        getNetwork().addStargate(stargateAddress, gatePos);

        JSG.debug("Setting gate's address at pos " + gatePos.dimensionID + " : " + gatePos.gatePos.toString() + " to " + stargateAddress);

        markDirty();
    }

    public void renameStargatePos(String newName) {
        for (SymbolTypeEnum s : SymbolTypeEnum.values()) {
            StargatePos p = gatePosMap.get(s);
            p.setName(newName);
            getNetwork().addStargate(gateAddressMap.get(s), p);
            gatePosMap.put(s, p);
        }
        markDirty();
    }

    public void updatePosSymbolType() {
        for (SymbolTypeEnum s : SymbolTypeEnum.values()) {
            StargatePos p = gatePosMap.get(s);
            p.gateSymbolType = getSymbolType();
            getNetwork().addStargate(gateAddressMap.get(s), p);
            gatePosMap.put(s, p);
        }
        markDirty();
    }

    public void refresh() {
        for (SymbolTypeEnum s : SymbolTypeEnum.values()) {
            StargateAddress address = getStargateAddress(s);
            if (address == null) {
                generateAddresses(true);
                address = getStargateAddress(s);
            }
            setGateAddress(s, address);
        }
        updateFacing(world.getBlockState(pos).getValue(JSGProps.FACING_HORIZONTAL), world.getBlockState(pos).getValue(JSGProps.FACING_VERTICAL), true);
        lastPos = pos;
        markDirty();
    }

    public StargateAddressDynamic getDialedAddress() {
        return dialedAddress;
    }

    protected int getMaxChevrons() {
        return 7;
    }

    public int getMaxChevronsForApi() {
        return getMaxChevrons();
    }

    protected boolean stargateWillLock(SymbolInterface symbol, boolean notAddedYet) {
        if ((dialedAddress.size() + (notAddedYet ? 1 : 0)) == getMaxChevrons()) return true;

        if ((dialedAddress.size() + (notAddedYet ? 1 : 0)) >= 7 && symbol.origin()) return true;

        return false;
    }

    protected boolean stargateWillLock(SymbolInterface symbol) {
        return stargateWillLock(symbol, false);
    }

    /**
     * Checks whether the symbol can be added to the address.
     *
     * @param symbol Symbol to be added.
     * @return
     */
    public boolean canAddSymbol(SymbolInterface symbol) {
        return canAddSymbolInternal(symbol) && !(new StargateChevronEngagedEvent(this, symbol, stargateWillLock(symbol)).post());
    }

    protected boolean canAddSymbolInternal(SymbolInterface symbol) {
        if (dialedAddress.contains(symbol)) return false;

        if ((dialedAddress.size()) >= getMaxChevrons()) return false;

        return true;
    }

    /**
     * Adds symbol to address. Called from GateRenderingUpdatePacketToServer.
     *
     * @param symbol Currently added symbol.
     */
    private void addSymbolToAddress(SymbolInterface symbol, int addSymbol) {
        if (!canAddSymbol(symbol)) throw new IllegalStateException("Cannot add that symbol");
        if (addSymbol == 1) dialedAddress.addSymbol(symbol);
        StargateAddressDynamic dialAddr_backup = new StargateAddressDynamic(getSymbolType());
        dialAddr_backup.clear();
        dialAddr_backup.addAll(dialedAddress);
        if (symbol != getSymbolType().getOrigin()) {
            if (dialedAddress.size() >= 6) {
                dialedAddress.addOrigin();

                if (checkAddressAndEnergy(dialedAddress).ok() && !connectedToGate && !Objects.requireNonNull(getNetwork().getStargate(dialedAddress)).getTileEntity().stargateState.incoming()) {
                    connectingToGate = true;
                } else if (!checkAddressAndEnergy(dialedAddress).ok() && connectedToGate) {
                    StargateAbstractBaseTile targetTile = Objects.requireNonNull(getNetwork().getStargate(dialedAddress)).getTileEntity();
                    if (targetTile != null) {
                        targetTile.disconnectGate(true);
                        targetTile.stargateState = EnumStargateState.IDLE;
                        targetTile.markDirty();
                    }
                }

                dialedAddress.clear();
                dialedAddress.addAll(dialAddr_backup);
            }
        }
        markDirty();
    }

    protected void addSymbolToAddress(SymbolInterface symbol) {
        if (tryDialInternal(this, symbol))
            addSymbolToAddress(symbol, 0);
        else {
            if ((dialedAddress.getSize() + 1) >= 5)
                network.checkAndGenerateStargate(dialedAddress);
            addSymbolToAddress(symbol, 1);
        }
    }

    protected void resetTargetIncomingAnimation() {
        if (connectedToGatePos != null) {
            StargateAbstractBaseTile targetGateTile = connectedToGatePos.getTileEntity();
            if (targetGateTile != null) {
                targetGateTile.disconnectGate(true);
                targetGateTile.stargateState = EnumStargateState.IDLE;
                targetGateTile.markDirty();
            }
            connectedToGatePos = null;
            connectedToGate = false;
            connectingToGate = false;
            markDirty();
        }
    }

    protected void doIncomingAnimation(int time, boolean byComputer) {
        doIncomingAnimation(time, byComputer, null);
    }

    protected void doIncomingAnimation(int time, boolean byComputer, SymbolInterface symbol) {
        if (!connectingToGate) return;
        connectingToGate = false;
        markDirty();
        StargateAddressDynamic dialAddr_backup = new StargateAddressDynamic(getSymbolType());
        dialAddr_backup.clear();
        dialAddr_backup.addAll(dialedAddress);
        if (dialedAddress.size() >= 6 && (symbol == null || symbol.origin())) {
            dialedAddress.addOrigin();

            StargatePos targetGatePos = getNetwork().getStargate(dialedAddress);
            if (targetGatePos != null) {
                StargateAbstractBaseTile targetGateTile = targetGatePos.getTileEntity();
                if (targetGateTile != null) {
                    if (checkAddressAndEnergy(dialedAddress).ok() && !connectedToGate) {
                        int size = dialedAddress.size();

                        connectedToGate = true;
                        connectedToGatePos = targetGatePos;
                        markDirty();
                        int period = 400;
                        if (byComputer) {
                            time += 20; // add 20 ticks to time
                            period = ((time / 20) * 1000) / size;
                        }
                        targetGateTile.isIncoming = true;
                        targetGateTile.markDirty();
                        if (targetGateTile instanceof StargateClassicBaseTile && ((StargateClassicBaseTile) targetGateTile).config.getOption(ALLOW_INCOMING.id).getBooleanValue())
                            targetGateTile.incomingWormhole(size, period);
                        else targetGateTile.incomingWormhole(size);
                        targetGateTile.sendSignal(null, "stargate_incoming_wormhole", new Object[]{size});
                        targetGateTile.stargateState = EnumStargateState.INCOMING;
                        targetGateTile.markDirty();
                        targetGateTile.failGate();
                    } else if (!checkAddressAndEnergy(dialedAddress).ok() && connectedToGate) {
                        targetGateTile.disconnectGate(true);
                        targetGateTile.stargateState = EnumStargateState.IDLE;
                        targetGateTile.markDirty();
                        connectedToGatePos = null;
                        markDirty();
                    }
                }
            }

            dialedAddress.clear();
            dialedAddress.addAll(dialAddr_backup);
        }
    }

    public boolean isConnected() {
        return connectedToGate;
    }

    /**
     * Called on receiving gate. Sets renderer's state
     *
     * @param dialedAddressSize - How many symbols are there pressed on the DHD
     */
    public void incomingWormhole(int dialedAddressSize) {
        dialedAddress.clear();
        sendSignal(null, "stargate_incoming_wormhole", new Object[]{dialedAddressSize});
    }

    public void incomingWormhole(int dialedAddressSize, int time) {
        isIncoming = true;
        incomingWormhole(dialedAddressSize);
        markDirty();
    }

    protected int getOpenSoundDelay() {
        return EnumScheduledTask.STARGATE_OPEN_SOUND.waitTicks;
    }

    /**
     * Called from {@link this#attemptOpenDialed()}. The address is valid here.
     * It opens the gate unconditionally. Called only internally.
     *
     * @param targetGatePos Valid {@link StargatePos} pointing to the other Gate.
     * @param isInitiating  True if gate is initializing the connection, false otherwise.
     */
    protected void openGate(StargatePos targetGatePos, boolean isInitiating, boolean noxDialing) {
        setOpenedSince();

        this.isInitiating = isInitiating;
        this.targetGatePos = targetGatePos;
        this.stargateState = EnumStargateState.UNSTABLE;
        this.isNoxDialing = noxDialing;

        ChunkManager.forceChunk(world, new ChunkPos(pos));

        sendRenderingUpdate(StargateRendererActionState.EnumGateAction.OPEN_GATE, 0, noxDialing);

        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_OPEN_SOUND, getOpenSoundDelay()));
        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_HORIZON_LIGHT_BLOCK, EnumScheduledTask.STARGATE_OPEN_SOUND.waitTicks + 19 + getTicksPerHorizonSegment(true)));
        if (!noxDialing)
            addTask(new ScheduledTask(EnumScheduledTask.STARGATE_HORIZON_WIDEN, EnumScheduledTask.STARGATE_OPEN_SOUND.waitTicks + 23 + getTicksPerHorizonSegment(true))); // 1.3s of the sound to the kill
        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_ENGAGE));

        if (isInitiating) {
            EnergyRequiredToOperate energyRequired = getEnergyRequiredToDial(targetGatePos);
            getEnergyStorage().extractEnergy(energyRequired.energyToOpen, false);
            keepAliveEnergyPerTick = energyRequired.keepAlive;
        }

        sendSignal(null, "stargate_open", new Object[]{isInitiating});
        JSG.debug("Gate at " + pos.toString() + " opened!");

        markDirty();
    }

    /**
     * Called either on pressing BRB on open gate or close command from a computer.
     */
    protected void closeGate(StargateClosedReasonEnum reason) {
        stargateState = EnumStargateState.UNSTABLE;
        energySecondsToClose = 0;
        resetOpenedSince();

        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CLOSE, 62));

        playSoundEvent(StargateSoundEventEnum.CLOSE);
        sendRenderingUpdate(StargateRendererActionState.EnumGateAction.CLOSE_GATE, 0, false);
        sendSignal(null, "stargate_close", new Object[]{reason.toString().toLowerCase()});
        JSGSoundHelper.playPositionedSound(world, getGateCenterPos(), SoundPositionedEnum.WORMHOLE_LOOP, false);

        if (isInitiating) {
            horizonFlashTask = null;
            isCurrentlyUnstable = false;
            updateFlashState(false);
        }

        targetGatePos = null;
        connectedToGate = false;
        connectingToGate = false;
        connectedToGatePos = null;
        isIncoming = false;
        JSG.debug("Gate at " + pos.toString() + " closed!");

        markDirty();
    }

    /**
     * Called on the failed dialing.
     */
    protected void dialingFailed(StargateOpenResult reason) {
        if (stargateState == EnumStargateState.DIALING || stargateState == EnumStargateState.DIALING_COMPUTER || stargateState == EnumStargateState.IDLE) {

            // disengage target gate
            if (connectedToGate) {
                if (this instanceof StargateClassicBaseTile)
                    dialedAddress.addSymbol(getSymbolType().getOrigin());

                if (getNetwork().getStargate(dialedAddress) != null)
                    Objects.requireNonNull(getNetwork().getStargate(dialedAddress)).getTileEntity().disconnectGate();
            }

            sendSignal(null, "stargate_failed", new Object[]{reason.toString().toLowerCase()});
            horizonFlashTask = null;

            new StargateDialFailEvent(this, reason).post();

            addFailedTaskAndPlaySound();
            if (stargateState != EnumStargateState.INCOMING)
                stargateState = EnumStargateState.FAILING;

            JSG.debug("Gate at " + pos.toString() + " failed!");

            markDirty();
        }
    }

    protected void addFailedTaskAndPlaySound() {
        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_FAIL, 53));
        playSoundEvent(StargateSoundEventEnum.DIAL_FAILED);
    }

    /**
     * Checks if {@link this#targetGatePos} points at a valid
     * Stargate base block. If no, close the connection.
     *
     * @return True if the connecion is valid.
     */
    protected boolean verifyConnection() {
        if ((targetGatePos == null || targetGatePos.getTileEntity() == null) && !randomIncomingIsActive) {
            closeGate(StargateClosedReasonEnum.CONNECTION_LOST);
            return false;
        }

        return true;
    }


    // ------------------------------------------------------------------------
    // Sounds

    @Nullable
    protected abstract SoundPositionedEnum getPositionedSound(StargateSoundPositionedEnum soundEnum);

    @Nullable
    protected abstract SoundEventEnum getSoundEvent(StargateSoundEventEnum soundEnum);

    public void playPositionedSound(StargateSoundPositionedEnum soundEnum, boolean play) {
        SoundPositionedEnum positionedSound = getPositionedSound(soundEnum);

        if (positionedSound == null)
            throw new IllegalArgumentException("Tried to play " + soundEnum + " on " + getClass().getCanonicalName() + " which apparently doesn't support it.");

        playPositionedSound(positionedSound, play);
    }

    public void playPositionedSound(@Nonnull SoundPositionedEnum positionedSound, boolean play) {
        if (world.isRemote) JSG.proxy.playPositionedSoundClientSide(getGateCenterPos(), positionedSound, play);
        else JSGSoundHelper.playPositionedSound(world, getGateCenterPos(), positionedSound, play);
    }

    public void playPositionedSoundServer(StargateSoundPositionedEnum soundEnum, boolean play) {
        SoundPositionedEnum positionedSound = getPositionedSound(soundEnum);

        if (positionedSound == null)
            throw new IllegalArgumentException("Tried to play " + soundEnum + " on " + getClass().getCanonicalName() + " which apparently doesn't support it.");

        JSGSoundHelper.playPositionedSound(world, getGateCenterPos(), positionedSound, play);
    }

    public void playSoundEvent(StargateSoundEventEnum soundEnum) {
        SoundEventEnum soundEvent = getSoundEvent(soundEnum);

        if (soundEvent == null)
            throw new IllegalArgumentException("Tried to play " + soundEnum + " on " + getClass().getCanonicalName() + " which apparently doesn't support it.");

        if (world.isRemote) JSGSoundHelper.playSoundEventClientSide(world, getGateCenterPos(), soundEvent);
        else JSGSoundHelper.playSoundEvent(world, getGateCenterPos(), soundEvent);
    }

    public void playSoundEvent(SoundEventEnum soundEnum) {
        if (soundEnum == null)
            throw new IllegalArgumentException("Tried to play " + soundEnum + " on " + getClass().getCanonicalName() + " which apparently doesn't support it.");

        if (world.isRemote) JSGSoundHelper.playSoundEventClientSide(world, getGateCenterPos(), soundEnum);
        else JSGSoundHelper.playSoundEvent(world, getGateCenterPos(), soundEnum);
    }

    // ------------------------------------------------------------------------
    // Ticking and loading

    public abstract BlockPos getGateCenterPos();

    protected TargetPoint targetPoint;
    protected EnumFacing facing = EnumFacing.NORTH;
    protected StargateNetwork network;

    public EnumFacing getFacing() {
        return facing;
    }

    protected EnumFacing facingVertical = EnumFacing.SOUTH;

    public EnumFacing getFacingVertical() {
        return facingVertical;
    }

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            lastPos = pos;
            markDirty();
            updateFacing(world.getBlockState(pos).getValue(JSGProps.FACING_HORIZONTAL), world.getBlockState(pos).getValue(JSGProps.FACING_VERTICAL), true);
            network = StargateNetwork.get(world);

            targetPoint = new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);

            generateAddresses(false);

            if (stargateState.engaged()) {
                verifyConnection();
            }
        } else {
            JSGPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.RENDERER_STATE));
            JSGPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.SOUND_UPDATE));
        }
    }

    public void generateAddresses(boolean reset) {
        Random random = new Random(pos.hashCode() * 31L + world.provider.getDimension());

        for (SymbolTypeEnum symbolType : SymbolTypeEnum.values()) {
            StargateAddress address = getStargateAddress(symbolType);

            if (gateAddressMap.get(symbolType) == null || reset) {
                address = new StargateAddress(symbolType);
                address.generate(random);
            }

            this.setGateAddress(symbolType, address);
        }
        lastPos = pos;
        markDirty();
    }

    private boolean addedToNetwork;

    // ------------------------------------------------------------------------
    // Stargate incoming wormhole from unknow

    public int randomIncomingEntities = 0;
    public int randomIncomingAddrSize = 7;
    public int randomIncomingOpenDelay = 0;
    public float randomIncomingState = 0;
    public boolean randomIncomingIsActive = false;

    public void generateIncoming(int entities, int addressSize) {
        generateIncoming(entities, addressSize, 80 + (new Random().nextInt(120)));
    }

    public void generateIncoming(int entities, int addressSize, int delay) {
        if (!isMerged) return;
        if (this instanceof StargateUniverseBaseTile && !((stargateState.idle() || (stargateState.dialing() && !stargateState.dialingComputer())) && !randomIncomingIsActive))
            return;
        else if (!((stargateState.idle() || stargateState.dialing()) && !randomIncomingIsActive)) return;

        if (stargateState.incoming()) return;

        addressSize = Math.max(7, Math.min(9, addressSize));

        this.randomIncomingEntities = entities;
        this.randomIncomingAddrSize = addressSize;
        this.randomIncomingOpenDelay = delay;
        this.randomIncomingState = 0;
        this.randomIncomingIsActive = true;
    }

    public void resetRandomIncoming() {
        this.randomIncomingIsActive = false;
        this.randomIncomingEntities = 0;
        this.randomIncomingState = 0;
        this.randomIncomingAddrSize = 7;
        this.randomIncomingOpenDelay = 0;
    }

    public void activateDHDSymbolBRB() {
    }

    public boolean getForceUnstable() {
        return false;
    }

    public float getSecondsToClose(int energyStored, int morePower) {
        if (keepAliveEnergyPerTick <= 0) {
            return JSGConfig.Stargate.power.instabilitySeconds + 5;
        }
        return (energyStored / (float) (keepAliveEnergyPerTick + morePower + shieldKeepAlive) / 20f);
    }

    @Override
    public void update() {
        // Scheduled tasks
        ScheduledTask.iterate(scheduledTasks, world.getTotalWorldTime());

        if (!world.isRemote) {
            // This cannot be done in onLoad because it makes
            // Stargates invisible to the network sometimes
            if (!addedToNetwork) {
                addedToNetwork = true;
                JSG.ocWrapper.joinWirelessNetwork(this);
                JSG.ocWrapper.joinOrCreateNetwork(this);
                // JSG.info(pos + ": Stargate joined OC network");
            }
            if (targetPoint == null) {
                targetPoint = new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
                markDirty();
            }

            if (stargateState.engaged() && targetGatePos == null && !randomIncomingIsActive) {
                JSG.error("A stargateState indicates the Gate should be open, but targetGatePos is null. This is a bug. Closing gate...");
                attemptClose(StargateClosedReasonEnum.CONNECTION_LOST);
            }

            if (stargateState.engaged() && lastPos != pos) {
                lastPos = pos;
                markDirty();
                JSG.error("A stargateState indicates the Gate should be open, but last pos is not matching current pos! Closing gate...");
                attemptClose(StargateClosedReasonEnum.CONNECTION_LOST);
                refresh();
            }

            updatePassedEntities();

            // Event horizon teleportation
            if (stargateState.initiating()) {
                eventHorizon.scheduleTeleportation(targetGatePos, true);
            } else if (stargateState.engaged()) {
                eventHorizon.scheduleTeleportation(targetGatePos, false);
            }

            // Autoclose
            if (world.getTotalWorldTime() % 20 == 0 && stargateState == EnumStargateState.ENGAGED && JSGConfig.Stargate.autoClose.autocloseEnabled && shouldAutoclose()) {
                if (targetGatePos != null)
                    targetGatePos.getTileEntity().attemptClose(StargateClosedReasonEnum.AUTOCLOSE);
                else attemptClose(StargateClosedReasonEnum.AUTOCLOSE);
            }

            if (horizonFlashTask != null && horizonFlashTask.isActive()) {
                horizonFlashTask.update(world.getTotalWorldTime());
            }

            // Anti idle target (AIT)
            if (targetGatePos != null && world.getTotalWorldTime() % 80 == 0) {
                if (this.stargateState.engaged()) {
                    StargateAbstractBaseTile tile = targetGatePos.getTileEntity();
                    if (tile != null) {
                        if (tile.stargateState.idle()) {
                            this.attemptClose(StargateClosedReasonEnum.CONNECTION_LOST);
                        }
                    }
                }
            }


            // Event horizon killing
            kawooshDestruction();

            /*
             * Draw power (engaged)
             *
             * If initiating
             * 	True: Extract energy each tick
             * 	False: Update the source gate about consumed energy each second
             */
            if (stargateState.initiating()) {
                //if (targetGatePos == null)
                //   targetGatePos = getNetwork().getStargate(this.getStargateAddress(SymbolTypeEnum.MILKYWAY));
                if (targetGatePos == null) {
                    targetGatePos = getNetwork().getStargate(this.getStargateAddress(SymbolTypeEnum.MILKYWAY));
                    attemptClose(StargateClosedReasonEnum.CONNECTION_LOST);
                }

                // Max Open Time
                int morePower = doTimeLimitFunc();
                energySecondsToClose = getSecondsToClose(getEnergyStorage().getEnergyStored(), morePower);

                if (energySecondsToClose >= 1) {

                    /*
                     * If energy can sustain connection for less than JSGConfig.powerConfig.instabilitySeconds seconds
                     * Start flickering
                     *
                     * 2020-04-25: changed the below to check if the gate is being sufficiently externally powered and, if so,
                     * do not start flickering even if the internal power isn't enough.
                     *
                     * 2023-06-29: added tile config bypass for this
                     */

                    boolean forceUnstable = getForceUnstable();

                    // Horizon becomes unstable
                    if (horizonFlashTask == null && (forceUnstable || (energySecondsToClose < JSGConfig.Stargate.power.instabilitySeconds && energyTransferedLastTick < 0))) {
                        resetFlashingSequence();
                        shouldBeUnstable = true;

                        setHorizonFlashTask(new ScheduledTask(EnumScheduledTask.HORIZON_FLASH, (int) (Math.random() * 40) + 5));
                    }

                    // Horizon becomes stable
                    if (horizonFlashTask != null && (!forceUnstable && (energySecondsToClose > JSGConfig.Stargate.power.instabilitySeconds || energyTransferedLastTick >= 0))) {
                        horizonFlashTask = null;
                        isCurrentlyUnstable = false;
                        shouldBeUnstable = false;

                        updateFlashState(false);
                    }

                    getEnergyStorage().extractEnergy(keepAliveEnergyPerTick + morePower + shieldKeepAlive, false);

                    markDirty();
                } else
                    attemptClose(StargateClosedReasonEnum.OUT_OF_POWER);
            } else {
                if (shieldKeepAlive > 0) getEnergyStorage().extractEnergy(shieldKeepAlive, false);
            }

            energyTransferedLastTick = getEnergyStorage().getEnergyStored() - energyStoredLastTick;
            energyStoredLastTick = getEnergyStorage().getEnergyStored();
        }
    }

    protected void kawooshDestruction() {
        // Event horizon killing
        if (horizonKilling) {
            List<Entity> entities = new ArrayList<>();
            List<BlockPos> blocks = new ArrayList<>();

            // Get all blocks and entities inside the kawoosh
            for (int i = 0; i < horizonSegments; i++) {
                if (localKillingBoxes.size() > i) {
                    JSGAxisAlignedBB gBox = localKillingBoxes.get(i).offset(pos);

                    entities.addAll(world.getEntitiesWithinAABB(Entity.class, gBox));

                    //					JSG.info(new AxisAlignedBB((int)Math.floor(gBox.minX), (int)Math.floor(gBox.minY+1), (int)Math.floor(gBox.minZ), (int)Math.ceil(gBox.maxX-1), (int)Math.ceil(gBox.maxY-1), (int)Math.ceil(gBox.maxZ-1)).toString());
                    for (BlockPos bPos : BlockPos.getAllInBox((int) Math.floor(gBox.minX), (int) Math.floor(gBox.minY), (int) Math.floor(gBox.minZ), (int) Math.ceil(gBox.maxX) - 1, (int) Math.ceil(gBox.maxY) - 1, (int) Math.ceil(gBox.maxZ) - 1))
                        blocks.add(bPos);
                }
            }

            // Get all entities inside the gate
            for (JSGAxisAlignedBB lBox : localInnerEntityBoxes)
                entities.addAll(world.getEntitiesWithinAABB(Entity.class, lBox.offset(pos)));

            // Get all blocks inside the gate
            for (JSGAxisAlignedBB lBox : localInnerBlockBoxes) {
                JSGAxisAlignedBB gBox = lBox.offset(pos);

                for (BlockPos bPos : BlockPos.getAllInBox((int) gBox.minX, (int) gBox.minY, (int) gBox.minZ, (int) gBox.maxX - 1, (int) gBox.maxY - 1, (int) gBox.maxZ - 1)) {
                    // If not snow layer
                    if (!DHDBlock.SNOW_MATCHER.apply(world.getBlockState(bPos))) {
                        blocks.add(bPos);
                    }
                }
            }

            // Kill them
            for (Entity entity : entities) {
                eventHorizon.horizonKill(entity);
                JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.STARGATE_VAPORIZE_BLOCK_PARTICLES, new StargateVaporizeBlockParticlesRequest(entity.getPosition())), targetPoint);
            }

            // Vaporize them
            for (BlockPos dPos : blocks) {
                if (!dPos.equals(getGateCenterPos())) {
                    IBlockState state = world.getBlockState(dPos);
                    if (!world.isAirBlock(dPos) && state.getBlockHardness(world, dPos) >= 0.0f && JSGConfigUtil.canKawooshDestroyBlock(state)) {
                        world.setBlockToAir(dPos);
                        JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.STARGATE_VAPORIZE_BLOCK_PARTICLES, new StargateVaporizeBlockParticlesRequest(dPos)), targetPoint);
                    }
                }
            }
        }
    }

    public abstract EnumSet<BiomeOverlayEnum> getSupportedOverlays();

    public BiomeOverlayEnum getBiomeOverlayWithOverride(boolean override) {
        return BiomeOverlayEnum.updateBiomeOverlay(world, getMergeHelper().getTopBlock().add(pos), getSupportedOverlays());
    }

    /**
     * Method for closing the gate using Autoclose mechanism.
     *
     * @return {@code True} if the gate should be closed, false otherwise.
     */
    protected boolean shouldAutoclose() {
        if (!randomIncomingIsActive && targetGatePos != null) return getAutoCloseManager().shouldClose(targetGatePos);
        else return !randomIncomingIsActive;
    }

    protected void resetOpenedSince() {
        openedSince = -1;
        markDirty();
    }

    protected void setOpenedSince() {
        openedSince = world.getTotalWorldTime();
        markDirty();
    }

    public void clearDHDSymbols() {
    }

    protected int doTimeLimitFunc() {
        int morePower = 0;
        getOpenedSeconds();
        int configPower = JSGConfig.Stargate.openLimit.maxOpenedPowerDrawAfterLimit;
        int maxSeconds = JSGConfig.Stargate.openLimit.maxOpenedSeconds;
        StargateTimeLimitModeEnum limitMode = JSGConfig.Stargate.openLimit.maxOpenedWhat;

        if (this instanceof StargateClassicBaseTile) {
            StargateClassicBaseTile casted = ((StargateClassicBaseTile) this);
            limitMode = StargateTimeLimitModeEnum.byId(casted.getConfig().getOption(StargateClassicBaseTile.ConfigOptions.TIME_LIMIT_MODE.id).getEnumValue().getIntValue());
            configPower = casted.getConfig().getOption(StargateClassicBaseTile.ConfigOptions.TIME_LIMIT_POWER.id).getIntValue();
            maxSeconds = casted.getConfig().getOption(StargateClassicBaseTile.ConfigOptions.TIME_LIMIT_TIME.id).getIntValue();
        }
        boolean enabled = (limitMode != StargateTimeLimitModeEnum.DISABLED);

        if (enabled && (getOpenedSeconds() >= maxSeconds)) {
            if (limitMode == StargateTimeLimitModeEnum.CLOSE_GATE) {
                attemptClose(StargateClosedReasonEnum.CONNECTION_LOST);
                clearDHDSymbols();
            } else
                morePower = (int) (((double) getOpenedSeconds() / maxSeconds) * configPower);
        }
        return morePower;
    }

    public long getOpenedSeconds() {
        if (openedSince <= 0) return -1;
        return (world.getTotalWorldTime() - openedSince) / 20;
    }

    public String getOpenedSecondsToDisplayAsMinutes() {
        long openedSeconds = getOpenedSeconds();
        if (openedSeconds < 1) return "Closed!";
        int minutes = ((int) Math.floor((double) openedSeconds / 60));
        int seconds = ((int) (openedSeconds - (60 * minutes)));
        String secondsString = ((seconds < 10) ? "0" + seconds : "" + seconds);
        return minutes + ":" + secondsString + "min";
    }

    protected void extractEnergyByShield(int keepAlive) {
        this.shieldKeepAlive = keepAlive;
    }

    @Override
    public void onChunkUnload() {
        if (node != null) node.remove();

        JSG.ocWrapper.leaveWirelessNetwork(this);
    }

    @Override
    public void invalidate() {
        if (node != null) node.remove();

        JSG.ocWrapper.leaveWirelessNetwork(this);

        super.invalidate();
    }

    @Override
    public void rotate(Rotation rotation) {
        IBlockState state = world.getBlockState(pos);

        EnumFacing facing = state.getValue(JSGProps.FACING_HORIZONTAL);
        world.setBlockState(pos, state.withProperty(JSGProps.FACING_HORIZONTAL, rotation.rotate(facing)));
    }

    // ------------------------------------------------------------------------
    // Killing and block vaporizing

    /**
     * Gets full {@link AxisAlignedBB} of the killing area.
     *
     * @param server Calling side.
     * @return Approximate kawoosh size.
     */
    protected abstract JSGAxisAlignedBB getHorizonKillingBox(boolean server);

    /**
     * How many segments should the exclusion zone have.
     *
     * @param server Calling side.
     * @return Count of subsegments of the killing box.
     */
    protected abstract int getHorizonSegmentCount(boolean server);

    /**
     * The event horizon in the gate also should kill
     * and vaporize everything
     *
     * @param server Calling side.
     * @return List of {@link AxisAlignedBB} for the inner gate area.
     */
    protected abstract List<JSGAxisAlignedBB> getGateVaporizingBoxes(boolean server);

    /**
     * How many ticks should the {@link StargateAbstractBaseTile} wait to perform
     * next update to the size of the killing box.
     *
     * @param server Calling side
     */
    protected int getTicksPerHorizonSegment(boolean server) {
        return 12 / getHorizonSegmentCount(server);
    }

    /**
     * Contains all the subboxes to be activated with the kawoosh.
     * On the server needs to be offsetted by the {@link TileEntity#getPos()}
     */
    protected List<JSGAxisAlignedBB> localKillingBoxes;

    public List<JSGAxisAlignedBB> getLocalKillingBoxes() {
        return localKillingBoxes;
    }

    /**
     * Contains all boxes of the inner part of the gate.
     * Full blocks. Used for destroying blocks.
     * On the server needs to be offsetted by the {@link TileEntity#getPos()}
     */
    protected List<JSGAxisAlignedBB> localInnerBlockBoxes;

    public List<JSGAxisAlignedBB> getLocalInnerBlockBoxes() {
        return localInnerBlockBoxes;
    }

    /**
     * Contains all boxes of the inner part of the gate.
     * Not full blocks. Used for entity killing.
     * On the server needs to be offsetted by the {@link TileEntity#getPos()}
     */
    protected List<JSGAxisAlignedBB> localInnerEntityBoxes;

    public List<JSGAxisAlignedBB> getLocalInnerEntityBoxes() {
        return localInnerEntityBoxes;
    }

    protected boolean horizonKilling = false;
    protected int horizonSegments = 0;

    // ------------------------------------------------------------------------
    // Rendering

    private AxisAlignedBB renderBoundingBox = TileEntity.INFINITE_EXTENT_AABB;

    public JSGAxisAlignedBB getRenderBoundingBoxForDisplay() {
        return getRenderBoundingBoxRaw().rotate(facingVertical).rotate(facing).offset(0.5, 0, 0.5);
    }

    protected StargateAbstractRendererStateBuilder getRendererStateServer() {
        return StargateAbstractRendererState.builder().setStargateState(stargateState);
    }

    StargateAbstractRendererState rendererStateClient;

    protected abstract StargateAbstractRendererState createRendererStateClient();

    public StargateAbstractRendererState getRendererStateClient() {
        return rendererStateClient;
    }

    protected void setRendererStateClient(StargateAbstractRendererState rendererState) {
        this.rendererStateClient = rendererState;

        JSGSoundHelper.playPositionedSound(world, getGateCenterPos(), SoundPositionedEnum.WORMHOLE_LOOP, rendererState.doEventHorizonRender);
        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_LIGHTING_UPDATE_CLIENT, 10));
    }

    protected abstract JSGAxisAlignedBB getRenderBoundingBoxRaw();

    public void updateFacing(EnumFacing facing, EnumFacing verticalFacing, boolean server) {
        this.facing = facing;
        this.facingVertical = verticalFacing;
        this.eventHorizon = new EventHorizon(world, pos, getGateCenterPos(), facing, verticalFacing, getHorizonTeleportBox(server));
        this.renderBoundingBox = getRenderBoundingBoxRaw().rotate(verticalFacing).rotate(facing).offset(0.5, 0, 0.5).offset(pos);

        JSGAxisAlignedBB kBox = getHorizonKillingBox(server);
        double width = kBox.maxZ - kBox.minZ;
        width /= getHorizonSegmentCount(server);

        localKillingBoxes = new ArrayList<>(getHorizonSegmentCount(server));
        for (int i = 0; i < getHorizonSegmentCount(server); i++) {
            JSGAxisAlignedBB box = new JSGAxisAlignedBB(kBox.minX, kBox.minY, kBox.minZ + width * i, kBox.maxX, kBox.maxY, kBox.minZ + width * (i + 1));
            box = box.rotate(verticalFacing).rotate(facing).offset(0.5, 0, 0.5);

            localKillingBoxes.add(box);
        }

        localInnerBlockBoxes = new ArrayList<>();
        localInnerEntityBoxes = new ArrayList<>();
        for (JSGAxisAlignedBB lBox : getGateVaporizingBoxes(server)) {
            localInnerBlockBoxes.add(lBox.rotate(verticalFacing).rotate(facing).offset(0.5, 0, 0.5));
            localInnerEntityBoxes.add(lBox.grow(0, 0, -0.25).rotate(verticalFacing).rotate(facing).offset(0.5, 0, 0.5));
        }
    }

    @Override
    public boolean shouldRefresh(@Nonnull World world, @Nonnull BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Nonnull
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return renderBoundingBox;
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 65536;
    }


    // ------------------------------------------------------------------------
    // Merging

    private boolean isMerged;

    public final boolean isMerged() {
        return isMerged;
    }

    /**
     * @return Appropriate merge helper
     */
    public abstract StargateAbstractMergeHelper getMergeHelper();

    /**
     * Checks gate's merge state
     *
     * @param shouldBeMerged - True if gate's multiblock structure is valid
     * @param facing         Facing of the base block.
     */
    public final void updateMergeState(boolean shouldBeMerged, EnumFacing facing, EnumFacing facingVertical) {
        if (!shouldBeMerged) {
            if (isMerged) onGateBroken();

            if (stargateState.engaged()) {
                targetGatePos.getTileEntity().closeGate(StargateClosedReasonEnum.CONNECTION_LOST);
            }
        } else {
            tryTriggerRangedAdvancement(this, JSGAdvancementsUtil.EnumAdvancementType.GATE_MERGE);
            onGateMerged();
        }

        if (this.isMerged == shouldBeMerged) {
            if (shouldBeMerged) {
                getMergeHelper().updateMembersBasePos(world, pos, facing, facingVertical);
            }
            return;
        }

        this.isMerged = shouldBeMerged;
        IBlockState actualState = world.getBlockState(pos);

        // When the block is destroyed, there will be air in this place and we cannot set its block state
        if (getMergeHelper().matchBase(actualState)) {
            world.setBlockState(pos, actualState.withProperty(JSGProps.RENDER_BLOCK, !shouldBeMerged), 2);
        }

        getMergeHelper().updateMembersMergeStatus(world, pos, facing, facingVertical, shouldBeMerged);

        markDirty();
    }

    // ------------------------------------------------------------------------
    // AutoClose

    // Last entities that were going through the gate
    // This prevents to kill entity on destination by "Wrong Side Kill" method
    public final HashMap<Integer, Entity> entitiesPassedLast = new HashMap<>();

    public void updatePassedEntities() {
        if (!stargateState.engaged()) {
            if (entitiesPassedLast.size() > 0)
                entitiesPassedLast.clear();
            markDirty();
            return;
        }

        AxisAlignedBB scanBox = new AxisAlignedBB(getGateCenterPos().add(new Vec3i(-5, -5, -5)), getGateCenterPos().add(new Vec3i(5, 5, 5)));
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, scanBox);
        List<Integer> ids = new ArrayList<Integer>() {{
            for (Entity e : entities)
                add(e.getEntityId());
        }};
        HashMap<Integer, Entity> clonedEntitiesLast = new HashMap<Integer, Entity>() {{
            putAll(entitiesPassedLast);
        }};
        for (int i : clonedEntitiesLast.keySet()) {
            if (!ids.contains(i))
                entitiesPassedLast.remove(i);
        }
        markDirty();
    }

    public final void entityPassing(Entity entity, boolean inbound) {
        if (inbound)
            entitiesPassedLast.put(entity.getEntityId(), entity);

        boolean isPlayer = entity instanceof EntityPlayerMP;

        if (isPlayer)
            getAutoCloseManager().playerPassing();
        markDirty();

        sendSignal(null, "stargate_traveler", new Object[]{inbound, isPlayer, entity.getClass().getSimpleName()});
    }


    // -----------------------------------------------------------------
    // Horizon flashing
    private ScheduledTask horizonFlashTask;

    private void setHorizonFlashTask(ScheduledTask horizonFlashTask) {
        horizonFlashTask.setExecutor(this);
        horizonFlashTask.setTaskCreated(world.getTotalWorldTime());

        this.horizonFlashTask = horizonFlashTask;
        markDirty();
    }

    private int flashIndex = 0;
    public boolean isCurrentlyUnstable = false;

    /**
     * Defines if gate should be unstable
     * - this variable is used for OC methods
     */
    public boolean shouldBeUnstable = false;

    private void resetFlashingSequence() {
        flashIndex = 0;
        isCurrentlyUnstable = false;
    }

    private void updateFlashState(boolean flash) {
        JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.FLASH_STATE, new StargateFlashState(isCurrentlyUnstable)), targetPoint);

        if (targetGatePos != null) {
            BlockPos tPos = targetGatePos.gatePos;
            JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(tPos, StateTypeEnum.FLASH_STATE, new StargateFlashState(isCurrentlyUnstable)), new TargetPoint(targetGatePos.dimensionID, tPos.getX(), tPos.getY(), tPos.getZ(), 512));
        }
    }

    // Linking (sg generator)
    public abstract void setLinkedDHD(BlockPos dhdPos, int linkId);

    public boolean isSpinning() {
        return false;
    }


    // ------------------------------------------------------------------------
    // States

    @Override
    public State getState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_STATE:
                return getRendererStateServer().build();

            case SOUND_UPDATE:
                StateSoundPositionedUpdate state = new StateSoundPositionedUpdate();
                state.add(SoundPositionedEnum.WORMHOLE_LOOP, getStargateState().engaged());
                state.add(getPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL), isSpinning());
                return state;

            default:
                return null;
        }
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_STATE:
                return createRendererStateClient();

            case SOUND_UPDATE:
                return new StateSoundPositionedUpdate();

            case RENDERER_UPDATE:
                return new StargateRendererActionState();

            case STARGATE_VAPORIZE_BLOCK_PARTICLES:
                return new StargateVaporizeBlockParticlesRequest();

            case FLASH_STATE:
                return new StargateFlashState();

            default:
                return null;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {
            case RENDERER_STATE:
                EnumFacing facing = world.getBlockState(pos).getValue(JSGProps.FACING_HORIZONTAL);
                EnumFacing facingVertical = world.getBlockState(pos).getValue(JSGProps.FACING_VERTICAL);

                setRendererStateClient(((StargateAbstractRendererState) state).initClient(pos, facing, facingVertical, getBiomeOverlayWithOverride(false)));

                updateFacing(facing, facingVertical, false);

                break;

            case SOUND_UPDATE:
                StateSoundPositionedUpdate s = (StateSoundPositionedUpdate) state;
                for (Map.Entry<Integer, Boolean> e : s.soundMap.entrySet()) {
                    SoundPositionedEnum sound = SoundPositionedEnum.valueOf(e.getKey());
                    if (sound == null) continue;
                    playPositionedSound(sound, e.getValue());
                }
                break;

            case RENDERER_UPDATE:
                if (getRendererStateClient() == null) break;
                switch (((StargateRendererActionState) state).action) {
                    case OPEN_GATE:
                        boolean noxDialing = ((StargateRendererActionState) state).modifyFinal;

                        getRendererStateClient().horizonSegments = 0;
                        getRendererStateClient().openGate(world.getTotalWorldTime(), noxDialing);
                        break;

                    case CLOSE_GATE:
                        getRendererStateClient().closeGate(world.getTotalWorldTime());
                        break;

                    case STARGATE_HORIZON_WIDEN:
                        getRendererStateClient().horizonSegments++;
                        break;

                    case STARGATE_HORIZON_SHRINK:
                        getRendererStateClient().horizonSegments--;
                        break;

                    default:
                        break;
                }

                break;

            case STARGATE_VAPORIZE_BLOCK_PARTICLES:
                BlockPos b = ((StargateVaporizeBlockParticlesRequest) state).block;

                for (int i = 0; i < 20; i++) {
                    Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleWhiteSmoke(world, b.getX() + (Math.random() - 0.5), b.getY(), b.getZ() + (Math.random() - 0.5), 0, 0, false));
                }

                break;

            case FLASH_STATE:
                if (getRendererStateClient() != null)
                    getRendererStateClient().horizonUnstable = ((StargateFlashState) state).flash;

                break;

            default:
                break;
        }
    }

    // ------------------------------------------------------------------------
    // Scheduled tasks

    /**
     * List of scheduled tasks to be performed on {@link ITickable#update()}.
     */
    protected List<ScheduledTask> scheduledTasks = new ArrayList<>();

    protected ScheduledTask lastSpinFinished;

    @Override
    public void addTask(ScheduledTask scheduledTask) {
        scheduledTask.setExecutor(this);
        scheduledTask.setTaskCreated(world.getTotalWorldTime());

        scheduledTasks.add(scheduledTask);
        markDirty();
    }

    public void removeTask(ScheduledTask scheduledTask) {
        scheduledTasks.remove(scheduledTask);
        markDirty();
    }

    public int getOriginId() {
        return OriginsLoader.DEFAULT_ORIGIN_ID;
    }

    public void setOriginId(NBTTagCompound compound) {
        compound.setInteger("originId", getOriginId());
    }

    public ItemStack getAddressPage(SymbolTypeEnum symbolType, ItemStack defaultStack, boolean hasUpgrade, boolean hideOrigin, boolean hideLastSymbol) {
        ItemStack stack = defaultStack;

        if (stack.getItem() == JSGItems.UNIVERSE_DIALER) {
            NBTTagList saved = Objects.requireNonNull(stack.getTagCompound()).getTagList("saved", Constants.NBT.TAG_COMPOUND);
            NBTTagCompound compound = gateAddressMap.get(symbolType).serializeNBT();
            compound.setBoolean("hasUpgrade", hasUpgrade);
            setOriginId(compound);
            saved.appendTag(compound);
        } else {
            JSG.debug("Giving Notebook page of address " + symbolType);

            NBTTagCompound compound = PageNotebookItem.getCompoundFromAddress(gateAddressMap.get(symbolType), hasUpgrade, hideLastSymbol, hideOrigin, PageNotebookItem.getRegistryPathFromWorld(world, pos), getOriginId());

            stack = new ItemStack(JSGItems.PAGE_NOTEBOOK_ITEM, 1, 1);
            stack.setTagCompound(compound);
        }
        return stack;
    }

    @Override
    public void executeTask(EnumScheduledTask scheduledTask, NBTTagCompound customData) {
        switch (scheduledTask) {
            case STARGATE_OPEN_SOUND:
                if (isNoxDialing)
                    playSoundEvent(StargateSoundEventEnum.OPEN_NOX);
                else
                    playSoundEvent(StargateSoundEventEnum.OPEN);
                break;

            case STARGATE_HORIZON_LIGHT_BLOCK:
                world.setBlockState(getGateCenterPos(), JSGBlocks.INVISIBLE_BLOCK.getDefaultState().withProperty(JSGProps.HAS_COLLISIONS, false));

                break;

            case STARGATE_HORIZON_WIDEN:
                if (!horizonKilling) horizonKilling = true;

                horizonSegments++;
                JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.RENDERER_UPDATE, StargateRendererActionState.STARGATE_HORIZON_WIDEN_ACTION), targetPoint);

                if (horizonSegments < getHorizonSegmentCount(true))
                    addTask(new ScheduledTask(EnumScheduledTask.STARGATE_HORIZON_WIDEN, getTicksPerHorizonSegment(true)));
                else
                    addTask(new ScheduledTask(EnumScheduledTask.STARGATE_HORIZON_SHRINK, getTicksPerHorizonSegment(true) + 12));

                break;

            case STARGATE_HORIZON_SHRINK:
                horizonSegments--;
                JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.RENDERER_UPDATE, StargateRendererActionState.STARGATE_HORIZON_SHRINK_ACTION), targetPoint);

                if (horizonSegments > 0)
                    addTask(new ScheduledTask(EnumScheduledTask.STARGATE_HORIZON_SHRINK, getTicksPerHorizonSegment(true) + 1));
                else horizonKilling = false;

                markDirty();

                break;

            case STARGATE_CLOSE:
                world.setBlockToAir(getGateCenterPos());
                disconnectGate();
                break;

            case STARGATE_FAIL:
                failGate();
                break;

            case STARGATE_ENGAGE:
                // Gate destroyed mid-process
                if (verifyConnection()) {
                    engageGate();
                }

                break;

            case STARGATE_LIGHTING_UPDATE_CLIENT:
                world.notifyLightSet(getGateCenterPos());
                world.checkLightFor(EnumSkyBlock.BLOCK, getGateCenterPos());

                break;

            case HORIZON_FLASH:
                isCurrentlyUnstable ^= true;

                if (isCurrentlyUnstable) {
                    flashIndex++;

                    if (flashIndex == 1 && targetGatePos != null) {
                        tryTriggerRangedAdvancement(this, JSGAdvancementsUtil.EnumAdvancementType.GATE_FLICKER);
                        JSGSoundHelper.playSoundEvent(world, getGateCenterPos(), SoundEventEnum.WORMHOLE_FLICKER);
                        JSGSoundHelper.playSoundEvent(targetGatePos.getWorld(), targetGatePos.getTileEntity().getGateCenterPos(), SoundEventEnum.WORMHOLE_FLICKER);
                    }

                    // Schedule change into stable state
                    setHorizonFlashTask(new ScheduledTask(EnumScheduledTask.HORIZON_FLASH, (int) (Math.random() * 3) + 3));
                } else {
                    if (flashIndex == 1)
                        // Schedule second flash
                        setHorizonFlashTask(new ScheduledTask(EnumScheduledTask.HORIZON_FLASH, (int) (Math.random() * 4) + 1));

                    else {
                        // Schedule next flash sequence
                        float mul = energySecondsToClose / (float) JSGConfig.Stargate.power.instabilitySeconds;
                        if (getForceUnstable()) {
                            mul = 0.5f;
                        }
                        int min = (int) (15 * mul);
                        int off = (int) (20 * mul);
                        setHorizonFlashTask(new ScheduledTask(EnumScheduledTask.HORIZON_FLASH, min + (int) (Math.random() * off)));

                        resetFlashingSequence();
                    }
                }

                updateFlashState(isCurrentlyUnstable);

                markDirty();
                break;

            default:
                break;
        }
    }


    // -----------------------------------------------------------------
    // Power system

    private int keepAliveEnergyPerTick = 0;
    private int shieldKeepAlive = 0;
    private int energyStoredLastTick = 0;
    protected int energyTransferedLastTick = 0;
    protected float energySecondsToClose = 0;
    public long openedSince;

    public int getEnergyTransferedLastTick() {
        return energyTransferedLastTick;
    }

    public float getEnergySecondsToClose() {
        return energySecondsToClose;
    }

    protected abstract SmallEnergyStorage getEnergyStorage();

    public SmallEnergyStorage getEnergyStorageForApi() {
        return getEnergyStorage();
    }

    public int getEnergyStored() {
        return getEnergyStorage().getEnergyStored();
    }

    public EnergyRequiredToOperate getEnergyRequiredToDialForApi(StargatePos targetGatePos) {
        return getEnergyRequiredToDial(targetGatePos);
    }

    protected EnergyRequiredToOperate getEnergyRequiredToDial(StargatePos targetGatePos) {
        BlockPos sPos = pos;
        BlockPos tPos = targetGatePos.gatePos;

        int sourceDim = world.provider.getDimension();
        int targetDim = targetGatePos.getWorld().provider.getDimension();

        if (sourceDim == DimensionType.OVERWORLD.getId() && targetDim == DimensionType.NETHER.getId())
            tPos = new BlockPos(tPos.getX() * 8, tPos.getY(), tPos.getZ() * 8);
        else if (sourceDim == DimensionType.NETHER.getId() && targetDim == DimensionType.OVERWORLD.getId())
            sPos = new BlockPos(sPos.getX() * 8, sPos.getY(), sPos.getZ() * 8);

        double distance = (int) sPos.getDistance(tPos.getX(), tPos.getY(), tPos.getZ());

        if (distance < 5000) distance *= 0.8;
        else distance = 5000 * Math.log10(distance) / Math.log10(5000);

        EnergyRequiredToOperate energyRequired = EnergyRequiredToOperate.stargate();
        energyRequired = energyRequired.mul(distance).add(StargateDimensionConfig.getCost(sourceDim, targetDim));

        if (dialedAddress.size() == 9)
            energyRequired.mul(JSGConfig.Stargate.power.nineSymbolAddressMul);
        if (dialedAddress.size() == 8)
            energyRequired.mul(JSGConfig.Stargate.power.eightSymbolAddressMul);

        //JSG.logger.info(String.format("Energy required to dial [distance=%,d, from=%s, to=%s] = %,d / keepAlive: %,d/t, stored=%,d", Math.round(distance), sourceDim, targetDim, energyRequired.energyToOpen, energyRequired.keepAlive, getEnergyStorage().getEnergyStored()));

        return energyRequired;
    }

    /**
     * Checks is gate has sufficient power to dial across specified distance and dimension
     * It also sets energy draw for (possibly) outgoing wormhole
     */
    public boolean hasEnergyToDial(StargatePos targetGatePos) {
        EnergyRequiredToOperate energyRequired = getEnergyRequiredToDial(targetGatePos);

        return getEnergyStorage().getEnergyStored() >= energyRequired.energyToOpen;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityEnergy.ENERGY) || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(getEnergyStorage());
        }

        return super.getCapability(capability, facing);
    }


    // ------------------------------------------------------------------------
    // NBT
    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        for (StargateAddress stargateAddress : gateAddressMap.values()) {
            // we don't want to copy address of gate to another gate -_-
            if (stargateAddress == null) continue;
            StargatePos pos = gatePosMap.get(stargateAddress.getSymbolType());
            if (pos == null) continue;
            compound.setTag("address_" + stargateAddress.getSymbolType(), stargateAddress.serializeNBT());
            compound.setTag("gatePos_" + stargateAddress.getSymbolType(), pos.serializeNBT());
        }

        compound.setTag("dialedAddress", dialedAddress.serializeNBT());

        if (targetGatePos != null) compound.setTag("targetGatePos", targetGatePos.serializeNBT());
        if (connectedToGatePos != null) compound.setTag("connectedToGatePos", connectedToGatePos.serializeNBT());

        compound.setBoolean("connectingToGate", connectingToGate);
        compound.setBoolean("connectedToGate", connectedToGate);

        compound.setBoolean("isMerged", isMerged);
        compound.setTag("autoCloseManager", getAutoCloseManager().serializeNBT());

        compound.setInteger("keepAliveCostPerTick", keepAliveEnergyPerTick);

        if (stargateState != null) compound.setInteger("stargateState", stargateState.id);

        compound.setTag("scheduledTasks", ScheduledTask.serializeList(scheduledTasks));

        compound.setTag("energyStorage", getEnergyStorage().serializeNBT());

        if (node != null) {
            NBTTagCompound nodeCompound = new NBTTagCompound();
            node.save(nodeCompound);

            compound.setTag("node", nodeCompound);
        }

        compound.setBoolean("horizonKilling", horizonKilling);
        compound.setInteger("horizonSegments", horizonSegments);
        compound.setLong("openedSince", openedSince);
        compound.setInteger("facingVertical", (facingVertical == EnumFacing.UP ? 2 : facingVertical == EnumFacing.DOWN ? 1 : 0));

        compound.setLong("lastPos", (lastPos != null ? lastPos.toLong() : pos.toLong()));

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        for (SymbolTypeEnum symbolType : SymbolTypeEnum.values()) {
            if (compound.hasKey("address_" + symbolType))
                gateAddressMap.put(symbolType, new StargateAddress(compound.getCompoundTag("address_" + symbolType)));
            if (compound.hasKey("gatePos_" + symbolType))
                gatePosMap.put(symbolType, new StargatePos(symbolType, compound.getCompoundTag("gatePos_" + symbolType)));
        }

        dialedAddress.deserializeNBT(compound.getCompoundTag("dialedAddress"));

        if (compound.hasKey("targetGatePos"))
            targetGatePos = new StargatePos(getSymbolType(), compound.getCompoundTag("targetGatePos"));
        if (compound.hasKey("connectedToGatePos"))
            connectedToGatePos = new StargatePos(getSymbolType(), compound.getCompoundTag("connectedToGatePos"));

        connectingToGate = compound.getBoolean("connectingToGate");
        connectedToGate = compound.getBoolean("connectedToGate");

        isMerged = compound.getBoolean("isMerged");
        getAutoCloseManager().deserializeNBT(compound.getCompoundTag("autoCloseManager"));

        try {
            ScheduledTask.deserializeList(compound.getCompoundTag("scheduledTasks"), scheduledTasks, this);
        } catch (NullPointerException | IndexOutOfBoundsException | ClassCastException e) {
            JSG.warn("Exception at reading NBT");
            JSG.warn("If loading world used with previous version and nothing game-breaking doesn't happen, please ignore it");
            JSG.warn("Stacktrace: ", e);
        }

        getEnergyStorage().deserializeNBT(compound.getCompoundTag("energyStorage"));
        this.keepAliveEnergyPerTick = compound.getInteger("keepAliveCostPerTick");

        stargateState = EnumStargateState.valueOf(compound.getInteger("stargateState"));
        if (stargateState == null) stargateState = EnumStargateState.IDLE;
        if (node != null && compound.hasKey("node")) node.load(compound.getCompoundTag("node"));

        horizonKilling = compound.getBoolean("horizonKilling");
        horizonSegments = compound.getInteger("horizonSegments");
        openedSince = compound.getLong("openedSince");

        if (compound.hasKey("facingVertical")) {
            int fVertIndex = compound.getInteger("facingVertical");
            facingVertical = (fVertIndex == 2 ? EnumFacing.UP : fVertIndex == 1 ? EnumFacing.DOWN : EnumFacing.SOUTH);
        }

        if (compound.hasKey("lastPos"))
            lastPos = BlockPos.fromLong(compound.getLong("lastPos"));
        else
            lastPos = pos;

        super.readFromNBT(compound);
    }

    @Override
    public boolean prepare(ICommandSender sender, ICommand command) {
        if (!stargateState.idle()) {
            CommandBase.notifyCommandListener(sender, command, "Stop any gate activity before preparation.");
            return false;
        }

        gateAddressMap.clear();
        dialedAddress.clear();
        scheduledTasks.clear();

        return true;
    }

    // ------------------------------------------------------------------------
    // OpenComputers

    /**
     * Tries to find a {@link SymbolInterface} instance from
     * Integer index or String name of the symbol.
     *
     * @param nameIndex Name or index.
     * @return Symbol.
     * @throws IllegalArgumentException When symbol/index is invalid.
     */
    public SymbolInterface getSymbolFromNameIndex(Object nameIndex) throws IllegalArgumentException {
        SymbolInterface symbol = null;

        if (nameIndex instanceof Double && (Double) nameIndex == Math.floor((Double) nameIndex))
            symbol = getSymbolType().valueOfSymbol(((Double) nameIndex).intValue());

        else if (nameIndex instanceof Integer) symbol = getSymbolType().valueOfSymbol((Integer) nameIndex);

        else if (nameIndex instanceof byte[]) symbol = getSymbolType().fromEnglishName(new String((byte[]) nameIndex));

        else if (nameIndex instanceof String) symbol = getSymbolType().fromEnglishName((String) nameIndex);

        if (symbol == null) throw new IllegalArgumentException("bad argument (symbol name/index invalid)");

        return symbol;
    }

    // ------------------------------------------------------------
    // Wireless Network
    @Override
    public int x() {
        return pos.getX();
    }

    @Override
    public int y() {
        return pos.getY();
    }

    @Override
    public int z() {
        return pos.getZ();
    }

    @Override
    public World world() {
        return world;
    }

    @Override
    @Optional.Method(modid = "opencomputers")
    public void receivePacket(Packet packet, WirelessEndpoint sender) {
        //		JSG.info("received packet: ttl="+packet.ttl());

        if (stargateState.engaged() && packet.ttl() > 0) {
            Network.sendWirelessPacket(targetGatePos.getTileEntity(), 20, packet.hop());
        }
    }


    // ------------------------------------------------------------
    // Node-related work
    private final Node node = JSG.ocWrapper.createNode(this, "stargate");

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
    // function(arg:type[, optionArg:type]):resultType; Description.

    @net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    @Callback
    @SuppressWarnings("unused")
    public Object[] getJSGVersion(Context context, Arguments args) {
        return new Object[]{JSG.MOD_VERSION};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(getter = true)
    @SuppressWarnings("unused")
    public Object[] stargateAddress(Context context, Arguments args) {
        if (!isMerged()) return new Object[]{null};

        Map<SymbolTypeEnum, List<String>> map = new HashMap<>(3);

        for (SymbolTypeEnum symbolType : SymbolTypeEnum.values()) {
            map.put(symbolType, gateAddressMap.get(symbolType).getNameList());
        }

        return new Object[]{map};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(getter = true)
    @SuppressWarnings("unused")
    public Object[] dialedAddress(Context context, Arguments args) {
        return (isMerged && !stargateState.incoming() && !stargateState.unstable() && !stargateState.notInitiating()) ? new Object[]{dialedAddress} : new Object[]{null};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
    @SuppressWarnings("unused")
    public Object[] getEnergyStored(Context context, Arguments args) {

        return new Object[]{isMerged ? getEnergyStorage().getEnergyStored() : null};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(getter = true)
    @SuppressWarnings("unused")
    public Object[] isUnstable(Context context, Arguments args) {
        return new Object[]{shouldBeUnstable};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
    @SuppressWarnings("unused")
    public Object[] getMaxEnergyStored(Context context, Arguments args) {
        return new Object[]{isMerged ? getEnergyStorage().getMaxEnergyStored() : null};
    }
}
