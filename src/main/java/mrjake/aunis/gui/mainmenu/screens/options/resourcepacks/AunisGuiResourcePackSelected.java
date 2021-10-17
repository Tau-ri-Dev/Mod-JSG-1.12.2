package mrjake.aunis.gui.mainmenu.screens.options.resourcepacks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class AunisGuiResourcePackSelected extends AunisGuiResourcePackList{
    public AunisGuiResourcePackSelected(Minecraft mcIn, int p_i45056_2_, int p_i45056_3_, List<ResourcePackListEntry> p_i45056_4_)
    {
        super(mcIn, p_i45056_2_, p_i45056_3_, p_i45056_4_);
    }

    protected String getListHeader()
    {
        return I18n.format("resourcePack.selected.title");
    }
}
