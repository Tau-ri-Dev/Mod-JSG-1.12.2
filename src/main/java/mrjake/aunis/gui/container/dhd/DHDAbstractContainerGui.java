package mrjake.aunis.gui.container.dhd;

import mrjake.aunis.fluid.AunisFluids;
import mrjake.aunis.gui.element.*;
import mrjake.aunis.gui.element.Diode.DiodeStatus;
import mrjake.aunis.gui.element.Tab.SlotTab;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.SetOpenTabToServer;
import mrjake.aunis.tileentity.dialhomedevice.DHDAbstractTile;
import mrjake.aunis.tileentity.util.ReactorStateEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.SlotItemHandler;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class DHDAbstractContainerGui extends GuiContainer implements TabbedContainerInterface {
	private DHDAbstractContainer container;
	private FluidTankElement tank;

	private List<Diode> diodes = new ArrayList<Diode>(3);

	private List<Tab> tabs = new ArrayList<>();
	private TabBiomeOverlay overlayTab;

	public DHDAbstractContainerGui(DHDAbstractContainer container) {
		super(container);
		
		this.xSize = 176;
		this.ySize = 168;
				
		this.container = container;
		container.tankNaquadah.setFluid(new FluidStack(AunisFluids.moltenNaquadahRefined, 0));
		this.tank = new FluidTankElement(this, 151, 18, 16, 54, container.tankNaquadah);
		
		diodes.add(new Diode(this, 8, 55, I18n.format("gui.dhd.crystalStatus")).setDiodeStatus(DiodeStatus.OFF)
				.putStatus(DiodeStatus.OFF, I18n.format("gui.dhd.no_crystal"))
				.putStatus(DiodeStatus.ON, I18n.format("gui.dhd.crystal_ok"))
				.setStatusMapper(() -> {
					return container.slotCrystal.getHasStack() ? DiodeStatus.ON : DiodeStatus.OFF;
				}));
		
		diodes.add(new Diode(this, 17, 55, I18n.format("gui.dhd.linkStatus")).setDiodeStatus(DiodeStatus.OFF)
				.putStatus(DiodeStatus.OFF, I18n.format("gui.dhd.not_linked"))
				.putStatus(DiodeStatus.ON, I18n.format("gui.dhd.linked"))
				.setStatusMapper(() -> {
					return container.dhdTile.isLinkedClient ? DiodeStatus.ON : DiodeStatus.OFF;
				}));
		
		diodes.add(new Diode(this, 26, 55, I18n.format("gui.dhd.reactorStatus"))
				.putStatus(DiodeStatus.OFF, I18n.format("gui.dhd.no_fuel"))
				.putStatus(DiodeStatus.WARN, I18n.format("gui.dhd.standby"))
				.putStatus(DiodeStatus.ON, I18n.format("gui.dhd.running"))
				.setStatusMapper(() -> {
					switch (container.dhdTile.getReactorState()) {
						case NOT_LINKED:
						case NO_FUEL:
						case NO_CRYSTAL:
							return DiodeStatus.OFF;
							
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
						case NOT_LINKED: return I18n.format("gui.dhd.not_linked");
						case NO_CRYSTAL: return I18n.format("gui.dhd.no_crystal");
						default: return null;
					}	
				}));
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void initGui() {
		super.initGui();
		
		tabs.clear();
		
		overlayTab = (TabBiomeOverlay) TabBiomeOverlay.builder()
				.setSupportedOverlays(container.dhdTile.getSupportedOverlays())
				.setSlotTexture(176, 86)
				.setGuiSize(xSize, ySize)
				.setGuiPosition(guiLeft, guiTop)
				.setTabPosition(176-107, 2)
				.setOpenX(176)
				.setHiddenX(54)
				.setTabSize(128, 51)
				.setTabTitle(I18n.format("gui.stargate.biome_overlay"))
				.setTabSide(TabSideEnum.RIGHT)
				.setTexture(getBackgroundTexture(), 256)
				.setBackgroundTextureLocation(0, 194)
				.setIconRenderPos(107, 7)
				.setIconSize(20, 18)
				.setIconTextureLocation(176, 104).build();
		
		tabs.add(overlayTab);
		
		container.inventorySlots.set(DHDAbstractTile.BIOME_OVERRIDE_SLOT, overlayTab.createAndSaveSlot((SlotItemHandler) container.getSlot(DHDAbstractTile.BIOME_OVERRIDE_SLOT)));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
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
		
		mc.getTextureManager().bindTexture(getBackgroundTexture());
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        
		// Crystal background
		if (container.slotCrystal.getHasStack()) {
			GlStateManager.enableBlend();
			drawTexturedModalRect(guiLeft+76, guiTop+16, 176, 0, 24, 32);
			GlStateManager.disableBlend();
		}
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.color(1, 1, 1, 1);
		
		if (container.dhdTile.getReactorState() == ReactorStateEnum.ONLINE) {
			TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(AunisFluids.moltenNaquadahRefined.getStill().toString());
			
			// Top duct Naquadah
			for (int i=0; i<3; i++)
				drawTexturedModalRect(guiLeft+102+16*i, guiTop+55, sprite, 16, 16);
			
			// Bottom duct Naquadah
			for (int i=0; i<5; i++)
				GuiHelper.drawTexturedRectScaled(guiLeft+86+16*i, guiTop+82, sprite, 16, 16, 10.0f/16);
		}
		
		// Naquadah ducts
		GlStateManager.enableBlend();
		mc.getTextureManager().bindTexture(getBackgroundTexture());
		drawTexturedModalRect(guiLeft+102, guiTop+55, 0, 168, 48, 16);
		drawTexturedModalRect(guiLeft+83, guiTop+72, 0, 184, 84, 10);
		GlStateManager.disableBlend();
	}
		
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String fusion = I18n.format("gui.dhd.fusion");
		fontRenderer.drawString(fusion, 168-fontRenderer.getStringWidth(fusion)+2, 6, 4210752);
		fontRenderer.drawString(I18n.format("gui.upgrades"), 7, 6, 4210752);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
        
        tank.renderTank();
        
        // Tank's gauge
		GlStateManager.enableBlend();
		mc.getTextureManager().bindTexture(getBackgroundTexture());
		drawTexturedModalRect(151, 18, 176, 32, 16, 54);
		GlStateManager.disableBlend();
		
		boolean[] statuses = new boolean[diodes.size()];
		
		for (int i=0; i<diodes.size(); i++) {
			statuses[i] = diodes.get(i).render(mouseX-guiLeft, mouseY-guiTop);
		}
		
		for (int i=0; i<diodes.size(); i++) {
			if (statuses[i])
				diodes.get(i).renderTooltip(mouseX-guiLeft, mouseY-guiTop);
		}
		
		tank.renderTooltip(mouseX, mouseY);
		
		for (Tab tab : tabs) {
			tab.renderFg(this, fontRenderer, mouseX, mouseY);
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		for (int i=0; i<tabs.size(); i++) {
			Tab tab = tabs.get(i);
			
			if (tab.isCursorOnTab(mouseX, mouseY)) {
				if (Tab.tabsInteract(tabs, i))
					container.setOpenTabId(i);
				else
					container.setOpenTabId(-1);
				
				AunisPacketHandler.INSTANCE.sendToServer(new SetOpenTabToServer(container.getOpenTabId()));
				
				break;
			}
		}
	}
	
	@Override
	public List<Rectangle> getGuiExtraAreas() {		
		return tabs.stream()
				.map(tab -> tab.getArea())
				.collect(Collectors.toList());
	}

	public abstract ResourceLocation getBackgroundTexture();
}
