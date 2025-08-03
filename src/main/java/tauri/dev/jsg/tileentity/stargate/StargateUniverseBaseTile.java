package tauri.dev.jsg.tileentity.stargate;

import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.stargate.StargateDimensionConfig;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.power.general.EnergyRequiredToOperate;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.renderer.stargate.StargateAbstractRendererState;
import tauri.dev.jsg.renderer.stargate.StargateClassicRendererState;
import tauri.dev.jsg.renderer.stargate.StargateUniverseRendererState;
import tauri.dev.jsg.sound.*;
import tauri.dev.jsg.stargate.*;
import tauri.dev.jsg.stargate.merging.StargateAbstractMergeHelper;
import tauri.dev.jsg.stargate.merging.StargateUniverseMergeHelper;
import tauri.dev.jsg.stargate.network.*;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.state.stargate.StargateRendererActionState;
import tauri.dev.jsg.state.stargate.StargateSpinState;
import tauri.dev.jsg.state.stargate.StargateUniverseSymbolState;
import tauri.dev.jsg.tileentity.props.DestinyBearingTile;
import tauri.dev.jsg.tileentity.props.DestinyChevronTile;
import tauri.dev.jsg.tileentity.props.DestinyVentTile;
import tauri.dev.jsg.tileentity.util.ScheduledTask;
import tauri.dev.jsg.util.LinkingHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;

import static tauri.dev.jsg.stargate.EnumStargateState.DIALING;
import static tauri.dev.jsg.stargate.EnumStargateState.FAILING;
import static tauri.dev.jsg.stargate.network.SymbolUniverseEnum.G1;
import static tauri.dev.jsg.stargate.network.SymbolUniverseEnum.TOP_CHEVRON;

public class StargateUniverseBaseTile extends StargateClassicBaseTile {

    protected World fakeWorld;
    protected BlockPos fakePos;

    @Override
    public World getFakeWorld() {
        if (fakeWorld == null) return world;
        return fakeWorld;
    }

    @Override
    public void setFakeWorld(World world) {
        fakeWorld = world;
        markDirty();
    }

    @Override
    public BlockPos getFakePos() {
        if (fakePos == null) return pos;
        return fakePos;
    }

    @Override
    public void setFakePos(BlockPos pos) {
        fakePos = pos;
        markDirty();
    }

    public void resetFakePos(){
        this.fakePos = this.pos;
        this.fakeWorld = this.world;
        markDirty();
    }

    // general
    private static final EnumSet<BiomeOverlayEnum> SUPPORTED_OVERLAYS = EnumSet.of(
            BiomeOverlayEnum.NORMAL,
            BiomeOverlayEnum.FROST,
            BiomeOverlayEnum.MOSSY,
            BiomeOverlayEnum.AGED
    );

    // dialing
    private StargateAddress addressToDial;
    private int symbolsToDialCount;
    private int addressPosition;

    // aborting
    private boolean abortingDialing = false;

    // fast dialing
    private static final int fastDialingPeriod = 30; //ticks (1 symbol per this)

    // cooldown
    private int coolDown = 0;
    private static final int COOL_DOWN_DELAY = 60; // in ticks (3 seconds)

    // --------------------------------------------------------------------------------
    // CoolDown system - prevent from spamming uni dialer functions

    public boolean canContinue() {
        return !(coolDown > 0);
    }

    public void updateCoolDown() {
        if (coolDown > 0)
            coolDown--;
        if (coolDown < 0)
            coolDown = 0;
        markDirty();
    }

    public void setCoolDown() {
        coolDown = COOL_DOWN_DELAY;
        markDirty();
    }


    // --------------------------------------------------------------------------------
    // Update

    private BlockPos lastPos = BlockPos.ORIGIN;

    @Override
    public void update() {
        super.update();
        updateCoolDown();
        updateFloorChevron();

        if (!world.isRemote) {
            if (!lastPos.equals(pos)) {
                lastPos = pos;
                markDirty();
            }
        }
    }

