package tauri.dev.jsg.block.stargate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.renderer.stargate.StargatePegasusRenderer;
import tauri.dev.jsg.tileentity.stargate.StargatePegasusBaseTile;

public class StargatePegasusBaseBlock extends StargateClassicBaseBlock {

  public static final String BLOCK_NAME = "stargate_pegasus_base_block";

  public StargatePegasusBaseBlock() {
    super(BLOCK_NAME);
  }

  @Override
  public TileEntity createTileEntity(World world, IBlockState state) {
    return new StargatePegasusBaseTile();
  }

  @Override
  public Class<? extends TileEntity> getTileEntityClass() {
    return StargatePegasusBaseTile.class;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public TileEntitySpecialRenderer<? extends TileEntity> getTESR() {
    return new StargatePegasusRenderer();
  }
}
