package tauri.dev.jsg.renderer.stargate;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import tauri.dev.jsg.config.JSGConfig;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static tauri.dev.jsg.renderer.stargate.StargateAbstractRenderer.isEhAnimatedLoaded;

public class StargateRendererStatic {
    static final float EVENT_HORIZON_RADIUS = 3.790975f;

    private static final int QUADS = 16;
    private static final int SECTIONS = 36 * 2;
    private static final float SECTION_ANGLE = (float) (2 * Math.PI / SECTIONS);

    public static final float INNER_CIRCLE_RADIUS = 0.25f;
    private static final float QUAD_STEP = (EVENT_HORIZON_RADIUS - INNER_CIRCLE_RADIUS) / QUADS;

    private static final List<Float> OFFSET_LIST = new ArrayList<>();
    private static final List<Float> SIN = new ArrayList<>();
    private static final List<Float> COS = new ArrayList<>();

    private static final List<Float> QUAD_RADIUS = new ArrayList<>();

    static InnerCircle innerCircle;
    static List<QuadStrip> quadStrips = new ArrayList<>();

    private static final Random RANDOM = new Random();

    private static float getRandomFloat() {
        return RANDOM.nextFloat() * 2 - 1;
    }

    private static float getOffset(int index, float tick, float mul, int quadStripIndex) {
        return (float) (Math.sin(tick / 4f + OFFSET_LIST.get(index)) * mul * (quadStripIndex / 4f) * (quadStripIndex - quadStrips.size()) / 400f);
    }

    private static float toUV(float coord) {
        return (coord + 1) / 2f;
    }

    static {
        initEventHorizon();
        initKawoosh();
    }

    private static void initEventHorizon() {
        for (int i = 0; i < SECTIONS * (QUADS + 1); i++) {
            OFFSET_LIST.add(getRandomFloat() * 3);
        }

        for (int i = 0; i <= SECTIONS; i++) {
            SIN.add(MathHelper.sin(SECTION_ANGLE * i));
            COS.add(MathHelper.cos(SECTION_ANGLE * i));
        }

        innerCircle = new InnerCircle();

        for (int i = 0; i <= QUADS; i++) {
            QUAD_RADIUS.add(INNER_CIRCLE_RADIUS + QUAD_STEP * i);
        }

        for (int i = 0; i < QUADS; i++) {
            quadStrips.add(new QuadStrip(i));
        }

        // horizonStateChange = world.getTotalWorldTime();
    }

    public static class InnerCircle {
        private final List<Float> x = new ArrayList<>();
        private final List<Float> y = new ArrayList<>();

        private final List<Float> tx = new ArrayList<>();
        private final List<Float> ty = new ArrayList<>();

        public InnerCircle() {
            float texMul = (INNER_CIRCLE_RADIUS / EVENT_HORIZON_RADIUS);

            for (int i = 0; i < SECTIONS; i++) {
                x.add(SIN.get(i) * INNER_CIRCLE_RADIUS);
                y.add(COS.get(i) * INNER_CIRCLE_RADIUS);

                tx.add(toUV(SIN.get(i) * texMul));
                ty.add(toUV(COS.get(i) * texMul));
            }
        }

        public void render(float tick, boolean white, Float alpha, float mul) {
            render(tick, white, alpha, mul, (byte) 0);
        }

