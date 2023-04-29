package tauri.dev.jsg.gui.container.stargate;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.SlotItemHandler;
import org.lwjgl.input.Mouse;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.ingame.JSGTileEntityConfig;
import tauri.dev.jsg.gui.element.tabs.*;
import tauri.dev.jsg.gui.element.tabs.Tab.SlotTab;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.SetOpenTabToServer;
import tauri.dev.jsg.packet.stargate.SaveConfigToServer;
import tauri.dev.jsg.packet.stargate.SaveIrisCodeToServer;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.stargate.network.SymbolMilkyWayEnum;
import tauri.dev.jsg.stargate.network.SymbolPegasusEnum;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.stargate.network.SymbolUniverseEnum;
import tauri.dev.jsg.power.stargate.StargateClassicEnergyStorage;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile.StargateUpgradeEnum;
import tauri.dev.jsg.util.math.TemperatureHelper;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class StargateContainerGui extends GuiContainer implements TabbedContainerInterface {

    public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(JSG.MOD_ID, "textures/gui/container_stargate.png");

    public static TabConfig createConfigTab(JSGTileEntityConfig config, int guiXSize, int guiYSize, int guiLeft, int guiTop) {
        return (TabConfig) TabConfig.builder()
                .setConfig(config)
                .setGuiSize(guiXSize, guiYSize)
                .setGuiPosition(guiLeft, guiTop)
                .setTabPosition(-21, 11 + 22 * 3)
                .setOpenX(-128)
                .setHiddenX(-6)
                .setTabSize(128, 96)
                .setTabTitle(I18n.format("gui.configuration"))
                .setTabSide(TabSideEnum.LEFT)
                .setTexture(BACKGROUND_TEXTURE, 512)
                .setBackgroundTextureLocation(176, 165)
                .setIconRenderPos(0, 6)
                .setIconSize(22, 22)
                .setIconTextureLocation(326, 66).build();
    }

    public static TabBiomeOverlay createOverlayTab(EnumSet<BiomeOverlayEnum> supportedOverlay, int guiXSize, int guiYSize, int guiLeft, int guiTop) {
        return (TabBiomeOverlay) TabBiomeOverlay.builder()
                .setSupportedOverlays(supportedOverlay)
                .setSlotTexture(6, 179)
                .setGuiSize(guiXSize, guiYSize)
                .setGuiPosition(guiLeft, guiTop)
                .setTabPosition(176 - 107, 2)
                .setOpenX(176)
                .setHiddenX(54)
                .setTabSize(128, 51)
                .setTabTitle(I18n.format("gui.stargate.biome_overlay"))
                .setTabSide(TabSideEnum.RIGHT)
                .setTexture(BACKGROUND_TEXTURE, 512)
                .setBackgroundTextureLocation(176 + 24, 113)
                .setIconRenderPos(107, 6)
                .setIconSize(22, 22)
                .setIconTextureLocation(304, 22 * 3).build();
    }

    private final StargateContainer container;
    private final List<Tab> tabs = new ArrayList<>();

    private TabAddress milkyWayAddressTab;
    private TabAddress pegasusAddressTab;
    private TabAddress universeAddressTab;
    private TabIris irisTab;
    private TabConfig configTab;
    private TabInfo infoTab;

    private int energyStored;
    private int maxEnergyStored;

    private final BlockPos pos;

    public StargateContainerGui(BlockPos pos, StargateContainer container) {
        super(container);
        this.container = container;

        this.xSize = 176;
        this.ySize = 173;

        this.pos = pos;
    }

    @Override
    public void initGui() {
        super.initGui();

        tabs.clear();

        milkyWayAddressTab = (TabAddress) TabAddress.builder()
                .setGateTile(container.gateTile)
                .setSymbolType(SymbolTypeEnum.MILKYWAY)
                .setProgressColor(0x98BCF9)
                .setGuiSize(xSize, ySize)
                .setGuiPosition(guiLeft, guiTop)
                .setTabPosition(-21, 11)
                .setOpenX(-128)
                .setHiddenX(-6)
                .setTabSize(128, 113)
                .setTabTitle(I18n.format("gui.stargate.milky_way_address"))
                .setTabSide(TabSideEnum.LEFT)
                .setTexture(BACKGROUND_TEXTURE, 512)
                .setBackgroundTextureLocation(176, 0)
                .setIconRenderPos(0, 6)
                .setIconSize(22, 22)
                .setIconTextureLocation(304, 0).build();

        pegasusAddressTab = (TabAddress) TabAddress.builder()
                .setGateTile(container.gateTile)
                .setSymbolType(SymbolTypeEnum.PEGASUS)
                .setProgressColor(0x5BCAEC)
                .setGuiSize(xSize, ySize)
                .setGuiPosition(guiLeft, guiTop)
                .setTabPosition(-21, 11 + 22)
                .setOpenX(-128)
                .setHiddenX(-6)
                .setTabSize(128, 113)
                .setTabTitle(I18n.format("gui.stargate.pegasus_address"))
                .setTabSide(TabSideEnum.LEFT)
                .setTexture(BACKGROUND_TEXTURE, 512)
                .setBackgroundTextureLocation(176, 0)
                .setIconRenderPos(0, 6)
                .setIconSize(22, 22)
                .setIconTextureLocation(304, 22).build();

        universeAddressTab = (TabAddress) TabAddress.builder()
                .setGateTile(container.gateTile)
                .setSymbolType(SymbolTypeEnum.UNIVERSE)
                .setProgressColor(0x707070)
                .setGuiSize(xSize, ySize)
                .setGuiPosition(guiLeft, guiTop)
                .setTabPosition(-21, 11 + 22 * 2)
                .setOpenX(-128)
                .setHiddenX(-6)
                .setTabSize(128, 113)
                .setTabTitle(I18n.format("gui.stargate.universe_address"))
                .setTabSide(TabSideEnum.LEFT)
                .setTexture(BACKGROUND_TEXTURE, 512)
                .setBackgroundTextureLocation(176, 0)
                .setIconRenderPos(0, 6)
                .setIconSize(22, 22)
                .setIconTextureLocation(304, 22 * 2).build();

        configTab = createConfigTab(container.gateTile.getConfig(), xSize, ySize, guiLeft, guiTop);

        TabBiomeOverlay overlayTab = createOverlayTab(container.gateTile.getSupportedOverlays(), xSize, ySize, guiLeft, guiTop);

        irisTab = (TabIris) TabIris.builder()
                .setCode(container.gateTile.getIrisCode())
                .setIsUniverse(container.gateTile.getSymbolType() == SymbolTypeEnum.UNIVERSE)
                .setIrisMode(container.gateTile.getIrisMode())
                .setGuiSize(xSize, ySize)
                .setGuiPosition(guiLeft, guiTop)
                .setTabPosition(176 - 107, 2 + 22)
                .setOpenX(176)
                .setHiddenX(54)
                .setTabSize(128, 51)
                .setTabTitle(I18n.format("gui.stargate.iris_code"))
                .setTabSide(TabSideEnum.RIGHT)
                .setTexture(BACKGROUND_TEXTURE, 512)
                .setBackgroundTextureLocation(176 + 24, 113)
                .setIconRenderPos(107, 6)
                .setIconSize(22, 22)
                .setIconTextureLocation(304, 22 * 4).build();

        infoTab = (TabInfo) TabInfo.builder()
                .setGuiSize(xSize, ySize)
                .setGuiPosition(guiLeft, guiTop)
                .setTabPosition(176 - 131, 2 + 22 * 2)
                .setOpenX(176)
                .setHiddenX(30)
                .setTabSize(152, 51)
                .setTabTitle(I18n.format("gui.stargate.info"))
                .setTabSide(TabSideEnum.RIGHT)
                .setTexture(BACKGROUND_TEXTURE, 512)
                .setBackgroundTextureLocation(176, 113)
                .setIconRenderPos(131, 6)
                .setIconSize(22, 22)
                .setIconTextureLocation(326, 88).build();

        irisTab.setOnTabClose(this::saveIrisCode);
        configTab.setOnTabClose(this::saveConfig);

        tabs.add(milkyWayAddressTab);
        tabs.add(pegasusAddressTab);
        tabs.add(universeAddressTab);
        tabs.add(configTab);

        tabs.add(overlayTab);
        tabs.add(irisTab);

        tabs.add(infoTab);

        container.inventorySlots.set(7, milkyWayAddressTab.createSlot((SlotItemHandler) container.getSlot(7)));
        container.inventorySlots.set(8, pegasusAddressTab.createSlot((SlotItemHandler) container.getSlot(8)));
        container.inventorySlots.set(9, universeAddressTab.createSlot((SlotItemHandler) container.getSlot(9)));
        container.inventorySlots.set(10, overlayTab.createAndSaveSlot((SlotItemHandler) container.getSlot(10)));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        if (infoTab != null) {
            infoTab.clearStrings();
            int y = 22;

            // opened time
            long openedSince = container.gateTile.openedSince;
            if (openedSince > 0) {
                long openedSeconds = container.gateTile.getOpenedSeconds();
                String format = TextFormatting.DARK_GREEN.toString();
                int maxTime = container.gateTile.getConfig().getOption(StargateClassicBaseTile.ConfigOptions.TIME_LIMIT_TIME.id).getIntValue();
                if (openedSeconds >= (maxTime * 0.75))
                    format = TextFormatting.YELLOW.toString();
                if (openedSeconds >= maxTime)
                    format = TextFormatting.RED.toString();
                String openedTime = I18n.format("gui.stargate.state.opened") + " " + format + container.gateTile.getOpenedSecondsToDisplayAsMinutes();
                infoTab.addString(new TabInfo.InfoString(openedTime, 4, y));
                y += 9;
            }
            // gate temp
            double gateTemperature = container.gateTile.gateHeat;
            // iris temp
            double irisTemperature = container.gateTile.irisHeat;

            String format = TextFormatting.DARK_GREEN.toString();
            if (gateTemperature >= (StargateClassicBaseTile.GATE_MAX_HEAT * 0.5))
                format = TextFormatting.YELLOW.toString();
            if (gateTemperature >= (StargateClassicBaseTile.GATE_MAX_HEAT * 0.75))
                format = TextFormatting.RED.toString();

            infoTab.addString(new TabInfo.InfoString(I18n.format("gui.stargate.state.gate_temp") + " " + format + JSGConfig.General.visual.temperatureUnit.getTemperatureToDisplay(TemperatureHelper.asKelvins(TemperatureHelper.asCelsius(gateTemperature).toKelvins()), 0), 4, y));
            y += 9;
            if (container.gateTile.hasPhysicalIris()) {
                //irisTemperature = gateTemperature;

                double maxHeat = container.gateTile.getMaxIrisHeat();

                format = TextFormatting.DARK_GREEN.toString();
                if (irisTemperature > (maxHeat * 0.5))
                    format = TextFormatting.YELLOW.toString();
                if (irisTemperature > (maxHeat * 0.75))
                    format = TextFormatting.RED.toString();

                infoTab.addString(new TabInfo.InfoString(I18n.format("gui.stargate.state.iris_temp") + " " + format + JSGConfig.General.visual.temperatureUnit.getTemperatureToDisplay(TemperatureHelper.asKelvins(TemperatureHelper.asCelsius(irisTemperature).toKelvins()), 0), 4, y));
            }
        }

        drawDefaultBackground();

        boolean hasAddressUpgrade = false;
        boolean hasMilkyWayUpgrade = false;
        boolean hasAtlantisUpgrade = false;
        boolean hasUniverseUpgrade = false;
        boolean hasIrisUpgrade = !container.getSlot(11).getStack().isEmpty();

        for (int i = 0; i < 4; i++) {
            ItemStack itemStack = container.getSlot(i).getStack();

            if (!itemStack.isEmpty()) {
                StargateUpgradeEnum upgrade = StargateUpgradeEnum.valueOf(itemStack.getItem());
                if (upgrade == null) continue;
                switch (upgrade) {
                    case CHEVRON_UPGRADE:
                        hasAddressUpgrade = true;
                        break;

                    case MILKYWAY_GLYPHS:
                        hasMilkyWayUpgrade = true;
                        break;

                    case PEGASUS_GLYPHS:
                        hasAtlantisUpgrade = true;
                        break;

                    case UNIVERSE_GLYPHS:
                        hasUniverseUpgrade = true;
                        break;
                }
            }
        }

        milkyWayAddressTab.setMaxSymbols(SymbolMilkyWayEnum.getMaxSymbolsDisplay(hasAddressUpgrade));
        pegasusAddressTab.setMaxSymbols(SymbolPegasusEnum.getMaxSymbolsDisplay(hasAddressUpgrade));
        universeAddressTab.setMaxSymbols(SymbolUniverseEnum.getMaxSymbolsDisplay(hasAddressUpgrade));

        milkyWayAddressTab.setVisible(hasMilkyWayUpgrade);
        pegasusAddressTab.setVisible(hasAtlantisUpgrade);
        universeAddressTab.setVisible(hasUniverseUpgrade);
        irisTab.setVisible(hasIrisUpgrade);
        configTab.setVisible(container.isOperator);

        Tab.updatePositions(tabs);

        StargateClassicEnergyStorage energyStorageInternal = (StargateClassicEnergyStorage) container.gateTile.getCapability(CapabilityEnergy.ENERGY, null);
        if (energyStorageInternal != null) {
            energyStored = energyStorageInternal.getEnergyStoredInternally();
            maxEnergyStored = energyStorageInternal.getMaxEnergyStoredInternally();
        }

        for (int i = 4; i < 7; i++) {
            IEnergyStorage energyStorage = container.getSlot(i).getStack().getCapability(CapabilityEnergy.ENERGY, null);

            if (energyStorage == null)
                continue;

            energyStored += energyStorage.getEnergyStored();
            maxEnergyStored += energyStorage.getMaxEnergyStored();
        }

        for (int i = 7; i < 11; i++)
            ((SlotTab) container.getSlot(i)).updatePos();


        super.drawScreen(mouseX, mouseY, partialTicks);

        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        if (irisTab.isVisible() && !irisTab.isOpen()) {
            if (irisTab.getIrisMode() != container.gateTile.getIrisMode())
                irisTab.updateValue(container.gateTile.getIrisMode());
            if (irisTab.getCode() != container.gateTile.getIrisCode())
                irisTab.updateValue(container.gateTile.getIrisCode());
        }
        if (container.gateTile.getConfig().getOptions().size() != configTab.getConfig(false).getOptions().size())
            configTab.updateConfig(container.gateTile.getConfig(), true);
        for (Tab tab : tabs) {
            tab.render(fontRenderer, mouseX, mouseY);
        }

        mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        GlStateManager.color(1, 1, 1, 1);
        drawModalRectWithCustomSizedTexture(guiLeft, guiTop, 0, 0, xSize, ySize, 512, 512);

        // Draw cross on inactive capacitors
        for (int i = 0; i < 3 - container.gateTile.getSupportedCapacitors(); i++) {
            drawModalRectWithCustomSizedTexture(guiLeft + 151 - 18 * i, guiTop + 27, 24, 180, 16, 16, 512, 512);
        }

        for (int i = container.gateTile.getPowerTier(); i < 4; i++)
            drawModalRectWithCustomSizedTexture(guiLeft + 10 + 39 * i, guiTop + 69, 0, 173, 39, 6, 512, 512);

        int width = Math.round((energyStored / (float) JSGConfig.Stargate.power.stargateEnergyStorage * 156));
        drawGradientRect(guiLeft + 10, guiTop + 69, guiLeft + 10 + width, guiTop + 69 + 6, 0xffcc2828, 0xff731616);

        // Draw ancient title
        switch (container.gateTile.getSymbolType()) {
            case MILKYWAY:
                drawModalRectWithCustomSizedTexture(guiLeft + 137, guiTop + 4, 330, 0, 35, 8, 512, 512);
                break;
            case PEGASUS:
                drawModalRectWithCustomSizedTexture(guiLeft + 137, guiTop + 4, 330, 18, 35, 8, 512, 512);
                break;
            case UNIVERSE:
                drawModalRectWithCustomSizedTexture(guiLeft + 137, guiTop + 4, 330, 36, 35, 8, 512, 512);
                break;
            default:
                break;
        }

        boolean drawICFirstCable = false;
        boolean drawICSecondCable = false;

        // Draw cables
        for (int i = 0; i < 7; i++) {
            if (container.getSlot(i).getHasStack()) {
                if (i < 4) drawICFirstCable = true;
                // render activated wires/cables
                switch (i) {
                    // upgrades
                    case 0:
                        drawModalRectWithCustomSizedTexture(guiLeft + 16, guiTop + 44, 18, 239, 48 - 17, 253 - 238, 512, 512);
                        break;
                    case 1:
                        drawModalRectWithCustomSizedTexture(guiLeft + 34, guiTop + 44, 3, 239, 15 - 2, 249 - 238, 512, 512);
                        break;
                    case 2:
                        drawModalRectWithCustomSizedTexture(guiLeft + 52, guiTop + 44, 0, 239, 2, 6, 512, 512);
                        break;
                    case 3:
                        drawModalRectWithCustomSizedTexture(guiLeft + 59, guiTop + 44, 33, 254, 45 - 32, 264 - 253, 512, 512);
                        break;

                    // capacitors
                    case 4:
                        drawModalRectWithCustomSizedTexture(guiLeft + 121, guiTop + 44, 0, 225, 14, 236 - 224, 512, 512);
                        break;
                    case 5:
                        drawModalRectWithCustomSizedTexture(guiLeft + 139, guiTop + 44, 14, 225, 4, 230 - 224, 512, 512);
                        break;
                    case 6:
                        drawModalRectWithCustomSizedTexture(guiLeft + 147, guiTop + 44, 18, 225, 31 - 17, 238 - 224, 512, 512);
                        break;
                    default:
                        break;
                }
            }
        }
        if (container.getSlot(11).getHasStack()) {
            drawICSecondCable = true;
            ItemStack stack = container.getSlot(11).getStack();
            if (stack.getItem() == JSGItems.UPGRADE_SHIELD) {
                // render power cable for shield
                drawModalRectWithCustomSizedTexture(guiLeft + 98, guiTop + 33, 0, 197, 37, 224 - 196, 512, 512);
            }
            // render activated wire for iris upgrade slot
            drawModalRectWithCustomSizedTexture(guiLeft + 59, guiTop + 44, 0, 254, 31, 268 - 253, 512, 512);
        }

        // render cables from 1. IC to power line
        if (drawICFirstCable)
            drawModalRectWithCustomSizedTexture(guiLeft + 50, guiTop + 62, 0, 239, 2, 6, 512, 512);
        if (drawICSecondCable)
            drawModalRectWithCustomSizedTexture(guiLeft + 54, guiTop + 62, 0, 239, 2, 6, 512, 512);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String caps = I18n.format("gui.stargate.capacitors");
        fontRenderer.drawString(caps, this.xSize - 8 - fontRenderer.getStringWidth(caps), 16, 4210752);

        String energyPercent = String.format("%.2f", energyStored / (float) maxEnergyStored * 100) + " %";
        fontRenderer.drawString(energyPercent, this.xSize - 8 - fontRenderer.getStringWidth(energyPercent), 79, 4210752);

        fontRenderer.drawString(I18n.format("gui.upgrades"), 8, 16, 4210752);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);

        for (Tab tab : tabs) {
            tab.renderFg(this, fontRenderer, mouseX, mouseY);
        }

        int transferred = container.gateTile.getEnergyTransferedLastTick();
        TextFormatting transferredFormatting = TextFormatting.GRAY;
        String transferredSign = "";

        if (transferred > 0) {
            transferredFormatting = TextFormatting.GREEN;
            transferredSign = "+";
        } else if (transferred < 0) {
            transferredFormatting = TextFormatting.RED;
        }

        float toClose = container.gateTile.getEnergySecondsToClose();
        TextFormatting toCloseFormatting = TextFormatting.GRAY;

        if (toClose > 0) {
            if (toClose < JSGConfig.Stargate.power.instabilitySeconds)
                toCloseFormatting = TextFormatting.DARK_RED;
            else
                toCloseFormatting = TextFormatting.GREEN;
        }

        if (isPointInRegion(10, 69, 156, 6, mouseX, mouseY)) {
            List<String> power = Arrays.asList(
                    I18n.format("gui.stargate.energyBuffer"),
                    TextFormatting.GRAY + String.format("%,d / %,d RF", energyStored, maxEnergyStored),
                    transferredFormatting + transferredSign + String.format("%,d RF/t", transferred),
                    toCloseFormatting + String.format("%.2f s", toClose));
            drawHoveringText(power, mouseX - guiLeft, mouseY - guiTop);
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
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();

        if (wheel != 0) {
            for (Tab tab : tabs) {
                if (tab instanceof TabScrollAble && tab.isVisible() && tab.isOpen()) {
                    ((TabScrollAble) tab).scroll(wheel);
                }
            }
        }
    }

    @Override
    public List<Rectangle> getGuiExtraAreas() {
        return tabs.stream()
                .map(Tab::getArea)
                .collect(Collectors.toList());
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1 || mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))
            super.keyTyped(typedChar, keyCode);
        for (Tab tab : tabs) {
            if (tab.isOpen() && tab.isVisible()) {
                tab.keyTyped(typedChar, keyCode);
            }
        }
        if (keyCode == 1) // pressed "e"
            super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        saveConfig();
        saveIrisCode();
        super.onGuiClosed();
    }

    private void saveConfig() {
        JSGPacketHandler.INSTANCE.sendToServer(new SaveConfigToServer(pos, configTab.config));
        container.gateTile.setConfig(configTab.getConfig(true));
    }

    private void saveIrisCode() {
        JSGPacketHandler.INSTANCE.sendToServer(new SaveIrisCodeToServer(pos, irisTab.getCode(), irisTab.getIrisMode()));
        container.gateTile.setIrisCode(irisTab.getCode());
        container.gateTile.setIrisMode(irisTab.getIrisMode());
    }
}
