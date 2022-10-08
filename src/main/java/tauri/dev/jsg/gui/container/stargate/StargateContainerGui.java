package tauri.dev.jsg.gui.container.stargate;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.gui.element.tabs.*;
import tauri.dev.jsg.gui.element.tabs.Tab.SlotTab;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.SetOpenTabToServer;
import tauri.dev.jsg.packet.stargate.SaveConfigToServer;
import tauri.dev.jsg.packet.stargate.SaveIrisCodeToServer;
import tauri.dev.jsg.stargate.network.SymbolMilkyWayEnum;
import tauri.dev.jsg.stargate.network.SymbolPegasusEnum;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.stargate.network.SymbolUniverseEnum;
import tauri.dev.jsg.stargate.power.StargateClassicEnergyStorage;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile.StargateUpgradeEnum;
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
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StargateContainerGui extends GuiContainer implements TabbedContainerInterface {
	
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(JSG.MOD_ID, "textures/gui/container_stargate.png");
	
	private final StargateContainer container;
	private final List<Tab> tabs = new ArrayList<Tab>();;
	
	private TabAddress milkyWayAddressTab;
	private TabAddress pegasusAddressTab;
	private TabAddress universeAddressTab;
	private TabBiomeOverlay overlayTab;
	private TabIris irisTab;
	private TabConfig configTab;

	private int energyStored;
	private int maxEnergyStored;

	private final BlockPos pos;
		
	public StargateContainerGui(BlockPos pos, StargateContainer container) {
		super(container);
		this.container = container;
		
		this.xSize = 176;
		this.ySize = 168;

		this.pos = pos;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		tabs.clear();
				
		milkyWayAddressTab = (TabAddress) TabAddress.builder()
				.setGateTile(container.gateTile)
				.setSymbolType(SymbolTypeEnum.MILKYWAY)
				.setProgressColor(0x98BCF9)
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
		
		pegasusAddressTab = (TabAddress) TabAddress.builder()
				.setGateTile(container.gateTile)
				.setSymbolType(SymbolTypeEnum.PEGASUS)
				.setProgressColor(0x5BCAEC)
				.setGuiSize(xSize, ySize)
				.setGuiPosition(guiLeft, guiTop)
				.setTabPosition(-21, 2+22)
				.setOpenX(-128)
				.setHiddenX(-6)
				.setTabSize(128, 113)
				.setTabTitle(I18n.format("gui.stargate.pegasus_address"))
				.setTabSide(TabSideEnum.LEFT)
				.setTexture(BACKGROUND_TEXTURE, 512)
				.setBackgroundTextureLocation(176, 0)
				.setIconRenderPos(1, 7)
				.setIconSize(20, 18)
				.setIconTextureLocation(304, 18).build();
		
		universeAddressTab = (TabAddress) TabAddress.builder()
				.setGateTile(container.gateTile)
				.setSymbolType(SymbolTypeEnum.UNIVERSE)
				.setProgressColor(0x707070)
				.setGuiSize(xSize, ySize)
				.setGuiPosition(guiLeft, guiTop)
				.setTabPosition(-21, 2+22*2)
				.setOpenX(-128)
				.setHiddenX(-6)
				.setTabSize(128, 113)
				.setTabTitle(I18n.format("gui.stargate.universe_address"))
				.setTabSide(TabSideEnum.LEFT)
				.setTexture(BACKGROUND_TEXTURE, 512)
				.setBackgroundTextureLocation(176, 0)
				.setIconRenderPos(1, 7)
				.setIconSize(20, 18)
				.setIconTextureLocation(304, 18*2).build();

		configTab = (TabConfig) TabConfig.builder()
				.setConfig(container.gateTile.getConfig())
				.setGuiSize(xSize, ySize)
				.setGuiPosition(guiLeft, guiTop)
				.setTabPosition(-21, 2+22*3)
				.setOpenX(-128)
				.setHiddenX(-6)
				.setTabSize(128, 94)
				.setTabTitle(I18n.format("gui.configuration"))
				.setTabSide(TabSideEnum.LEFT)
				.setTexture(BACKGROUND_TEXTURE, 512)
				.setBackgroundTextureLocation(176, 165)
				.setIconRenderPos(1, 7)
				.setIconSize(20, 18)
				.setIconTextureLocation(304, 91).build();
		
		overlayTab = (TabBiomeOverlay) TabBiomeOverlay.builder()
				.setSupportedOverlays(container.gateTile.getSupportedOverlays())
				.setSlotTexture(6, 174)
				.setGuiSize(xSize, ySize)
				.setGuiPosition(guiLeft, guiTop)
				.setTabPosition(176-107, 2)
				.setOpenX(176)
				.setHiddenX(54)
				.setTabSize(128, 51)
				.setTabTitle(I18n.format("gui.stargate.biome_overlay"))
				.setTabSide(TabSideEnum.RIGHT)
				.setTexture(BACKGROUND_TEXTURE, 512)
				.setBackgroundTextureLocation(176, 113)
				.setIconRenderPos(107, 7)
				.setIconSize(20, 18)
				.setIconTextureLocation(304, 54).build();

		irisTab = (TabIris) TabIris.builder()
				.setCode(container.gateTile.getIrisCode())
				.setIrisMode(container.gateTile.getIrisMode())
				.setGuiSize(xSize, ySize)
				.setGuiPosition(guiLeft, guiTop)
				.setTabPosition(176-107, 2+22)
				.setOpenX(176)
				.setHiddenX(54)
				.setTabSize(128, 51)
				.setTabTitle(I18n.format("gui.stargate.iris_code"))
				.setTabSide(TabSideEnum.RIGHT)
				.setTexture(BACKGROUND_TEXTURE, 512)
				.setBackgroundTextureLocation(176, 114)
				.setIconRenderPos(107, 7)
				.setIconSize(20, 18)
				.setIconTextureLocation(304, 72).build();

		irisTab.setOnTabClose(this::saveIrisCode);
		configTab.setOnTabClose(this::saveConfig);

		tabs.add(milkyWayAddressTab);
		tabs.add(pegasusAddressTab);
		tabs.add(universeAddressTab);
		tabs.add(configTab);

		tabs.add(overlayTab);
		tabs.add(irisTab);
		
		container.inventorySlots.set(7, milkyWayAddressTab.createSlot((SlotItemHandler) container.getSlot(7)));
		container.inventorySlots.set(8, pegasusAddressTab.createSlot((SlotItemHandler) container.getSlot(8)));
		container.inventorySlots.set(9, universeAddressTab.createSlot((SlotItemHandler) container.getSlot(9)));
		container.inventorySlots.set(10, overlayTab.createAndSaveSlot((SlotItemHandler) container.getSlot(10)));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
				
		boolean hasAddressUpgrade = false;
		boolean hasMilkyWayUpgrade = false;
		boolean hasAtlantisUpgrade = false;
		boolean hasUniverseUpgrade = false;
		boolean hasIrisUpgrade = !container.getSlot(11).getStack().isEmpty();
		
		for (int i=0; i<4; i++) {
			ItemStack itemStack = container.getSlot(i).getStack();
			
			if (!itemStack.isEmpty()) {
				StargateUpgradeEnum upgrade = StargateUpgradeEnum.valueOf(itemStack.getItem());
				if (upgrade == null) continue;
				switch (upgrade) {
					case CHEVRON_UPGRADE:
						hasAddressUpgrade = true;
						break;

					case MILKYWAY_GLYPHS:
						hasMilkyWayUpgrade = true;
						break;

					case PEGASUS_GLYPHS:
						hasAtlantisUpgrade = true;
						break;

					case UNIVERSE_GLYPHS:
						hasUniverseUpgrade = true;
						break;
				}
			}
		}
		
		milkyWayAddressTab.setMaxSymbols(SymbolMilkyWayEnum.getMaxSymbolsDisplay(hasAddressUpgrade));
		pegasusAddressTab.setMaxSymbols(SymbolPegasusEnum.getMaxSymbolsDisplay(hasAddressUpgrade));
		universeAddressTab.setMaxSymbols(SymbolUniverseEnum.getMaxSymbolsDisplay(hasAddressUpgrade));
		
		milkyWayAddressTab.setVisible(hasMilkyWayUpgrade);
		pegasusAddressTab.setVisible(hasAtlantisUpgrade);
		universeAddressTab.setVisible(hasUniverseUpgrade);
		irisTab.setVisible(hasIrisUpgrade);
		configTab.setVisible(container.isOperator);

		Tab.updatePositions(tabs);
		
		StargateClassicEnergyStorage energyStorageInternal = (StargateClassicEnergyStorage) container.gateTile.getCapability(CapabilityEnergy.ENERGY, null);
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
			if (irisTab.getIrisMode() != container.gateTile.getIrisMode())
				irisTab.updateValue(container.gateTile.getIrisMode());
			if (irisTab.getCode() != container.gateTile.getIrisCode())
				irisTab.updateValue(container.gateTile.getIrisCode());
		}
		if(container.gateTile.getConfig().getOptions().size() != configTab.getConfig(false).getOptions().size())
			configTab.updateConfig(container.gateTile.getConfig(), true);
		for (Tab tab : tabs) {
			tab.render(fontRenderer, mouseX, mouseY);
		}
		
		mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		GlStateManager.color(1,1,1, 1);
		drawModalRectWithCustomSizedTexture(guiLeft, guiTop, 0, 0, xSize, ySize, 512, 512);
		
		// Draw cross on inactive capacitors
		for (int i=0; i<3-container.gateTile.getSupportedCapacitors(); i++) {
			drawModalRectWithCustomSizedTexture(guiLeft+151 - 18*i, guiTop+40, 24, 175, 16, 16, 512, 512);
		}
		
		for (int i=container.gateTile.getPowerTier(); i<4; i++)
			drawModalRectWithCustomSizedTexture(guiLeft+10+39*i, guiTop+61, 0, 168, 39, 6, 512, 512);
		
		int width = Math.round((energyStored/(float) tauri.dev.jsg.config.JSGConfig.powerConfig.stargateEnergyStorage * 156));
		drawGradientRect(guiLeft+10, guiTop+61, guiLeft+10+width, guiTop+61+6, 0xffcc2828, 0xff731616);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("gui.stargate.capacitors"), 112, 29, 4210752);
				
		String energyPercent = String.format("%.2f", energyStored/(float)maxEnergyStored * 100) + " %";
		fontRenderer.drawString(energyPercent, 168-fontRenderer.getStringWidth(energyPercent)+2, 71, 4210752);

		// opened time
		float openedSeconds = container.gateTile.getOpenedSecondsToDisplay();
		if(openedSeconds > 0) {
			String openedTime = I18n.format("gui.stargate.state.opened") + " " + container.gateTile.getOpenedSecondsToDisplayAsMinutes();
			fontRenderer.drawString(openedTime, 46, 16, 4210752);
		}
		else{
			fontRenderer.drawString(I18n.format("gui.stargate.state.closed"), 46, 16, 4210752);
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
			if (toClose < JSGConfig.powerConfig.instabilitySeconds)
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
				else {
					container.setOpenTabId(-1);
				}
				
				JSGPacketHandler.INSTANCE.sendToServer(new SetOpenTabToServer(container.getOpenTabId()));
				
				break;
			}

		}
		for(Tab tab : tabs){
			if(tab.isOpen() && tab.isVisible()){
				tab.mouseClicked(mouseX, mouseY, mouseButton);
			}
		}
	}


	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int wheel = Mouse.getEventDWheel();

		if (wheel != 0) {
			for(Tab tab : tabs){
				if(tab instanceof TabScrollAble && tab.isVisible() && tab.isOpen()){
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
		for(Tab tab : tabs){
			if(tab.isOpen() && tab.isVisible()){
				tab.keyTyped(typedChar, keyCode);
			}
		}
		if(keyCode == 1) // pressed "e"
			super.keyTyped(typedChar, keyCode);
	}

	@Override
	public void onGuiClosed() {
		saveConfig();
		saveIrisCode();
		super.onGuiClosed();
	}

	private void saveConfig(){
		JSGPacketHandler.INSTANCE.sendToServer(new SaveConfigToServer(pos, configTab.config));
		container.gateTile.setConfig(configTab.getConfig(true));
	}

	private void saveIrisCode() {
		JSGPacketHandler.INSTANCE.sendToServer(new SaveIrisCodeToServer(pos, irisTab.getCode(), irisTab.getIrisMode()));
		container.gateTile.setIrisCode(irisTab.getCode());
		container.gateTile.setIrisMode(irisTab.getIrisMode());
	}
}