        public void render(float tick, boolean white, Float alpha, float mul, byte animationOverride) {
            render(tick, white, alpha, mul, animationOverride, new float[]{1, 1, 1});
        }
        public void render(float tick, boolean white, Float alpha, float mul, byte animationOverride, float[] color) {
            boolean animated = !JSGConfig.Stargate.eventHorizon.disableAnimatedEventHorizon && isEhAnimatedLoaded();
            if (animationOverride == -1) animated = false;
            if (animationOverride == 1) animated = true;

            if (white) {
                GlStateManager.disableTexture2D();
                if (alpha > 0.5f)
                    alpha = 1.0f - alpha;
            }

            glBegin(GL_TRIANGLE_FAN);

            int texIndex = (int) (tick * 4 % 185);
            float xTexOffset = texIndex % 14 / 14f;
            float yTexOffset = texIndex / 14 / 14f;

            float xTex = 0.5f;
            float yTex = 0.5f;

            if (animated) {
                xTex /= 14.0f;
                xTex += xTexOffset;
                yTex /= 14.0f;
                yTex += yTexOffset;
            } else yTex *= -1;

            if (alpha != null) glColor4f(color[0], color[1], color[2], alpha);
            if (!white) glTexCoord2f(xTex, yTex);

            glVertex3f(0, 0, 0);

            int index;
            for (int i = SECTIONS; i >= 0; i--) {
                if (i == SECTIONS)
                    index = 0;
                else
                    index = i;

                xTex = tx.get(index);
                yTex = ty.get(index);

                if (animated) {
                    xTex /= 14.0f;
                    xTex += xTexOffset;
                    yTex /= 14.0f;
                    yTex += yTexOffset;
                } else yTex *= -1;

                if (!white) glTexCoord2f(xTex, yTex);
                glVertex3f(x.get(index), y.get(index), getOffset(index, tick * mul, mul, 0));
            }

            glEnd();

            if (alpha != null) glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            if (white) GlStateManager.enableTexture2D();
        }
    }

    public static class QuadStrip {
        private final List<Float> x = new ArrayList<>();
        private final List<Float> y = new ArrayList<>();

        private final List<Float> tx = new ArrayList<>();
        private final List<Float> ty = new ArrayList<>();

        private final int quadStripIndex;

        public QuadStrip(int quadStripIndex) {
            this(quadStripIndex, QUAD_RADIUS.get(quadStripIndex), QUAD_RADIUS.get(quadStripIndex + 1), null);
        }

        public QuadStrip(int quadStripIndex, float innerRadius, float outerRadius, Float tick) {
            this.quadStripIndex = quadStripIndex;
            recalculate(innerRadius, outerRadius, tick);
        }

        public void recalculate(float innerRadius, float outerRadius, Float tick) {

            List<Float> radius = new ArrayList<>();
            List<Float> texMul = new ArrayList<>();


            radius.add(innerRadius);
            radius.add(outerRadius);

            for (int i = 0; i < 2; i++)
                texMul.add(radius.get(i) / EVENT_HORIZON_RADIUS);

            for (int k = 0; k < 2; k++) {
                for (int i = 0; i < SECTIONS; i++) {
                    float rad = radius.get(k);

                    if (tick != null) {
                        rad += getOffset(i, tick, 1, quadStripIndex) * 2;
                    }

                    x.add(rad * SIN.get(i));
                    y.add(rad * COS.get(i));

                    tx.add(toUV(SIN.get(i) * texMul.get(k)));
                    ty.add(toUV(COS.get(i) * texMul.get(k)));
                }
            }
        }

        public void render(float tick, boolean white, Float alpha, float mul) {
            render(tick, null, null, white, alpha, mul);
        }

        public void render(float tick, boolean white, Float alpha, float mul, byte animationOverride) {
            render(tick, null, null, white, alpha, mul, false, animationOverride);
        }

        public void render(float tick, boolean white, Float alpha, float mul, byte animationOverride, float[] color) {
            render(tick, null, null, white, alpha, mul, false, animationOverride, color);
        }

        public void render(float tick, Float outerZ, Float innerZ, boolean white, Float alpha, float mul) {
            render(tick, outerZ, innerZ, white, alpha, mul, false, (byte) 0);
        }

