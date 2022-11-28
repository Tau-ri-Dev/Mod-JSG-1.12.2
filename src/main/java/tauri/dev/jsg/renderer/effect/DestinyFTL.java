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
    private static boolean jumpingIn;
    private static float defaultFov;

    private static final int TOTAL_EFFECT_TIME = 20;

    public static double calcFog(World world, long tickStart, double partialTicks) {
        double effTick = world.getTotalWorldTime() - tickStart + partialTicks;

        return -(effTick * (effTick - TOTAL_EFFECT_TIME)) / (20 * 20);
    }

    @SubscribeEvent
    public static void onRender(RenderGameOverlayEvent.Post event) {
        if (jumpingIn) {
            float fog = (float) calcFog(world, tickStart, event.getPartialTicks());

            if (fog < 0) {
                jumpingIn = false;
                Minecraft.getMinecraft().gameSettings.setOptionFloatValue(GameSettings.Options.FOV, defaultFov);
            } else {
                int alpha = (int) (fog * (255 / 3));
                float scale = (fog * 2f);

                alpha /= 3;
                alpha <<= 24;

                ScaledResolution res = event.getResolution();
                Gui.drawRect(0, 0, res.getScaledWidth(), res.getScaledHeight(), 0xFFFFFF | alpha);

                float newFov = defaultFov * scale;
                Minecraft.getMinecraft().gameSettings.setOptionFloatValue(GameSettings.Options.FOV, newFov);
            }
        }
    }

    public static void jumpIn() {
        defaultFov = Minecraft.getMinecraft().gameSettings.getOptionFloatValue(GameSettings.Options.FOV);
        world = Minecraft.getMinecraft().world;
        tickStart = world.getTotalWorldTime();

        jumpingIn = true;
    }
}
