package mrjake.aunis.tileentity.transportrings;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import mrjake.aunis.Aunis;
import mrjake.aunis.AunisProps;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.gui.RingsGUI;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.packet.StateUpdateRequestToServer;
import mrjake.aunis.packet.transportrings.StartPlayerFadeOutToClient;
import mrjake.aunis.renderer.transportrings.TransportRingsAbstractRenderer;
import mrjake.aunis.sound.AunisSoundHelper;
import mrjake.aunis.sound.SoundEventEnum;
import mrjake.aunis.stargate.EnumScheduledTask;
import mrjake.aunis.state.*;
import mrjake.aunis.state.transportrings.TransportRingsGuiState;
import mrjake.aunis.state.transportrings.TransportRingsRendererState;
import mrjake.aunis.state.transportrings.TransportRingsStartAnimationRequest;
import mrjake.aunis.tesr.RendererInterface;
import mrjake.aunis.tesr.RendererProviderInterface;
import mrjake.aunis.tileentity.util.ScheduledTask;
import mrjake.aunis.tileentity.util.ScheduledTaskExecutorInterface;
import mrjake.aunis.transportrings.*;
import mrjake.aunis.util.AunisAxisAlignedBB;
import mrjake.aunis.util.ILinkable;
import mrjake.aunis.util.LinkingHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

import static mrjake.aunis.transportrings.TransportRingsAddress.MAX_SYMBOLS;

@Optional.InterfaceList({@Optional.Interface(iface = "li.cil.oc.api.network.Environment", modid = "opencomputers"), @Optional.Interface(iface = "li.cil.oc.api.network.WirelessEndpoint", modid = "opencomputers")})
public abstract class TransportRingsAbstractTile extends TileEntity implements ITickable, RendererProviderInterface, StateProviderInterface, ScheduledTaskExecutorInterface, ILinkable, Environment {
    public static final int FADE_OUT_TOTAL_TIME = 2 * 20; // 2s
    public static final int TIMEOUT_TELEPORT = FADE_OUT_TOTAL_TIME / 2;
    public static final int TIMEOUT_FADE_OUT = (int) (30 + TransportRingsAbstractRenderer.INTERVAL_UPRISING * TransportRingsAbstractRenderer.RING_COUNT + TransportRingsAbstractRenderer.ANIMATION_SPEED_DIVISOR * Math.PI);
    public static final int RINGS_CLEAR_OUT = (int) (15 + TransportRingsAbstractRenderer.INTERVAL_FALLING * TransportRingsAbstractRenderer.RING_COUNT + TransportRingsAbstractRenderer.ANIMATION_SPEED_DIVISOR * Math.PI);
    // ---------------------------------------------------------------------------------
    // Ticking and loading

    protected AunisAxisAlignedBB LOCAL_TELEPORT_BOX = new AunisAxisAlignedBB(-1, 2, -1, 2, 4.5, 2);
    private List<BlockPos> invisibleBlocksTemplate = Arrays.asList(new BlockPos(0, 2, 2), new BlockPos(1, 2, 2), new BlockPos(2, 2, 1));
    protected AunisAxisAlignedBB globalTeleportBox;
    protected List<Entity> teleportList = new ArrayList<>();
    protected BlockPos lastPos = BlockPos.ORIGIN;
    private AxisAlignedBB renderBoundingBox = TileEntity.INFINITE_EXTENT_AABB;

    @Override
    public void update() {
        if (!world.isRemote) {
            ScheduledTask.iterate(scheduledTasks, world.getTotalWorldTime());

            if(getRings().getAddress() == null || getRings().getAddress().size() < 4)
                generateAddress(true);

            if (!lastPos.equals(pos)) {
                lastPos = pos;

                getRings().setPos(pos);
                updateRingsDistance();
                setRingsParams(getRings().getAddress(), getRings().getName());
                updateLinkStatus();

                markDirty();
            }
        }
    }

