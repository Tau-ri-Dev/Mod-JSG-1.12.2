package tauri.dev.jsg.block.transportrings;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.renderer.transportrings.TransportRingsGoauldRenderer;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsAbstractTile;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsGoauldTile;

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

    @Override
    @SideOnly(Side.CLIENT)
    public TileEntitySpecialRenderer<? extends TileEntity> getTESR() {
        return new TransportRingsGoauldRenderer();
    }
}
