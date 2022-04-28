package mrjake.aunis.entity.friendly;

import mrjake.aunis.entity.AunisEnergyProjectile;
import mrjake.aunis.entity.EntityRegister;
import mrjake.aunis.item.AunisItems;
import mrjake.aunis.item.tools.zat.ZatItem;
import mrjake.aunis.sound.SoundEventEnum;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TokraEntity extends EntityAgeable implements IRangedAttackMob, INpc {

    public TokraEntity(World worldIn) {
        super(worldIn);
        this.setSize(0.6F, 1.95F);
        ((PathNavigateGround)this.getNavigator()).setBreakDoors(true);
        this.setCanPickUpLoot(true);
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(AunisItems.ZAT));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onStruckByLightning(EntityLightningBolt lightningBolt) {}

    @Override
    @ParametersAreNonnullByDefault
    public TokraEntity createChild(EntityAgeable ageable) {
        TokraEntity youngTokra = new TokraEntity(this.world);
        youngTokra.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(youngTokra)), null);
        return youngTokra;
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void updateEquipmentIfNeeded(EntityItem itemEntity) {
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackRanged(this, 0.8D, 20, 10.0F));
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, false));
        this.tasks.addTask(2, new EntityAIMoveIndoors(this));
        this.tasks.addTask(3, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(4, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.6D));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
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
        EntityRegister.playSoundEvent(SoundEventEnum.ZAT_SHOOT, this);
        this.world.spawnEntity(AunisEnergyProjectile.createEnergyBall(world, this, target, AunisItems.ZAT));
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
