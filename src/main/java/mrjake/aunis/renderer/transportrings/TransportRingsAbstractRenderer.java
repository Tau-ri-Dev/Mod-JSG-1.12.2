package mrjake.aunis.renderer.transportrings;

import mrjake.aunis.Aunis;
import mrjake.aunis.block.props.TRPlatformBlock;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.loader.texture.TextureLoader;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.state.StateTypeEnum;
import mrjake.aunis.state.transportrings.TransportRingsRendererState;
import mrjake.aunis.tileentity.transportrings.TransportRingsAbstractTile;
import mrjake.aunis.transportrings.RingsPlatform;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class TransportRingsAbstractRenderer extends TileEntitySpecialRenderer<TransportRingsAbstractTile> {

    public static final int RING_COUNT = 5;
    public static final int INTERVAL_UPRISING = 5;
    public static final int INTERVAL_FALLING = 5;

    public static final double ANIMATION_SPEED_DIVISOR = 2.7;

    public static final int PLATFORM_ANIMATION_DURATION = 20;
    public static final float PLATFORM_MAX_Y = 0.8f;
    public static final float PLATFORM_MAX_X = 3.8f;

    public abstract void renderRings(TransportRingsRendererState state, float partialTicks, int distance);

    @Override
    public void render(TransportRingsAbstractTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        TransportRingsRendererState state = te.getRendererState();
        if (state == null) return;

        World world = te.getWorld();
        int ringsDistance = state.ringsDistance;

        if (state.rings.size() < RING_COUNT) {
            for (int i = state.rings.size(); i < RING_COUNT; i++) {
                state.rings.add(new Ring(i));
            }
        }
        for (Ring ring : state.rings) {
            ring.setWorld(world);
        }


        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 15 * 16, 15 * 16);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        GlStateManager.translate(0.50, 0.63271 / 2 + 1.35, 0.50);
        GlStateManager.scale(0.5, 0.5, 0.5);

        int relativeY = -4;
        if (ringsDistance < 0) {
            relativeY = 2;
        }

        // ---------------------------------------------------------------------------

        long tick = world.getTotalWorldTime() - state.animationStart;
        renderPlatform(state, tick, te);

        GlStateManager.translate(0, relativeY, 0);
        renderRings(state, partialTicks, ringsDistance);
        GlStateManager.popMatrix();

        if (state.isAnimationActive) {
            if (state.ringsUprising) {
                if (tick > 30) {
                    tick -= 30;

                    if (tick % INTERVAL_UPRISING == 0 && tick != state.lastTick) {
                        state.currentRing = (int) (tick / INTERVAL_UPRISING) - 1;

//						Aunis.info("[uprising][currentRing="+currentRing+"]: tick: "+tick);

                        // Handles correction when rings were not rendered
                        for (int ring = state.lastRingAnimated + 1; ring < Math.min(state.currentRing, RING_COUNT); ring++) {
//							Aunis.info("[uprising][ring="+ring+"]: setTop()");

                            state.rings.get(ring).setTop();
                        }

                        if (state.currentRing < RING_COUNT) {
                            state.rings.get(state.currentRing).animate(state.ringsUprising);

                            state.lastRingAnimated = state.currentRing;
                            state.lastTick = tick;
                        }

                        if (state.currentRing >= RING_COUNT - 1) {
                            state.ringsUprising = false;

                            state.lastRingAnimated = RING_COUNT;
                            state.lastTick = -1;
                        }
                    }
                }
            } else {
                if (tick > 100) {
                    tick -= 100;

                    if (tick % INTERVAL_FALLING == 0 && tick != state.lastTick) {
                        state.currentRing = RING_COUNT - (int) (tick / INTERVAL_FALLING);

//						Aunis.info("[falling ][currentRing="+currentRing+"]: lastRingAnimated: "+lastRingAnimated);

                        // Correction for skipped frames(when not looking at)
                        for (int ring = state.lastRingAnimated - 1; ring > Math.max(state.currentRing, -1); ring--) {
//							Aunis.info("[falling ][ring="+ring+"]: setDown()");

                            state.rings.get(ring).setDown();
                        }


                        if (state.currentRing >= 0) {
                            state.rings.get(state.currentRing).animate(state.ringsUprising);

                            state.lastRingAnimated = state.currentRing;
                            state.lastTick = tick;
                        } else {
                            state.isAnimationActive = false;
                        }

                        state.lastTick = tick;
                    }
                }
            }

            te.setState(StateTypeEnum.RENDERER_STATE, state);
        }
    }

    public void renderPlatform(TransportRingsRendererState state, long tick, TransportRingsAbstractTile te) {
        RingsPlatform platform = te.getPlatform();

        if (platform == null) return;

        IBlockState blockOverlayState = te.getPlatformOverlayBlockState();
        ResourceLocation platformOverlayTextureLocation = TextureLoader.getBlockTexture(blockOverlayState);

        TRPlatformBlock platformBlock = platform.platformBlock;

        ElementEnum platformMoving = platformBlock.getPlatformModelMoving();
        ElementEnum platformBase = platformBlock.getPlatformModelBase();
        ElementEnum platformToOverlay = platformBlock.getPlatformModelToOverlay();
        boolean canRenderUnderZero = platformBlock.canRenderUnderZero();

        float platformX = 0;
        float platformY = 0;
        int coefficient = -1;


        if (state.ringsDistance >= 0) {

            // ---------------------
            // MATH

            if (state.isAnimationActive) {
                if (tick < PLATFORM_ANIMATION_DURATION) {
                    if (state.ringsUprising) {
                        float multiplier = ((float) tick / (PLATFORM_ANIMATION_DURATION - ((float) PLATFORM_ANIMATION_DURATION / 3)));
                        if (multiplier > 1) multiplier = 1;
                        if (multiplier < -1) multiplier = -1;
                        if (tick > PLATFORM_ANIMATION_DURATION / 3) {
                            platformX = multiplier * PLATFORM_MAX_X;
                            platformY = PLATFORM_MAX_Y;
                        }
                        if (tick <= PLATFORM_ANIMATION_DURATION / 3)
                            platformY = multiplier * PLATFORM_MAX_Y;
                    }
                } else {
                    platformX = PLATFORM_MAX_X;
                    platformY = PLATFORM_MAX_Y;
                }
            }
            if (!state.ringsUprising) {
                long tick2 = tick - 135;
                if (tick2 >= 0) {
                    if (tick2 <= PLATFORM_ANIMATION_DURATION) {
                        float multiplier = ((float) tick2 / (PLATFORM_ANIMATION_DURATION - ((float) PLATFORM_ANIMATION_DURATION / 3)));
                        if (multiplier > 1) multiplier = 1;
                        if (multiplier < -1) multiplier = -1;
                        if (tick2 <= PLATFORM_ANIMATION_DURATION / 3) {
                            platformX = PLATFORM_MAX_X - multiplier * PLATFORM_MAX_X;
                            platformY = PLATFORM_MAX_Y;
                        }
                        if (tick2 > PLATFORM_ANIMATION_DURATION / 3)
                            platformY = PLATFORM_MAX_Y - multiplier * PLATFORM_MAX_Y;
                    }
                } else {
                    platformX = PLATFORM_MAX_X;
                    platformY = PLATFORM_MAX_Y;
                }
            }
        }

        // ---------------------
        // RENDER

        int distance = state.ringsDistance;
        if (distance < 0) {
            if (!canRenderUnderZero) return;
            distance += 4;
        }

        if (platformMoving != null) {
            if (state.ringsConfig.getOption(TransportRingsAbstractTile.ConfigOptions.RENDER_PLATFORM_MOVING.id).getBooleanValue()) {
                for (int i = 0; i < 2; i++) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(platformX * (i == 1 ? 1 : -1), (platformY * coefficient) - 3.4f + distance * 2, 0);
                    if (i == 1)
                        GlStateManager.rotate(180, 0, 1, 0);
                    platformMoving.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
                    GlStateManager.popMatrix();
                }
            }
        }
        if (platformBase != null) {
            if (state.ringsConfig.getOption(TransportRingsAbstractTile.ConfigOptions.RENDER_PLATFORM_BASE.id).getBooleanValue()) {

                GlStateManager.pushMatrix();
                GlStateManager.translate(0, -3.4f + distance * 2, 0);

                platformBase.bindTextureAndRender(BiomeOverlayEnum.NORMAL);

                Minecraft.getMinecraft().getTextureManager().bindTexture(platformOverlayTextureLocation);
                //Aunis.info(platformOverlayTextureLocation.toString());
                platformToOverlay.render();
                GlStateManager.popMatrix();
            }
        }
    }
}