        public void render(float tick, Float outerZ, Float innerZ, boolean white, Float alpha, float mul, boolean reversed, byte animationOverride) {
            render(tick, outerZ, innerZ, white, alpha, mul, reversed, animationOverride, new float[]{1, 1, 1});
        }
        public void render(float tick, Float outerZ, Float innerZ, boolean white, Float alpha, float mul, boolean reversed, byte animationOverride, float[] color) {
            boolean animate = !tauri.dev.jsg.config.JSGConfig.Stargate.eventHorizon.disableAnimatedEventHorizon && isEhAnimatedLoaded();
            if (animationOverride == -1) animate = false;
            if (animationOverride == 1) animate = true;

            if (white) {
                GlStateManager.disableTexture2D();
                if (alpha > 0.5f)
                    alpha = 1.0f - alpha;
            }

            if (alpha != null) glColor4d(color[0], color[1], color[2], alpha);

            glBegin(GL_QUAD_STRIP);

            int index;

            int texIndex = (int) (tick * 4 % 185);
            float xTexOffset = texIndex % 14 / 14f;
            float yTexOffset = texIndex / 14 / 14f;

            for (int i = reversed ? 0 : SECTIONS; (reversed && i <= SECTIONS) || (!reversed && i >= 0); i += (reversed ? 1 : -1)) {
                if (i == SECTIONS)
                    index = 0;
                else
                    index = i;

                float z;

                if (outerZ != null) z = outerZ;
                else z = getOffset(index + SECTIONS * quadStripIndex, tick * mul, mul, quadStripIndex);

                float xTex = tx.get(index);
                float yTex = ty.get(index);

                if (animate) {
                    xTex /= 14.0f;
                    xTex += xTexOffset;
                    yTex /= 14.0f;
                    yTex += yTexOffset;
                } else yTex *= -1;

                if (!white) glTexCoord2f(xTex, yTex);
                glVertex3f(x.get(index), y.get(index), z);

//				JSG.info("z: " + z);

                index = index + SECTIONS;

                xTex = tx.get(index);
                yTex = ty.get(index);

                if (animate) {
                    xTex /= 14.0f;
                    xTex += xTexOffset;
                    yTex /= 14.0f;
                    yTex += yTexOffset;
                } else yTex *= -1;

                if (innerZ != null) z = innerZ;
                else z = getOffset(index + SECTIONS * quadStripIndex, tick * mul, mul, quadStripIndex + 1);

                if (!white) glTexCoord2f(xTex, yTex);
                glVertex3f(x.get(index), y.get(index), z);
            }

            glEnd();

            if (white) GlStateManager.enableTexture2D();
            if (alpha != null) glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    static final float kawooshRadius = 2.5f;
    private static final float kawooshSize = 7f;
    private static final float kawooshSections = 128;

    static Map<Float, Float> Z_RadiusMap;

    // Generate kawoosh shape using 4 functions
    private static void initKawoosh() {
        Z_RadiusMap = new LinkedHashMap<>();

        float begin = 0;
        float end = 0.545f;

        float rng = end - begin;

        float step = rng / kawooshSections;
        boolean first = true;

        float scaleX = kawooshSize / rng;
        float scaleY = 1;

        for (int i = 0; i <= kawooshSections; i++) {
            float x = begin + step * i;
            float y = 0;

            float border1 = 0.2575f;
            float border2 = 0.4241f;
            float border3 = 0.4577f;


            if (x >= 0 && x <= border1) {
                float a = 2f;
                float b = -4.7f;
                float c = 2.1f;

                float p = x + (b / 20f);
                y = (a / 2f) * (p * p) + (c / 30f);

                if (first) {
                    first = false;
                    scaleY = kawooshRadius / y;
                    // JSG.info("radius: " + kawooshRadius + "  y: " + y + "  scale: "+kawooshScaleFactor);
                }
            } else if (x > border1 && x <= border2) {
                float a = 1.4f;
                float b = -4.3f;
                float c = 1.4f;

                float p = x + (b / 20f);
                y = (a / 5f) * (p * p) + (c / 20f);
            } else if (x > border2 && x <= border3) {
                float a = -7.4f;
                float b = -8.6f;
                float c = 3.3f;

                float p = x + (b / 20f);
                y = a * (p * p) + (c / 40f);
            } else if (x > border3 && x <= 0.545f) {
                float a = 5.2f;

                y = (float) (a / 20f * Math.sqrt(0.545f - x));
            }

            Z_RadiusMap.put(x * scaleX, y * scaleY);
        }
    }

}
