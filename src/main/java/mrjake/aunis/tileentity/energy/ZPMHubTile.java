package mrjake.aunis.tileentity.energy;

import mrjake.aunis.Aunis;
import mrjake.aunis.AunisProps;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.packet.StateUpdateRequestToServer;
import mrjake.aunis.renderer.energy.ZPMHubRendererState;
import mrjake.aunis.state.State;
import mrjake.aunis.state.StateProviderInterface;
import mrjake.aunis.state.StateTypeEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.network.NetworkRegistry;


public class ZPMHubTile extends TileEntity implements ITickable, ICapabilityProvider, StateProviderInterface {

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
            if (!lastPos.equals(pos)) {
                lastPos = pos;
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

                return new ZPMHubRendererState(0);
                // ZPMHubRendererState(zpmsActive);

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

    public boolean isLinkedClient;

    @Override
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {
            case RENDERER_STATE:
                float horizontalRotation = world.getBlockState(pos).getValue(AunisProps.ROTATION_HORIZONTAL) * -22.5f;
                rendererStateClient = ((ZPMHubRendererState) state).initClient(pos, horizontalRotation);

                break;

            default:
                throw new UnsupportedOperationException("EnumStateType." + stateType.name() + " not implemented on " + this.getClass().getName());
        }
    }

    // ---------------------------------------------------------------------------------------------------
    // NBT

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
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
