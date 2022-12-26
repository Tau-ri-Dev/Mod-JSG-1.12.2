package tauri.dev.jsg.gui.entry;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.gui.base.JSGButton;
import tauri.dev.jsg.gui.element.ArrowButton;
import tauri.dev.jsg.item.linkable.dialer.UniverseDialerActionEnum;
import tauri.dev.jsg.item.linkable.dialer.UniverseDialerActionPacketToServer;
import tauri.dev.jsg.item.linkable.dialer.UniverseDialerMode;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.stargate.network.SymbolUniverseEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.client.config.GuiUtils;

/**
 * Class handles universal screen shown when editing Notebook or Universe Dialer
 * saved addresses.
 * 
 * @author MrJake222
 */
public class UniverseEntryChangeGui extends AbstractAddressEntryChangeGui {

	private World world;

	public UniverseEntryChangeGui(EnumHand hand, NBTTagCompound compound, World world) {
		super(hand, compound);
		this.world = world;
	}
	
	protected GuiButton ocButton = null;
	protected GuiButton abortButton = null;
	//protected GuiButton toggleFastDial = null;
	@Override
	public void initGui() {
		super.initGui();
		
		if (JSG.ocWrapper.isModLoaded()) {
			ocButton = new ArrowButton(100, 0, 0, ArrowButton.ArrowType.RIGHT)
					.setFgColor(GuiUtils.getColorCode('a', true))
					.setActionCallback(() -> Minecraft.getMinecraft().displayGuiScreen(new OCEntryChangeGui(hand, mainCompound, this)));
						
			buttonList.add(ocButton);
		}
		
		abortButton = new JSGButton(100, 0, 0, 50, 20, new TextComponentTranslation("item.jsg.universe_dialer.abort").getFormattedText())
				.setFgColor(GuiUtils.getColorCode('c', true))
				.setActionCallback(() -> JSGPacketHandler.INSTANCE.sendToServer(new UniverseDialerActionPacketToServer(UniverseDialerActionEnum.ABORT, hand, false)));
		
		buttonList.add(abortButton);

		/*--toggleFastDial = new JSGButton(101, 0, 0, 100, 20, new TextComponentTranslation("item.jsg.universe_dialer.toggle_fast_dial").getFormattedText())
				.setFgColor(GuiUtils.getColorCode('f', true))
				.setActionCallback(() -> JSGPacketHandler.INSTANCE.sendToServer(new UniverseDialerActionPacketToServer(UniverseDialerActionEnum.SET_FAST_DIAL, hand, false)));

		buttonList.add(toggleFastDial);*/
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if(ocButton != null){
			ocButton.x = dispx+guiWidth+3;
			ocButton.y = height- PADDING -3-20;
		}

		if(abortButton != null) {
			abortButton.visible = mainCompound.hasKey("linkedGate");
			abortButton.x = dispx- PADDING + 2;
			abortButton.y = height- PADDING -3-20;
		}

		/*--if(toggleFastDial != null) {
			toggleFastDial.visible = mainCompound.hasKey("linkedGate") && mainCompound.getBoolean("serverSideEnabledFastDial");
			toggleFastDial.x = dispx- PADDING + 2 + 55;
			toggleFastDial.y = height- PADDING -3-20;
		}*/
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void generateEntries() {
		NBTTagList list = mainCompound.getTagList(UniverseDialerMode.MEMORY.tagListName, NBT.TAG_COMPOUND);

		for (int i=0; i<list.tagCount(); i++) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			
			StargateAddress stargateAddress = new StargateAddress(compound);
			int maxSymbols = SymbolUniverseEnum.getMaxSymbolsDisplay(compound.getBoolean("hasUpgrade"));
			String name = "";
			
			if (compound.hasKey("name")) {
				name = compound.getString("name");
			}
			
			UniverseEntry entry = new UniverseEntry(mc, i, list.tagCount(), hand, name, (action, index) -> performAction(action, index), SymbolTypeEnum.UNIVERSE, stargateAddress, maxSymbols);
			entries.add(entry);
		}
	}

	@Override
	protected void generateSections() {
		sections.add(new Section(UniverseEntry.ADDRESS_WIDTH, "item.jsg.gui.address"));
		sections.add(new Section(100, "item.jsg.gui.name"));
		sections.add(new Section(UniverseEntry.BUTTON_COUNT*25 - 5, ""));
	}
	
	@Override
	protected int getEntryBottomMargin() {
		return 2;
	}

	@Override
	public int getAddressWidth(){ return UniverseEntry.ADDRESS_WIDTH; }
}
