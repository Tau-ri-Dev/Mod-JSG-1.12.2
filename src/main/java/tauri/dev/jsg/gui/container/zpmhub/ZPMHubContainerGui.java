package tauri.dev.jsg.gui.container.zpmhub;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.gui.element.BetterButton;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.ZPMHubAnimationToServer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ZPMHubContainerGui extends GuiContainer {

    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(JSG.MOD_ID, "textures/gui/container_zpmhub.png");

    private final ZPMHubContainer container;
    private BetterButton button;

    public ZPMHubContainerGui(ZPMHubContainer container) {
        super(container);

        this.container = container;
        this.xSize = 176;
        this.ySize = 168;
    }

    @Override
    public void initGui() {
        super.initGui();
        button = new BetterButton(0, 10 + guiLeft, 38 + guiTop, 16, BACKGROUND_TEXTURE, 256, 256, 176, 0);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        long energyStored = 0;
        long maxEnergyStored = 0;

        for (int i = 0; i < 3; i++) {
            IEnergyStorage energyStorage = container.getSlot(i).getStack().getCapability(CapabilityEnergy.ENERGY, null);
            if (energyStorage == null)
                continue;

            energyStored += energyStorage.getEnergyStored();
            maxEnergyStored += energyStorage.getMaxEnergyStored();
        }
        int width = Math.round((energyStored / ((float) maxEnergyStored) * 156));
        drawGradientRect(guiLeft + 10, guiTop + 61, guiLeft + 10 + width, guiTop + 61 + 6, 0xffcc2828, 0xff731616);

        button.setEnabled(!container.hubTile.isAnimating);
        button.drawButton(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(I18n.format("tile.jsg.zpm_hub_block.name"), 7, 6, 4210752);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
        long energyStored = 0;
        long maxEnergyStored = 0;

        for (int i = 0; i < 3; i++) {
            IEnergyStorage energyStorage = container.getSlot(i).getStack().getCapability(CapabilityEnergy.ENERGY, null);
            if (energyStorage == null)
                continue;

            energyStored += energyStorage.getEnergyStored();
            maxEnergyStored += energyStorage.getMaxEnergyStored();
        }

        String energyPercent = String.format("%.2f", (maxEnergyStored != 0 ? (energyStored / (float) maxEnergyStored * 100) : 0)) + " %";
        fontRenderer.drawString(energyPercent, 170 - fontRenderer.getStringWidth(energyPercent), 71, 4210752);

        int transferred = container.hubTile.getEnergyTransferedLastTick();
        TextFormatting transferredFormatting = TextFormatting.GRAY;
        String transferredSign = "";

        if (transferred > 0) {
            transferredFormatting = TextFormatting.GREEN;
            transferredSign = "+";
        } else if (transferred < 0) {
            transferredFormatting = TextFormatting.RED;
        }

        if (isPointInRegion(10, 61, 156, 6, mouseX, mouseY)) {
            List<String> power = Arrays.asList(
                    I18n.format("gui.stargate.energyBuffer"),
                    TextFormatting.GRAY + String.format("%,d / %,d RF", energyStored, maxEnergyStored),
                    transferredFormatting + transferredSign + String.format("%,d RF/t", transferred));
            drawHoveringText(power, mouseX - guiLeft, mouseY - guiTop);
        }
        if (isPointInRegion(10, 38, 16, 16, mouseX, mouseY)) {
            List<String> power = Collections.singletonList((container.hubTile.isAnimating ? I18n.format("gui.zpmhub.inProgress") : (container.hubTile.isSlidingUp ? I18n.format("gui.zpmhub.slideDown") : I18n.format("gui.zpmhub.slideUp"))));
            drawHoveringText(power, mouseX - guiLeft, mouseY - guiTop);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (button.isMouseOnButton(mouseX, mouseY)) {
            startAnimation();
            button.playPressSound(Minecraft.getMinecraft().getSoundHandler());
        }
    }

    public void startAnimation() {
        JSGPacketHandler.INSTANCE.sendToServer(new ZPMHubAnimationToServer(container.hubTile.getPos()));
    }
}
