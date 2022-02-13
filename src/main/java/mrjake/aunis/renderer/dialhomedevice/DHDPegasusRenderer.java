package mrjake.aunis.renderer.dialhomedevice;

import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.loader.model.ModelLoader;
import mrjake.aunis.stargate.network.SymbolPegasusEnum;
import net.minecraft.block.Block;

public class DHDPegasusRenderer extends DHDAbstractRenderer {
  @Override
  public void renderSymbols(DHDAbstractRendererState rendererState){
    for (SymbolPegasusEnum symbol : SymbolPegasusEnum.values()) {
      rendererDispatcher.renderEngine.bindTexture(((DHDPegasusRendererState) rendererState).getButtonTexture(symbol, rendererState.getBiomeOverlay()));
      ModelLoader.getModel(symbol.modelResource).render();
    }
  }

  @Override
  public void renderDHD(DHDAbstractRendererState rendererState){
    ElementEnum.PEGASUS_DHD.bindTextureAndRender(rendererState.getBiomeOverlay());
  }

  @Override
  public Block getDHDBlock(){
    return AunisBlocks.DHD_PEGASUS_BLOCK;
  }
}
