package tauri.dev.jsg.tileentity.stargate;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.CapabilityItemHandler;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.beamer.BeamerLinkingHelper;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.chunkloader.ChunkManager;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.JSGConfigUtil;
import tauri.dev.jsg.config.ingame.*;
import tauri.dev.jsg.config.stargate.StargateDimensionConfig;
import tauri.dev.jsg.config.stargate.StargateSizeEnum;
import tauri.dev.jsg.config.stargate.StargateTimeLimitModeEnum;
import tauri.dev.jsg.gui.container.stargate.StargateContainerGuiState;
import tauri.dev.jsg.gui.container.stargate.StargateContainerGuiUpdate;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.item.energy.CapacitorItemBlock;
import tauri.dev.jsg.item.linkable.gdo.GDOMessages;
import tauri.dev.jsg.item.stargate.IrisItem;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdateRequestToServer;
import tauri.dev.jsg.power.general.EnergyRequiredToOperate;
import tauri.dev.jsg.power.general.LargeEnergyStorage;
import tauri.dev.jsg.power.general.SmallEnergyStorage;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.renderer.stargate.StargateClassicRenderer;
import tauri.dev.jsg.renderer.stargate.StargateClassicRendererState;
import tauri.dev.jsg.renderer.stargate.StargateClassicRendererState.StargateClassicRendererStateBuilder;
import tauri.dev.jsg.sound.JSGSoundHelper;
import tauri.dev.jsg.sound.SoundEventEnum;
import tauri.dev.jsg.sound.StargateSoundEventEnum;
import tauri.dev.jsg.sound.StargateSoundPositionedEnum;
import tauri.dev.jsg.stargate.*;
import tauri.dev.jsg.stargate.codesender.CodeSender;
import tauri.dev.jsg.stargate.codesender.CodeSenderType;
import tauri.dev.jsg.stargate.codesender.ComputerCodeSender;
import tauri.dev.jsg.stargate.network.*;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.state.stargate.StargateBiomeOverrideState;
import tauri.dev.jsg.state.stargate.StargateRendererActionState;
import tauri.dev.jsg.state.stargate.StargateSpinState;
import tauri.dev.jsg.tileentity.BeamerTile;
import tauri.dev.jsg.tileentity.util.IUpgradable;
import tauri.dev.jsg.tileentity.util.ScheduledTask;
import tauri.dev.jsg.util.*;
import tauri.dev.jsg.util.math.TemperatureHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static tauri.dev.jsg.stargate.EnumIrisType.IRIS_CREATIVE;
import static tauri.dev.jsg.stargate.EnumIrisType.IRIS_TITANIUM;
import static tauri.dev.jsg.stargate.EnumSpinDirection.CLOCKWISE;
import static tauri.dev.jsg.stargate.EnumSpinDirection.COUNTER_CLOCKWISE;
import static tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile.ConfigOptions.*;
import static tauri.dev.jsg.util.JSGAdvancementsUtil.tryTriggerRangedAdvancement;

/**
 * This class wraps common behavior for the fully-functional Stargates i.e.
 * all of them (right now) except Orlin's.
 *
 * @author MrJake222
 */
public abstract class StargateClassicBaseTile extends StargateAbstractBaseTile implements IUpgradable, ITileConfig {

    // IRIS/SHIELD VARIABLES/CONSTANTS
    private EnumIrisState irisState = EnumIrisState.OPENED;
    private EnumIrisType irisType = EnumIrisType.NULL;
    private String irisCode = "";
    protected EnumIrisMode irisMode = EnumIrisMode.OPENED;
    private long irisAnimation = 0;

    public int shieldKeepAlive = 0;

    private int irisDurability = 0;
    private int irisMaxDurability = 0;
    protected boolean isFinalActive;

    public boolean isFinalActive(){
        return isFinalActive;
    }

    public boolean isFastDialing;
    public boolean isFastDialingOverride;

    public boolean getFastDialState() {
        return isFastDialing;
    }

    public void setFastDial(boolean state) {
        if (stargateState.idle()) isFastDialing = state;
        markDirty();
    }

    protected double lastIrisHeat = -2;
    protected double lastGateHeat = -2;
    public double irisHeat;
    public double gateHeat;
    public static final double IRIS_MAX_HEAT_TITANIUM = JSGConfig.Stargate.iris.irisTitaniumMaxHeat;
    public static final double IRIS_MAX_HEAT_TRINIUM = JSGConfig.Stargate.iris.irisTriniumMaxHeat;
    public static final double GATE_MAX_HEAT = JSGConfig.Stargate.mechanics.gateMaxHeat;

    public void tryHeatUp(boolean byIrisHit, double irisHeatUpCoefficient) {
        tryHeatUp(byIrisHit, false, 1, irisHeatUpCoefficient, 1, -1, -1);
    }

    public void tryHeatUp(double gateHeatUpCoefficient) {
        tryHeatUp(false, true, gateHeatUpCoefficient, 1, 1, -1, -1);
    }

    public double getMaxIrisHeat() {
        if (hasShieldIris() || hasCreativeIris()) return Double.MAX_VALUE;
        return (getIrisType() == EnumIrisType.IRIS_TRINIUM ? IRIS_MAX_HEAT_TRINIUM : IRIS_MAX_HEAT_TITANIUM);
    }

    public double getMaxGateHeat(){
        return GATE_MAX_HEAT;
    }

    public void tryHeatUp(boolean heatUpIris, boolean heatUpGate, double gateHeatUpCoefficient, double irisHeatUpCoefficient, double coolDownCoefficient, double maxHeatByAround, double minHeatByAround) {

        final double heatUpCoefficientConst = 0.7;
        final double coolDownCoefficientConst = 0.3;

        if ((heatUpGate || Math.abs(gateHeat - irisHeat) >= 50) && (maxHeatByAround == -1 || (gateHeat + (heatUpCoefficientConst * gateHeatUpCoefficient)) <= maxHeatByAround))
            gateHeat += (heatUpCoefficientConst * gateHeatUpCoefficient);
        if ((minHeatByAround == -1 || (gateHeat - (coolDownCoefficientConst * coolDownCoefficient)) > minHeatByAround))
            gateHeat -= (coolDownCoefficientConst * coolDownCoefficient);

        if ((heatUpIris || Math.abs(gateHeat - irisHeat) >= 25) && (maxHeatByAround == -1 || (irisHeat + (heatUpCoefficientConst * irisHeatUpCoefficient)) <= maxHeatByAround))
            irisHeat += (heatUpCoefficientConst * irisHeatUpCoefficient);

        if ((minHeatByAround == -1 || (irisHeat - (coolDownCoefficientConst * coolDownCoefficient)) > minHeatByAround))
            irisHeat -= (coolDownCoefficientConst * coolDownCoefficient);

        // iris breaking
        ItemStack irisItem = getItemHandler().getStackInSlot(11);
        double maxHeat = getMaxIrisHeat();
        if (irisHeat >= maxHeat) {
            int heatCoefficient = (int) Math.round(Math.abs(irisHeat - maxHeat));
            if (JSGConfig.Stargate.iris.enableIrisOverHeatCollapse && heatCoefficient > 0) {
                if (world.getTotalWorldTime() % (((int) (Math.random() * 70)) + 1) == 0) {
                    if (hasPhysicalIris() && irisItem.isItemStackDamageable()) {
                        irisItem.getItem().setDamage(irisItem, irisItem.getItem().getDamage(irisItem) + (new Random().nextInt(heatCoefficient) + 1));
                        if (irisItem.getCount() == 0)
                            updateIrisType();
                        JSGSoundHelper.playSoundEvent(world, getGateCenterPos(), SoundEventEnum.IRIS_HIT);
                    }
                }
            }
        }

        // gate explosion
        if (gateHeat >= GATE_MAX_HEAT) {
            if (JSGConfig.Stargate.mechanics.enableGateOverHeatExplosion)
                world.newExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 60, false, true);
            //gateHeat = GATE_MAX_HEAT;
        }

        // send render update about temperature -> to color the gate red
        if (world.getTotalWorldTime() % 20 == 0) {// every second send a render_update packet to client if heat changed
            if (lastIrisHeat != irisHeat || lastGateHeat != gateHeat) {
                lastIrisHeat = irisHeat;
                lastGateHeat = gateHeat;
                markDirty();
                sendState(StateTypeEnum.RENDERER_UPDATE, new StargateRendererActionState(irisHeat, gateHeat));
            }
        }

