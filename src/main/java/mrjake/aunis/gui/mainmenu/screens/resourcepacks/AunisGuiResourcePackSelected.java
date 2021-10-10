package mrjake.aunis.gui.mainmenu.screens.resourcepacks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiResourcePackList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;

import java.util.List;

public class AunisGuiResourcePackSelected extends GuiResourcePackList{
    protected final Minecraft mc;
    protected List<ResourcePackListEntry> resourcePackEntries;
    public AunisGuiResourcePackSelected(Minecraft mcIn, int p_i45056_2_, int p_i45056_3_, List<ResourcePackListEntry> p_i45056_4_)
    {
        super(mcIn, p_i45056_2_, p_i45056_3_, p_i45056_4_);
        this.mc = mcIn;
        this.resourcePackEntries = p_i45056_4_;
        this.centerListVertically = false;
        this.setHasListHeader(true, (int)((float)mcIn.fontRenderer.FONT_HEIGHT * 1.5F));
    }

    protected String getListHeader()
    {
        return I18n.format("resourcePack.selected.title");
    }
}
