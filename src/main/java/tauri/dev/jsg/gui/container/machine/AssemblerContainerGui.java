package tauri.dev.jsg.gui.container.machine;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.energy.CapabilityEnergy;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.gui.element.tabs.Tab;
import tauri.dev.jsg.gui.element.tabs.TabAssemblerRecipes;
import tauri.dev.jsg.gui.element.tabs.TabSideEnum;
import tauri.dev.jsg.gui.element.tabs.TabbedContainerInterface;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.machine.AssemblerRecipes;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.SetOpenTabToServer;
import tauri.dev.jsg.stargate.power.StargateAbstractEnergyStorage;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AssemblerContainerGui extends GuiContainer implements TabbedContainerInterface {

    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(JSG.MOD_ID, "textures/gui/container_assembler.png");

    private final AssemblerContainer container;
    private final List<Tab> tabs = new ArrayList<>();
    private TabAssemblerRecipes milkyWayTab;
    private TabAssemblerRecipes pegasusTab;
    private TabAssemblerRecipes universeTab;
    private TabAssemblerRecipes goauldTab;
    private TabAssemblerRecipes oriTab;
    private TabAssemblerRecipes ancientTab;

    public AssemblerContainerGui(AssemblerContainer container) {
        super(container);

        this.container = container;
        this.xSize = 175;
        this.ySize = 230;
    }

    @Override
    public void initGui() {
        super.initGui();

        tabs.clear();

        milkyWayTab = (TabAssemblerRecipes) TabAssemblerRecipes.builder()
                .setContainer(container)
                .addButton(100, 25, 25, BACKGROUND_TEXTURE, 176, 149, AssemblerRecipes.SG_MW_BASE_BLOCK)
                .addButton(101, 25 + 18, 25, BACKGROUND_TEXTURE, 176, 165, AssemblerRecipes.SG_MW_CHEVRON_BLOCK)
                .addButton(102, 25 + 18*2, 25, BACKGROUND_TEXTURE, 176, 181, AssemblerRecipes.SG_MW_RING_BLOCK)
                .addButton(103, 25, 25 + 18, BACKGROUND_TEXTURE, 211, 149, AssemblerRecipes.SG_MW_DHD)
                .setGuiSize(xSize, ySize)
                .setGuiPosition(guiLeft, guiTop)
                .setTabPosition(-21, 2)
                .setOpenX(-128)
                .setHiddenX(-6)
                .setTabSize(128, 113)
                .setTabTitle(I18n.format("gui.assembler.milkyway"))
                .setTabSide(TabSideEnum.LEFT)
                .setTexture(BACKGROUND_TEXTURE, 512)
                .setBackgroundTextureLocation(176, 0)
                .setIconRenderPos(1, 7)
                .setIconSize(20, 18)
                .setIconTextureLocation(304, 0).build();

        pegasusTab = (TabAssemblerRecipes) TabAssemblerRecipes.builder()
                .setContainer(container)
                .addButton(100, 25, 25, BACKGROUND_TEXTURE, 176, 199, AssemblerRecipes.SG_PEG_BASE_BLOCK)
                .addButton(101, 25 + 18, 25, BACKGROUND_TEXTURE, 176, 215, AssemblerRecipes.SG_PEG_CHEVRON_BLOCK)
                .addButton(102, 25 + 18*2, 25, BACKGROUND_TEXTURE, 176, 231, AssemblerRecipes.SG_PEG_RING_BLOCK)
                .addButton(103, 25, 25 + 18, BACKGROUND_TEXTURE, 211, 199, AssemblerRecipes.SG_PEG_DHD)
                .setGuiSize(xSize, ySize)
                .setGuiPosition(guiLeft, guiTop)
                .setTabPosition(-21, 2 + 22)
                .setOpenX(-128)
                .setHiddenX(-6)
                .setTabSize(128, 113)
                .setTabTitle(I18n.format("gui.assembler.pegasus"))
                .setTabSide(TabSideEnum.LEFT)
                .setTexture(BACKGROUND_TEXTURE, 512)
                .setBackgroundTextureLocation(176, 0)
                .setIconRenderPos(1, 7)
                .setIconSize(20, 18)
                .setIconTextureLocation(304, 18).build();

        universeTab = (TabAssemblerRecipes) TabAssemblerRecipes.builder()
                .setContainer(container)
                .addButton(100, 25, 25, BACKGROUND_TEXTURE, 176, 249, AssemblerRecipes.SG_UNI_BASE_BLOCK)
                .addButton(101, 25 + 18, 25, BACKGROUND_TEXTURE, 176, 265, AssemblerRecipes.SG_UNI_CHEVRON_BLOCK)
                .addButton(102, 25 + 18*2, 25, BACKGROUND_TEXTURE, 176, 281, AssemblerRecipes.SG_UNI_RING_BLOCK)
                .setGuiSize(xSize, ySize)
                .setGuiPosition(guiLeft, guiTop)
                .setTabPosition(-21, 2 + 22 * 2)
                .setOpenX(-128)
                .setHiddenX(-6)
                .setTabSize(128, 113)
                .setTabTitle(I18n.format("gui.assembler.universe"))
                .setTabSide(TabSideEnum.LEFT)
                .setTexture(BACKGROUND_TEXTURE, 512)
                .setBackgroundTextureLocation(176, 0)
                .setIconRenderPos(1, 7)
                .setIconSize(20, 18)
                .setIconTextureLocation(304, 18 * 2).build();

        goauldTab = (TabAssemblerRecipes) TabAssemblerRecipes.builder()
                .setContainer(container)
                .setGuiSize(xSize, ySize)
                .setGuiPosition(guiLeft, guiTop)
                .setTabPosition(-21, 2 + 22 * 3)
                .setOpenX(-128)
                .setHiddenX(-6)
                .setTabSize(128, 113)
                .setTabTitle(I18n.format("gui.assembler.goauld"))
                .setTabSide(TabSideEnum.LEFT)
                .setTexture(BACKGROUND_TEXTURE, 512)
                .setBackgroundTextureLocation(176, 0)
                .setIconRenderPos(1, 7)
                .setIconSize(20, 18)
                .setIconTextureLocation(324, 0).build();

        oriTab = (TabAssemblerRecipes) TabAssemblerRecipes.builder()
                .setContainer(container)
                .setGuiSize(xSize, ySize)
                .setGuiPosition(guiLeft, guiTop)
                .setTabPosition(-21, 2 + 22 * 4)
                .setOpenX(-128)
                .setHiddenX(-6)
                .setTabSize(128, 113)
                .setTabTitle(I18n.format("gui.assembler.ori"))
                .setTabSide(TabSideEnum.LEFT)
                .setTexture(BACKGROUND_TEXTURE, 512)
                .setBackgroundTextureLocation(176, 0)
                .setIconRenderPos(1, 7)
                .setIconSize(20, 18)
                .setIconTextureLocation(324, 18).build();

        ancientTab = (TabAssemblerRecipes) TabAssemblerRecipes.builder()
                .setContainer(container)
                .setGuiSize(xSize, ySize)
                .setGuiPosition(guiLeft, guiTop)
                .setTabPosition(-21, 2 + 22 * 5)
                .setOpenX(-128)
                .setHiddenX(-6)
                .setTabSize(128, 113)
                .setTabTitle(I18n.format("gui.assembler.ancient"))
                .setTabSide(TabSideEnum.LEFT)
                .setTexture(BACKGROUND_TEXTURE, 512)
                .setBackgroundTextureLocation(176, 0)
                .setIconRenderPos(1, 7)
                .setIconSize(20, 18)
                .setIconTextureLocation(324, 18 * 2).build();

        tabs.add(milkyWayTab);
        tabs.add(pegasusTab);
        tabs.add(universeTab);
        tabs.add(goauldTab);
        tabs.add(oriTab);
        tabs.add(ancientTab);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        boolean hasMilkyWayUpgrade = false;
        boolean hasPegasusUpgrade = false;
        boolean hasUniverseUpgrade = false;
        boolean hasGoauldUpgrade = false;
        boolean hasOriUpgrade = false;
        boolean hasAncientUpgrade = false;

        Item item = container.getSlot(0).getStack().getItem();
        if (item == JSGItems.SCHEMATIC_MILKYWAY) hasMilkyWayUpgrade = true;
        if (item == JSGItems.SCHEMATIC_PEGASUS) hasPegasusUpgrade = true;
        if (item == JSGItems.SCHEMATIC_UNIVERSE) hasUniverseUpgrade = true;
        if (item == JSGItems.SCHEMATIC_TR_GOAULD) hasGoauldUpgrade = true;
        if (item == JSGItems.SCHEMATIC_TR_ORI) hasOriUpgrade = true;
        if (item == JSGItems.SCHEMATIC_TR_ANCIENT) hasAncientUpgrade = true;

        milkyWayTab.setVisible(hasMilkyWayUpgrade);
        pegasusTab.setVisible(hasPegasusUpgrade);
        universeTab.setVisible(hasUniverseUpgrade);
        goauldTab.setVisible(hasGoauldUpgrade);
        oriTab.setVisible(hasOriUpgrade);
        ancientTab.setVisible(hasAncientUpgrade);

        Tab.updatePositions(tabs);

        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        for (Tab tab : tabs) {
            tab.render(fontRenderer, mouseX, mouseY);
        }

        mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawModalRectWithCustomSizedTexture(guiLeft, guiTop, 0, 0, xSize, ySize, 512, 512);

        long start = container.tile.getMachineStart();
        long end = container.tile.getMachineEnd();
        double progress = (start == -1 || end == -1 || start == end) ? 0 : ((double) (container.tile.getWorld().getTotalWorldTime() - start)) / ((double) (end - start));
        drawModalRectWithCustomSizedTexture(guiLeft + 95, guiTop + 65, 176, 128, ((int) ((216 - 176) * progress)), 142 - 128, 512, 512);

        StargateAbstractEnergyStorage energyStorage = (StargateAbstractEnergyStorage) container.tile.getCapability(CapabilityEnergy.ENERGY, null);

        int width = Math.round((energyStorage.getEnergyStored() / ((float) energyStorage.getMaxEnergyStored()) * 156));
        drawGradientRect(guiLeft + 10, guiTop + 124, guiLeft + 10 + width, guiTop + 124 + 6, 0xffcc2828, 0xff731616);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(I18n.format("gui.assembler.name"), 7, 6, 4210752);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);

        StargateAbstractEnergyStorage energyStorage = (StargateAbstractEnergyStorage) container.tile.getCapability(CapabilityEnergy.ENERGY, null);

        int energyStored = energyStorage.getEnergyStored();
        int maxEnergyStored = energyStorage.getMaxEnergyStored();

        String energyPercent = String.format("%.2f", energyStored / (float) maxEnergyStored * 100) + " %";
        fontRenderer.drawString(energyPercent, 170 - fontRenderer.getStringWidth(energyPercent), 135, 4210752);

        int transferred = container.tile.getEnergyTransferedLastTick();
        TextFormatting transferredFormatting = TextFormatting.GRAY;
        String transferredSign = "";

        if (transferred > 0) {
            transferredFormatting = TextFormatting.GREEN;
            transferredSign = "+";
        } else if (transferred < 0) {
            transferredFormatting = TextFormatting.RED;
        }
        if (isPointInRegion(10, 124, 156, 6, mouseX, mouseY)) {
            List<String> power = Arrays.asList(
                    I18n.format("gui.stargate.energyBuffer"),
                    TextFormatting.GRAY + String.format("%,d / %,d RF", energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored()),
                    transferredFormatting + transferredSign + String.format("%,d RF/t", transferred));
            drawHoveringText(power, mouseX - guiLeft, mouseY - guiTop);
        }

        for (Tab tab : tabs) {
            tab.renderFg(this, fontRenderer, mouseX, mouseY);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (int i = 0; i < tabs.size(); i++) {
            Tab tab = tabs.get(i);

            if (tab.isCursorOnTab(mouseX, mouseY)) {
                if (Tab.tabsInteract(tabs, i)) {
                    container.setOpenTabId(i);
                } else {
                    container.setOpenTabId(-1);
                }

                JSGPacketHandler.INSTANCE.sendToServer(new SetOpenTabToServer(container.getOpenTabId()));

                break;
            }

        }
        for (Tab tab : tabs) {
            if (tab.isOpen() && tab.isVisible()) {
                tab.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }


    @Override
    public List<Rectangle> getGuiExtraAreas() {
        return tabs.stream()
                .map(Tab::getArea)
                .collect(Collectors.toList());
    }
}
