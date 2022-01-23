package mrjake.aunis.gui.container.zpmhub;

import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.fluid.AunisFluids;
import mrjake.aunis.gui.container.DHDContainer;
import mrjake.aunis.gui.element.*;
import mrjake.aunis.gui.element.Diode.DiodeStatus;
import mrjake.aunis.gui.element.Tab.SlotTab;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.SetOpenTabToServer;
import mrjake.aunis.stargate.power.StargateAbstractEnergyStorage;
import mrjake.aunis.tileentity.dialhomedevice.DHDMilkyWayTile;
import mrjake.aunis.tileentity.util.ReactorStateEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.SlotItemHandler;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ZPMHubContainerGui extends GuiContainer {

	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Aunis.ModID, "textures/gui/container_zpmhub.png");

	private ZPMHubContainer container;

	public ZPMHubContainerGui(ZPMHubContainer container) {
		super(container);
		
		this.xSize = 176;
		this.ySize = 168;
		this.container = container;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void initGui() {
		super.initGui();
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

		int energyStored = 0;
        
		// Zpm slots background
		for(int i = 0; i < 3; i++) {
			if (container.zpmSlots.get(i).getHasStack()) {
				energyStored += container.zpmHubTile.getEnergyInZPM(i+1);
				GlStateManager.enableBlend();
				drawTexturedModalRect(guiLeft + 50 + (i*26), guiTop + 16, 176, 0, 24, 32);
				GlStateManager.disableBlend();
			}
		}
		int width = Math.round((energyStored/((float) AunisConfig.powerConfig.zpmEnergyStorage*3) * 156));
		drawGradientRect(guiLeft+10, guiTop+61, guiLeft+10+width, guiTop+61+6, 0xffcc2828, 0xff731616);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.color(1, 1, 1, 1);
	}
		
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String fusion = I18n.format("gui.zpmhub.zpms");
		fontRenderer.drawString(fusion, 168-fontRenderer.getStringWidth(fusion)+2, 6, 4210752);
		//fontRenderer.drawString(I18n.format("gui.upgrades"), 7, 6, 4210752);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);

		int energyStored = 0;
		// Zpm slots background
		for(int i = 0; i < 3; i++) {
			if (container.zpmSlots.get(i).getHasStack()) {
				energyStored += container.zpmHubTile.getEnergyInZPM(i+1);
			}
		}
		int maxEnergyStored = AunisConfig.powerConfig.zpmEnergyStorage*3;

		String energyPercent = String.format("%.2f", energyStored/(float)maxEnergyStored * 100) + " %";
		fontRenderer.drawString(energyPercent, 170-fontRenderer.getStringWidth(energyPercent), 71, 4210752);

		int transferred = container.zpmHubTile.getEnergyTransferedLastTick();
		TextFormatting transferredFormatting = TextFormatting.GRAY;

		if (transferred > 0)
			transferred *= -1;
		if(transferred != 0)
			transferredFormatting = TextFormatting.RED;

		if (isPointInRegion(10, 61, 156, 6, mouseX, mouseY)) {
			List<String> power = Arrays.asList(
					I18n.format("gui.stargate.energyBuffer"),
					TextFormatting.GRAY + String.format("%,d / %,d RF", energyStored, maxEnergyStored),
					transferredFormatting + String.format("%,d RF/t", transferred));
			drawHoveringText(power, mouseX-guiLeft, mouseY-guiTop);
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
}
