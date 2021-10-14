package mrjake.aunis.gui.mainmenu.screens.options.resourcepacks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class AunisGuiResourcePacksAvailable extends AunisGuiResourcePackList {
    public AunisGuiResourcePacksAvailable(Minecraft mcIn, int p_i45055_2_, int p_i45055_3_, List<ResourcePackListEntry> p_i45055_4_) {
        super(mcIn, p_i45055_2_, p_i45055_3_, p_i45055_4_);
    }

    @Override
    protected String getListHeader()
    {
        return I18n.format("resourcePack.available.title");
    }
}
