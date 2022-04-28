package mrjake.aunis.entity;

import mrjake.aunis.AunisDamageSources;
import mrjake.aunis.config.AunisConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

public class AunisEnergyProjectile extends EntitySmallFireball {

    private static final int K = 1;
    public static int MAX_ALIVE_TIME = AunisConfig.toolsConfig.zatDistance * K;
    public int ticksAlive;

    public AunisEnergyProjectile(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
        super(worldIn, shooter, accelX, accelY, accelZ);
        ticksAlive = 0;
    }

    public static AunisEnergyProjectile createEnergyBall(World worldIn, EntityLivingBase shooter) {
        Vec3d vector = shooter.getLookVec();
        AunisEnergyProjectile ball = new AunisEnergyProjectile(worldIn, shooter, vector.x * 50, vector.y * 50, vector.z * 50);
        return setEnergyBall(shooter, ball);
    }

    public static AunisEnergyProjectile createEnergyBall(World worldIn, EntityLivingBase shooter, EntityLivingBase target) {
        double d0 = target.posY + (double) target.getEyeHeight() - 1.100000023841858D;
        double d1 = target.posX - shooter.posX;
        double d2 = d0 - (shooter.posY + 1);
        double d3 = target.posZ - shooter.posZ;
        float f = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F;
        AunisEnergyProjectile ball = new AunisEnergyProjectile(worldIn, shooter, d1, d2 + (double) f, d3);
        return setEnergyBall(shooter, ball);
    }

    private static AunisEnergyProjectile setEnergyBall(EntityLivingBase shooter, AunisEnergyProjectile ball) {
        ball.setLocationAndAngles(shooter.posX, shooter.posY + 1.5, shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        ball.setPosition(ball.posX, ball.posY, ball.posZ);
        ball.setInvisible(true);
        return ball;
    }

    @Override
    protected boolean isFireballFiery() {
        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!world.isRemote) {
            if (AunisConfig.toolsConfig.zatDistance * K != MAX_ALIVE_TIME)
                MAX_ALIVE_TIME = AunisConfig.toolsConfig.zatDistance * K;
        }

        ticksAlive++;
        if (ticksAlive > MAX_ALIVE_TIME)
            this.setDead();
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            if (result.entityHit != null) {
                Entity entity = result.entityHit;
                boolean flag = entity.attackEntityFrom(AunisDamageSources.DAMAGE_ZAT, 1.0f);

                if (flag) {
                    this.applyEnchantments(this.shootingEntity, result.entityHit);
                    if (entity instanceof EntityLivingBase) {
                        EntityLivingBase e = (EntityLivingBase) entity;
                        if (e.isPotionActive(MobEffects.SLOWNESS) && e.isPotionActive(MobEffects.WEAKNESS))
                            e.attackEntityFrom(AunisDamageSources.DAMAGE_ZAT, Float.MAX_VALUE);
                        else {
                            e.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 5 * 20));
                            e.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 5 * 20));
                            e.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 3 * 20));
                        }
                    }
                    else if(entity instanceof EntityItem)
                        entity.setDead();
                }
            }
            this.setDead();
        }
    }
}
