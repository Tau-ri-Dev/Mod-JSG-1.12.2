package tauri.dev.jsg.gui.container.dhd;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.SlotItemHandler;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.fluid.JSGFluids;
import tauri.dev.jsg.gui.element.Diode;
import tauri.dev.jsg.gui.element.Diode.DiodeStatus;
import tauri.dev.jsg.gui.element.FluidTankElement;
import tauri.dev.jsg.gui.element.GuiHelper;
import tauri.dev.jsg.gui.element.tabs.Tab;
import tauri.dev.jsg.gui.element.tabs.Tab.SlotTab;
import tauri.dev.jsg.gui.element.tabs.TabBiomeOverlay;
import tauri.dev.jsg.gui.element.tabs.TabbedContainerInterface;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.SetOpenTabToServer;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDAbstractTile;
import tauri.dev.jsg.tileentity.util.ReactorStateEnum;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static tauri.dev.jsg.gui.container.stargate.StargateContainerGui.createOverlayTab;

public abstract class DHDAbstractContainerGui extends GuiContainer implements TabbedContainerInterface {
    private final DHDAbstractContainer container;
    private final FluidTankElement tank;

    private final List<Diode> diodes = new ArrayList<>(3);

    private final List<Tab> tabs = new ArrayList<>();

    public static final ResourceLocation BACKGROUND = new ResourceLocation(JSG.MOD_ID, "textures/gui/container_dhd.png");

    public DHDAbstractContainerGui(DHDAbstractContainer container) {
        super(container);

        this.xSize = 176;
        this.ySize = 172;

        this.container = container;
        container.tankNaquadah.setFluid(new FluidStack(JSGFluids.NAQUADAH_MOLTEN_REFINED, 0));
        this.tank = new FluidTankElement(this, 152, 23, 16, 54, container.tankNaquadah);

        diodes.add(new Diode(this, 8, 18, I18n.format("gui.dhd.crystalStatus")).setDiodeStatus(DiodeStatus.OFF)
                .putStatus(DiodeStatus.OFF, I18n.format("gui.dhd.no_crystal"))
                .putStatus(DiodeStatus.ON, I18n.format("gui.dhd.crystal_ok"))
                .setStatusMapper(() -> container.slotCrystal.getHasStack() ? DiodeStatus.ON : DiodeStatus.OFF));

        diodes.add(new Diode(this, 17, 18, I18n.format("gui.dhd.linkStatus")).setDiodeStatus(DiodeStatus.OFF)
                .putStatus(DiodeStatus.OFF, I18n.format("gui.dhd.not_linked"))
                .putStatus(DiodeStatus.ON, I18n.format("gui.dhd.linked"))
                .setStatusMapper(() -> container.dhdTile.isLinkedClient ? DiodeStatus.ON : DiodeStatus.OFF));

        diodes.add(new Diode(this, 26, 18, I18n.format("gui.dhd.reactorStatus"))
                .putStatus(DiodeStatus.OFF, I18n.format("gui.dhd.no_fuel"))
                .putStatus(DiodeStatus.WARN, I18n.format("gui.dhd.standby"))
                .putStatus(DiodeStatus.ON, I18n.format("gui.dhd.running"))
                .setStatusMapper(() -> {
                    switch (container.dhdTile.getReactorState()) {
                        case ONLINE:
                            return DiodeStatus.ON;

                        case STANDBY:
                            return DiodeStatus.WARN;

                        default:
                            return DiodeStatus.OFF;
                    }
                })
                .setStatusStringMapper(() -> {
                    switch (container.dhdTile.getReactorState()) {
                        case NOT_LINKED:
                            return I18n.format("gui.dhd.not_linked");
                        case NO_CRYSTAL:
                            return I18n.format("gui.dhd.no_crystal");
                        default:
                            return null;
                    }
                }));
    }

    @Override
    public void initGui() {
        super.initGui();

        tabs.clear();

        TabBiomeOverlay overlayTab = createOverlayTab(container.dhdTile.getSupportedOverlays(), xSize, ySize, guiLeft, guiTop);

        tabs.add(overlayTab);

        container.inventorySlots.set(DHDAbstractTile.BIOME_OVERRIDE_SLOT, overlayTab.createAndSaveSlot((SlotItemHandler) container.getSlot(DHDAbstractTile.BIOME_OVERRIDE_SLOT)));
    }

