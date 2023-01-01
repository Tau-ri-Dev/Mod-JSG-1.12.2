package tauri.dev.jsg.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class JSGMinecraftHelper {
    /**
     * Get current tick on client side.
     * !IT DOES NOT PAUSE IF YOU PAUSE THE GAME!
     *
     * @return current tick (long)
     */
    public static long getClientTick() {
        return (long) Math.floor((Minecraft.getSystemTime() / (double) 1000) * 20);
    }

    public static double getClientTickPrecise() {
        return ((Minecraft.getSystemTime() / 1000D) * 20D);
    }

    public static long getPlayerTickClientSide() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if(player == null) return 0;
        return player.getEntityWorld().getTotalWorldTime();
    }
}
