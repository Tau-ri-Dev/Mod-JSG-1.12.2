package mrjake.aunis.item;

import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author matousss
 */
public class UpgradeIris extends Item {
    public final int MAX_DAMAGE;

    public UpgradeIris(String name, int durability) {
        MAX_DAMAGE = durability;
        setRegistryName(Aunis.ModID + ":" + name);
        setUnlocalizedName(Aunis.ModID + "." + name);

        setCreativeTab(Aunis.aunisCreativeTab);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        if (damage >= MAX_DAMAGE) {
            stack.setCount(0);
            return;
        }
        NBTTagCompound nbt;
        if (stack.hasTagCompound()) {
            nbt = stack.getTagCompound();
        } else {
            nbt = new NBTTagCompound();
        }
        nbt.setInteger("damage", damage);
        stack.setTagCompound(nbt);
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return MAX_DAMAGE;
    }

    @Override
    public int getDamage(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt.hasKey("damage")) {
                return nbt.getInteger("damage");
            }
            else {
                nbt.setInteger("damage", 0);
                stack.setTagCompound(nbt);
            }
        }

        return 0;

    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getDamage(stack) != 0;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return getDamage(stack) / (double) MAX_DAMAGE;
    }

    @Override
    public Item setMaxDamage(int maxDamageIn) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(MAX_DAMAGE - getDamage(stack) + "/" + MAX_DAMAGE);
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("damage", 0);
        stack.setTagCompound(nbt);
    }

    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
            items.add(getDefaultInstance());
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemStack = new ItemStack(this);
        setDamage(itemStack, 0);
        return itemStack;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (AunisConfig.irisConfig.unbreakingChance == 0) return false;
        return (enchantment.getName().equals("enchantment.unbreaking"));
    }

    @Override
    public int getItemEnchantability() {
        return 10;
    }

}
