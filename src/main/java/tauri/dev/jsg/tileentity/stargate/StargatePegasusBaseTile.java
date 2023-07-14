package tauri.dev.jsg.tileentity.stargate;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.ingame.JSGTileEntityConfig;
import tauri.dev.jsg.gui.container.stargate.StargateContainerGuiUpdate;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.renderer.dialhomedevice.DHDAbstractRendererState;
import tauri.dev.jsg.renderer.stargate.ChevronEnum;
import tauri.dev.jsg.renderer.stargate.StargateAbstractRendererState;
import tauri.dev.jsg.renderer.stargate.StargatePegasusRendererState;
import tauri.dev.jsg.sound.SoundEventEnum;
import tauri.dev.jsg.sound.SoundPositionedEnum;
import tauri.dev.jsg.sound.StargateSoundEventEnum;
import tauri.dev.jsg.sound.StargateSoundPositionedEnum;
import tauri.dev.jsg.stargate.*;
import tauri.dev.jsg.stargate.merging.StargateAbstractMergeHelper;
import tauri.dev.jsg.stargate.merging.StargatePegasusMergeHelper;
import tauri.dev.jsg.stargate.network.*;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.state.dialhomedevice.DHDActivateButtonState;
import tauri.dev.jsg.state.stargate.StargateBiomeOverrideState;
import tauri.dev.jsg.state.stargate.StargateRendererActionState;
import tauri.dev.jsg.state.stargate.StargateSpinState;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDAbstractTile;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDPegasusTile;
import tauri.dev.jsg.tileentity.util.ScheduledTask;
import tauri.dev.jsg.util.ILinkable;
import tauri.dev.jsg.util.LinkingHelper;

import javax.annotation.Nullable;
import java.util.*;

import static tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile.ConfigOptions.SPIN_GATE_INCOMING;

public class StargatePegasusBaseTile extends StargateClassicBaseTile implements ILinkable {

    // ------------------------------------------------------------------------
    // Stargate state

    @Override
    protected void disconnectGate() {
        super.disconnectGate();

        resetToDialSymbols();

        if (isLinkedAndDHDOperational()) getLinkedDHD(world).clearSymbols();
    }

    @Override
    protected void failGate() {
        super.failGate();

        resetToDialSymbols();

        if (isLinkedAndDHDOperational()) Objects.requireNonNull(getLinkedDHD(world)).clearSymbols();
    }

    @Override
    protected void dialingFailed(StargateOpenResult reason) {
        resetToDialSymbols();
        super.dialingFailed(reason);
    }

    @Override
    protected void addFailedTaskAndPlaySound() {
        continueDialing = false;
        markDirty();
        if (stargateState == EnumStargateState.DIALING || stargateState == EnumStargateState.DIALING_COMPUTER || stargateState == EnumStargateState.IDLE) {
            addTask(new ScheduledTask(EnumScheduledTask.STARGATE_FAIL, stargateState.dialingComputer() ? 83 : 53));
            playSoundEvent(StargateSoundEventEnum.DIAL_FAILED);
        }
    }


    // ------------------------------------------------------------------------
    // Stargate connection

    @Override
    public void openGate(StargatePos targetGatePos, boolean isInitiating) {
        super.openGate(targetGatePos, isInitiating);

        resetToDialSymbols();

        if (isLinkedAndDHDOperational()) {
            getLinkedDHD(world).activateSymbol(SymbolPegasusEnum.BBB);
        }
    }

    @Override
    public void activateDHDSymbolBRB() {
        if (isLinkedAndDHDOperational()) {
            getLinkedDHD(world).activateSymbol(SymbolPegasusEnum.BBB);
        }
    }

    public void clearDHDSymbols() {
        if (isLinkedAndDHDOperational()) getLinkedDHD(world).clearSymbols();
    }

