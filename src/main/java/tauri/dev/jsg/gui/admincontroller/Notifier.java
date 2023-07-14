package tauri.dev.jsg.gui.admincontroller;

import tauri.dev.jsg.util.JSGMinecraftHelper;

import javax.annotation.Nonnull;

@SuppressWarnings("all")
public class Notifier {
    private String lastText = null;
    private int color = 0x202020;
    private long lastTextEntered = 0;
    private int showSeconds = 0;

    public enum EnumAlertType{
        INFO(0x21CEB7),
        WARNING(0xCEA021),
        ERROR(0xCE2121);

        private final int color;
        EnumAlertType(int color){
            this.color = color;
        }
    }

    public void clearText(){
        this.lastText = null;
    }

    public void setText(@Nonnull String text, @Nonnull EnumAlertType type, int secondsToShow){
        lastTextEntered = JSGMinecraftHelper.getClientTick();
        showSeconds = secondsToShow;
        lastText = text;
        color = type.color;
    }

    public void render(GuiAdminController baseGui, int x, int y){
        if((JSGMinecraftHelper.getClientTick() - lastTextEntered) > (showSeconds * 20L) || lastText == null) return;
        baseGui.drawString(baseGui.mc.fontRenderer, lastText, x, y, color);
    }
}