    @Override
    public void onLoad() {
        if (world.isRemote) {
            renderer = getNewRenderer();
            AunisPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.RENDERER_STATE));
        }
        if (!world.isRemote) {
            setBarrierBlocks(false, false);
            generateAddress(false);
            globalTeleportBox = LOCAL_TELEPORT_BOX.offset(pos);
        }
        Aunis.ocWrapper.joinOrCreateNetwork(this);
    }

    // ---------------------------------------------------------------------------------
    // Address system

    public TransportRingsAddress generateAndPostAddress(boolean reset) {
        Random random = new Random(pos.hashCode() * 31L + world.provider.getDimension());
        if (reset) {
            TransportRingsAddress address = new TransportRingsAddress();
            address.generate(random);
            return address;
        }
        else{
            return getRings().getAddress();
        }
    }

    public void generateAddress(boolean reset) {
        setRingsParams(generateAndPostAddress(reset));
    }

    // ---------------------------------------------------------------------------------
    // Adjustable distance

    public int ringsDistance = 2;

    public void setNewRingsDistance(int newRingsDistance) {
        getRings().setRingsDistance(newRingsDistance);
        updateRingsDistance();
    }

    public void updateRingsDistance(){
        ringsDistance = getRings().getRingsDistance();
        LOCAL_TELEPORT_BOX = new AunisAxisAlignedBB(-1, ringsDistance, -1, 2, ringsDistance + 2.5, 2);
        invisibleBlocksTemplate = Arrays.asList(new BlockPos(0, ringsDistance, 2), new BlockPos(1, ringsDistance, 2), new BlockPos(2, ringsDistance, 1));
        globalTeleportBox = LOCAL_TELEPORT_BOX.offset(pos);
        renderBoundingBox = ringsDistance < 0 ? new AunisAxisAlignedBB(-5.5, 5, -0.5, 5.5, ringsDistance - 15, 0.5) : new AunisAxisAlignedBB(-5.5, -5, -0.5, 5.5, ringsDistance + 15, 0.5);
        markDirty();
    }


    // ---------------------------------------------------------------------------------
    // Scheduled task

    List<ScheduledTask> scheduledTasks = new ArrayList<>();

    @Override
    public void addTask(ScheduledTask scheduledTask) {
        scheduledTask.setExecutor(this);
        scheduledTask.setTaskCreated(world.getTotalWorldTime());

        scheduledTasks.add(scheduledTask);
        markDirty();
    }

    @Override
    public void executeTask(EnumScheduledTask scheduledTask, NBTTagCompound customData) {
        switch (scheduledTask) {
            case RINGS_START_ANIMATION:
                animationStart();
                setBarrierBlocks(true, true);

                addTask(new ScheduledTask(EnumScheduledTask.RINGS_FADE_OUT));
                addTask(new ScheduledTask(EnumScheduledTask.RINGS_SOLID_BLOCKS, 35));
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
                if(targetTile == null) break;
                int targetRingsHeight = targetTile.getRings().getRingsDistance();

                for (Entity entity : teleportList) {
                    if (!excludedEntities.contains(entity)) {
                        BlockPos ePos = entity.getPosition().add(teleportVector);
                        double y = targetRingsPos.getY() + targetRingsHeight;
                        entity.setPositionAndUpdate(ePos.getX(), y, ePos.getZ());
                    }
                }

                teleportList.clear();
                excludedEntities.clear();

                addTask(new ScheduledTask(EnumScheduledTask.RINGS_CLEAR_OUT));
                break;

            case RINGS_CLEAR_OUT:
                setBarrierBlocks(false, false);
                setBusy(false);

                TransportRingsAbstractTile targetRingsTile = (TransportRingsAbstractTile) world.getTileEntity(targetRingsPos);
                if (targetRingsTile != null) targetRingsTile.setBusy(false);

                sendSignal(ocContext, "transportrings_teleport_finished", initiating);

                break;

            default:
                throw new UnsupportedOperationException("EnumScheduledTask." + scheduledTask.name() + " not implemented on " + this.getClass().getName());
        }
    }


    // ---------------------------------------------------------------------------------
    // Teleportation
    protected BlockPos targetRingsPos = new BlockPos(0, 0, 0);
    protected List<Entity> excludedEntities = new ArrayList<>();
    protected Object ocContext;
    protected boolean initiating;

    /**
     * True if there is an active transport.
     */
    private boolean busy = false;

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public List<Entity> startAnimationAndTeleport(BlockPos targetRingsPos, List<Entity> excludedEntities, int waitTime, boolean initiating) {
        this.targetRingsPos = targetRingsPos;
        this.excludedEntities = excludedEntities;

        addTask(new ScheduledTask(EnumScheduledTask.RINGS_START_ANIMATION, waitTime));
        sendSignal(ocContext, "transportrings_teleport_start", initiating);
        this.initiating = initiating;

        return world.getEntitiesWithinAABB(Entity.class, globalTeleportBox);
    }

    public void animationStart() {
        rendererState.animationStart = world.getTotalWorldTime();
        rendererState.ringsUprising = true;
        rendererState.isAnimationActive = true;
        rendererState.ringsDistance = getRings().getRingsDistance();

        NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
        AunisPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.RINGS_START_ANIMATION, new TransportRingsStartAnimationRequest(rendererState.animationStart, rendererState.ringsDistance)), point);
    }

    public TransportResult addSymbolToAddress(int symbolId){
        SymbolTransportRingsEnum symbol = SymbolTransportRingsEnum.valueOf(symbolId);
        if(canAddSymbol(symbol)){
            dialedAddress.add(symbol);
            markDirty();
            if(ringsWillLock()){
                if(!attemptTransportTo(dialedAddress, EnumScheduledTask.RINGS_START_ANIMATION.waitTicks).ok()) {
                    dialedAddress.clear();
                    markDirty();
                }
            }
            return TransportResult.OK;
        }
        dialedAddress.clear();
        markDirty();
        return TransportResult.NO_SUCH_ADDRESS;
    }

    protected TransportRingsAddress dialedAddress = new TransportRingsAddress();

    public boolean canAddSymbol(SymbolTransportRingsEnum symbol){
        if(dialedAddress.contains(symbol)) return false;
        if(dialedAddress.size() > MAX_SYMBOLS){
            dialedAddress.clear();
            return false;
        }
        return !isBusy();
    }

    public boolean ringsWillLock(){
        return (dialedAddress.size() > MAX_SYMBOLS && dialedAddress.getLast().origin());
    }

    /**
     * Checks if Rings are linked to Rings at given address.
     * If yes, it starts teleportation.
     *
     * @param address Target rings address
     */
    public TransportResult attemptTransportTo(TransportRingsAddress address, int waitTime) {
        if (checkIfObstructed()) {
            return TransportResult.OBSTRUCTED;
        }
        if (isBusy()) {
            return TransportResult.BUSY;
        }

        TransportRingsAddress strippedAddress = address.getLast().origin() ? address.stripOrigin() : address;

        TransportRings rings = ringsMap.get(strippedAddress.calAddress());

        // Binding exists
        if (rings != null) {
            BlockPos targetRingsPos = rings.getPos();
            TransportRingsAbstractTile targetRingsTile = (TransportRingsAbstractTile) world.getTileEntity(targetRingsPos);

            if (targetRingsTile == null || targetRingsTile.checkIfObstructed()) {
                return TransportResult.OBSTRUCTED_TARGET;
            }

            if (targetRingsTile.isBusy()) {
                return TransportResult.BUSY_TARGET;
            }

            this.setBusy(true);
            targetRingsTile.setBusy(true);

            List<Entity> excludedFromReceivingSite = world.getEntitiesWithinAABB(Entity.class, globalTeleportBox);
            List<Entity> excludedEntities = targetRingsTile.startAnimationAndTeleport(pos, excludedFromReceivingSite, waitTime, false);
            startAnimationAndTeleport(targetRingsPos, excludedEntities, waitTime, true);

            return TransportResult.OK;
        } else {
            return TransportResult.NO_SUCH_ADDRESS;
        }
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
                        Aunis.logger.info("TransportRings: " + newPos + " obstructed with " + world.getBlockState(newPos));
                        return true;
                    }
                }
            }
        }

        return false;
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
                        if (world.getBlockState(newPos).getBlock() == AunisBlocks.INVISIBLE_BLOCK) world.setBlockToAir(newPos);
                    }
                }
            }
        }
    }


    // ---------------------------------------------------------------------------------
    // Controller
    private BlockPos linkedController;

    public void setLinkedController(BlockPos pos, int linkId) {
        this.linkedController = pos;
        this.linkId = linkId;

        markDirty();
    }

    public BlockPos getLinkedController() {
        return linkedController;
    }

    public boolean isLinked() {
        return linkedController != null && world.getTileEntity(linkedController) instanceof TRControllerAbstractTile;
    }

    public TRControllerAbstractTile getLinkedControllerTile(World world) {
        return (linkedController != null ? ((TRControllerAbstractTile) world.getTileEntity(linkedController)) : null);
    }

    @Override
    public boolean canLinkTo() {
        return !isLinked();
    }

    private int linkId = -1;

    @Override
    public int getLinkId() {
        return linkId;
    }

    // ---------------------------------------------------------------------------------
    // Rings network
    protected TransportRings rings;

    protected TransportRings getRings() {
        if (rings == null) rings = new TransportRings(generateAndPostAddress(true), pos);

        return rings;
    }

    public TransportRings getClonedRings(BlockPos callerPos) {
        return getRings().cloneWithNewDistance(callerPos);
    }

    public Map<String, TransportRings> ringsMap = new HashMap<>();

    public void addRings(TransportRingsAbstractTile caller) {
        TransportRings clonedRings = caller.getClonedRings(this.pos);

        if (clonedRings.isInGrid()) {
            ringsMap.put(clonedRings.getAddress().calAddress(), clonedRings);

            markDirty();
        }
    }

    public void removeRingsFromMap(String address) {
        if (ringsMap.remove(address) != null) markDirty();
    }

    public void removeRings(String address) {
        TransportRings rings = ringsMap.get(address);
        if (rings != null) {
            TileEntity tile = world.getTileEntity(rings.getPos());
            if (tile instanceof TransportRingsAbstractTile) {
                ((TransportRingsAbstractTile) tile).removeRingsFromMap(getRings().getAddress().calAddress());
            }
        }
        removeRingsFromMap(address);
    }

    public void removeAllRings() {
        for (String address : new ArrayList<>(ringsMap.keySet())) {
            removeRings(address);
        }
    }

    public ParamsSetResult setRingsParams(String name, int distance) {
        setNewRingsDistance(distance);
        return setRingsParams(name);
    }
    public ParamsSetResult setRingsParams(TransportRingsAddress address) {
        return setRingsParams(address, getRings().getName());
    }
    public ParamsSetResult setRingsParams(String name) {
        return setRingsParams(getRings().getAddress(), name);
    }
    public ParamsSetResult setRingsParams(TransportRingsAddress address, String name) {
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

        removeAllRings();

        getRings().setAddress(address);
        getRings().setName(name);
        getRings().setRingsDistance(ringsDistance);

        for (TransportRingsAbstractTile newRingsTile : ringsTilesInRange) {
            this.addRings(newRingsTile);
            newRingsTile.addRings(this);
        }

        markDirty();
        return ParamsSetResult.OK;
    }


    // ---------------------------------------------------------------------------------
    // NBT data

    @Override
    protected void setWorldCreate(World worldIn) {
        setWorld(worldIn);
    }

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
        compound.setInteger("ringsDistance", ringsDistance);

        if (node != null) {
            NBTTagCompound nodeCompound = new NBTTagCompound();
            node.save(nodeCompound);

            compound.setTag("node", nodeCompound);
        }

        compound.setBoolean("initiating", initiating);

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
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

        } catch (NullPointerException | IndexOutOfBoundsException | ClassCastException e) {
            Aunis.logger.warn("Exception at reading NBT");
            Aunis.logger.warn("If loading world used with previous version and nothing game-breaking doesn't happen, please ignore it");

            e.printStackTrace();
        }

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

                ringsMap.put(rings.getAddress().calAddress(), rings);
            }
        }

        if (node != null && compound.hasKey("node")) node.load(compound.getCompoundTag("node"));

        setBusy(compound.getBoolean("busy"));
        ringsDistance = compound.getInteger("ringsDistance");
        initiating = compound.getBoolean("initiating");

        super.readFromNBT(compound);
    }


    // ---------------------------------------------------------------------------------
    // States

    @Override
    public State getState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_STATE:
                return rendererState;

            case GUI_STATE:
                return new TransportRingsGuiState(getRings(), ringsMap.values());

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
                return new TransportRingsStartAnimationRequest();

            case GUI_STATE:
                return new TransportRingsGuiState();

            default:
                return null;
        }
    }

    @SideOnly(Side.CLIENT)
    private RingsGUI openGui;

    @Override
    @SideOnly(Side.CLIENT)
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {
            case RENDERER_STATE:
                renderer.setState((TransportRingsRendererState) state);
                break;

            case RINGS_START_ANIMATION:
                int distance = ((TransportRingsStartAnimationRequest) state).ringsDistance;
                AunisSoundHelper.playSoundEventClientSide(world, (distance > 0 ? pos.up(distance+2) : pos.down((distance*-1) - (distance < -2 ? 2 : 0))), SoundEventEnum.RINGS_TRANSPORT);
                renderer.animationStart(((TransportRingsStartAnimationRequest) state).animationStart, distance);
                break;

            case GUI_STATE:

                if (openGui == null || !openGui.isOpen) {
                    openGui = new RingsGUI(pos, (TransportRingsGuiState) state);
                    Minecraft.getMinecraft().displayGuiScreen(openGui);
                } else {
                    openGui.state = (TransportRingsGuiState) state;
                }

                break;

            default:
                break;
        }
    }

    public void updateLinkStatus() {
        BlockPos closestController = LinkingHelper.findClosestUnlinked(world, pos, new BlockPos(10, 5, 10), AunisBlocks.TR_CONTROLLER_BLOCK, linkId);

        int linkId = closestController == null ? -1 : LinkingHelper.getLinkId();

        if (closestController != null) {
            TRControllerAbstractTile controllerTile = (TRControllerAbstractTile) world.getTileEntity(closestController);
            controllerTile.setLinkedRings(pos, linkId);
        }

        setLinkedController(closestController, linkId);
    }

    // ---------------------------------------------------------------------------------
    // Renders
    TransportRingsAbstractRenderer renderer;
    TransportRingsRendererState rendererState = new TransportRingsRendererState();

    @Override
    public RendererInterface getRenderer() {
        return renderer;
    }

    public abstract TransportRingsAbstractRenderer getNewRenderer();

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return renderBoundingBox;
    }


    // ------------------------------------------------------------------------
    // OpenComputers

    @Override
    public void onChunkUnload() {
        if (node != null) node.remove();
    }

    @Override
    public void invalidate() {
        if (node != null) node.remove();

        super.invalidate();
    }

    // ------------------------------------------------------------
    // Node-related work
    private Node node = Aunis.ocWrapper.createNode(this, "transportrings");

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
    public Object[] isInGrid(Context context, Arguments args) {
        return new Object[]{rings.isInGrid()};
    }

    @net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getAddress(Context context, Arguments args) {
        if (!rings.isInGrid()) return new Object[]{"NOT_IN_GRID", "Use setAddressAndName"};

        return new Object[]{rings.getAddress()};
    }

    @net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getName(Context context, Arguments args) {
        if (!rings.isInGrid()) return new Object[]{"NOT_IN_GRID", "Use setAddressAndName"};

        return new Object[]{rings.getName()};
    }

    /*@net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] setAddress(Context context, Arguments args) {
        if (!rings.isInGrid()) return new Object[]{"NOT_IN_GRID", "Use setAddressAndName"};

        int address = args.checkInteger(0);

        if (address < 1 || address > 6)
            throw new IllegalArgumentException("bad argument #1 (address out of range, allowed <1..6>)");

        return new Object[]{setRingsParams(address, rings.getName())};
    }*/

    @net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] setName(Context context, Arguments args) {
        if (!rings.isInGrid()) return new Object[]{"NOT_IN_GRID", "Use setAddressAndName"};

        String name = args.checkString(0);
        setRingsParams(rings.getAddress(), name);

        return new Object[]{};
    }

    /*@net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] setAddressAndName(Context context, Arguments args) {
        int address = args.checkInteger(0);
        String name = args.checkString(1);

        if (address < 1 || address > 6)
            throw new IllegalArgumentException("bad argument #1 (address out of range, allowed <1..6>)");

        return new Object[]{setRingsParams(address, name)};
    }

    @net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getAvailableRings(Context context, Arguments args) {
        if (!rings.isInGrid()) return new Object[]{"NOT_IN_GRID", "Use setAddressAndName"};

        Map<Integer, String> values = new HashMap<>(ringsMap.size());

        for (Map.Entry<Integer, TransportRings> rings : ringsMap.entrySet())
            values.put(rings.getKey(), rings.getValue().getName());

        return new Object[]{values};
    }*/

    @net.minecraftforge.fml.common.Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getAvailableRingsAddresses(Context context, Arguments args) {
        if (!rings.isInGrid()) return new Object[]{"NOT_IN_GRID", "Use setAddressAndName"};

        return new Object[]{ringsMap.keySet()};
    }


    /*@Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] attemptTransportTo(Context context, Arguments args) {
        if (!rings.isInGrid()) return new Object[]{"NOT_IN_GRID", "Use setAddressAndName"};

        int address = args.checkInteger(0);

        if (address < 1 || address > 6)
            throw new IllegalArgumentException("bad argument #1 (address out of range, allowed <1..6>)");

        ocContext = context;

        return new Object[]{attemptTransportTo(address, 0)};
    }*/
}
