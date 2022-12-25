package tauri.dev.jsg.gui.container.zpmslot;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.gui.container.zpmhub.ZPMHubContainer;
import tauri.dev.jsg.gui.container.zpmhub.ZPMHubContainerGui;
import tauri.dev.jsg.gui.element.BetterButton;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ZPMSlotContainerGui extends ZPMHubContainerGui {
    public ZPMSlotContainerGui(ZPMHubContainer container) {
        super(container);
        this.ySize = 163;
    }

    @Override
    public void initGui() {
        super.initGui();
        button = new BetterButton(0, 9 + guiLeft, 35 + guiTop, 16, getBackground(), 256, 256, 176, 0);
    }

    @Override
    public void renderPowerBar(int width){
        drawGradientRect(guiLeft + 10, guiTop + 59, guiLeft + 10 + width, guiTop + 59 + 6, 0xffcc2828, 0xff731616);
    }

    @Override
    public void renderPowerText(String energyPercent){
        fontRenderer.drawString(energyPercent, xSize - 8 - fontRenderer.getStringWidth(energyPercent), 69, 4210752);
    }

    @Override
    public void renderHoverTexts(int mouseX, int mouseY, long energyStored, long maxEnergyStored){
        int transferred = container.hubTile.getEnergyTransferedLastTick();
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
                    TextFormatting.GRAY + String.format("%,d / %,d RF", energyStored, maxEnergyStored),
                    transferredFormatting + transferredSign + String.format("%,d RF/t", transferred));
            drawHoveringText(power, mouseX - guiLeft, mouseY - guiTop);
        }
    }

    @Override
    public ResourceLocation getBackground(){
        return new ResourceLocation(JSG.MOD_ID, "textures/gui/container_zpmslot.png");
    }

    @Override
    public String getGuiUnlocalizedName(){
        return "tile.jsg.zpm_slot_block.name";
    }
}
