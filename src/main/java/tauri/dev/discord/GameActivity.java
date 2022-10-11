package tauri.dev.discord;

import tauri.dev.jsg.JSG;

public class GameActivity {
    public static void register() {
        JSG.info("Discord: Registered!");
    }
    public static void setActivity() {
        JSG.info("Discord: Activity set!");
    }
    public static void clearActivity(){
        JSG.info("Discord: Unloaded!");
    }
}
