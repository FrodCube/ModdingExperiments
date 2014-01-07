package moddingExperiments.client.particles;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

public class ScrambledParticle extends EntityFX {
	
	protected ScrambledParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		super(world, x, y, z, vx, vy, vz);
		this.motionX = vx;
		this.motionY = vy;
		this.motionZ = vz;
		this.particleScale = (float) (1.3F + 0.2F * rand.nextGaussian());
		this.particleRed = 1.0F;
		this.particleGreen = (float) (0.5F + 0.4F * rand.nextGaussian()) ;
		this.particleBlue = 0.0F;
		this.particleMaxAge = (int) (10 * rand.nextGaussian() + 40);
		this.particleGravity = 0.25F;
	}
	
//	@Override
//	public void onUpdate() {
//		this.prevPosX = this.posX;
//        this.prevPosY = this.posY;
//        this.prevPosZ = this.posZ;
//
//        if (this.particleAge++ >= this.particleMaxAge) {
//            this.setDead();
//        }
//
//		this.motionY -= 0.04D * (double) this.particleGravity;
//		
//        this.posX += this.motionX;
//        this.posY += this.motionY;
//        this.posZ += this.motionZ;
//        
////        this.onGround = d7 != par3 && d7 < 0.0D;
//        
//        this.motionX *= 0.95;
//        this.motionY *= 0.95;
//        this.motionZ *= 0.95;
//        
//        if (this.onGround) {
//			this.motionY *= -1D;
//		}
//	}

}
