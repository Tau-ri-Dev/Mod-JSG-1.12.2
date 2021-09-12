package mrjake.aunis.item;

import mrjake.aunis.Aunis;
import mrjake.aunis.stargate.power.StargateItemEnergyStorage;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Matousss
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

        return MAX_DAMAGE;

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

    @Override
    public boolean isDamaged(ItemStack stack) {
        return getDamage(stack) == 0;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            items.add(new ItemStack(this));

            ItemStack stack = new ItemStack(this);
            setDamage(stack, MAX_DAMAGE);
            items.add(stack);
        }
    }

}