    @Override
    public void setConfig(JSGTileEntityConfig config) {
        super.setConfig(config);
        if (isLinked()) {
            DHDAbstractTile dhd = getLinkedDHD(world);
            if (dhd != null) {
                DHDAbstractRendererState state = ((DHDAbstractRendererState) dhd.getState(StateTypeEnum.RENDERER_STATE));
                state.gateConfig = getConfig();
                dhd.sendState(StateTypeEnum.RENDERER_STATE, state);
            }
        }
    }

    // ------------------------------------------------------------------------
    // Stargate Network

    @Override
    public SymbolTypeEnum getSymbolType() {
        return SymbolTypeEnum.PEGASUS;
    }

    @Override
    protected int getMaxChevrons() {
        if(dialingWithoutEnergy) return 9;
        return isLinkedAndDHDOperational() && stargateState != EnumStargateState.DIALING_COMPUTER && !getLinkedDHD(world).hasUpgrade(DHDPegasusTile.DHDUpgradeEnum.CHEVRON_UPGRADE) ? 7 : 9;
    }

    @Override
    public void addSymbolToAddress(SymbolInterface symbol) {
        addSymbolToAddress(symbol, true);
    }

    public void addSymbolToAddress(SymbolInterface symbol, boolean activateSymbol) {

        if (isLinkedAndDHDOperational()) {
            if (activateSymbol) getLinkedDHD(world).activateSymbol((SymbolPegasusEnum) symbol);
        }

        super.addSymbolToAddress(symbol);
    }


    public void incomingWormhole(int dialedAddressSize) {
        super.incomingWormhole(dialedAddressSize);

        if (isLinkedAndDHDOperational()) {
            getLinkedDHD(world).clearSymbols();
        }

        startIncomingAnimation(dialedAddressSize, 300);
        markDirty();
    }

    public void incomingWormhole(int dialedAddressSize, int time) {
        super.incomingWormhole(dialedAddressSize);

        if (isLinkedAndDHDOperational()) {
            getLinkedDHD(world).clearSymbols();
        }
        startIncomingAnimation(dialedAddressSize, time);
        markDirty();
    }

    @Override
    public void startIncomingAnimation(int addressSize, int period) {
        super.startIncomingAnimation(addressSize, period);
        incomingPeriod = (int) Math.round((double) (incomingPeriod * addressSize) / 36);
        markDirty();
    }

