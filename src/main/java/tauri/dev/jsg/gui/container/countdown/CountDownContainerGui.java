package tauri.dev.jsg.gui.container.countdown;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Mouse;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.gui.element.tabs.Tab;
import tauri.dev.jsg.gui.element.tabs.TabConfig;
import tauri.dev.jsg.gui.element.tabs.TabScrollAble;
import tauri.dev.jsg.gui.element.tabs.TabbedContainerInterface;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.SetOpenTabToServer;
import tauri.dev.jsg.packet.stargate.SaveConfigToServer;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static tauri.dev.jsg.gui.container.stargate.StargateContainerGui.createConfigTab;

public class CountDownContainerGui extends GuiContainer implements TabbedContainerInterface {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(JSG.MOD_ID, "textures/gui/container_only_inv.png");

    private TabConfig configTab;
    private List<Tab> tabs;

    private final BlockPos pos;
    private final CountDownContainer container;

    public CountDownContainerGui(BlockPos pos, CountDownContainer container) {
        super(container);
        this.container = container;

        this.xSize = 176;
        this.ySize = 128;

        this.pos = pos;
    }

    @Override
    public void initGui() {
        super.initGui();

        tabs = new ArrayList<>();

        configTab = createConfigTab(container.tile.getConfig(), xSize, ySize, guiLeft, guiTop);

        tabs.add(configTab);

        configTab.setOnTabClose(this::saveConfig);
        configTab.setVisible(container.isOperator);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        Tab.updatePositions(tabs);
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
        for (Tab tab : tabs) {
            tab.renderFg(this, fontRenderer, mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        if (container.tile.getConfig().getOptions().size() != configTab.getConfig(false).getOptions().size())
            configTab.updateConfig(container.tile.getConfig(), true);

        for (Tab tab : tabs) {
            tab.render(fontRenderer, mouseX, mouseY);
        }

        mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        GlStateManager.color(1, 1, 1, 1);
        drawModalRectWithCustomSizedTexture(guiLeft, guiTop, 0, 0, xSize, ySize, 256, 256);

        // title
        drawTexturedModalRect(guiLeft + 119 - 247 + 208, guiTop + 12 - 7, 209, 0, 247 - 208, 8);
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
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1 || mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))
            super.keyTyped(typedChar, keyCode);
        for (Tab tab : tabs) {
            if (tab.isOpen() && tab.isVisible()) {
                tab.keyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public void onGuiClosed() {
        saveConfig();
        super.onGuiClosed();
    }

    private void saveConfig() {
        JSGPacketHandler.INSTANCE.sendToServer(new SaveConfigToServer(pos, configTab.config));
        container.tile.setConfig(configTab.getConfig(true));
    }

    @Override
    public List<Rectangle> getGuiExtraAreas() {
        return tabs.stream()
                .map(Tab::getArea)
                .collect(Collectors.toList());
    }
}
