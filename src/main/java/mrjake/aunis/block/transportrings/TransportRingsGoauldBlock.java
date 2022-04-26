package mrjake.aunis.block.transportrings;

import mrjake.aunis.tileentity.dialhomedevice.DHDPegasusTile;
import mrjake.aunis.tileentity.transportrings.TransportRingsAbstractTile;
import mrjake.aunis.tileentity.transportrings.TransportRingsGoauldTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TransportRingsGoauldBlock extends TransportRingsAbstractBlock {

    private static final String BLOCK_NAME = "transportrings_goauld_block";

    public TransportRingsGoauldBlock() {
        super(BLOCK_NAME);
    }

    @Override
    public TransportRingsAbstractTile createTileEntity(World world, IBlockState state) {
        return new TransportRingsGoauldTile();
    }

    @Override
    public Class<? extends TileEntity> getTileEntityClass() {
        return TransportRingsGoauldTile.class;
    }
}
