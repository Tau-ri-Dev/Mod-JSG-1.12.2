package tauri.dev.jsg.crafting;

import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.item.linkable.dialer.UniverseDialerItem;
import tauri.dev.jsg.item.linkable.dialer.UniverseDialerMode;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

public class UniverseDialerCloneRecipe extends Impl<IRecipe> implements IRecipe {

	public UniverseDialerCloneRecipe() {
		setRegistryName("universe_dialer_cloning");
	}
	
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		int matchCount = 0;
		
		for (int i=0; i<inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			Item item = stack.getItem();
			
			if (item == JSGItems.UNIVERSE_DIALER && stack.getMetadata() == UniverseDialerItem.UniverseDialerVariants.NORMAL.meta)
				matchCount++;
			else if (!stack.isEmpty())
				return false;
		}
		
		return matchCount >= 2;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		int outputCount = 0;
		NBTTagList addressTagList = new NBTTagList();
		NBTTagList ocTagList = new NBTTagList();
		
		for (int i=0; i<inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			Item item = stack.getItem();
			
			if (item == JSGItems.UNIVERSE_DIALER) {
				NBTTagList addressTags = stack.getTagCompound().getTagList(UniverseDialerMode.MEMORY.tagListName, NBT.TAG_COMPOUND);
				NBTTagList ocTags = stack.getTagCompound().getTagList(UniverseDialerMode.OC.tagListName, NBT.TAG_COMPOUND);
				
				for (NBTBase tag : addressTags) {
					if (!NotebookRecipe.tagListContains(addressTagList, (NBTTagCompound) tag)) {
						addressTagList.appendTag(tag);
					}
				}
				
				for (NBTBase tag : ocTags) {
					if (!NotebookRecipe.tagListContains(ocTagList, (NBTTagCompound) tag)) {
						ocTagList.appendTag(tag);
					}
				}
				
				outputCount++;
			}
		}
		
		ItemStack output = new ItemStack(JSGItems.UNIVERSE_DIALER, outputCount);
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag(UniverseDialerMode.MEMORY.tagListName, addressTagList);
		compound.setTag(UniverseDialerMode.OC.tagListName, ocTagList);
		compound.setInteger("selected", 0);
		output.setTagCompound(compound);
		
		return output;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width*height >= 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(JSGItems.UNIVERSE_DIALER, 2);
	}
}
