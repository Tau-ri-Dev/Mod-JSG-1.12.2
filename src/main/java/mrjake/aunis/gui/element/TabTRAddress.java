package mrjake.aunis.gui.element;

import mrjake.aunis.tileentity.transportrings.TransportRingsAbstractTile;
import mrjake.aunis.transportrings.SymbolTypeTransportRingsEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.items.SlotItemHandler;

public class TabTRAddress extends Tab {

	// Gate's address
	private TransportRingsAbstractTile trTile;
	private SymbolTypeTransportRingsEnum symbolType;

	protected TabTRAddress(TabAddressBuilder builder) {
		super(builder);
		
		this.trTile = builder.trTile;
		this.symbolType = builder.symbolType;
	}
	
	@Override
	public void render(FontRenderer fontRenderer, int mouseX, int mouseY) {
		super.render(fontRenderer, mouseX, mouseY);
		
		// Draw page slot
		Minecraft.getMinecraft().getTextureManager().bindTexture(bgTexLocation);
		GlStateManager.color(1, 1, 1, 1);
		Gui.drawModalRectWithCustomSizedTexture(guiLeft+currentOffsetX+105, guiTop+defaultY+86, 6, 174, 18, 18, textureSize, textureSize);
		
		int shadow = 2;
		float color = 1.0f;
		
		if (isVisible() && trTile.getRings().getAddress(symbolType) != null) {
			for (int i=0; i<4; i++) {
				Minecraft.getMinecraft().getTextureManager().bindTexture(trTile.getRings().getAddress(symbolType).get(i).getIconResource());
				
				SymbolCoords symbolCoords = getSymbolCoords(i);
				GuiHelper.drawTexturedRectWithShadow(symbolCoords.x, symbolCoords.y, shadow, shadow, symbolType.iconWidht, symbolType.iconHeight, color);
			}
			
			GlStateManager.enableBlend();
			
			Minecraft.getMinecraft().getTextureManager().bindTexture(bgTexLocation);
			int progress = trTile.getPageProgress();
			Gui.drawModalRectWithCustomSizedTexture(guiLeft+currentOffsetX+97, guiTop+defaultY+86+(18-progress), 0, 174+(18-progress), 6, progress, textureSize, textureSize);
						
			GlStateManager.disableBlend();
		}
	}
	
	@Override
	public void renderFg(GuiScreen screen, FontRenderer fontRenderer, int mouseX, int mouseY) {
		super.renderFg(screen, fontRenderer, mouseX, mouseY);
		
		if (isVisible() && isOpen() && trTile.getRings().getAddress(symbolType) != null) {
			for (int i=0; i<4; i++) {
				SymbolCoords symbolCoords = getSymbolCoords(i);
				
				if (GuiHelper.isPointInRegion(symbolCoords.x, symbolCoords.y, symbolType.iconWidht, symbolType.iconHeight, mouseX, mouseY)) {					
					screen.drawHoveringText(trTile.getRings().getAddress(symbolType).get(i).localize(), mouseX-guiLeft, mouseY-guiTop);
				}
			}
		}
	}
	
	public SlotTab createSlot(SlotItemHandler slot) {
		return new SlotTab(slot, (slotTab) -> {
			slotTab.xPos = currentOffsetX + 106;
			slotTab.yPos = defaultY + 87;
		});
	}
	
	public SymbolCoords getSymbolCoords(int symbol) {
		return new SymbolCoords(guiLeft+currentOffsetX+29+31*(symbol%3), guiTop+defaultY+20+28*(symbol/3));
	}
	
	public static class SymbolCoords {
		public final int x;
		public final int y;
		
		public SymbolCoords(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	// ------------------------------------------------------------------------------------------------
	// Builder
	
	public static TabAddressBuilder builder() {
		return new TabAddressBuilder();
	}
	
	public static class TabAddressBuilder extends TabBuilder {
		
		// Gate's TileEntity reference
		private TransportRingsAbstractTile trTile;
		private SymbolTypeTransportRingsEnum symbolType;
		
		public TabAddressBuilder setTile(TransportRingsAbstractTile gateTile) {
			this.trTile = gateTile;
			
			return this;
		}
		
		public TabAddressBuilder setSymbolType(SymbolTypeTransportRingsEnum symbolType) {
			this.symbolType = symbolType;
			
			return this;
		}
		
		@Override
		public TabTRAddress build() {
			return new TabTRAddress(this);
		}
	}
}
