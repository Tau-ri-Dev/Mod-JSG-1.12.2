package mrjake.aunis.renderer.transportrings;

import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.state.transportrings.TransportRingsRendererState;
import mrjake.aunis.tesr.RendererInterface;
import mrjake.aunis.util.AunisAxisAlignedBB;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public abstract class TransportRingsAbstractRenderer implements RendererInterface {

    public static final int RING_COUNT = 5;
    public static final int INTERVAL_UPRISING = 5;
    public static final int INTERVAL_FALLING = 5;

    public static final double ANIMATION_SPEED_DIVISOR = 2.7;

    public static final int PLATFORM_ANIMATION_DURATION = 10;
    public static final float PLATFORM_MAX_Y = 0.8f;
    public static final float PLATFORM_MAX_X = 3.5f;

    protected World world;
    protected AunisAxisAlignedBB localTeleportBox;
    protected List<Ring> rings;

    public TransportRingsAbstractRenderer(World world, BlockPos pos, AunisAxisAlignedBB localTeleportBox) {
        this.world = world;
        this.localTeleportBox = localTeleportBox;

        rings = new ArrayList<>();
        for (int i = 0; i < RING_COUNT; i++) {
            rings.add(new Ring(world, i));
        }
    }


    // --------------------------------------------------------------------------
    private int currentRing;
    private int lastRingAnimated;
    private long lastTick;
    private int ringsDistance;
    private final AunisAxisAlignedBB renderBoundingBox = new AunisAxisAlignedBB(-3, -40, -3, 3, 40, 3);

    public abstract void renderRings(float partialTicks, int distance);

    @Override
    public void render(double x, double y, double z, float partialTicks) {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 15 * 16, 15 * 16);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        if (AunisConfig.debugConfig.renderBoundingBoxes) {
            localTeleportBox.render();
            renderBoundingBox.render();
        }

        GlStateManager.translate(0.50, 0.63271 / 2 + 1.35, 0.50);
        GlStateManager.scale(0.5, 0.5, 0.5);

        int relativeY = -4;
        if(ringsDistance < 0) {
            relativeY = 2;
        }

        // ---------------------------------------------------------------------------

        long tick = world.getTotalWorldTime() - state.animationStart;
        renderPlatform(tick);

        GlStateManager.translate(0, relativeY, 0);
        renderRings(partialTicks, ringsDistance);
        GlStateManager.popMatrix();

        if (state.isAnimationActive) {
            /**
             * If the rings are going up(initial state), wait 30 ticks(1.5s) for the animation to begin
             */
            if (state.ringsUprising) {
                if (tick > 30) {
                    tick -= 30;

                    /**
                     * Spawn rings in intervals of 7 ticks(not repeated in a single tick)
                     */
                    if (tick % INTERVAL_UPRISING == 0 && tick != lastTick) {
                        currentRing = (int) (tick / INTERVAL_UPRISING) - 1;

//						Aunis.info("[uprising][currentRing="+currentRing+"]: tick: "+tick);

                        // Handles correction when rings were not rendered
                        for (int ring = lastRingAnimated + 1; ring < Math.min(currentRing, RING_COUNT); ring++) {
//							Aunis.info("[uprising][ring="+ring+"]: setTop()");

                            rings.get(ring).setTop();
                        }

                        if (currentRing < RING_COUNT) {
                            rings.get(currentRing).animate(state.ringsUprising);

                            lastRingAnimated = currentRing;
                            lastTick = tick;
                        }

                        if (currentRing >= RING_COUNT - 1) {
                            state.ringsUprising = false;

                            lastRingAnimated = RING_COUNT;
                            lastTick = -1;
                        }
                    }
                }
            }

            /**
             * If going down wait 100 tick (5s, transport sound played)
             */
            else {
                if (tick > 100) {
                    tick -= 100;

                    /**
                     * Start lowering them in interval of 5 ticks
                     */
                    if (tick % INTERVAL_FALLING == 0 && tick != lastTick) {
                        currentRing = RING_COUNT - (int) (tick / INTERVAL_FALLING);

//						Aunis.info("[falling ][currentRing="+currentRing+"]: lastRingAnimated: "+lastRingAnimated);

                        // Correction for skipped frames(when not looking at)
                        for (int ring = lastRingAnimated - 1; ring > Math.max(currentRing, -1); ring--) {
//							Aunis.info("[falling ][ring="+ring+"]: setDown()");

                            rings.get(ring).setDown();
                        }


                        if (currentRing >= 0) {
                            rings.get(currentRing).animate(state.ringsUprising);

                            lastRingAnimated = currentRing;
                            lastTick = tick;
                        } else {
                            state.isAnimationActive = false;
                        }

                        lastTick = tick;
                    }
                }
            }
        }
    }

    public void animationStart(long animationStart, int distance) {
        setRingsDistance(distance);
        lastTick = -1;
        currentRing = 0;
        lastRingAnimated = -1;

        state.animationStart = animationStart;
        state.ringsUprising = true;
        state.isAnimationActive = true;
    }

    public void setRingsDistance(int distance){
        state.ringsDistance = distance;
        ringsDistance = distance;
        localTeleportBox = new AunisAxisAlignedBB(-1, ringsDistance, -1, 2, ringsDistance + 2.5, 2);
    }

    TransportRingsRendererState state = new TransportRingsRendererState();

    public void setState(TransportRingsRendererState rendererState) {
        lastTick = -1;
        this.state = rendererState;
    }

    public void renderPlatform(long tick){
        float platformX = 0;
        float platformY = 0;
        int coefficient = -1;
        if(ringsDistance < 0) coefficient = 1;
        if (state.isAnimationActive) {
            // temporarily rendering the platform here
            if (tick < PLATFORM_ANIMATION_DURATION) {
                if (state.ringsUprising) {
                    float multiplier = ((float) tick / (PLATFORM_ANIMATION_DURATION - ((float) PLATFORM_ANIMATION_DURATION/3)));
                    if(multiplier > 1) multiplier = 1;
                    if(multiplier < -1) multiplier = -1;
                    if(tick > PLATFORM_ANIMATION_DURATION/3) {
                        platformX = multiplier * PLATFORM_MAX_X;
                        platformY = PLATFORM_MAX_Y;
                    }
                    if(tick <= PLATFORM_ANIMATION_DURATION/3)
                        platformY = multiplier * PLATFORM_MAX_Y;
                }
            }
            else{
                platformX = PLATFORM_MAX_X;
                platformY = PLATFORM_MAX_Y;
            }
        }
        if (!state.ringsUprising) {
            long tick2 = tick - 135;
            if(tick2 >= 0) {
                if (tick2 <= PLATFORM_ANIMATION_DURATION) {
                    float multiplier = ((float) tick2 / (PLATFORM_ANIMATION_DURATION - ((float) PLATFORM_ANIMATION_DURATION / 3)));
                    if (multiplier > 1) multiplier = 1;
                    if (multiplier < -1) multiplier = -1;
                    if (tick2 <= PLATFORM_ANIMATION_DURATION / 3) {
                        platformX = PLATFORM_MAX_X - multiplier * PLATFORM_MAX_X;
                        platformY = PLATFORM_MAX_Y;
                    }
                    if (tick2 > PLATFORM_ANIMATION_DURATION / 3)
                        platformY = PLATFORM_MAX_Y- multiplier * PLATFORM_MAX_Y;
                }
            }
            else{
                platformX = PLATFORM_MAX_X;
                platformY = PLATFORM_MAX_Y;
            }
        }
        for (int i = 0; i < 2; i++) {
            GlStateManager.pushMatrix();
            int distance = ringsDistance;
            if(distance < 0) distance += 4;
            GlStateManager.translate(platformX * (i == 1 ? 1 : -1), (platformY * coefficient) - 3.2f + distance*2, 0);
            if (i == 1)
                GlStateManager.rotate(180, 0, 1, 0);
            ElementEnum.PLATFORM_RINGS_GOAULD_BASIC.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
            GlStateManager.popMatrix();
        }
    }
}
