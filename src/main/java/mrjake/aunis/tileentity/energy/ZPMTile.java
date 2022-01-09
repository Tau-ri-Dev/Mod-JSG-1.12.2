package mrjake.aunis.tileentity.energy;

import mrjake.aunis.Aunis;
import mrjake.aunis.AunisProps;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.gui.container.CapacitorContainerGuiUpdate;
import mrjake.aunis.gui.container.zpm.ZPMContainerGuiUpdate;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.packet.StateUpdateRequestToServer;
import mrjake.aunis.renderer.energy.ZPMRendererState;
import mrjake.aunis.stargate.power.StargateAbstractEnergyStorage;
import mrjake.aunis.state.State;
import mrjake.aunis.state.StateProviderInterface;
import mrjake.aunis.state.StateTypeEnum;
import net.minecraft.block.state.IBlockState;
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
import net.minecraftforge.fml.common.network.NetworkRegistry;


public class ZPMTile extends TileEntity implements ITickable, ICapabilityProvider, StateProviderInterface {

    // -----------------------------------------------------------------------------
    // Power system

    private int energyStoredLastTick = 0;
    private int energyTransferedLastTick = 0;

    public int getEnergyTransferedLastTick() {
        return energyTransferedLastTick;
    }

    private StargateAbstractEnergyStorage energyStorage = new StargateAbstractEnergyStorage(100000000) {

        @Override
        protected void onEnergyChanged() {
            markDirty();
        }
    };

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityEnergy.ENERGY) || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public void rotate(Rotation rotation) {
        IBlockState state = world.getBlockState(pos);

        int rotationOrig = state.getValue(AunisProps.ROTATION_HORIZONTAL);
        world.setBlockState(pos, state.withProperty(AunisProps.ROTATION_HORIZONTAL, rotation.rotate(rotationOrig, 16)));
    }

    // ---------------------------------------------------------------------------------------------------
    // Renderer state

    private ZPMRendererState rendererStateClient;

    public ZPMRendererState getRendererStateClient() {
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
            if (!lastPos.equals(pos)) {
                lastPos = pos;
            }
            for (EnumFacing facing : EnumFacing.VALUES) {
                TileEntity tile = world.getTileEntity(pos.offset(facing));

                if (tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
                    int extracted = energyStorage.extractEnergy(AunisConfig.powerConfig.stargateMaxEnergyTransfer, true);
                    extracted = tile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite()).receiveEnergy(extracted, false);

                    energyStorage.extractEnergy(extracted, false);
                }
            }

            energyTransferedLastTick = energyStorage.getEnergyStored() - energyStoredLastTick;
            energyStoredLastTick = energyStorage.getEnergyStored();
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
                return new ZPMRendererState(true);

            case GUI_UPDATE:
                return new ZPMContainerGuiUpdate(energyStorage.getEnergyStored(), energyTransferedLastTick);

            default:
                throw new UnsupportedOperationException("EnumStateType." + stateType.name() + " not implemented on " + this.getClass().getName());
        }
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_STATE:
                return new ZPMRendererState();

            case GUI_UPDATE:
                return new ZPMContainerGuiUpdate();

            default:
                throw new UnsupportedOperationException("EnumStateType." + stateType.name() + " not implemented on " + this.getClass().getName());
        }
    }

    @Override
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {
            case RENDERER_STATE:
                float horizontalRotation = world.getBlockState(pos).getValue(AunisProps.ROTATION_HORIZONTAL) * -22.5f;
                rendererStateClient = ((ZPMRendererState) state).initClient(pos, horizontalRotation);

                break;

            case GUI_UPDATE:
                ZPMContainerGuiUpdate guiUpdate = (ZPMContainerGuiUpdate) state;
                energyStorage.setEnergyStored(guiUpdate.energyStored);
                energyTransferedLastTick = guiUpdate.energyTransferedLastTick;
                break;

            default:
                throw new UnsupportedOperationException("EnumStateType." + stateType.name() + " not implemented on " + this.getClass().getName());
        }
    }

    // ---------------------------------------------------------------------------------------------------
    // NBT

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("energyStorage", energyStorage.serializeNBT());

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        energyStorage.deserializeNBT(compound.getCompoundTag("energyStorage"));

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
