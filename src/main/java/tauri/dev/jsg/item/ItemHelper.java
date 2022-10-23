package tauri.dev.jsg.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.creativetabs.JSGAbstractCreativeTab;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemHelper {

    public static Item createGenericItem(String name, JSGAbstractCreativeTab tab) {
        Item item = new Item();

        item.setRegistryName(JSG.MOD_ID + ":" + name);
        item.setUnlocalizedName(JSG.MOD_ID + "." + name);

        if (tab != null)
            item.setCreativeTab(tab);

        return item;
    }

    public static Item createDurabilityItem(String name, JSGAbstractCreativeTab tab, int maxDamage, boolean shouldStayInCrafting) {
        Item item = new Item() {
            @Override
            public void setDamage(ItemStack stack, int damage) {
                if (damage >= maxDamage) {
                    stack.setCount(0);
                    return;
                }
                NBTTagCompound nbt;
                if (stack.hasTagCompound()) {
                    nbt = stack.getTagCompound();
                } else {
                    nbt = new NBTTagCompound();
                }
                if (nbt != null) {
                    nbt.setInteger("damage", damage);
                }
                stack.setTagCompound(nbt);
            }

            @Override
            public boolean isDamageable() {
                return true;
            }

            @Override
            public int getMaxDamage(@Nonnull ItemStack stack) {
                return maxDamage;
            }

            @Override
            public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
                tooltip.add(String.format("%.2f", (((double)(maxDamage - getDamage(stack))/((double) maxDamage))*100)) + "%");
            }

            @Override
            public int getDamage(ItemStack stack) {
                if (stack.hasTagCompound()) {
                    NBTTagCompound nbt = stack.getTagCompound();
                    if (nbt.hasKey("damage")) {
                        return nbt.getInteger("damage");
                    } else {
                        nbt.setInteger("damage", 0);
                        stack.setTagCompound(nbt);
                    }
                }
                return 0;
            }

            @Override
            public boolean showDurabilityBar(@Nonnull ItemStack stack) {
                return getDamage(stack) != 0;
            }

            @Override
            public double getDurabilityForDisplay(@Nonnull ItemStack stack) {
                return getDamage(stack) / (double) maxDamage;
            }

            @Nonnull
            @Override
            public Item setMaxDamage(int maxDamageIn) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void onCreated(ItemStack stack, @Nonnull World worldIn, @Nonnull EntityPlayer playerIn) {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setInteger("damage", 0);
                stack.setTagCompound(nbt);
            }

			@Override
            public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
                if (this.isInCreativeTab(tab))
                    items.add(getDefaultInstance());
            }

            @Nonnull
            @Override
            public ItemStack getDefaultInstance() {
                ItemStack itemStack = new ItemStack(this);
                setDamage(itemStack, 0);
                return itemStack;
            }

            @Override
            public boolean canApplyAtEnchantingTable(@Nonnull ItemStack stack, @Nonnull Enchantment enchantment) {
                return (enchantment.getName().equals("enchantment.unbreaking"));
            }

            @Override
            public int getItemEnchantability() {
                return 3;
            }

            @Override
            public boolean hasContainerItem(@Nonnull ItemStack stack) {
                return shouldStayInCrafting;
            }

            @Nonnull
            @Override
            public ItemStack getContainerItem(ItemStack itemStack) {
                itemStack.setItemDamage(itemStack.getItemDamage() + 1);
                return (!shouldStayInCrafting) ? super.getContainerItem(itemStack) : itemStack;
            }
        };

        item.setRegistryName(JSG.MOD_ID + ":" + name);
        item.setUnlocalizedName(JSG.MOD_ID + "." + name);

        if (tab != null)
            item.setCreativeTab(tab);

        return item;
    }

}
