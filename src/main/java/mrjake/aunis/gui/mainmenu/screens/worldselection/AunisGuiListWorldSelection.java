package mrjake.aunis.gui.mainmenu.screens.worldselection;

import com.google.common.collect.Lists;
import mrjake.aunis.gui.mainmenu.screens.AunisSinglePlayerGui;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class AunisGuiListWorldSelection extends GuiListExtended {
    private static final Logger LOGGER = LogManager.getLogger();
    private final AunisSinglePlayerGui worldSelection;
    private final List<AunisGuiListWorldSelectionEntry> entries = Lists.<AunisGuiListWorldSelectionEntry>newArrayList();
    private int selectedIdx = -1;

    public AunisGuiListWorldSelection(AunisSinglePlayerGui p_i46590_1_, Minecraft clientIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
        super(clientIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.worldSelection = p_i46590_1_;
        this.refreshList();
    }

    @Override
    public void drawScreen(int mouseXIn, int mouseYIn, float partialTicks)
    {
        if (this.visible)
        {
            this.mouseX = mouseXIn;
            this.mouseY = mouseYIn;
            int i = this.getScrollBarX();
            int j = i + 6;
            this.bindAmountScrolled();
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            // Forge: background rendering moved into separate method.
            //this.drawContainerBackground(tessellator);
            int k = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            int l = this.top + 4 - (int)this.amountScrolled;

            if (this.hasListHeader)
            {
                this.drawListHeader(k, l, tessellator);
            }

            this.drawSelectionBox(k, l, mouseXIn, mouseYIn, partialTicks);
            GlStateManager.disableDepth();
            this.overlayBackground(0, this.top, 255, 255);
            this.overlayBackground(this.bottom, this.height, 255, 255);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            GlStateManager.disableAlpha();
            GlStateManager.shadeModel(7425);
            GlStateManager.disableTexture2D();
            int j1 = this.getMaxScroll();

            if (j1 > 0)
            {
                int k1 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
                k1 = MathHelper.clamp(k1, 32, this.bottom - this.top - 8);
                int l1 = (int)this.amountScrolled * (this.bottom - this.top - k1) / j1 + this.top;

                if (l1 < this.top)
                {
                    l1 = this.top;
                }
            }

            this.renderDecorations(mouseXIn, mouseYIn);
            GlStateManager.enableTexture2D();
            GlStateManager.shadeModel(7424);
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
        }
    }

    public void refreshList()
    {
        ISaveFormat isaveformat = this.mc.getSaveLoader();
        List<WorldSummary> list;

        try
        {
            list = isaveformat.getSaveList();
        }
        catch (AnvilConverterException anvilconverterexception)
        {
            LOGGER.error("Couldn't load level list", (Throwable)anvilconverterexception);
            this.mc.displayGuiScreen(new GuiErrorScreen(I18n.format("selectWorld.unable_to_load"), anvilconverterexception.getMessage()));
            return;
        }

        Collections.sort(list);

        for (WorldSummary worldsummary : list)
        {
            this.entries.add(new AunisGuiListWorldSelectionEntry(this, worldsummary, this.mc.getSaveLoader()));
        }
    }

    @Override
    public AunisGuiListWorldSelectionEntry getListEntry(int index)
    {
        return this.entries.get(index);
    }

    @Override
    protected int getSize()
    {
        return this.entries.size();
    }

    @Override
    protected int getScrollBarX()
    {
        return super.getScrollBarX() + 20;
    }

    @Override
    public int getListWidth()
    {
        return super.getListWidth() + 50;
    }

    public void selectWorld(int idx)
    {
        this.selectedIdx = idx;
        this.worldSelection.selectWorld(this.getSelectedWorld());
    }

    @Override
    protected boolean isSelected(int slotIndex)
    {
        return slotIndex == this.selectedIdx;
    }

    @Nullable
    public AunisGuiListWorldSelectionEntry getSelectedWorld()
    {
        return this.selectedIdx >= 0 && this.selectedIdx < this.getSize() ? this.getListEntry(this.selectedIdx) : null;
    }

    public AunisSinglePlayerGui getGuiWorldSelection()
    {
        return this.worldSelection;
    }


    @Override
    protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks)
    {
        this.getListEntry(slotIndex).drawEntry(slotIndex, xPos, yPos, this.getListWidth(), heightIn, mouseXIn, mouseYIn, this.isMouseYWithinSlotBounds(mouseYIn) && this.getSlotIndexFromScreenCoords(mouseXIn, mouseYIn) == slotIndex, partialTicks);
    }

    @Override
    protected void updateItemPos(int entryID, int insideLeft, int yPos, float partialTicks)
    {
        this.getListEntry(entryID).updatePosition(entryID, insideLeft, yPos, partialTicks);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent)
    {
        if (this.isMouseYWithinSlotBounds(mouseY))
        {
            int i = this.getSlotIndexFromScreenCoords(mouseX, mouseY);

            if (i >= 0)
            {
                int j = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
                int k = this.top + 4 - this.getAmountScrolled() + i * this.slotHeight + this.headerPadding;
                int l = mouseX - j;
                int i1 = mouseY - k;

                if (this.getListEntry(i).mousePressed(i, mouseX, mouseY, mouseEvent, l, i1))
                {
                    this.setEnabled(false);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(int x, int y, int mouseEvent)
    {
        for (int i = 0; i < this.getSize(); ++i)
        {
            int j = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            int k = this.top + 4 - this.getAmountScrolled() + i * this.slotHeight + this.headerPadding;
            int l = x - j;
            int i1 = y - k;
            this.getListEntry(i).mouseReleased(i, x, y, mouseEvent, l, i1);
        }

        this.setEnabled(true);
        return false;
    }
}
