package mrjake.aunis.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AunisEnergyProjectile extends EntitySmallFireball {

    public AunisEnergyProjectile(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
        super(worldIn, shooter, accelX, accelY, accelZ);
    }

    public static EntityFireball createEnergyBall(World worldIn, EntityLivingBase shooter) {
        Vec3d vector = shooter.getLookVec();
        AunisEnergyProjectile ball = new AunisEnergyProjectile(worldIn, shooter, vector.x * 50, vector.y * 50, vector.z * 50);
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
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            if (result.entityHit != null) {
                if (!result.entityHit.isImmuneToFire()) {
                    boolean flag = result.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 5.0F);

                    if (flag) {
                        this.applyEnchantments(this.shootingEntity, result.entityHit);
                        result.entityHit.setFire(5);
                    }
                }
            }
            this.setDead();
        }
    }
}
