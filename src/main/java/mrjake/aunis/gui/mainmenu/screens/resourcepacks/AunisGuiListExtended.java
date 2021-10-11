package mrjake.aunis.gui.mainmenu.screens.resourcepacks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AunisGuiListExtended extends AunisGuiSlot {
    public AunisGuiListExtended(Minecraft mcIn, int width, int height, int topIn, int bottomIn, int slotHeightIn) {
        super(mcIn, width, height, topIn, bottomIn, slotHeightIn);
    }

    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY)
    {
    }

    protected boolean isSelected(int slotIndex)
    {
        return false;
    }

    protected void drawBackground()
    {
    }

    protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks)
    {
        this.getListEntry(slotIndex).drawEntry(slotIndex, xPos, yPos, this.getListWidth(), heightIn, mouseXIn, mouseYIn, this.isMouseYWithinSlotBounds(mouseYIn) && this.getSlotIndexFromScreenCoords(mouseXIn, mouseYIn) == slotIndex, partialTicks);
    }

    protected void updateItemPos(int entryID, int insideLeft, int yPos, float partialTicks)
    {
        this.getListEntry(entryID).updatePosition(entryID, insideLeft, yPos, partialTicks);
    }

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

    public abstract GuiListExtended.IGuiListEntry getListEntry(int index);
}