    @Override
    protected void onGateMerged() {
        super.onGateMerged();
    }

    // --------------------------------------------------------------------------------
    // Dialing

    public StargateAddress getAddressToDial() {
        return addressToDial;
    }

    public void addSymbolToAddressDHD(SymbolInterface symbol) {
        if(isNoxDialing){
            stargateState = DIALING;
            markDirty();
            addSymbolToAddress(symbol);
            stargateState = EnumStargateState.IDLE;
            markDirty();
        }
    }

    /**
     * Dial the address using DIALER
     *
     * @param address     - address to dial
     * @param symbolCount - symbols to engage
     */
    public boolean dialAddress(StargateAddress address, int symbolCount, boolean withoutEnergy, EnumDialingType dialingType) {
        if (!canContinue()) return false;
        if (!stargateState.idle()) return false;
        super.dialAddress(address, symbolCount, withoutEnergy, dialingType);

        if(isNoxDialing){
            for(int i = 0; i < symbolCount; i++) {
                addSymbolToAddressByNox(address.get(i));
            }
            addSymbolToAddressByNox(getSymbolType().getOrigin());
            addSymbolToAddressByNox(getSymbolType().getBRB());
            return true;
        }

        this.addressToDial = address;
        this.symbolsToDialCount = symbolCount;
        this.addressPosition = -1;
        targetRingSymbol = G1;
        stargateState = DIALING;
        JSGSoundHelper.playSoundEvent(world, getGateCenterPos(), SoundEventEnum.GATE_UNIVERSE_DIAL_START);
        sendRenderingUpdate(StargateRendererActionState.EnumGateAction.LIGHT_UP_CHEVRONS, 9, true);
        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_DIAL_NEXT, 35, null));
        ringSpinContext = null;
        spinDirection = EnumSpinDirection.CLOCKWISE;
        setCoolDown();
        markDirty();
        if (isFastDialing)
            updateBearing(true);
        return true;
    }

    /**
     * get next symbol from addressToDial
     *
     * @return SymbolInterface
     */
    public SymbolInterface getNextSymbol(boolean addOne) {
        addressPosition++;
        int pos = addressPosition;
        if (!addOne) addressPosition--;
        markDirty();
        if (addressToDial == null) return null;
        if (pos >= (addressToDial.getSize() + 1)) return getSymbolType().getTopSymbol();
        if (pos >= symbolsToDialCount && addOne)
            return getSymbolType().getOrigin(); // return origin when symbols is last one
        else if (pos >= symbolsToDialCount)
            return null;
        return addressToDial.get(pos);
    }

    /**
     * abort dialing
     */
    @Override
    public boolean abortDialingSequence() {
        if (stargateState.incoming()) return false;
        if (isIncoming) return false;
        if (canContinue() && (stargateState.dialingComputer() || stargateState.idle() || stargateState.dialing())) {
            abortingDialing = true;
            currentRingSymbol = targetRingSymbol;
            markDirty();
            addTask(new ScheduledTask(EnumScheduledTask.STARGATE_RESET, 60, null));
            spinStartTime = -1;
            isSpinning = false;
            sendState(StateTypeEnum.SPIN_STATE, new StargateSpinState(targetRingSymbol, spinDirection, true, 0));
            addFailedTaskAndPlaySound();
            playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, false);
            // remove last spinning finished task
            if (lastSpinFinished != null && scheduledTasks.contains(lastSpinFinished))
                removeTask(lastSpinFinished);
            if (!isIncoming) disconnectGate();
            stargateState = FAILING;
            setCoolDown();
            markDirty();
            resetTargetIncomingAnimation();
            return true;
        }
        return false;
    }

    /**
     * fail the gate
     */
    @Override
    public void failGate() {
        if (stargateState.incoming()) return;
        isIncoming = false;
        markDirty();
        if (abortingDialing) {
            isFinalActive = false;
            markDirty();
            return;
        }
        super.failGate();
        addressToDial = null;
        updateBearing(false);
        if (!abortingDialing && targetRingSymbol != TOP_CHEVRON)
            addSymbolToAddressManual(TOP_CHEVRON, null);
    }

    @Override
    public void dialingFailed(StargateOpenResult reason) {
        playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, false);
        super.dialingFailed(reason);
    }

    /**
     * disconnect gate
     */
    @Override
    protected void disconnectGate() {
        isIncoming = false;
        markDirty();
        super.disconnectGate();
        addressToDial = null;
        updateBearing(false);
        if (!abortingDialing)
            addSymbolToAddressManual(TOP_CHEVRON, null);
    }

    /**
     * Add symbol by spinning the gate
     *
     * @param targetSymbol - symbol to dial
     * @param context      - null if dialing with dialer
     */
    @Override
    public void addSymbolToAddressManual(SymbolInterface targetSymbol, Object context) {
        if (stargateState.incoming()) return;
        updateBearing(false);
        if (targetSymbol != getSymbolType().getTopSymbol()) {
            if (context != null) stargateState = EnumStargateState.DIALING_COMPUTER;
            else stargateState = DIALING;
        }

        if (dialedAddress.size() == 0 && targetSymbol != TOP_CHEVRON && stargateState.dialingComputer()) {
            JSGSoundHelper.playSoundEvent(world, getGateCenterPos(), SoundEventEnum.GATE_UNIVERSE_DIAL_START);
            sendRenderingUpdate(StargateRendererActionState.EnumGateAction.LIGHT_UP_CHEVRONS, 9, true);
            targetRingSymbol = targetSymbol;
            addTask(new ScheduledTask(EnumScheduledTask.STARGATE_DIAL_NEXT, 35, null));
            ringSpinContext = context;
            spinDirection = EnumSpinDirection.CLOCKWISE;
        } else super.addSymbolToAddressManual(targetSymbol, context);

        if (targetSymbol == getSymbolType().getTopSymbol())
            stargateState = FAILING;
        markDirty();
    }

    /**
     * get energy required to dial specific address
     *
     * @param targetGatePos - position of target gate
     * @return energy
     */
    @Override
    protected EnergyRequiredToOperate getEnergyRequiredToDial(StargatePos targetGatePos, StargateAddressDynamic address) {
        if(dialingWithoutEnergy)
            return EnergyRequiredToOperate.free();
        BlockPos sPos = getFakePos();
        BlockPos tPos = targetGatePos.gatePos;
        int sourceDim = getFakeWorld().provider.getDimension();
        int targetDim = targetGatePos.getWorld().provider.getDimension();

        StargateAbstractBaseTile targetTile = targetGatePos.getTileEntity();
        if (targetTile instanceof StargateUniverseBaseTile) {
            tPos = ((StargateUniverseBaseTile) targetTile).getFakePos();
            targetDim = ((StargateUniverseBaseTile) targetTile).getFakeWorld().provider.getDimension();
        }

        if (sourceDim == DimensionType.OVERWORLD.getId() && targetDim == DimensionType.NETHER.getId())
            tPos = new BlockPos(tPos.getX() * 8, tPos.getY(), tPos.getZ() * 8);
        else if (sourceDim == DimensionType.NETHER.getId() && targetDim == DimensionType.OVERWORLD.getId())
            sPos = new BlockPos(sPos.getX() * 8, sPos.getY(), sPos.getZ() * 8);

        double distance = (int) sPos.getDistance(tPos.getX(), tPos.getY(), tPos.getZ());

        if (distance < 5000) distance *= 0.8;
        else distance = 5000 * Math.log10(distance) / Math.log10(5000);

        EnergyRequiredToOperate energyRequired = EnergyRequiredToOperate.stargate();
        energyRequired = energyRequired.mul(distance).add(StargateDimensionConfig.getCost(sourceDim, targetDim));

        if (address.size() == 9)
            energyRequired = energyRequired.mul(JSGConfig.Stargate.power.nineSymbolAddressMul);
        if (address.size() == 8)
            energyRequired = energyRequired.mul(JSGConfig.Stargate.power.eightSymbolAddressMul);

        return energyRequired.mul(JSGConfig.Stargate.power.stargateUniverseEnergyMul);
    }

    @Override
    public SymbolTypeEnum getSymbolType() {
        return SymbolTypeEnum.UNIVERSE;
    }

    @Override
    protected int getMaxChevrons() {
        return 9;
    }

    @Override
    protected int getOpenSoundDelay() {
        return super.getOpenSoundDelay() + 10;
    }

    private void activateSymbolServer(SymbolInterface symbol) {
        if (!(symbol instanceof SymbolUniverseEnum)) {
            JSG.error("Error while engaging symbol " + symbol.getEnglishName() + " for clients.", new ClassCastException());
            return;
        }
        JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.STARGATE_UNIVERSE_ACTIVATE_SYMBOL, new StargateUniverseSymbolState((SymbolUniverseEnum) symbol, false)), targetPoint);
    }

    public void updateBearing(boolean activate) {
        if (world.isRemote) return;
        BlockPos p = getMergeHelper().getTopBlockAboveBase();
        if (p == null) return;
        BlockPos bearingPos = p.up().add(pos);
        TileEntity te = world.getTileEntity(bearingPos);
        if (te instanceof DestinyBearingTile) {
            ((DestinyBearingTile) te).updateState(activate);
        }
    }

    public boolean isFloorChevronActivated = false;

    public void updateFloorChevron() {
        if (!world.isRemote) {
            // Server
            // - update active state

            boolean shouldBeActivated = (getStargateState().engaged() || getStargateState().unstable());
            if (shouldBeActivated == isFloorChevronActivated) return;

            isFloorChevronActivated = shouldBeActivated;
            markDirty();

            BlockPos pos = LinkingHelper.findClosestPos(world, this.pos, new BlockPos(10, 3, 10), new Block[]{JSGBlocks.DESTINY_CHEVRON_BLOCK}, new ArrayList<>());
            if (pos == null) return;
            TileEntity te = world.getTileEntity(pos);
            if (!(te instanceof DestinyChevronTile)) return;
            ((DestinyChevronTile) te).updateState(isFloorChevronActivated);
        } else {
            // Client
            // - update overlay
            BlockPos pos = LinkingHelper.findClosestPos(world, this.pos, new BlockPos(10, 3, 10), new Block[]{JSGBlocks.DESTINY_CHEVRON_BLOCK}, new ArrayList<>());
            if (pos == null) return;
            TileEntity te = world.getTileEntity(pos);
            if (!(te instanceof DestinyChevronTile)) return;
            StargateAbstractRendererState rs = getRendererStateClient();
            if (rs == null) return;
            ((DestinyChevronTile) te).updateOverlay(rs.getBiomeOverlay());
        }
    }

    @Override
    public void openGate(StargatePos targetGatePos, boolean isInitiating, boolean noxDialing) {
        super.openGate(targetGatePos, isInitiating, noxDialing);
        wasStargateActivated = true;
        markDirty();
    }

    private boolean wasStargateActivated = false;

    public void animateVents() {
        if (!wasStargateActivated) return;
        if (world.isRemote) return;
        wasStargateActivated = false;
        markDirty();
        ArrayList<BlockPos> alreadyDone = new ArrayList<>();
        BlockPos nearest;
        do {
            nearest = LinkingHelper.findClosestPos(world, this.pos, new BlockPos(10, 3, 10), new Block[]{JSGBlocks.DESTINY_VENT_BLOCK}, alreadyDone);
            alreadyDone.add(nearest);
            if (nearest != null) {
                TileEntity te = world.getTileEntity(nearest);
                if (te instanceof DestinyVentTile) {
                    ((DestinyVentTile) te).startAnimation();
                }
            }
        } while (nearest != null);
    }

    @Override
    protected void addSymbolToAddress(SymbolInterface symbol) {
        updateBearing(true);
        activateSymbolServer(symbol);
        super.addSymbolToAddress(symbol);
    }

    // --------------------------------------------------------------------------------
    // Scheduled tasks

    @Override
    public void executeTask(EnumScheduledTask scheduledTask, NBTTagCompound customData) {
        boolean onlySpin = false;
        if (customData != null && customData.hasKey("onlySpin"))
            onlySpin = customData.getBoolean("onlySpin");
        switch (scheduledTask) {
            case LIGHT_UP_CHEVRONS:
                sendRenderingUpdate(StargateRendererActionState.EnumGateAction.LIGHT_UP_CHEVRONS, 9, true);
                break;
            case STARGATE_DIAL_NEXT:
                updateBearing(false);
                if (stargateState.incoming()) break;
                if (abortingDialing || stargateState.failing()) break;
                if (isFastDialing && stargateState.dialingDHD()) {
                    if (dialedAddress.size() == 0)
                        spinRing(1, false, true, fastDialingPeriod * symbolsToDialCount + (5 * 20));
                    SymbolInterface tempSymbol = getNextSymbol(true);
                    if (!canAddSymbol(tempSymbol) || tempSymbol == TOP_CHEVRON)
                        break;
                    addSymbolToAddress(tempSymbol);
                    playSoundEvent(StargateSoundEventEnum.CHEVRON_SHUT);
                    //addTask(new ScheduledTask(EnumScheduledTask.STARGATE_DIAL_FINISHED, 10));
                    sendSignal(ringSpinContext, "stargate_spin_chevron_engaged", new Object[]{dialedAddress.size(), stargateWillLock(targetRingSymbol), targetRingSymbol.getEnglishName()});
                    doIncomingAnimation(fastDialingPeriod * 2, true, getNextSymbol(false));
                    if (!stargateWillLock(tempSymbol)) {
                        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_DIAL_NEXT, fastDialingPeriod));
                    }
                    break;
                }
                if (stargateState.dialingComputer() && targetRingSymbol != getSymbolType().getTopSymbol())
                    super.addSymbolToAddressManual(targetRingSymbol, ringSpinContext);
                else if (targetRingSymbol != getSymbolType().getTopSymbol()) {
                    targetRingSymbol = getNextSymbol(true);
                    markDirty();
                    if (targetRingSymbol == TOP_CHEVRON) {
                        abortDialingSequence();
                        break;
                    }
                    addSymbolToAddressManual(targetRingSymbol, null);
                }
                break;
            case STARGATE_RESET:
                if (stargateState.incoming()) break;
                updateBearing(false);
                addSymbolToAddressManual(getSymbolType().getTopSymbol(), null);
                abortingDialing = false;
                break;

            case STARGATE_SPIN_FINISHED:
                if (onlySpin && stargateState.dialingComputer()) {
                    stargateState = EnumStargateState.IDLE;
                    sendRenderingUpdate(StargateRendererActionState.EnumGateAction.CLEAR_CHEVRONS, 9, true);
                    markDirty();
                    break;
                } else if (onlySpin && stargateState.dialing() && isFastDialing) {
                    attemptOpenAndFail();
                    break;
                } else if (onlySpin || stargateState.incoming())
                    break;

                if ((targetRingSymbol != TOP_CHEVRON)) {
                    playSoundEvent(StargateSoundEventEnum.CHEVRON_SHUT);
                    addTask(new ScheduledTask(EnumScheduledTask.STARGATE_DIAL_FINISHED, 10));
                } else {
                    updateBearing(false);
                    animateVents();
                    dialingFailed(StargateOpenResult.ABORTED);
                    stargateState = EnumStargateState.IDLE;
                    abortingDialing = false;
                }
                markDirty();
                break;

            case BEGIN_SPIN:
                if (customData != null && customData.hasKey("period")) {
                    int period = customData.getInteger("period");
                    spinRing(1, false, true, period);
                }
                break;

            case STARGATE_FAILED_SOUND:
                playSoundEvent(StargateSoundEventEnum.DIAL_FAILED);
                markDirty();
                break;

            case STARGATE_DIAL_FINISHED:
                if (abortingDialing) return;
                if (canAddSymbol(targetRingSymbol)) {
                    addSymbolToAddress(targetRingSymbol);
                    if (stargateState.dialingComputer()) {
                        if (!abortingDialing) stargateState = EnumStargateState.IDLE;
                    } else {
                        if (!stargateWillLock(targetRingSymbol)) {
                            addTask(new ScheduledTask(EnumScheduledTask.STARGATE_DIAL_NEXT, 24));
                        } else
                            attemptOpenAndFail();
                    }
                } else if (!stargateState.incoming()) {
                    dialingFailed(StargateOpenResult.ADDRESS_MALFORMED);
                    stargateState = EnumStargateState.IDLE;
                }
                sendSignal(ringSpinContext, "stargate_spin_chevron_engaged", new Object[]{dialedAddress.size(), stargateWillLock(targetRingSymbol), targetRingSymbol.getEnglishName()});
                break;

            default:
                break;
        }
        markDirty();
        super.executeTask(scheduledTask, customData);
    }

    // --------------------------------------------------------------------------------
    // States

    @Override
    public State createState(StateTypeEnum stateType) {
        if (stateType == StateTypeEnum.STARGATE_UNIVERSE_ACTIVATE_SYMBOL) {
            return new StargateUniverseSymbolState();
        }
        return super.createState(stateType);
    }

    @Override
    public void setState(StateTypeEnum stateType, State state) {
        if (getRendererStateClient() != null) {
            switch (stateType) {
                case STARGATE_UNIVERSE_ACTIVATE_SYMBOL:
                    StargateUniverseSymbolState symbolState = (StargateUniverseSymbolState) state;

                    if (symbolState.dimAll) getRendererStateClient().clearSymbols(world.getTotalWorldTime());
                    else getRendererStateClient().activateSymbol(world.getTotalWorldTime(), symbolState.symbol);

                    break;

                case RENDERER_UPDATE:
                    StargateRendererActionState gateActionState = (StargateRendererActionState) state;

                    if (gateActionState.action == StargateRendererActionState.EnumGateAction.CLEAR_CHEVRONS) {
                        getRendererStateClient().clearSymbols(world.getTotalWorldTime());
                    }

                    break;

                default:
                    break;
            }
        }

        super.setState(stateType, state);
    }

    // --------------------------------------------------------------------------------
    // Incoming animation

    @Override
    public void incomingWormhole(int dialedAddressSize) {
        startIncomingAnimation(dialedAddressSize, 10);
        super.incomingWormhole(9);
    }

    @Override
    public void incomingWormhole(int dialedAddressSize, int time) {
        time = time * dialedAddressSize;
        int period = time - 2000;
        if (period < 0) period = 0;
        startIncomingAnimation(dialedAddressSize, period);
        super.incomingWormhole(9, false);
    }

    @Override
    public void startIncomingAnimation(int addressSize, int period) {
        double ticks = (double) (period * 20) / 1000;
        incomingPeriod = (int) Math.round(ticks);
        incomingAddressSize = addressSize;
        incomingLastChevronLightUp = 0;
        stargateState = EnumStargateState.INCOMING;
        isIncoming = true;
        sendRenderingUpdate(StargateRendererActionState.EnumGateAction.CLEAR_CHEVRONS, 9, true);
        if (stargateState.dialing())
            abortDialingSequence();
        markDirty();
        updateBearing(true);
        this.lightUpChevronByIncoming(!config.getOption(ConfigOptions.ALLOW_INCOMING.id).getBooleanValue());
    }

    @Override
    protected void lightUpChevronByIncoming(boolean disableAnimation) {
        super.lightUpChevronByIncoming(disableAnimation);
        if (incomingPeriod == -1) return;

        boolean spin = config.getOption(ConfigOptions.SPIN_GATE_INCOMING.id).getBooleanValue();

        if (!disableAnimation && incomingLastChevronLightUp == 1) {
            stargateState = EnumStargateState.INCOMING;
            NBTTagCompound compound = new NBTTagCompound();
            int time = incomingPeriod - (8 + 7);
            compound.setInteger("period", time);
            if (spin && time > 25)
                addTask(new ScheduledTask(EnumScheduledTask.BEGIN_SPIN, 8 + 7, compound));
            addTask(new ScheduledTask(EnumScheduledTask.LIGHT_UP_CHEVRONS, 8));
            sendSignal(null, "stargate_incoming_wormhole", new Object[]{incomingAddressSize});
            playSoundEvent(StargateSoundEventEnum.INCOMING);
            super.resetIncomingAnimation();
            isIncoming = false;
            if (irisMode == EnumIrisMode.AUTO && isIrisOpened()) {
                toggleIris();
            }
        } else {
            if (incomingLastChevronLightUp == 2 && disableAnimation) {
                stargateState = EnumStargateState.INCOMING;
                sendRenderingUpdate(StargateRendererActionState.EnumGateAction.LIGHT_UP_CHEVRONS, 9, true);
                sendSignal(null, "stargate_incoming_wormhole", new Object[]{incomingAddressSize});
                playSoundEvent(StargateSoundEventEnum.INCOMING);
                super.resetIncomingAnimation();
                isIncoming = false;
                if (irisMode == EnumIrisMode.AUTO && isIrisOpened()) {
                    toggleIris();
                }
            } else if (isIncoming && !stargateState.engaged())
                stargateState = EnumStargateState.INCOMING;
        }
        markDirty();
    }

    // --------------------------------------------------------------------------------
    // Overlays

    @Override
    public EnumSet<BiomeOverlayEnum> getSupportedOverlays() {
        return SUPPORTED_OVERLAYS;
    }

    // --------------------------------------------------------------------------------
    // Sounds

    @Nullable
    @Override
    protected SoundPositionedEnum getPositionedSound(StargateSoundPositionedEnum soundEnum) {
        switch (soundEnum) {
            case GATE_RING_ROLL:
                return SoundPositionedEnum.UNIVERSE_RING_ROLL;
            case GATE_RING_ROLL_START:
                return SoundPositionedEnum.UNIVERSE_RING_ROLL_START;
        }

        return null;
    }

    @Nullable
    @Override
    protected SoundEventEnum getSoundEvent(StargateSoundEventEnum soundEnum) {
        switch (soundEnum) {
            case OPEN:
                return SoundEventEnum.GATE_UNIVERSE_OPEN;
            case OPEN_NOX:
                return SoundEventEnum.GATE_NOX_OPEN;
            case CLOSE:
                return SoundEventEnum.GATE_UNIVERSE_CLOSE;
            case DIAL_FAILED:
                return SoundEventEnum.GATE_UNIVERSE_DIAL_FAILED;
            case INCOMING:
                return SoundEventEnum.GATE_UNIVERSE_DIAL_START;
            case CHEVRON_SHUT:
                return targetRingSymbol == TOP_CHEVRON ? SoundEventEnum.GATE_UNIVERSE_CHEVRON_TOP_LOCK : SoundEventEnum.GATE_UNIVERSE_CHEVRON_LOCK;
        }

        return null;
    }

    // --------------------------------------------------------------------------------
    // Merging

    @Override
    public StargateAbstractMergeHelper getMergeHelper() {
        return StargateUniverseMergeHelper.INSTANCE;
    }

    @Override
    protected boolean onGateMergeRequested() {
        if (stargateSize != JSGConfig.Stargate.stargateSize) {
            StargateUniverseMergeHelper.INSTANCE.convertToPattern(world, pos, facing, facingVertical, stargateSize, tauri.dev.jsg.config.JSGConfig.Stargate.stargateSize);
            stargateSize = tauri.dev.jsg.config.JSGConfig.Stargate.stargateSize;
        }

        return StargateUniverseMergeHelper.INSTANCE.checkBlocks(world, pos, facing, facingVertical);
    }

    @Override
    public void setLinkedDHD(BlockPos dhdPos, int linkId) {
    }


    // --------------------------------------------------------------------------------
    // Renderer states

    @Override
    protected StargateClassicRendererState.StargateClassicRendererStateBuilder getRendererStateServer() {
        return new StargateUniverseRendererState.StargateUniverseRendererStateBuilder(super.getRendererStateServer())
                .setDialedAddress((stargateState.initiating() || stargateState.dialing()) ? dialedAddress : new StargateAddressDynamic(getSymbolType()))
                .setActiveChevrons(stargateState.idle() ? 0 : 9).setStargateSize(stargateSize);
    }

    @Override
    protected StargateUniverseRendererState createRendererStateClient() {
        return new StargateUniverseRendererState();
    }

    @Override
    public StargateUniverseRendererState getRendererStateClient() {
        return (StargateUniverseRendererState) super.getRendererStateClient();
    }

    @Override
    public int getDefaultCapacitors() {
        return JSGConfig.Stargate.power.universeCapacitors;
    }


    // --------------------------------------------------------------------------------
    // NBTs

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        if (addressToDial != null) compound.setTag("addressToDial", addressToDial.serializeNBT());
        compound.setInteger("symbolsToDialCount", symbolsToDialCount);
        compound.setInteger("addressPosition", addressPosition);
        compound.setInteger("coolDown", coolDown);
        compound.setBoolean("abortingDialing", abortingDialing);
        compound.setBoolean("wasStargateActivated", wasStargateActivated);

        if (fakePos != null) {
            compound.setInteger("fakeX", fakePos.getX());
            compound.setInteger("fakeY", fakePos.getY());
            compound.setInteger("fakeZ", fakePos.getY());
        }
        if (fakeWorld != null)
            compound.setInteger("fakeWorld", fakeWorld.provider.getDimension());

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);

        addressToDial = new StargateAddress(compound.getCompoundTag("addressToDial"));
        addressPosition = compound.getInteger("addressPosition");
        symbolsToDialCount = compound.getInteger("symbolsToDialCount");
        coolDown = compound.getInteger("coolDown");
        abortingDialing = compound.getBoolean("abortingDialing");
        wasStargateActivated = compound.getBoolean("wasStargateActivated");

        if (compound.hasKey("fakeX"))
            this.fakePos = new BlockPos(compound.getInteger("fakeX"), compound.getInteger("fakeY"), compound.getInteger("fakeZ"));
        if (compound.hasKey("fakeWorld") && world.getMinecraftServer() != null)
            this.fakeWorld = this.world.getMinecraftServer().getWorld(compound.getInteger("fakeWorld"));
    }

    public NearbyGate getRandomNearbyGate() {
        ArrayList<NearbyGate> addresses = getNearbyGates();
        if (addresses.size() == 0) return null;
        int i = (int) Math.min(Math.floor(Math.random() * addresses.size()), (addresses.size() - 1));
        if (i < 0) i = 0;
        return addresses.get(i);
    }

    @Override
    public boolean prepare(ICommandSender sender, ICommand command) {
        setLinkedDHD(null, -1);

        return super.prepare(sender, command);
    }
}