package tauri.dev.jsg.gui.entry;

import tauri.dev.jsg.gui.base.JSGButton;
import tauri.dev.jsg.gui.base.JSGTextField;
import tauri.dev.jsg.item.oc.ItemOCMessage;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.gui.entry.EntryDataTypeEnum;
import tauri.dev.jsg.packet.gui.entry.OCActionEnum;
import tauri.dev.jsg.packet.gui.entry.OCActionToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;

public class OCEntry extends AbstractEntry {

	public static final int ADDRESS_WIDTH = 222;
	public static final int PORT_WIDTH = 38;
	public static final int PARAM_WIDTH = 100;
	public static final int BUTTON_COUNT = 3;

	protected ItemOCMessage message;
	
	protected GuiTextField addressField;
	protected GuiTextField portField;
	protected GuiTextField paramField;
	protected JSGButton addButton;
	
	public OCEntry(Minecraft mc, int index, int maxIndex, EnumHand hand, ItemOCMessage message, ActionListener actionListener) {
		super(mc, index, maxIndex, hand, message.name, actionListener);
		this.message = message;
		
		
		// ----------------------------------------------------------------------------------------------------
		// Text fields
		
		int tId = 0;
		addressField = new JSGTextField(tId++, mc.fontRenderer, 0, 0, ADDRESS_WIDTH, 20, message.address)
				.setMaxStringLengthBetter(36)
				.setActionCallback(() -> ocAction(OCActionEnum.CHANGE_ADDRESS));
				
		portField = new JSGTextField(tId++, mc.fontRenderer, 0, 0, PORT_WIDTH, 20, String.valueOf(message.port))
				.setNumbersOnly()
				.setMaxStringLengthBetter(5)
				.setActionCallback(() -> ocAction(OCActionEnum.CHANGE_PORT));
		
		paramField = new JSGTextField(tId++, mc.fontRenderer, 0, 0, PARAM_WIDTH, 20, message.dataStr)
				.setActionCallback(() -> ocAction(OCActionEnum.CHANGE_PARAMS));
		
		textFields.add(addressField);
		textFields.add(portField);
		textFields.add(paramField);
	}
	
	private void ocAction(OCActionEnum action) {
		String string = "";
		switch (action) {
			case CHANGE_ADDRESS:
				string = addressField.getText();
				break;
				
			case CHANGE_PORT:
				int port;
				
				try {
					port = Integer.valueOf(portField.getText());
					
					if (port < 0 || port > 65535) {
						throw new NumberFormatException();
					}
				}
				
				catch (NumberFormatException e) {
					Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentTranslation("item.jsg.gui.oc_port_invalid"), true);
					return;
				}
				
				string = portField.getText();
				break;
				
			case CHANGE_PARAMS:
				string = paramField.getText();
				break;
				
		}
		
		JSGPacketHandler.INSTANCE.sendToServer(new OCActionToServer(hand, action, index, string));
	}
	
	@Override
	protected int getHeight() {
		return 20;
	}

	@Override
	protected int getMaxNameLength() {
		return 16;
	}

	@Override
	protected EntryDataTypeEnum getEntryDataType() {
		return EntryDataTypeEnum.OC;
	}
	
}
