package tauri.dev.jsg.gui.container.transportrings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.SlotItemHandler;
import org.lwjgl.input.Mouse;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.gui.element.tabs.*;
import tauri.dev.jsg.gui.element.tabs.Tab.SlotTab;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.SetOpenTabToServer;
import tauri.dev.jsg.packet.stargate.SaveConfigToServer;
import tauri.dev.jsg.packet.transportrings.SaveRingsParametersToServer;
import tauri.dev.jsg.power.stargate.StargateClassicEnergyStorage;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsAbstractTile;
import tauri.dev.jsg.transportrings.SymbolTypeTransportRingsEnum;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static tauri.dev.jsg.gui.container.stargate.StargateContainerGui.createConfigTab;

public class TRGui extends GuiContainer implements TabbedContainerInterface {

    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(JSG.MOD_ID, "textures/gui/container_transportrings.png");
    private final TRContainer container;
    private List<Tab> tabs;
    private TabTRAddress goauldAddressTab;
    private TabTRAddress oriAddressTab;
    private TabTRAddress ancientAddressTab;
    private TabConfig configTab;
    private TabTRSettings settingsTab;
    private int energyStored;
    private int maxEnergyStored;

    private final BlockPos pos;

    public TRGui(BlockPos pos, TRContainer container) {
        super(container);
        this.container = container;

        this.xSize = 176;
        this.ySize = 173;

        this.pos = pos;
    }