    public void updateTank() {
        int capacity = JSGConfig.dhdConfig.fluidCapacity;
        if (container.dhdTile.hasUpgrade(DHDAbstractTile.DHDUpgradeEnum.CAPACITY_UPGRADE))
            capacity *= JSGConfig.dhdConfig.capacityUpgradeMultiplier;
        if (capacity != container.tankNaquadah.getCapacity())
            container.tankNaquadah.setCapacity(capacity);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        updateTank();
        drawDefaultBackground();

        Tab.updatePositions(tabs);

        ((SlotTab) container.getSlot(5)).updatePos();

        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        for (Tab tab : tabs) {
            tab.render(fontRenderer, mouseX, mouseY);
        }

        mc.getTextureManager().bindTexture(BACKGROUND);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        // Crystal background
        if (container.slotCrystal.getHasStack()) {
            GlStateManager.enableBlend();
            drawCrystal();
            GlStateManager.disableBlend();
        }

        // Wires for upgrades
        for (int i = 1; i < 4; i++) {
            if (container.getSlot(i).getHasStack()) {
                switch (i) {
                    case 1:
                        drawTexturedModalRect(guiLeft + 16, guiTop + 57, 121, 173, 178 - 121, 184 - 172);
                        break;
                    case 2:
                        drawTexturedModalRect(guiLeft + 34, guiTop + 57, 121, 185, 160 - 121, 193 - 184);
                        break;
                    case 3:
                        drawTexturedModalRect(guiLeft + 52, guiTop + 57, 121, 194, 142 - 121, 199 - 193);
                        break;
                    default:
                        break;
                }
            }
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.color(1, 1, 1, 1);

        if (container.dhdTile.getReactorState() == ReactorStateEnum.ONLINE) {
            TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(JSGFluids.NAQUADAH_MOLTEN_REFINED.getStill().toString());

            // Top duct Naquadah
            for (int i = 0; i < 3; i++)
                drawTexturedModalRect(guiLeft + 103 + 16 * i, guiTop + 60, sprite, 16, 16);

            // Bottom duct Naquadah
            for (int i = 0; i < 5; i++)
                GuiHelper.drawTexturedRectScaled(guiLeft + 87 + 16 * i, guiTop + 87, sprite, 16, 16, 10.0f / 16);
        }

        // Naquadah ducts
        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(BACKGROUND);
        drawTexturedModalRect(guiLeft + 103, guiTop + 60, 0, 173, 48, 16);
        drawTexturedModalRect(guiLeft + 84, guiTop + 77, 0, 189, 84, 10);
        GlStateManager.disableBlend();

        // Titles
        drawAncientTitle();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(I18n.format("gui.upgrades"), 8, 29, 4210752);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);

        tank.renderTank();

        // Tank's gauge
        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(BACKGROUND);
        drawTexturedModalRect(152, 23, 176, 32, 16, 54);
        GlStateManager.disableBlend();

        boolean[] statuses = new boolean[diodes.size()];

        for (int i = 0; i < diodes.size(); i++) {
            statuses[i] = diodes.get(i).render(mouseX - guiLeft, mouseY - guiTop);
        }

        for (int i = 0; i < diodes.size(); i++) {
            if (statuses[i])
                diodes.get(i).renderTooltip(mouseX - guiLeft, mouseY - guiTop);
        }

        tank.renderTooltip(mouseX, mouseY);

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
                if (Tab.tabsInteract(tabs, i))
                    container.setOpenTabId(i);
                else
                    container.setOpenTabId(-1);

                JSGPacketHandler.INSTANCE.sendToServer(new SetOpenTabToServer(container.getOpenTabId()));

                break;
            }
        }
    }

    @Override
    public List<Rectangle> getGuiExtraAreas() {
        return tabs.stream()
                .map(Tab::getArea)
                .collect(Collectors.toList());
    }

    public abstract void drawCrystal();

    public abstract void drawAncientTitle();
}
