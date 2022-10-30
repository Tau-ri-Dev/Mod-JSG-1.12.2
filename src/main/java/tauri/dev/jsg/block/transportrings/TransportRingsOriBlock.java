package tauri.dev.jsg.block.transportrings;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.renderer.transportrings.TransportRingsOriRenderer;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsAbstractTile;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsOriTile;

public class TransportRingsOriBlock extends TransportRingsAbstractBlock {

    private static final String BLOCK_NAME = "transportrings_ori_block";

    public TransportRingsOriBlock() {
        super(BLOCK_NAME);
    }

    @Override
    public TransportRingsAbstractTile createTileEntity(World world, IBlockState state) {
        return new TransportRingsOriTile();
    }

    @Override
    public Class<? extends TileEntity> getTileEntityClass() {
        return TransportRingsOriTile.class;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TileEntitySpecialRenderer<? extends TileEntity> getTESR() {
        return new TransportRingsOriRenderer();
    }
}
