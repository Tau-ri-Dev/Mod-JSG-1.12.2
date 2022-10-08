package tauri.dev.jsg.gui.entry;

import tauri.dev.jsg.gui.OCAddMessageGui;
import tauri.dev.jsg.gui.element.ArrowButton;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.item.linkable.dialer.UniverseDialerMode;
import tauri.dev.jsg.item.oc.ItemOCMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.client.config.GuiUtils;

public class OCEntryChangeGui extends AbstractEntryChangeGui implements OCUpdatable {

	protected GuiScreen parentScreen;
	protected GuiButton backButton;
	protected GuiButton addButton;
	
	public OCEntryChangeGui(EnumHand hand, NBTTagCompound compound, GuiScreen parentScreen) {
		super(hand, compound);
		
		this.parentScreen =parentScreen;
	}
	@Override
	public void initGui() {
		super.initGui();
		
		backButton = new ArrowButton(100, 0, 0, ArrowButton.ArrowType.LEFT)
				.setFgColor(GuiUtils.getColorCode('c', true))
				.setActionCallback(() -> Minecraft.getMinecraft().displayGuiScreen(parentScreen));
		
		addButton = new ArrowButton(100, 0, 0, ArrowButton.ArrowType.PLUS)
				.setFgColor(GuiUtils.getColorCode('a', true))
				.setActionCallback(() -> Minecraft.getMinecraft().displayGuiScreen(new OCAddMessageGui(hand, this)));
		
		buttonList.add(backButton);
		buttonList.add(addButton);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if(addButton != null){
			addButton.x = dispx+guiWidth+3;
			addButton.y = height-AbstractEntryChangeGui.PADDING-3-20;
		}
		if(backButton != null) {
			backButton.x = dispx-AbstractEntryChangeGui.PADDING + 2;
			backButton.y = height-AbstractEntryChangeGui.PADDING-3-20;
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void generateEntries() {
		NBTTagList list = mainCompound.getTagList(UniverseDialerMode.OC.tagListName, NBT.TAG_COMPOUND);

		for (int i=0; i<list.tagCount(); i++) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			
			ItemOCMessage message = new ItemOCMessage(compound);
			
			OCEntry entry = new OCEntry(mc, i, list.tagCount(), hand, message, (action, index) -> performAction(action, index));
			entries.add(entry);
		}
	}

	@Override
	protected void generateSections() {
		sections.add(new Section(100, "item.jsg.gui.name"));
		sections.add(new Section(OCEntry.ADDRESS_WIDTH, "item.jsg.gui.oc_address"));
		sections.add(new Section(OCEntry.PORT_WIDTH, "item.jsg.gui.oc_port"));
		sections.add(new Section(OCEntry.PARAM_WIDTH, "item.jsg.gui.oc_params"));
		sections.add(new Section(UniverseEntry.BUTTON_COUNT*25 - 5, ""));
	}
	
	@Override
	public void entryAdded(ItemOCMessage message) {
		entries.add(new OCEntry(mc, entries.size(), entries.size()+1, hand, message, (action, index) -> performAction(action, index)));
		calculateGuiHeight();

		ItemStack stack = Minecraft.getMinecraft().player.getHeldItem(hand);

		if ((stack.getItem() == JSGItems.UNIVERSE_DIALER || stack.getItem() == JSGItems.GDO) && stack.hasTagCompound()) {
			NBTTagCompound compound = mainCompound;
			NBTTagList ocList = compound.getTagList(UniverseDialerMode.OC.tagListName, Constants.NBT.TAG_COMPOUND);
			ocList.appendTag(message.serializeNBT());
			compound.setTag(UniverseDialerMode.OC.tagListName, ocList);
		}

	}

	@Override
	protected int getEntryBottomMargin() {
		return 2;
	}
}
