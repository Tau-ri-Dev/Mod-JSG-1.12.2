package mrjake.aunis.entity.friendly;

import mrjake.aunis.entity.EntityRegister;
import mrjake.aunis.sound.SoundEventEnum;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TokraEntity extends EntityVillager implements IRangedAttackMob {

    public TokraEntity(World worldIn) {
        super(worldIn, 0);
    }

    public static void registerFixesTokra(DataFixer fixer) {
        EntityLiving.registerFixesMob(fixer, TokraEntity.class);
    }

    @Override
    public void onStruckByLightning(EntityLightningBolt lightningBolt) {
        if (!this.world.isRemote && !this.isDead) {
        }
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
        super.setItemStackToSlot(slotIn, stack);
    }

    @Override
    public TokraEntity createChild(EntityAgeable ageable) {
        TokraEntity youngTokra = new TokraEntity(this.world);
        youngTokra.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(youngTokra)), null);
        return youngTokra;
    }

    @Override
    protected void updateEquipmentIfNeeded(EntityItem itemEntity) {
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAITradePlayer(this));
        this.tasks.addTask(1, new EntityAIAttackRanged(this, 0.8D, 20, 10.0F));
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, false));
        this.tasks.addTask(1, new EntityAILookAtTradePlayer(this));
        this.tasks.addTask(2, new EntityAIMoveIndoors(this));
        this.tasks.addTask(3, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(4, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.6D));
        this.tasks.addTask(6, new EntityAIVillagerMate(this));
        this.tasks.addTask(7, new EntityAIFollowGolem(this));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(9, new EntityAIVillagerInteract(this));
        this.tasks.addTask(9, new EntityAIWanderAvoidWater(this, 0.6D));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
        this.tasks.addTask(11, new EntityAIAttackMelee(this, 1.0D, false));
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

    protected EntityArrow getArrow(float distanceFactor) {
        EntityTippedArrow entitytippedarrow = new EntityTippedArrow(this.world, this);
        entitytippedarrow.setEnchantmentEffectsFromEntity(this, distanceFactor);
        return entitytippedarrow;
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        // todo(Mine): make goauld staff projectile entity
        EntityArrow arrow = getArrow(distanceFactor);
        double d0 = target.posY + (double) target.getEyeHeight() - 1.100000023841858D;
        double d1 = target.posX - this.posX;
        double d2 = d0 - arrow.posY;
        double d3 = target.posZ - this.posZ;
        float f = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F;
        arrow.shoot(d1, d2 + (double) f, d3, 1.6F, 12.0F);
        EntityRegister.playSoundEvent(SoundEventEnum.ZAT_SHOOT, this);
        this.world.spawnEntity(arrow);
    }

    public float getEyeHeight() {
        return 1.7F;
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
    }

    @Override
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
