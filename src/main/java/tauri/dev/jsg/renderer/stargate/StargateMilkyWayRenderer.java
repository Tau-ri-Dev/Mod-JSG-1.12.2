package tauri.dev.jsg.renderer.stargate;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.loader.model.ModelLoader;
import tauri.dev.jsg.loader.texture.TextureLoader;
import tauri.dev.jsg.stargate.network.SymbolMilkyWayEnum;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.util.math.MathFunction;
import tauri.dev.jsg.util.math.MathFunctionImpl;
import tauri.dev.jsg.util.math.MathRange;

public class StargateMilkyWayRenderer extends StargateClassicRenderer<StargateMilkyWayRendererState> {

    private static final Vec3d RING_LOC = new Vec3d(0.0, -0.122333, -0.000597);
    private static final float GATE_DIAMETER = 10.1815f;

    @Override
    public float getGateDiameter() {
        return GATE_DIAMETER;
    }

    @Override
    public double getScaleMultiplier() {
        return 1;
    }

    @Override
    protected void renderGate(StargateMilkyWayRendererState rendererState, double partialTicks) {
        setGateHeatColor(rendererState);
        renderRing(rendererState, partialTicks);
        renderChevrons(rendererState, partialTicks);

        GlStateManager.pushMatrix();
        applyLightMap(rendererState, partialTicks);
        ElementEnum.MILKYWAY_GATE.bindTextureAndRender(rendererState.getBiomeOverlay());
        GlStateManager.popMatrix();
    }

    // ----------------------------------------------------------------------------------------
    // Ring

    private void renderRing(StargateMilkyWayRendererState rendererState, double partialTicks) {
        GlStateManager.pushMatrix();
        applyLightMap(rendererState, partialTicks);
        float angularRotation = rendererState.spinHelper.getCurrentSymbol().getAngle();

        if (rendererState.spinHelper.getIsSpinning())
            angularRotation += rendererState.spinHelper.apply(getWorld().getTotalWorldTime() + partialTicks);


        GlStateManager.translate(RING_LOC.x, RING_LOC.z, RING_LOC.y);
        GlStateManager.rotate(-angularRotation, 0, 0, 1);
        GlStateManager.translate(-RING_LOC.x, -RING_LOC.z, -RING_LOC.y);

        ElementEnum.MILKYWAY_RING.bindTextureAndRender(rendererState.getBiomeOverlay());
        ModelLoader.getModel(((SymbolMilkyWayEnum) SymbolMilkyWayEnum.getOrigin()).getModelResource(rendererState.getBiomeOverlay(), getWorld().provider.getDimension(), false, false, rendererState.config.getOption(StargateClassicBaseTile.ConfigOptions.ORIGIN_MODEL.id).getEnumValue().getIntValue())).render();

        GlStateManager.popMatrix();
    }


    // ----------------------------------------------------------------------------------------
    // Chevrons

    private static final MathRange CHEVRON_OPEN_RANGE = new MathRange(0, 1.57f);
    private static final MathFunction CHEVRON_OPEN_FUNCTION = new MathFunctionImpl(x -> x * x * x * x / 80f);

    private static final MathRange CHEVRON_CLOSE_RANGE = new MathRange(0, 1.428f);
    private static final MathFunction CHEVRON_CLOSE_FUNCTION = new MathFunctionImpl(x0 -> MathHelper.cos(x0 * 1.1f) / 12f);

    private float calculateTopChevronOffset(StargateMilkyWayRendererState rendererState, double partialTicks) {
        float tick = (float) (getWorld().getTotalWorldTime() - rendererState.chevronActionStart + partialTicks);
        float x = tick / 6.0f;

        if (rendererState.chevronOpening) {
            if (CHEVRON_OPEN_RANGE.test(x)) return CHEVRON_OPEN_FUNCTION.apply(x);
            else {
                rendererState.chevronOpen = true;
                rendererState.chevronOpening = false;
            }
        } else if (rendererState.chevronClosing) {
            if (CHEVRON_CLOSE_RANGE.test(x)) return CHEVRON_CLOSE_FUNCTION.apply(x);
            else {
                rendererState.chevronOpen = false;
                rendererState.chevronClosing = false;
            }
        }

        return rendererState.chevronOpen ? 0.08333f : 0;
    }

    @Override
    protected void renderChevron(StargateMilkyWayRendererState rendererState, double partialTicks, ChevronEnum chevron, boolean onlyLight) {
        GlStateManager.pushMatrix();

        GlStateManager.rotate(chevron.rotation, 0, 0, 1);

        TextureLoader.getTexture(rendererState.chevronTextureList.get(rendererState.getBiomeOverlay(), chevron, onlyLight)).bindTexture();

        if (chevron.isFinal()) {
            float chevronOffset = calculateTopChevronOffset(rendererState, partialTicks);

            GlStateManager.pushMatrix();

            GlStateManager.translate(0, chevronOffset, 0);
            ElementEnum.MILKYWAY_CHEVRON_LIGHT.render();

            GlStateManager.translate(0, -2 * chevronOffset, 0);
            ElementEnum.MILKYWAY_CHEVRON_MOVING.render();

            GlStateManager.popMatrix();
        } else {
            ElementEnum.MILKYWAY_CHEVRON_MOVING.render();
            ElementEnum.MILKYWAY_CHEVRON_LIGHT.render();
        }

        if (!onlyLight) {
            applyLightMap(rendererState, partialTicks);
            ElementEnum.MILKYWAY_CHEVRON_FRAME.bindTextureAndRender(rendererState.getBiomeOverlay());
            ElementEnum.MILKYWAY_CHEVRON_BACK.render();
        }


        GlStateManager.popMatrix();
    }
}
