package mrjake.aunis.gui;

import com.google.common.collect.Lists;
import mrjake.aunis.gui.mainmenu.screens.resourcepacks.AunisGuiSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

import static net.minecraft.client.gui.Gui.drawRect;

@SideOnly(Side.CLIENT)
public class AunisGuiOptionsRowList extends GuiOptionsRowList {
    protected final ArrayList<Row> options = Lists.<AunisGuiOptionsRowList.Row>newArrayList();
    public AunisGuiOptionsRowList(Minecraft mcIn, int p_i45015_2_, int p_i45015_3_, int p_i45015_4_, int p_i45015_5_, int p_i45015_6_, GameSettings.Options... p_i45015_7_) {
        super(mcIn, p_i45015_2_, p_i45015_3_, p_i45015_4_, p_i45015_5_, p_i45015_6_, p_i45015_7_);
        this.centerListVertically = false;

        for (int i = 0; i < p_i45015_7_.length; i += 2)
        {
            GameSettings.Options gamesettings$options = p_i45015_7_[i];
            GameSettings.Options gamesettings$options1 = i < p_i45015_7_.length - 1 ? p_i45015_7_[i + 1] : null;
            AunisGuiButton guibutton = this.createButton(mcIn, p_i45015_2_ / 2 - 205, (12 * i) + 40, gamesettings$options);
            AunisGuiButton guibutton1 = this.createButton(mcIn, p_i45015_2_ / 2 + 5, (12 * i) + 40, gamesettings$options1);
            this.options.add(new AunisGuiOptionsRowList.Row(guibutton, guibutton1));
        }
    }

    private AunisGuiButton createButton(Minecraft mcIn, int p_148182_2_, int p_148182_3_, GameSettings.Options options)
    {
        if (options == null)
        {
            return null;
        }
        else
        {
            int i = options.getOrdinal();
            return (AunisGuiButton)(options.isFloat() ? new AunisGuiSlider(i, p_148182_2_, p_148182_3_, options) : new AunisOptionButton(i, p_148182_2_, p_148182_3_, options, mcIn.gameSettings.getKeyBinding(options)));
        }
    }


    public AunisGuiOptionsRowList.Row getListEntry(int index)
    {
        return this.options.get(index);
    }

    protected int getSize()
    {
        return this.options.size();
    }

    public int getListWidth()
    {
        return 400;
    }

