//
// This file is a part of the Chunk Stories Core codebase
// Check out README.md for more information
// Website: http://chunkstories.xyz
//

package xyz.chunkstories.core.particles;

/*import xyz.chunkstories.api.particles.*;
import xyz.chunkstories.api.world.World;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3fc;

public class ParticleFire extends ParticleTypeHandler {
	final boolean ignoreCollisions;
	final boolean destroyOnCollision;

	public ParticleFire(ParticleTypeDefinition type) {
		super(type);

		ignoreCollisions = type.resolveProperty("ignoreCollisions", "false").equals("true");
		destroyOnCollision = type.resolveProperty("destroyOnCollision", "true").equals("true");
	}

	public class ParticleFireData extends ParticleData implements ParticleDataWithVelocity, ParticleDataWithTextureCoordinates {

		public int timer = 60 * 60;
		public float temp = 7000;
		Vector3d vel = new Vector3d();
		int decay;

		public ParticleFireData(float x, float y, float z) {
			super(x, y, z);

			decay = 15 + (int) (Math.random() * 5);
		}

		public void setVelocity(Vector3dc vel) {
			this.vel.set(vel);
		}

		@Override
		public float getTextureCoordinateXTopLeft() {
			// TODO Auto-generated method stub
			return temp;
		}

		@Override
		public float getTextureCoordinateXTopRight() {
			// TODO Auto-generated method stub
			return temp;
		}

		@Override
		public float getTextureCoordinateXBottomLeft() {
			// TODO Auto-generated method stub
			return temp;
		}

		@Override
		public float getTextureCoordinateXBottomRight() {
			// TODO Auto-generated method stub
			return temp;
		}

		@Override
		public float getTextureCoordinateYTopLeft() {
			return 0;
		}

		@Override
		public float getTextureCoordinateYTopRight() {
			return 0;
		}

		@Override
		public float getTextureCoordinateYBottomLeft() {
			return 0;
		}

		@Override
		public float getTextureCoordinateYBottomRight() {
			return 0;
		}

		@Override
		public void setVelocity(Vector3fc vel) {
			this.vel.set(vel);
		}
	}

	@Override
	public ParticleData createNew(World world, float x, float y, float z) {
		return new ParticleFireData(x, y, z);
	}

	@Override
	public void forEach_Physics(World world, ParticleData data) {
		ParticleFireData b = (ParticleFireData) data;

		b.timer--;
		b.x = ((float) (b.x() + b.vel.x()));
		b.y = ((float) (b.y() + b.vel.y()));
		b.z = ((float) (b.z() + b.vel.z()));

		if (!b.isCollidingAgainst(world))
			// if (!((WorldImplementation) world).checkCollisionPoint(b.x(), b.y(), b.z())
			// && !ignoreCollisions)
			b.vel.y = (b.vel.y() + 0.02 / 60.0);
		else
			b.vel.set(0d, 0d, 0d);

		// 60th square of 0.5
		// b.vel.scale(0.98581402);

		// b.vel.scale(0.95129708668990249416970880243486);

		b.vel.mul(0.93);

		if (b.vel.length() < 0.1 / 60.0)
			b.vel.set(0d, 0d, 0d);

		if (b.temp > 3000)
			b.temp -= 10 + b.decay;
		else if (b.temp > 0)
			b.temp -= b.decay;
		else if (b.temp <= 0) {
			b.destroy();
			b.temp = 1;
		}

		if (b.isCollidingAgainst(world) && destroyOnCollision)
			// if(((WorldImplementation) world).checkCollisionPoint(b.x(), b.y(), b.z()) &&
			// destroyOnCollision)
			b.destroy();
	}
}*/