package tauri.dev.jsg.renderer.stargate;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.loader.model.ModelLoader;
import tauri.dev.jsg.loader.texture.TextureLoader;
import tauri.dev.jsg.stargate.EnumIrisType;
import tauri.dev.jsg.stargate.network.SymbolUniverseEnum;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;
import tauri.dev.jsg.util.JSGTextureLightningHelper;

public class StargateUniverseRenderer extends StargateClassicRenderer<StargateUniverseRendererState> {

    private static final float GATE_DIAMETER = 8.67415f;

    @Override
    public float getGateDiameter(){
        return GATE_DIAMETER;
    }

    @Override
    public double getScaleMultiplier(){
        return 1.14;
    }

    @Override
    protected void renderGate(StargateAbstractBaseTile te, StargateUniverseRendererState rendererState, double partialTicks) {
        setGateHeatColor(rendererState);
        float angularRotation = rendererState.spinHelper.getCurrentSymbol().getAngle();

        if (rendererState.spinHelper.getIsSpinning())
            angularRotation += rendererState.spinHelper.apply(getWorld().getTotalWorldTime() + partialTicks);

        GlStateManager.rotate(-angularRotation, 0, 0, 1);

        // render
        applyLightMap(rendererState, partialTicks);
        GlStateManager.pushMatrix();
        ElementEnum.UNIVERSE_GATE.bindTextureAndRender(rendererState.getBiomeOverlay());
        GlStateManager.popMatrix();

        renderChevrons(rendererState, partialTicks);

        ElementEnum.UNIVERSE_SYMBOL.bindTexture(rendererState.getBiomeOverlay());
        GlStateManager.disableLighting();
        for (SymbolUniverseEnum symbol : SymbolUniverseEnum.values()) {
            if (symbol.modelResource != null) {
                float color = rendererState.getSymbolColor(symbol) + 0.25f;
                GlStateManager.pushMatrix();
                JSGTextureLightningHelper.lightUpTexture(rendererState.getSymbolColor(symbol) / 0.6f);

                GlStateManager.color(color, color, color);
                ModelLoader.getModel(symbol.modelResource).render();
                applyLightMap(rendererState, partialTicks);
                GlStateManager.popMatrix();
            }
        }
        GlStateManager.enableLighting();

        rendererState.iterate(getWorld(), partialTicks);
    }

    @Override
    protected String getEventHorizonTexturePath(StargateAbstractRendererState rendererState, boolean kawoosh) {
        String texture = (kawoosh ? EV_HORIZON_DESATURATED_KAWOOSH_TEXTURE_ANIMATED : EV_HORIZON_DESATURATED_TEXTURE_ANIMATED);
        if (JSGConfig.horizonConfig.disableAnimatedEventHorizon || !isEhAnimatedLoaded())
            texture = EV_HORIZON_DESATURATED_TEXTURE;

        return texture;
    }

    @Override
    protected void renderKawoosh(StargateAbstractRendererState rendererState, double partialTicks) {

        GlStateManager.translate(0, -0.05f, 0);
        GlStateManager.scale(0.9, 0.9, 0.9);

        super.renderKawoosh(rendererState, partialTicks);

    }

    private static final float IRIS_DARK_COLOR = 0.6f;

    @Override
    public void renderIris(double partialTicks, World world, StargateUniverseRendererState rendererState, boolean backOnly) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, -0.05f, 0);
        GlStateManager.scale(0.887f, 0.887f, 0.887f);
        super.renderIris(partialTicks, world, rendererState, backOnly);
        GlStateManager.popMatrix();
    }

    // ----------------------------------------------------------------------------------------
    // Chevrons

    @Override
    protected void renderChevron(StargateUniverseRendererState rendererState, double partialTicks, ChevronEnum chevron, boolean onlyLight) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(chevron.rotation, 0, 0, 1);

        if(onlyLight){
            float color = rendererState.chevronTextureList.getColor(chevron);
            GlStateManager.color(color, color, color);
        }

        TextureLoader.getTexture(rendererState.chevronTextureList.get(rendererState.getBiomeOverlay(), chevron, onlyLight)).bindTexture();
        ElementEnum.UNIVERSE_CHEVRON.render();

        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1);
    }

    @Override
    public void setIrisHeatColor(StargateClassicRendererState rendererState, float red) {
        GlStateManager.color(IRIS_DARK_COLOR + (red * 2F), IRIS_DARK_COLOR, IRIS_DARK_COLOR);
    }
}
