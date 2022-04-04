package mrjake.aunis.gui;

import mrjake.aunis.Aunis;
import mrjake.aunis.gui.base.AunisGuiButton;
import mrjake.aunis.gui.base.AunisGuiBase;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.transportrings.SaveRingsParametersToServer;
import mrjake.aunis.state.transportrings.TransportRingsGuiState;
import mrjake.aunis.transportrings.TransportRings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RingsGUI extends AunisGuiBase {
	
	private BlockPos pos;
	public TransportRingsGuiState state;
	
	public RingsGUI(BlockPos pos, TransportRingsGuiState state) {
		super(196, 160, 8, FRAME_COLOR, BG_COLOR, TEXT_COLOR, 4);
		
		this.pos = pos;
		this.state = state;
	}

	private final List<GuiTextField> textFields = new ArrayList<>();

	private GuiTextField nameTextField;
	private GuiTextField distanceTextField;

	private AunisGuiButton saveButton;
	
	@Override
	public void initGui() {	
		super.initGui();
		int y = 20;
		GuiTextField addressTextField = createTextField(50, y, 250, state.isInGrid() ? "" + state.getAddress().toString() : "");
		addressTextField.setEnabled(false);
		textFields.add(addressTextField);
		y += 15;
		
		nameTextField = createTextField(50, y, 16, state.getName());
		textFields.add(nameTextField);
		y += 15;

		distanceTextField = createTextField(50, y, 3, state.getDistance() + "");
		textFields.add(distanceTextField);
		y += 15;
		
		saveButton = new AunisGuiButton(id++, getBottomRightInside(false)-90, getBottomRightInside(true)-20, 90, 20, Aunis.proxy.localize("tile.aunis.transportrings_block.rings_save"));
		buttonList.add(saveButton);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//		drawDefaultBackground();
		
		mouseX -= getTopLeftAbsolute(false);
		mouseY -= getTopLeftAbsolute(true);
		
		GlStateManager.pushMatrix();
		setBackGroundSize(width/3, height-100);
		translateToCenter();
		drawBackground();

		if (state.isInGrid()) {
			drawVerticallCenteredString(new TextComponentTranslation("tile.aunis.transportrings_block.rings_no", state.getAddress()).getFormattedText(), 0, 0, 0xAA5500);
//			drawText("Connected to:", 0, 13, color(88, 97, 115, 255));
		}
		
		else {
			drawVerticallCenteredString(Aunis.proxy.localize("tile.aunis.transportrings_block.rings_not_in_grid"), 0, 0, 0xB36262);
		}	
		
		drawString(Aunis.proxy.localize("tile.aunis.transportrings_block.rings_address") + ": ", 0, 20, 0x00AA00);
		drawString(Aunis.proxy.localize("tile.aunis.transportrings_block.rings_name") + ": ", 0, 35, 0x00AAAA);
		drawString(Aunis.proxy.localize("tile.aunis.transportrings_block.rings_distance") + ": ", 0, 50, 0xF9801D);
		
		for (GuiTextField tf : textFields)
			drawTextBox(tf);
		
		int y = 65;
		for (TransportRings rings : state.getRings()) {			
			drawString(""+rings.getAddress(), 60, y, 0x00AA00);
			drawString(rings.getName(), 70, y, 0x00AAAA);
			
			y += 12;
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.popMatrix();
		
		
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button == saveButton) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			
			try {
				String name = nameTextField.getText();
				try {
					int distance = Integer.parseInt(distanceTextField.getText());

					if (distance >= -40 && distance <= 40) {
						AunisPacketHandler.INSTANCE.sendToServer(new SaveRingsParametersToServer(pos, name, distance));
					}

					else {
						player.sendStatusMessage(new TextComponentTranslation("tile.aunis.transportrings_block.wrong_distance"), true);
					}
				}

				catch (NumberFormatException e) {
					player.sendStatusMessage(new TextComponentTranslation("tile.aunis.transportrings_block.wrong_distance"), true);
				}
			}
			
			catch (NumberFormatException e) {
				player.sendStatusMessage(new TextComponentTranslation("tile.aunis.transportrings_block.wrong_address"), true);
			}
		}
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		
		for (GuiTextField tf : textFields)
			tf.textboxKeyTyped(typedChar, keyCode);
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		
		for (GuiTextField tf : textFields)
			tf.updateCursorCounter();
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		mouseX -= getTopLeftAbsolute(false);
		mouseY -= getTopLeftAbsolute(true);
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		for (GuiTextField tf : textFields)
			tf.mouseClicked(mouseX, mouseY, mouseButton);
	}
}
