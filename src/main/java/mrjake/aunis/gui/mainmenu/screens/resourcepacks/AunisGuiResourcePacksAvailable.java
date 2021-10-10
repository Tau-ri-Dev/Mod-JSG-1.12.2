package mrjake.aunis.gui.mainmenu.screens.resourcepacks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiResourcePackList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;

import java.util.List;

public class AunisGuiResourcePacksAvailable extends GuiResourcePackList {
    protected Minecraft mc;
    protected List<ResourcePackListEntry> resourcePackEntries;
    public AunisGuiResourcePacksAvailable(Minecraft mcIn, int p_i45054_2_, int p_i45054_3_, List<ResourcePackListEntry> p_i45054_4_)
    {
        super(mcIn, p_i45054_2_, p_i45054_3_, p_i45054_4_);
        mc = mcIn;
        resourcePackEntries = p_i45054_4_;
        centerListVertically = false;
        setHasListHeader(true, (int)((float)mcIn.fontRenderer.FONT_HEIGHT * 1.5F));
    }

    protected String getListHeader()
    {
        return I18n.format("resourcePack.available.title");
    }
}
