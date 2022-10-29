package tauri.dev.jsg.particle;

import net.minecraft.client.particle.ParticleExplosion;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ParticleAtoms extends ParticleExplosion {
    private final float portalParticleScale;
    private final double portalPosX;
    private final double portalPosY;
    private final double portalPosZ;

    public ParticleAtoms(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.portalPosX = this.posX;
        this.portalPosY = this.posY;
        this.portalPosZ = this.posZ;
        float f = this.rand.nextFloat() * 0.6F + 0.4F;
        this.particleScale = this.rand.nextFloat() * 0.2F + 0.5F;
        this.portalParticleScale = this.particleScale;
        this.particleRed = f;
        this.particleGreen = f;
        this.particleBlue = f;
        this.particleMaxAge = (int) (Math.random() * 10.0D) + 40;
        this.particleScale = (float) (0.8f + (Math.random() * 1));
    }

    @Override
    public void move(double x, double y, double z) {
        this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
        this.resetPositionToBB();
    }

    @Override
    public void renderParticle(@Nonnull BufferBuilder buffer, @Nonnull Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float f = ((float) this.particleAge + partialTicks) / (float) this.particleMaxAge;
        f = 1.0F - f;
        f = f * f;
        f = 1.0F - f;
        this.particleScale = this.portalParticleScale * f;
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    public int getBrightnessForRender(float p_189214_1_) {
        int i = super.getBrightnessForRender(p_189214_1_);
        float f = (float) this.particleAge / (float) this.particleMaxAge;
        f = f * f;
        f = f * f;
        int j = i & 255;
        int k = i >> 16 & 255;
        k = k + (int) (f * 15.0F * 16.0F);

        if (k > 240) {
            k = 240;
        }

        return j | k << 16;
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        float f = (float) this.particleAge / (float) this.particleMaxAge;
        float f1 = -f + f * f * 2.0F;
        float f2 = 1.0F - f1;
        this.posX = this.portalPosX + this.motionX * (double) f2;
        this.posY = this.portalPosY + this.motionY * (double) f2 + (double) (1.0F - f);
        this.posZ = this.portalPosZ + this.motionZ * (double) f2;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
    }
}
