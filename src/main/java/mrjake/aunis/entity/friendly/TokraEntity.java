package mrjake.aunis.entity.friendly;

import mrjake.aunis.entity.AunisEnergyProjectile;
import mrjake.aunis.item.AunisItems;
import mrjake.aunis.item.tools.EnergyWeapon;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TokraEntity extends EntityVillager implements IRangedAttackMob, INpc {

    public TokraEntity(World worldIn) {
        super(worldIn);
        this.setSize(0.6F, 1.95F);
        ((PathNavigateGround) this.getNavigator()).setBreakDoors(true);
        this.setCanPickUpLoot(true);

        Random rand = new Random();
        if (rand.nextInt(100) < 25)
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(AunisItems.STAFF));
        else
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(AunisItems.ZAT));
    }

    @Override
    protected boolean canEquipItem(ItemStack stack) {
        return (stack.getItem() != Items.EGG || !this.isChild() || !this.isRiding()) && super.canEquipItem(stack);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onStruckByLightning(EntityLightningBolt lightningBolt) {
    }

    @Override
    @ParametersAreNonnullByDefault
    public TokraEntity createChild(EntityAgeable entity) {
        return null;
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void updateEquipmentIfNeeded(EntityItem itemEntity) {
        ItemStack itemstack = itemEntity.getItem();
        EntityEquipmentSlot entityequipmentslot = getSlotForItemStack(itemstack);
        boolean flag = true;
        ItemStack itemstack1 = this.getItemStackFromSlot(entityequipmentslot);

        if (!itemstack1.isEmpty()) {
            if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.HAND) {
                if (itemstack.getItem() instanceof ItemSword && !(itemstack1.getItem() instanceof ItemSword)) {
                    flag = true;
                } else if (itemstack.getItem() instanceof ItemSword && itemstack1.getItem() instanceof ItemSword) {
                    ItemSword itemsword = (ItemSword) itemstack.getItem();
                    ItemSword itemsword1 = (ItemSword) itemstack1.getItem();

                    if (itemsword.getAttackDamage() == itemsword1.getAttackDamage()) {
                        flag = itemstack.getMetadata() > itemstack1.getMetadata() || itemstack.hasTagCompound() && !itemstack1.hasTagCompound();
                    } else {
                        flag = itemsword.getAttackDamage() > itemsword1.getAttackDamage();
                    }
                } else if (itemstack.getItem() instanceof ItemBow && itemstack1.getItem() instanceof ItemBow) {
                    flag = itemstack.hasTagCompound() && !itemstack1.hasTagCompound();
                } else {
                    flag = false;
                }
            } else if (itemstack.getItem() instanceof ItemArmor && !(itemstack1.getItem() instanceof ItemArmor)) {
                flag = true;
            } else if (itemstack.getItem() instanceof ItemArmor && itemstack1.getItem() instanceof ItemArmor && !EnchantmentHelper.hasBindingCurse(itemstack1)) {
                ItemArmor itemarmor = (ItemArmor) itemstack.getItem();
                ItemArmor itemArmor1 = (ItemArmor) itemstack1.getItem();

                if (itemarmor.damageReduceAmount == itemArmor1.damageReduceAmount) {
                    flag = itemstack.getMetadata() > itemstack1.getMetadata() || itemstack.hasTagCompound() && !itemstack1.hasTagCompound();
                } else {
                    flag = itemarmor.damageReduceAmount > itemArmor1.damageReduceAmount;
                }
            } else {
                flag = false;
            }
        }

        if (flag && this.canEquipItem(itemstack)) {
            double d0;

            switch (entityequipmentslot.getSlotType()) {
                case HAND:
                    d0 = this.inventoryHandsDropChances[entityequipmentslot.getIndex()];
                    break;
                case ARMOR:
                    d0 = this.inventoryArmorDropChances[entityequipmentslot.getIndex()];
                    break;
                default:
                    d0 = 0.0D;
            }

            if (!itemstack1.isEmpty() && (double) (this.rand.nextFloat() - 0.1F) < d0) {
                this.entityDropItem(itemstack1, 0.0F);
            }

            this.setItemStackToSlot(entityequipmentslot, itemstack);

            switch (entityequipmentslot.getSlotType()) {
                case HAND:
                    this.inventoryHandsDropChances[entityequipmentslot.getIndex()] = 2.0F;
                    break;
                case ARMOR:
                    this.inventoryArmorDropChances[entityequipmentslot.getIndex()] = 2.0F;
            }
            this.onItemPickup(itemEntity, itemstack.getCount());
            itemEntity.setDead();
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isTrading() ? SoundEvents.ENTITY_VILLAGER_TRADING : SoundEvents.ENTITY_VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VILLAGER_DEATH;
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackRanged(this, 0.5D, 20, 10.0F));
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 0.5D, false));
        this.tasks.addTask(1, new EntityAITradePlayer(this));
        this.tasks.addTask(1, new EntityAILookAtTradePlayer(this));
        this.tasks.addTask(2, new EntityAIMoveIndoors(this));
        this.tasks.addTask(3, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(4, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.6D));
        this.tasks.addTask(6, new EntityAIVillagerMate(this));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(9, new EntityAIVillagerInteract(this));
        this.tasks.addTask(9, new EntityAIWanderAvoidWater(this, 0.6D));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
        this.tasks.addTask(12, new EntityAIWanderAvoidWater(this, 0.8D));
        this.tasks.addTask(15, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, TokraEntity.class));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityLiving.class, 10, true, false, IMob.MOB_SELECTOR));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
        Map<Item, Float> items = new HashMap<>();
        items.put(Items.LEATHER_HELMET, 0.07F);
        items.put(Items.LEATHER_BOOTS, 0.07F);
        items.put(Items.LEATHER_CHESTPLATE, 0.07F);
        items.put(Items.LEATHER_LEGGINGS, 0.07F);

        Random rand = new Random();
        for (Item item : items.keySet()) {
            if (rand.nextFloat() < items.get(item))
                this.entityDropItem(new ItemStack(item), 0.0F);
        }
        if (rand.nextFloat() < 0.5f) {
            EntitySilverfish goauld = new EntitySilverfish(world);
            goauld.setLocationAndAngles(posX, posY + 0.5D, posZ, rand.nextFloat() * 360.0F, 0.0F);
            world.spawnEntity(goauld);
        }

    }

    @Override
    @ParametersAreNonnullByDefault
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        if (this.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof EnergyWeapon) {
            EnergyWeapon item = (EnergyWeapon) this.getHeldItem(EnumHand.MAIN_HAND).getItem();
            item.playShootSound(world, this);
            world.spawnEntity(AunisEnergyProjectile.createEnergyBall(world, this, target, item));
        }
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
    }

    public float getEyeHeight() {
        return 1.7F;
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        Team team = this.getTeam();
        String s = this.getCustomNameTag();

        if (!s.isEmpty()) {
            TextComponentString textcomponentstring = new TextComponentString(ScorePlayerTeam.formatPlayerName(team, s));
            textcomponentstring.getStyle().setHoverEvent(this.getHoverEvent());
            textcomponentstring.getStyle().setInsertion(this.getCachedUniqueIdString());
            return textcomponentstring;
        } else {
            super.getDisplayName();

            String s1 = "default";
            ITextComponent itextcomponent = new TextComponentTranslation("entity.Tokra." + s1);
            itextcomponent.getStyle().setHoverEvent(this.getHoverEvent());
            itextcomponent.getStyle().setInsertion(this.getCachedUniqueIdString());

            if (team != null) {
                itextcomponent.getStyle().setColor(team.getColor());
            }

            return itextcomponent;
        }
    }
}
