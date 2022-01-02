package mrjake.aunis.renderer.stargate;

import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.loader.model.ModelLoader;
import mrjake.aunis.loader.texture.TextureLoader;
import mrjake.aunis.stargate.EnumIrisType;
import mrjake.aunis.stargate.network.SymbolUniverseEnum;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class StargateUniverseRenderer extends StargateClassicRenderer<StargateUniverseRendererState> {

  private static final float GATE_DIAMETER = 8.67415f;

  @Override
  protected void applyTransformations(StargateUniverseRendererState rendererState) {
    float scale = 0.90f;
    GlStateManager.scale(scale, scale, scale);

    GlStateManager.translate(0.5, GATE_DIAMETER / 2 + 0.20, 0.55);
    GlStateManager.rotate(90, 0, 0, 1);
  }

  @Override
  protected void renderGate(StargateUniverseRendererState rendererState, double partialTicks) {
    GlStateManager.rotate(-rendererState.horizontalRotation, 0, 1, 0);
    float angularRotation = rendererState.spinHelper.getCurrentSymbol().getAngle();

    if (rendererState.spinHelper.getIsSpinning())
      angularRotation += rendererState.spinHelper.apply(getWorld().getTotalWorldTime() + partialTicks);

    GlStateManager.rotate(rendererState.horizontalRotation - 90, 1, 0, 0);
    GlStateManager.rotate((float) angularRotation + 0.6f, 0, 1, 0);
    renderChevrons(rendererState, partialTicks);

    ElementEnum.UNIVERSE_GATE.bindTextureAndRender(rendererState.getBiomeOverlay());

    GlStateManager.disableLighting();
    ElementEnum.UNIVERSE_CHEVRON.bindTexture(rendererState.getBiomeOverlay());

    for (SymbolUniverseEnum symbol : SymbolUniverseEnum.values()) {
      if (symbol.modelResource != null) {
        float color = rendererState.getSymbolColor(symbol) + 0.25f;

        GlStateManager.color(color, color, color);
        ModelLoader.getModel(symbol.modelResource).render();
      }
    }
    GlStateManager.enableLighting();

    rendererState.iterate(getWorld(), partialTicks);
  }

  @Override
  protected ResourceLocation getEventHorizonTextureResource(StargateAbstractRendererState rendererState) {
    if (AunisConfig.stargateConfig.disableAnimatedEventHorizon) return EV_HORIZON_DESATURATED_TEXTURE;

    return EV_HORIZON_DESATURATED_TEXTURE_ANIMATED;
  }

  @Override
  protected void renderKawoosh(StargateAbstractRendererState rendererState, double partialTicks) {

    GlStateManager.translate(0, 0.04, 0);
    GlStateManager.rotate(90, 1, 0, 0);
    GlStateManager.scale(0.9, 0.9, 0.9);

    super.renderKawoosh(rendererState, partialTicks);

  }
  private static final float darkness = .6f;
  @Override
  public void renderIris(double partialTicks, World world, StargateUniverseRendererState rendererState, boolean backOnly) {
    GlStateManager.pushMatrix();
    GlStateManager.translate(0, 0.04, 0);
    GlStateManager.rotate(90, 1, 0, 0);
    GlStateManager.scale(0.9, 0.9, 0.9);
    if (rendererState.irisType == EnumIrisType.SHIELD) GlStateManager.color(1,1 ,1);
    else GlStateManager.color(darkness, darkness, darkness);
    super.renderIris(partialTicks, world, rendererState, backOnly);
    GlStateManager.popMatrix();
  }

  // ----------------------------------------------------------------------------------------
  // Chevrons

  @Override
  protected void renderChevron(StargateUniverseRendererState rendererState, double partialTicks, ChevronEnum chevron) {
    GlStateManager.pushMatrix();

    GlStateManager.rotate(-chevron.rotation, 0, 1, 0);
    TextureLoader.getTexture(rendererState.chevronTextureList.get(rendererState.getBiomeOverlay(), chevron)).bindTexture();
    ElementEnum.UNIVERSE_CHEVRON.render();

    GlStateManager.popMatrix();
  }
}
