package mrjake.aunis.renderer.transportrings;

import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.loader.model.ModelLoader;
import mrjake.aunis.tileentity.transportrings.TRControllerAbstractTile;
import mrjake.aunis.transportrings.SymbolTransportRingsEnum;
import net.minecraft.client.renderer.GlStateManager;


public class TRControllerGoauldRenderer extends TRControllerAbstractRenderer {
    @Override
    public void renderController(TRControllerAbstractTile te, TRControllerAbstractRendererState rendererState) {
        ElementEnum.RINGSCONTROLLER_GOAULD.bindTextureAndRender(te.getBiomeOverlay());
        int symbolId = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 2; x++) {
                GlStateManager.pushMatrix();
                if (AunisConfig.devConfig.test3)
                    GlStateManager.translate(x * AunisConfig.devConfig.test1, y * AunisConfig.devConfig.test2, 0);
                else
                    GlStateManager.translate(0, y * -0.1, x * -0.25);
                ElementEnum button = ElementEnum.RINGSCONTROLLER_GOAULD_BUTTON;
                rendererDispatcher.renderEngine.bindTexture(((TRControllerGoauldRendererState) rendererState).getButtonTexture(SymbolTransportRingsEnum.valueOf(symbolId), rendererState.getBiomeOverlay()));
                ModelLoader.getModel(button.modelResource).render();
                GlStateManager.popMatrix();
                symbolId++;
            }
        }
    }
}
