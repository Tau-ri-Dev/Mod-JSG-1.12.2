package mrjake.aunis.renderer.transportrings;

import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Contains single instance of a transport ring
 */
public class Ring {

    private final int index;
    private World world;
    private boolean shouldRender;
    private boolean shouldAnimate;
    private boolean ringsUprising;
    private long animationStart;
    private double y;
    private double yMax;

    public Ring(World world, int index) {
        this.world = world;

        this.shouldRender = false;

        this.y = 0;
        this.index = index;
        this.yMax = index;
    }

    public void render(double partialTicks, ElementEnum type, int distance) {
        if (distance >= 0) {
            yMax = (distance * 2) - index + 1.5 + 4;
        } else {
            yMax = (distance * 2) + index - (1.5 * 2) - 2;
        }
        if (shouldRender) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, y, 0);

            type.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
            GlStateManager.popMatrix();

        }

        if (shouldAnimate) {
            double effTick = world.getTotalWorldTime() - animationStart + partialTicks;

            effTick /= TransportRingsAbstractRenderer.ANIMATION_SPEED_DIVISOR;

            if (effTick > Math.PI) {
                effTick = Math.PI;
                shouldAnimate = false;
            }

            float cos = MathHelper.cos((float) effTick);

            if (ringsUprising)
                cos *= -1;

            y = ((cos + 1) / 2) * yMax;

            if (!ringsUprising && effTick == Math.PI)
                shouldRender = false;

//			Aunis.info("y = " + y);
        }
    }

    public void animate(boolean ringsUprising) {
        this.ringsUprising = ringsUprising;
        shouldRender = true;

        animationStart = world.getTotalWorldTime();
        shouldAnimate = true;
    }

    public void setTop() {
        y = yMax;

        shouldAnimate = false;
        shouldRender = true;
    }

    public void setDown() {
        shouldAnimate = false;
        shouldRender = false;
    }
}
