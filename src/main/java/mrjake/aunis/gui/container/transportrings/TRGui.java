package mrjake.aunis.gui.container.transportrings;

import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.gui.element.*;
import mrjake.aunis.gui.element.Tab.SlotTab;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.SetOpenTabToServer;
import mrjake.aunis.packet.stargate.SaveIrisCodeToServer;
import mrjake.aunis.stargate.network.SymbolMilkyWayEnum;
import mrjake.aunis.stargate.network.SymbolPegasusEnum;
import mrjake.aunis.stargate.network.SymbolTypeEnum;
import mrjake.aunis.stargate.network.SymbolUniverseEnum;
import mrjake.aunis.stargate.power.StargateClassicEnergyStorage;
import mrjake.aunis.tileentity.stargate.StargateClassicBaseTile.StargateUpgradeEnum;
import mrjake.aunis.tileentity.transportrings.TransportRingsAbstractTile;
import mrjake.aunis.transportrings.TransportRings;
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

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TRGui extends GuiContainer implements TabbedContainerInterface {
	
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Aunis.ModID, "textures/gui/container_stargate.png");
	
	private TRContainer container;
	private List<Tab> tabs;
	
	private TabAddress goauldAddressTab;

	private int energyStored;
	private int maxEnergyStored;

	private BlockPos pos;
		
	public TRGui(BlockPos pos, TRContainer container) {
		super(container);
		this.container = container;
		
		this.xSize = 176;
		this.ySize = 168;

		this.pos = pos;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		tabs = new ArrayList<Tab>();

		goauldAddressTab = (TabAddress) TabAddress.builder()
				.setGateTile(container.trTile)
				.setSymbolType(SymbolTypeEnum.MILKYWAY)
				.setGuiSize(xSize, ySize)
				.setGuiPosition(guiLeft, guiTop)
				.setTabPosition(-21, 2)
				.setOpenX(-128)
				.setHiddenX(-6)
				.setTabSize(128, 113)
				.setTabTitle(I18n.format("gui.stargate.milky_way_address"))
				.setTabSide(TabSideEnum.LEFT)
				.setTexture(BACKGROUND_TEXTURE, 512)
				.setBackgroundTextureLocation(176, 0)
				.setIconRenderPos(1, 7)
				.setIconSize(20, 18)
				.setIconTextureLocation(304, 0).build();

		tabs.add(goauldAddressTab);
		
		container.inventorySlots.set(7, goauldAddressTab.createSlot((SlotItemHandler) container.getSlot(7)));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
				
		boolean hasGoauldUpgrade = false;
		
		for (int i=0; i<4; i++) {
			ItemStack itemStack = container.getSlot(i).getStack();
			
			if (!itemStack.isEmpty()) {
				TransportRingsAbstractTile.TransportRingsUpgradeEnum upgrade = TransportRingsAbstractTile.TransportRingsUpgradeEnum.valueOf(itemStack.getItem());
				if (upgrade == null) continue;
				switch (upgrade) {
					case GOAULD_UPGRADE:
						hasGoauldUpgrade = true;
						break;
				}
			}
		}
		
		goauldAddressTab.setVisible(hasGoauldUpgrade);

		Tab.updatePositions(tabs);

		StargateClassicEnergyStorage energyStorageInternal = (StargateClassicEnergyStorage) container.trTile.getCapability(CapabilityEnergy.ENERGY, null);
		energyStored = energyStorageInternal.getEnergyStoredInternally();
		maxEnergyStored = energyStorageInternal.getMaxEnergyStoredInternally();
		
		for (int i=4; i<7; i++) {
			IEnergyStorage energyStorage = container.getSlot(i).getStack().getCapability(CapabilityEnergy.ENERGY, null);
			
			if (energyStorage == null)
				continue;
			
			energyStored += energyStorage.getEnergyStored();
			maxEnergyStored += energyStorage.getMaxEnergyStored();
		}
		
		for (int i=7; i<11; i++)
			((SlotTab) container.getSlot(i)).updatePos();


				
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		renderHoveredToolTip(mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		if (irisTab.isVisible() && !irisTab.isOpen()) {
			if (irisTab.getIrisMode() != container.gateTile.getIrisMode()) irisTab.updateValue(container.gateTile.getIrisMode());
			if (irisTab.getCode() != container.gateTile.getIrisCode()) irisTab.updateValue(container.gateTile.getIrisCode());
		}
		for (Tab tab : tabs) {
			tab.render(fontRenderer, mouseX, mouseY);
		}
		
		mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		GlStateManager.color(1,1,1, 1);
		drawModalRectWithCustomSizedTexture(guiLeft, guiTop, 0, 0, xSize, ySize, 512, 512);
		
		// Draw cross on inactive capacitors
		for (int i=0; i<3-container.trTile.getSupportedCapacitors(); i++) {
			drawModalRectWithCustomSizedTexture(guiLeft+151 - 18*i, guiTop+40, 24, 175, 16, 16, 512, 512);
		}
		
		for (int i=container.trTile.getPowerTier(); i<4; i++)
			drawModalRectWithCustomSizedTexture(guiLeft+10+39*i, guiTop+61, 0, 168, 39, 6, 512, 512);
		
		int width = Math.round((energyStored/(float)AunisConfig.powerConfig.stargateEnergyStorage * 156));
		drawGradientRect(guiLeft+10, guiTop+61, guiLeft+10+width, guiTop+61+6, 0xffcc2828, 0xff731616);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("gui.stargate.capacitors"), 112, 29, 4210752);
				
		String energyPercent = String.format("%.2f", energyStored/(float)maxEnergyStored * 100) + " %";
		fontRenderer.drawString(energyPercent, 168-fontRenderer.getStringWidth(energyPercent)+2, 71, 4210752);

		// opened time
		float openedSeconds = container.trTile.getOpenedSecondsToDisplay();
		if(openedSeconds > 0) {
			int minutes = ((int) Math.floor(openedSeconds / 60));
			int seconds = ((int) (openedSeconds - (60 * minutes)));
			String secondsString = ((seconds < 10) ? "0" + seconds : "" + seconds);
			String openedTime = "Opened: " + minutes + ":" + secondsString + "min";
			fontRenderer.drawString(openedTime, 46, 16, 4210752);
		}
		else{
			fontRenderer.drawString("Gate closed", 46, 16, 4210752);
		}

		fontRenderer.drawString(I18n.format("gui.upgrades"), 7, 6, 4210752);
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
			if (toClose < AunisConfig.powerConfig.instabilitySeconds)
				toCloseFormatting = TextFormatting.DARK_RED;
			else
				toCloseFormatting = TextFormatting.GREEN;
		}
		
		if (isPointInRegion(10, 61, 156, 6, mouseX, mouseY)) {
			List<String> power = Arrays.asList(
					I18n.format("gui.stargate.energyBuffer"),
					TextFormatting.GRAY + String.format("%,d / %,d RF", energyStored, maxEnergyStored),
					transferredFormatting + transferredSign + String.format("%,d RF/t", transferred),
					toCloseFormatting + String.format("%.2f s", toClose));
			drawHoveringText(power, mouseX-guiLeft, mouseY-guiTop);
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		for (int i=0; i<tabs.size(); i++) {
			Tab tab = tabs.get(i);
			
			if (tab.isCursorOnTab(mouseX, mouseY)) {
				if (Tab.tabsInteract(tabs, i)) {
					container.setOpenTabId(i);
				}
				else
					container.setOpenTabId(-1);
				
				AunisPacketHandler.INSTANCE.sendToServer(new SetOpenTabToServer(container.getOpenTabId()));
				
				break;
			}
		}
		if (irisTab.isOpen()) {
			irisTab.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}
	
	@Override
	public List<Rectangle> getGuiExtraAreas() {		
		return tabs.stream()
				.map(tab -> tab.getArea())
				.collect(Collectors.toList());
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (irisTab.isOpen()){
			irisTab.keyTyped(typedChar, keyCode);
		}
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	public void onGuiClosed() {
		saveIrisCode();
	}

	private void saveIrisCode() {
		AunisPacketHandler.INSTANCE.sendToServer(new SaveIrisCodeToServer(pos, irisTab.getCode(), irisTab.getIrisMode()));
		container.gateTile.setIrisCode(irisTab.getCode());
		container.gateTile.setIrisMode(irisTab.getIrisMode());
	}
}
