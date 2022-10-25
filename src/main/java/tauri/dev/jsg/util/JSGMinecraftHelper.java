package tauri.dev.jsg.util;

import net.minecraft.client.Minecraft;

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
}
