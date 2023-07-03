package tauri.dev.jsg.particle;

import net.minecraft.world.World;

public class ParticleCOBlast extends ParticleWhiteSmoke {

    public ParticleCOBlast(World world, double x, double y, double z, double motionX, double motionY, double motionZ, boolean orange) {
        super(world, x, y, z, motionX, motionZ, false);

        float f = this.rand.nextFloat() * 0.3F + 0.35F;
        this.particleRed = f;
        this.particleGreen = f;
        this.particleBlue = f;

        if (orange) {
            this.particleRed = Math.min(1, f * 2f);
            this.particleGreen = Math.min(1, f * 1.4f);
            this.particleBlue = Math.min(1, f * 1.1f);
        }

        this.motionY = motionY;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }

        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.8999999761581421D;
        this.motionY *= 0.8999999761581421D;
        this.motionZ *= 0.8999999761581421D;

        if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }
}
