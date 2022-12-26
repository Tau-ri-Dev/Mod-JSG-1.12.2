package tauri.dev.jsg.block.transportrings;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.renderer.transportrings.TransportRingsAncientRenderer;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsAbstractTile;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsAncientTile;

public class TransportRingsAncientBlock extends TransportRingsAbstractBlock {

    private static final String BLOCK_NAME = "transportrings_ancient_block";

    public TransportRingsAncientBlock() {
        super(BLOCK_NAME);
    }

    @Override
    public TransportRingsAbstractTile createTileEntity(World world, IBlockState state) {
        return new TransportRingsAncientTile();
    }

    @Override
    public Class<? extends TileEntity> getTileEntityClass() {
        return TransportRingsAncientTile.class;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TileEntitySpecialRenderer<? extends TileEntity> getTESR() {
        return new TransportRingsAncientRenderer();
    }
}
