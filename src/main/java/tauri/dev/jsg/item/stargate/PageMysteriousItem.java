package tauri.dev.jsg.item.stargate;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.item.notebook.PageNotebookItem;
import tauri.dev.jsg.worldgen.StargateGenerator;
import tauri.dev.jsg.worldgen.StargateGenerator.GeneratedStargate;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import tauri.dev.jsg.util.main.loader.JSGCreativeTabsHandler;

import java.util.List;

public class PageMysteriousItem extends Item {
	public static final String ITEM_NAME = "page_mysterious";

	public PageMysteriousItem() {
		setRegistryName(JSG.MOD_ID + ":" + ITEM_NAME);
		setUnlocalizedName(JSG.MOD_ID + "." + ITEM_NAME);
		
		setCreativeTab(JSGCreativeTabsHandler.jsgItemsCreativeTab);
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TextFormatting.ITALIC + JSG.proxy.localize("item.jsg.page_mysterious.tooltip"));
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		
		if (!world.isRemote) {
			GeneratedStargate stargate = StargateGenerator.generateStargate(world);
			
			if (stargate != null) {
				NBTTagCompound compound = PageNotebookItem.getCompoundFromAddress(stargate.address, stargate.hasUpgrade, stargate.path);
				
				ItemStack stack = new ItemStack(JSGItems.PAGE_NOTEBOOK_ITEM, 1, 1);
				stack.setTagCompound(compound);
				
				ItemStack held = player.getHeldItem(hand);
				held.shrink(1);
				
				if (held.isEmpty())				
					player.setHeldItem(hand, stack);
				
				else {
					player.setHeldItem(hand, held);
					player.addItemStackToInventory(stack);
				}

				if(JSGConfig.mysteriousConfig.pageCooldown > 0)
					player.getCooldownTracker().setCooldown(this, tauri.dev.jsg.config.JSGConfig.mysteriousConfig.pageCooldown);
			}
		}
		
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));	
	}
}
