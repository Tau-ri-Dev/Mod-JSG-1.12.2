package tauri.dev.jsg.item.armor;

import com.google.common.collect.Multimap;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.UUID;

import static tauri.dev.jsg.creativetabs.JSGCreativeTabsHandler.JSG_ITEMS_CREATIVE_TAB;

public class AncientShield extends Item {

    public AncientShield() {
        String name = "shield_emitter";
        setRegistryName(JSG.MOD_ID + ":" + name);
        setUnlocalizedName(JSG.MOD_ID + "." + name);
        setCreativeTab(JSG_ITEMS_CREATIVE_TAB);
    }

    @Nullable
    @ParametersAreNonnullByDefault
    public EntityEquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EntityEquipmentSlot.CHEST;
    }

    @ParametersAreNonnullByDefault
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
        ItemStack itemstack1 = playerIn.getItemStackFromSlot(entityequipmentslot);

        if (itemstack1.isEmpty()) {
            playerIn.setItemStackToSlot(entityequipmentslot, itemstack.copy());
            itemstack.setCount(0);
            return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
        } else {
            return new ActionResult<>(EnumActionResult.FAIL, itemstack);
        }
    }

    // Do not change that! I see you!
    public static final UUID MODIFIERS_UUID = UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E");

    @Nonnull
    @ParametersAreNonnullByDefault
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EntityEquipmentSlot.CHEST) {
            multimap.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(MODIFIERS_UUID, "Anti-knock-back", 1f, 0));
        }

        return multimap;
    }

    @Override
    public void onArmorTick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull ItemStack itemStack) {
        if (!world.isRemote) {
            long tick = world.getTotalWorldTime();
            if (tick % 20 == 0) {
                if (itemStack.getItem() == this) {
                    // Resistance
                    player.addPotionEffect(new PotionEffect(Objects.requireNonNull(Potion.getPotionFromResourceLocation("resistance")), (20 * 2), 255, true, false));

                    // Hunger thing
                    NBTTagCompound nbt = itemStack.getTagCompound();
                    if(nbt == null) nbt = new NBTTagCompound();
                    long time = nbt.getLong("timeUpdated");
                    long seconds = nbt.getLong("secondsWear");
                    if((tick - time) > 20 * 3){
                        // Not wearing before - reset
                        seconds = 0;
                    }
                    else{
                        // Wearing before - count and effect
                        seconds++;
                        if(seconds > (60L * JSGConfig.Items.shield.hungryAfter)){
                            // Hunger
                            player.addPotionEffect(new PotionEffect(Objects.requireNonNull(Potion.getPotionFromResourceLocation("hunger")), (int) (20 * (60 * JSGConfig.Items.shield.hungryLength)), 2, true, false));
                        }
                    }
                    time = tick;
                    nbt.setLong("timeUpdated", time);
                    nbt.setLong("secondsWear", seconds);

                    itemStack.setTagCompound(nbt);
                }
            }
        }
    }
}
