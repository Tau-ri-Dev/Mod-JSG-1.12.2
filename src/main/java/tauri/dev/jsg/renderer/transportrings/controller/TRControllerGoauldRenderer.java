package tauri.dev.jsg.renderer.transportrings.controller;

import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.loader.model.ModelLoader;
import tauri.dev.jsg.tileentity.transportrings.controller.TRControllerAbstractTile;
import tauri.dev.jsg.transportrings.SymbolGoauldEnum;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import tauri.dev.jsg.util.JSGTextureLightningHelper;


public class TRControllerGoauldRenderer extends TRControllerAbstractRenderer {
    @Override
    public void renderController(TRControllerAbstractTile te, TRControllerAbstractRendererState rendererState) {
        TRControllerGoauldRendererState rs = ((TRControllerGoauldRendererState) rendererState);
        if(rs == null) return;
        if(rs.pos == null) return;

        // render plate
        ElementEnum.RINGS_CONTROLLER_GOAULD.bindTextureAndRender(te.getBiomeOverlay());

        // render light
        GlStateManager.pushMatrix();
        JSGTextureLightningHelper.lightUpTexture(getWorld(), rs.pos, rs.BUTTON_STATE_MAP.get(SymbolGoauldEnum.LIGHT.id)/5f);
        ResourceLocation lightTexture = rs.getButtonTexture(SymbolGoauldEnum.LIGHT.id, rendererState.getBiomeOverlay());
        if (rendererDispatcher != null && rendererDispatcher.renderEngine != null && lightTexture!= null) {
            rendererDispatcher.renderEngine.bindTexture(lightTexture);
            ModelLoader.getModel(ElementEnum.RINGS_CONTROLLER_GOAULD_LIGHT.modelResource).render();
        }
        JSGTextureLightningHelper.resetLight(getWorld(), rs.pos);
        GlStateManager.popMatrix();

        // render buttons
        int symbolId = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 2; x++) {
                GlStateManager.pushMatrix();
                JSGTextureLightningHelper.lightUpTexture(getWorld(), rs.pos, ((rs.BUTTON_STATE_MAP.get(symbolId) > 0) ? 1f : 0));
                ResourceLocation texture = ((TRControllerGoauldRendererState) rendererState).getButtonTexture(symbolId, rendererState.getBiomeOverlay());
                if (rendererDispatcher != null && rendererDispatcher.renderEngine != null && texture != null) {
                    rendererDispatcher.renderEngine.bindTexture(texture);
                    ModelLoader.getModel(SymbolGoauldEnum.valueOf(symbolId).modelResource).render();
                }
                JSGTextureLightningHelper.resetLight(getWorld(), rs.pos);
                GlStateManager.popMatrix();
                symbolId++;
            }
        }
    }
}
