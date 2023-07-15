package tauri.dev.jsg.crafting;

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
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.item.linkable.dialer.UniverseDialerItem;
import tauri.dev.jsg.item.linkable.dialer.UniverseDialerMode;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class DialerPageRecipe extends Impl<IRecipe> implements IRecipe {

    public DialerPageRecipe() {
        setRegistryName("universe_dialer_cloning");
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int dialerCount = 0;
        int pagesCount = 0;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            Item item = stack.getItem();

            if (item == JSGItems.UNIVERSE_DIALER && stack.getMetadata() == UniverseDialerItem.UniverseDialerVariants.NORMAL.meta)
                dialerCount++;
            else if (item == JSGItems.PAGE_NOTEBOOK_ITEM && stack.getMetadata() == 1) {
                if (stack.getTagCompound() != null && SymbolTypeEnum.valueOf(stack.getTagCompound().getInteger("symbolType")) == SymbolTypeEnum.UNIVERSE)
                    pagesCount++;
            } else if (!stack.isEmpty())
                return false;
        }

        return dialerCount == 1 && pagesCount >= 1;
    }

    @Override
    @Nonnull
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        NBTTagList addressTagList = new NBTTagList();
        NBTTagList ocTagList = new NBTTagList();

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            Item item = stack.getItem();
            NBTTagCompound compound = stack.getTagCompound();

            if (item == JSGItems.UNIVERSE_DIALER) {
                if (compound == null) return ItemStack.EMPTY;
                NBTTagList addressTags = compound.getTagList(UniverseDialerMode.MEMORY.tagListName, NBT.TAG_COMPOUND);
                NBTTagList ocTags = compound.getTagList(UniverseDialerMode.OC.tagListName, NBT.TAG_COMPOUND);

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
            }
            if (item == JSGItems.PAGE_NOTEBOOK_ITEM) {
                if (compound == null) return ItemStack.EMPTY;
                StargateAddress stargateAddress = new StargateAddress(compound.getCompoundTag("address"));
                NBTTagCompound compoundNew = stargateAddress.serializeNBT();
                compoundNew.setBoolean("hasUpgrade", compound.getBoolean("hasUpgrade"));
                compoundNew.setInteger("originId", 0);
                addressTagList.appendTag(compoundNew);
            }
        }

        ItemStack output = new ItemStack(JSGItems.UNIVERSE_DIALER, 1);
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag(UniverseDialerMode.MEMORY.tagListName, addressTagList);
        compound.setTag(UniverseDialerMode.OC.tagListName, ocTagList);
        compound.setInteger("selected", 0);
        output.setTagCompound(compound);

        return output;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    @Nonnull
    public ItemStack getRecipeOutput() {
        return new ItemStack(JSGItems.UNIVERSE_DIALER);
    }
}
