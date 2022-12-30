package tauri.dev.jsg.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public abstract class JSGTexturedGui extends GuiScreen {
    protected int guiLeft;
    protected int guiTop;

    protected final int xSize;
    protected final int ySize;

    public JSGTexturedGui(int sizeX, int sizeY){
        this.xSize = sizeX;
        this.ySize = sizeY;
    }

    public abstract ResourceLocation getBackground();

    public void drawBackground(){
        drawDefaultBackground();
        mc.getTextureManager().bindTexture(getBackground());
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    public void initGui(){
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    public abstract void drawForeground(int mouseX, int mouseY, float partialTicks);

    public void drawButtons(int mouseX, int mouseY, float partialTicks){
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        drawBackground();
        drawButtons(mouseX, mouseY, partialTicks);

        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft, guiTop, 0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        drawForeground(mouseX, mouseY, partialTicks);
        GlStateManager.popMatrix();
    }
}
