package mrjake.aunis.entity;

import mrjake.aunis.item.tools.EnergyWeapon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

public class AunisEnergyProjectile extends EntitySmallFireball {

    public int maxAliveTime = 10;
    public float damage = 1.0f;
    public boolean igniteGround = false;
    public boolean paralyze = false;
    public boolean explode = false;
    public boolean instaKill = false;
    public boolean invisible = false;
    public DamageSource damageSource = null;
    public int explosionPower = 1;
    private int ticksAlive;

    public AunisEnergyProjectile(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ, EnergyWeapon itemIn) {
        super(worldIn, shooter, accelX, accelY, accelZ);
        itemIn.setEnergyBallParams(this);
        setLocationAndAngles(shooter.posX, shooter.posY + 1.5, shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        setPosition(posX, posY, posZ);
        if (invisible) setInvisible(true);
        ticksAlive = 0;
    }

    public static AunisEnergyProjectile createEnergyBall(World worldIn, EntityLivingBase shooter, EnergyWeapon itemIn) {
        Vec3d vector = shooter.getLookVec();
        return new AunisEnergyProjectile(worldIn, shooter, vector.x * 50, vector.y * 50, vector.z * 50, itemIn);
    }

    public static AunisEnergyProjectile createEnergyBall(World worldIn, EntityLivingBase shooter, EntityLivingBase target, EnergyWeapon itemIn) {
        double d0 = target.posY + (double) target.getEyeHeight() - 1.100000023841858D;
        double d1 = target.posX - shooter.posX;
        double d2 = d0 - (shooter.posY + 1);
        double d3 = target.posZ - shooter.posZ;
        float f = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F;
        return new AunisEnergyProjectile(worldIn, shooter, d1, d2 + (double) f, d3, itemIn);
    }

    @Override
    protected boolean isFireballFiery() {
        return igniteGround;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        if (invisible)
            return false;
        else
            return super.isInRangeToRenderDist(distance);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        ticksAlive++;
        if (ticksAlive > maxAliveTime)
            this.setDead();
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            if (damageSource != null) {
                if (result.entityHit != null) {
                    Entity entity = result.entityHit;
                    boolean flag = entity.attackEntityFrom(damageSource, damage);

                    if (entity instanceof EntityItem)
                        entity.setDead();

                    if (flag) {
                        this.applyEnchantments(this.shootingEntity, entity);
                        if (paralyze) {
                            if (entity instanceof EntityLivingBase) {
                                EntityLivingBase e = (EntityLivingBase) entity;
                                if (e.isPotionActive(MobEffects.SLOWNESS) && e.isPotionActive(MobEffects.WEAKNESS))
                                    e.attackEntityFrom(damageSource, Float.MAX_VALUE);
                                else {
                                    e.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 5 * 20));
                                    e.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 5 * 20));
                                    e.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 3 * 20));
                                }
                            }
                        }
                        if (instaKill && !entity.isDead) {
                            entity.attackEntityFrom(damageSource, Float.MAX_VALUE);
                        }
                        if (explode) {
                            entity.setFire(5);
                            world.newExplosion((Entity) null, posX, posY, posZ, (float) explosionPower, igniteGround, true);
                        }
                        if (igniteGround) {
                            entity.setFire(5);
                        }
                    }
                } else {
                    if (igniteGround) {
                        BlockPos blockpos = result.getBlockPos().offset(result.sideHit);
                        if (world.isAirBlock(blockpos)) {
                            world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
                        }
                    }
                }
            }
            this.setDead();
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("ExplosionPower", explosionPower);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("ExplosionPower", 99)) {
            explosionPower = compound.getInteger("ExplosionPower");
        }
    }
}
