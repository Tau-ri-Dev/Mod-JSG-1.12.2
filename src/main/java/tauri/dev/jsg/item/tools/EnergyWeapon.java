package tauri.dev.jsg.item.tools;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.capability.WeaponCapabilityProvider;
import tauri.dev.jsg.capability.endpoint.ItemEndpointCapability;
import tauri.dev.jsg.entity.JSGEnergyProjectile;
import tauri.dev.jsg.item.renderer.CustomModel;
import tauri.dev.jsg.item.renderer.CustomModelItemInterface;
import tauri.dev.jsg.stargate.power.StargateItemEnergyStorage;
import tauri.dev.jsg.util.main.loader.JSGCreativeTabsHandler;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class EnergyWeapon extends Item implements CustomModelItemInterface {

    private final int maxEnergyStored;
    private final int energyPerShot;
    public String itemName;
    private CustomModel customModel;

    public EnergyWeapon(String itemName, int maxEnergyStored, int energyPerShot) {
        this.itemName = itemName;
        this.maxEnergyStored = maxEnergyStored;
        this.energyPerShot = energyPerShot;

        setRegistryName(new ResourceLocation(JSG.MOD_ID, this.itemName));
        setUnlocalizedName(JSG.MOD_ID + "." + this.itemName);
        setMaxStackSize(1);
        setCreativeTab(JSGCreativeTabsHandler.jsgWeaponsCreativeTab);
    }

    private static void checkNBT(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            initNBT(stack);
        }
    }

    private static void initNBT(ItemStack stack) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setBoolean("scope", false);
        stack.setTagCompound(compound);
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            items.add(new ItemStack(this));

            ItemStack stack = new ItemStack(this);
            StargateItemEnergyStorage energyStorage = (StargateItemEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null);
            if (energyStorage == null) return;
            energyStorage.setEnergyStored(energyStorage.getMaxEnergyStored());
            items.add(stack);
        }
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        if (!world.isRemote) {
            checkNBT(player.getHeldItem(hand));
            ItemStack stack = player.getHeldItem(hand);
            NBTTagCompound compound = stack.getTagCompound();

            if (!player.isSneaking()) {
                StargateItemEnergyStorage energyStorage = (StargateItemEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null);
                if (energyStorage != null && energyStorage.extractEnergy(energyPerShot, true) >= energyPerShot) {
                    playShootSound(world, player);
                    player.getCooldownTracker().setCooldown(this, getWeaponCoolDown());
                    world.spawnEntity(JSGEnergyProjectile.createEnergyBall(world, player, this));
                    energyStorage.extractEnergy(energyPerShot, false);
                }
            } else if (compound != null) {
                compound.setBoolean("scope", !compound.getBoolean("scope"));
                stack.setTagCompound(compound);
            }
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack stack, EntityPlayer player) {
        stack.getCapability(ItemEndpointCapability.ENDPOINT_CAPABILITY, null).removeEndpoint();

        return super.onDroppedByPlayer(stack, player);
    }

    public abstract void playShootSound(World world, Entity entity);

    public abstract int getWeaponCoolDown();

    public abstract DamageSource getDamageSource(Entity source, Entity attacker);

    public void setEnergyBallParams(JSGEnergyProjectile projectile) {
        projectile.maxAliveTime = 5;
        projectile.damage = 5.0F;
        projectile.igniteGround = true;
        projectile.paralyze = false;
        projectile.explode = false;
        projectile.invisible = true;
        projectile.damageSource = getDamageSource(projectile, projectile.shootingEntity);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
                               @Nonnull ITooltipFlag flagIn) {
        tooltip.add(JSG.getInProgress());
        tooltip.add("");
        IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (energyStorage == null) {
            tooltip.add("NBTs are broken! This is a bug!");
            return;
        }

        String energy = String.format("%,d", energyStorage.getEnergyStored());
        String capacity = String.format("%,d", energyStorage.getMaxEnergyStored());

        tooltip.add(energy + " / " + capacity + " RF");

        String energyPercent = String.format("%.2f", energyStorage.getEnergyStored() / (float) energyStorage.getMaxEnergyStored() * 100) + " %";
        tooltip.add(energyPercent);
        tooltip.add("");
        tooltip.add(JSG.proxy.localize("item.jsg.energyWeapon.available_shots") + " " + (int) Math.floor((float) energyStorage.getEnergyStored() / energyPerShot) + "/" + (int) Math.floor((float) energyStorage.getMaxEnergyStored() / energyPerShot));
    }

    @Override
    public void setCustomModel(CustomModel customModel) {
        this.customModel = customModel;
    }

    public ItemCameraTransforms.TransformType getLastTransform() {
        return customModel.lastTransform;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (oldStack.getItem() != newStack.getItem()) {
            if (oldStack.getItem() instanceof EnergyWeapon) {
                if (oldStack.getTagCompound() != null) {
                    NBTTagCompound compound = oldStack.getTagCompound();
                    compound.setBoolean("scope", false);
                    oldStack.setTagCompound(compound);
                }
            }
            if (newStack.getItem() instanceof EnergyWeapon) {
                if (newStack.getTagCompound() != null) {
                    NBTTagCompound compound = newStack.getTagCompound();
                    compound.setBoolean("scope", false);
                    newStack.setTagCompound(compound);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new WeaponCapabilityProvider(stack, nbt, maxEnergyStored);
    }

    @Override
    public boolean showDurabilityBar(@Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (energyStorage == null) return 1;
        return 1 - (energyStorage.getEnergyStored() / (double) energyStorage.getMaxEnergyStored());
    }
}
