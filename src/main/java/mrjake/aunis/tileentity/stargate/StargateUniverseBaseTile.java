package mrjake.aunis.tileentity.stargate;

import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.config.StargateSizeEnum;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.stargate.StargateClassicRendererState;
import mrjake.aunis.renderer.stargate.StargateUniverseRendererState;
import mrjake.aunis.sound.*;
import mrjake.aunis.stargate.EnumIrisMode;
import mrjake.aunis.stargate.EnumScheduledTask;
import mrjake.aunis.stargate.EnumStargateState;
import mrjake.aunis.stargate.StargateOpenResult;
import mrjake.aunis.stargate.merging.StargateAbstractMergeHelper;
import mrjake.aunis.stargate.merging.StargateUniverseMergeHelper;
import mrjake.aunis.stargate.network.*;
import mrjake.aunis.stargate.power.StargateEnergyRequired;
import mrjake.aunis.state.State;
import mrjake.aunis.state.StateTypeEnum;
import mrjake.aunis.state.stargate.StargateRendererActionState;
import mrjake.aunis.state.stargate.StargateSpinState;
import mrjake.aunis.state.stargate.StargateUniverseSymbolState;
import mrjake.aunis.tileentity.util.ScheduledTask;
import mrjake.aunis.util.AunisAxisAlignedBB;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

import static mrjake.aunis.stargate.EnumStargateState.DIALING;
import static mrjake.aunis.stargate.EnumStargateState.FAILING;
import static mrjake.aunis.stargate.network.SymbolUniverseEnum.G1;
import static mrjake.aunis.stargate.network.SymbolUniverseEnum.TOP_CHEVRON;

public class StargateUniverseBaseTile extends StargateClassicBaseTile {

    // general
    private static final StargateSizeEnum defaultStargateSize = StargateSizeEnum.SMALL;
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
    private boolean isFastDialing = false;
    private static final int fastDialingPeriod = 30; //ticks (1 symbol per this)

    // cooldown
    private int coolDown = 0;
    private static final int coolDownDelay = 60; // in ticks (3 seconds)

    // --------------------------------------------------------------------------------
    // CoolDown system - prevent from spamming uni dialer functions

    public boolean canContinue(){
        return !(coolDown > 0);
    }
    public void updateCoolDown(){
        if(coolDown > 0)
            coolDown--;
        if(coolDown < 0)
            coolDown = 0;
        markDirty();
    }
    public void setCoolDown(){
        coolDown = coolDownDelay;
        markDirty();
    }


    // --------------------------------------------------------------------------------
    // Update

    private BlockPos lastPos = BlockPos.ORIGIN;

    @Override
    public void update(){
        super.update();
        updateCoolDown();

        if (!world.isRemote) {
            if (!lastPos.equals(pos)) {
                lastPos = pos;
                markDirty();
            }
        }
    }

    // --------------------------------------------------------------------------------
    // Dialing

    public StargateAddress getAddressToDial() {
        return addressToDial;
    }

