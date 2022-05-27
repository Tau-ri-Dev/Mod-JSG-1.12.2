package mrjake.aunis.tileentity.transportrings;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import mrjake.aunis.Aunis;
import mrjake.aunis.block.props.TRPlatformBlock;
import mrjake.aunis.util.main.AunisProps;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.config.ingame.AunisConfigOption;
import mrjake.aunis.config.ingame.AunisConfigOptionTypeEnum;
import mrjake.aunis.config.ingame.AunisTileEntityConfig;
import mrjake.aunis.config.ingame.ITileConfig;
import mrjake.aunis.gui.container.transportrings.TRGuiState;
import mrjake.aunis.gui.container.transportrings.TRGuiUpdate;
import mrjake.aunis.item.AunisItems;
import mrjake.aunis.item.notebook.PageNotebookItem;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.packet.StateUpdateRequestToServer;
import mrjake.aunis.packet.transportrings.StartPlayerFadeOutToClient;
import mrjake.aunis.renderer.transportrings.TransportRingsAbstractRenderer;
import mrjake.aunis.sound.AunisSoundHelper;
import mrjake.aunis.sound.SoundEventEnum;
import mrjake.aunis.stargate.EnumScheduledTask;
import mrjake.aunis.stargate.network.SymbolInterface;
import mrjake.aunis.stargate.power.StargateClassicEnergyStorage;
import mrjake.aunis.stargate.power.StargateEnergyRequired;
import mrjake.aunis.state.State;
import mrjake.aunis.state.StateProviderInterface;
import mrjake.aunis.state.StateTypeEnum;
import mrjake.aunis.state.dialhomedevice.DHDActivateButtonState;
import mrjake.aunis.state.transportrings.TransportRingsRendererState;
import mrjake.aunis.state.transportrings.TransportRingsStartAnimationRequest;
import mrjake.aunis.tileentity.util.IUpgradable;
import mrjake.aunis.tileentity.util.ScheduledTask;
import mrjake.aunis.tileentity.util.ScheduledTaskExecutorInterface;
import mrjake.aunis.transportrings.*;
import mrjake.aunis.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.util.*;

import static mrjake.aunis.transportrings.TransportRingsAddress.MAX_SYMBOLS;

@Optional.InterfaceList({@Optional.Interface(iface = "li.cil.oc.api.network.Environment", modid = "opencomputers"), @Optional.Interface(iface = "li.cil.oc.api.network.WirelessEndpoint", modid = "opencomputers")})
public abstract class TransportRingsAbstractTile extends TileEntity implements ITickable, StateProviderInterface, ScheduledTaskExecutorInterface, ILinkable, Environment, IUpgradable, ITileConfig {
    public static final int FADE_OUT_TOTAL_TIME = 2 * 20; // 2s
    public static final int TIMEOUT_TELEPORT = FADE_OUT_TOTAL_TIME / 2;
    public static final int TIMEOUT_FADE_OUT = (int) (30 + TransportRingsAbstractRenderer.INTERVAL_UPRISING * TransportRingsAbstractRenderer.RING_COUNT + TransportRingsAbstractRenderer.ANIMATION_SPEED_DIVISOR * Math.PI);
    public static final int RINGS_CLEAR_OUT = (int) (15 + TransportRingsAbstractRenderer.INTERVAL_FALLING * TransportRingsAbstractRenderer.RING_COUNT + TransportRingsAbstractRenderer.ANIMATION_SPEED_DIVISOR * Math.PI);
    private final StargateClassicEnergyStorage energyStorage = new StargateClassicEnergyStorage() {

        @Override
        protected void onEnergyChanged() {
            markDirty();
        }
    };
    // ---------------------------------------------------------------------------------
    // Ticking and loading
    public Map<Map<SymbolTypeTransportRingsEnum, TransportRingsAddress>, TransportRings> ringsMap = new HashMap<>();
    public AunisAxisAlignedBB LOCAL_TELEPORT_BOX = new AunisAxisAlignedBB(-1, 2, -1, 2, 4.5, 2);
    protected AunisAxisAlignedBB globalTeleportBox;
    protected List<Entity> teleportList = new ArrayList<>();
    protected BlockPos lastPos = BlockPos.ORIGIN;
    // ---------------------------------------------------------------------------------
    // Teleportation
    protected BlockPos targetRingsPos = new BlockPos(0, 0, 0);
    protected List<Entity> excludedEntities = new ArrayList<>();
    protected Object ocContext;
    // ---------------------------------------------------------------------------------
    // Address system
    protected boolean initiating;
    // ---------------------------------------------------------------------------------
    // Adjustable distance
    // ---------------------------------------------------------------------------------
    // Rings network
    protected TransportRings rings;
    protected int energyTransferedLastTick = 0;
    List<ScheduledTask> scheduledTasks = new ArrayList<>();
    protected int ringsDistance = 2;
    protected String ringsName = "";
    // ---------------------------------------------------------------------------------
    // Scheduled task
    TransportRingsRendererState rendererState = new TransportRingsRendererState();
    private List<BlockPos> invisibleBlocksTemplate = Arrays.asList(new BlockPos(0, 2, 2), new BlockPos(1, 2, 2), new BlockPos(2, 2, 1));
    private AxisAlignedBB renderBoundingBox = TileEntity.INFINITE_EXTENT_AABB;
    /**
     * True if there is an active transport.
     */
    private boolean busy = false;
    // ---------------------------------------------------------------------------------
    // Controller
    private BlockPos linkedController;
    public TransportRingsAddress dialedAddress = new TransportRingsAddress(getSymbolType());
    private int linkId = -1;
    // ------------------------------------------------------------
    // Node-related work
    private final Node node = Aunis.ocWrapper.createNode(this, "transportrings");
    private int currentPowerTier = 1;
    public int itemStackHandlerSlotsCount = 9;
    private final AunisItemStackHandler itemStackHandler = new AunisItemStackHandler(9) {

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            Item item = stack.getItem();
            boolean isItemCapacitor = (item == Item.getItemFromBlock(AunisBlocks.CAPACITOR_BLOCK));
            switch (slot) {
                case 0:
                case 1:
                case 2:
                case 3:
                    return TransportRingsAbstractTile.TransportRingsUpgradeEnum.contains(item) && !hasUpgrade(item);

                case 4:
                    return isItemCapacitor && getSupportedCapacitors() >= 1;
                case 5:
                    return isItemCapacitor && getSupportedCapacitors() >= 2;
                case 6:
                    return isItemCapacitor && getSupportedCapacitors() >= 3;

                case 7:
                case 8:
                    return item == AunisItems.PAGE_NOTEBOOK_ITEM;
                default:
                    return true;
            }
        }