    @Override
    public void initGui() {
        super.initGui();

        tabs = new ArrayList<>();

        goauldAddressTab = (TabTRAddress) TabTRAddress.builder()
                .setTile(container.trTile)
                .setSymbolType(SymbolTypeTransportRingsEnum.GOAULD)
                .setProgressColor(0xE9A93A)
                .setGuiSize(xSize, ySize)
                .setGuiPosition(guiLeft, guiTop)
                .setTabPosition(-21, 11)
                .setOpenX(-128)
                .setHiddenX(-6)
                .setTabSize(128, 113)
                .setTabTitle(I18n.format("gui.transportrings.goauld_address"))
                .setTabSide(TabSideEnum.LEFT)
                .setTexture(BACKGROUND_TEXTURE, 512)
                .setBackgroundTextureLocation(176, 0)
                .setIconRenderPos(0, 6)
                .setIconSize(22, 22)
                .setIconTextureLocation(304, 22).build();

        oriAddressTab = (TabTRAddress) TabTRAddress.builder()
                .setTile(container.trTile)
                .setSymbolType(SymbolTypeTransportRingsEnum.ORI)
                .setProgressColor(0x90E0F9)
                .setGuiSize(xSize, ySize)
                .setGuiPosition(guiLeft, guiTop)
                .setTabPosition(-21, 11 + 22)
                .setOpenX(-128)
                .setHiddenX(-6)
                .setTabSize(128, 113)
                .setTabTitle(I18n.format("gui.transportrings.ori_address"))
                .setTabSide(TabSideEnum.LEFT)
                .setTexture(BACKGROUND_TEXTURE, 512)
                .setBackgroundTextureLocation(176, 0)
                .setIconRenderPos(0, 6)
                .setIconSize(22, 22)
                .setIconTextureLocation(304, 22 * 2).build();

        ancientAddressTab = (TabTRAddress) TabTRAddress.builder()
                .setTile(container.trTile)
                .setSymbolType(SymbolTypeTransportRingsEnum.ANCIENT)
                .setProgressColor(0x90E0F9)
                .setGuiSize(xSize, ySize)
                .setGuiPosition(guiLeft, guiTop)
                .setTabPosition(-21, 11 + 22 * 2)
                .setOpenX(-128)
                .setHiddenX(-6)
                .setTabSize(128, 113)
                .setTabTitle(I18n.format("gui.transportrings.ancient_address"))
                .setTabSide(TabSideEnum.LEFT)
                .setTexture(BACKGROUND_TEXTURE, 512)
                .setBackgroundTextureLocation(176, 0)
                .setIconRenderPos(0, 6)
                .setIconSize(22, 22)
                .setIconTextureLocation(304, 22 * 3).build();

        settingsTab = (TabTRSettings) TabTRSettings.builder()
                .setParams(container.trTile.getRingsName(), container.trTile.getRingsDistance())
                .setGuiSize(xSize, ySize)
                .setGuiPosition(guiLeft, guiTop)
                .setTabPosition(176 - 107, 2)
                .setOpenX(176)
                .setHiddenX(54)
                .setTabSize(128, 68)
                .setTabTitle(I18n.format("gui.transportrings.parameters"))
                .setTabSide(TabSideEnum.RIGHT)
                .setTexture(BACKGROUND_TEXTURE, 512)
                .setBackgroundTextureLocation(176, 113)
                .setIconRenderPos(107, 5)
                .setIconSize(22, 22)
                .setIconTextureLocation(304, 0).build();

        configTab = createConfigTab(container.trTile.getConfig(), xSize, ySize, guiLeft, guiTop);

        tabs.add(goauldAddressTab);
        tabs.add(oriAddressTab);
        tabs.add(ancientAddressTab);
        tabs.add(settingsTab);
        tabs.add(configTab);

        configTab.setOnTabClose(this::saveConfig);

        configTab.setVisible(container.isOperator);

        container.inventorySlots.set(7, goauldAddressTab.createSlot((SlotItemHandler) container.getSlot(7)));
        container.inventorySlots.set(8, oriAddressTab.createSlot((SlotItemHandler) container.getSlot(8)));
        container.inventorySlots.set(9, ancientAddressTab.createSlot((SlotItemHandler) container.getSlot(9)));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        settingsTab.setParams(container.trTile.getRingsName(), container.trTile.getRingsDistance());
        drawDefaultBackground();

        boolean hasGoauldUpgrade = false;
        boolean hasOriUpgrade = false;
        boolean hasAncientUpgrade = false;

        for (int i = 0; i < 4; i++) {
            ItemStack itemStack = container.getSlot(i).getStack();

            if (!itemStack.isEmpty()) {
                TransportRingsAbstractTile.TransportRingsUpgradeEnum upgrade = TransportRingsAbstractTile.TransportRingsUpgradeEnum.valueOf(itemStack.getItem());
                if (upgrade == null) continue;
                switch (upgrade) {
                    case GOAULD_UPGRADE:
                        hasGoauldUpgrade = true;
                        break;
                    case ORI_UPGRADE:
                        hasOriUpgrade = true;
                        break;
                    case ANCIENT_UPGRADE:
                        hasAncientUpgrade = true;
                        break;
                }
            }
        }

        goauldAddressTab.setVisible(hasGoauldUpgrade);
        oriAddressTab.setVisible(hasOriUpgrade);
        ancientAddressTab.setVisible(hasAncientUpgrade);

        Tab.updatePositions(tabs);

        StargateClassicEnergyStorage energyStorageInternal = (StargateClassicEnergyStorage) container.trTile.getCapability(CapabilityEnergy.ENERGY, null);
        energyStored = Objects.requireNonNull(energyStorageInternal).getEnergyStoredInternally();
        maxEnergyStored = energyStorageInternal.getMaxEnergyStoredInternally();

        for (int i = 4; i < 7; i++) {
            IEnergyStorage energyStorage = container.getSlot(i).getStack().getCapability(CapabilityEnergy.ENERGY, null);

            if (energyStorage == null)
                continue;

            energyStored += energyStorage.getEnergyStored();
            maxEnergyStored += energyStorage.getMaxEnergyStored();
        }

        for (int i = 7; i < container.trTile.getSlotsCount(); i++)
            ((SlotTab) container.getSlot(i)).updatePos();


        super.drawScreen(mouseX, mouseY, partialTicks);

        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        if (container.trTile.getConfig().getOptions().size() != configTab.getConfig(false).getOptions().size())
            configTab.updateConfig(container.trTile.getConfig(), true);

        for (Tab tab : tabs) {
            tab.render(fontRenderer, mouseX, mouseY);
        }

        mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        GlStateManager.color(1, 1, 1, 1);
        drawModalRectWithCustomSizedTexture(guiLeft, guiTop, 0, 0, xSize, ySize, 512, 512);

        // Draw cross on inactive capacitors
        for (int i = 0; i < 3 - container.trTile.getSupportedCapacitors(); i++) {
            drawModalRectWithCustomSizedTexture(guiLeft + 151 - 18 * i, guiTop + 27, 24, 180, 16, 16, 512, 512);
        }

        for (int i = container.trTile.getPowerTier(); i < 4; i++)
            drawModalRectWithCustomSizedTexture(guiLeft + 10 + 39 * i, guiTop + 69, 0, 173, 39, 6, 512, 512);

        int width = Math.round((energyStored / (float) JSGConfig.Stargate.power.stargateEnergyStorage * 156));
        drawGradientRect(guiLeft + 10, guiTop + 69, guiLeft + 10 + width, guiTop + 69 + 6, 0xffcc2828, 0xff731616);

        // Draw ancient title
        switch (container.trTile.getSymbolType()) {
            case GOAULD:
                drawModalRectWithCustomSizedTexture(guiLeft + 136, guiTop + 4, 330, 0, 35, 8, 512, 512);
                break;
            case ORI:
                drawModalRectWithCustomSizedTexture(guiLeft + 136, guiTop + 4, 330, 18, 35, 8, 512, 512);
                break;
            case ANCIENT:
                drawModalRectWithCustomSizedTexture(guiLeft + 136, guiTop + 4, 330, 36, 35, 8, 512, 512);
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
                        drawModalRectWithCustomSizedTexture(guiLeft + 16, guiTop + 44, 11, 239, 32 - 10, 254 - 238, 512, 512);
                        break;
                    case 1:
                        drawModalRectWithCustomSizedTexture(guiLeft + 34, guiTop + 44, 7, 237, 10 - 6, 246 - 236, 512, 512);
                        break;
                    case 2:
                        drawModalRectWithCustomSizedTexture(guiLeft + 50, guiTop + 44, 2, 237, 5 - 1, 246 - 236, 512, 512);
                        break;
                    case 3:
                        drawICSecondCable = true;
                        drawModalRectWithCustomSizedTexture(guiLeft + 50, guiTop + 44, 0, 255, 22, 270 - 254, 512, 512);
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

        // render cable from 1. IC to power line
        if (drawICFirstCable)
            drawModalRectWithCustomSizedTexture(guiLeft + 41, guiTop + 62, 0, 239, 2, 6, 512, 512);
        if (drawICSecondCable)
            drawModalRectWithCustomSizedTexture(guiLeft + 45, guiTop + 62, 0, 239, 2, 6, 512, 512);
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

        int transferred = container.trTile.getEnergyTransferedLastTick();
        TextFormatting transferredFormatting = TextFormatting.GRAY;
        String transferredSign = "";

        if (transferred > 0) {
            transferredFormatting = TextFormatting.GREEN;
            transferredSign = "+";
        } else if (transferred < 0) {
            transferredFormatting = TextFormatting.RED;
        }

        if (isPointInRegion(10, 69, 156, 6, mouseX, mouseY)) {
            List<String> power = Arrays.asList(
                    I18n.format("gui.transportrings.energyBuffer"),
                    TextFormatting.GRAY + String.format("%,d / %,d RF", energyStored, maxEnergyStored),
                    transferredFormatting + transferredSign + String.format("%,d RF/t", transferred));
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
                } else
                    container.setOpenTabId(-1);

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
        boolean typed = false;
        for (Tab tab : tabs) {
            if (tab.isOpen() && tab.isVisible()) {
                if(tab.keyTyped(typedChar, keyCode))
                    typed = true;
            }
        }
        if (!typed || keyCode == 1)
            super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        for (Tab t : tabs)
            t.updateScreen();
    }

    @Override
    public void onGuiClosed() {
        saveData();
        saveConfig();
        super.onGuiClosed();
    }

    private void saveConfig() {
        JSGPacketHandler.INSTANCE.sendToServer(new SaveConfigToServer(pos, configTab.config));
        container.trTile.setConfig(configTab.getConfig(true));
    }

    public void saveData() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        try {
            String name = settingsTab.nameTextField.getText();
            try {
                int distance = Integer.parseInt(settingsTab.distanceTextField.getText());

                if (distance >= -40 && distance <= 40) {
                    JSGPacketHandler.INSTANCE.sendToServer(new SaveRingsParametersToServer(pos, name, distance));
                    container.trTile.setRingsParams(name, distance);
                } else {
                    player.sendStatusMessage(new TextComponentTranslation("tile.jsg.transportrings_block.wrong_distance"), true);
                }
            } catch (NumberFormatException e) {
                player.sendStatusMessage(new TextComponentTranslation("tile.jsg.transportrings_block.wrong_distance"), true);
            }
        } catch (NumberFormatException e) {
            player.sendStatusMessage(new TextComponentTranslation("tile.jsg.transportrings_block.wrong_address"), true);
        }
    }
}
