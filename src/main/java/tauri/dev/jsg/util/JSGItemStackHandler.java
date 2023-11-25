package tauri.dev.jsg.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

/**
 * Modified version of {@link ItemStackHandler}.
 * Respects resizing of the item handlers.
 */
public class JSGItemStackHandler extends ItemStackHandler {

    private final int size;
	
	public JSGItemStackHandler(int size) {
		super(size);
		this.size = size;
	}

	public int getSize() {
	    return size;
    }

	@Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        NBTTagList tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.tagCount(); i++)
        {
            NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
            int slot = itemTags.getInteger("Slot");

            if (slot >= 0 && slot < stacks.size())
            {
                stacks.set(slot, new ItemStack(itemTags));
            }
        }
        onLoad();
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate){
        ItemStack stack = getStackInSlot(slot);
        //if(!CreativeItemsChecker.canInteractWith(stack, false)) return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }
}
