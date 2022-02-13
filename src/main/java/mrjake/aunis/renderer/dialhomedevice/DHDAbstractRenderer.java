package mrjake.aunis.renderer.dialhomedevice;

import mrjake.aunis.AunisProps;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.loader.model.ModelLoader;
import mrjake.aunis.renderer.BlockRenderer;
import mrjake.aunis.stargate.network.SymbolMilkyWayEnum;
import mrjake.aunis.tileentity.dialhomedevice.DHDAbstractTile;
import mrjake.aunis.tileentity.dialhomedevice.DHDMilkyWayTile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public abstract class DHDAbstractRenderer extends TileEntitySpecialRenderer<DHDAbstractTile> {

  protected static final BlockPos ZERO_BLOCKPOS = new BlockPos(0, 0, 0);

  @Override
  public void render(DHDAbstractTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
    DHDAbstractRendererState rendererState = te.getRendererStateClient();

    if (rendererState != null) {
      IBlockState state = te.getWorld().getBlockState(te.getPos()).getActualState(te.getWorld(), te.getPos());
      if (state.getBlock() != getDHDBlock()) return;
      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, z);

      if (state.getValue(AunisProps.SNOWY)) {
        BlockRenderer.render(getWorld(), ZERO_BLOCKPOS, Blocks.SNOW_LAYER.getDefaultState(), te.getPos());
      }

      GlStateManager.translate(0.5, 0, 0.5);
      GlStateManager.rotate(rendererState.horizontalRotation, 0, 1, 0);

      renderDHD(rendererState);
      renderSymbols(rendererState);

      GlStateManager.popMatrix();

      rendererState.iterate(getWorld(), partialTicks);
    }
  }

  public abstract void renderSymbols(DHDAbstractRendererState rendererState);
  public abstract void renderDHD(DHDAbstractRendererState rendererState);
  public abstract Block getDHDBlock();
}
