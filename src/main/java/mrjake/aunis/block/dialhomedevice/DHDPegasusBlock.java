package mrjake.aunis.block.dialhomedevice;

import mrjake.aunis.gui.GuiIdEnum;
import mrjake.aunis.tileentity.dialhomedevice.DHDPegasusTile;
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
  public GuiIdEnum getGui() {
    return GuiIdEnum.GUI_PEGASUS_DHD;
  }
}
