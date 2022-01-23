package mrjake.aunis.tileentity.energy;

import cofh.redstoneflux.impl.TileEnergyHandler;
import mrjake.aunis.Aunis;
import mrjake.aunis.AunisProps;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.gui.container.zpm.ZPMContainerGuiUpdate;
import mrjake.aunis.gui.container.zpmhub.ZPMHubContainerGuiUpdate;
import mrjake.aunis.item.AunisItems;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.packet.StateUpdateRequestToServer;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.energy.ZPMHubRenderer;
import mrjake.aunis.renderer.energy.ZPMHubRendererState;
import mrjake.aunis.sound.AunisSoundHelper;
import mrjake.aunis.sound.SoundPositionedEnum;
import mrjake.aunis.stargate.power.StargateAbstractEnergyStorage;
import mrjake.aunis.state.State;
import mrjake.aunis.state.StateProviderInterface;
import mrjake.aunis.state.StateTypeEnum;
import mrjake.aunis.state.stargate.StargateBiomeOverrideState;
import mrjake.aunis.tileentity.stargate.StargateClassicBaseTile;
import mrjake.aunis.util.AunisItemStackHandler;
import mrjake.aunis.util.ItemMetaPair;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.ArrayList;
import java.util.Objects;


public class ZPMHubTile extends TileEntity implements ITickable, ICapabilityProvider, StateProviderInterface {

    private long animationStart;
    private boolean isPutting = false;
    private int zpmsCount = 0;
    public int zpmAnimated = 0;

    private ArrayList<Boolean> zpmIsDown = new ArrayList<Boolean>(3);



    // ZPMs
    private final AunisItemStackHandler itemStackHandler = new AunisItemStackHandler(3) {

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
                        if(!getStackInSlot(i).isEmpty())
                            zpmsCount++;
                    }
                    if(getZpmsCount() != zpmsCount)
                        initAnimation(putting, zpmsCount, slot);
                    break;
            }

            markDirty();
        }
    };

    public int getZpmsCount(){
        return this.zpmsCount;
    }

    public float getEnergyLevelOfZPM(int zpm){
        ItemStack zpmStack = this.itemStackHandler.getStackInSlot(zpm);
        if(zpmStack.isEmpty())
            return 0;
        int energyStored = Objects.requireNonNull(zpmStack.getCapability(CapabilityEnergy.ENERGY, null)).getEnergyStored();
        int maxEnergyStored = AunisConfig.powerConfig.zpmEnergyStorage;
        return Math.round((energyStored/(float)maxEnergyStored * 10)/2);
    }

    public int getEnergyInZPM(int zpm){
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
    }

    public void setZPMStatus(int slot, boolean down){
        this.zpmIsDown.set(slot, down);
        zpmAnimated = 0;
        if(down && Objects.requireNonNull(itemStackHandler.getStackInSlot(slot).getCapability(CapabilityEnergy.ENERGY, null)).getEnergyStored() > 0)
                AunisSoundHelper.playPositionedSound(world, pos, SoundPositionedEnum.ZPMHUB_ZPM_ACTIVATED, true);
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

    @Override
    public void update() {
        if(zpmIsDown.size() < 1){
            // init zpmdown list
            for(int i = 0; i < 3; i++)
                zpmIsDown.add(true);
        }
        if (!world.isRemote) {
            AunisPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.RENDERER_STATE));
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
                TileEntity tile = world.getTileEntity(pos.offset(facing));

                if (tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {

                    for(int i = 0; i < 3; i++){
                        ItemStack zpmStack = itemStackHandler.getStackInSlot(i);

                        if(zpmStack.isEmpty())
                            // zpm not found -> skip extract process
                            continue;

                        int extracted = extractEnergyFromZPM(i+1, AunisConfig.powerConfig.stargateMaxEnergyTransfer, true);
                        extracted = Objects.requireNonNull(tile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite())).receiveEnergy(extracted*2, false);
                        extractEnergyFromZPM(i+1, extracted/2, false);

                        /**
                         *    *2 and /2 is prevention of getting energy back to ZPM
                         */
                    }
                }
            }

            int energyStored = 0;
            for(int i = 0; i < 3; i++){
                if(itemStackHandler.getStackInSlot(i).isEmpty()) continue;
                energyStored += Objects.requireNonNull(itemStackHandler.getStackInSlot(i).getCapability(CapabilityEnergy.ENERGY, null)).getEnergyStored();
            }
            energyTransferedLastTick = energyStored - energyStoredLastTick;
            energyStoredLastTick = energyStored;
        }
    }


    // -----------------------------------------------------------------------------
    // States

    protected void sendState(StateTypeEnum type, State state) {
        if (world.isRemote) return;

        if (targetPoint != null) {
            AunisPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, type, state), targetPoint);
        } else {
            Aunis.logger.debug("targetPoint was null trying to send " + type + " from " + this.getClass().getCanonicalName());
        }
    }

    @Override
    public State getState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_STATE:
                return new ZPMHubRendererState();
            case GUI_UPDATE:
                return new ZPMHubContainerGuiUpdate(zpmsCount);

            default:
                throw new UnsupportedOperationException("EnumStateType." + stateType.name() + " not implemented on " + this.getClass().getName());
        }
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_STATE:
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
                rendererStateClient = ((ZPMHubRendererState) state).initClient(pos, animationStart, isPutting, zpmsCount, zpmAnimated);
            case GUI_UPDATE:
                if(!(state instanceof ZPMHubContainerGuiUpdate))
                    break;
                ZPMHubContainerGuiUpdate guiUpdate = (ZPMHubContainerGuiUpdate) state;
                zpmsCount = guiUpdate.zpmsCount;
                break;

            default:
                throw new UnsupportedOperationException("EnumStateType." + stateType.name() + " not implemented on " + this.getClass().getName());
        }
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

    protected int energyStoredLastTick = 0;
    protected int energyTransferedLastTick = 0;

    public int getEnergyTransferedLastTick() {
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
        compound.setLong("animationStart", animationStart);
        compound.setInteger("zpmsCount", zpmsCount);
        compound.setBoolean("isPutting", isPutting);

        compound.setByte("zpmMapSize", (byte) zpmIsDown.size());
        for(int i = 0; i < zpmIsDown.size(); i++) {
            compound.setBoolean("zpmMapZpm_" + i+1, zpmIsDown.get(i));
        }

        compound.setTag("itemStackHandler", itemStackHandler.serializeNBT());

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        animationStart = compound.getLong("animationStart");
        zpmsCount = compound.getInteger("zpmsCount");
        isPutting = compound.getBoolean("isPutting");

        int zpmMapSize = (int) compound.getByte("zpmMapSize");
        for(int i = 0; i < zpmMapSize; i++) {
            zpmIsDown.set(i, compound.getBoolean("zpmMapZpm_" + i+1));
        }

        itemStackHandler.deserializeNBT(compound.getCompoundTag("itemStackHandler"));

        for(int i = 0; i < zpmsCount; i++) {
            itemStackHandler.setStackInSlot(i, new ItemStack(Item.getItemFromBlock(AunisBlocks.ZPM)));
        }

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
}
