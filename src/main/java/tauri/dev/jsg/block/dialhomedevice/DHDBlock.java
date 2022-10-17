package tauri.dev.jsg.block.dialhomedevice;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tauri.dev.jsg.gui.GuiIdEnum;
import tauri.dev.jsg.renderer.dialhomedevice.DHDMilkyWayRenderer;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDMilkyWayTile;

public class DHDBlock extends DHDAbstractBlock {

    public static final String BLOCK_NAME = "dhd_block";

    public DHDBlock() {
        super(BLOCK_NAME);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new DHDMilkyWayTile();
    }

    @Override
    public Class<? extends TileEntity> getTileEntityClass() {
        return DHDMilkyWayTile.class;
    }

    @Override
    public TileEntitySpecialRenderer<? extends TileEntity> getTESR() {
        return new DHDMilkyWayRenderer();
    }

    @Override
    public GuiIdEnum getGui() {
        return GuiIdEnum.GUI_DHD;
    }
}
