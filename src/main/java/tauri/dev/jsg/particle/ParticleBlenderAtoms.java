package tauri.dev.jsg.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class ParticleBlenderAtoms extends ParticleBlender {

	public ParticleBlenderAtoms(float x, float y, float z, int moduloTicks, int moduloTicksSlower, float motionX, float motionZ, boolean falling, RandomizeInterface randomize) {
		super(x, y, z, moduloTicks, moduloTicksSlower, motionX, motionZ, falling, randomize);
	}

	public ParticleBlenderAtoms(float x, float y, float z, int moduloTicks, int moduloTicksSlower, boolean falling, RandomizeInterface randomize) {
		super(x, y, z, moduloTicks, moduloTicksSlower, falling, randomize);
	}

	@Override
	protected Particle createParticle(World world, double x, float y, double z, double motionX, double motionZ, boolean falling) {
		return new ParticleAtoms(world, x, y, z, motionX, 0, motionZ);
	}

}
