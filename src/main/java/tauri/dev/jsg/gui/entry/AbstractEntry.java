package tauri.dev.jsg.gui.entry;

import tauri.dev.jsg.gui.base.JSGButton;
import tauri.dev.jsg.gui.base.JSGTextField;
import tauri.dev.jsg.gui.element.ArrowButton;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.gui.entry.EntryActionEnum;
import tauri.dev.jsg.packet.gui.entry.EntryActionToServer;
import tauri.dev.jsg.packet.gui.entry.EntryDataTypeEnum;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.opengl.GL11;

import java.util.*;

import static net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText;

public abstract class AbstractEntry {
	
	protected Minecraft mc;
	protected int index;
	protected int maxIndex;
	protected EnumHand hand;
	protected String name;
	
	private ActionListener actionListener;
	
	protected GuiTextField nameField;
	protected JSGButton upButton;
	protected JSGButton downButton;
	protected JSGButton removeButton;
	
	protected List<JSGButton> buttons = new ArrayList<>();
	protected List<GuiTextField> textFields = new ArrayList<>();

	public int entryX;
	public int entryY;

	public AbstractEntry(Minecraft mc, int index, int maxIndex, EnumHand hand, String name, ActionListener actionListener) {
		this.mc = mc;
		this.index = index;
		this.maxIndex = maxIndex;
		this.hand = hand;
		this.name = name;
		this.actionListener = actionListener;
		
		// ----------------------------------------------------------------------------------------------------
		// Text fields
		
		int tId = 0;
		nameField = new JSGTextField(tId++, mc.fontRenderer, 0, 0, 100, 20, name)
				.setActionCallback(() -> action(EntryActionEnum.RENAME));
		
		nameField.setText(name);
		nameField.setMaxStringLength(getMaxNameLength());
		textFields.add(nameField);
		
		
		// ----------------------------------------------------------------------------------------------------
		// Buttons
		
		int bId = 0;
		upButton = new ArrowButton(bId++, 0, 0, ArrowButton.ArrowType.UP)
				.setFgColor(GuiUtils.getColorCode('f', true))
				.setActionCallback(() -> action(EntryActionEnum.MOVE_UP));
		
		downButton = new ArrowButton(bId++, 0, 0, ArrowButton.ArrowType.DOWN)
				.setFgColor(GuiUtils.getColorCode('f', true))
				.setActionCallback(() -> action(EntryActionEnum.MOVE_DOWN));
		
		removeButton = new ArrowButton(bId++, 0, 0, ArrowButton.ArrowType.CROSS)
				.setFgColor(GuiUtils.getColorCode('c', true))
				.setActionCallback(() -> action(EntryActionEnum.REMOVE));
		
		buttons.add(upButton);
		buttons.add(downButton);
		buttons.add(removeButton);
	}
	
	public void renderAt(int dx, int dy, int mouseX, int mouseY, float partialTicks) {
		entryX = dx;
		entryY = dy;
//		dy += getButtonOffset();
		
		// Fields		
		for (GuiTextField tf : textFields) {
			tf.x = dx;
			tf.y = dy;
			tf.drawTextBox();
			
			dx += tf.width + 10;
		}
		
		
		// Buttons
		boolean first = (index == 0);
		boolean last = (index == maxIndex-1);
		upButton.enabled = !first;
		downButton.enabled = !last;
				
		for (GuiButton btn : buttons) {
			btn.x = dx;
			btn.y = dy;
			btn.drawButton(mc, mouseX, mouseY, partialTicks);
			
			dx += 25;
		}
	}

	public void setLocation(int dx, int dy){
		for (GuiTextField tf : textFields) {
			tf.x = dx;
			tf.y = dy;
			dx += tf.width + 10;
		}
		for (GuiButton btn : buttons) {
			btn.x = dx;
			btn.y = dy;
			dx += 25;
		}
	}
	
	
	// ----------------------------------------------------------------------------------------------------
	// Actions
	
	protected void action(EntryActionEnum action) {
		JSGPacketHandler.INSTANCE.sendToServer(new EntryActionToServer(hand, getEntryDataType(), action, index, nameField.getText()));
		actionListener.action(action, index);
	}
	
	
	// ----------------------------------------------------------------------------------------------------
	// Interactions
	
	/**
	 * Called on mouse clicked on every instance of {@link AbstractEntry}
	 * @return {@code true} when a button was clicked, {@code false} if other or no element was activated.
	 */
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton != 0)
			return false;

		for (JSGButton btn : buttons) {
			if (btn.mousePressed(mc, mouseX, mouseY)) {
				// Mouse pressed inside of this button
				btn.playPressSound(this.mc.getSoundHandler());
				btn.performAction();
				
				return true;
			}
		}
		
		for (GuiTextField tf : textFields) {
			tf.mouseClicked(mouseX, mouseY, mouseButton);
		}
		
		return false;
	}
	
	protected void keyTyped(char typedChar, int keyCode) {		
		for (GuiTextField tf : textFields) {
			tf.textboxKeyTyped(typedChar, keyCode);
		}
	}
	
	public void updateScreen() {		
		for (GuiTextField tf : textFields) {
			tf.updateCursorCounter();
		}
	}
	
	protected abstract int getHeight();
//	protected abstract int getButtonOffset();
	protected abstract int getMaxNameLength();
	protected abstract EntryDataTypeEnum getEntryDataType();
	
	protected void renderSymbol(int x, int y, int sizeX, int sizeY, int mouseX, int mouseY, SymbolInterface symbol) {
		GlStateManager.enableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_ADD);
		GlStateManager.color(0.77f, 0.77f, 0.77f, 1);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(symbol.getIconResource());		

		Gui.drawScaledCustomSizeModalRect(x, y, 0, 0, 256, 256, sizeX, sizeY, 256, 256);
		
		GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
	}
	
	static interface ActionListener {
		public void action(EntryActionEnum action, int index);
	}
}
