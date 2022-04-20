package mrjake.aunis.tileentity.transportrings;

import mrjake.aunis.Aunis;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.packet.StateUpdateRequestToServer;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.transportrings.TRControllerAbstractRendererState;
import mrjake.aunis.state.State;
import mrjake.aunis.state.StateProviderInterface;
import mrjake.aunis.state.StateTypeEnum;
import mrjake.aunis.state.dialhomedevice.DHDActivateButtonState;
import mrjake.aunis.transportrings.SymbolTypeTransportRingsEnum;
import mrjake.aunis.util.ILinkable;
import mrjake.aunis.util.LinkingHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.EnumSet;

public abstract class TRControllerAbstractTile extends TileEntity implements ITickable, ILinkable, StateProviderInterface {
    protected BiomeOverlayEnum biomeOverlay = BiomeOverlayEnum.NORMAL;
    protected TransportRingsAbstractTile linkedRingsTile;
    protected NetworkRegistry.TargetPoint targetPoint;
    TRControllerAbstractRendererState rendererState;
    private BlockPos lastPos = BlockPos.ORIGIN;
    // ------------------------------------------------------------------------
    // Rings
    private BlockPos linkedRings;
    private int linkId = -1;

    public BiomeOverlayEnum getBiomeOverlay() {
        return biomeOverlay;
    }

    public void setBiomeOverlay(BiomeOverlayEnum biomeOverlay) {
        this.biomeOverlay = biomeOverlay;
    }

    public abstract SymbolTypeTransportRingsEnum getSymbolType();

    @Override
    public void onLoad() {
        if (!world.isRemote)
            targetPoint = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
        else
            AunisPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.RENDERER_STATE));
    }

    @Override
    public void update() {
        if (world.isRemote) {
            // Client

            if (world.getTotalWorldTime() % 40 == 0 && rendererState != null) {
                biomeOverlay = BiomeOverlayEnum.updateBiomeOverlay(world, pos, getSupportedOverlays());
                rendererState.setBiomeOverlay(BiomeOverlayEnum.updateBiomeOverlay(world, pos, getSupportedOverlays()));
            }

            if (!lastPos.equals(pos)) {
                lastPos = pos;

                updateLinkStatus();

                markDirty();
            }
        }
        if (!world.isRemote) {
            if (isLinked() && getLinkedRings() != null) {
                linkedRingsTile = (TransportRingsAbstractTile) world.getTileEntity(getLinkedRings());
            }
            linkedRingsTile = null;
        }
    }

    protected abstract EnumSet<BiomeOverlayEnum> getSupportedOverlays();

    public abstract TRControllerAbstractRendererState getRendererState();

    public void setLinkedRings(BlockPos pos, int linkId) {
        this.linkedRings = pos;
        this.linkId = linkId;

        markDirty();
    }

    public BlockPos getLinkedRings() {
        return linkedRings;
    }

    public TransportRingsAbstractTile getLinkedRingsTile() {
        return (TransportRingsAbstractTile) world.getTileEntity(linkedRings);
    }

    public boolean isLinked() {
        return linkedRings != null;
    }

    public TransportRingsAbstractTile getLinkedRingsTile(World world) {
        return (linkedRings != null ? ((TransportRingsAbstractTile) world.getTileEntity(linkedRings)) : null);
    }

    @Override
    public boolean canLinkTo() {
        return !isLinked();
    }

    // ------------------------------------------------------------------------
    // NBT

    @Override
    public int getLinkId() {
        return linkId;
    }

    public void updateLinkStatus() {
        BlockPos closestRings = LinkingHelper.findClosestUnlinked(world, pos, new BlockPos(10, 40, 10), AunisBlocks.RINGS_BLOCKS, linkId);
        int linkId = closestRings == null ? -1 : LinkingHelper.getLinkId();

        if (closestRings != null) {
            TransportRingsAbstractTile ringsTile = (TransportRingsAbstractTile) world.getTileEntity(closestRings);
            ringsTile.setLinkedController(pos, linkId);
        }

        setLinkedRings(closestRings, linkId);
    }


    // ------------------------------------------------------------------------
    // Renderer

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        if (linkedRings != null) {
            compound.setLong("linkedRings", linkedRings.toLong());
            compound.setInteger("linkId", linkId);
        }

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("linkedRings")) {
            linkedRings = BlockPos.fromLong(compound.getLong("linkedRings"));
            linkId = compound.getInteger("linkId");
        }

        super.readFromNBT(compound);
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {
            case DHD_ACTIVATE_BUTTON:
                return new DHDActivateButtonState();

            default:
                throw new UnsupportedOperationException("EnumStateType." + stateType.name() + " not implemented on " + this.getClass().getName());
        }
    }

    @Override
    public State getState(StateTypeEnum stateType) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setState(StateTypeEnum stateType, State state) {
    }

    protected void sendState(StateTypeEnum type, State state) {
        if (world.isRemote) return;

        if (targetPoint != null) {
            AunisPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, type, state), targetPoint);
        } else {
            Aunis.logger.debug("targetPoint was null trying to send " + type + " from " + this.getClass().getCanonicalName());
        }
    }
}
