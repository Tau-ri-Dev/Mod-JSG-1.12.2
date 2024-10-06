package tauri.dev.jsg.renderer.stargate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.loader.texture.Texture;
import tauri.dev.jsg.loader.texture.TextureLoader;
import tauri.dev.jsg.renderer.BlockRenderer;
import tauri.dev.jsg.stargate.EnumIrisState;
import tauri.dev.jsg.stargate.EnumIrisType;
import tauri.dev.jsg.stargate.merging.StargateAbstractMergeHelper;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;
import tauri.dev.jsg.util.FacingHelper;
import tauri.dev.jsg.util.JSGAxisAlignedBB;
import tauri.dev.jsg.util.JSGColorUtil;
import tauri.dev.jsg.util.JSGMinecraftHelper;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public abstract class StargateAbstractRenderer<S extends StargateAbstractRendererState> extends TileEntitySpecialRenderer<StargateAbstractBaseTile> {

    // ---------------------------------------------------------------------------------------
    // Render

    protected static final String EV_HORIZON_NORMAL_TEXTURE_ANIMATED = "textures/tesr/event_horizon_animated.jpg";
    protected static final String EV_HORIZON_KAWOOSH_TEXTURE_ANIMATED = "textures/tesr/event_horizon_animated_kawoosh.jpg";
    protected static final String EV_HORIZON_DESATURATED_KAWOOSH_TEXTURE_ANIMATED = "textures/tesr/event_horizon_animated_kawoosh_unstable.jpg";
    protected static final String EV_HORIZON_DESATURATED_TEXTURE_ANIMATED = "textures/tesr/event_horizon_animated_unstable.jpg";
    protected static final String EV_HORIZON_NORMAL_TEXTURE = "textures/tesr/event_horizon.jpg";
    protected static final String EV_HORIZON_DESATURATED_TEXTURE = "textures/tesr/event_horizon_unstable.jpg";
    private static final float VORTEX_START = 5.275f;
    private static final float SPEED_FACTOR = 6f;

    public static boolean isEhAnimatedLoaded() {
        return !TextureLoader.isNotTextureLoaded(new ResourceLocation(JSG.MOD_ID, EV_HORIZON_NORMAL_TEXTURE_ANIMATED));
    }

    public static boolean isEhKawooshLoaded() {
        if (!isEhAnimatedLoaded()) return true;
        return !TextureLoader.isNotTextureLoaded(new ResourceLocation(JSG.MOD_ID, EV_HORIZON_KAWOOSH_TEXTURE_ANIMATED + "_0.0"));
    }

    private static final Map<ResourceLocation, Boolean> EH_RENDERED = new HashMap<>();

    static {
        EH_RENDERED.put(new ResourceLocation(JSG.MOD_ID, EV_HORIZON_NORMAL_TEXTURE_ANIMATED), false);
        EH_RENDERED.put(new ResourceLocation(JSG.MOD_ID, EV_HORIZON_KAWOOSH_TEXTURE_ANIMATED), false);
        EH_RENDERED.put(new ResourceLocation(JSG.MOD_ID, EV_HORIZON_DESATURATED_KAWOOSH_TEXTURE_ANIMATED), false);
        EH_RENDERED.put(new ResourceLocation(JSG.MOD_ID, EV_HORIZON_DESATURATED_TEXTURE_ANIMATED), false);
        EH_RENDERED.put(new ResourceLocation(JSG.MOD_ID, EV_HORIZON_NORMAL_TEXTURE), false);
        EH_RENDERED.put(new ResourceLocation(JSG.MOD_ID, EV_HORIZON_DESATURATED_TEXTURE), false);
    }

    protected StargateAbstractBaseTile gateTile;

    public void renderWholeGate(StargateAbstractBaseTile te, float partialTicks, S rendererState) {
        renderGate(te, rendererState, partialTicks);
        renderIris(partialTicks, getWorld(), rendererState, true);

        if (rendererState.doEventHorizonRender) {
            GlStateManager.pushMatrix();
            renderKawoosh(rendererState, partialTicks);
            GlStateManager.popMatrix();
        } else if (JSGConfig.Stargate.eventHorizon.renderEHifTheyNot) {
            GlStateManager.pushMatrix();
            preRenderKawoosh(rendererState, partialTicks);
            GlStateManager.popMatrix();
        }

        renderIris(partialTicks, getWorld(), rendererState, false);
    }
    public S rendererState;

    @Override
    @SuppressWarnings("unchecked")
    public void render(StargateAbstractBaseTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        rendererState = (S) te.getRendererStateClient();
        this.gateTile = te;

        if (rendererState != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.disableLighting();


            if (shouldRender(rendererState)) {

                if (JSGConfig.General.debug.renderBoundingBoxes || JSGConfig.General.debug.renderWholeKawooshBoundingBox) {
                    te.getEventHorizonLocalBox().render();

                    int segments = tauri.dev.jsg.config.JSGConfig.General.debug.renderWholeKawooshBoundingBox ? te.getLocalKillingBoxes().size() : rendererState.horizonSegments;

                    for (int i = 0; i < segments; i++) {
                        te.getLocalKillingBoxes().get(i).render();
                    }

                    for (JSGAxisAlignedBB b : te.getLocalInnerBlockBoxes())
                        b.render();

                    te.getRenderBoundingBoxForDisplay().render();
                }

                applyTransformations(rendererState);
                GlStateManager.disableRescaleNormal();
                applyLightMap(rendererState, partialTicks);

                GlStateManager.rotate(rendererState.horizontalRotation, 0, 1, 0);

                renderWholeGate(te, partialTicks, rendererState);

            } else if (JSGConfig.Stargate.visual.renderStargateNotPlaced) {
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
                GL14.glBlendColor(0, 0, 0, 0.7f);

                for (Map.Entry<BlockPos, IBlockState> entry : getMemberBlockStates(te.getMergeHelper(), rendererState.facing, rendererState.facingVertical).entrySet()) {
                    BlockPos pos = FacingHelper.rotateBlock(entry.getKey(), rendererState.facing, rendererState.facingVertical); // entry.getKey().rotate(FacingHelper.getRotation(rendererState.facing));
                    BlockPos absolutePos = pos.add(rendererState.pos);

                    if (getWorld().getBlockState(absolutePos).getBlock().isReplaceable(getWorld(), absolutePos))
                        BlockRenderer.render(getWorld(), pos, entry.getValue(), absolutePos);
                }

                GlStateManager.disableBlend();
            }

            GlStateManager.popMatrix();
            GlStateManager.enableLighting();
        }
    }

    protected boolean shouldRender(S rendererState) {
        IBlockState state = getWorld().getBlockState(rendererState.pos);
        return state.getPropertyKeys().contains(JSGProps.RENDER_BLOCK) && !state.getValue(JSGProps.RENDER_BLOCK);
    }

    /**
     * @param mergeHelper Merge helper instance.
     * @return {@link Map} of {@link BlockPos} to {@link IBlockState} for rendering of the ghost blocks.
     */
    protected abstract Map<BlockPos, IBlockState> getMemberBlockStates(StargateAbstractMergeHelper mergeHelper, EnumFacing facing, EnumFacing facingVertical);

    protected abstract void applyLightMap(S rendererState, double partialTicks);

    protected abstract void applyTransformations(S rendererState);

    protected abstract void renderGate(StargateAbstractBaseTile te, S rendererState, double partialTicks);

    public void scaleVortex() {
    }

    public boolean shouldRenderBackVortex() {
        return false;
    }

    protected ResourceLocation getEventHorizonTextureResource(StargateAbstractRendererState rendererState, boolean kawoosh) {
        return new ResourceLocation(JSG.MOD_ID, getEventHorizonTexturePath(rendererState, kawoosh));
    }

    protected String getEventHorizonTexturePath(StargateAbstractRendererState rendererState, boolean kawoosh) {

        String texture = (rendererState.horizonUnstable ? (kawoosh ? EV_HORIZON_DESATURATED_KAWOOSH_TEXTURE_ANIMATED : EV_HORIZON_DESATURATED_TEXTURE_ANIMATED) : (kawoosh ? EV_HORIZON_KAWOOSH_TEXTURE_ANIMATED : EV_HORIZON_NORMAL_TEXTURE_ANIMATED));
        if (tauri.dev.jsg.config.JSGConfig.Stargate.eventHorizon.disableAnimatedEventHorizon || !isEhAnimatedLoaded())
            texture = (rendererState.horizonUnstable ? EV_HORIZON_DESATURATED_TEXTURE : EV_HORIZON_NORMAL_TEXTURE);

        return texture;
    }

    protected void renderKawoosh(StargateAbstractRendererState rendererState, double partialTicks) {
        renderKawoosh(rendererState, partialTicks, true);
    }

    protected void preRenderKawoosh(StargateAbstractRendererState rendererState, double partialTicks) {

        StargateAbstractRendererState rs = new StargateAbstractRendererState().initClient(rendererState.pos, rendererState.facing, rendererState.facingVertical, rendererState.getBiomeOverlay());

        for (int i = 0; i < 2; i++) {
            rs.vortexState = (i == 0 ? EnumVortexState.STILL : EnumVortexState.FORMING);
            renderKawoosh(rs, partialTicks, false);
        }
    }

    public float[] getEventHorizonColor(){
        return new float[]{1.0f, 1.0f, 1.0f};
    }

    protected void renderKawoosh(StargateAbstractRendererState rendererState, double partialTicks, boolean render) {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 15 * 16, 15 * 16);

        float gateWait = (float) getWorld().getTotalWorldTime() - rendererState.gateWaitStart;

        // Waiting for sound sync
        if (gateWait < (44 - 24)) {
            return;
        }

        boolean isKawoosh = (rendererState.vortexState == EnumVortexState.FORMING
                || rendererState.vortexState == EnumVortexState.DECREASING
                || rendererState.vortexState == EnumVortexState.FULL);

        ResourceLocation ehTextureRes = getEventHorizonTextureResource(rendererState, isKawoosh && !rendererState.noxDialing);
        String ehTextureResKawooshPath = getEventHorizonTexturePath(rendererState, true);
        if (!render && EH_RENDERED.get(ehTextureRes)) return;

        EH_RENDERED.put(ehTextureRes, true);

        GlStateManager.disableLighting();
        GlStateManager.enableCull();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 0.02);

        if (!render) {
            GlStateManager.scale(0.0000001f, 0.0000001f, 0.0000001f);
        }

        // set default texture
        Texture ehTexture = TextureLoader.getTexture(ehTextureRes);

        // bind texture
        if (ehTexture != null) ehTexture.bindTexture();

        long kawooshStart = rendererState.gateWaitStart + 44 - 24;
        float tick = (float) (getWorld().getTotalWorldTime() - kawooshStart + partialTicks);
        float mul;

        float inner = StargateRendererStatic.EVENT_HORIZON_RADIUS - (tick / (rendererState.noxDialing ? 3.2f : 1)) / 3.957f;

        // Fading in the unstable vortex
        float tick2 = tick / 4f;
        if (tick2 <= Math.PI / 2) rendererState.whiteOverlayAlpha = MathHelper.cos(tick2);

        else {
            if (!rendererState.zeroAlphaSet) {
                rendererState.zeroAlphaSet = true;
                rendererState.whiteOverlayAlpha = 0.0f;
            }
        }

        // ----------------------------------------------------------------------------------------------
        // DO MATH - calculate EH and kawoosh

        float kawooshRadius = StargateRendererStatic.kawooshRadius - 2;
        if (rendererState.noxDialing) {
            kawooshRadius = 0.2f;
        }

        float noxAlpha = 0;
        if (rendererState.noxDialing) {
            noxAlpha = Math.min(0.8f, Math.max(0, (inner / StargateRendererStatic.EVENT_HORIZON_RADIUS)));
        }

        // Back side of the EH
        if (rendererState.vortexState != EnumVortexState.STILL && rendererState.vortexState != EnumVortexState.CLOSING) {
            if (inner >= 0.2f) {
                rendererState.frontStrip = new StargateRendererStatic.QuadStrip(8, inner - 0.2f, StargateRendererStatic.EVENT_HORIZON_RADIUS, tick);
            }
        } else
            rendererState.frontStrip = null;
        // ----

        // Going center
        if (inner >= kawooshRadius) {
            rendererState.backStrip = new StargateRendererStatic.QuadStrip(8, inner - 0.2f, StargateRendererStatic.EVENT_HORIZON_RADIUS, tick);
        }
        if (inner < StargateRendererStatic.kawooshRadius) {
            if (rendererState.backStripClamp) {
                // Clamping to the desired size
                if (inner < kawooshRadius) {
                    rendererState.backStripClamp = false;
                    rendererState.backStrip = new StargateRendererStatic.QuadStrip(8, kawooshRadius - 0.2f, StargateRendererStatic.EVENT_HORIZON_RADIUS, null);
                }

                float argState = (tick - VORTEX_START) / SPEED_FACTOR;

                if (argState < 1.342f) rendererState.vortexState = EnumVortexState.FORMING;
                else if (argState < 4.15f) rendererState.vortexState = EnumVortexState.FULL;
                else if (argState < 5.898f) rendererState.vortexState = EnumVortexState.DECREASING;
                else if (rendererState.vortexState != EnumVortexState.CLOSING)
                    rendererState.vortexState = EnumVortexState.STILL;
            }
            if (rendererState.frontStripClamp && inner < 0.2f) {
                rendererState.frontStripClamp = false;
                rendererState.frontStrip = new StargateRendererStatic.QuadStrip(8, 0, StargateRendererStatic.EVENT_HORIZON_RADIUS, null);
            }
            if (!(rendererState.vortexState == EnumVortexState.STILL)) {
                float arg = (tick - VORTEX_START) / SPEED_FACTOR;

                if (!(rendererState.vortexState == EnumVortexState.CLOSING)) {
                    if (!(rendererState.vortexState == EnumVortexState.SHRINKING)) {
                        if (rendererState.vortexState == EnumVortexState.FORMING && arg >= 1.342f) {
                            rendererState.vortexState = EnumVortexState.FULL;
                        }

                        // Offset of the end of the function domain used to generate vortex
                        float end = 0.75f;

                        if (rendererState.vortexState == (EnumVortexState.DECREASING) && arg >= 5.398 + end) {
                            rendererState.vortexState = EnumVortexState.STILL;
                        }

                        if (rendererState.vortexState == (EnumVortexState.FULL)) {
                            if (arg >= 3.65f + end) {
                                rendererState.vortexState = EnumVortexState.DECREASING;
                            }

                            // Flattening the vortex and keeping it still for a moment
                            if (arg < 2) mul = (arg - 1.5f) * (arg - 2.5f) / -10f + 0.91f;
                            else if (arg > 3 + end) mul = (arg - 2.5f - end) * (arg - 3.5f - end) / -10f + 0.91f;
                            else mul = 0.935f;
                        } else {
                            if (rendererState.vortexState == (EnumVortexState.FORMING))
                                mul = (arg * (arg - 4)) / -4.0f;

                            else mul = ((arg - 1 - end) * (arg - 5 - end)) / -5.968f + 0.29333f;
                        }

                        boolean renderWortex = true;
                        // Rendering the vortex
                        if (rendererState instanceof StargateClassicRendererState) {
                            StargateClassicRendererState casted = (StargateClassicRendererState) rendererState;
                            // disable mul while iris/shield is closed
                            if (casted.irisState == EnumIrisState.CLOSED && casted.irisType != EnumIrisType.NULL) {
                                mul = 0;
                                renderWortex = false;
                            }
                        }
                        if (!rendererState.noxDialing && renderWortex) {
                            GlStateManager.pushMatrix();
                            scaleVortex();
                            float prevZ = 0;
                            float prevRad = 0;

                            int index = 0;
                            for (Map.Entry<Float, Float> e : StargateRendererStatic.Z_RadiusMap.entrySet()) {
                                float currentZ = e.getKey();
                                if (currentZ < 0) continue;
                                if (currentZ >= 0 && mul <= 0) continue;
                                float mulAbs = Math.abs(mul);
                                float currentRad = e.getValue() == 0 ? 0 : e.getValue() + StargateRendererStatic.getOffset(index, tick, 7, 1);
                                if (index != 0) {
                                    new StargateRendererStatic.QuadStrip(9, currentRad, prevRad, tick, 1 / 5f * 7).render(tick, currentZ * mulAbs, prevZ * mulAbs, false, 1.0f - rendererState.whiteOverlayAlpha, 5, false);
                                }
                                prevZ = currentZ;
                                prevRad = currentRad;
                                index++;
                            }
                            GlStateManager.popMatrix();
                        }

                    } // not shrinking if

                    else {
                        // Going outwards, closing the gate 29
                        long stateChange = rendererState.gateWaitClose + 35;
                        float arg2 = (float) ((getWorld().getTotalWorldTime() - stateChange + partialTicks) / 3f) - 1.0f;

                        rendererState.whiteOverlayAlpha = MathHelper.sin(arg2);

                        if (arg2 < StargateRendererStatic.EVENT_HORIZON_RADIUS + 0.1f) {
                            rendererState.backStrip = new StargateRendererStatic.QuadStrip(8, arg2, StargateRendererStatic.EVENT_HORIZON_RADIUS, tick);
                            rendererState.frontStrip = new StargateRendererStatic.QuadStrip(8, arg2, StargateRendererStatic.EVENT_HORIZON_RADIUS, tick);
                        } else {
                            rendererState.whiteOverlayAlpha = null;

                            if (getWorld().getTotalWorldTime() - stateChange - 9 > 7) {
                                rendererState.doEventHorizonRender = false;
                            }
                        }
                    }
                } // not closing if

                else {
                    // Fading out the event horizon, closing the gate
                    if ((getWorld().getTotalWorldTime() - rendererState.gateWaitClose) > 35) {
                        float arg2 = (float) ((getWorld().getTotalWorldTime() - (rendererState.gateWaitClose + 35) + partialTicks) / SPEED_FACTOR / 2f);

                        if (arg2 <= Math.PI / 6) rendererState.whiteOverlayAlpha = MathHelper.sin(arg2);
                        else {
                            if (rendererState.backStrip == null)
                                rendererState.backStrip = new StargateRendererStatic.QuadStrip(8, arg2, StargateRendererStatic.EVENT_HORIZON_RADIUS, tick);

                            if (rendererState.frontStrip == null)
                                rendererState.frontStrip = new StargateRendererStatic.QuadStrip(8, arg2, StargateRendererStatic.EVENT_HORIZON_RADIUS, tick);

                            rendererState.vortexState = EnumVortexState.SHRINKING;
                        }
                    }
                }
            } // not still if
        }

        // ----------------------------------------------------------------------------------------------
        // RENDER - render kawoosh and EH

        // Rendering stable wormhole EH
        if (rendererState.vortexState != null) {
            if (rendererState.vortexState == EnumVortexState.STILL || rendererState.vortexState == EnumVortexState.CLOSING) {
                if (rendererState.vortexState == EnumVortexState.CLOSING)
                    renderEventHorizon(partialTicks, true, rendererState.whiteOverlayAlpha, false, 1.7f);
                else
                    renderEventHorizon(partialTicks, false, rendererState.horizonUnstable ? 0.3f : null, false, rendererState.horizonUnstable ? 1.2f : 1);

                GlStateManager.popMatrix();
                //GlStateManager.enableLighting();

                return;
            }
        }

        // Render kawoosh and animations (opening, closing going to/from center)
        if (rendererState.whiteOverlayAlpha != null) {

            if (rendererState.backStrip != null)
                rendererState.backStrip.render(tick, 0f, 0f, false, Math.max(0, 1.0f - rendererState.whiteOverlayAlpha - noxAlpha), 1);

            if (rendererState.frontStrip != null) {
                if(shouldRenderBackVortex()){
                    renderEventHorizon(partialTicks, false, 0.3f, false, 1, true);
                }
                double tickFromStart = (getWorld().getTotalWorldTime() - kawooshStart + partialTicks);
                double arg2 = (((tickFromStart - VORTEX_START) / SPEED_FACTOR) - (3.65 + 0.75)) / 1.5;
                if(!shouldRenderBackVortex() || arg2 <= 0) {
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.rotate(-180, 0, 1, 0);

                    // Bind non-kawoosh texture from back
                    ehTextureRes = getEventHorizonTextureResource(rendererState, false);
                    ehTexture = TextureLoader.getTexture(ehTextureRes);
                    if (ehTexture != null) ehTexture.bindTexture();

                    Float alpha = Math.max(0, 1.0f - rendererState.whiteOverlayAlpha - 0.3f - noxAlpha);

                    rendererState.frontStrip.render(tick, 0f, 0f, false, alpha, 1);
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
            }
        }

        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    /**
     * Renders event horizon(white/blue flat thing)
     *
     * @param white    Are we rendering the white overlay?
     * @param alpha    Alpha channel of the white overlay
     * @param backOnly Render only the back face?(Used in kawoosh)
     * @param mul      Multiplier of the horizon waving speed
     */
    @SuppressWarnings("all")
    protected void renderEventHorizon(double partialTicks, boolean white, Float alpha, boolean backOnly, float mul) {
        renderEventHorizon(partialTicks, white, alpha, backOnly, mul, false);
    }
    protected void renderEventHorizon(double partialTicks, boolean white, Float alpha, boolean backOnly, float mul, boolean closingAnimation) {
        if(getBlackHoleVortexDepth() != 0){
            //renderBackVortex(closingAnimation, false, getBlackHoleVortexDepth(), gateTile.blackHoleAnimationState.getBackVortexAngle(), gateTile.blackHoleAnimationState.getBackVortexRed());
            return;
        }
        float tick = (float) JSGMinecraftHelper.getClientTick();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();

        if(!closingAnimation) {
            for (int k = (backOnly ? 1 : 0); k < (shouldRenderBackVortex() ? 1 : 2); k++) {
                Float a = alpha;
                GlStateManager.pushMatrix();
                if (k == 1) {
                    GlStateManager.rotate(-180, 0, 1, 0);
                }

                if (a == null) a = 0.0f;

                if (k == 1) a += 0.3f;


                if (white)
                    StargateRendererStatic.innerCircle.render(tick, true, a, mul, (byte) 0, getEventHorizonColor());

                StargateRendererStatic.innerCircle.render(tick, false, 1.0f - a, mul, (byte) 0, getEventHorizonColor());


                for (StargateRendererStatic.QuadStrip strip : StargateRendererStatic.quadStrips) {
                    if (white) strip.render(tick, true, a, mul, (byte) 0, getEventHorizonColor());

                    strip.render(tick, false, 1.0f - a, mul, (byte) 0, getEventHorizonColor());
                }
                GlStateManager.popMatrix();
            }
        }
        GlStateManager.disableBlend();
        if (shouldRenderBackVortex()) {
            renderBackVortex(partialTicks, closingAnimation, true, 1, tick, 0);
        }
    }

    public float getBlackHoleVortexDepth(){
        return 0; //tileEntity.blackHoleAnimationState.getBackVortexDepth();
    }

    protected void renderBackVortex(double partialTicks, boolean closingAnimation, boolean onlyBack, float depthMul, float rotationZ, float redMul){
        float tick = (float) JSGMinecraftHelper.getClientTick();
        long kawooshStart = rendererState.gateWaitStart + 44 - 24;
        double tickFromStart = (getWorld().getTotalWorldTime() - kawooshStart + (double) partialTicks);
        double arg = (((tickFromStart - VORTEX_START) / SPEED_FACTOR) - (3.65 + 0.75)) / 1.5;
        if (arg < 0) return;
        float factor;
        if (arg > 1.82) factor = 1;
        else factor = (float) (-(((arg + 2.65f) * (arg + 2.65f - 4f)) / (-5.968f + 0.29333f)) + 0.63f);
        float mul = 0.3085f * factor * 2 * depthMul;

        float[] color = getEventHorizonColor();
        Color colorC = new Color((int) (color[0] * 255), (int) (color[1] * 255), (int) (color[2] * 255), 255);
        int mixColors = JSGColorUtil.blendColors(JSGColorUtil.fromColor(colorC), 0x00FF00FF, redMul);
        Color finalColor = JSGColorUtil.toColor(mixColors);

        GlStateManager.enableBlend();
        GlStateManager.pushMatrix();
        GlStateManager.rotate(rotationZ, 0, 0, 1);
        scaleVortex();
        for(int i = 0; i < (onlyBack ? 1 : 2); i++){
            GlStateManager.pushMatrix();
            if(i == 1){
                GlStateManager.scale(1, -1, 1);
            }
            float prevZ = 0;
            float prevRad = 0;

            int index = 0;
            for (Map.Entry<Float, Float> e : StargateRendererStatic.Z_RadiusMap.entrySet()) {
                float currentZ = e.getKey();
                if (currentZ > 0) continue;
                float currentRad = e.getValue() == 0 ? 0 : e.getValue() + StargateRendererStatic.getOffset(index, tick, 7, 1);
                float mul2 = 0.5f * (-currentZ / 5) * (Math.max(0, (1f+currentZ))/2);
                if (currentZ == 0){
                    currentRad = StargateRendererStatic.EVENT_HORIZON_RADIUS;
                    mul2 = 0;
                }
                if(rendererState.frontStrip != null && closingAnimation && currentRad < rendererState.frontStrip.innerRadius) continue;
                if (index != 0) {
                    new StargateRendererStatic.QuadStrip(9, currentRad, prevRad, tick, 1 / 5f * 7 * mul2).render(tick, currentZ * mul, prevZ * mul, false, (i == 1 ? 1 : 0.7f), 5 * mul2, false, (byte) 0, new float[]{finalColor.getRed()/255f, finalColor.getGreen()/255f, finalColor.getBlue()/255f}, false);
                }
                prevZ = currentZ;
                prevRad = currentRad;
                index++;
            }
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
    }

    @Override
    public boolean isGlobalRenderer(@Nonnull StargateAbstractBaseTile te) {
        return true;
    }

    protected void renderIris(double partialTicks, World world, S rendererState, boolean backOnly) {
    }

    public enum EnumVortexState {
        FORMING(0),
        FULL(1),
        DECREASING(2),
        STILL(3),
        CLOSING(4),
        SHRINKING(5);

        private static final Map<Integer, EnumVortexState> map = new HashMap<>();

        static {
            for (EnumVortexState packet : EnumVortexState.values()) {
                map.put(packet.index, packet);
            }
        }

        public final int index;

        EnumVortexState(int index) {
            this.index = index;
        }

        public static EnumVortexState valueOf(int index) {
            return map.get(index);
        }

        public boolean equals(EnumVortexState state) {
            return this.index == state.index;
        }
    }
}
