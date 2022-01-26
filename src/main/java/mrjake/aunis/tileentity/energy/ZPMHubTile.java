package mrjake.aunis.tileentity.energy;

import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.*;
import mrjake.aunis.Aunis;
import mrjake.aunis.AunisProps;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.gui.container.zpmhub.ZPMHubContainerGuiUpdate;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.packet.StateUpdateRequestToServer;
import mrjake.aunis.renderer.energy.ZPMHubRenderer;
import mrjake.aunis.renderer.energy.ZPMHubRendererState;
import mrjake.aunis.sound.AunisSoundHelper;
import mrjake.aunis.sound.SoundPositionedEnum;
import mrjake.aunis.stargate.power.StargateAbstractEnergyStorage;
import mrjake.aunis.state.State;
import mrjake.aunis.state.StateProviderInterface;
import mrjake.aunis.state.StateTypeEnum;
import mrjake.aunis.util.AunisItemStackHandler;
import mrjake.aunis.zpm.EnumZPMState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.IntStream;

@Optional.InterfaceList({@Optional.Interface(iface = "li.cil.oc.api.network.Environment", modid = "opencomputers"), @Optional.Interface(iface = "li.cil.oc.api.network.WirelessEndpoint", modid = "opencomputers")})
public class ZPMHubTile extends TileEntity implements ITickable, ICapabilityProvider, StateProviderInterface, Environment, WirelessEndpoint {

    private long animationStart;
    private boolean isPutting = false;
    private int zpmsCount = 0;
    public int zpmAnimated = 0;

    private ArrayList<EnumZPMState> zpmStates = new ArrayList<EnumZPMState>(3);
    public ArrayList<Integer> lastZPMPowerLevel = new ArrayList<Integer>(3);
    public ArrayList<Integer> lastZPMPower = new ArrayList<Integer>(3);



