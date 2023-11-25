package tauri.dev.jsg.renderer.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class StargateFadeOut {

    private static World world;

    private static long tickStart;
    public static boolean fading;

    private static final double TOTAL_EFFECT_TIME_IN = 15;

    public static double calcFog(World world, long tickStart, double partialTicks) {
        double effTick = world.getTotalWorldTime() - tickStart + partialTicks;
        double total = TOTAL_EFFECT_TIME_IN;
        double tick = effTick / total;
        return (tick < total) ? Math.min(1, (Math.sin(Math.toRadians(tick * 180)) * 1.4)) : -1;
    }

    @SubscribeEvent
    public static void onRender(RenderGameOverlayEvent.Post event) {
        if (fading) {
            float fog = (float) calcFog(world, tickStart, event.getPartialTicks());

            if (fog < 0) {
                fading = false;
            } else {
                int alpha = (int) (fog * (255 / 1.5f));

                alpha /= 3;
                alpha <<= 24;

                ScaledResolution res = event.getResolution();
                Gui.drawRect(0, 0, res.getScaledWidth(), res.getScaledHeight(), 0xFFFFFF | alpha);
            }
        }
    }

    public static void startFadeOut() {
        fading = true;
        world = Minecraft.getMinecraft().world;
        tickStart = world.getTotalWorldTime();
    }
}
