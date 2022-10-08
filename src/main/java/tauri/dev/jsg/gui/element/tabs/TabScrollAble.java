package tauri.dev.jsg.gui.element.tabs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public abstract class TabScrollAble extends Tab {
    protected static final int SCROLL_AMOUNT = 5;
    protected int scrolled = 0;

    protected TabScrollAble(TabBuilder builder) {
        super(builder);
    }

    public void renderCover(FontRenderer fontRenderer){
        Minecraft mc = Minecraft.getMinecraft();

        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);

        mc.getTextureManager().bindTexture(bgTexLocation);
        Gui.drawModalRectWithCustomSizedTexture(guiLeft+currentOffsetX, guiTop+defaultY, bgTexX + width + 1, bgTexY, width, height, textureSize, textureSize);

        fontRenderer.drawString(tabTitle, guiLeft+currentOffsetX+(side.left() ? 24 : 0)+4, guiTop+defaultY+10, 4210752);

        GlStateManager.disableBlend();
    }

    @Override
    public void closeTab() {
        super.closeTab();
        this.scrolled = 0;
    }

    public void scroll(int k) {
        if (k == 0) return;
        if (k < 0) k = -1;
        if (k > 0) k = 1;
        if (isVisible() && isOpen() && canContinueScrolling(k)) {
            scrolled += (SCROLL_AMOUNT * k);
        }
    }

    public abstract boolean canRenderEntry(int x, int y);

    public abstract boolean canContinueScrolling(int k);
}