    // ZPMs
    public final AunisItemStackHandler itemStackHandler = new AunisItemStackHandler(3) {

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            Item item = stack.getItem();
            switch (slot) {
                case 0:
                case 1:
                case 2:
                    if(zpmAnimated != 0) return false;
                    return item == Item.getItemFromBlock(AunisBlocks.ZPM);
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
                case 0:
                case 1:
                case 2:
                    boolean putting = !getStackInSlot(slot).isEmpty();
                    int zpmsCount = 0;
                    for(int i = 0; i < 3; i++){
                        if(lastZPMPowerLevel.size() < i+1)
                            lastZPMPowerLevel.add(0);
                        if(lastZPMPower.size() < i+1)
                            lastZPMPower.add(-1);

                        if(!getStackInSlot(i).isEmpty()) {
                            zpmsCount++;
                            lastZPMPowerLevel.set(i, this.getStackInSlot(i).getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored());
                        }
                        else {
                            lastZPMPower.set(i, -1);
                        }
                    }
                    if(getZpmsCount() != zpmsCount)
                        initAnimation(putting, zpmsCount, slot);
                    break;
            }

            markDirty();
        }
    };

    public boolean tryInsertZPM(EntityPlayer player, EnumHand hand){
        ItemStack stack = player.getHeldItem(hand);

        if(stack.isEmpty())
            return false;
        Iterator<Integer> iter = IntStream.range(0, itemStackHandler.getSlots()).iterator();
        while (iter.hasNext()) {
            int slot = iter.next();

            if(itemStackHandler.getStackInSlot(slot).isEmpty() && itemStackHandler.isItemValid(slot, stack)) {
                player.setHeldItem(hand, itemStackHandler.insertItem(slot, stack, false));
                return true;
            }
        }
        return false;
    }

    public boolean tryGetZPM(EntityPlayer player, EnumHand hand){
        ItemStack stack = player.getHeldItem(hand);

        if(!stack.isEmpty())
            return false;
        Iterator<Integer> iter = IntStream.range(0, itemStackHandler.getSlots()).iterator();
        while (iter.hasNext()) {
            int slot = iter.next();

            if(!itemStackHandler.getStackInSlot(slot).isEmpty() && stack.isEmpty()) {
                player.setHeldItem(hand, itemStackHandler.extractItem(slot, 1, false));
                return true;
            }
        }
        return false;
    }

    public int getZpmsCount(){
        return this.zpmsCount;
    }

    public int getEnergyLevelOfZPM(int zpm){
        ItemStack zpmStack = this.itemStackHandler.getStackInSlot(zpm);

        for(int i = 0; i < 3; i++){
            if(lastZPMPowerLevel.size() < i+1)
                lastZPMPowerLevel.add(0);
        }
        if(zpmStack.isEmpty())
            return lastZPMPowerLevel.get(zpm);

        int energyStored = Objects.requireNonNull(zpmStack.getCapability(CapabilityEnergy.ENERGY, null)).getEnergyStored();
        int maxEnergyStored = AunisConfig.powerConfig.zpmEnergyStorage;
        lastZPMPowerLevel.set(zpm, Math.round(((energyStored/(float)maxEnergyStored * 10)/2)));
        return Math.round(((energyStored/(float)maxEnergyStored * 10)/2));
    }

    public long getEnergyInZPM(int zpm){
        ItemStack zpmStack = this.itemStackHandler.getStackInSlot(zpm-1);
        if(zpmStack.isEmpty())
            return 0;
        return Objects.requireNonNull(zpmStack.getCapability(CapabilityEnergy.ENERGY, null)).getEnergyStored();
    }

    public int extractEnergyFromZPM(int zpm, int energy, boolean simulate){
        if(zpmsCount >= zpm){
            ItemStack zpmStack = this.itemStackHandler.getStackInSlot(zpm-1);
            return Objects.requireNonNull(zpmStack.getCapability(CapabilityEnergy.ENERGY, null)).extractEnergy(energy*3, simulate);
        }
        return 0;
    }


    public void initAnimation(boolean isPutting, int zpmsCount, int slot){
        this.animationStart = world.getTotalWorldTime();
        this.isPutting = isPutting;
        this.zpmsCount = zpmsCount;
        this.zpmAnimated = slot+1;
        markDirty();
    }

    public void setZPMStatus(int slot, boolean down){

        if(!down)
            lastZPMPower.set(slot, -1);
        else
            lastZPMPower.set(slot, (int) getEnergyInZPM(slot+1));
        zpmAnimated = 0;
        if(down && Objects.requireNonNull(itemStackHandler.getStackInSlot(slot).getCapability(CapabilityEnergy.ENERGY, null)).getEnergyStored() > 0)
                AunisSoundHelper.playPositionedSound(world, pos, SoundPositionedEnum.ZPMHUB_ZPM_ACTIVATED, true);
        markDirty();
    }

    public void initZPMSound(boolean down){
        if(down)
            AunisSoundHelper.playPositionedSound(world, pos, SoundPositionedEnum.ZPMHUB_ZPM_SLIDE_IN, true);
        else
            AunisSoundHelper.playPositionedSound(world, pos, SoundPositionedEnum.ZPMHUB_ZPM_SLIDE_OUT, true);
    }


    @Override
    public void rotate(Rotation rotation) {
        IBlockState state = world.getBlockState(pos);

        int rotationOrig = state.getValue(AunisProps.ROTATION_HORIZONTAL);
        world.setBlockState(pos, state.withProperty(AunisProps.ROTATION_HORIZONTAL, rotation.rotate(rotationOrig, 16)));
    }

    // ---------------------------------------------------------------------------------------------------
    // Renderer state

    private ZPMHubRendererState rendererStateClient;

    public ZPMHubRendererState getRendererStateClient() {
        return rendererStateClient;
    }

    // ---------------------------------------------------------------------------------------------------
    // Loading and ticking

    private NetworkRegistry.TargetPoint targetPoint;

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            targetPoint = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
        } else {
            AunisPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.RENDERER_STATE));
        }
    }

    private BlockPos lastPos = BlockPos.ORIGIN;

    private boolean addedToNetwork;

    @Override
    public void update() {

        if(lastZPMPowerLevel.size() < 3){
            // init zpmdown list
            for(int i = 0; i < (3 - lastZPMPowerLevel.size()); i++)
                lastZPMPowerLevel.add(0);
        }
        if(lastZPMPower.size() < 3){
            // init zpmdown list
            for(int i = 0; i < (3 - lastZPMPower.size()); i++)
                lastZPMPower.add(-1);
        }

        if (!world.isRemote) {
            if (!addedToNetwork) {
                addedToNetwork = true;
                Aunis.ocWrapper.joinWirelessNetwork(this);
                Aunis.ocWrapper.joinOrCreateNetwork(this);
            }
            if (!lastPos.equals(pos)) {
                lastPos = pos;
            }

            if((world.getTotalWorldTime() - animationStart) > ZPMHubRenderer.ANIMATION_LENGTH){
                zpmAnimated = 0;
            }
            if(zpmAnimated == 0){
                int zpmsCount = 0;
                for(int i = 0; i < 3; i++) {
                    if(!itemStackHandler.getStackInSlot(i).isEmpty())
                        zpmsCount++;
                }
                if(this.zpmsCount != zpmsCount) {
                    this.zpmsCount = zpmsCount;
                }
            }

            // power system draw
            for (EnumFacing facing : EnumFacing.VALUES) {
                if(facing == EnumFacing.UP) continue; // prevents from getting energy from top of the zpmhub
                TileEntity tile = world.getTileEntity(pos.offset(facing));

                if (tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {

                    for(int i = 0; i < 3; i++){
                        ItemStack zpmStack = itemStackHandler.getStackInSlot(i);
                        if(lastZPMPower.size() < i+1)
                            lastZPMPower.add(-1);
                        if(zpmStack.isEmpty())
                            // zpm not found -> skip extract process
                            continue;

                        int extracted = extractEnergyFromZPM(i+1, AunisConfig.powerConfig.stargateMaxEnergyTransfer, true);
                        extracted = Objects.requireNonNull(tile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite())).receiveEnergy(extracted, false);
                        extractEnergyFromZPM(i+1, extracted, false);
                    }
                }
            }

            long energyStored = 0;
            for(int i = 0; i < 3; i++){
                if(lastZPMPowerLevel.size() < i+1)
                    lastZPMPowerLevel.add(0);

                if(itemStackHandler.getStackInSlot(i).isEmpty()) continue;
                int energyStoredTemp = Objects.requireNonNull(itemStackHandler.getStackInSlot(i).getCapability(CapabilityEnergy.ENERGY, null)).getEnergyStored();
                lastZPMPower.set(i, energyStoredTemp);
                energyStored += energyStoredTemp;
            }
            energyTransferedLastTick = energyStored - energyStoredLastTick;
            energyStoredLastTick = energyStored;

            markDirty();
        }
        else
            AunisPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.RENDERER_UPDATE));
    }


    // -----------------------------------------------------------------------------
    // States

    protected void sendState(StateTypeEnum type) {
        if (world.isRemote) return;

        if (targetPoint != null) {
            State state = getState(type);
            AunisPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, type, state), targetPoint);
        } else {
            Aunis.logger.debug("targetPoint was null trying to send " + type + " from " + this.getClass().getCanonicalName());
        }
    }

    @Override
    public State getState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_STATE:
            case RENDERER_UPDATE:
                return new ZPMHubRendererState();
            case GUI_UPDATE:
                return new ZPMHubContainerGuiUpdate(zpmsCount, energyTransferedLastTick);

            default:
                throw new UnsupportedOperationException("EnumStateType." + stateType.name() + " not implemented on " + this.getClass().getName());
        }
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_STATE:
            case RENDERER_UPDATE:
                return new ZPMHubRendererState();
            case GUI_UPDATE:
                return new ZPMHubContainerGuiUpdate();

            default:
                throw new UnsupportedOperationException("EnumStateType." + stateType.name() + " not implemented on " + this.getClass().getName());
        }
    }

    @Override
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {
            case RENDERER_STATE:
                rendererStateClient = ((ZPMHubRendererState) state).initClient(pos, animationStart, isPutting, zpmsCount, zpmAnimated, energyTransferedLastTick, lastZPMPowerLevel, lastZPMPower);
                break;
            case RENDERER_UPDATE:
                if(getRendererStateClient() == null) rendererStateClient = ((ZPMHubRendererState) state).initClient(pos, animationStart, isPutting, zpmsCount, zpmAnimated, energyTransferedLastTick, lastZPMPowerLevel, lastZPMPower);
                else rendererStateClient = getRendererStateClient();
                rendererStateClient.energyTransferedLastTick = energyTransferedLastTick;
                rendererStateClient.isPutting = isPutting;
                rendererStateClient.zpmsCount = zpmsCount;
                rendererStateClient.animationStart = animationStart;
                rendererStateClient.zpmAnimated = zpmAnimated;
                rendererStateClient.lastZPMPowerLevel = lastZPMPowerLevel;
                rendererStateClient.lastZPMPower = lastZPMPower;
                break;
            case GUI_UPDATE:
                if(!(state instanceof ZPMHubContainerGuiUpdate))
                    break;
                ZPMHubContainerGuiUpdate guiUpdate = (ZPMHubContainerGuiUpdate) state;
                zpmsCount = guiUpdate.zpmsCount;
                markDirty();
                break;

            default:
                throw new UnsupportedOperationException("EnumStateType." + stateType.name() + " not implemented on " + this.getClass().getName());
        }
        markDirty();
    }

    // -----------------------------------------------------------------------------
    // Capabilities

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityEnergy.ENERGY) || (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemStackHandler);

        if (capability == CapabilityEnergy.ENERGY)
            return CapabilityEnergy.ENERGY.cast(getEnergyStorage());

        return super.getCapability(capability, facing);
    }

    public StargateAbstractEnergyStorage getEnergyStorage(){
        return energyStorage;
    }

    // -----------------------------------------------------------------------------
    // Power system

    protected long energyStoredLastTick = 0;
    protected long energyTransferedLastTick = 0;

    public long getEnergyTransferedLastTick() {
        return energyTransferedLastTick;
    }

    private StargateAbstractEnergyStorage energyStorage = new StargateAbstractEnergyStorage() {

        @Override
        protected void onEnergyChanged() {
            markDirty();
        }

    };

    // ---------------------------------------------------------------------------------------------------
    // NBT

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("itemHandler", itemStackHandler.serializeNBT());

        compound.setLong("animationStart", animationStart);
        compound.setInteger("zpmsCount", zpmsCount);
        compound.setBoolean("isPutting", isPutting);

        for(int i = 0; i < 3; i++){
            if(lastZPMPower.size() < i+1) lastZPMPower.add(-1);
            if(lastZPMPowerLevel.size() < i+1) lastZPMPowerLevel.add(-1);

            compound.setInteger("zpmPower_" + i, lastZPMPower.get(i));
            compound.setInteger("zpmLevel_" + i, lastZPMPowerLevel.get(i));
        }

        if (node != null) {
            NBTTagCompound nodeCompound = new NBTTagCompound();
            node.save(nodeCompound);

            compound.setTag("node", nodeCompound);
        }

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        itemStackHandler.deserializeNBT(compound.getCompoundTag("itemHandler"));

        animationStart = compound.getLong("animationStart");
        zpmsCount = compound.getInteger("zpmsCount");
        isPutting = compound.getBoolean("isPutting");

        lastZPMPower.clear();
        lastZPMPowerLevel.clear();

        for(int i = 0; i < 3; i++){
            lastZPMPower.add(compound.getInteger("zpmPower_" + i));
            lastZPMPowerLevel.add(compound.getInteger("zpmLevel_" + i));
        }

        if (node != null && compound.hasKey("node")) node.load(compound.getCompoundTag("node"));

        markDirty();


        super.readFromNBT(compound);
    }


    // ---------------------------------------------------------------------------------------------------
    // Rendering distance

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().add(-1, 0, -1), getPos().add(1, 2, 1));
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 65536;
    }


    // ------------------------------------------------------------------------
    // OpenComputers

    @Override
    public void onChunkUnload() {
        if (node != null) node.remove();

        Aunis.ocWrapper.leaveWirelessNetwork(this);
    }

    @Override
    public void invalidate() {
        if (node != null) node.remove();

        Aunis.ocWrapper.leaveWirelessNetwork(this);

        super.invalidate();
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
    }


    // ------------------------------------------------------------
    // Node-related work
    private Node node = Aunis.ocWrapper.createNode(this, "zpmhub");

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
        Aunis.ocWrapper.sendSignalToReachable(node, (Context) context, name, params);
    }

    // METHODS
    // todo(Mine): add support for OC
    /*

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() -- ZPM gone up")
    public Object[] zpmUp(Context context, Arguments args) {

        try{
            for(int i = 0; i < 3; i++) {
                if (getZPMState(i+1) == EnumZPMState.DOWN && !itemStackHandler.getStackInSlot(i).isEmpty()) {
                    initAnimation(false, zpmsCount, i);
                }
            }
            markDirty();
            return new Object[]{true, "zpmhub_zpm_up"};
        }catch(Exception e){
            return new Object[]{false, "zpmhub_fail", "Cannot parse argument:" + e};
        }
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() -- ZPM gone down")
    public Object[] zpmDown(Context context, Arguments args) {
        try{
            for(int i = 0; i < 3; i++) {
                if (getZPMState(i+1) == EnumZPMState.UP && !itemStackHandler.getStackInSlot(i).isEmpty()) {
                    initAnimation(false, zpmsCount, i);
                }
            }
            markDirty();
            return new Object[]{true, "zpmhub_zpm_down"};
        }catch(Exception e){
            return new Object[]{false, "zpmhub_fail", "Cannot parse argument:" + e};
        }
    }
     */
}
