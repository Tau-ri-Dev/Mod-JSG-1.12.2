package mrjake.aunis.renderer.dialhomedevice;

import mrjake.aunis.AunisProps;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.loader.model.ModelLoader;
import mrjake.aunis.renderer.BlockRenderer;
import mrjake.aunis.stargate.network.SymbolMilkyWayEnum;
import mrjake.aunis.tileentity.dialhomedevice.DHDMilkyWayTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class DHDMilkyWayRenderer extends TileEntitySpecialRenderer<DHDMilkyWayTile> {

  private static final BlockPos ZERO_BLOCKPOS = new BlockPos(0, 0, 0);

  @Override
  public void render(DHDMilkyWayTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
    DHDMilkyWayRendererState rendererState = te.getRendererStateClient();

    if (rendererState != null) {
      IBlockState state = te.getWorld().getBlockState(te.getPos()).getActualState(te.getWorld(), te.getPos());
      if (state.getBlock() != AunisBlocks.DHD_BLOCK) return;
      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, z);

      if (state.getValue(AunisProps.SNOWY)) {
        BlockRenderer.render(getWorld(), ZERO_BLOCKPOS, Blocks.SNOW_LAYER.getDefaultState(), te.getPos());
      }

      GlStateManager.translate(0.5, 0, 0.5);
      GlStateManager.rotate(rendererState.horizontalRotation, 0, 1, 0);

      ElementEnum.MILKYWAY_DHD.bindTextureAndRender(rendererState.getBiomeOverlay());

      for (SymbolMilkyWayEnum symbol : SymbolMilkyWayEnum.values()) {
        rendererDispatcher.renderEngine.bindTexture(rendererState.getButtonTexture(symbol, rendererState.getBiomeOverlay()));
        ModelLoader.getModel(symbol.modelResource).render();
      }


      GlStateManager.popMatrix();

      rendererState.iterate(getWorld(), partialTicks);
    }
  }
}