        markDirty();
    }


    protected StargateSizeEnum stargateSize = JSGConfig.Stargate.stargateSize;

    /**
     * Returns stargate state either from config or from client's state.
     * THIS IS NOT A GETTER OF stargateSize.
     *
     * @param server Is the code running on server
     * @return Stargate's size
     */
    protected StargateSizeEnum getStargateSizeConfig(boolean server) {
        return server ? JSGConfig.Stargate.stargateSize : getRendererStateClient().stargateSize;
    }

    @Override
    public BlockPos getGateCenterPos() {
        if (stargateSize == StargateSizeEnum.EXTRA_LARGE)
            return pos.offset(EnumFacing.UP, 5);
        return pos.offset(EnumFacing.UP, 4);
    }

    @Nonnull
    protected StargateSizeEnum getStargateSize() {
        return stargateSize;
    }

    public StargateSizeEnum getStargateSizeForApi() {
        return getStargateSize();
    }


    // ------------------------------------------------------------------------
    // Killing and block vaporizing

    @Override
    protected JSGAxisAlignedBB getHorizonKillingBox(boolean server) {
        return getStargateSizeConfig(server).killingBox;
    }

    @Override
    protected int getHorizonSegmentCount(boolean server) {
        return getStargateSizeConfig(server).horizonSegmentCount;
    }

    @Override
    protected List<JSGAxisAlignedBB> getGateVaporizingBoxes(boolean server) {
        return getStargateSizeConfig(server).gateVaporizingBoxes;
    }

    @Override
    protected JSGAxisAlignedBB getHorizonTeleportBox(boolean server) {
        return getStargateSizeConfig(server).teleportBox;
    }

    @Override
    protected void engageGate() {
        super.engageGate();
        for (BlockPos beamerPos : linkedBeamers) {
            if (world.getTileEntity(beamerPos) != null)
                ((BeamerTile) Objects.requireNonNull(world.getTileEntity(beamerPos))).gateEngaged(targetGatePos);
        }
    }

    @Override
    public void closeGate(StargateClosedReasonEnum reason) {
        super.closeGate(reason);
        for (BlockPos beamerPos : linkedBeamers) {
            if (world.getTileEntity(beamerPos) != null)
                ((BeamerTile) Objects.requireNonNull(world.getTileEntity(beamerPos))).gateClosed();
        }
        resetACPreferences();
        markDirty();
    }

    @Override
    protected void disconnectGate() {
        super.disconnectGate();
        playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, false);
        if (irisMode == EnumIrisMode.AUTO && isIrisClosed()) toggleIris();
        isFinalActive = false;
        if (codeSender != null) codeSender = null;
        updateChevronLight(0, false);
        sendRenderingUpdate(StargateRendererActionState.EnumGateAction.CLEAR_CHEVRONS, dialedAddress.size(), isFinalActive);

        resetACPreferences();
        markDirty();
    }

    public boolean isGateBuried() {
        if (!getConfig().getOption(ENABLE_BURY_STATE.id).getBooleanValue()) return false;
        for (BlockPos targetPos : Objects.requireNonNull(StargateSizeEnum.getIrisBlocksPattern(getStargateSize()))) {
            BlockPos newPos = pos.add(FacingHelper.rotateBlock(targetPos, facing, facingVertical));
            IBlockState state = world.getBlockState(newPos);
            if (isLiquidBlock(false, state) || isLiquidBlock(true, state))
                return false;
            if (state.getMaterial() == Material.AIR || state.getBlock() == JSGBlocks.IRIS_BLOCK || state.getBlock() == JSGBlocks.INVISIBLE_BLOCK)
                return false;
        }
        return true;
    }

    @Override
    protected void failGate() {
        super.failGate();
        resetTargetIncomingAnimation();
        playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, false);

        isFinalActive = false;
        resetACPreferences();
        markDirty();

        if (stargateState != EnumStargateState.INCOMING && !isIncoming) {
            updateChevronLight(0, false);
            sendRenderingUpdate(StargateRendererActionState.EnumGateAction.CLEAR_CHEVRONS, dialedAddress.size(), isFinalActive);
        }
    }

    @Override
    protected ResultTargetValid attemptOpenDialed() {
        StargateOpenResult result = checkAddressAndEnergy(dialedAddress);
        boolean targetValid = result.ok();
        if (connectedToGatePos == null) {
            return new ResultTargetValid(StargateOpenResult.ADDRESS_MALFORMED, targetValid);
        }
        if (!(connectedToGatePos.getTileEntity().stargateState.incoming())) {
            return new ResultTargetValid(StargateOpenResult.CALLER_HUNG_UP, targetValid);
        }
        if (this.isGateBuried())
            return new ResultTargetValid(StargateOpenResult.GATE_BURIED, targetValid);
        return super.attemptOpenDialed();
    }

    @Override
    public void openGate(StargatePos targetGatePos, boolean isInitiating, boolean noxDialing) {
        super.openGate(targetGatePos, isInitiating, noxDialing);
        playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, false);
        tryHeatUp(8);
        resetACPreferences(false);
        markDirty();

        this.isFinalActive = true;
    }

    public boolean abortDialingSequence() {
        if (stargateState.incoming()) return false;
        if (isIncoming) return false;
        if (stargateState.dialingComputer() || stargateState.idle() || stargateState.dialing()) {
            spinStartTime = world.getTotalWorldTime() + 3000;
            isSpinning = false;
            currentRingSymbol = targetRingSymbol;
            sendState(StateTypeEnum.SPIN_STATE, new StargateSpinState(targetRingSymbol, spinDirection, true, 0));
            addFailedTaskAndPlaySound();
            playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, false);
            // remove last spinning finished task
            if (lastSpinFinished != null && scheduledTasks.contains(lastSpinFinished))
                removeTask(lastSpinFinished);
            failGate();
            if (!isIncoming) disconnectGate();
            resetACPreferences();
            markDirty();
            resetTargetIncomingAnimation();
            return true;
        }
        return false;
    }

    @Override
    public void generateIncoming(int entities, int addressSize, int delay) {
        if (this.isGateBuried()) return;
        super.generateIncoming(entities, addressSize, delay);
    }

    protected boolean dialingWithoutEnergy = false;

    public boolean isDialingWithoutEnergy(){
        return dialingWithoutEnergy;
    }
    protected void resetACPreferences(){
        resetACPreferences(true);
    }
    protected void resetACPreferences(boolean resetNox){
        dialingWithoutEnergy = false;
        isFastDialingOverride = false;
        if(resetNox)
            isNoxDialing = false;
        markDirty();
    }

    public boolean dialAddress(StargateAddress address, int maxSymbols, boolean withoutEnergy, EnumDialingType dialingType){
        dialingWithoutEnergy = withoutEnergy;
        if(dialingType == EnumDialingType.FAST) {
            isFastDialingOverride = true;
            setFastDial(true);
        }
        else if(dialingType == EnumDialingType.NOX){
            isNoxDialing = true;
        }
        markDirty();
        return true;
    }

    @Nullable
    @SuppressWarnings("all")
    protected void addSymbolToAddressByNox(SymbolInterface symbol){
        if (symbol != getSymbolType().getBRB() && !canAddSymbol(symbol)) return;
        if(symbol == getSymbolType().getBRB()){
            addTask(new ScheduledTask(EnumScheduledTask.STARGATE_OPEN_NOX));
            return;
        }
        addSymbolToAddressDHD(symbol);
        sendSignal(null, "stargate_dhd_chevron_engaged", new Object[]{dialedAddress.size(), isFinalActive, symbol.getEnglishName()});
    }

    @Override
    protected EnergyRequiredToOperate getEnergyRequiredToDial(StargatePos targetGatePos) {
        if(dialingWithoutEnergy)
            return EnergyRequiredToOperate.free();
        return super.getEnergyRequiredToDial(targetGatePos);
    }

    public abstract void addSymbolToAddressDHD(SymbolInterface symbol);

    @Override
    public void incomingWormhole(int dialedAddressSize) {
        incomingWormhole(dialedAddressSize, true);
    }

    public void incomingWormhole(int dialedAddressSize, boolean toggleIris) {
        if (irisMode == EnumIrisMode.AUTO && isIrisOpened() && toggleIris) {
            toggleIris();
        }
        super.incomingWormhole(dialedAddressSize);
        isFinalActive = true;
        updateChevronLight(dialedAddressSize, true);
    }

    @Override
    public void onGateBroken() {
        super.onGateBroken();
        updateChevronLight(0, false);
        if (irisType != EnumIrisType.NULL && irisState == EnumIrisState.CLOSED) {
            setIrisBlocks(false);
        }
        isSpinning = false;
        irisState = EnumIrisState.OPENED;
        irisType = EnumIrisType.NULL;
        currentRingSymbol = getSymbolType().getTopSymbol();
        sendState(StateTypeEnum.SPIN_STATE, new StargateSpinState(currentRingSymbol, spinDirection, true, 0));

        playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, false);
        ItemHandlerHelper.dropInventoryItems(world, pos, itemStackHandler);

        for (BlockPos beamerPos : linkedBeamers) {
            if (world.getTileEntity(beamerPos) != null) {
                BeamerTile beamerTile = (BeamerTile) world.getTileEntity(beamerPos);
                Objects.requireNonNull(beamerTile).setLinkedGate(null, null);
            }
        }

        linkedBeamers.clear();

    }

    @Override
    protected void onGateMerged() {
        super.onGateMerged();

        BeamerLinkingHelper.findBeamersInFront(world, pos, facing);
        updateBeamers();
        updateIrisType();
        double heat = TemperatureHelper.asKelvins(getTemperatureAroundGate()).toCelsius();
        gateHeat = heat;
        irisHeat = heat;
        markDirty();
    }


    // ------------------------------------------------------------------------
    // Loading and ticking


    @Override
    public void onLoad() {
        super.onLoad();
        if (!world.isRemote) {

            updateBeamers();
            updatePowerTier(true);

            updateIrisType();
            boolean set = irisType != EnumIrisType.NULL;
            if (isMerged()) {
                setIrisBlocks(set && irisState == EnumIrisState.CLOSED);
            }

            this.lastFakeWorld = getFakeWorld();
            this.lastFakePos = getFakePos();
            markDirty();
        }
    }


    protected abstract boolean onGateMergeRequested();

    private BlockPos lastPos = BlockPos.ORIGIN;

    /*
     * Stargate Incoming Animations Helper
     */

    protected int incomingAddressSize = -1;
    protected int incomingPeriod = -1;
    protected int incomingLastChevronLightUp = -1;

    /**
     * Begin incoming animation
     *
     * @param addressSize - how many chevrons should light up
     * @param period      - period in milliseconds
     */
    public void startIncomingAnimation(int addressSize, int period) {

        double ticks = (double) (period * 20) / 1000;
        incomingPeriod = (int) Math.round(ticks);
        incomingAddressSize = addressSize;
        incomingLastChevronLightUp = 0;
        stargateState = EnumStargateState.INCOMING;
        isIncoming = true;
        sendRenderingUpdate(StargateRendererActionState.EnumGateAction.CLEAR_CHEVRONS, 9, true);
        if (stargateState == EnumStargateState.DIALING_COMPUTER)
            abortDialingSequence();

        markDirty();
    }

    /**
     * Reset incoming animation state
     */
    public void resetIncomingAnimation() {
        incomingAddressSize = -1;
        incomingPeriod = -1;
        incomingLastChevronLightUp = -1;
        markDirty();
    }

    /**
     * Try to light up chevron/symbol
     */
    protected void lightUpChevronByIncoming(boolean disableAnimation) {
        if (!isIncoming) {
            if (incomingPeriod != -1) stargateState = EnumStargateState.IDLE;
            sendRenderingUpdate(StargateRendererActionState.EnumGateAction.CLEAR_CHEVRONS, 9, true);
            resetIncomingAnimation();
            markDirty();
            return;
        }

        if (stargateState.idle()) {
            stargateState = EnumStargateState.IDLE;
            markDirty();
            sendRenderingUpdate(StargateRendererActionState.EnumGateAction.CLEAR_CHEVRONS, 0, false);
            resetIncomingAnimation();
            return;
        }
        incomingLastChevronLightUp++;
    }

    /**
     * Try to run {@link #lightUpChevronByIncoming(boolean disableAnimation)}
     */
    public void tryRunIncoming(long ticks) {
        if (incomingPeriod == 0) incomingPeriod = 1;
        if (incomingPeriod != -1 && ticks % incomingPeriod == 0)
            lightUpChevronByIncoming(!getConfig().getOption(ALLOW_INCOMING.id).getBooleanValue());
    }

    private static boolean isLiquidBlock(boolean lava, IBlockState state) {
        return getLiquidBlockTemp(lava, state) > -1;
    }

    /**
     * @param lava  - searching for hot block?
     * @param state - state of target block
     * @return temperature in Kelvins
     */
    private static double getLiquidBlockTemp(boolean lava, IBlockState state) {
        Block block = state.getBlock();
        if (block instanceof BlockLiquid) {
            if (lava && block.getUnlocalizedName().equalsIgnoreCase("tile.lava"))
                return TemperatureHelper.asCelsius(1200).toKelvins();
            if (!lava && block.getUnlocalizedName().equalsIgnoreCase("tile.water"))
                return TemperatureHelper.asCelsius(3).toKelvins();
        }

        if (block instanceof BlockFluidBase) {
            BlockFluidBase liquid = (BlockFluidBase) block;
            boolean isHot = (liquid.getFluid().getTemperature() >= TemperatureHelper.asCelsius(1200).toKelvins());
            if ((isHot && lava) || (!isHot && !lava)) {
                return liquid.getFluid().getTemperature();
            }
        }
        return -1;
    }

    public double getAroundGateLiquid(boolean lava, boolean getTemperature) {
        return getTemperatureAroundGate((lava ? 1 : -1), getTemperature);
    }

    /**
     * @param type           - specifies type of search -> 1: hot; 0: air; -1: cold
     * @param getTemperature - return sum of temperatures of all blocks?
     */
    public double getTemperatureAroundGate(int type, boolean getTemperature) {
        boolean lava = (type == 1);
        boolean air = (type == 0);
        double suma = 0;
        // check chevron blocks
        for (BlockPos chevron : getMergeHelper().getChevronBlocks()) {
            chevron = FacingHelper.rotateBlock(chevron, facing, facingVertical).add(pos);
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos newPos = chevron.add(facing.getDirectionVec());
                IBlockState state = world.getBlockState(newPos);
                Block block = state.getBlock();
                if (air) {
                    if (world.isAirBlock(newPos)) {
                        if (getTemperature)
                            suma += TemperatureHelper.asCelsius(world.getBiome(pos).getTemperature(pos) * 30).toKelvins();
                        else
                            suma++;
                    }
                } else if (isLiquidBlock(lava, state)) {
                    if (getTemperature)
                        suma += getLiquidBlockTemp(lava, state);
                    else
                        suma++;
                } else if (!lava && (block == Blocks.ICE || block == Blocks.SNOW || block == Blocks.PACKED_ICE)) {
                    if (getTemperature)
                        suma += (block == Blocks.SNOW ? TemperatureHelper.asCelsius(3).toKelvins() : TemperatureHelper.asCelsius(-3).toKelvins());
                    else
                        suma++;
                }
            }
        }

        // check ring blocks
        for (BlockPos ring : getMergeHelper().getRingBlocks()) {
            ring = FacingHelper.rotateBlock(ring, facing, facingVertical).add(pos);
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos newPos = ring.add(facing.getDirectionVec());
                IBlockState state = world.getBlockState(newPos);
                Block block = state.getBlock();
                if (air) {
                    if (world.isAirBlock(newPos)) {
                        if (getTemperature)
                            suma += TemperatureHelper.asCelsius(world.getBiome(pos).getTemperature(pos) * 30).toKelvins();
                        else
                            suma++;
                    }
                } else if (isLiquidBlock(lava, state)) {
                    if (getTemperature)
                        suma += getLiquidBlockTemp(lava, state);
                    else
                        suma++;
                } else if (!lava && (block == Blocks.ICE || block == Blocks.SNOW || block == Blocks.PACKED_ICE)) {
                    if (getTemperature)
                        suma += (block == Blocks.SNOW ? TemperatureHelper.asCelsius(3).toKelvins() : TemperatureHelper.asCelsius(-3).toKelvins());
                    else
                        suma++;
                }
            }
        }

        // check base block
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos newPos = pos.add(facing.getDirectionVec());
            IBlockState state = world.getBlockState(newPos);
            Block block = state.getBlock();
            if (air) {
                if (world.isAirBlock(newPos)) {
                    if (getTemperature)
                        suma += TemperatureHelper.asCelsius(world.getBiome(pos).getTemperature(pos) * 30).toKelvins();
                    else
                        suma++;
                }
            } else if (isLiquidBlock(lava, state)) {
                if (getTemperature)
                    suma += getLiquidBlockTemp(lava, state);
                else
                    suma++;
            } else if (!lava && (block == Blocks.ICE || block == Blocks.SNOW || block == Blocks.PACKED_ICE)) {
                if (getTemperature)
                    suma += (block == Blocks.SNOW ? TemperatureHelper.asCelsius(3).toKelvins() : TemperatureHelper.asCelsius(-3).toKelvins());
                else
                    suma++;
            }
        }
        return suma;
    }

    /**
     * @return temperature of air/blocks/liquids around the gate
     */
    public double getTemperatureAroundGate() {
        double lavaCount = getAroundGateLiquid(true, false);
        double waterCount = getAroundGateLiquid(false, false);
        double airCount = getTemperatureAroundGate(0, false);

        double total = lavaCount + waterCount + airCount;

        double maxTemperature = getAroundGateLiquid(true, true);
        double minTemperature = getAroundGateLiquid(false, true);
        double airTemp = getTemperatureAroundGate(0, true);

        double totalTemp = maxTemperature + minTemperature + airTemp;

        return TemperatureHelper.asCelsius(((total > 0) ? TemperatureHelper.asKelvins((totalTemp / total)).toCelsius() : 25)).toKelvins();
    }

    @Override
    public boolean getForceUnstable(){
        return (getConfig().getOption(FORCE_UNSTABLE_EH.id).getBooleanValue() || (targetGatePos != null && (targetGatePos.getTileEntity() instanceof StargateClassicBaseTile) && ((StargateClassicBaseTile) targetGatePos.getTileEntity()).getConfig().getOption(FORCE_UNSTABLE_EH.id).getBooleanValue()));
    }

    @Override
    public void update() {
        if (world.isRemote) {
            // Client -> request to update client config
            if (getConfig().getOptions().size() < 1) {
                JSGPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.GUI_STATE));
            }
        }

        // Checking lastFakePos and lastFakeWorld (if changed, close the gate if its open (gate was probably warped))
        if(!world.isRemote){
            if ((lastFakePos != getFakePos() || lastFakeWorld != getFakeWorld())) {
                if (!getStargateState().engaged() && !getStargateState().unstable() && !getStargateState().idle() && (connectedToGate || connectingToGate)) {
                    abortDialingSequence();
                    lastFakePos = getFakePos();
                    lastFakeWorld = getFakeWorld();
                    markDirty();
                }
                else if (getStargateState().engaged() || getStargateState().unstable()) {
                    JSG.info("A stargateState indicates the Gate should be open, but gate was warped! Closing gate...");
                    attemptClose(StargateClosedReasonEnum.CONNECTION_LOST);
                    lastFakePos = getFakePos();
                    lastFakeWorld = getFakeWorld();
                    markDirty();
                }
                else{
                    lastFakePos = getFakePos();
                    lastFakeWorld = getFakeWorld();
                    markDirty();
                }
            }
        }

        // Fast dialing
        if (!world.isRemote) {
            boolean shouldBeFast = getConfig().getOption(ENABLE_FAST_DIAL.id).getBooleanValue();
            if (!isFastDialingOverride && isFastDialing != shouldBeFast && getStargateState().idle()) {
                setFastDial(shouldBeFast);
            }
        }

        // Charging gate with lighting bold
        if (!world.isRemote && isMerged()) {
            BlockPos topBlockPos = getMergeHelper().getTopBlockAboveBase().add(pos);
            if (world.getWorldInfo().isThundering() && world.getWorldInfo().isRaining() && BlockHelpers.isBlockDirectlyUnderSky(world, topBlockPos)) {
                Random rand = new Random();
                float chance = rand.nextFloat();
                if (chance < JSGConfig.Stargate.mechanics.lightingBoldChance) {
                    boolean canStrike = true;
                    int dim = this.world.provider.getDimension();
                    for(int i : JSGConfig.Stargate.mechanics.lightingStrikeDisabledDims){
                        if(dim == i){
                            canStrike = false;
                            break;
                        }
                    }
                    if(canStrike){
                        int max = JSGConfig.Stargate.power.stargateEnergyStorage / 17;
                        int min = max / 6;
                        int energy = (int) ((rand.nextFloat() * (max - min)) + min);
                        getEnergyStorage().receiveEnergy(energy, false);
                        world.addWeatherEffect(new EntityLightningBolt(world, topBlockPos.getX(), topBlockPos.getY(), topBlockPos.getZ(), false));
                    }
                }
            }
        }


        /*
         * =========================================================================
         * HEATING UP System
         */
        if (!world.isRemote && isMerged()) {

            double middleTemperature = TemperatureHelper.asKelvins(getTemperatureAroundGate()).toCelsius();

            double cc = Math.min(Math.abs(gateHeat / (middleTemperature * 2)), 0.5);

            double c = Math.sin(cc) * 0.7 + 0.05;

            tryHeatUp(false, true, c, c, c, middleTemperature, middleTemperature);
            if (!hasIris()) {
                irisHeat = -1;
                markDirty();
            }
        }


        String RIG_PREFIX = "[RIG] at " + pos.toString() + ":: ";

        // Stargate Incoming Animations Timer
        if (!world.isRemote)
            tryRunIncoming(world.getTotalWorldTime());

        /*
         * =========================================================================
         * Stargate Random Incoming Generator (RIG)
         */
        if (!world.isRemote) {
            initConfig();

            Random rand = new Random();
            if (world.getDifficulty() != EnumDifficulty.PEACEFUL && config.getOption(ALLOW_RIG.id).getBooleanValue() && world.isAreaLoaded(pos, 10)) {
                if (world.getTotalWorldTime() % 200 == 0) { // every 10 seconds
                    int chanceToRandom = rand.nextInt(1000);

                    //if chance && stargate state is idle or dialing by DHD and RANDOM INCOMING IS NOT ACTIVATED YET
                    if (chanceToRandom <= JSGConfig.Stargate.rig.chance) {
                        int entities = rand.nextInt(25);
                        int delay = rand.nextInt(200);
                        if (this instanceof StargateUniverseBaseTile) {
                            delay = rand.nextInt(300);
                            if (delay < 120) delay = 120;
                        }
                        if (delay < 80) delay = 80;
                        if (entities < 3) entities = 3;

                        generateIncoming(entities, 7, delay); // execute
                        JSG.debug(RIG_PREFIX + "Stargate at " + pos.toString() + " generated RIG!");
                    }
                }
            }

            if (randomIncomingIsActive) {

                int wait = 4 * 20;
                int waitOpen = randomIncomingOpenDelay + 20;
                if (waitOpen < 80) waitOpen = 80;

                if (isMerged()) {
                    if (randomIncomingState == 0) { // incoming wormhole
                        if (canAcceptConnectionFrom(null)) {
                            randomIncomingState++;
                            int period = (((waitOpen) / 20) * 1000) / randomIncomingAddrSize;
                            stargateState = EnumStargateState.INCOMING;
                            isIncoming = true;
                            if (connectedToGate) {
                                StargateAbstractBaseTile tGate;
                                if (!dialedAddress.contains(getSymbolType().getOrigin()))
                                    dialedAddress.addOrigin();
                                tGate = Objects.requireNonNull(network.getStargate(dialedAddress)).getTileEntity();
                                if (tGate != null) {
                                    tGate.stargateState = EnumStargateState.IDLE;
                                    tGate.markDirty();
                                }
                                connectedToGate = false;
                                connectingToGate = false;
                            }
                            markDirty();
                            if (getConfig().getOption(ALLOW_INCOMING.id).getBooleanValue())
                                this.incomingWormhole(randomIncomingAddrSize, period);
                            else this.incomingWormhole(randomIncomingAddrSize);
                            this.sendSignal(null, "stargate_incoming_wormhole", new Object[]{randomIncomingAddrSize});
                            this.failGate();
                            JSG.debug(RIG_PREFIX + "Incoming!");
                        } else resetRandomIncoming();
                    } else if (randomIncomingState < waitOpen) { // wait waitOpen ticks to open gate
                        if (!stargateState.engaged() && !stargateState.unstable()) {
                            stargateState = EnumStargateState.INCOMING;
                            randomIncomingState++;
                        } else resetRandomIncoming();
                    } else if (randomIncomingState == waitOpen) { // open gate
                        if (!stargateState.engaged() && !stargateState.unstable() && stargateState.incoming()) {
                            randomIncomingState++;
                            targetGatePos = null;
                            setOpenedSince();

                            ChunkManager.forceChunk(world, new ChunkPos(pos));

                            sendRenderingUpdate(StargateRendererActionState.EnumGateAction.OPEN_GATE, 0, false);

                            addTask(new ScheduledTask(EnumScheduledTask.STARGATE_OPEN_SOUND, getOpenSoundDelay()));
                            addTask(new ScheduledTask(EnumScheduledTask.STARGATE_HORIZON_LIGHT_BLOCK, EnumScheduledTask.STARGATE_OPEN_SOUND.waitTicks + 19 + getTicksPerHorizonSegment(true)));
                            addTask(new ScheduledTask(EnumScheduledTask.STARGATE_HORIZON_WIDEN, EnumScheduledTask.STARGATE_OPEN_SOUND.waitTicks + 23 + getTicksPerHorizonSegment(true))); // 1.3s of the sound to the kill
                            addTask(new ScheduledTask(EnumScheduledTask.STARGATE_ENGAGE));

                            sendSignal(null, "stargate_open", new Object[]{false});

                            // activate DHD brb
                            activateDHDSymbolBRB();

                            markDirty();
                            JSG.debug(RIG_PREFIX + "Opening!");

                            this.isFinalActive = true;
                        } else resetRandomIncoming();
                    } else if (randomIncomingState < (waitOpen + wait)) {
                        randomIncomingState++;
                    } else if (randomIncomingState >= (waitOpen + wait) && randomIncomingEntities > 0 && (stargateState == EnumStargateState.ENGAGED || stargateState == EnumStargateState.INCOMING)) {
                        randomIncomingState++;

                        // Load entities
                        String[] entityListString = JSGConfig.Stargate.rig.entitiesToSpawn;
                        List<Entity> entityList = new ArrayList<>();
                        for (String entityString : entityListString) {
                            ResourceLocation rlString = new ResourceLocation(entityString);
                            entityList.add(EntityList.createEntityByIDFromName(rlString, world));
                        }


                        int randomDelay = new Random().nextInt(16);
                        if (randomDelay <= 0) randomDelay = 1;
                        if (randomIncomingState % (5 * randomDelay) == 0) {
                            randomIncomingEntities--;
                            int posX = this.getGateCenterPos().getX();
                            int posY = this.getGateCenterPos().getY();
                            int posZ = this.getGateCenterPos().getZ();
                            // create entity
                            Entity mobEntity = new EntityZombie(world);

                            int entitiesLength = entityList.size();
                            if (entitiesLength > 0) {
                                int randomEntity = rand.nextInt(entitiesLength);
                                if (entityList.get(randomEntity) != null)
                                    mobEntity = entityList.get(randomEntity);
                            }
                            mobEntity.setLocationAndAngles(posX, posY, posZ, 0, 0);
                            if (isIrisOpened() || irisType.equals(EnumIrisType.NULL)) {
                                // spawn zombie
                                this.world.spawnEntity(mobEntity);
                                JSGSoundHelper.playSoundEvent(world, getGateCenterPos(), SoundEventEnum.WORMHOLE_GO);

                                JSG.debug(RIG_PREFIX + "Spawned " + mobEntity.getName());
                            } else {
                                // do iris shit

                                if (hasPhysicalIris()) {
                                    JSGSoundHelper.playSoundEvent(world,
                                            getGateCenterPos(),
                                            SoundEventEnum.IRIS_HIT);
                                } else if (hasShieldIris()) {
                                    JSGSoundHelper.playSoundEvent(world,
                                            getGateCenterPos(),
                                            SoundEventEnum.SHIELD_HIT);
                                }
                                tryTriggerRangedAdvancement(this, JSGAdvancementsUtil.EnumAdvancementType.IRIS_IMPACT);
                                ItemStack irisItem = getItemHandler().getStackInSlot(11);
                                if (irisItem.getItem() instanceof IrisItem) {
                                    // different damages per source
                                    int chance = EnchantmentHelper.getEnchantments(irisItem).containsKey(Enchantments.UNBREAKING) ? (JSGConfig.Stargate.iris.unbreakingChance * EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, irisItem)) : 0;
                                    int random = rand.nextInt(100);

                                    if (random > chance) {
                                        JSGItems.UPGRADE_IRIS.setDamage(irisItem, JSGItems.UPGRADE_IRIS.getDamage(irisItem) + 1);
                                    }
                                    if (irisItem.getCount() == 0) {
                                        updateIrisType();
                                    }
                                } else {
                                    IEnergyStorage energyStorage = getCapability(CapabilityEnergy.ENERGY, null);
                                    if (energyStorage != null) {
                                        energyStorage.extractEnergy(500, false);
                                    }
                                }
                                JSG.debug(RIG_PREFIX + mobEntity.getName() + " hit iris!");
                                sendSignal(null, "stargate_event_iris_hit", new Object[]{"Something just hit the IRIS!"});
                            }

                        }
                    } else if ((randomIncomingEntities <= 0 && randomIncomingState >= (waitOpen + wait)) || stargateState != EnumStargateState.ENGAGED) {
                        resetRandomIncoming();
                        attemptClose(StargateClosedReasonEnum.AUTOCLOSE);
                        JSG.debug(RIG_PREFIX + "Closed!!!");

                        clearDHDSymbols();
                    }

                    markDirty();
                } else resetRandomIncoming();
            }
        }

        /*
         * =========================================================================
         */


        /*
         * Draw power (shield)
         */
        extractEnergyByShield(0);
        if (!world.isRemote && hasShieldIris()) {
            shieldKeepAlive = JSGConfig.Stargate.iris.shieldPowerDraw;
            shieldKeepAlive += (int) (irisHeat * (irisHeat / IRIS_MAX_HEAT_TRINIUM));
            if (isIrisClosed()) extractEnergyByShield(shieldKeepAlive);
            if (getEnergyStorage().getEnergyStored() < shieldKeepAlive) {
                toggleIris();
                sendSignal(null, "stargate_iris_out_of_power", new Object[]{"Shield runs out of power! Opening shield..."});
            } else if (irisMode == EnumIrisMode.CLOSED && isIrisOpened()) {
                toggleIris();
            }
        }

        super.update();

        if (!world.isRemote) {


            if (!lastPos.equals(pos)) {
                lastPos = pos;
                //generateAddresses(true);

                if (isMerged()) {
                    updateMergeState(onGateMergeRequested(), facing, facingVertical);
                }
            }

            if (givePageTask != null) {
                if (givePageTask.update(world.getTotalWorldTime())) {
                    givePageTask = null;
                }
            }

            if (doPageProgress) {
                if (world.getTotalWorldTime() % 2 == 0) {
                    pageProgress++;

                    if (pageProgress > 18) {
                        pageProgress = 0;
                        doPageProgress = false;
                    }
                }

                if (itemStackHandler.getStackInSlot(pageSlotId).isEmpty()) {
                    lockPage = false;
                    doPageProgress = false;
                    pageProgress = 0;
                    givePageTask = null;
                }
            } else {
                if (lockPage && itemStackHandler.getStackInSlot(pageSlotId).isEmpty()) {
                    lockPage = false;
                }

                if (!lockPage) {
                    for (int i = 7; i < 10; i++) {
                        if (!itemStackHandler.getStackInSlot(i).isEmpty()) {
                            doPageProgress = true;
                            lockPage = true;
                            pageSlotId = i;
                            givePageTask = new ScheduledTask(EnumScheduledTask.STARGATE_GIVE_PAGE, 36);
                            givePageTask.setTaskCreated(world.getTotalWorldTime());
                            givePageTask.setExecutor(this);

                            break;
                        }
                    }
                }
            }

            if (!(isIrisClosed() || isIrisOpened()) && (world.getTotalWorldTime() - irisAnimation) > (hasPhysicalIris() ? StargateClassicRenderer.PHYSICAL_IRIS_ANIMATION_LENGTH : StargateClassicRenderer.SHIELD_IRIS_ANIMATION_LENGTH)) {
                switch (irisState) {
                    case OPENING:
                        irisState = EnumIrisState.OPENED;

                        sendRenderingUpdate(StargateRendererActionState.EnumGateAction.IRIS_UPDATE, 0, false, irisType, irisState, irisAnimation);
                        if (afterIrisDone != null) afterIrisDone.run();
                        afterIrisDone = null;
                        sendSignal(null, "stargate_iris_opened", new Object[]{"Iris is opened"});
                        break;
                    case CLOSING:
                        irisState = EnumIrisState.CLOSED;
                        setIrisBlocks(true);
                        sendRenderingUpdate(StargateRendererActionState.EnumGateAction.IRIS_UPDATE, 0, false, irisType, irisState, irisAnimation);
                        if (afterIrisDone != null) afterIrisDone.run();
                        afterIrisDone = null;
                        sendSignal(null, "stargate_iris_closed", new Object[]{"Iris is closed"});
                        break;
                    default:
                        break;
                }
                markDirty();
            }
            // update iris blocks each 5 ticks
            if(isIrisClosed() && hasIris() && world.getTotalWorldTime() % 5 == 0){
                setIrisBlocks(true);
            }

        } else {
            // Client

            // Each 2s check for the biome overlay
            if (world.getTotalWorldTime() % 40 == 0 && rendererStateClient != null) {
                if (getRendererStateClient().biomeOverride == null)
                    rendererStateClient.setBiomeOverlay(getBiomeOverlayWithOverride(false));
//               if (getRendererStateClient().irisType != EnumIrisType.NULL
//                       && (getRendererStateClient().irisType != irisType || getRendererStateClient().irisState != irisState)) {
//                   sendRenderingUpdate(EnumGateAction.IRIS_UPDATE, 0, false, irisType, irisState, irisAnimation);
//                }
            }
        }
    }

    @Override
    protected void kawooshDestruction() {
        if (!isIrisClosed() || irisType == EnumIrisType.NULL) super.kawooshDestruction();
    }

    // Server
    private BiomeOverlayEnum determineBiomeOverride() {
        ItemStack stack = itemStackHandler.getStackInSlot(BIOME_OVERRIDE_SLOT);

        if (stack.isEmpty()) {
            return null;
        }

        BiomeOverlayEnum biomeOverlay = JSGConfigUtil.getBiomeOverrideItemMetaPairs().get(new ItemMetaPair(stack));

        if (getSupportedOverlays().contains(biomeOverlay)) {
            return biomeOverlay;
        }

        return null;
    }

    @Override
    public BiomeOverlayEnum getBiomeOverlayWithOverride(boolean override) {
        BiomeOverlayEnum overlay = null;

        // TODO(Mine): Fix this shit
        /*if (gateHeat < (JSGConfig.stargateConfig.frostyTemperatureThreshold * 30))
            overlay = BiomeOverlayEnum.FROST;
        if (!getSupportedOverlays().contains(overlay))
            overlay = null;*/

        if (override) overlay = determineBiomeOverride();

        if (overlay == null) return super.getBiomeOverlayWithOverride(override);
        return overlay;
    }

    @Override
    protected boolean shouldAutoclose() {
        boolean beamerActive = false;

        for (BlockPos beamerPos : linkedBeamers) {
            if (world.getTileEntity(beamerPos) != null) {
                BeamerTile beamerTile = (BeamerTile) world.getTileEntity(beamerPos);
                beamerActive = beamerTile.isActive();
            }

            if (beamerActive) break;
        }

        return !beamerActive && super.shouldAutoclose();
    }

    @Override
    protected boolean canAddSymbolInternal(SymbolInterface symbol) {
        if (isFinalActive) return false;
        return super.canAddSymbolInternal(symbol);
    }

    // ------------------------------------------------------------------------
    // NBT

    @Override
    protected void setWorldCreate(World world) {
        setWorld(world);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setInteger("stargateSize", stargateSize.id);
        compound.setTag("itemHandler", itemStackHandler.serializeNBT());
        compound.setBoolean("isFinalActive", isFinalActive);

        compound.setBoolean("isSpinning", isSpinning);
        compound.setLong("spinStartTime", spinStartTime);
        compound.setInteger("currentRingSymbol", currentRingSymbol.getId());
        compound.setInteger("targetRingSymbol", targetRingSymbol.getId());
        compound.setInteger("spinDirection", spinDirection.id);

        NBTTagList linkedBeamersTagList = new NBTTagList();
        for (BlockPos vect : linkedBeamers)
            linkedBeamersTagList.appendTag(new NBTTagLong(vect.toLong()));
        compound.setTag("linkedBeamers", linkedBeamersTagList);
        if (irisState == null) {
            if (codeSender != null) {
                codeSender.sendMessage(GDOMessages.OPENED.textComponent);
                codeSender = null;
            }
            irisState = EnumIrisState.OPENED;
        }
        compound.setByte("irisState", irisState.id);
        compound.setString("irisCode", irisCode);
        compound.setByte("irisMode", irisMode.id);
        compound.setByte("irisType", irisType.id);
        if (codeSender != null && !world.isRemote) {
            compound.setTag("codeSender", codeSender.serializeNBT());
        }

        compound.setInteger("powerTier", currentPowerTier);

        compound.setInteger("incomingLastChevronLightUp", incomingLastChevronLightUp);
        compound.setInteger("incomingPeriod", incomingPeriod);
        compound.setInteger("incomingAddressSize", incomingAddressSize);

        compound.setTag("config", getConfig().serializeNBT());

        compound.setDouble("irisHeat", irisHeat);
        compound.setDouble("lastIrisHeat", lastIrisHeat);
        compound.setDouble("gateHeat", gateHeat);
        compound.setDouble("lastGateHeat", lastGateHeat);

        compound.setBoolean("fastDialing", isFastDialing);
        compound.setBoolean("isFastDialingOverride", isFastDialingOverride);
        compound.setBoolean("isNoxDialing", isNoxDialing);

        compound.setInteger("facingVertical", FacingHelper.toInt(facingVertical));

        if (lastFakePos != null)
            compound.setLong("lastFakePos", lastFakePos.toLong());
        if (lastFakeWorld != null)
            compound.setInteger("lastFakeWorld", lastFakeWorld.provider.getDimension());

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        if (compound.hasKey("patternVersion")) stargateSize = StargateSizeEnum.SMALL;
        else {
            if (compound.hasKey("stargateSize"))
                stargateSize = StargateSizeEnum.fromId(compound.getInteger("stargateSize"));
            else stargateSize = JSGConfig.Stargate.stargateSize;
        }

        itemStackHandler.deserializeNBT(compound.getCompoundTag("itemHandler"));

        isFinalActive = compound.getBoolean("isFinalActive");

        isSpinning = compound.getBoolean("isSpinning");
        spinStartTime = compound.getLong("spinStartTime");
        currentRingSymbol = getSymbolType().valueOfSymbol(compound.getInteger("currentRingSymbol"));
        targetRingSymbol = getSymbolType().valueOfSymbol(compound.getInteger("targetRingSymbol"));
        spinDirection = EnumSpinDirection.valueOf(compound.getInteger("spinDirection"));

        for (NBTBase tag : compound.getTagList("linkedBeamers", NBT.TAG_LONG))
            linkedBeamers.add(BlockPos.fromLong(((NBTTagLong) tag).getLong()));


        irisState = EnumIrisState.getValue(compound.getByte("irisState"));
        irisCode = !compound.getString("irisCode").equals("") ? compound.getString("irisCode") : "";
        irisMode = EnumIrisMode.getValue(compound.getByte("irisMode"));
        irisType = EnumIrisType.byId(compound.getByte("irisType"));
        if (compound.hasKey("codeSender") && !world.isRemote) {
            NBTTagCompound nbt = compound.getCompoundTag("codeSender");
            codeSender = codeSenderFromNBT(nbt);
        }

        currentPowerTier = compound.getInteger("powerTier");

        incomingPeriod = compound.getInteger("incomingPeriod");
        incomingLastChevronLightUp = compound.getInteger("incomingLastChevronLightUp");
        incomingAddressSize = compound.getInteger("incomingAddressSize");

        getConfig().deserializeNBT(compound.getCompoundTag("config"));

        this.irisHeat = compound.getDouble("irisHeat");
        this.lastIrisHeat = compound.getDouble("lastIrisHeat");
        this.gateHeat = compound.getDouble("gateHeat");
        this.lastGateHeat = compound.getDouble("lastGateHeat");

        this.isFastDialing = compound.getBoolean("fastDialing");
        this.isFastDialingOverride = compound.getBoolean("isFastDialingOverride");
        this.isNoxDialing = compound.getBoolean("isNoxDialing");

        facingVertical = FacingHelper.fromInt(compound.getInteger("facingVertical"));

        if (compound.hasKey("lastFakePos"))
            this.lastFakePos = BlockPos.fromLong(compound.getLong("lastFakePos"));
        if (compound.hasKey("lastFakeWorld") && world.getMinecraftServer() != null)
            this.lastFakeWorld = this.world.getMinecraftServer().getWorld(compound.getInteger("lastFakeWorld"));

        super.readFromNBT(compound);
    }

    private CodeSender codeSenderFromNBT(NBTTagCompound compound) {
        codeSender = CodeSenderType.fromId(compound.getInteger("type")).constructor.get();
        switch (codeSender.getType()) {
            case PLAYER:
                codeSender.prepareToLoad(new Object[]{world});
                break;
            case COMPUTER:
                codeSender.prepareToLoad(null);
                break;

        }
        codeSender.deserializeNBT(compound);
        return codeSender;
    }

    // -----------------------------------------------------------------
    // Tile entity config

    protected JSGTileEntityConfig config = new JSGTileEntityConfig();

    public enum ConfigOptions implements ITileConfigEntry {
        ALLOW_INCOMING(
                0, "allowIncomingAnim", JSGConfigOptionTypeEnum.BOOLEAN, JSGConfig.Stargate.visual.allowIncomingAnimations + "",
                "Enable incoming animation",
                "on this gate"
        ),
        DHD_TOP_LOCK(
                1, "dhdLockPoO", JSGConfigOptionTypeEnum.BOOLEAN, JSGConfig.DialHomeDevice.visual.dhdLastOpen + "",
                "Enable opening last chevron",
                "while dialing Milky Way gate with dhd",
                " - ONLY FOR MW GATES - "
        ),
        ENABLE_FAST_DIAL(
                2, "enableFastDial", JSGConfigOptionTypeEnum.BOOLEAN, JSGConfig.Stargate.visual.enableFastDialing + "",
                "Enable fast dialing",
                " - ONLY FOR UNI GATES NOW - "
        ),
        ALLOW_RIG(
                3, "enableRIG", JSGConfigOptionTypeEnum.BOOLEAN, JSGConfig.Stargate.rig.enableRandomIncoming + "",
                "Enable random incoming",
                "generator on this gate"
        ),
        ENABLE_DHD_PRESS_SOUND(
                4, "dhdPressSound", JSGConfigOptionTypeEnum.BOOLEAN, JSGConfig.DialHomeDevice.audio.computerDialSound + "",
                "Enable DHD press sound",
                "when dialing with OC"
        ),
        CAPACITORS_COUNT(
                5, "maxCapacitors", JSGConfigOptionTypeEnum.NUMBER, "3", 0, 3,
                "Specifies how many",
                "capacitors can be installed",
                "into this gate"
        ),
        PEG_DIAL_ANIMATION(
                6, "pegDialAnim", "1", // default value here is index of value in array below
                new ArrayList<JSGConfigEnumEntry>() {{
                    add(new JSGConfigEnumEntry("Slow", "-1"));
                    add(new JSGConfigEnumEntry("Normal", "0"));
                    add(new JSGConfigEnumEntry("Fast", "1"));
                }}, "Speed of Pegasus gate dialing with DHD"
        ),
        SPIN_GATE_INCOMING(
                7, "incomingSpin", JSGConfigOptionTypeEnum.BOOLEAN, "true",
                "Enable ring spin",
                "animation while incoming animation",
                "occurs"
        ),
        ORIGIN_MODEL(
                8, "originModel", "0", // default value here is index of value in array below
                new ArrayList<JSGConfigEnumEntry>() {{
                    add(new JSGConfigEnumEntry("[by overlay]", "-1"));
                    add(new JSGConfigEnumEntry("Default", "0"));
                    add(new JSGConfigEnumEntry("P7J-989", "1"));
                    add(new JSGConfigEnumEntry("Nether", "2"));
                    add(new JSGConfigEnumEntry("Antarctica", "3"));
                    add(new JSGConfigEnumEntry("Abydos", "4"));
                    add(new JSGConfigEnumEntry("Tauri", "5"));
                    for (String poo : JSGConfig.Stargate.pointOfOrigins.additionalOrigins) {
                        String name = poo.split(":")[1];
                        String value = poo.split(":")[0];
                        add(new JSGConfigEnumEntry(name, value));
                    }
                }},
                "Override point of origin model",
                " - ONLY FOR MW ADDRESS/GATE - "
        ),
        ENABLE_BURY_STATE(
                9, "enableBuryState", JSGConfigOptionTypeEnum.BOOLEAN, JSGConfig.Stargate.mechanics.enableBuriedState + "",
                "Enable bury state for the gate?"
        ),
        TIME_LIMIT_MODE(
                10, "timeLimitMode", JSGConfig.Stargate.openLimit.maxOpenedWhat.id + "",
                new ArrayList<JSGConfigEnumEntry>() {{
                    add(new JSGConfigEnumEntry(StargateTimeLimitModeEnum.DISABLED.name, StargateTimeLimitModeEnum.DISABLED.id + ""));
                    add(new JSGConfigEnumEntry(StargateTimeLimitModeEnum.CLOSE_GATE.name, StargateTimeLimitModeEnum.CLOSE_GATE.id + ""));
                    add(new JSGConfigEnumEntry(StargateTimeLimitModeEnum.DRAW_MORE_POWER.name, StargateTimeLimitModeEnum.DRAW_MORE_POWER.id + ""));
                }},
                "Gate open time limit mode"
        ),
        TIME_LIMIT_TIME(
                11, "timeLimitTime", JSGConfigOptionTypeEnum.NUMBER, JSGConfig.Stargate.openLimit.maxOpenedSeconds + "",
                0, -1,
                "Seconds of gate's open time limit."
        ),
        TIME_LIMIT_POWER(
                12, "timeLimitPower", JSGConfigOptionTypeEnum.NUMBER, JSGConfig.Stargate.openLimit.maxOpenedPowerDrawAfterLimit + "",
                0, -1,
                "Power draw when gate runs",
                "out of open time limit.",
                " - TIME LIMIT MODE MUST BE SET TO \"DRAW_POWER\" - "
        ),
        STARGATE_SPIN_SPEED(
                13, "spinSpeed", JSGConfigOptionTypeEnum.NUMBER, "100",
                1, 300,
                "Speed factor of the gate",
                " - ONLY FOR MW AND UNI GATES - "
        ),
        FORCE_UNSTABLE_EH(
                14, "forceUnstable", JSGConfigOptionTypeEnum.BOOLEAN, "false",
                "Force unstable state on this gate?"
        ),
        UNIVERSE_ORANGE_SHIELD(
                15, "universeOrangeShield", JSGConfigOptionTypeEnum.BOOLEAN, "true",
                "Should Universe gate's shield be orange?"
        );

        public final int id;
        public final String label;
        public final String[] comment;
        public final JSGConfigOptionTypeEnum type;
        public final String defaultValue;
        public List<JSGConfigEnumEntry> possibleValues;

        public final int minInt;
        public final int maxInt;

        ConfigOptions(int optionId, String label, String defaultValue, List<JSGConfigEnumEntry> possibleValues, String... comment) {
            this(optionId, label, JSGConfigOptionTypeEnum.SWITCH, defaultValue, comment);
            this.possibleValues = possibleValues;
        }

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
        JSGConfigOption o = getConfig().getOption(CAPACITORS_COUNT.id, true);
        int caps = ((o == null || o.defaultValue.equals(o.getStringValue()) ? getDefaultCapacitors() : o.getIntValue()));
        JSGTileEntityConfig.initConfig(getConfig(), ConfigOptions.values());
        getConfig().getOption(CAPACITORS_COUNT.id).setDefaultValue(getDefaultCapacitors() + "").setValue(caps + "");
        markDirty();
    }


    // ------------------------------------------------------------------------
    // Rendering

    protected void updateChevronLight(int lightUp, boolean isFinalActive) {
        if (isFinalActive) lightUp--;

        for (int i = 0; i < 9; i++) {
            BlockPos chevPos = FacingHelper.rotateBlock(getMergeHelper().getChevronBlocks().get(i), facing, facingVertical).add(pos);

            if (getMergeHelper().matchMember(world.getBlockState(chevPos))) {
                StargateClassicMemberTile memberTile = (StargateClassicMemberTile) world.getTileEntity(chevPos);
                if (memberTile != null) {
                    memberTile.setLitUp(i == 8 ? isFinalActive : lightUp > i);
                }
            }
        }
    }

    @Override
    protected StargateClassicRendererStateBuilder getRendererStateServer() {
        return new StargateClassicRendererStateBuilder(super.getRendererStateServer())
                .setSymbolType(getSymbolType())
                .setActiveChevrons(dialedAddress.size())
                .setFinalActive(isFinalActive)
                .setCurrentRingSymbol(currentRingSymbol)
                .setSpinDirection(spinDirection)
                .setSpinning(isSpinning)
                .setTargetRingSymbol(targetRingSymbol)
                .setSpinStartTime(spinStartTime)
                .setBiomeOverride(determineBiomeOverride())
                .setIrisState(irisState)
                .setIrisType(irisType)
                .setIrisMode(irisMode)
                .setIrisCode(irisCode)
                .setIrisAnimation(irisAnimation);
    }

    @Override
    public StargateClassicRendererState getRendererStateClient() {
        return (StargateClassicRendererState) super.getRendererStateClient();
    }

    public static final JSGAxisAlignedBB RENDER_BOX = new JSGAxisAlignedBB(-5.5, 0, -0.5, 5.5, 10.5, 0.5);
    public static final JSGAxisAlignedBB RENDER_BOX_LARGE = new JSGAxisAlignedBB(-7.5, 0, -0.5, 7.5, 12.5, 0.5);

    @Override
    protected JSGAxisAlignedBB getRenderBoundingBoxRaw() {
        if (getStargateSize() == StargateSizeEnum.EXTRA_LARGE)
            return RENDER_BOX_LARGE;
        return RENDER_BOX;
    }

    protected long getSpinStartOffset() {
        return 0;
    }

    // -----------------------------------------------------------------
    // States

    @Override
    public State getState(StateTypeEnum stateType) {
        switch (stateType) {
            case GUI_STATE:
                return new StargateContainerGuiState(gateAddressMap, getConfig());

            case GUI_UPDATE:
                return new StargateContainerGuiUpdate(energyStorage.getEnergyStoredInternally(), energyTransferedLastTick, energySecondsToClose, this.irisMode, this.irisCode, this.openedSince, this.gateHeat, this.irisHeat);

            default:
                return super.getState(stateType);
        }
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {
            case GUI_STATE:
                return new StargateContainerGuiState();

            case GUI_UPDATE:
                return new StargateContainerGuiUpdate();

            case SPIN_STATE:
                return new StargateSpinState();

            case BIOME_OVERRIDE_STATE:
                return new StargateBiomeOverrideState();

            default:
                return super.createState(stateType);
        }
    }

    @Override
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {
            case RENDERER_UPDATE:
                if (getRendererStateClient() == null) break;
                StargateRendererActionState gateActionState = (StargateRendererActionState) state;

                switch (gateActionState.action) {
                    case CHEVRON_ACTIVATE:
                        if (gateActionState.modifyFinal)
                            getRendererStateClient().chevronTextureList.activateFinalChevron(world.getTotalWorldTime());
                        else
                            getRendererStateClient().chevronTextureList.activateNextChevron(world.getTotalWorldTime(), gateActionState.chevronCount);

                        break;


                    case CLEAR_CHEVRONS:

                        getRendererStateClient().clearChevrons(world.getTotalWorldTime());
                        break;

                    case LIGHT_UP_CHEVRONS:
                        getRendererStateClient().chevronTextureList.lightUpChevrons(world.getTotalWorldTime(), gateActionState.chevronCount);
                        break;

                    case CHEVRON_ACTIVATE_BOTH:
                        getRendererStateClient().chevronTextureList.activateNextChevron(world.getTotalWorldTime());
                        getRendererStateClient().chevronTextureList.activateFinalChevron(world.getTotalWorldTime());
                        break;

                    case CHEVRON_DIM:
                        getRendererStateClient().chevronTextureList.deactivateFinalChevron(world.getTotalWorldTime());
                        break;

                    case IRIS_UPDATE:
                        getRendererStateClient().irisState = gateActionState.irisState;
                        getRendererStateClient().irisType = gateActionState.irisType;
                        if (gateActionState.irisState == EnumIrisState.CLOSING || gateActionState.irisState == EnumIrisState.OPENING) {
                            getRendererStateClient().irisAnimation = world.getTotalWorldTime();
                        }
                        break;
                    case HEAT_UPDATE:
                        getRendererStateClient().irisHeat = gateActionState.irisHeat;
                        getRendererStateClient().gateHeat = gateActionState.gateHeat;
                        this.irisHeat = gateActionState.irisHeat;
                        this.gateHeat = gateActionState.gateHeat;
                        markDirty();
                        break;


                    default:
                        break;
                }

                break;

            case GUI_STATE:
                StargateContainerGuiState guiState = (StargateContainerGuiState) state;
                gateAddressMap = guiState.gateAdddressMap;
                config = guiState.config;
                markDirty();
                break;

            case GUI_UPDATE:
                StargateContainerGuiUpdate guiUpdate = (StargateContainerGuiUpdate) state;
                energyStorage.setEnergyStoredInternally(guiUpdate.energyStored);
                energyTransferedLastTick = guiUpdate.transferedLastTick;
                energySecondsToClose = guiUpdate.secondsToClose;
                irisMode = guiUpdate.irisMode;
                irisCode = guiUpdate.irisCode;
                openedSince = guiUpdate.openedSince;
                gateHeat = guiUpdate.gateTemp;
                irisHeat = guiUpdate.irisTemp;
                markDirty();
                break;

            case SPIN_STATE:
                if (getRendererStateClient() == null) break;
                StargateSpinState spinState = (StargateSpinState) state;
                if (spinState.setOnly) {
                    getRendererStateClient().spinHelper.setIsSpinning(false);
                    getRendererStateClient().spinHelper.setCurrentSymbol(spinState.targetSymbol);
                } else {
                    int speed = getConfig().getOption(STARGATE_SPIN_SPEED.id).getIntValue();
                    getRendererStateClient().spinHelper.initRotation(speed / 100f, world.getTotalWorldTime(), spinState.targetSymbol, spinState.direction, getSpinStartOffset(), spinState.plusRounds);
                }

                break;

            case BIOME_OVERRIDE_STATE:
                StargateBiomeOverrideState overrideState = (StargateBiomeOverrideState) state;

                if (rendererStateClient != null) {
                    getRendererStateClient().biomeOverride = overrideState.biomeOverride;
                }

                break;
            default:
                break;
        }

        super.setState(stateType, state);
    }


    // -----------------------------------------------------------------
    // Scheduled tasks

    protected long lastSpinFinishedIn = 0;

    @Override
    public void executeTask(EnumScheduledTask scheduledTask, NBTTagCompound customData) {
        boolean fastDial = false;
        boolean onlySpin = false;
        if (customData != null) {
            if (customData.hasKey("fastDial"))
                fastDial = customData.getBoolean("fastDial");
            if (customData.hasKey("onlySpin"))
                onlySpin = customData.getBoolean("onlySpin");
        }
        switch (scheduledTask) {
            case STARGATE_HORIZON_LIGHT_BLOCK:
                if (irisType == EnumIrisType.NULL || irisType == EnumIrisType.SHIELD || !isIrisClosed()) {
                    super.executeTask(scheduledTask, customData);
                } else if (isIrisClosed()) {
                    world.getBlockState(getGateCenterPos()).getBlock().setLightLevel(.7f);
                }
                break;
            case STARGATE_CLOSE:
                if (irisType == EnumIrisType.NULL || !isIrisClosed()) {
                    super.executeTask(scheduledTask, customData);
                } else if (isIrisClosed()) {
                    world.getBlockState(getGateCenterPos()).getBlock().setLightLevel(0);
                    disconnectGate();
                }
                break;
            case STARGATE_OPEN_NOX:
                // Debug chevrons for clients
                sendRenderingUpdate(StargateRendererActionState.EnumGateAction.LIGHT_UP_CHEVRONS, dialedAddress.size(), false);

                attemptOpenAndFail();
                break;

            case STARGATE_SPIN_FINISHED:
                lastSpinFinishedIn = world.getTotalWorldTime();
                if (fastDial) break;
                isSpinning = false;
                currentRingSymbol = targetRingSymbol;
                if (!(this instanceof StargatePegasusBaseTile) || !((StargatePegasusBaseTile) this).continueDialing)
                    playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, false);

                playSoundEvent(StargateSoundEventEnum.CHEVRON_SHUT);
                playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL_START, false);

                markDirty();
                break;

            case STARGATE_GIVE_PAGE:
                if(pageSlotId < 7) break;
                SymbolTypeEnum symbolType = SymbolTypeEnum.valueOf(pageSlotId - 7);
                ItemStack stack = itemStackHandler.getStackInSlot(pageSlotId);
                stack = getAddressPage(symbolType, stack, hasUpgrade(StargateUpgradeEnum.CHEVRON_UPGRADE), false, false);
                itemStackHandler.setStackInSlot(pageSlotId, stack);

                break;

            case GATE_RING_ROLL:
                if (!stargateState.idle() || onlySpin)
                    playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, true);
                break;

            default:
                super.executeTask(scheduledTask, customData);
        }
    }
    public static int getOriginId(BiomeOverlayEnum overlay, int dimId, int configOrigin) {
        if (configOrigin >= 0) return configOrigin;

        if (overlay == null) overlay = BiomeOverlayEnum.NORMAL;

        int override = StargateDimensionConfig.getOrigin(DimensionManager.getProviderType(dimId), overlay);
        if (override >= 0)
            return override;

        switch (overlay) {
            case FROST:
                return 3;
            case AGED:
                return 4;
            case SOOTY:
                return 2;
            case NORMAL:
                if (dimId == 0) return 5;
                return 0;
            default:
                break;
        }
        return 0;
    }

    public int getOriginId() {
        return getOriginId(getBiomeOverlayWithOverride(true), getFakeWorld().provider.getDimension(), getConfig().getOption(ORIGIN_MODEL.id).getEnumValue().getIntValue());
    }


    // ------------------------------------------------------------------------
    // Ring spinning

    protected boolean isSpinning;

    @Override
    public boolean isSpinning(){
        return isSpinning;
    }
    protected long spinStartTime;
    protected SymbolInterface currentRingSymbol = getSymbolType().getTopSymbol();
    protected SymbolInterface targetRingSymbol = getSymbolType().getTopSymbol();
    protected EnumSpinDirection spinDirection = COUNTER_CLOCKWISE;
    protected Object ringSpinContext;

    public void addSymbolToAddressManual(SymbolInterface targetSymbol, @Nullable Object context) {
        int soundSpinWait = 5;
        if (this instanceof StargateUniverseBaseTile)
            soundSpinWait = 10;
        targetRingSymbol = targetSymbol;

        boolean moveOnly = targetRingSymbol == currentRingSymbol;
        int plusRounds = 0;

        spinDirection = spinDirection.opposite();
        float distance = spinDirection.getDistance(currentRingSymbol, targetRingSymbol);
        if (moveOnly) {
            plusRounds += 1;
        }

        if (!JSGConfig.Stargate.visual.fasterMWGateDial) {
            if (distance < 90 && plusRounds == 0) {
                plusRounds += 1;
            }
            if (distance < 270 && plusRounds == 0 && getConfig().getOption(ALLOW_INCOMING.id).getBooleanValue()) {
                if (targetRingSymbol == targetRingSymbol.getSymbolType().getOrigin()) {
                    plusRounds += 1;
                }
            }
        } else if (distance > 180) {
            spinDirection = spinDirection.opposite();
            distance = spinDirection.getDistance(currentRingSymbol, targetRingSymbol);
            plusRounds = 0;
        }

        distance += (plusRounds * 360);


        float speedFactor = getConfig().getOption(STARGATE_SPIN_SPEED.id).getIntValue() / 100f;
        int duration = StargateClassicSpinHelper.getAnimationDuration(speedFactor, distance);
        doIncomingAnimation(duration, true, targetRingSymbol);

        sendState(StateTypeEnum.SPIN_STATE, new StargateSpinState(targetRingSymbol, spinDirection, false, plusRounds));
        lastSpinFinished = new ScheduledTask(EnumScheduledTask.STARGATE_SPIN_FINISHED, duration - 5);
        addTask(lastSpinFinished);
        addTask(new ScheduledTask(EnumScheduledTask.GATE_RING_ROLL, soundSpinWait));
        playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL_START, true);

        isSpinning = true;
        spinStartTime = world.getTotalWorldTime();

        ringSpinContext = context;
        if (context != null)
            sendSignal(context, "stargate_spin_start", new Object[]{dialedAddress.size(), stargateWillLock(targetRingSymbol), targetSymbol.getEnglishName()});

        markDirty();
    }

    public void spinRing(int rounds, boolean changeState, boolean findNearest, int time) {
        if (time < 0) time *= -1;
        time -= 20; // time to lock the last chevron
        targetRingSymbol = currentRingSymbol;
        spinDirection = CLOCKWISE;
        if (changeState) stargateState = EnumStargateState.DIALING_COMPUTER;
        if (rounds == 0)
            rounds = 1;
        if (rounds < 0) {
            spinDirection = COUNTER_CLOCKWISE;
            rounds *= -1;
        }

        float speedFactor = getConfig().getOption(STARGATE_SPIN_SPEED.id).getIntValue() / 100f;

        float distance = 360 * rounds;
        if (findNearest) { // spinRing() was called by incoming wormhole -> do animation
            rounds = 0;
            float currentAngle = currentRingSymbol.getAngle();
            float angle = StargateClassicSpinHelper.getAnimationDistance(speedFactor, time);
            if (angle > 360) {
                rounds = (int) Math.floor(angle / 360);
                angle = angle - (rounds * 360);
            }
            float finalAngle = currentAngle - angle;
            if (spinDirection == CLOCKWISE)
                finalAngle = angle + currentAngle;

            if (finalAngle > 360)
                finalAngle -= 360;
            else if (finalAngle < 0)
                finalAngle += 360;

            // CALCULATE NEAREST GLYPH
            if (finalAngle < 15 && rounds == 0)
                finalAngle += 15;
            float nearestAngle = getSymbolType().getAngleOfNearest(finalAngle);
            targetRingSymbol = getSymbolType().getSymbolByAngle(nearestAngle);
            distance = spinDirection.getDistance(currentRingSymbol, targetRingSymbol);
            distance += 360 * rounds;
        }


        // set to renderer that its not normal spinning and the chevron will not lock
        NBTTagCompound compound = new NBTTagCompound();
        compound.setBoolean("onlySpin", true);

        int duration = StargateClassicSpinHelper.getAnimationDuration(speedFactor, distance);

        if (targetPoint != null)
            sendState(StateTypeEnum.SPIN_STATE, new StargateSpinState(targetRingSymbol, spinDirection, false, rounds));
        if (stargateState.incoming()) {
            stargateState = EnumStargateState.INCOMING;
            markDirty();
            addTask(new ScheduledTask(EnumScheduledTask.STARGATE_SPIN_FINISHED, duration - 5, compound));
        } else {
            lastSpinFinished = new ScheduledTask(EnumScheduledTask.STARGATE_SPIN_FINISHED, duration - 5, compound);
            addTask(lastSpinFinished);
        }
        addTask(new ScheduledTask(EnumScheduledTask.GATE_RING_ROLL, 5));
        playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL_START, true);

        isSpinning = true;
        spinStartTime = world.getTotalWorldTime();

        ringSpinContext = null;
        sendSignal(null, "stargate_spin_start", new Object[]{dialedAddress.size(), false, targetRingSymbol.getEnglishName()});

        markDirty();
    }

    // -----------------------------------------------------------------------------
    // Page conversion

    private short pageProgress = 0;
    private int pageSlotId;
    private boolean doPageProgress;
    private ScheduledTask givePageTask;
    private boolean lockPage;

    public short getPageProgress() {
        return pageProgress;
    }

    public void setPageProgress(int pageProgress) {
        this.pageProgress = (short) pageProgress;
    }

    // -----------------------------------------------------------------------------
    // Item handler

    public static final int BIOME_OVERRIDE_SLOT = 10;

    private final JSGItemStackHandler itemStackHandler = new JSGItemStackHandler(12) {

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            Item item = stack.getItem();
            boolean isItemCapacitor = (item instanceof CapacitorItemBlock);
            switch (slot) {
                case 0:
                case 1:
                case 2:
                case 3:
                    return StargateUpgradeEnum.contains(item) && !hasUpgrade(item);

                case 4:
                    return isItemCapacitor && getSupportedCapacitors() >= 1;
                case 5:
                    return isItemCapacitor && getSupportedCapacitors() >= 2;
                case 6:
                    return isItemCapacitor && getSupportedCapacitors() >= 3;

                case 7:
                case 8:
                    return item == JSGItems.PAGE_NOTEBOOK_ITEM;

                case 9:
                    return item == JSGItems.PAGE_NOTEBOOK_ITEM || item == JSGItems.UNIVERSE_DIALER;

                case BIOME_OVERRIDE_SLOT:
                    BiomeOverlayEnum override = JSGConfigUtil.getBiomeOverrideItemMetaPairs().get(new ItemMetaPair(stack));
                    if (override == null) return false;

                    return getSupportedOverlays().contains(override);
                case 11:
                    return canInsertItemAsIris(item);
                default:
                    return true;
            }
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            switch (slot) {
                case 4:
                case 5:
                case 6:
                    updatePowerTier();
                    break;

                case BIOME_OVERRIDE_SLOT:
                    sendState(StateTypeEnum.BIOME_OVERRIDE_STATE, new StargateBiomeOverrideState(determineBiomeOverride()));
                    break;
                // iris update state
                case 11:
                    updateIrisType();
                    break;
                default:
                    break;
            }

            markDirty();
        }
    };

    public int getSupportedCapacitors() {
        return getConfig().getOption(ConfigOptions.CAPACITORS_COUNT.id).getIntValue();
    }

    public abstract int getDefaultCapacitors();


    public enum StargateUpgradeEnum implements EnumKeyInterface<Item> {
        MILKYWAY_GLYPHS(JSGItems.CRYSTAL_GLYPH_MILKYWAY),
        PEGASUS_GLYPHS(JSGItems.CRYSTAL_GLYPH_PEGASUS),
        UNIVERSE_GLYPHS(JSGItems.CRYSTAL_GLYPH_UNIVERSE),
        CHEVRON_UPGRADE(JSGItems.CRYSTAL_GLYPH_STARGATE);

        public final Item item;

        StargateUpgradeEnum(Item item) {
            this.item = item;
        }

        @Override
        public Item getKey() {
            return item;
        }

        private static final EnumKeyMap<Item, StargateUpgradeEnum> idMap = new EnumKeyMap<Item, StargateClassicBaseTile.StargateUpgradeEnum>(values());

        public static StargateUpgradeEnum valueOf(Item item) {
            return idMap.valueOf(item);
        }

        public static boolean contains(Item item) {
            return idMap.contains(item);
        }
    }

    // ----------------------------------------------------------
    // IRISES

    public enum StargateIrisUpgradeEnum implements EnumKeyInterface<Item> {
        IRIS_UPGRADE_CLASSIC(JSGItems.UPGRADE_IRIS),
        IRIS_UPGRADE_TRINIUM(JSGItems.UPGRADE_IRIS_TRINIUM),
        IRIS_UPGRADE_CREATIVE(JSGItems.UPGRADE_IRIS_CREATIVE),
        IRIS_UPGRADE_SHIELD(JSGItems.UPGRADE_SHIELD);

        public final Item item;

        StargateIrisUpgradeEnum(Item item) {
            this.item = item;
        }

        @Override
        public Item getKey() {
            return item;
        }

        private static final EnumKeyMap<Item, StargateIrisUpgradeEnum> idMap =
                new EnumKeyMap<Item, StargateClassicBaseTile.StargateIrisUpgradeEnum>(values());

        public static StargateIrisUpgradeEnum valueOf(Item item) {
            return idMap.valueOf(item);
        }

        public static boolean contains(Item item) {
            return idMap.contains(item);
        }
    }

    public boolean canInsertItemAsIris(Item item) {
        return StargateIrisUpgradeEnum.contains(item);
    }

    public void updateIrisType() {
        updateIrisType(true);
    }

    public void updateIrisType(boolean markDirty) {
        irisType = EnumIrisType.byItem(itemStackHandler.getStackInSlot(11).getItem());
        irisAnimation = getWorld().getTotalWorldTime();
        if (irisType == EnumIrisType.NULL)
            irisState = EnumIrisState.OPENED;
        sendRenderingUpdate(StargateRendererActionState.EnumGateAction.IRIS_UPDATE, 0, false, irisType, irisState, irisAnimation);
        updateIrisDurability();
        if (markDirty) markDirty();
        if (!world.isRemote && isIrisOpened()) {
            setIrisBlocks(false);

        }
    }

    public void updateIrisDurability() {
        irisDurability = 0;
        irisMaxDurability = 0;
        if (hasPhysicalIris()) {
            irisMaxDurability = (irisType == IRIS_TITANIUM ? JSGConfig.Stargate.iris.titaniumIrisDurability : JSGConfig.Stargate.iris.triniumIrisDurability);
            irisDurability = irisMaxDurability - itemStackHandler.getStackInSlot(11).getItem().getDamage(itemStackHandler.getStackInSlot(11));
        }
    }

    public EnumIrisType getIrisType() {
        return irisType;
    }

    public EnumIrisState getIrisState() {
        return irisState;
    }

    public boolean isIrisClosed() {
        return irisState == EnumIrisState.CLOSED;
    }

    public boolean isIrisOpened() {
        return irisState == EnumIrisState.OPENED;
    }

    public boolean hasCreativeIris(){
        return (irisType == IRIS_CREATIVE);
    }

    public boolean hasPhysicalIris() {
        switch (irisType) {
            case IRIS_TITANIUM:
            case IRIS_TRINIUM:
            case IRIS_CREATIVE:
                return true;
            default:
                return false;
        }
    }

    public boolean hasIris() {
        return irisType != EnumIrisType.NULL;
    }

    public boolean hasShieldIris() {
        return irisType == EnumIrisType.SHIELD;
    }

    public boolean toggleIris() {
        if (irisType == EnumIrisType.NULL) return false;
        if (isIrisClosed() || isIrisOpened())
            irisAnimation = getWorld().getTotalWorldTime();
        SoundEventEnum openSound;
        SoundEventEnum closeSound;
        if (hasPhysicalIris()) {
            openSound = SoundEventEnum.IRIS_OPENING;
            closeSound = SoundEventEnum.IRIS_CLOSING;
        } else {
            openSound = SoundEventEnum.SHIELD_OPENING;
            closeSound = SoundEventEnum.SHIELD_CLOSING;
        }
        switch (irisState) {
            case OPENED:
            case OPENING:
                if (hasShieldIris() && getEnergyStorage().getEnergyStored() < shieldKeepAlive * 3)
                    return false;

                irisState = EnumIrisState.CLOSING;
                sendRenderingUpdate(StargateRendererActionState.EnumGateAction.IRIS_UPDATE, 0, true, irisType, irisState, irisAnimation);
                sendSignal(null, "stargate_iris_closing", new Object[]{"Iris is closing"});
                markDirty();
                playSoundEvent(closeSound);
                if (targetGatePos != null) executeTask(EnumScheduledTask.STARGATE_HORIZON_LIGHT_BLOCK, null);
                break;
            case CLOSED:
            case CLOSING:
                irisState = EnumIrisState.OPENING;
                setIrisBlocks(false);
                sendRenderingUpdate(StargateRendererActionState.EnumGateAction.IRIS_UPDATE, 0, true, irisType, irisState, irisAnimation);
                sendSignal(null, "stargate_iris_opening", new Object[]{"Iris is opening"});
                markDirty();
                playSoundEvent(openSound);
                if (targetGatePos != null) executeTask(EnumScheduledTask.STARGATE_HORIZON_LIGHT_BLOCK, null);
                break;
            default:
                return false;
        }
        markDirty();
        return true;
    }

    protected CodeSender codeSender;

    public boolean receiveIrisCode(CodeSender sender, String code) {
        sendSignal(null, "received_code", code);
        if (irisMode != EnumIrisMode.AUTO) {
            sender.sendMessage(GDOMessages.SEND_TO_COMPUTER.textComponent);
            codeSender = sender;
            return false;
        }
        if (code.equalsIgnoreCase(irisCode)) {
            switch (this.irisState) {
                case OPENED:
                    sender.sendMessage(GDOMessages.OPENED.textComponent);
                    break;
                case CLOSED:
                    sender.sendMessage(GDOMessages.CODE_ACCEPTED.textComponent);
                    codeSender = sender;
                    toggleIris();
                    break;
                case OPENING:
                case CLOSING:
                    sender.sendMessage(GDOMessages.BUSY.textComponent);
                    break;
                default:
                    break;
            }
        } else {
            sender.sendMessage(GDOMessages.CODE_REJECTED.textComponent);
            //sender.sendMessage(new TextComponentString("Iris code: " + irisCode));
            return false;
        }
        markDirty();
        return true;
    }

    public void setIrisCode(String code) {
        this.irisCode = code;
        markDirty();
    }

    private Runnable afterIrisDone = null;

    public void setIrisMode(EnumIrisMode irisMode) {
        if (this.irisMode != irisMode && hasIris()) {
            switch (irisMode) {
                case OPENED:
                case CLOSED:
                    irisModeAction(irisMode);
                    break;
                case AUTO:
                    if (getStargateState().notInitiating() || getStargateState().incoming()) {
                        if (irisState == EnumIrisState.OPENED) toggleIris();
                    } else {
                        if (isIrisClosed()) toggleIris();
                    }
                    break;
                case OC:
                default:
                    break;
            }


        }

        this.irisMode = irisMode;
        markDirty();
    }

    private void irisModeAction(EnumIrisMode mode) {
        EnumIrisState p, p2;
        if (mode == EnumIrisMode.OPENED) {
            p = EnumIrisState.CLOSED;
            p2 = EnumIrisState.CLOSING;
        } else if (mode == EnumIrisMode.CLOSED) {
            p = EnumIrisState.OPENED;
            p2 = EnumIrisState.OPENING;
        } else return;

        if (irisState == p) toggleIris();
        else if (irisState == p2) afterIrisDone = this::toggleIris;

    }

    public String getIrisCode() {
        return this.irisCode;
    }

    public EnumIrisMode getIrisMode() {
        return this.irisMode;
    }


    private void setIrisBlocks(boolean set) {
        IBlockState invBlockState = JSGBlocks.IRIS_BLOCK.getDefaultState();
        if (set) invBlockState = JSGBlocks.IRIS_BLOCK.getStateFromMeta(getFacing().getHorizontalIndex());
        BlockPos startPos = this.pos;
        for (BlockPos invPos : Objects.requireNonNull(StargateSizeEnum.getIrisBlocksPattern(getStargateSize()))) {
            BlockPos newPos = startPos.add(FacingHelper.rotateBlock(invPos, facing, facingVertical));

            if (set) {

                if (world.getBlockState(newPos).getMaterial() != Material.AIR && world.getBlockState(newPos).getBlock() != invBlockState.getBlock()) {
                    if (!JSGConfig.Stargate.iris.irisDestroysBlocks) continue;
                    world.destroyBlock(newPos, true);
                }
                world.setBlockState(newPos, invBlockState, 3);
                if (newPos == getGateCenterPos() && targetGatePos != null) {
                    world.getBlockState(newPos).getBlock().setLightLevel(1f);
                }

            } else {
                if (newPos == getGateCenterPos() && targetGatePos != null)
                    executeTask(EnumScheduledTask.STARGATE_HORIZON_LIGHT_BLOCK, null);
                if (world.getBlockState(newPos).getBlock() == JSGBlocks.IRIS_BLOCK) world.setBlockToAir(newPos);
            }
        }
    }

    // -----------------------------------------------------------

    private static final List<Integer> UPGRADE_SLOTS_IDS = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 11));

    @Override
    public Iterator<Integer> getUpgradeSlotsIterator() {
//        return IntStream.range(0, 7).iterator();
        return UPGRADE_SLOTS_IDS.iterator();
    }

    // -----------------------------------------------------------------------------
    // Power system

    private final LargeEnergyStorage energyStorage = new LargeEnergyStorage() {

        @Override
        protected void onEnergyChanged() {
            markDirty();
        }
    };

    @Override
    protected SmallEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public int getEnergyStored(){
        return getEnergyStorage().getEnergyStored();
    }

    private int currentPowerTier = 1;

    public int getPowerTier() {
        return currentPowerTier;
    }

    private void updatePowerTier() {
        updatePowerTier(false);
    }
    private void updatePowerTier(boolean resetEnergy) {
        int powerTier = 1;

        for (int i = 4; i < 7; i++) {
            if (!itemStackHandler.getStackInSlot(i).isEmpty()) {
                powerTier++;
            }
        }

        if (resetEnergy || powerTier != currentPowerTier) {
            currentPowerTier = powerTier;

            energyStorage.clearStorages();

            for (int i = 4; i < 7; i++) {
                ItemStack stack = itemStackHandler.getStackInSlot(i);

                if (!stack.isEmpty()) {
                    energyStorage.addStorage(stack.getCapability(CapabilityEnergy.ENERGY, null));
                }
            }

            JSG.debug("Updated to power tier: " + powerTier);
        }
    }


    // -----------------------------------------------------------------------------
    // Capabilities

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == null) || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == null)
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemStackHandler);

        return super.getCapability(capability, facing);
    }


    // -----------------------------------------------------------------
    // Beamers

    private final List<BlockPos> linkedBeamers = new ArrayList<>();

    public void addLinkedBeamer(BlockPos pos) {
        if (stargateState.engaged()) {
            ((BeamerTile) Objects.requireNonNull(world.getTileEntity(pos))).gateEngaged(targetGatePos);
        }

        linkedBeamers.add(pos.toImmutable());
        markDirty();
    }

    public void removeLinkedBeamer(BlockPos pos) {
        linkedBeamers.remove(pos);
        markDirty();
    }

    private void updateBeamers() {
        if (stargateState.engaged()) {
            for (BlockPos beamerPos : linkedBeamers) {
                if (world.getTileEntity(beamerPos) != null)
                    ((BeamerTile) Objects.requireNonNull(world.getTileEntity(beamerPos))).gateEngaged(targetGatePos);
            }
        }
    }

    @Override
    public void refresh() {
        super.refresh();
        this.lastFakeWorld = getFakeWorld();
        this.lastFakePos = getFakePos();
        markDirty();
    }

    protected World lastFakeWorld = getFakeWorld();
    protected BlockPos lastFakePos = getFakePos();

    public World getFakeWorld() {
        return world;
    }

    public void setFakeWorld(World world) {
    }

    public BlockPos getFakePos() {
        return pos;
    }

    public void setFakePos(BlockPos pos) {
    }

    public ArrayList<NearbyGate> getNearbyGates() {
        return getNearbyGates(null, false, true);
    }

    public ArrayList<NearbyGate> getNearbyGates(@Nullable SymbolTypeEnum gateType, boolean ignoreIfInstance, boolean checkAddressAndEnergy) {
        if (gateType == null) gateType = getSymbolType();
        double squaredGate = (double) JSGConfig.Stargate.mechanics.universeGateNearbyReach * JSGConfig.Stargate.mechanics.universeGateNearbyReach;

        ArrayList<NearbyGate> addresses = new ArrayList<>();

        for (Map.Entry<StargateAddress, StargatePos> entry : StargateNetwork.get(getFakeWorld()).getMap().get(gateType).entrySet()) {

            StargatePos stargatePos = entry.getValue();

            if (!ignoreIfInstance && stargatePos.getGateSymbolType() != gateType)
                continue;

            StargateAbstractBaseTile targetGateTile = stargatePos.getTileEntity();

            if (!(targetGateTile instanceof StargateClassicBaseTile))
                continue;

            StargateClassicBaseTile classicTile = (StargateClassicBaseTile) targetGateTile;

            if (!classicTile.isMerged())
                continue;

            int targetDim = classicTile.getFakeWorld().provider.getDimension();
            BlockPos targetFoundPos = classicTile.getFakePos();

            if (targetDim != getFakeWorld().provider.getDimension())
                continue;

            if (targetFoundPos.distanceSq(getFakePos()) > squaredGate)
                continue;

            if (stargatePos.gatePos.equals(pos) && stargatePos.dimensionID == world.provider.getDimension())
                continue;

            int symbolsNeeded = getMinimalSymbolsToDial(gateType, stargatePos);

            if (checkAddressAndEnergy) {
                StargateAddressDynamic addr3 = new StargateAddressDynamic(gateType);
                addr3.addAll(entry.getKey().subList(0, (symbolsNeeded - 1)));
                addr3.addSymbol(targetGateTile.getSymbolType().getOrigin());
                if (checkAddressAndEnergy(addr3).ok())
                    addresses.add(new NearbyGate(entry.getKey(), symbolsNeeded, targetGateTile.getSymbolType()));
            } else
                addresses.add(new NearbyGate(entry.getKey(), symbolsNeeded, targetGateTile.getSymbolType()));
        }
        return addresses;
    }

    public int getMinimalSymbolsToDial(SymbolTypeEnum symbolType, StargatePos targetGatePos){
        return getSymbolType().getMinimalSymbolCountTo(symbolType, StargateDimensionConfig.isGroupEqual(targetGatePos.dimensionID, world.provider.getDimension()));
    }


    // -----------------------------------------------------------------
    // OpenComputers methods


    @SuppressWarnings("unused")
    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getOpenedTime(Context context, Arguments args) {
        if (stargateState.engaged()) {
            float openedSeconds = getOpenedSeconds();
            int minutes = ((int) Math.floor(openedSeconds / 60));
            int seconds = ((int) (openedSeconds - (60 * minutes)));
            String secondsString = ((seconds < 10) ? "0" + seconds : "" + seconds);
            if (openedSeconds > 0) return new Object[]{true, "stargate_time", "" + minutes, "" + secondsString};
            return new Object[]{false, "stargate_not_connected"};
        }
        return new Object[]{false, "stargate_not_connected"};
    }

    @SuppressWarnings("unused")
    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() -- close/open the iris/shield")
    public Object[] toggleIris(Context context, Arguments args) {
        if (irisType == EnumIrisType.NULL)
            return new Object[]{false, "stargate_iris_missing", "Iris is not installed!"};
        if (irisMode != EnumIrisMode.OC)
            return new Object[]{false, "stargate_iris_error_mode", "Iris mode must be set to OC"};
        boolean result = toggleIris();
        markDirty();
        if (!result && (hasShieldIris() && isIrisOpened() && getEnergyStorage().getEnergyStored() < shieldKeepAlive * 3))
            return new Object[]{false, "stargate_iris_not_power", "Not enough power to close shield"};
        else if (!result)
            return new Object[]{false, "stargate_iris_busy", "Iris is busy"};
        else
            return new Object[]{true};
    }

    @SuppressWarnings("unused")
    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() -- get info about iris")
    public Object[] getIrisState(Context context, Arguments args) {
        return new Object[]{irisState.toString()};
    }

    @SuppressWarnings("unused")
    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() -- get info about iris")
    public Object[] getIrisType(Context context, Arguments args) {
        return new Object[]{irisType.toString()};
    }

    @SuppressWarnings("unused")
    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() -- get info about iris")
    public Object[] getIrisDurability(Context context, Arguments args) {
        updateIrisDurability();
        return new Object[]{irisDurability + "/" + irisMaxDurability, irisDurability, irisMaxDurability};
    }

    @SuppressWarnings("unused")
    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function(message:string) -- Sends message to last person, who sent code for iris")
    public Object[] sendMessageToIncoming(Context context, Arguments args) {
        if (!isMerged()) return new Object[]{null, "stargate_failure_not_merged", "Stargate is not merged"};
        if (!stargateState.engaged())
            return new Object[]{null, "stargate_failure_not_engaged", "Stargate is not engaged"};
        if (!args.isString(0)) return new Object[]{false, "wrong_argument_type"};

        if (codeSender != null && codeSender.canReceiveMessage()) {
            codeSender.sendMessage(new TextComponentString(args.checkString(0)));
            return new Object[]{true, "success"};
        }

        return new Object[]{false, "no_listener_available"};
    }

    @SuppressWarnings("unused")
    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function(code:string) -- send code like GDO (should be only numbers)")
    public Object[] sendIrisCode(Context context, Arguments args) {
        if (args.count() <= 0) {
            return new Object[]{false, "invalid_method_format", "You must enter code!"};
        }
        StargatePos destinationPos = StargateNetwork.get(world).getStargate(dialedAddress);
        if (destinationPos == null || this.getStargateState().notInitiating()) return new Object[]{false, "stargate_not_engaged_initiating"};
        StargateAbstractBaseTile te = destinationPos.getTileEntity();
        if (te instanceof StargateClassicBaseTile) {
            ((StargateClassicBaseTile) te).receiveIrisCode(new ComputerCodeSender(
                            StargateNetwork.get(world).getStargate(
                                    this.getStargateAddress(SymbolTypeEnum.MILKYWAY)
                            )
                    ), args.checkString(0)
            );
        } else {
            return new Object[]{false, "invalid_target_gate"};
        }
        return new Object[]{true, "success"};
    }

    @SuppressWarnings("unused")
    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function(symbolName:string) -- Spins the ring to the given symbol and engages/locks it")
    public Object[] engageSymbol(Context context, Arguments args) {
        if (!isMerged()) return new Object[]{null, "stargate_failure_not_merged", "Stargate is not merged"};

        if (!stargateState.idle()) {
            return new Object[]{null, "stargate_failure_busy", "Stargate is busy, state: " + stargateState};
        }

        if (dialedAddress.size() == 9) {
            return new Object[]{null, "stargate_failure_full", "Already dialed 9 chevrons"};
        }

        SymbolInterface targetSymbol = getSymbolFromNameIndex(args.checkAny(0));

        // disables engaging unknown symbols (gate has only 36, but dhd 38)
        if (targetSymbol == SymbolPegasusEnum.UNKNOW1 || targetSymbol == SymbolPegasusEnum.UNKNOW2)
            throw new IllegalArgumentException("bad argument (symbol name/index invalid)");
        addSymbolToAddressManual(targetSymbol, context);
        markDirty();

        return new Object[]{"stargate_spin"};
    }

    @SuppressWarnings("unused")
    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() - aborts dialing")
    public Object[] abortDialing(Context context, Arguments args) {
        if (!isMerged()) return new Object[]{null, "stargate_failure_not_merged", "Stargate is not merged"};

        if (stargateState.dialingComputer() || stargateState.idle()) {
            abortDialingSequence();
            markDirty();
            return new Object[]{null, "stargate_aborting", "Aborting dialing"};
        }
        return new Object[]{null, "stargate_aborting_failed", "Aborting dialing failed"};
    }

    @SuppressWarnings("unused")
    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() -- Tries to open the gate")
    public Object[] engageGate(Context context, Arguments args) {
        if (!isMerged()) return new Object[]{null, "stargate_failure_not_merged", "Stargate is not merged"};

        if (stargateState.idle()) {
            StargateOpenResult gateState = attemptOpenAndFail();

            if (gateState.ok()) {
                return new Object[]{"stargate_engage"};
            } else {
                sendSignal(null, "stargate_failed", "");
                return new Object[]{null, "stargate_failure_opening", "Stargate failed to open", gateState.toString()};
            }
        } else {
            return new Object[]{null, "stargate_failure_busy", "Stargate is busy", stargateState.toString()};
        }
    }

    @SuppressWarnings("unused")
    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() -- Tries to close the gate")
    public Object[] disengageGate(Context context, Arguments args) {
        if (!isMerged()) return new Object[]{null, "stargate_failure_not_merged", "Stargate is not merged"};

        if (stargateState.engaged()) {
            if (getStargateState().initiating()) {
                attemptClose(StargateClosedReasonEnum.REQUESTED);
                return new Object[]{"stargate_disengage"};
            } else return new Object[]{null, "stargate_failure_wrong_end", "Unable to close the gate on this end"};
        } else {
            return new Object[]{null, "stargate_failure_not_open", "The gate is closed"};
        }
    }

    @SuppressWarnings("unused")
    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() -- Tries to spin mw gate")
    public Object[] spinGate(Context context, Arguments args) {
        if (!isMerged()) return new Object[]{null, "stargate_failure_not_merged", "Stargate is not merged"};

        if ((this instanceof StargatePegasusBaseTile))
            return new Object[]{null, "stargate_not_supported", "Stargate type is not supported"};

        if (stargateState.idle()) {
            int time;
            if (args.isInteger(0)) {
                time = args.checkInteger(0);
                boolean changeState = true;
                if (args.isBoolean(1))
                    changeState = args.checkBoolean(1);
                if (time != 0) {
                    spinRing(1, changeState, true, time);
                    return new Object[]{null, "stargate_spin"};
                }
                return new Object[]{null, "stargate_failure_wrong_usage", "Time is 0"};
            }
            return new Object[]{null, "stargate_failure_wrong_usage", "Missing first argument (time in ticks)"};
        } else {
            return new Object[]{null, "stargate_failure_not_idle", "The gate is not idle"};
        }
    }

    @SuppressWarnings("unused")
    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() -- Returns capacitors count")
    public Object[] getCapacitorsInstalled(Context context, Arguments args) {
        return new Object[]{isMerged() ? currentPowerTier - 1 : null};
    }

    @SuppressWarnings("unused")
    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() -- Returns gate type")
    public Object[] getGateType(Context context, Arguments args) {
        return new Object[]{isMerged() ? getSymbolType() : null};
    }

    @SuppressWarnings("unused")
    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() -- Returns gate status")
    public Object[] getGateStatus(Context context, Arguments args) {
        if (!isMerged()) return new Object[]{"not_merged"};

        if (stargateState.engaged()) return new Object[]{"open", stargateState.initiating()};

        return new Object[]{stargateState.toString().toLowerCase()};
    }

    @SuppressWarnings({"unused", "unchecked"})
    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function(address:table|address:string...) -- Returns symbols needed to dial an address")
    public Object[] getSymbolsNeeded(Context context, Arguments args) {
        if (!isMerged()) return new Object[]{"not_merged"};

        StargateAddressDynamic stargateAddress = new StargateAddressDynamic(getSymbolType());
        Iterator<Object> iterator;

        if (args.isTable(0)) {
            iterator = args.checkTable(0).values().iterator();
        } else {
            iterator = args.iterator();
        }

        while (iterator.hasNext()) {
            Object symbolObj = iterator.next();

            if (stargateAddress.size() == 9) {
                throw new IllegalArgumentException("Too much glyphs");
            }

            SymbolInterface symbol = getSymbolFromNameIndex(symbolObj);
            if (stargateAddress.contains(symbol)) {
                throw new IllegalArgumentException("Duplicate glyph");
            }

            stargateAddress.addSymbol(symbol);
        }

        if (!stargateAddress.getLast().origin() && stargateAddress.size() < 9) stargateAddress.addOrigin();

        if (!stargateAddress.validate()) return new Object[]{"address_malformed"};

        if (!canDialAddress(stargateAddress)) return new Object[]{"address_malformed"};

        StargatePos pos = network.getStargate(stargateAddress);
        if(pos == null) return new Object[]{"gate_not_found"};

        int symbols = getMinimalSymbolsToDial(pos.getGateSymbolType(), pos);

        return new Object[]{true, symbols};
    }

    @SuppressWarnings({"unused", "unchecked"})
    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function(address:table|address:string...) -- Returns energy needed to dial an address")
    public Object[] getEnergyRequiredToDial(Context context, Arguments args) {
        if (!isMerged()) return new Object[]{"not_merged"};

        StargateAddressDynamic stargateAddress = new StargateAddressDynamic(getSymbolType());
        Iterator<Object> iterator;

        if (args.isTable(0)) {
            iterator = args.checkTable(0).values().iterator();
        } else {
            iterator = args.iterator();
        }

        while (iterator.hasNext()) {
            Object symbolObj = iterator.next();

            if (stargateAddress.size() == 9) {
                throw new IllegalArgumentException("Too much glyphs");
            }

            SymbolInterface symbol = getSymbolFromNameIndex(symbolObj);
            if (stargateAddress.contains(symbol)) {
                throw new IllegalArgumentException("Duplicate glyph");
            }

            stargateAddress.addSymbol(symbol);
        }

        if (!stargateAddress.getLast().origin() && stargateAddress.size() < 9) stargateAddress.addOrigin();

        if (!stargateAddress.validate()) return new Object[]{"address_malformed"};

        if (!canDialAddress(stargateAddress)) return new Object[]{"address_malformed"};

        EnergyRequiredToOperate energyRequired = getEnergyRequiredToDial(Objects.requireNonNull(network.getStargate(stargateAddress)));
        Map<String, Object> energyMap = new HashMap<>(2);

        energyMap.put("open", energyRequired.energyToOpen);
        energyMap.put("keepAlive", energyRequired.keepAlive);
        energyMap.put("canOpen", getEnergyStorage().getEnergyStored() >= energyRequired.energyToOpen);

        return new Object[]{energyMap};
    }

    @SuppressWarnings("unused")
    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function(gateType:string|gateType:int, checkGateType:boolean, checkAddressAndEnergy:boolean) -- Returns nearby gates")
    public Object[] getNearbyGates(Context context, Arguments args) {
        if (!isMerged()) return new Object[]{null, false, "gate_not_merged", new HashMap<String, Object>()};
        Map<String, Map<List<String>, Integer>> map = new HashMap<>(); // (SymbolType, (address, symbolsNeeded))

        SymbolTypeEnum symbolType = (args.isInteger(0) ? SymbolTypeEnum.valueOf(args.checkInteger(0)) : (args.isString(0) ? SymbolTypeEnum.valueOf(args.checkString(0)) : getSymbolType()));
        boolean checkType = (args.isBoolean(1) && args.checkBoolean(1));
        boolean checkAddEne = (args.isBoolean(2) && args.checkBoolean(2));

        for (NearbyGate g : getNearbyGates(symbolType, checkType, checkAddEne)) {
            Map<List<String>, Integer> map2 = map.computeIfAbsent(g.address.getSymbolType().toString(), k -> new HashMap<>());
            map2.put(g.address.getNameList(), g.symbolsNeeded);
            map.put(g.gateType.toString(), map2);
        }

        return new Object[]{null, true, "success", map};
    }

}