    protected int getScrollBarX()
    {
        return super.getScrollBarX() + 32;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.visible)
        {
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.drawBackground();
            drawRect(0, 2000, 0, 2000, 0x00000099);
            int i = this.getScrollBarX();
            int j = i + 6;
            this.bindAmountScrolled();
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            Tessellator tessellator = Tessellator.getInstance();
            int k = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            int l = this.top + 4 - (int)this.amountScrolled;

            if (this.hasListHeader)
            {
                this.drawListHeader(k, l, tessellator);
            }

            this.drawSelectionBox(k, l, mouseX, mouseY, partialTicks);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            GlStateManager.disableBlend();
        }
    }

    protected void drawSelectionBox(int insideLeft, int insideTop, int mouseXIn, int mouseYIn, float partialTicks){
        int i = this.getSize();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        for (int j = 0; j < i; ++j)
        {
            int k = insideTop + j * this.slotHeight + this.headerPadding;
            int l = this.slotHeight - 4;

            if (k > this.bottom || k + l < this.top)
            {
                this.updateItemPos(j, insideLeft, k, partialTicks);
            }

            if (this.showSelectionBox && this.isSelected(j))
            {
                int i1 = this.left + (this.width / 2 - this.getListWidth() / 2);
                int j1 = this.left + this.width / 2 + this.getListWidth() / 2;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.disableTexture2D();
                drawRect(0, 2000, 0, 2000, 0x00000099);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                bufferbuilder.pos((double)i1, (double)(k + l + 2), 0.0D).tex(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
                bufferbuilder.pos((double)j1, (double)(k + l + 2), 0.0D).tex(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
                bufferbuilder.pos((double)j1, (double)(k - 2), 0.0D).tex(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
                bufferbuilder.pos((double)i1, (double)(k - 2), 0.0D).tex(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
                bufferbuilder.pos((double)(i1 + 1), (double)(k + l + 1), 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos((double)(j1 - 1), (double)(k + l + 1), 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos((double)(j1 - 1), (double)(k - 1), 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos((double)(i1 + 1), (double)(k - 1), 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
                tessellator.draw();
                GlStateManager.enableTexture2D();
            }

            this.drawSlot(j, insideLeft, k, l, mouseXIn, mouseYIn, partialTicks);
        }
    }

    protected void overlayBackground(int startY, int endY, int startAlpha, int endAlpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        this.mc.getTextureManager().bindTexture(Gui.OPTIONS_BACKGROUND);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos((double)this.left, (double)endY, 0.0D).tex(0.0D, (double)((float)endY / 32.0F)).color(64, 64, 64, endAlpha).endVertex();
        bufferbuilder.pos((double)(this.left + this.width), (double)endY, 0.0D).tex((double)((float)this.width / 32.0F), (double)((float)endY / 32.0F)).color(64, 64, 64, endAlpha).endVertex();
        bufferbuilder.pos((double)(this.left + this.width), (double)startY, 0.0D).tex((double)((float)this.width / 32.0F), (double)((float)startY / 32.0F)).color(64, 64, 64, startAlpha).endVertex();
        bufferbuilder.pos((double)this.left, (double)startY, 0.0D).tex(0.0D, (double)((float)startY / 32.0F)).color(64, 64, 64, startAlpha).endVertex();
        tessellator.draw();
    }

    public void setSlotXBoundsFromLeft(int leftIn)
    {
        this.left = leftIn;
        this.right = leftIn + this.width;
    }

    public int getSlotHeight()
    {
        return this.slotHeight;
    }

    @SideOnly(Side.CLIENT)
    public static class Row extends GuiOptionsRowList.Row implements GuiListExtended.IGuiListEntry
    {
        private final Minecraft client = Minecraft.getMinecraft();
        private final AunisGuiButton buttonA;
        private final AunisGuiButton buttonB;

        public Row(AunisGuiButton buttonAIn, AunisGuiButton buttonBIn)
        {
            super(buttonAIn, buttonBIn);
            this.buttonA = buttonAIn;
            this.buttonB = buttonBIn;
        }

        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks)
        {
            if (this.buttonA != null)
            {
                this.buttonA.y = y;
                this.buttonA.drawButton(this.client, mouseX, mouseY, partialTicks);
            }

            if (this.buttonB != null)
            {
                this.buttonB.y = y;
                this.buttonB.drawButton(this.client, mouseX, mouseY, partialTicks);
            }
        }

        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY)
        {
            if (this.buttonA.mousePressed(this.client, mouseX, mouseY))
            {
                if (this.buttonA instanceof AunisOptionButton)
                {
                    this.client.gameSettings.setOptionValue(((AunisOptionButton)this.buttonA).getOption(), 1);
                    this.buttonA.displayString = this.client.gameSettings.getKeyBinding(GameSettings.Options.byOrdinal(this.buttonA.id));
                }

                return true;
            }
            else if (this.buttonB != null && this.buttonB.mousePressed(this.client, mouseX, mouseY))
            {
                if (this.buttonB instanceof AunisOptionButton)
                {
                    this.client.gameSettings.setOptionValue(((AunisOptionButton)this.buttonB).getOption(), 1);
                    this.buttonB.displayString = this.client.gameSettings.getKeyBinding(GameSettings.Options.byOrdinal(this.buttonB.id));
                }

                return true;
            }
            else
            {
                return false;
            }
        }

        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY)
        {
            if (this.buttonA != null)
            {
                this.buttonA.mouseReleased(x, y);
            }

            if (this.buttonB != null)
            {
                this.buttonB.mouseReleased(x, y);
            }
        }

        public void updatePosition(int slotIndex, int x, int y, float partialTicks)
        {
        }
    }
}
