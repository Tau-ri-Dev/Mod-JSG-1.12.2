package tauri.dev.jsg.tileentity.transportrings.controller;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.transportrings.TransportRingsAbstractBlock;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.packet.StateUpdateRequestToServer;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.renderer.transportrings.controller.TRControllerAbstractRendererState;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateProviderInterface;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.state.dialhomedevice.DHDActivateButtonState;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsAbstractTile;
import tauri.dev.jsg.transportrings.SymbolTypeTransportRingsEnum;
import tauri.dev.jsg.util.ILinkable;
import tauri.dev.jsg.util.LinkingHelper;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Objects;

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

    public abstract void playPressSound(boolean isFinal);

    @Override
    public void onLoad() {
        if (!world.isRemote)
            targetPoint = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
        else
            JSGPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.RENDERER_STATE));
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
            if (isLinked() && getLinkedRings() == null) {
                linkedRingsTile = (TransportRingsAbstractTile) world.getTileEntity(getLinkedRings());
            }
            if (!isLinked() && getLinkedRings() != null)
                linkedRingsTile = null;
            markDirty();
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

    public abstract TransportRingsAbstractBlock getTRBlock();

    public void updateLinkStatus() {
        BlockPos closestRings = LinkingHelper.findClosestUnlinked(world, pos, new BlockPos(10, 40, 10), getTRBlock(), linkId);
        int linkId = closestRings == null ? -1 : LinkingHelper.getLinkId();

        if (closestRings != null) {
            TransportRingsAbstractTile ringsTile = (TransportRingsAbstractTile) world.getTileEntity(closestRings);
            Objects.requireNonNull(ringsTile).setLinkedController(pos, linkId);
        }

        setLinkedRings(closestRings, linkId);
    }


    // ------------------------------------------------------------------------
    // Renderer

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
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
        if (stateType == StateTypeEnum.DHD_ACTIVATE_BUTTON) {
            return new DHDActivateButtonState();
        }
        throw new UnsupportedOperationException("EnumStateType." + stateType.name() + " not implemented on " + this.getClass().getName());
    }

    @Override
    public State getState(StateTypeEnum stateType) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setState(StateTypeEnum stateType, State state) {
    }

    public void sendState(StateTypeEnum type, State state) {
        if (world.isRemote) return;

        if (targetPoint != null) {
            JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, type, state), targetPoint);
        } else {
            JSG.debug("targetPoint was null trying to send " + type + " from " + this.getClass().getCanonicalName());
        }
    }
}
