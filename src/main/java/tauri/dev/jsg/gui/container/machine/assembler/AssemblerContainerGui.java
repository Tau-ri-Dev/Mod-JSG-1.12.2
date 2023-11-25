package tauri.dev.jsg.gui.container.machine.assembler;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.energy.CapabilityEnergy;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.power.general.SmallEnergyStorage;

import java.util.Arrays;
import java.util.List;

public class AssemblerContainerGui extends GuiContainer {

    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(JSG.MOD_ID, "textures/gui/container_assembler.png");

    private final AssemblerContainer container;

    public AssemblerContainerGui(AssemblerContainer container) {
        super(container);

        this.container = container;
        this.xSize = 175;
        this.ySize = 230;
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
        drawModalRectWithCustomSizedTexture(guiLeft, guiTop, 0, 0, xSize, ySize, 256, 256);

        long start = container.tile.getMachineStart();
        long end = container.tile.getMachineEnd();
        double progress = (start == -1 || end == -1 || start == end) ? 0 : ((double) (container.tile.getWorld().getTotalWorldTime() - start)) / ((double) (end - start));
        drawModalRectWithCustomSizedTexture(guiLeft + 95, guiTop + 65, 176, 128, ((int) ((216 - 176) * progress)), 143 - 128, 256, 256);

        SmallEnergyStorage energyStorage = (SmallEnergyStorage) container.tile.getCapability(CapabilityEnergy.ENERGY, null);

        int width = Math.round((energyStorage.getEnergyStored() / ((float) energyStorage.getMaxEnergyStored()) * 156));
        drawGradientRect(guiLeft + 10, guiTop + 124, guiLeft + 10 + width, guiTop + 124 + 6, 0xffcc2828, 0xff731616);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(I18n.format("gui.assembler.name"), 7, 6, 4210752);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);

        SmallEnergyStorage energyStorage = (SmallEnergyStorage) container.tile.getCapability(CapabilityEnergy.ENERGY, null);

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
    }
}
