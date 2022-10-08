package tauri.dev.jsg.renderer.transportrings;

import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.loader.model.ModelLoader;
import tauri.dev.jsg.tileentity.transportrings.TRControllerAbstractTile;
import tauri.dev.jsg.transportrings.SymbolGoauldEnum;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;


public class TRControllerGoauldRenderer extends TRControllerAbstractRenderer {
    @Override
    public void renderController(TRControllerAbstractTile te, TRControllerAbstractRendererState rendererState) {

        // render plate
        ElementEnum.RINGS_CONTROLLER_GOAULD.bindTextureAndRender(te.getBiomeOverlay());

        // render light
        ResourceLocation lightTexture = ((TRControllerGoauldRendererState) rendererState).getButtonTexture(6, rendererState.getBiomeOverlay());
        if (rendererDispatcher != null && rendererDispatcher.renderEngine != null && lightTexture!= null) {
            rendererDispatcher.renderEngine.bindTexture(lightTexture);
            ModelLoader.getModel(ElementEnum.RINGS_CONTROLLER_GOAULD_LIGHT.modelResource).render();
        }

        // render buttons
        int symbolId = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 2; x++) {
                GlStateManager.pushMatrix();
                //GlStateManager.translate(x * -0.25, y * -0.1, 0);
                ResourceLocation texture = ((TRControllerGoauldRendererState) rendererState).getButtonTexture(symbolId, rendererState.getBiomeOverlay());
                if (rendererDispatcher != null && rendererDispatcher.renderEngine != null && texture != null) {
                    rendererDispatcher.renderEngine.bindTexture(texture);
                    ModelLoader.getModel(SymbolGoauldEnum.valueOf(symbolId).modelResource).render();
                }
                GlStateManager.popMatrix();
                symbolId++;
            }
        }
    }
}
