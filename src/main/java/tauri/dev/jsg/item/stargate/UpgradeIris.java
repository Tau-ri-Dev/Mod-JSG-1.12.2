package tauri.dev.jsg.item.stargate;

import tauri.dev.jsg.JSG;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.creativetabs.JSGCreativeTabsHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author matousss
 */
public class UpgradeIris extends Item {
    public final int MAX_DAMAGE;

    public UpgradeIris(String name, int durability) {
        MAX_DAMAGE = durability;
        setRegistryName(JSG.MOD_ID + ":" + name);
        setUnlocalizedName(JSG.MOD_ID + "." + name);

        setCreativeTab(JSGCreativeTabsHandler.JSG_ITEMS_CREATIVE_TAB);
    }

    @Override
    public void setDamage(@Nonnull ItemStack stack, int damage) {
        if (damage >= MAX_DAMAGE) {
            stack.setCount(0);
            return;
        }
        NBTTagCompound nbt = null;
        if (stack.hasTagCompound())
            nbt = stack.getTagCompound();

        if(nbt == null)
            nbt = new NBTTagCompound();

        nbt.setInteger("damage", damage);
        stack.setTagCompound(nbt);
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public int getMaxDamage(@Nonnull ItemStack stack) {
        return MAX_DAMAGE;
    }

    @Override
    public int getDamage(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            if(nbt != null) {
                if (nbt.hasKey("damage")) {
                    return nbt.getInteger("damage");
                } else {
                    nbt.setInteger("damage", 0);
                    stack.setTagCompound(nbt);
                }
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
        return getDamage(stack) / (double) MAX_DAMAGE;
    }

    @Nonnull
    @Override
    public Item setMaxDamage(int maxDamageIn) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        float percent = ((float) getDamage(stack)/MAX_DAMAGE) * 100f;
        tooltip.add(String.format("%.2f", percent) + " %");
    }

    @Override
    public void onCreated(ItemStack stack, @Nonnull World worldIn, @Nonnull EntityPlayer playerIn) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("damage", 0);
        stack.setTagCompound(nbt);
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items)
    {
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
        if (JSGConfig.irisConfig.unbreakingChance == 0) return false;
        return (enchantment.getName().equals("enchantment.unbreaking"));
    }

    @Override
    public int getItemEnchantability() {
        return 10;
    }

}
