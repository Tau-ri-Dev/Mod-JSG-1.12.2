package tauri.dev.jsg.block.dialhomedevice;

import tauri.dev.jsg.gui.GuiIdEnum;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDPegasusTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class DHDPegasusBlock extends DHDAbstractBlock {

    public static final String BLOCK_NAME = "dhd_pegasus_block";

    public DHDPegasusBlock() {
        super(BLOCK_NAME);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new DHDPegasusTile();
    }

    @Override
    public Class<? extends TileEntity> getTileEntityClass() {
        return DHDPegasusTile.class;
    }

    @Override
    public GuiIdEnum getGui() {
        return GuiIdEnum.GUI_PEGASUS_DHD;
    }
}
