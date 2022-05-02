package mrjake.aunis.gui.entry;

import mrjake.aunis.gui.OCAddMessageGui;
import mrjake.aunis.gui.element.ArrowButton;
import mrjake.aunis.item.AunisItems;
import mrjake.aunis.item.dialer.UniverseDialerMode;
import mrjake.aunis.item.oc.ItemOCMessage;
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
		sections.add(new Section(100, "item.aunis.gui.name"));
		sections.add(new Section(OCEntry.ADDRESS_WIDTH, "item.aunis.gui.oc_address"));
		sections.add(new Section(OCEntry.PORT_WIDTH, "item.aunis.gui.oc_port"));
		sections.add(new Section(OCEntry.PARAM_WIDTH, "item.aunis.gui.oc_params"));
		sections.add(new Section(UniverseEntry.BUTTON_COUNT*25 - 5, ""));
	}
	
	@Override
	public void entryAdded(ItemOCMessage message) {
		entries.add(new OCEntry(mc, entries.size(), entries.size()+1, hand, message, (action, index) -> performAction(action, index)));
		calculateGuiHeight();

		ItemStack stack = Minecraft.getMinecraft().player.getHeldItem(hand);

		if ((stack.getItem() == AunisItems.UNIVERSE_DIALER || stack.getItem() == AunisItems.GDO) && stack.hasTagCompound()) {
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
