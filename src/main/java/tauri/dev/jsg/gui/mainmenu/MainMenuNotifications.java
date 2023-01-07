package tauri.dev.jsg.gui.mainmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static net.minecraft.client.gui.Gui.drawModalRectWithCustomSizedTexture;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class MainMenuNotifications {
    public static class Notification {
        public boolean dismissed;
        public final String[] lines;
        public final List<GuiButton> buttons;

        public Notification(List<GuiButton> buttons, String... text) {
            this.lines = text;
            this.buttons = buttons;
            dismissed = false;
        }

        public void dismiss() {
            dismissed = true;
        }

        public void actionPerformed(@Nonnull GuiButton button) {
        }

        public void render(int mouseX, int mouseY, int width, int height, int rectX, int rectY, GuiScreen parentScreen) {
            renderText(mouseX, mouseY, width, height, rectX, rectY, parentScreen);
        }
        public void renderText(int mouseX, int mouseY, int width, int height, int rectX, int rectY, GuiScreen parentScreen) {
            int centerX = getManager().getCenterPos(0, 0)[0];
            int i = 0;
            for (String s : lines) {
                if (parentScreen instanceof GuiCustomMainMenu)
                    ((GuiCustomMainMenu) parentScreen).drawCenteredString(parentScreen.mc.fontRenderer, s, centerX, rectY + 18 + (10 * i), 0x404040, false);
                else
                    parentScreen.mc.fontRenderer.drawString(s, centerX - (parentScreen.mc.fontRenderer.getStringWidth(s) / 2), rectY + 18 + (10 * i), 0x404040);
                i++;
            }
        }
    }

    // Static
    public static final MainMenuNotifications INSTANCE = new MainMenuNotifications();
    private static final ResourceLocation NOTIFICATION_TEXTURE = new ResourceLocation(JSG.MOD_ID, "textures/gui/mainmenu/popup.png");
    public static final int BUTTONS_ID_START = 40;

    public static MainMenuNotifications getManager() {
        return INSTANCE;
    }

    // Relative
    private final HashMap<Integer, Notification> NOTIFICATIONS = new HashMap<>();
    private int id = 0;

    public int getId() {
        return id;
    }

    public int add(Notification notification) {
        NOTIFICATIONS.put(getId(), notification);
        return id++;
    }

    @Nullable
    public Notification get(int id) {
        return NOTIFICATIONS.get(id);
    }

    @Nullable
    public Notification getFirstToDisplay() {
        for (Notification n : NOTIFICATIONS.values()) {
            if (!n.dismissed) return n;
        }
        return null;
    }

    public Notification currentDisplayed = getFirstToDisplay();

    public int width = 0;
    public int height = 0;
    public Minecraft mc = Minecraft.getMinecraft();

    public int[] getCenterPos(int rectWidth, int rectHeight) {
        return new int[]{((width - rectWidth) / 2), ((height - rectHeight) / 2)};
    }

    public void update() {
        currentDisplayed = getFirstToDisplay();
    }

    public static final int BACKGROUND_WIDTH = 300;
    public static final int BACKGROUND_HEIGHT = 140;

    public void render(int mouseX, int mouseY, int winWidth, int winHeight, GuiScreen parentScreen) {
        this.width = winWidth;
        this.height = winHeight;
        if (currentDisplayed == null) return;
        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1);

        int[] center = getCenterPos(BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        int x = center[0];
        int y = center[1];

        // Background
        Minecraft.getMinecraft().getTextureManager().bindTexture(NOTIFICATION_TEXTURE);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);

        currentDisplayed.render(mouseX, mouseY, width, height, x, y, parentScreen);

        GlStateManager.popMatrix();

    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (currentDisplayed == null) return;
        if (mouseButton != 0) return;
        for (GuiButton guibutton : currentDisplayed.buttons) {
            if (guibutton.mousePressed(mc, mouseX, mouseY)) {
                guibutton.playPressSound(mc.getSoundHandler());
                currentDisplayed.actionPerformed(guibutton);
            }
        }
    }
}
