package tauri.dev.jsg.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class ParticleBlenderCOBlast extends ParticleBlenderSmoke {
    public float motionY;
    public ParticleBlenderCOBlast(float x, float y, float z, int moduloTicks, int moduloTicksSlower, float motionX, float motionY, float motionZ, RandomizeInterface randomize) {
        super(x, y, z, moduloTicks, moduloTicksSlower, motionX, motionZ, false, randomize);
        this.motionY = motionY;
    }

    @Override
    protected Particle createParticle(World world, double x, float y, double z, double motionX, double motionZ, boolean falling) {
        return new ParticleCOBlast(world, x, y, z, motionX, motionY, motionZ);
    }
}
