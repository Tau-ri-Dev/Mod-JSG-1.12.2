package tauri.dev.jsg.gui.container.capacitor;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.energy.CapabilityEnergy;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.power.general.SmallEnergyStorage;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CapacitorContainerGui extends GuiContainer {

    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(JSG.MOD_ID, "textures/gui/container_capacitor.png");

    private final CapacitorContainer container;

    public CapacitorContainerGui(CapacitorContainer container) {
        super(container);

        this.container = container;
        this.xSize = 176;
        this.ySize = 163;
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

        SmallEnergyStorage energyStorage = (SmallEnergyStorage) container.capTile.getCapability(CapabilityEnergy.ENERGY, null);

        int width = Math.round((Objects.requireNonNull(energyStorage).getEnergyStored() / ((float) JSGConfig.Stargate.power.stargateEnergyStorage / 4) * 156));
        drawGradientRect(guiLeft + 10, guiTop + 59, guiLeft + 10 + width, guiTop + 59 + 6, 0xffcc2828, 0xff731616);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(I18n.format("gui.capacitor.name"), 8, 16, 4210752);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);

        SmallEnergyStorage energyStorage = (SmallEnergyStorage) container.capTile.getCapability(CapabilityEnergy.ENERGY, null);

        int energyStored = Objects.requireNonNull(energyStorage).getEnergyStored();
        int maxEnergyStored = energyStorage.getMaxEnergyStored();

        String energyPercent = String.format("%.2f", energyStored / (float) maxEnergyStored * 100) + " %";
        fontRenderer.drawString(energyPercent, this.xSize - 8 - fontRenderer.getStringWidth(energyPercent), 69, 4210752);

        int transferred = container.capTile.getEnergyTransferedLastTick();
        TextFormatting transferredFormatting = TextFormatting.GRAY;
        String transferredSign = "";

        if (transferred > 0) {
            transferredFormatting = TextFormatting.GREEN;
            transferredSign = "+";
        } else if (transferred < 0) {
            transferredFormatting = TextFormatting.RED;
        }

        if (isPointInRegion(10, 59, 156, 6, mouseX, mouseY)) {
            List<String> power = Arrays.asList(
                    I18n.format("gui.stargate.energyBuffer"),
                    TextFormatting.GRAY + String.format("%,d / %,d RF", energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored()),
                    transferredFormatting + transferredSign + String.format("%,d RF/t", transferred));
            drawHoveringText(power, mouseX - guiLeft, mouseY - guiTop);
        }
    }
}
