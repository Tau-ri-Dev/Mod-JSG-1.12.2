package tauri.dev.jsg.item.notebook;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.creativetabs.JSGCreativeTabsHandler;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.item.renderer.CustomModel;
import tauri.dev.jsg.item.renderer.CustomModelItemInterface;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.StargateNetwork;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.transportrings.SymbolTypeTransportRingsEnum;
import tauri.dev.jsg.transportrings.TransportRingsAddress;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class PageNotebookItem extends Item implements CustomModelItemInterface {

	public static final String ITEM_NAME = "page_notebook";

	public PageNotebookItem() {
		setRegistryName(JSG.MOD_ID + ":" + ITEM_NAME);
		setUnlocalizedName(JSG.MOD_ID + "." + ITEM_NAME);
		
		setCreativeTab(JSGCreativeTabsHandler.JSG_ITEMS_CREATIVE_TAB);
		
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	private CustomModel customModel;
	
	@Override
	public void setCustomModel(CustomModel customModel) {
		this.customModel = customModel;
	}
	
	public TransformType getLastTransform() {
		return customModel.lastTransform;
	}
	
	@Override
	public void registerCustomModel(IRegistry<ModelResourceLocation, IBakedModel> registry) {
		ModelResourceLocation modelResourceLocation = new ModelResourceLocation(getRegistryName() + "_filled", "inventory");
		
		IBakedModel defaultModel = registry.getObject(modelResourceLocation);
		customModel = new CustomModel(defaultModel);
		
		registry.putObject(modelResourceLocation, customModel);
	}
	
	@Override
	public void setCustomModelLocation() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName() + "_empty", "inventory"));
		ModelLoader.setCustomModelResourceLocation(this, 1, new ModelResourceLocation(getRegistryName() + "_filled", "inventory"));
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
		if (!world.isRemote) {
			NBTTagCompound tag = player.getHeldItem(hand).getTagCompound();
			if(tag != null){
				if(tag.hasKey("generateEndAddress")){
					boolean b = tag.getBoolean("generateEndAddress");
					if(b){
						StargateNetwork sgn = StargateNetwork.get(player.getEntityWorld());
						Map.Entry<StargatePos, Map<SymbolTypeEnum, StargateAddress>> gotAddressMap = sgn.getEndStargate();
						if (gotAddressMap == null) {
							player.sendStatusMessage(new TextComponentTranslation("item.jsg.page_mysterious.generation.failed"), true);
						}
						else {
							// gen page
							SymbolTypeEnum symbolTypeEnum = SymbolTypeEnum.getRandom();
							StargateAddress address = gotAddressMap.getValue().get(symbolTypeEnum);
							StargatePos pos = gotAddressMap.getKey();

							String biome = ((pos.getWorld() == null || pos.gatePos == null) ? "plains" : PageNotebookItem.getRegistryPathFromWorld(pos.getWorld(), pos.gatePos));
							int origin = StargateClassicBaseTile.getOriginId(null, 1, -1);

							NBTTagCompound sgCompound = PageNotebookItem.getCompoundFromAddress(address, true, false, false, biome, origin);

							ItemStack stack = new ItemStack(JSGItems.PAGE_NOTEBOOK_ITEM, 1, 1);
							stack.setTagCompound(sgCompound);

							ItemStack held = player.getHeldItem(hand);
							held.shrink(1);

							if (held.isEmpty())
								player.setHeldItem(hand, stack);

							else {
								player.setHeldItem(hand, held);
								player.addItemStackToInventory(stack);
							}
							return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
						}
					}
				}
			}
		}
		return super.onItemRightClick(world, player, hand);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TileEntityItemStackRenderer createTEISR() {
		return new PageNotebookTEISR();
	}
	
	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
		if (stack.getItemDamage() == 0) {			
			tooltip.add(JSG.proxy.localize("item.jsg.page_notebook.empty"));
		}
		
		else {			
			if (stack.hasTagCompound()) {
				NBTTagCompound compound = stack.getTagCompound();
				if(compound == null) return;
				if(!compound.hasKey("transportRings")) {
					SymbolTypeEnum symbolType = SymbolTypeEnum.valueOf(compound.getInteger("symbolType"));
					StargateAddress stargateAddress = new StargateAddress(compound.getCompoundTag("address"));
					int maxSymbols = symbolType.getMaxSymbolsDisplay(compound.getBoolean("hasUpgrade"));
					boolean hideLastSymbol = compound.hasKey("hideLastSymbol") && compound.getBoolean("hideLastSymbol");

					for (int i = 0; i < maxSymbols; i++) {
						if(i == 7 && hideLastSymbol) continue;
						tooltip.add(TextFormatting.ITALIC + "" + (i > 5 ? TextFormatting.DARK_PURPLE : TextFormatting.AQUA) + stargateAddress.get(i).localize());
					}
				}
				else{
					SymbolTypeTransportRingsEnum symbolType = SymbolTypeTransportRingsEnum.valueOf(compound.getInteger("symbolType"));
					TransportRingsAddress trAddress = new TransportRingsAddress(compound.getCompoundTag("address"));

					for (int i = 0; i < trAddress.size(); i++) {
						tooltip.add(TextFormatting.ITALIC + "" + (i > 5 ? TextFormatting.DARK_PURPLE : TextFormatting.AQUA) + trAddress.get(i).localize());
					}
				}
			}
		}
	}
	
	/**
	 * Returns color from the Biome
	 * 
	 * @param registryPath - Registry path of the Biome
	 * @return color
	 */
	public static int getColorForBiome(String registryPath) {
		int color = 0x303000;
		
		if (registryPath.contains("ocean") || registryPath.contains("river")) color = 0x2131A0;
		else if (registryPath.contains("plains")) color = 0x48703D;
		else if (registryPath.contains("desert") || registryPath.contains("beach")) color = 0x9B9C6E;
		else if (registryPath.contains("extreme_hills")) color = 0x736150;
		else if (registryPath.contains("forest")) color = 0x507341;
		else if (registryPath.contains("taiga")) color = 0x7BA9A9;
		else if (registryPath.contains("swamp")) color = 0x6B7337;
		else if (registryPath.contains("hell")) color = 0x962A0B;
		else if (registryPath.contains("sky")) color = 0x67897A;
		else if (registryPath.contains("ice")) color = 0x69B8C6;
		else if (registryPath.contains("mushroom")) color = 0x544B4D;
		else if (registryPath.contains("jungle")) color = 0x104004;
		else if (registryPath.contains("savanna")) color = 0x66622D;
		else if (registryPath.contains("mesa")) color = 0x804117;
		
		return color;
	}
	
	public static String getRegistryPathFromWorld(World world, BlockPos pos) {
		return world.getBiome(pos).getRegistryName().getResourcePath();
	}

	public static NBTTagCompound getCompoundFromAddress(StargateAddress address, boolean hasUpgrade, String registryPath, int originId) {
		return getCompoundFromAddress(address, hasUpgrade, false, false, registryPath, originId);
	}
	public static NBTTagCompound getCompoundFromAddress(StargateAddress address, boolean hasUpgrade, boolean hideLastSymbol, boolean hideOrigin, String registryPath, int originId) {
		NBTTagCompound compound = new NBTTagCompound();
		if(address != null) {
			if (address.getSymbolType() != null) compound.setInteger("symbolType", address.getSymbolType().id);
			if (address.serializeNBT() != null) compound.setTag("address", address.serializeNBT());
		}
		compound.setBoolean("hasUpgrade", hasUpgrade);
		compound.setBoolean("hideLastSymbol", hideLastSymbol);
		compound.setBoolean("hideOrigin", hideOrigin);
		compound.setInteger("color", PageNotebookItem.getColorForBiome(registryPath));
		compound.setInteger("originId", originId);
		
		return compound;
	}

	public static NBTTagCompound getCompoundFromAddress(TransportRingsAddress address, String registryPath) {
		NBTTagCompound compound = new NBTTagCompound();
		if(address != null) {
			if (address.getSymbolType() != null) compound.setInteger("symbolType", address.getSymbolType().id);
			if (address.serializeNBT() != null) compound.setTag("address", address.serializeNBT());
		}
		compound.setBoolean("hasUpgrade", false);
		compound.setBoolean("transportRings", true);
		compound.setInteger("color", PageNotebookItem.getColorForBiome(registryPath));

		return compound;
	}

	private static final String UNNAMED = "item.jsg.notebook.unnamed";
	
	public static String getUnnamedLocalized() {
		return JSG.proxy.localize(UNNAMED);
	}
	
	public static void setName(NBTTagCompound page, String name) {
		NBTTagCompound display = new NBTTagCompound();
		display.setString("Name", name);
		page.setTag("display", display);
	}
	
	public static String getNameFromCompound(NBTTagCompound compound) {		
		if (compound.hasKey("display")) {
			NBTTagCompound display = compound.getCompoundTag("display");
			if (display.hasKey("Name")) {
				return display.getString("Name");
			}
		}
		
		return getUnnamedLocalized();
	}
}