    /**
     * Dial the address using DIALER
     *
     * @param address     - address to dial
     * @param symbolCount - symbols to engage
     */
    public boolean dialAddress(StargateAddress address, int symbolCount) {
        if(!canContinue()) return false;
        if (!stargateState.idle()) return false;

        this.addressToDial = address;
        this.symbolsToDialCount = symbolCount;
        this.addressPosition = -1;
        targetRingSymbol = G1;
        stargateState = DIALING;
        AunisSoundHelper.playSoundEvent(world, getGateCenterPos(), SoundEventEnum.GATE_UNIVERSE_DIAL_START);
        sendRenderingUpdate(StargateRendererActionState.EnumGateAction.LIGHT_UP_CHEVRONS, 9, true);
        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_DIAL_NEXT, 35, null));
        ringSpinContext = null;
        setCoolDown();
        markDirty();
        return true;
    }

    public boolean getFastDialState(){
        return isFastDialing;
    }
    public void setFastDial(boolean state){
        if(stargateState.idle()) isFastDialing = state;
        markDirty();
    }

    /**
     * get next symbol from addressToDial
     *
     * @return SymbolInterface
     */
    public SymbolInterface getNextSymbol(boolean addOne) {
        addressPosition++;
        int pos = addressPosition;
        if(!addOne) addressPosition--;

        markDirty();
        if(pos >= (addressToDial.getSize() + 1)) return getSymbolType().getTopSymbol();
        if (pos >= symbolsToDialCount && addOne)
            return getSymbolType().getOrigin(); // return origin when symbols is last one
        else if(pos >= symbolsToDialCount)
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
            addTask(new ScheduledTask(EnumScheduledTask.STARGATE_RESET, 5, null));
            spinStartTime = world.getTotalWorldTime() + 3000;
            isSpinning = false;
            AunisPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.SPIN_STATE, new StargateSpinState(targetRingSymbol, spinDirection, true, 0)), targetPoint);
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
        if(stargateState.incoming()) return;
        isIncoming = false;
        markDirty();
        if(abortingDialing){
            isFinalActive = false;
            markDirty();
            return;
        }
        super.failGate();
        if (!abortingDialing && targetRingSymbol != TOP_CHEVRON)
            addSymbolToAddressManual(TOP_CHEVRON, null);
    }

    @Override
    public void dialingFailed(StargateOpenResult reason){
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
        if(!abortingDialing)
            addSymbolToAddressManual(TOP_CHEVRON, null);
    }

    /**
     * Add symbol by spinning the gate
     * @param targetSymbol - symbol to dial
     * @param context - null if dialing with dialer
     */
    @Override
    public void addSymbolToAddressManual(SymbolInterface targetSymbol, Object context) {
        if(stargateState.incoming()) return;
        if(targetSymbol != getSymbolType().getTopSymbol()) {
            if (context != null) stargateState = EnumStargateState.DIALING_COMPUTER;
            else stargateState = DIALING;
        }

        if (dialedAddress.size() == 0 && targetSymbol != TOP_CHEVRON && stargateState.dialingComputer()) {
            AunisSoundHelper.playSoundEvent(world, getGateCenterPos(), SoundEventEnum.GATE_UNIVERSE_DIAL_START);
            sendRenderingUpdate(StargateRendererActionState.EnumGateAction.LIGHT_UP_CHEVRONS, 9, true);
            targetRingSymbol = targetSymbol;
            addTask(new ScheduledTask(EnumScheduledTask.STARGATE_DIAL_NEXT, 35, null));
            ringSpinContext = context;
        } else super.addSymbolToAddressManual(targetSymbol, context);

        if(targetSymbol == getSymbolType().getTopSymbol())
            stargateState = FAILING;
        markDirty();
    }

    /**
     * get energy required to dial specific address
     * @param targetGatePos - position of target gate
     * @return energy
     */
    @Override
    protected StargateEnergyRequired getEnergyRequiredToDial(StargatePos targetGatePos) {
        return super.getEnergyRequiredToDial(targetGatePos).mul(AunisConfig.powerConfig.stargateUniverseEnergyMul);
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
        AunisPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.STARGATE_UNIVERSE_ACTIVATE_SYMBOL, new StargateUniverseSymbolState((SymbolUniverseEnum) symbol, false)), targetPoint);
    }

    @Override
    protected void addSymbolToAddress(SymbolInterface symbol) {
        activateSymbolServer(symbol);
        if(isFastDialing)
            playSoundEvent(StargateSoundEventEnum.CHEVRON_SHUT);
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
                if(stargateState.incoming()) break;
                if(abortingDialing || stargateState.failing()) break;
                if(isFastDialing && stargateState.dialingDHD()){
                    if(dialedAddress.size() == 0)
                        spinRing(1, false, true, fastDialingPeriod*symbolsToDialCount+(5*20));
                    SymbolInterface tempSymbol = getNextSymbol(true);
                    if(!canAddSymbol(tempSymbol) || tempSymbol == TOP_CHEVRON)
                        break;
                    addSymbolToAddress(tempSymbol);
                    addTask(new ScheduledTask(EnumScheduledTask.STARGATE_DIAL_FINISHED, 10));
                    doIncomingAnimation(fastDialingPeriod*2, true, getNextSymbol(false));
                    if(!stargateWillLock(tempSymbol)){
                        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_DIAL_NEXT, fastDialingPeriod));
                    }
                    break;
                }
                if (stargateState.dialingComputer() && targetRingSymbol != getSymbolType().getTopSymbol())
                    super.addSymbolToAddressManual(targetRingSymbol, ringSpinContext);
                else if (targetRingSymbol != getSymbolType().getTopSymbol()) {
                    targetRingSymbol = getNextSymbol(true);
                    markDirty();
                    if(targetRingSymbol == TOP_CHEVRON){
                        abortDialingSequence();
                        break;
                    }
                    addSymbolToAddressManual(targetRingSymbol, null);
                }
                break;
            case STARGATE_RESET:
                if(stargateState.incoming()) break;
                addSymbolToAddressManual(getSymbolType().getTopSymbol(), null);
                abortingDialing = false;
                break;

            case STARGATE_SPIN_FINISHED:
                if (onlySpin && stargateState.dialingComputer()) {
                    stargateState = EnumStargateState.IDLE;
                    sendRenderingUpdate(StargateRendererActionState.EnumGateAction.CLEAR_CHEVRONS, 9, true);
                    markDirty();
                    break;
                }
                else if(onlySpin && stargateState.dialing() && isFastDialing){
                    attemptOpenAndFail();
                    break;
                }
                else if(onlySpin || stargateState.incoming())
                    break;

                if ((targetRingSymbol != TOP_CHEVRON)) {
                    if (canAddSymbol(targetRingSymbol)) {
                        addSymbolToAddress(targetRingSymbol);
                        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_DIAL_FINISHED, 10));

                        if (stargateState.dialingComputer()) {
                            if(!abortingDialing) stargateState = EnumStargateState.IDLE;
                        } else {
                            if (!stargateWillLock(targetRingSymbol)) {
                                addTask(new ScheduledTask(EnumScheduledTask.STARGATE_DIAL_NEXT, 14));
                            }
                            else
                                attemptOpenAndFail();
                        }
                    } else if(!stargateState.incoming()){
                        dialingFailed(StargateOpenResult.ADDRESS_MALFORMED);
                        stargateState = EnumStargateState.IDLE;
                    }
                } else{
                    dialingFailed(StargateOpenResult.ABORTED);
                    stargateState = EnumStargateState.IDLE;
                    abortingDialing = false;
                }
                markDirty();
                break;

            case BEGIN_SPIN:
                if(customData != null && customData.hasKey("period")){
                    int period = customData.getInteger("period");
                    spinRing(1, false, true, period);
                }
                break;

            case STARGATE_FAILED_SOUND:
                playSoundEvent(StargateSoundEventEnum.DIAL_FAILED);
                markDirty();
                break;

            case STARGATE_DIAL_FINISHED:
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
        switch (stateType) {
            case STARGATE_UNIVERSE_ACTIVATE_SYMBOL:
                return new StargateUniverseSymbolState();

            default:
                return super.createState(stateType);
        }
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

                    switch (gateActionState.action) {
                        case CLEAR_CHEVRONS:
                            getRendererStateClient().clearSymbols(world.getTotalWorldTime());
                            break;

                        default:
                            break;
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
        this.lightUpChevronByIncoming(!AunisConfig.dialingConfig.allowIncomingAnimations);
    }

    @Override
    public void lightUpChevronByIncoming(boolean disableAnimation) {
        super.lightUpChevronByIncoming(disableAnimation);
        if (incomingPeriod == -1) return;
        if (!disableAnimation && incomingLastChevronLightUp == 1){
            stargateState = EnumStargateState.INCOMING;
            NBTTagCompound compound = new NBTTagCompound();
            int time = incomingPeriod - (8+7);
            compound.setInteger("period", time);
            addTask(new ScheduledTask(EnumScheduledTask.BEGIN_SPIN, 8+7, compound));
            addTask(new ScheduledTask(EnumScheduledTask.LIGHT_UP_CHEVRONS, 8));
            sendSignal(null, "stargate_incoming_wormhole", new Object[]{incomingAddressSize});
            playSoundEvent(StargateSoundEventEnum.INCOMING);
            super.resetIncomingAnimation();
            isIncoming = false;
            if (irisMode == EnumIrisMode.AUTO && isOpened()) {
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
                if (irisMode == EnumIrisMode.AUTO && isOpened()) {
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
    // Teleportation

    @Override
    protected AunisAxisAlignedBB getHorizonTeleportBox(boolean server) {
        return defaultStargateSize.teleportBox;
    }

    @Override
    public BlockPos getGateCenterPos() {
        return pos.up(4);
    }

    @Override
    protected AunisAxisAlignedBB getHorizonKillingBox(boolean server) {
        return defaultStargateSize.killingBox;
    }

    @Override
    protected int getHorizonSegmentCount(boolean server) {
        return defaultStargateSize.horizonSegmentCount;
    }

    @Override
    protected List<AunisAxisAlignedBB> getGateVaporizingBoxes(boolean server) {
        return defaultStargateSize.gateVaporizingBoxes;
    }

    // --------------------------------------------------------------------------------
    // Merging

    @Override
    public StargateAbstractMergeHelper getMergeHelper() {
        return StargateUniverseMergeHelper.INSTANCE;
    }

    @Override
    protected boolean onGateMergeRequested() {
        return StargateUniverseMergeHelper.INSTANCE.checkBlocks(world, pos, facing);
    }

    @Override
    public void setLinkedDHD(BlockPos dhdPos, int linkId) {}

    @Nonnull
    @Override
    protected StargateSizeEnum getStargateSize() {
        return defaultStargateSize;
    }


    // --------------------------------------------------------------------------------
    // Renderer states

    @Override
    protected StargateClassicRendererState.StargateClassicRendererStateBuilder getRendererStateServer() {
        return new StargateUniverseRendererState.StargateUniverseRendererStateBuilder(super.getRendererStateServer())
                .setDialedAddress((stargateState.initiating() || stargateState.dialing()) ? dialedAddress : new StargateAddressDynamic(getSymbolType()))
                .setActiveChevrons(stargateState.idle() ? 0 : 9);
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
    public int getSupportedCapacitors() {
        return AunisConfig.powerConfig.universeCapacitors;
    }


    // --------------------------------------------------------------------------------
    // NBTs

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        if (addressToDial != null) compound.setTag("addressToDial", addressToDial.serializeNBT());
        compound.setInteger("symbolsToDialCount", symbolsToDialCount);
        compound.setInteger("addressPosition", addressPosition);
        compound.setInteger("coolDown", coolDown);
        compound.setBoolean("fastDialing", isFastDialing);
        compound.setBoolean("abortingDialing", abortingDialing);

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        addressToDial = new StargateAddress(compound.getCompoundTag("addressToDial"));
        addressPosition = compound.getInteger("addressPosition");
        symbolsToDialCount = compound.getInteger("symbolsToDialCount");
        coolDown = compound.getInteger("coolDown");
        isFastDialing = compound.getBoolean("fastDialing");
        abortingDialing = compound.getBoolean("abortingDialing");
    }
}