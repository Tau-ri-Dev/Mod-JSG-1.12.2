package mrjake.aunis.gui.mainmenu.screens.options.resourcepacks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class AunisGuiResourcePackList extends AunisGuiListExtended {
    protected final Minecraft mc;
    protected final List<ResourcePackListEntry> resourcePackEntries;

    public AunisGuiResourcePackList(Minecraft mcIn, int p_i45055_2_, int p_i45055_3_, List<ResourcePackListEntry> p_i45055_4_)
    {
        super(mcIn, p_i45055_2_, p_i45055_3_, 32, p_i45055_3_ - 55 + 4, 36);
        this.mc = mcIn;
        this.resourcePackEntries = p_i45055_4_;
        this.centerListVertically = false;
        this.setHasListHeader(true, (int)((float)mcIn.fontRenderer.FONT_HEIGHT * 1.5F));
    }

    public void drawListHeader(int insideLeft, int insideTop, Tessellator tessellatorIn)
    {
        String s = TextFormatting.UNDERLINE + "" + TextFormatting.BOLD + this.getListHeader();
        this.mc.fontRenderer.drawString(s, insideLeft + this.width / 2 - this.mc.fontRenderer.getStringWidth(s) / 2, Math.min(this.top + 5, insideTop), 16777215);
    }

    protected abstract String getListHeader();

    public List<ResourcePackListEntry> getList()
    {
        return this.resourcePackEntries;
    }

    protected int getSize()
    {
        return this.getList().size();
    }

    public ResourcePackListEntry getListEntry(int index)
    {
        return (ResourcePackListEntry)this.getList().get(index);
    }

    public int getListWidth()
    {
        return this.width;
    }

    protected int getScrollBarX()
    {
        return this.right - 6;
    }
}
