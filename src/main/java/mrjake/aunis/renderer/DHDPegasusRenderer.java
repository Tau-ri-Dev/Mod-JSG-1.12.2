package mrjake.aunis.renderer;

import mrjake.aunis.AunisProps;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.loader.model.ModelLoader;
import mrjake.aunis.stargate.network.SymbolPegasusEnum;
import mrjake.aunis.tileentity.DHDPegasusTile;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class DHDPegasusRenderer extends TileEntitySpecialRenderer<DHDPegasusTile> {

  private static final BlockPos ZERO_BLOCKPOS = new BlockPos(0, 0, 0);

  public void render(DHDPegasusTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
    DHDPegasusRendererState rendererState = te.getRendererStateClient();

    if (rendererState != null) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, z);

      if (te.getWorld().getBlockState(te.getPos()).getActualState(te.getWorld(), te.getPos()).getValue(AunisProps.SNOWY)) {
        BlockRenderer.render(getWorld(), ZERO_BLOCKPOS, Blocks.SNOW_LAYER.getDefaultState(), te.getPos());
      }

      GlStateManager.translate(0.5, 0, 0.5);
      GlStateManager.rotate(rendererState.horizontalRotation, 0, 1, 0);

      ElementEnum.PEGASUS_DHD.bindTextureAndRender(rendererState.getBiomeOverlay());

      for (SymbolPegasusEnum symbol : SymbolPegasusEnum.values()) {
        rendererDispatcher.renderEngine.bindTexture(rendererState.getButtonTexture(symbol, rendererState.getBiomeOverlay()));
        ModelLoader.getModel(symbol.modelResource).render();
      }


      GlStateManager.popMatrix();

      rendererState.iterate(getWorld(), partialTicks);
    }
  }
}
