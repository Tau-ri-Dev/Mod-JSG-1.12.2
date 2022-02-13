package mrjake.aunis.renderer.dialhomedevice;

import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.loader.model.ModelLoader;
import mrjake.aunis.stargate.network.SymbolMilkyWayEnum;
import net.minecraft.block.Block;

public class DHDMilkyWayRenderer extends DHDAbstractRenderer {
    @Override
    public void renderSymbols(DHDAbstractRendererState rendererState){
        for (SymbolMilkyWayEnum symbol : SymbolMilkyWayEnum.values()) {
            rendererDispatcher.renderEngine.bindTexture(((DHDMilkyWayRendererState) rendererState).getButtonTexture(symbol, rendererState.getBiomeOverlay()));
            ModelLoader.getModel(symbol.modelResource).render();
        }
    }

    @Override
    public void renderDHD(DHDAbstractRendererState rendererState){
        ElementEnum.MILKYWAY_DHD.bindTextureAndRender(rendererState.getBiomeOverlay());
    }

    @Override
    public Block getDHDBlock(){
        return AunisBlocks.DHD_BLOCK;
    }
}
