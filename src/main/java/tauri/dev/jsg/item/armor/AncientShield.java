package tauri.dev.jsg.item.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.capability.ItemCapabilityProvider;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.power.general.ItemEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

import static tauri.dev.jsg.creativetabs.JSGCreativeTabsHandler.JSG_ITEMS_CREATIVE_TAB;

public class AncientShield extends ItemArmor {

    static {
        EnumHelper.addArmorMaterial("armor_shield", JSG.MOD_ID + ":" + "shield_off", 1, new int[]{0, 0, 1, 0}, 1, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0);
    }

    public AncientShield() {
        super(ArmorMaterial.valueOf("armor_shield"), 1, EntityEquipmentSlot.CHEST);
        String name = "shield_emitter";
        setRegistryName(JSG.MOD_ID + ":" + name);
        setUnlocalizedName(JSG.MOD_ID + "." + name);
        setCreativeTab(JSG_ITEMS_CREATIVE_TAB);
    }

    // ------------------------------------------------
    // Texturing

    @Nullable
    @ParametersAreNonnullByDefault
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        String texture = "shield_off";

        // Get last hit
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) nbt = new NBTTagCompound();
        long lastHit = nbt.getLong("lastHit");
        if ((entity.world.getTotalWorldTime() - lastHit) < 20) {
            texture = "shield_on";
        }

        return JSG.MOD_ID + ":textures/armor/" + texture + ".png";
    }

    @SideOnly(Side.CLIENT)
    @Nullable
    @ParametersAreNonnullByDefault
    public net.minecraft.client.model.ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, net.minecraft.client.model.ModelBiped _default) {

        _default.bipedHead.isHidden = false;
        _default.bipedBody.isHidden = false;
        _default.bipedHeadwear.isHidden = false;
        _default.bipedLeftArm.isHidden = false;
        _default.bipedRightArm.isHidden = false;
        _default.bipedLeftLeg.isHidden = false;
        _default.bipedRightLeg.isHidden = false;

        _default.setVisible(true);
        return _default;
    }


    // ------------------------------------------------
    // Energy

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            items.add(new ItemStack(this));

            ItemStack stack = new ItemStack(this);
            ItemEnergyStorage energyStorage = (ItemEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null);
            if (energyStorage == null) return;
            energyStorage.setEnergyStored(energyStorage.getMaxEnergyStored());
            items.add(stack);
        }
    }


    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new ItemCapabilityProvider(stack, nbt, JSGConfig.Items.shield.energy);
    }

    public int getEnergyStored(ItemStack stack) {
        ItemEnergyStorage energyStorage = (ItemEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (energyStorage == null) return 0;
        return energyStorage.getEnergyStored();
    }

    public void drawEnergyTick(ItemStack stack) {
        ItemEnergyStorage energyStorage = (ItemEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (energyStorage == null) return;
        energyStorage.extractEnergy(JSGConfig.Items.shield.energyPerTick, false);
    }

    public void drawEnergyHit(ItemStack stack, int amount) {
        ItemEnergyStorage energyStorage = (ItemEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (energyStorage == null) return;
        energyStorage.extractEnergy(JSGConfig.Items.shield.energyDamage * amount, false);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(JSG.getInProgress());
        tooltip.add("");
        ItemEnergyStorage energyStorage = (ItemEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (energyStorage == null) return;
        String energy = String.format("%,d", energyStorage.getEnergyStored());
        String capacity = String.format("%,d", energyStorage.getMaxEnergyStored());

        tooltip.add(energy + " / " + capacity + " RF");

        String energyPercent = String.format("%.2f", energyStorage.getEnergyStored() / (float) energyStorage.getMaxEnergyStored() * 100) + " %";
        tooltip.add(energyPercent);
    }

    @Override
    public boolean showDurabilityBar(@Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        ItemEnergyStorage energyStorage = (ItemEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (energyStorage == null) return 1;

        return 1 - (energyStorage.getEnergyStored() / (double) energyStorage.getMaxEnergyStored());
    }

    // ------------------------------------------------
    // Equipment

    @Nullable
    @ParametersAreNonnullByDefault
    public EntityEquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EntityEquipmentSlot.CHEST;
    }

    @ParametersAreNonnullByDefault
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);
        EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemStack);
        ItemStack itemStack1 = playerIn.getItemStackFromSlot(entityequipmentslot);

        if (itemStack1.isEmpty()) {
            playerIn.setItemStackToSlot(entityequipmentslot, itemStack.copy());
            itemStack.setCount(0);
            return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
        } else {
            return new ActionResult<>(EnumActionResult.FAIL, itemStack);
        }
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        return HashMultimap.create();
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getMaxDamage(ItemStack stack) {
        return 1;
    }


    @Override
    @ParametersAreNonnullByDefault
    public int getDamage(ItemStack stack) {
        return 0;
    }

    public void shieldHit(ItemStack stack, Entity e){
        if (getEnergyStored(stack) < (JSGConfig.Items.shield.energyDamage)) return;
        drawEnergyHit(stack, 1);
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) nbt = new NBTTagCompound();
        nbt.setLong("lastHit", e.world.getTotalWorldTime());

        stack.setTagCompound(nbt);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void setDamage(ItemStack stack, int amount) {
    }

    @Override
    @Nonnull
    public Item setMaxDamage(int amount) {
        return this;
    }

    @Override
    public void onArmorTick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull ItemStack itemStack) {
        long tick = world.getTotalWorldTime();
        NBTTagCompound nbt = itemStack.getTagCompound();
        if (nbt == null) nbt = new NBTTagCompound();

        boolean isThereChange = false;

        if (!world.isRemote) {
            if (tick % 20 == 0) {
                if (getEnergyStored(itemStack) >= JSGConfig.Items.shield.energyPerTick) {
                    drawEnergyTick(itemStack);
                    // Resistance
                    player.addPotionEffect(new PotionEffect(Objects.requireNonNull(Potion.getPotionFromResourceLocation("resistance")), (20 * 2), 255, true, false));

                    // Hunger thing
                    long time = nbt.getLong("timeUpdated");
                    long seconds = nbt.getLong("secondsWear");
                    if ((tick - time) > 20 * 3) {
                        // Not wearing before - reset
                        seconds = 0;
                    } else {
                        // Wearing before - count and effect
                        seconds++;
                        if (seconds > (60L * JSGConfig.Items.shield.hungryAfter)) {
                            // Hunger
                            player.addPotionEffect(new PotionEffect(Objects.requireNonNull(Potion.getPotionFromResourceLocation("hunger")), (int) (20 * (60 * JSGConfig.Items.shield.hungryLength)), 2, true, false));
                        }
                    }
                    time = tick;
                    nbt.setLong("timeUpdated", time);
                    nbt.setLong("secondsWear", seconds);
                    isThereChange = true;
                }
            }
        }
        if (isThereChange)
            itemStack.setTagCompound(nbt);
    }
}
