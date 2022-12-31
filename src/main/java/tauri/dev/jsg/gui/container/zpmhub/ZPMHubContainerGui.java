package tauri.dev.jsg.gui.container.zpmhub;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
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

    public ResourceLocation getBackground(){
        return new ResourceLocation(JSG.MOD_ID, "textures/gui/container_zpmhub.png");
    }

    public String getGuiUnlocalizedName(){
        return "tile.jsg.zpm_hub_block.name";
    }

    protected final ZPMHubContainer container;
    protected BetterButton button;

    public ZPMHubContainerGui(ZPMHubContainer container) {
        super(container);

        this.container = container;
        this.xSize = 176;
        this.ySize = 179;
    }

    @Override
    public void initGui() {
        super.initGui();
        button = new BetterButton(0, 9 + guiLeft, 51 + guiTop, 16, getBackground(), 256, 256, 176, 0);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    private boolean error = false;

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(getBackground());
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

        if(maxEnergyStored == 0)
            width = 0;

        renderPowerBar(width);

        error = container.hubTile.isAnimating;

        button.setEnabled(!error);
        button.drawButton(mouseX, mouseY);

        if(!container.hubTile.isSlidingUp)
            error = true;

        if(error){
            GlStateManager.enableBlend();
            drawTexturedModalRect(button.x + 7, button.y + 6, 176, 16, 16, 16);
            GlStateManager.disableBlend();
        }
    }

    public void renderPowerBar(int width){
        drawGradientRect(guiLeft + 10, guiTop + 75, guiLeft + 10 + width, guiTop + 75 + 6, 0xffcc2828, 0xff731616);
    }

    public void renderPowerText(String energyPercent){
        fontRenderer.drawString(energyPercent, xSize - 8 - fontRenderer.getStringWidth(energyPercent), 85, 4210752);
    }

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
        if (isPointInRegion(10, 75, 156, 6, mouseX, mouseY)) {
            List<String> power = Arrays.asList(
                    I18n.format("gui.stargate.energyBuffer"),
                    TextFormatting.GRAY + String.format("%,d / %,d RF", energyStored, maxEnergyStored),
                    transferredFormatting + transferredSign + String.format("%,d RF/t", transferred));
            drawHoveringText(power, mouseX - guiLeft, mouseY - guiTop);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(I18n.format(getGuiUnlocalizedName()), 8, 16, 4210752);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
        long energyStored = 0;
        long maxEnergyStored = 0;

        for (int i = 0; i < container.hubTile.getContainerSize(); i++) {
            IEnergyStorage energyStorage = container.getSlot(i).getStack().getCapability(CapabilityEnergy.ENERGY, null);
            if (energyStorage == null)
                continue;

            energyStored += energyStorage.getEnergyStored();
            maxEnergyStored += energyStorage.getMaxEnergyStored();
        }

        String energyPercent = String.format("%.2f", (maxEnergyStored != 0 ? (energyStored / (float) maxEnergyStored * 100) : 0)) + " %";
        renderPowerText(energyPercent);

        renderHoverTexts(mouseX, mouseY, energyStored, maxEnergyStored);

        if(error && isPointInRegion(button.x - guiLeft + 9, button.y - guiTop + 9, 16, 16, mouseX, mouseY)){
            List<String> s = Collections.singletonList(I18n.format("gui.zpmhub.alert"));
            drawHoveringText(s, mouseX - guiLeft, mouseY - guiTop);
        }
        else if (isPointInRegion(button.x - guiLeft, button.y - guiTop, 16, 16, mouseX, mouseY)) {
            List<String> s = Collections.singletonList((container.hubTile.isAnimating ? I18n.format("gui.zpmhub.inProgress") : (container.hubTile.isSlidingUp ? I18n.format("gui.zpmhub.slideDown") : I18n.format("gui.zpmhub.slideUp"))));
            drawHoveringText(s, mouseX - guiLeft, mouseY - guiTop);
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