    @Override
    protected void lightUpChevronByIncoming(boolean disableAnimation) {
        super.lightUpChevronByIncoming(disableAnimation);
        if (incomingPeriod == -1) return;

        if (!disableAnimation) {
            boolean spin = config.getOption(SPIN_GATE_INCOMING.id).getBooleanValue();

            int z = incomingLastChevronLightUp;

            if (z == 1 && spin) {
                addTask(new ScheduledTask(EnumScheduledTask.GATE_RING_ROLL, 15));
                playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL_START, true);
            }

            if (z % 4 == 0 && z > 0) {
                int chevron = z / 4;
                int[] pattern = {1, 2, 3, 7, 8, 4, 5, 6, 9}; // pattern of chevrons

                if (!stargateState.idle() && isIncoming) {
                    if (chevron < 9) {
                        if (pattern[chevron - 1] < incomingAddressSize) {
                            sendRenderingUpdate(StargateRendererActionState.EnumGateAction.CHEVRON_ACTIVATE, (pattern[chevron - 1] + 9), false);
                            playSoundEvent(StargateSoundEventEnum.INCOMING);
                        }
                    } else {
                        sendRenderingUpdate(StargateRendererActionState.EnumGateAction.CHEVRON_ACTIVATE, 0, true);
                        playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, false);
                        playSoundEvent(StargateSoundEventEnum.CHEVRON_OPEN);
                        resetIncomingAnimation();
                        markDirty();
                        return;
                    }
                } else {
                    isIncoming = false;
                    stargateState = EnumStargateState.IDLE;
                    addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CLEAR_CHEVRONS, 10));
                    playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, false);
                    resetIncomingAnimation();
                    markDirty();
                    return;
                }
            }
            if (spin)
                sendRenderingUpdate(StargateRendererActionState.EnumGateAction.ACTIVATE_GLYPH, z, false);
            else if (z == 1) {
                for (int i = 0; i < 36; i++)
                    sendRenderingUpdate(StargateRendererActionState.EnumGateAction.ACTIVATE_GLYPH, i, false);
            }
        } else {
            sendRenderingUpdate(StargateRendererActionState.EnumGateAction.LIGHT_UP_CHEVRONS, incomingAddressSize, false);
            playSoundEvent(StargateSoundEventEnum.INCOMING);
            isIncoming = false;
            resetIncomingAnimation();
            markDirty();
        }

        markDirty();
    }


    // ------------------------------------------------------------------------
    // Merging

    @Override
    public void onGateBroken() {
        super.onGateBroken();

        if (isLinked()) {
            getLinkedDHD(world).clearSymbols();
            getLinkedDHD(world).setLinkedGate(null, -1);
            setLinkedDHD(null, -1);
        }
    }

    @Override
    protected void onGateMerged() {
        super.onGateMerged();
        this.updateLinkStatus();
    }

    @Override
    public StargateAbstractMergeHelper getMergeHelper() {
        return StargatePegasusMergeHelper.INSTANCE;
    }


    // ------------------------------------------------------------------------
    // Linking

    private BlockPos linkedDHD = null;

    private int linkId = -1;

    @Override
    public boolean canLinkTo() {
        return isMerged() && !isLinked();
    }

    @Override
    public int getLinkId() {
        return linkId;
    }

    @Nullable
    public DHDPegasusTile getLinkedDHD(World world) {
        if (linkedDHD == null) return null;

        return (DHDPegasusTile) world.getTileEntity(linkedDHD);
    }

    public boolean isLinked() {
        return linkedDHD != null && world.getTileEntity(linkedDHD) instanceof DHDPegasusTile;
    }

    public boolean isLinkedAndDHDOperational() {
        if (!isLinked()) return false;

        DHDPegasusTile dhdTile = getLinkedDHD(world);
        if (dhdTile == null) return false;
        return dhdTile.hasControlCrystal();
    }

    public void setLinkedDHD(BlockPos dhdPos, int linkId) {
        this.linkedDHD = dhdPos;
        this.linkId = linkId;

        markDirty();
    }

    public void updateLinkStatus() {
        BlockPos closestDhd = LinkingHelper.findClosestUnlinked(world, pos, LinkingHelper.getDhdRange(), JSGBlocks.DHD_PEGASUS_BLOCK, this.getLinkId());
        int linkId = LinkingHelper.getLinkId();

        if (closestDhd != null) {
            DHDPegasusTile dhdTile = (DHDPegasusTile) world.getTileEntity(closestDhd);

            dhdTile.setLinkedGate(pos, linkId);
            setLinkedDHD(closestDhd, linkId);
            markDirty();
        }
    }

    // ------------------------------------------------------------------------
    // NBT

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        if (isLinked()) {
            compound.setLong("linkedDHD", linkedDHD.toLong());
            compound.setInteger("linkId", linkId);
        }

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("linkedDHD")) this.linkedDHD = BlockPos.fromLong(compound.getLong("linkedDHD"));
        if (compound.hasKey("linkId")) this.linkId = compound.getInteger("linkId");

        super.readFromNBT(compound);
        if (stargateState.engaged() || stargateState.incoming() || stargateState.unstable()) {
            for (int i = 0; i < 36; i++) {
                sendRenderingUpdate(StargateRendererActionState.EnumGateAction.ACTIVATE_GLYPH, i, false);
            }
        }
    }

    @Override
    public boolean prepare(ICommandSender sender, ICommand command) {
        setLinkedDHD(null, -1);

        return super.prepare(sender, command);
    }


    // ------------------------------------------------------------------------
    // Sounds

    @Override
    protected SoundPositionedEnum getPositionedSound(StargateSoundPositionedEnum soundEnum) {
        switch (soundEnum) {
            case GATE_RING_ROLL:
                return SoundPositionedEnum.PEGASUS_RING_ROLL;
            case GATE_RING_ROLL_START:
                return SoundPositionedEnum.PEGASUS_RING_ROLL_START;
        }

        return null;
    }

    @Override
    protected SoundEventEnum getSoundEvent(StargateSoundEventEnum soundEnum) {
        switch (soundEnum) {
            case OPEN:
                return SoundEventEnum.GATE_PEGASUS_OPEN;
            case CLOSE:
                return SoundEventEnum.GATE_MILKYWAY_CLOSE;
            case DIAL_FAILED:
                return SoundEventEnum.GATE_PEGASUS_DIAL_FAILED;
            case INCOMING:
                return SoundEventEnum.GATE_PEGASUS_INCOMING;
            case CHEVRON_OPEN:
            case CHEVRON_SHUT:
                return SoundEventEnum.GATE_PEGASUS_CHEVRON_OPEN;
        }

        return null;
    }


    // ------------------------------------------------------------------------
    // Ticking and loading

    @Override
    protected boolean onGateMergeRequested() {
        if (stargateSize != JSGConfig.Stargate.stargateSize) {
            StargatePegasusMergeHelper.INSTANCE.convertToPattern(world, pos, facing, facingVertical, stargateSize, JSGConfig.Stargate.stargateSize);
            stargateSize = JSGConfig.Stargate.stargateSize;
        }

        return StargatePegasusMergeHelper.INSTANCE.checkBlocks(world, pos, facing, facingVertical);
    }

    private BlockPos lastPos = BlockPos.ORIGIN;

    protected List<SymbolPegasusEnum> toDialSymbols = new ArrayList<>();
    protected EntityPlayer lastSender;

    public void resetToDialSymbols() {
        toDialSymbols.clear();
    }

    @Override
    protected boolean canAddSymbolInternal(SymbolInterface symbol) {
        if (dialedAddress.contains(symbol)) return false;

        return !((dialedAddress.size()) >= getMaxChevrons());
    }

    protected boolean continueDialing = false;

    @Override
    public void update() {
        super.update();

        if (!world.isRemote) {

            if ((toDialSymbols.size() > 0) && ((world.getTotalWorldTime() - lastSpinFinishedIn) > (continueDialing ? 2 : 10)) && stargateState.idle()) {
                if (toDialSymbols.get(0) == SymbolPegasusEnum.BBB || canAddSymbolInternal(toDialSymbols.get(0)))
                    addSymbolToAddressByList(toDialSymbols.get(0));
                if (toDialSymbols.size() > 0)
                    toDialSymbols.remove(0);
                markDirty();
            }
        }

        if (!lastPos.equals(pos)) {
            lastPos = pos;

            updateLinkStatus();
            markDirty();
        }
    }

    public static final EnumSet<BiomeOverlayEnum> SUPPORTED_OVERLAYS = EnumSet.of(BiomeOverlayEnum.NORMAL, BiomeOverlayEnum.FROST, BiomeOverlayEnum.MOSSY, BiomeOverlayEnum.AGED, BiomeOverlayEnum.SOOTY);

    @Override
    public EnumSet<BiomeOverlayEnum> getSupportedOverlays() {
        return SUPPORTED_OVERLAYS;
    }


    // ------------------------------------------------------------------------
    // Rendering

    @Override
    protected StargatePegasusRendererState.StargatePegasusRendererStateBuilder getRendererStateServer() {
        return (StargatePegasusRendererState.StargatePegasusRendererStateBuilder) new StargatePegasusRendererState.StargatePegasusRendererStateBuilder(super.getRendererStateServer()).setStargateSize(stargateSize);
    }

    @Override
    protected StargateAbstractRendererState createRendererStateClient() {
        return new StargatePegasusRendererState();
    }

    @Override
    public StargatePegasusRendererState getRendererStateClient() {
        return (StargatePegasusRendererState) super.getRendererStateClient();
    }


    // -----------------------------------------------------------------
    // States

    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {

            case RENDERER_STATE:
                return new StargatePegasusRendererState();

            case DHD_ACTIVATE_BUTTON:
                return new DHDActivateButtonState();

            case GUI_UPDATE:
                return new StargateContainerGuiUpdate();

            case BIOME_OVERRIDE_STATE:
                return new StargateBiomeOverrideState();

            default:
                return super.createState(stateType);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setState(StateTypeEnum stateType, State state) {
        if (getRendererStateClient() != null) {
            switch (stateType) {
                case RENDERER_UPDATE:
                    StargateRendererActionState gateActionState = (StargateRendererActionState) state;
                    switch (gateActionState.action) {
                        case CHEVRON_OPEN:
                            getRendererStateClient().openChevron(world.getTotalWorldTime());
                            break;

                        case CHEVRON_CLOSE:
                            getRendererStateClient().closeChevron(world.getTotalWorldTime());
                            break;
                        case CLEAR_CHEVRONS:
                            getRendererStateClient().clearChevrons(world.getTotalWorldTime());
                            return;
                        case ACTIVATE_GLYPH:
                            int slot = ((StargateRendererActionState) state).chevronCount;
                            slot = 36 - (slot - 9);
                            if (slot > 36) slot -= 36;
                            if (slot < 0) slot += 36;
                            if (getRendererStateClient() == null) break;
                            getRendererStateClient().setGlyphAtSlot(slot, slot);
                            break;

                        case CHEVRON_ACTIVATE:
                            getRendererStateClient().spinHelper.setIsSpinning(false);
                            if (((StargateRendererActionState) state).chevronCount <= 9) {
                                ChevronEnum chevron = gateActionState.modifyFinal ? ChevronEnum.getFinal() : getRendererStateClient().chevronTextureList.getNextChevron();
                                getRendererStateClient().lockChevron(getRendererStateClient().spinHelper.getTargetSymbol().getId(), chevron);
                            }

                            break;

                        default:
                            break;
                    }

                    break;

                case SPIN_STATE:
                    StargateSpinState spinState = (StargateSpinState) state;
                    if (spinState.setOnly) {
                        getRendererStateClient().spinHelper.setIsSpinning(false);
                        ((StargatePegasusSpinHelper) getRendererStateClient().spinHelper).setTargetSymbol(spinState.targetSymbol);
                    }

                    if (getRendererStateClient().chevronTextureList.getNextChevron().rotationIndex == 1) {
                        getRendererStateClient().slotToGlyphMap.clear();
                    }

                    break;

                default:
                    break;
            }
        }

        super.setState(stateType, state);
    }

    protected long getSpinStartOffset() {
        return slotFromChevron(getRendererStateClient().chevronTextureList.getCurrentChevron());
    }

    // TODO(sentialx): refactor
    public int slotFromChevron(ChevronEnum chevron) {
        return new int[]{9, 5, 1, 33, 29, 25, 21, 17, 13}[chevron.rotationIndex];
    }

    @Override
    public StargateOpenResult attemptOpenAndFail() {
        if (stargateState != EnumStargateState.INCOMING)
            playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, false);

        continueDialing = false;
        markDirty();

        return super.attemptOpenAndFail();
    }

    @Override
    public boolean canAddSymbol(SymbolInterface symbol) {
        if (dialedAddress.size() >= getMaxChevrons()) return false;

        return super.canAddSymbol(symbol);
    }

    public boolean canAddSymbolToList(SymbolInterface symbol) {
        int size = toDialSymbols.size();
        for (SymbolPegasusEnum s : toDialSymbols) {
            if (s.brb()) size--;
        }
        if (dialedAddress.size() + size + (this.stargateState.dialing() ? 1 : 0) >= getMaxChevrons()) return false;
        if (toDialSymbols.contains((SymbolPegasusEnum) symbol)) return false;

        return super.canAddSymbol(symbol);
    }


    public boolean dialAddress(StargateAddress address, int symbolCount, boolean withoutEnergy) {
        if (!getStargateState().idle()) return false;
        super.dialAddress(address, symbolCount, withoutEnergy);
        for (int i = 0; i < symbolCount; i++) {
            addSymbolToAddressDHD(address.get(i));
        }
        addSymbolToAddressDHD(getSymbolType().getOrigin());
        addSymbolToAddressDHD(SymbolPegasusEnum.BBB);
        return true;
    }

    public void addSymbolToAddressDHD(SymbolInterface targetSymbol, EntityPlayer sender) {
        lastSender = sender;
        addSymbolToAddressDHD(targetSymbol);
        markDirty();
    }

    public void addSymbolToAddressDHD(SymbolInterface targetSymbol) {
        if (targetSymbol != SymbolPegasusEnum.BBB && !canAddSymbolToList(targetSymbol)) return;
        if (!(targetSymbol instanceof SymbolPegasusEnum)) return;
        if (toDialSymbols.contains(targetSymbol)) return;
        if (isLinkedAndDHDOperational() && (targetSymbol != SymbolPegasusEnum.BBB || toDialSymbols.size() > 0)) {
            DHDAbstractTile dhd = getLinkedDHD(world);
            if (dhd != null)
                dhd.activateSymbol(targetSymbol);
        }
        toDialSymbols.add((SymbolPegasusEnum) targetSymbol);
    }

    public void addSymbolToAddressByList(SymbolInterface targetSymbol) {
        lastSpinFinishedIn = 0;
        if (((SymbolPegasusEnum) targetSymbol).brb()) {
            StargateOpenResult openResult = attemptOpenAndFail();
            if (openResult == StargateOpenResult.NOT_ENOUGH_POWER && lastSender != null)
                lastSender.sendStatusMessage(new TextComponentTranslation("tile.jsg.dhd_block.not_enough_power"), true);
            continueDialing = false;
            markDirty();
            return;
        }

        boolean fastDial = (getConfig().getOption(ConfigOptions.PEG_DIAL_ANIMATION.id).getEnumValue().getIntValue() == 1);
        boolean canContinueByConfig = (getConfig().getOption(ConfigOptions.PEG_DIAL_ANIMATION.id).getEnumValue().getIntValue() == 0);

        stargateState = EnumStargateState.DIALING;

        targetRingSymbol = targetSymbol;
        spinDirection = spinDirection.opposite();

        if (fastDial) {
            continueDialing = false;
            markDirty();

            JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.SPIN_STATE, new StargateSpinState(targetRingSymbol, EnumSpinDirection.CLOCKWISE, true, 0)), targetPoint);
            addTask(new ScheduledTask(EnumScheduledTask.STARGATE_SPIN_FINISHED, 5 + new Random().nextInt(5)));
            doIncomingAnimation(10, true);
            sendSignal(null, "stargate_dhd_chevron_engaged", new Object[]{dialedAddress.size(), stargateWillLock(targetRingSymbol), targetSymbol.getEnglishName()});
            return;
        }

        ChevronEnum targetChevron = stargateWillLock(targetSymbol, true) ? ChevronEnum.getFinal() : ChevronEnum.valueOf(dialedAddress.size());
        ChevronEnum currentChevron = dialedAddress.size() == 0 ? ChevronEnum.C1 : ChevronEnum.valueOf(targetChevron.index - 1);

        if (stargateWillLock(targetSymbol, true) && dialedAddress.size() == 6) currentChevron = ChevronEnum.C6;

        int indexDiff = slotFromChevron(currentChevron) - slotFromChevron(targetChevron);

        EnumSpinDirection counterDirection = indexDiff < 0 ? EnumSpinDirection.COUNTER_CLOCKWISE : EnumSpinDirection.CLOCKWISE;

        if (spinDirection == counterDirection) {
            indexDiff = 36 - Math.abs(indexDiff);
        }

        float distance = (float) Math.abs(indexDiff);
        if (distance <= 20) distance += 36;

        int duration = (int) (distance);
        doIncomingAnimation(duration, true, targetSymbol);

        JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.SPIN_STATE, new StargateSpinState(targetRingSymbol, spinDirection, false, 0)), targetPoint);
        lastSpinFinished = new ScheduledTask(EnumScheduledTask.STARGATE_SPIN_FINISHED, duration - 1);
        addTask(lastSpinFinished);
        if (!continueDialing) {
            addTask(new ScheduledTask(EnumScheduledTask.GATE_RING_ROLL, 15));
            playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL_START, true);
        }


        int toDialSize = toDialSymbols.size();
        if (toDialSymbols.contains(SymbolPegasusEnum.BBB))
            toDialSize--;
        continueDialing = (canContinueByConfig && (toDialSize >= 2) && (targetChevron != ChevronEnum.getFinal()));
        markDirty();

        isSpinning = true;
        spinStartTime = world.getTotalWorldTime();

        ringSpinContext = null;
        sendSignal(null, "stargate_dhd_chevron_engaged", new Object[]{dialedAddress.size(), stargateWillLock(targetRingSymbol), targetSymbol.getEnglishName()});
        markDirty();
    }

    @Override
    public void addSymbolToAddressManual(SymbolInterface targetSymbol, @Nullable Object context) {
        stargateState = EnumStargateState.DIALING_COMPUTER;

        targetRingSymbol = targetSymbol;

        boolean moveOnly = targetRingSymbol == currentRingSymbol;

        if (moveOnly) {
            addTask(new ScheduledTask(EnumScheduledTask.STARGATE_SPIN_FINISHED, 0));
            doIncomingAnimation(10, true);
        } else {
            spinDirection = spinDirection.opposite();

            ChevronEnum targetChevron = targetSymbol.origin() ? ChevronEnum.getFinal() : ChevronEnum.valueOf(dialedAddress.size());
            ChevronEnum currentChevron = dialedAddress.size() == 0 ? ChevronEnum.C1 : ChevronEnum.valueOf(targetChevron.index - 1);

            if (targetSymbol.origin() && dialedAddress.size() == 6) currentChevron = ChevronEnum.C6;

            int indexDiff = slotFromChevron(currentChevron) - slotFromChevron(targetChevron);

            EnumSpinDirection counterDirection = indexDiff < 0 ? EnumSpinDirection.COUNTER_CLOCKWISE : EnumSpinDirection.CLOCKWISE;

            if (spinDirection == counterDirection) {
                indexDiff = 36 - Math.abs(indexDiff);
            }

            float distance = (float) Math.abs(indexDiff);
            if (distance <= 20) distance += 36;

            int duration = (int) (distance);
            doIncomingAnimation(duration, true, targetRingSymbol);

            JSG.debug("addSymbolToAddressManual: " + "current:" + currentRingSymbol + ", " + "target:" + targetSymbol + ", " + "direction:" + spinDirection + ", " + "distance:" + distance + ", " + "duration:" + duration + ", " + "moveOnly:" + moveOnly);

            JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.SPIN_STATE, new StargateSpinState(targetRingSymbol, spinDirection, false, 0)), targetPoint);
            lastSpinFinished = new ScheduledTask(EnumScheduledTask.STARGATE_SPIN_FINISHED, duration - 1);
            addTask(lastSpinFinished);
            addTask(new ScheduledTask(EnumScheduledTask.GATE_RING_ROLL, 15));
            playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL_START, true);

            isSpinning = true;
            spinStartTime = world.getTotalWorldTime();

            ringSpinContext = context;
            if (context != null)
                sendSignal(context, "stargate_spin_start", new Object[]{dialedAddress.size(), stargateWillLock(targetRingSymbol), targetSymbol.getEnglishName()});
        }

        markDirty();
    }

    // -----------------------------------------------------------------
    // Scheduled tasks


    @Override
    public void executeTask(EnumScheduledTask scheduledTask, NBTTagCompound customData) {
        boolean onlySpin = false;
        if (customData != null && customData.hasKey("onlySpin"))
            onlySpin = customData.getBoolean("onlySpin");
        switch (scheduledTask) {
            case STARGATE_ACTIVATE_CHEVRON:
                stargateState = EnumStargateState.IDLE;
                markDirty();

                playSoundEvent(StargateSoundEventEnum.CHEVRON_OPEN);
                sendRenderingUpdate(StargateRendererActionState.EnumGateAction.CHEVRON_ACTIVATE, -1, isFinalActive);
                updateChevronLight(dialedAddress.size(), isFinalActive);
                //			JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.RENDERER_UPDATE, new StargateRendererActionState(EnumGateAction.CHEVRON_ACTIVATE, -1, customData.getBoolean("final"))), targetPoint);
                break;

            case STARGATE_SPIN_FINISHED:
                if (!onlySpin)
                    addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_OPEN, 0));
                else
                    stargateState = EnumStargateState.IDLE;

                markDirty();

                break;

            case STARGATE_CHEVRON_OPEN:
                playSoundEvent(StargateSoundEventEnum.CHEVRON_OPEN);
                if (!continueDialing)
                    playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, false);
                sendRenderingUpdate(StargateRendererActionState.EnumGateAction.CHEVRON_OPEN, 0, false);

                if (canAddSymbol(targetRingSymbol)) {
                    addSymbolToAddress(targetRingSymbol, stargateState == EnumStargateState.DIALING_COMPUTER);
                    addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_OPEN_SECOND, 0));
                } else addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_FAIL, 60));

                break;

            case STARGATE_CHEVRON_OPEN_SECOND:
                playSoundEvent(StargateSoundEventEnum.CHEVRON_OPEN);
                playSoundEvent(StargateSoundEventEnum.CHEVRON_OPEN);
                addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_LIGHT_UP, 0));

                break;

            case STARGATE_CHEVRON_LIGHT_UP:
                sendRenderingUpdate(StargateRendererActionState.EnumGateAction.CHEVRON_ACTIVATE, 0, stargateWillLock(targetRingSymbol));

                updateChevronLight(dialedAddress.size(), isFinalActive);

                addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_CLOSE, 1));

                break;

            case STARGATE_CHEVRON_CLOSE:
                if (stargateWillLock(targetRingSymbol)) {
                    stargateState = EnumStargateState.IDLE;
                    sendSignal(ringSpinContext, "stargate_spin_chevron_engaged", new Object[]{dialedAddress.size(), true, targetRingSymbol.getEnglishName()});
                } else addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_DIM, 1));

                break;

            case STARGATE_CHEVRON_DIM:
                //sendRenderingUpdate(EnumGateAction.CHEVRON_DIM, 0, false);
                stargateState = EnumStargateState.IDLE;

                sendSignal(ringSpinContext, "stargate_spin_chevron_engaged", new Object[]{dialedAddress.size(), false, targetRingSymbol.getEnglishName()});

                break;

            case STARGATE_CHEVRON_FAIL:
                sendRenderingUpdate(StargateRendererActionState.EnumGateAction.CHEVRON_CLOSE, 0, false);
                dialingFailed(checkAddressAndEnergy(dialedAddress));
                /*
                 *
                 * addTask(new ScheduledTask(EnumScheduledTask.STARGATE_ENGAGE, 0));
                 * addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_DIM, 1));
                 *
                 */

                break;

            case STARGATE_CLEAR_CHEVRONS:
                sendRenderingUpdate(StargateRendererActionState.EnumGateAction.CLEAR_CHEVRONS, 0, false);

            default:
                break;
        }

        super.executeTask(scheduledTask, customData);
    }

    @Override
    public int getDefaultCapacitors() {
        return 3;
    }
}