        @Override
        protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
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
                default:
                    break;
            }

            markDirty();
        }
    };
    // ------------------------------------------------------------
    // Page progress
    private short pageProgress = 0;
    private int pageSlotId;
    private boolean doPageProgress;
    private ScheduledTask givePageTask;
    private boolean lockPage;
    // ------------------------------------------------------------
    // Energy
    private int keepAliveEnergyPerTick = 0;
    private int energyStoredLastTick = 0;

    public short getPageProgress() {
        return pageProgress;
    }

    public void setPageProgress(int pageProgress) {
        this.pageProgress = (short) pageProgress;
    }


    // --------------------------------------------------
    // PLATFORMS

    public boolean isTherePlatform(){
        return getPlatform() != null;
    }

    public RingsPlatform getPlatform(){
        for(EnumFacing facing : EnumFacing.values()){
            BlockPos pos = this.pos.offset(facing, 1);
            Block block = world.getBlockState(pos).getBlock();
            if(block instanceof TRPlatformBlock){
                return new RingsPlatform(pos, (TRPlatformBlock) block);
            }
        }
        return null;
    }

    public void playPlatformSound(boolean closing){
        if(ringsDistance < 0 || !isTherePlatform()) return; // platform is not rendering!
        if(closing){
            AunisSoundHelper.playSoundEvent(world, getPosWithDistance(ringsDistance), SoundEventEnum.RINGS_PLATFORM_GOAULD_CLOSE);
            return;
        }
        AunisSoundHelper.playSoundEvent(world, getPosWithDistance(ringsDistance), SoundEventEnum.RINGS_PLATFORM_GOAULD_OPEN);
    }



    @Override
    public void update() {
        if (!world.isRemote) {
            initConfig();
            ScheduledTask.iterate(scheduledTasks, world.getTotalWorldTime());
            if (getRings().getAddresses() == null // if is null
                    || getRings().getAddresses().size() < SymbolTypeTransportRingsEnum.values().length // if is short
                    || getRings().getAddress(SymbolTypeTransportRingsEnum.valueOf(0)).get(0).equals(SymbolGoauldEnum.getOrigin()) // if first symbol is origin
                    || getRings().getAddress(SymbolTypeTransportRingsEnum.valueOf(0)).get(0).equals(getRings().getAddress(SymbolTypeTransportRingsEnum.valueOf(0)).get(1)) // if there are two same symbols
            ) {
                if(getRings().getAddresses() != null)
                    Aunis.logger.debug("TransportRings at " + pos.toString() + " are generating new addresses!");
                generateAddress(true);
            }

            if (!lastPos.equals(pos)) {
                lastPos = pos;

                getRings().setPos(pos);
                updateRingsDistance();
                setRingsParams(null, null, getRings().getName());
                updateLinkStatus();

                markDirty();
            }

            if(world.getTotalWorldTime() % 80 == 0) // every 4 seconds, update boxed (render and teleport)
                updateRingsDistance();

            /*
             * Draw power (isBusy)
             *
             * If initiating
             * 	True: Extract energy each tick
             */
            energyTransferedLastTick = 0;
            if (keepAliveEnergyPerTick > 0) {
                if (targetRingsPos == null) return;

                if (world.getTileEntity(targetRingsPos) == null) return;
                if (getEnergyStorage().getEnergyStored() >= keepAliveEnergyPerTick) {
                    getEnergyStorage().extractEnergy(keepAliveEnergyPerTick, false);
                    markDirty();
                }

                energyTransferedLastTick = getEnergyStorage().getEnergyStored() - energyStoredLastTick;
                energyStoredLastTick = getEnergyStorage().getEnergyStored();
                markDirty();
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
                    for (int i = 7; i < itemStackHandlerSlotsCount; i++) {
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

        }
    }

    public int getSlotsCount(){
        return itemStackHandlerSlotsCount;
    }

    @Override
    public void onLoad() {
        if (world.isRemote) {
            rendererState.ringsDistance = ringsDistance;
            AunisPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.RENDERER_STATE));
        }
        if (!world.isRemote) {
            setBarrierBlocks(false, false);
            generateAddress(false);
            globalTeleportBox = LOCAL_TELEPORT_BOX.offset(pos);

            updatePowerTier();
        }
        Aunis.ocWrapper.joinOrCreateNetwork(this);
    }

    public void onBreak() {
        setBarrierBlocks(false, false);
    }

    public Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> generateAndPostAddress(boolean reset) {
        Random random = new Random(pos.hashCode() * 31L + world.provider.getDimension());
        if (reset) {
            Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> map = new HashMap<>();
            for (SymbolTypeTransportRingsEnum symbolType : SymbolTypeTransportRingsEnum.values()) {
                TransportRingsAddress address = new TransportRingsAddress(symbolType);
                address.generate(random);
                map.put(symbolType, address);
            }
            return map;
        } else {
            return getRings().getAddresses();
        }
    }

    public void generateAddress(boolean reset) {
        setRingsParams(generateAndPostAddress(reset));
    }

    public void setNewRingsDistance(int newRingsDistance) {
        getRings().setRingsDistance(newRingsDistance);
        updateRingsDistance();
    }

    public void updateRingsDistance() {
        ringsDistance = getRings().getRingsDistance();
        LOCAL_TELEPORT_BOX = new AunisAxisAlignedBB(-1, ringsDistance, -1, 2, ringsDistance + 2.5, 2);
        invisibleBlocksTemplate = Arrays.asList(new BlockPos(0, ringsDistance, 2), new BlockPos(1, ringsDistance, 2), new BlockPos(2, ringsDistance, 1));
        globalTeleportBox = LOCAL_TELEPORT_BOX.offset(pos);
        renderBoundingBox = new AunisAxisAlignedBB(-3, -40, -3, 3, 40, 3).offset(pos);
        rendererState.ringsDistance = ringsDistance;
        rendererState.ringsConfig = config;
        NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
        AunisPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.RINGS_DISTANCE_UPDATE, new TransportRingsStartAnimationRequest(rendererState.animationStart, rendererState.ringsDistance, rendererState.ringsConfig)), point);
        markDirty();
    }

    @Override
    public void addTask(ScheduledTask scheduledTask) {
        scheduledTask.setExecutor(this);
        scheduledTask.setTaskCreated(world.getTotalWorldTime());

        scheduledTasks.add(scheduledTask);
        markDirty();
    }

    protected StargateEnergyRequired getEnergyRequiredToDial(TransportRings targetRings) {
        BlockPos sPos = pos;
        BlockPos tPos = targetRings.getPos();

        double distance = (int) sPos.getDistance(tPos.getX(), tPos.getY(), tPos.getZ());

        if (distance < 200) distance *= 0.8;
        else distance = 200 * Math.log10(distance) / Math.log10(200);

        int energyBase = AunisConfig.powerConfig.ringsKeepAliveBlockToEnergyRatioPerTick;
        StargateEnergyRequired energyRequired = new StargateEnergyRequired(energyBase, energyBase);
        energyRequired = energyRequired.mul(distance);

        return energyRequired;
    }

    @Override
    public void executeTask(EnumScheduledTask scheduledTask, NBTTagCompound customData) {
        switch (scheduledTask) {
            case RINGS_START_ANIMATION:
                animationStart();
                setBarrierBlocks(true, true);
                sendStateToController(StateTypeEnum.DHD_ACTIVATE_BUTTON, new DHDActivateButtonState(getSymbolType().getLight()));

                addTask(new ScheduledTask(EnumScheduledTask.RINGS_FADE_OUT));
                addTask(new ScheduledTask(EnumScheduledTask.RINGS_SOLID_BLOCKS, 35));

                playPlatformSound(false);
                Aunis.debug("Rings at " + pos.toString() + " started transport!");
                break;

            case RINGS_SOLID_BLOCKS:
                setBarrierBlocks(true, false);
                break;

            case RINGS_FADE_OUT:
                teleportList = world.getEntitiesWithinAABB(Entity.class, globalTeleportBox);

                for (Entity entity : teleportList) {
                    if (entity instanceof EntityPlayerMP) {
                        AunisPacketHandler.INSTANCE.sendTo(new StartPlayerFadeOutToClient(), (EntityPlayerMP) entity);
                    }
                }

                addTask(new ScheduledTask(EnumScheduledTask.RINGS_TELEPORT));
                break;

            case RINGS_TELEPORT:
                BlockPos teleportVector = targetRingsPos.subtract(pos);

                TransportRingsAbstractTile targetTile = ((TransportRingsAbstractTile) world.getTileEntity(targetRingsPos));
                if (targetTile == null) break;
                int targetRingsHeight = targetTile.getRings().getRingsDistance();

                for(Entity entity : teleportList){
                    int extracted = getEnergyStorage().extractEnergy(AunisConfig.powerConfig.ringsTeleportPowerDraw, true);
                    if(!initiating || extracted >= AunisConfig.powerConfig.ringsTeleportPowerDraw || !(entity instanceof EntityLivingBase)) {

                        if(entity instanceof EntityLivingBase && initiating)
                            getEnergyStorage().extractEnergy(extracted, false);

                        if (!excludedEntities.contains(entity)) {
                            BlockPos ePos = entity.getPosition().add(teleportVector);
                            double y = targetRingsPos.getY() + targetRingsHeight;
                            entity.setPositionAndUpdate(ePos.getX(), y, ePos.getZ());
                        }
                    }
                }
                markDirty();

                teleportList.clear();
                excludedEntities.clear();

                addTask(new ScheduledTask(EnumScheduledTask.RINGS_CLEAR_OUT));
                Aunis.debug("Rings at " + pos.toString() + " transported entities!");
                break;

            case RINGS_CLEAR_OUT:
                setBarrierBlocks(false, false);
                setBusy(false);
                clearButtonsDHD();

                TransportRingsAbstractTile targetRingsTile = (TransportRingsAbstractTile) world.getTileEntity(targetRingsPos);
                if (targetRingsTile != null) targetRingsTile.setBusy(false);

                sendSignal(ocContext, "transportrings_teleport_finished", initiating);
                keepAliveEnergyPerTick = 0;

                playPlatformSound(true);
                markDirty();
                Aunis.debug("Rings at " + pos.toString() + " deactivated!");

                break;


            case STARGATE_GIVE_PAGE:
                SymbolTypeTransportRingsEnum symbolType = SymbolTypeTransportRingsEnum.valueOf(pageSlotId - 7);
                ItemStack stack = itemStackHandler.getStackInSlot(pageSlotId);
                if (stack.isEmpty()) break;
                Aunis.logger.debug("Giving Notebook page of address " + symbolType);

                NBTTagCompound compound = PageNotebookItem.getCompoundFromAddress(getRings().getAddresses().get(symbolType), PageNotebookItem.getRegistryPathFromWorld(world, pos));

                stack = new ItemStack(AunisItems.PAGE_NOTEBOOK_ITEM, 1, 1);
                stack.setTagCompound(compound);
                itemStackHandler.setStackInSlot(pageSlotId, stack);

                break;

            case RINGS_SYMBOL_DEACTIVATE:
                if(customData != null && customData.hasKey("symbol")) {
                    int symbolId = customData.getInteger("symbol");
                    sendStateToController(StateTypeEnum.DHD_ACTIVATE_BUTTON, new DHDActivateButtonState(symbolId, true));
                }
                else{
                    sendStateToController(StateTypeEnum.DHD_ACTIVATE_BUTTON, new DHDActivateButtonState(true));
                }
                break;

            default:
                throw new UnsupportedOperationException("EnumScheduledTask." + scheduledTask.name() + " not implemented on " + this.getClass().getName());
        }
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public int getRingsDistance(){
        return ringsDistance;
    }

    public String getRingsName(){
        return ringsName;
    }

    public List<Entity> startAnimationAndTeleport(BlockPos targetRingsPos, List<Entity> excludedEntities, int waitTime, boolean initiating) {
        this.targetRingsPos = targetRingsPos;
        this.excludedEntities = excludedEntities;

        addTask(new ScheduledTask(EnumScheduledTask.RINGS_START_ANIMATION, waitTime));
        sendSignal(ocContext, "transportrings_teleport_start", initiating);
        this.initiating = initiating;
        markDirty();

        return world.getEntitiesWithinAABB(Entity.class, globalTeleportBox);
    }

    public void animationStart() {
        rendererState.animationStart = world.getTotalWorldTime();
        rendererState.ringsUprising = true;
        rendererState.isAnimationActive = true;
        rendererState.ringsDistance = getRings().getRingsDistance();
        rendererState.ringsConfig = config;
        markDirty();

        NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
        AunisPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.RINGS_START_ANIMATION, new TransportRingsStartAnimationRequest(rendererState.animationStart, rendererState.ringsDistance, rendererState.ringsConfig)), point);
    }

    public TransportRingsRendererState getRendererState(){
        return rendererState;
    }


    // -------------------------------
    // Engaging symbols

    public TransportResult addSymbolToAddress(SymbolInterface symbol) {
        if(isBusy()) return TransportResult.BUSY;
        if (canAddSymbol(symbol)) {
            dialedAddress.setSymbolType(getSymbolType());
            dialedAddress.add(symbol);
            markDirty();
            activateSymbolDHD(symbol);
            if (symbolWillLock()) {
                TransportResult result = attemptTransportTo(dialedAddress, EnumScheduledTask.RINGS_START_ANIMATION.waitTicks);
                if (!result.ok()) {
                    clearButtonsDHD(7);
                    markDirty();
                    sendSignal(ocContext, "transportrings_symbol_engage_failed", result.toString());
                    return result;
                }
            }
            sendSignal(ocContext, "transportrings_symbol_engage", TransportResult.OK.toString());
            return TransportResult.ACTIVATED;
        }
        if(symbol.origin()) {
            activateSymbolDHD(symbol);
            clearButtonsDHD(7);
            sendSignal(ocContext, "transportrings_symbol_engage_failed", TransportResult.NO_SUCH_ADDRESS.toString());
            return TransportResult.NO_SUCH_ADDRESS;
        }
        return TransportResult.ALREADY_ACTIVATED;
    }

    public void activateSymbolDHD(SymbolInterface symbol){
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("symbol", symbol.getId());
        sendStateToController(StateTypeEnum.DHD_ACTIVATE_BUTTON, new DHDActivateButtonState(symbol));
        addTask(new ScheduledTask(EnumScheduledTask.RINGS_SYMBOL_DEACTIVATE, compound));
    }

    public void clearButtonsDHD(){clearButtonsDHD(-1);}
    public void clearButtonsDHD(int waitTicks){
        if(waitTicks > -1)
            addTask(new ScheduledTask(EnumScheduledTask.RINGS_SYMBOL_DEACTIVATE, waitTicks));
        else
            sendStateToController(StateTypeEnum.DHD_ACTIVATE_BUTTON, new DHDActivateButtonState(true));
        dialedAddress.clear();
        markDirty();
    }

    public boolean canAddSymbol(SymbolInterface symbol) {
        if (dialedAddress.contains(symbol)) return false;
        if (dialedAddress.size() > MAX_SYMBOLS) {
            dialedAddress.clear();
            return false;
        }
        return !isBusy();
    }

    public void sendStateToController(StateTypeEnum stateType, State state){
        if(getLinkedControllerTile(world) != null)
            getLinkedControllerTile(world).sendState(stateType, state);
    }

    public boolean symbolWillLock() {
        return (dialedAddress.size() > MAX_SYMBOLS || dialedAddress.getLast().origin());
    }

    /**
     * Checks if Rings are linked to Rings at given address.
     * If yes, it starts teleportation.
     *
     * @param address Target rings address
     */
    public TransportResult attemptTransportTo(TransportRingsAddress address, int waitTime) {
        if (!getRings().isInGrid()) {
            return TransportResult.NOT_IN_GRID;
        }
        if (checkIfObstructed()) {
            return TransportResult.OBSTRUCTED;
        }
        if (isBusy()) {
            return TransportResult.BUSY;
        }

        TransportRingsAddress strippedAddress = (address.getLast().origin()) ? address.stripOrigin() : address;

        if(address.size() < MAX_SYMBOLS){
            return TransportResult.NO_SUCH_ADDRESS;
        }

        boolean found = false;
        for (TransportRings rings : ringsMap.values()) {
            // Binding exists
            if (rings.getAddress(address.getSymbolType()).equalsV2(strippedAddress, 4)) {
                if (!rings.isInGrid()) return TransportResult.NO_SUCH_ADDRESS;

                BlockPos targetRingsPos = rings.getPos();
                TransportRingsAbstractTile targetRingsTile = (TransportRingsAbstractTile) world.getTileEntity(targetRingsPos);

                if (targetRingsTile == null || targetRingsTile.checkIfObstructed()) {
                    return TransportResult.OBSTRUCTED_TARGET;
                }

                if (targetRingsTile.isBusy()) {
                    return TransportResult.BUSY_TARGET;
                }

                // power
                StargateEnergyRequired energyRequired = getEnergyRequiredToDial(rings);
                int extracted = getEnergyStorage().extractEnergy((energyRequired.keepAlive*20), true);
                if(extracted < (energyRequired.keepAlive*20)) // *20 because rings should be active more than 1 second
                    return TransportResult.NOT_ENOUGH_POWER;

                keepAliveEnergyPerTick = energyRequired.keepAlive;
                // ------

                this.setBusy(true);
                targetRingsTile.setBusy(true);

                List<Entity> excludedFromReceivingSite = world.getEntitiesWithinAABB(Entity.class, globalTeleportBox);
                List<Entity> excludedEntities = targetRingsTile.startAnimationAndTeleport(pos, excludedFromReceivingSite, waitTime, false);
                startAnimationAndTeleport(targetRingsPos, excludedEntities, waitTime, true);

                found = true;
                break;
            }
        }
        if (!found) {
            return TransportResult.NO_SUCH_ADDRESS;
        }
        else
            return TransportResult.OK;
    }

    private boolean checkIfObstructed() {
        if (AunisConfig.ringsConfig.ignoreObstructionCheck) return false;

        for (int y = 0; y < 3; y++) {
            for (Rotation rotation : Rotation.values()) {
                for (BlockPos invPos : invisibleBlocksTemplate) {

                    BlockPos newPos = new BlockPos(this.pos).add(invPos.rotate(rotation)).add(0, y, 0);
                    IBlockState newState = world.getBlockState(newPos);
                    Block newBlock = newState.getBlock();

                    if (!newBlock.isAir(newState, world, newPos) && !newBlock.isReplaceable(world, newPos)) {
                        return true;
                    }
                }
            }
        }

        return this.pos.getY() + ringsDistance < 0;
    }

    protected void setBarrierBlocks(boolean set, boolean passable) {
        IBlockState invBlockState = AunisBlocks.INVISIBLE_BLOCK.getDefaultState();

        if (passable) invBlockState = invBlockState.withProperty(AunisProps.HAS_COLLISIONS, false);

        for (int y = 1; y < 3; y++) {
            for (Rotation rotation : Rotation.values()) {
                for (BlockPos invPos : invisibleBlocksTemplate) {
                    BlockPos newPos = this.pos.add(invPos.rotate(rotation)).add(0, y, 0);

                    if (set) world.setBlockState(newPos, invBlockState, 3);
                    else {
                        if (world.getBlockState(newPos).getBlock() == AunisBlocks.INVISIBLE_BLOCK)
                            world.setBlockToAir(newPos);
                    }
                }
            }
        }
    }

    public void setLinkedController(BlockPos pos, int linkId) {
        this.linkedController = pos;
        this.linkId = linkId;

        markDirty();
    }

    public boolean isLinked() {
        return linkedController != null && world.getTileEntity(linkedController) instanceof TRControllerAbstractTile;
    }

    public TRControllerAbstractTile getLinkedControllerTile(World world) {
        return (linkedController != null ? ((TRControllerAbstractTile) world.getTileEntity(linkedController)) : null);
    }

    public SymbolTypeTransportRingsEnum getSymbolType() {
        if (getLinkedControllerTile(world) != null) {
            return getLinkedControllerTile(world).getSymbolType();
        }
        return SymbolTypeTransportRingsEnum.valueOf(0);
    }

    @Override
    public boolean canLinkTo() {
        return !isLinked();
    }


    // ---------------------------------------------------------------------------------
    // NBT data

    @Override
    public int getLinkId() {
        return linkId;
    }

    public TransportRings getRings() {
        if (rings == null) rings = new TransportRings(generateAndPostAddress(true), pos);

        return rings;
    }

    public TransportRings getClonedRings(BlockPos callerPos) {
        return getRings().cloneWithNewDistance(callerPos);
    }

    public void addRings(TransportRingsAbstractTile caller) {
        TransportRings clonedRings = caller.getClonedRings(this.pos);

        if (clonedRings.isInGrid()) {
            ringsMap.put(clonedRings.getAddresses(), clonedRings);

            markDirty();
        }
    }

    public void removeRingsFromMap(Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> addressMap) {
        if (ringsMap.remove(addressMap) != null) markDirty();
    }


    // ---------------------------------------------------------------------------------
    // States

    public void removeRings(Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> addressMap) {
        TransportRings rings = ringsMap.get(addressMap);
        if (rings != null) {
            TileEntity tile = world.getTileEntity(rings.getPos());
            if (tile instanceof TransportRingsAbstractTile) {
                ((TransportRingsAbstractTile) tile).removeRingsFromMap(getRings().getAddresses());
            }
        }
        removeRingsFromMap(addressMap);
    }

    public void removeAllRings() {
        for (Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> addressMap : new ArrayList<>(ringsMap.keySet())) {
            removeRings(addressMap);
        }
    }

    public ParamsSetResult setRingsParams(String name, int distance) {
        setNewRingsDistance(distance);
        setRingsName(name);
        return setRingsParams(null, null, null, name);
    }

    public void setRingsParams(Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> addressMap) {
        setRingsParams(null, addressMap, null, getRings().getName());
    }

    public void setRingsParams(TransportRingsAddress address, SymbolTypeTransportRingsEnum symbolType, String name) {
        setRingsName(name);
        setRingsParams(address, null, symbolType, name);
    }

    public ParamsSetResult setRingsParams(TransportRingsAddress address, Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> addressMap, SymbolTypeTransportRingsEnum symbolType, String name) {
        int x = pos.getX();
        int z = pos.getZ();

        int radius = AunisConfig.ringsConfig.rangeFlat;

        int y = pos.getY();
        int vertical = AunisConfig.ringsConfig.rangeVertical;

        List<TransportRingsAbstractTile> ringsTilesInRange = new ArrayList<>();

        for (BlockPos newRingsPos : BlockPos.getAllInBoxMutable(new BlockPos(x - radius, y - vertical, z - radius), new BlockPos(x + radius, y + vertical, z + radius))) {
            if (AunisBlocks.isInBlocksArray(world.getBlockState(newRingsPos).getBlock(), AunisBlocks.RINGS_BLOCKS) && !pos.equals(newRingsPos)) {
                TransportRingsAbstractTile newRingsTile = (TransportRingsAbstractTile) world.getTileEntity(newRingsPos);
                ringsTilesInRange.add(newRingsTile);
            }
        }

        Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> addressOld = getRings().getAddresses();

        removeAllRings();
        if (symbolType != null && addressMap == null)
            getRings().setAddress(symbolType, address);
        else if (addressMap != null) {
            getRings().setAddress(addressMap);
        } else {
            getRings().setAddress(addressOld);
        }
        getRings().setName(name);
        getRings().setRingsDistance(ringsDistance);

        for (TransportRingsAbstractTile newRingsTile : ringsTilesInRange) {
            this.addRings(newRingsTile);
            newRingsTile.addRings(this);
        }

        markDirty();
        return ParamsSetResult.OK;
    }

    public void setRingsName(String name){
        getRings().setName(name);
        ringsName = name;
        markDirty();
    }

    @Override
    protected void setWorldCreate(@Nonnull World worldIn) {
        setWorld(worldIn);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("rendererState", rendererState.serializeNBT());

        compound.setTag("ringsData", getRings().serializeNBT());
        if (linkedController != null) {
            compound.setLong("linkedController", linkedController.toLong());
            compound.setInteger("linkId", linkId);
        }

        compound.setInteger("ringsMapLength", ringsMap.size());

        int i = 0;
        for (TransportRings rings : ringsMap.values()) {
            compound.setTag("ringsMap" + i, rings.serializeNBT());

            i++;
        }

        compound.setTag("scheduledTasks", ScheduledTask.serializeList(scheduledTasks));

        compound.setInteger("teleportListSize", teleportList.size());
        for (int j = 0; j < teleportList.size(); j++)
            compound.setInteger("teleportList" + j, teleportList.get(j).getEntityId());

        compound.setInteger("excludedSize", excludedEntities.size());
        for (int j = 0; j < excludedEntities.size(); j++)
            compound.setInteger("excluded" + j, excludedEntities.get(j).getEntityId());

        compound.setLong("targetRingsPos", targetRingsPos.toLong());
        compound.setBoolean("busy", isBusy());

        if (node != null) {
            NBTTagCompound nodeCompound = new NBTTagCompound();
            node.save(nodeCompound);

            compound.setTag("node", nodeCompound);
        }

        compound.setBoolean("initiating", initiating);
        compound.setTag("itemHandler", itemStackHandler.serializeNBT());
        compound.setTag("energyStorage", energyStorage.serializeNBT());

        compound.setTag("config", config.serializeNBT());

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        try {
            rendererState.deserializeNBT(compound.getCompoundTag("rendererState"));
            ScheduledTask.deserializeList(compound.getCompoundTag("scheduledTasks"), scheduledTasks, this);

            teleportList = new ArrayList<>();
            int size = compound.getInteger("teleportListSize");
            for (int j = 0; j < size; j++)
                teleportList.add(world.getEntityByID(compound.getInteger("teleportList" + j)));

            excludedEntities = new ArrayList<>();
            size = compound.getInteger("excludedSize");
            for (int j = 0; j < size; j++)
                excludedEntities.add(world.getEntityByID(compound.getInteger("excluded" + j)));

            targetRingsPos = BlockPos.fromLong(compound.getLong("targetRingsPos"));

            if (compound.hasKey("ringsData")) getRings().deserializeNBT(compound.getCompoundTag("ringsData"));

            if (compound.hasKey("linkedController")) {
                linkedController = BlockPos.fromLong(compound.getLong("linkedController"));
                linkId = compound.getInteger("linkId");
            }

            if (compound.hasKey("ringsMapLength")) {
                int len = compound.getInteger("ringsMapLength");

                ringsMap.clear();

                for (int i = 0; i < len; i++) {
                    TransportRings rings = new TransportRings(compound.getCompoundTag("ringsMap" + i));

                    ringsMap.put(rings.getAddresses(), rings);
                }
            }

            if (node != null && compound.hasKey("node")) node.load(compound.getCompoundTag("node"));

            setBusy(compound.getBoolean("busy"));
            initiating = compound.getBoolean("initiating");

            itemStackHandler.deserializeNBT(compound.getCompoundTag("itemHandler"));
            ringsDistance = getRings().getRingsDistance();
            ringsName = getRings().getName();

            energyStorage.deserializeNBT(compound.getCompoundTag("energyStorage"));

            config.deserializeNBT(compound.getCompoundTag("config"));

        }catch (NullPointerException | IndexOutOfBoundsException | ClassCastException e) {
            Aunis.logger.warn("Exception at reading NBT");
            Aunis.logger.warn("If loading world used with previous version and nothing game-breaking doesn't happen, please ignore it");

            e.printStackTrace();
        }

        super.readFromNBT(compound);
    }

    @Override
    public State getState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_STATE:
                return rendererState;

            case GUI_STATE:
                return new TRGuiState(getRings().getAddresses(), getConfig());

            case GUI_UPDATE:
                return new TRGuiUpdate(energyStorage.getEnergyStoredInternally(), energyTransferedLastTick, ringsName, ringsDistance);


            default:
                return null;
        }
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_STATE:
                return new TransportRingsRendererState();

            case RINGS_START_ANIMATION:
            case RINGS_DISTANCE_UPDATE:
                return new TransportRingsStartAnimationRequest();

            case GUI_STATE:
                return new TRGuiState();

            case GUI_UPDATE:
                return new TRGuiUpdate();

            default:
                return null;
        }
    }

    public BlockPos getPosWithDistance(int distance){
        return (distance > 0 ? pos.up(distance + 2) : pos.down((distance * -1) - (distance < -2 ? 2 : 0)));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setState(StateTypeEnum stateType, State state) {
        int distance;
        long animationStart;
        switch (stateType) {
            case RENDERER_STATE:
                rendererState = ((TransportRingsRendererState) state);
                break;

            case RINGS_START_ANIMATION:
                distance = ((TransportRingsStartAnimationRequest) state).ringsDistance;
                animationStart = ((TransportRingsStartAnimationRequest) state).animationStart;
                AunisSoundHelper.playSoundEventClientSide(world, getPosWithDistance(distance), SoundEventEnum.RINGS_TRANSPORT);
                rendererState.ringsDistance = distance;
                rendererState.animationStart = animationStart;
                rendererState.isAnimationActive = true;
                rendererState.ringsUprising = true;
                break;

            case RINGS_DISTANCE_UPDATE:
                TransportRingsStartAnimationRequest s = ((TransportRingsStartAnimationRequest) state);
                rendererState.ringsDistance = s.ringsDistance;
                rendererState.ringsConfig = s.ringsConfig;
                break;

            case GUI_STATE:
                TRGuiState guiState = (TRGuiState) state;
                setRingsParams(guiState.trAdddressMap);
                config = guiState.config;
                break;

            case GUI_UPDATE:
                TRGuiUpdate guiUpdate = (TRGuiUpdate) state;
                energyStorage.setEnergyStoredInternally(guiUpdate.energyStored);
                energyTransferedLastTick = guiUpdate.transferedLastTick;
                if(!getRings().getName().equals(guiUpdate.ringsName) || getRings().getRingsDistance() != guiUpdate.distance)
                    setRingsParams(guiUpdate.ringsName, guiUpdate.distance);
                break;
            default:
                break;
        }
    }



    // -----------------------------------------------------------------
    // Tile entity config

    protected AunisTileEntityConfig config = new AunisTileEntityConfig();

    public enum ConfigOptions{
        RENDER_PLATFORM_MOVING(
                0, "platformMoving", AunisConfigOptionTypeEnum.BOOLEAN, "true",
                "Render platform moving part"
        ),
        RENDER_PLATFORM_BASE(
                1, "platformBase", AunisConfigOptionTypeEnum.BOOLEAN, "true",
                "Render platform base part"
        );

        public int id;
        public String label;
        public String[] comment;
        public AunisConfigOptionTypeEnum type;
        public String defaultValue;

        public int minInt;
        public int maxInt;

        ConfigOptions(int optionId, String label, AunisConfigOptionTypeEnum type, String defaultValue, String... comment){
            this(optionId, label, type, defaultValue, -1, -1, comment);
        }

        ConfigOptions(int optionId, String label, AunisConfigOptionTypeEnum type, String defaultValue, int minInt, int maxInt, String... comment){
            this.id = optionId;
            this.label = label;
            this.type = type;
            this.defaultValue = defaultValue;
            this.minInt = minInt;
            this.maxInt = maxInt;
            this.comment = comment;
        }
    }

    @Override
    public AunisTileEntityConfig getConfig() {
        return this.config;
    }

    @Override
    public void setConfig(AunisTileEntityConfig config) {
        for(AunisConfigOption o : config.getOptions()){
            this.config.getOption(o.id).setValue(o.getStringValue());
        }
        markDirty();
    }

    @Override
    public void initConfig(){
        if(getConfig().getOptions().size() != TransportRingsAbstractTile.ConfigOptions.values().length) {
            getConfig().clearOptions();
            for (TransportRingsAbstractTile.ConfigOptions option : TransportRingsAbstractTile.ConfigOptions.values()) {
                getConfig().addOption(
                        new AunisConfigOption(option.id)
                                .setType(option.type)
                                .setLabel(option.label)
                                .setValue(option.defaultValue)
                                .setDefaultValue(option.defaultValue)
                                .setMinInt(option.minInt)
                                .setMaxInt(option.maxInt)
                                .setComment(option.comment)
                );
            }
        }
    }



    public void updateLinkStatus() {
        BlockPos closestController = LinkingHelper.findClosestUnlinked(world, pos, new BlockPos(10, 5, 10), AunisBlocks.RINGS_CONTROLLERS, linkId);

        int linkId = closestController == null ? -1 : LinkingHelper.getLinkId();

        if (closestController != null) {
            TRControllerAbstractTile controllerTile = (TRControllerAbstractTile) world.getTileEntity(closestController);
            if (controllerTile != null) {
                controllerTile.setLinkedRings(pos, linkId);
            }
        }

        setLinkedController(closestController, linkId);
    }

    @Nonnull
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return renderBoundingBox;
    }

    @Override
    public void onChunkUnload() {
        if (node != null) node.remove();
    }

    // ------------------------------------------------------------------------
    // OpenComputers

    @Override
    public void invalidate() {
        if (node != null) node.remove();

        super.invalidate();
    }

    @Override
    @net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    public Node node() {
        return node;
    }

    @Override
    @net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    public void onConnect(Node node) {
    }

    @Override
    @net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    public void onDisconnect(Node node) {
    }

    // -----------------------------------------------------------------------------
    // Capabilities

    @Override
    @net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    public void onMessage(Message message) {
    }

    public void sendSignal(Object context, String name, Object... params) {
        Aunis.ocWrapper.sendSignalToReachable(node, (Context) context, name, params);
    }

    // ------------------------------------------------------------
    // Methods

    @net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getAunisVersion(Context context, Arguments args) {
        return new Object[]{Aunis.MOD_VERSION};
    }

    @net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getAddress(Context context, Arguments args) {
        Map<SymbolTypeTransportRingsEnum, List<String>> map = new HashMap<>();

        for (SymbolTypeTransportRingsEnum symbolType : SymbolTypeTransportRingsEnum.values()) {
            map.put(symbolType, getRings().getAddressNameList(symbolType));
        }

        return new Object[]{map};
    }

    @net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getAvailableRingsAddresses(Context context, Arguments args) {
        return new Object[]{ringsMap.keySet()};
    }

    @net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] isInGrid(Context context, Arguments args) {
        return new Object[]{getRings().isInGrid()};
    }

    @net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getName(Context context, Arguments args) {
        return new Object[]{getRings().getName()};
    }

    @net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] setName(Context context, Arguments args) {
        String name = args.checkString(0);
        setRingsName(name);

        return new Object[]{};
    }

    @net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getAvailableRings(Context context, Arguments args) {
        Map<String, String> values = new HashMap<>(ringsMap.size());

        for (TransportRings rings : ringsMap.values()) {
            for (SymbolTypeTransportRingsEnum symbolType : SymbolTypeTransportRingsEnum.values())
                values.put(rings.getAddress(symbolType).toString(), rings.getName());
        }

        return new Object[]{values};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] addSymbolToAddress(Context context, Arguments args) {
        String symbolName = "";
        int symbolId = -1;
        if(!args.isInteger(1))
            symbolName = args.checkString(1);
        else
            symbolId = args.checkInteger(1);

        int symbolType = args.checkInteger(0);
        int maxSymbolType = SymbolTypeTransportRingsEnum.values().length;
        if (symbolType < 0 || symbolType >= maxSymbolType)
            throw new IllegalArgumentException("bad argument #1 (symbolType must be in a range (0 - " + maxSymbolType + "))");

        SymbolTypeTransportRingsEnum symbolTypeConverted = SymbolTypeTransportRingsEnum.valueOf(symbolType);
        int maxSymbols = symbolTypeConverted.getSymbolsCount();
        if (symbolName.equals("") && (symbolId < 0 || symbolId >= maxSymbols))
            throw new IllegalArgumentException("bad argument #2 (address out of range, must be higher than -1 and lower than " + maxSymbols + ")");

        ocContext = context;
        if(symbolName.equals(""))
            return new Object[]{addSymbolToAddress(symbolTypeConverted.getSymbol(symbolId))};
        else
            return new Object[]{addSymbolToAddress(symbolTypeConverted.fromEnglishName(symbolName))};
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityEnergy.ENERGY) || (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == null) || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == null)
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemStackHandler);
        if (capability == CapabilityEnergy.ENERGY)
            return CapabilityEnergy.ENERGY.cast(getEnergyStorage());

        return super.getCapability(capability, facing);
    }

    // -----------------------------------------------------------------------------
    // Power system

    public int getEnergyTransferedLastTick() {
        return energyTransferedLastTick;
    }

    public abstract int getSupportedCapacitors();

    protected StargateClassicEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public int getPowerTier() {
        return currentPowerTier;
    }

    private void updatePowerTier() {
        int powerTier = 1;

        for (int i = 4; i < 7; i++) {
            if (!itemStackHandler.getStackInSlot(i).isEmpty()) {
                powerTier++;
            }
        }

        if (powerTier != currentPowerTier) {
            currentPowerTier = powerTier;

            energyStorage.clearStorages();

            for (int i = 4; i < 7; i++) {
                ItemStack stack = itemStackHandler.getStackInSlot(i);

                if (!stack.isEmpty()) {
                    energyStorage.addStorage(stack.getCapability(CapabilityEnergy.ENERGY, null));
                }
            }

            Aunis.logger.debug("Updated to power tier: " + powerTier);
        }
    }

    public static enum TransportRingsUpgradeEnum implements EnumKeyInterface<Item> {
        GOAULD_UPGRADE(AunisItems.CRYSTAL_GLYPH_GOAULD),
        ORI_UPGRADE(AunisItems.CRYSTAL_GLYPH_ORI);

        private static final EnumKeyMap<Item, TransportRingsAbstractTile.TransportRingsUpgradeEnum> idMap = new EnumKeyMap<>(values());
        public Item item;

        private TransportRingsUpgradeEnum(Item item) {
            this.item = item;
        }

        public static TransportRingsAbstractTile.TransportRingsUpgradeEnum valueOf(Item item) {
            return idMap.valueOf(item);
        }

        public static boolean contains(Item item) {
            return idMap.contains(item);
        }

        @Override
        public Item getKey() {
            return item;
        }
    }
}
