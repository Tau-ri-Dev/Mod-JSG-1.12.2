package tauri.dev.jsg.renderer.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class DestinyFTL {

    private static World world;

    private static long tickStart;
    public static boolean jumpingIn;
    public static boolean jumpingOut;

    public static float defaultFov;

    private static final double TOTAL_EFFECT_TIME_IN = 20 * 3;
    private static final double TOTAL_EFFECT_TIME_OUT = 20 * 3;

    public static double calcFog(World world, long tickStart, double partialTicks, boolean out) {
        double effTick = world.getTotalWorldTime() - tickStart + partialTicks;
        double total = (out ? TOTAL_EFFECT_TIME_OUT : TOTAL_EFFECT_TIME_IN);
        double tick = effTick / total;
        return (tick < total) ? Math.min(1, (Math.sin(Math.toRadians(tick * 180)) * 1.4)) : -1;
    }

    @SubscribeEvent
    public static void onRender(RenderGameOverlayEvent.Post event) {
        if (jumpingIn || jumpingOut) {
            float fog = (float) calcFog(world, tickStart, event.getPartialTicks(), jumpingOut);

            if (fog < 0) {
                jumpingIn = false;
                jumpingOut = false;
                Minecraft.getMinecraft().gameSettings.setOptionFloatValue(GameSettings.Options.FOV, defaultFov);
            } else {
                int alpha = (int) (fog * (255 / 2));
                float scale = (fog + 1f);

                alpha /= 3;
                alpha <<= 24;

                ScaledResolution res = event.getResolution();
                Gui.drawRect(0, 0, res.getScaledWidth(), res.getScaledHeight(), 0xFFFFFF | alpha);

                float newFov = (jumpingIn ? (defaultFov * scale) : (defaultFov / scale));
                Minecraft.getMinecraft().gameSettings.setOptionFloatValue(GameSettings.Options.FOV, newFov);
            }
        }
    }

    public static void jumpIn(boolean in) {
        if(jumpingIn || jumpingOut) return;
        defaultFov = Minecraft.getMinecraft().gameSettings.getOptionFloatValue(GameSettings.Options.FOV);

        world = Minecraft.getMinecraft().world;
        tickStart = world.getTotalWorldTime();

        if (in)
            jumpingIn = true;
        else
            jumpingOut = true;
    }
}
