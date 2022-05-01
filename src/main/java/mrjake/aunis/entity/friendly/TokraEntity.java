package mrjake.aunis.entity.friendly;

import mrjake.aunis.entity.AunisEnergyProjectile;
import mrjake.aunis.entity.AunisTradeableEntity;
import mrjake.aunis.entity.ai.AunisLookAtTradePlayerAI;
import mrjake.aunis.entity.ai.AunisTradePlayerAI;
import mrjake.aunis.entity.trading.ITradeList;
import mrjake.aunis.item.AunisItems;
import mrjake.aunis.item.tools.EnergyWeapon;
import mrjake.aunis.util.AunisItemStackHandler;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TokraEntity extends AunisTradeableEntity implements IRangedAttackMob, INpc {

    public static final int MAIN_HAND_SLOT = 0;
    public static final int OFF_HAND_SLOT = 1;
    public final AunisItemStackHandler ITEM_HANDLER = new AunisItemStackHandler(12);
    private final ItemStack ZAT_ITEM = new ItemStack(AunisItems.ZAT);
    private final ItemStack STAFF_ITEM = new ItemStack(AunisItems.STAFF);
    private final Random RANDOM = new Random();
    @Nullable
    private MerchantRecipeList buyingList;
    @Nullable
    private EntityPlayer buyingPlayer;

    public TokraEntity(World worldIn) {
        super(worldIn);
        this.setSize(0.6F, 1.95F);
        ((PathNavigateGround) this.getNavigator()).setBreakDoors(true);
        this.setCanPickUpLoot(true);
        initTokra();
    }

    private void initTokra() {
        if (RANDOM.nextInt(100) < 2) // 2% chance
            ITEM_HANDLER.setStackInSlot(MAIN_HAND_SLOT, STAFF_ITEM);
        else
            ITEM_HANDLER.setStackInSlot(MAIN_HAND_SLOT, ZAT_ITEM);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackRanged(this, 0.5D, 20, 10.0F));
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 0.5D, false));
        this.tasks.addTask(1, new AunisTradePlayerAI(this));
        this.tasks.addTask(1, new AunisLookAtTradePlayerAI(this));
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
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public boolean isTrading() {
        return this.buyingPlayer != null;
    }

    @Override
    public void populateBuyingList() {
        if (this.buyingList == null) {
            this.buyingList = new MerchantRecipeList();
        }
        List<ITradeList> trades = getTrades();

        if (trades != null) {
            for (ITradeList trade : trades) {
                trade.addMerchantRecipe(this, this.buyingList, this.rand);
            }
        }
    }

    @Override
    public List<ITradeList> getTrades() {
        return null;
    }

    @Override
    @Nullable
    public IEntityLivingData onInitialSpawn(@Nonnull DifficultyInstance difficulty, @Nullable IEntityLivingData livingData) {
        return livingData;
    }

    @Override
    protected void updateAITasks() {
    }

    @Nonnull
    @Override
    public EnumHandSide getPrimaryHand() {
        return EnumHandSide.RIGHT;
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
    public boolean processInteract(EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);

        if (itemstack.getItem() == Items.NAME_TAG) {
            itemstack.interactWithEntity(player, this, hand);
            return true;
        } else if (this.isEntityAlive() && !this.isTrading() && !player.isSneaking()) {
            if (this.buyingList == null) {
                this.populateBuyingList();
            }

            if (hand == EnumHand.MAIN_HAND) {
                player.addStat(StatList.TALKED_TO_VILLAGER);
            }

            if (!this.world.isRemote && !this.buyingList.isEmpty()) {
                this.setCustomer(player);
                player.displayVillagerTradeGui(this);
            } else if (this.buyingList.isEmpty()) {
                return super.processInteract(player, hand);
            }

            return true;
        } else {
            return super.processInteract(player, hand);
        }
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

    @Override
    public float getEyeHeight() {
        return 1.7F;
    }

    @Nullable
    @Override
    public EntityPlayer getCustomer() {
        return this.buyingPlayer;
    }

    @Override
    public void setCustomer(@Nullable EntityPlayer player) {
        this.buyingPlayer = player;
    }

    @Nullable
    @Override
    public MerchantRecipeList getRecipes(@Nonnull EntityPlayer player) {
        return null;
    }

    @Override
    public void setRecipes(@Nullable MerchantRecipeList recipeList) {

    }

    @Override
    public void useRecipe(@Nonnull MerchantRecipe recipe) {

    }

    @Override
    public void verifySellingItem(@Nonnull ItemStack stack) {

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

    @Override
    public World getWorld() {
        return null;
    }

    @Override
    public BlockPos getPos() {
        return null;
    }

    @Override
    protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
        for (int i = 0; i < ITEM_HANDLER.getSize(); i++) {
            entityDropItem(ITEM_HANDLER.getStackInSlot(i), 0f);
        }
    }

    @Override
    public boolean replaceItemInInventory(int inventorySlot, @Nonnull ItemStack itemStackIn) {
        if (inventorySlot < 0 || inventorySlot >= ITEM_HANDLER.getSize()) return false;
        ITEM_HANDLER.setStackInSlot(inventorySlot, itemStackIn);
        return true;
    }

    @Override
    public boolean getCanSpawnHere() {
        return false;
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, @Nonnull ItemStack stack) {
        int slotIndex = slotIn.getSlotIndex();
        if (slotIn.getSlotType() != EntityEquipmentSlot.Type.HAND)
            slotIndex += 2;
        if (slotIndex >= ITEM_HANDLER.getSize()) return;
        ITEM_HANDLER.setStackInSlot(slotIndex, stack);
    }

    @Nonnull
    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
        int slotIndex = slotIn.getSlotIndex();
        if (slotIn.getSlotType() != EntityEquipmentSlot.Type.HAND)
            slotIndex += 2;
        if (slotIndex >= ITEM_HANDLER.getSize()) return ItemStack.EMPTY;

        return ITEM_HANDLER.getStackInSlot(slotIndex);
    }

    private void updateItemHeld() {
        if (!this.world.isRemote) {
            this.setHeldItemMainhand(ITEM_HANDLER.getStackInSlot(MAIN_HAND_SLOT));
            this.setHeldItemOffhand(ITEM_HANDLER.getStackInSlot(OFF_HAND_SLOT));
        }
    }

    @Override
    protected void updateEquipmentIfNeeded(EntityItem itemEntity) {
        ItemStack stack = itemEntity.getItem();
        if (!stack.isEmpty()) {
            for (int i = 0; i < ITEM_HANDLER.getSize(); i++) {
                ItemStack inventoryStack = ITEM_HANDLER.getStackInSlot(i);
                if (inventoryStack.isEmpty()) {
                    onItemPickup(itemEntity, stack.getCount());
                    ITEM_HANDLER.setStackInSlot(i, stack);
                    itemEntity.setDead();
                    break;
                }
            }
        }
        this.updateItemHeld();
    }

    @Nonnull
    @Override
    public ItemStack getHeldItemMainhand() {
        return getHeldItem(EnumHand.MAIN_HAND);
    }

    public void setHeldItemMainhand(ItemStack stack) {
        this.setHeldItem(EnumHand.MAIN_HAND, stack);
    }

    @Nonnull
    @Override
    public ItemStack getHeldItemOffhand() {
        return getHeldItem(EnumHand.OFF_HAND);
    }

    public void setHeldItemOffhand(ItemStack stack) {
        this.setHeldItem(EnumHand.OFF_HAND, stack);
    }

    @Nonnull
    @Override
    public ItemStack getHeldItem(@Nonnull EnumHand hand) {
        if (hand == EnumHand.MAIN_HAND) {
            return this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
        } else if (hand == EnumHand.OFF_HAND) {
            return this.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
        } else {
            throw new IllegalArgumentException("Invalid hand " + hand);
        }
    }

    @Override
    public void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("Items")) {
            NBTTagList nbtList = compound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < nbtList.tagCount(); i++) {
                NBTTagCompound comp = nbtList.getCompoundTagAt(i);
                int slot = comp.getInteger("Slot");
                if (slot >= 0 && slot < ITEM_HANDLER.getSize()) {
                    ITEM_HANDLER.setStackInSlot(slot, new ItemStack(comp));
                }
            }
        }
        this.updateItemHeld();
    }

    @Override
    public void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        NBTTagList nbtList = new NBTTagList();
        for (int i = 0; i < ITEM_HANDLER.getSize(); i++) {
            ItemStack stack = ITEM_HANDLER.getStackInSlot(i);
            if (!stack.isEmpty()) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("Slot", i);
                stack.writeToNBT(tag);
                nbtList.appendTag(tag);
            }
        }
        compound.setTag("Items", nbtList);
    }
}
