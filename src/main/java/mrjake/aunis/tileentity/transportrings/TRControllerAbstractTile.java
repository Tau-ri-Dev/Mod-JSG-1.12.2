package mrjake.aunis.tileentity.transportrings;

import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.transportrings.TRControllerAbstractRenderer;
import mrjake.aunis.tesr.RendererInterface;
import mrjake.aunis.tesr.RendererProviderInterface;
import mrjake.aunis.util.ILinkable;
import mrjake.aunis.util.LinkingHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

public abstract class TRControllerAbstractTile extends TileEntity implements ITickable, ILinkable, RendererProviderInterface {

    private BiomeOverlayEnum biomeOverlay = BiomeOverlayEnum.NORMAL;

    public BiomeOverlayEnum getBiomeOverlay() {
        return biomeOverlay;
    }

    public void setBiomeOverlay(BiomeOverlayEnum biomeOverlay) {
        this.biomeOverlay = biomeOverlay;
    }

    @Override
    public void onLoad() {
    }

    private BlockPos lastPos = BlockPos.ORIGIN;

    @Override
    public void update() {
        if (world.isRemote) {
            // Client

            if (world.getTotalWorldTime() % 40 == 0) {
                biomeOverlay = BiomeOverlayEnum.updateBiomeOverlay(world, pos, SUPPORTED_OVERLAYS);
            }

            if (!lastPos.equals(pos)) {
                lastPos = pos;

                updateLinkStatus();

                markDirty();
            }
        }
    }

    public static final EnumSet<BiomeOverlayEnum> SUPPORTED_OVERLAYS = EnumSet.of(BiomeOverlayEnum.NORMAL, BiomeOverlayEnum.FROST, BiomeOverlayEnum.MOSSY);

    // ------------------------------------------------------------------------
    // Rings
    private BlockPos linkedRings;

    public void setLinkedRings(BlockPos pos, int linkId) {
        this.linkedRings = pos;
        this.linkId = linkId;

        markDirty();
    }

    public BlockPos getLinkedRings() {
        return linkedRings;
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

    private int linkId = -1;

    @Override
    public int getLinkId() {
        return linkId;
    }

    public void updateLinkStatus() {
        BlockPos closestRings = LinkingHelper.findClosestUnlinked(world, pos, new BlockPos(10, 5, 10), AunisBlocks.TRANSPORT_RINGS_BLOCK, linkId);

        int linkId = closestRings == null ? -1 : LinkingHelper.getLinkId();

        if (closestRings != null) {
            TransportRingsAbstractTile ringsTile = (TransportRingsAbstractTile) world.getTileEntity(closestRings);
            ringsTile.setLinkedController(pos, linkId);
        }

        setLinkedRings(closestRings, linkId);
    }

    // ------------------------------------------------------------------------
    // NBT

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


    // ------------------------------------------------------------------------
    // Renderer
    private TRControllerAbstractRenderer renderer;

    @Override
    public RendererInterface getRenderer() {
        return renderer;
    }

    public void setRenderer(TRControllerAbstractRenderer renderer) {
        this.renderer = renderer;
    }
}
