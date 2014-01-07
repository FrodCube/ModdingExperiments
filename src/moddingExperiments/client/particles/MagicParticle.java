package moddingExperiments.client.particles;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

public class MagicParticle extends EntityFX {
	
	protected MagicParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		super(world, x, y, z, vx, vy, vz);
		this.motionX = vx;
		this.motionY = vy;
		this.motionZ = vz;
		this.particleScale = (float) (1.3F + 0.6F * rand.nextGaussian());
		this.particleRed  = this.particleGreen = (float) (0.5F + 0.3F * rand.nextGaussian()) ;
		this.particleBlue = 1.0F;
		this.particleMaxAge = (int) (10 * rand.nextGaussian() + 40);
		this.particleGravity = 0.05F;
	}

}
