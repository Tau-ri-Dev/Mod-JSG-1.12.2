package mrjake.aunis.gui.mainmenu.screens.serverlist;

import com.google.common.collect.Lists;
import java.util.List;

import mrjake.aunis.gui.mainmenu.screens.AunisMultiPlayerGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.ServerListEntryLanDetected;
import net.minecraft.client.gui.ServerListEntryLanScan;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AunisServerSelectionList extends GuiListExtended
{
    private final AunisMultiPlayerGui owner;
    private final List<AunisServerListEntryNormal> serverListInternet = Lists.newArrayList();
    private final List<AunisServerListEntryLanDetected> serverListLan = Lists.newArrayList();
    private final GuiListExtended.IGuiListEntry lanScanEntry = new ServerListEntryLanScan();
    private int selectedSlotIndex = -1;

    public AunisServerSelectionList(AunisMultiPlayerGui ownerIn, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn)
    {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.owner = ownerIn;
    }

    public GuiListExtended.IGuiListEntry getListEntry(int index)
    {
        if (index < this.serverListInternet.size())
        {
            return this.serverListInternet.get(index);
        }
        else
        {
            index = index - this.serverListInternet.size();

            if (index == 0)
            {
                return this.lanScanEntry;
            }
            else
            {
                --index;
                return this.serverListLan.get(index);
            }
        }
    }

    public int getSize()
    {
        return this.serverListInternet.size() + 1 + this.serverListLan.size();
    }

    public void setSelectedSlotIndex(int selectedSlotIndexIn)
    {
        this.selectedSlotIndex = selectedSlotIndexIn;
    }

    protected boolean isSelected(int slotIndex)
    {
        return slotIndex == this.selectedSlotIndex;
    }

    public int getSelected()
    {
        return this.selectedSlotIndex;
    }

    public void updateOnlineServers(ServerList p_148195_1_)
    {
        this.serverListInternet.clear();

        for (int i = 0; i < p_148195_1_.countServers(); ++i)
        {
            this.serverListInternet.add(new AunisServerListEntryNormal(this.owner, p_148195_1_.getServerData(i)));
        }
    }

    public void updateNetworkServers(List<LanServerInfo> p_148194_1_)
    {
        this.serverListLan.clear();

        for (LanServerInfo lanserverinfo : p_148194_1_)
        {
            this.serverListLan.add(new AunisServerListEntryLanDetected(this.owner, lanserverinfo));
        }
    }

    protected int getScrollBarX()
    {
        return super.getScrollBarX() + 30;
    }

    public int getListWidth()
    {
        return super.getListWidth() + 85;
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
}
