package mrjake.aunis.tileentity.energy;

import mrjake.aunis.Aunis;
import mrjake.aunis.AunisProps;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.item.AunisItems;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.packet.StateUpdateRequestToServer;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.energy.ZPMHubRenderer;
import mrjake.aunis.renderer.energy.ZPMHubRendererState;
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
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;


public class ZPMHubTile extends TileEntity implements ITickable, ICapabilityProvider, StateProviderInterface {

    private long animationStart;
    private boolean isPutting = false;
    private int zpmsCount = 0;
    private int zpmAnimated = 0;

    private boolean[] zpmIsDown = {
            true,
            true,
            true,
    };



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
                    initAnimation(putting, zpmsCount, slot);
                    break;
            }

            markDirty();
        }
    };


    public void initAnimation(boolean isPutting, int zpmsCount, int slot){
        this.animationStart = world.getTotalWorldTime();
        this.isPutting = isPutting;
        this.zpmsCount = zpmsCount;
        this.zpmAnimated = slot+1;
    }

    public boolean isZPMDown(int slot){
        return this.zpmIsDown[slot];
    }
    public void setZPMStatus(int slot, boolean down){
        this.zpmIsDown[slot] = down;
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
                if(this.zpmsCount != zpmsCount)
                    this.zpmsCount = zpmsCount;
            }
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

            default:
                throw new UnsupportedOperationException("EnumStateType." + stateType.name() + " not implemented on " + this.getClass().getName());
        }
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_STATE:
                return new ZPMHubRendererState();

            default:
                throw new UnsupportedOperationException("EnumStateType." + stateType.name() + " not implemented on " + this.getClass().getName());
        }
    }

    @Override
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {
            case RENDERER_STATE:
                rendererStateClient = ((ZPMHubRendererState) state).initClient(pos, animationStart, isPutting, zpmsCount, zpmAnimated);

                break;

            default:
                throw new UnsupportedOperationException("EnumStateType." + stateType.name() + " not implemented on " + this.getClass().getName());
        }
    }

    // -----------------------------------------------------------------------------
    // Capabilities

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemStackHandler);

        return super.getCapability(capability, facing);
    }

    // ---------------------------------------------------------------------------------------------------
    // NBT

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setLong("animationStart", animationStart);
        compound.setInteger("zpmsCount", zpmsCount);
        compound.setBoolean("isPutting", isPutting);

        compound.setTag("itemStackHandler", itemStackHandler.serializeNBT());

        compound.setByte("zpmMapSize", (byte) zpmIsDown.length);
        for(int i = 0; i < zpmIsDown.length; i++) {
            compound.setBoolean("zpmMapZpm_" + i+1, zpmIsDown[i]);
        }

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        animationStart = compound.getLong("animationStart");
        zpmsCount = compound.getInteger("zpmsCount");
        isPutting = compound.getBoolean("isPutting");

        itemStackHandler.deserializeNBT(compound.getCompoundTag("itemStackHandler"));

        for(int i = 0; i < zpmsCount; i++) {
            itemStackHandler.setStackInSlot(i, new ItemStack(Item.getItemFromBlock(AunisBlocks.ZPM)));
        }

        int zpmMapSize = (int) compound.getByte("zpmMapSize");
        for(int i = 0; i < zpmMapSize; i++) {
            zpmIsDown[i] = compound.getBoolean("zpmMapZpm_" + i+1);
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
