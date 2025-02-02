//
// This file is a part of the Chunk Stories Core codebase
// Check out README.md for more information
// Website: http://chunkstories.xyz
//

package xyz.chunkstories.core.entity;

import xyz.chunkstories.api.animation.Animation;
import xyz.chunkstories.api.animation.CompoundAnimationHelper;
import xyz.chunkstories.api.entity.Entity;
import xyz.chunkstories.api.entity.traits.serializable.TraitHealth;
import xyz.chunkstories.api.entity.traits.serializable.TraitRotation;
import xyz.chunkstories.api.entity.traits.serializable.TraitSelectedItem;
import xyz.chunkstories.api.entity.traits.serializable.TraitVelocity;
import xyz.chunkstories.api.item.Item;
import xyz.chunkstories.api.item.interfaces.ItemCustomHoldingAnimation;
import xyz.chunkstories.api.item.inventory.ItemPile;
import xyz.chunkstories.api.world.World;
import xyz.chunkstories.api.world.WorldClient;
import xyz.chunkstories.core.entity.traits.TraitHumanoidStance;
import xyz.chunkstories.core.entity.traits.TraitHumanoidStance.HumanoidStance;
import xyz.chunkstories.core.entity.traits.MinerTrait;
import xyz.chunkstories.core.item.ItemMiningTool;
import xyz.chunkstories.core.item.BlockMiningOperation;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Arrays;

// TODO port
public class HumanoidSkeletonAnimator extends CompoundAnimationHelper {

	final Entity entity;
	final TraitHealth entityHealth;
	final TraitHumanoidStance stance;
	final TraitRotation entityRotation;
	final TraitVelocity entityVelocity;

	public HumanoidSkeletonAnimator(Entity entity) {
		this.entity = entity;

		this.entityHealth = entity.traits.get(TraitHealth.class);
		this.stance = entity.traits.get(TraitHumanoidStance.class);
		this.entityRotation = entity.traits.get(TraitRotation.class);
		this.entityVelocity = entity.traits.get(TraitVelocity.class);
	}

	@Override
	public Animation getAnimationPlayingForBone(String boneName, double animationTime) {
		if (entityHealth.isDead())
			return entity.getWorld().getGameContext().getContent().getAnimationsLibrary().getAnimation("./animations/human/ded.bvh");

		World world = entity.getWorld();

		if (Arrays.asList(new String[] { "boneArmLU", "boneArmRU", "boneArmLD", "boneArmRD", "boneItemInHand" }).contains(boneName)) {

			Animation r = entity.traits.tryWith(TraitSelectedItem.class, ecs -> {
				ItemPile selectedItemPile = ecs.getSelectedItem();

				if (selectedItemPile != null) {
					// TODO refactor BVH subsystem to enable SkeletonAnimator to also take care of
					// additional transforms
					Item item = selectedItemPile.getItem();

					if (item instanceof ItemMiningTool) {
						MinerTrait trait = entity.traits.get(MinerTrait.class);
						if (trait != null) {
							if (trait.getProgress() != null)
								return world.getGameContext().getContent().getAnimationsLibrary().getAnimation("./animations/human/mining.bvh");
						}
					}

					if (item instanceof ItemCustomHoldingAnimation)
						return world.getGameContext().getContent().getAnimationsLibrary()
								.getAnimation(((ItemCustomHoldingAnimation) item).getCustomAnimationName());
					else
						return world.getGameContext().getContent().getAnimationsLibrary().getAnimation("./animations/human/holding-item.bvh");
				}

				return null;
			});

			if (r != null)
				return r;
		}

		Vector3d vel = entityVelocity.getVelocity();

		// Extract just the horizontal speed from that
		double horizSpd = Math.sqrt(vel.x() * vel.x() + vel.z() * vel.z());

		if (stance.getStance() == HumanoidStance.STANDING) {
			if (horizSpd > 0.065) {
				// System.out.println("running");
				return world.getGameContext().getContent().getAnimationsLibrary().getAnimation("./animations/human/running.bvh");
			}
			if (horizSpd > 0.0)
				return world.getGameContext().getContent().getAnimationsLibrary().getAnimation("./animations/human/walking.bvh");

			return world.getGameContext().getContent().getAnimationsLibrary().getAnimation("./animations/human/standstill.bvh");
		} else if (stance.getStance() == HumanoidStance.CROUCHING) {
			if (horizSpd > 0.0)
				return world.getGameContext().getContent().getAnimationsLibrary().getAnimation("./animations/human/crouched-walking.bvh");

			return world.getGameContext().getContent().getAnimationsLibrary().getAnimation("./animations/human/crouched.bvh");
		} else {
			return world.getGameContext().getContent().getAnimationsLibrary().getAnimation("./animations/human/ded.bvh");
		}

	}

	public Matrix4f getBoneTransformationMatrix(String boneName, double animationTime) {
		Matrix4f characterRotationMatrix = new Matrix4f();
		// Only the torso is modified, the effect is replicated accross the other bones
		// later
		if (boneName.endsWith("boneTorso"))
			characterRotationMatrix.rotate((entityRotation.getHorizontalRotation()) / 180f * 3.14159f, new Vector3f(0, 1, 0));

		Vector3d vel = entityVelocity.getVelocity();

		double horizSpd = Math.sqrt(vel.x() * vel.x() + vel.z() * vel.z());

		animationTime *= 0.75;

		if (boneName.endsWith("boneHead")) {
			Matrix4f modify = new Matrix4f(getAnimationPlayingForBone(boneName, animationTime).getBone(boneName).getTransformationMatrix(animationTime));
			modify.rotate((float) -(entityRotation.getVerticalRotation() / 180 * Math.PI), new Vector3f(1, 0, 0));
			return modify;
		}

		if (horizSpd > 0.030)
			animationTime *= 1.5;

		if (horizSpd > 0.060)
			animationTime *= 1.5;
		else if (Arrays.asList(new String[] { "boneArmLU", "boneArmRU", "boneArmLD", "boneArmRD", "boneItemInHand", "boneTorso" }).contains(boneName)) {

			MinerTrait trait = entity.traits.get(MinerTrait.class);
			if (trait != null && Arrays.asList(new String[] { "boneArmLU", "boneArmLD", "boneItemInHand" }).contains(boneName)) {
				BlockMiningOperation miningProgress = trait.getProgress();
				if (miningProgress != null) {
					Animation lol = entity.getWorld().getGameContext().getContent().getAnimationsLibrary().getAnimation("./animations/human/mining.bvh");

					return characterRotationMatrix
							.mul(lol.getBone(boneName).getTransformationMatrix((System.currentTimeMillis() - miningProgress.getStarted()) * 1.5f));
				}
			}
		}

		ItemPile selectedItem = entity.traits.tryWith(TraitSelectedItem.class, eci -> eci.getSelectedItem());

		if (Arrays.asList("boneArmLU", "boneArmRU").contains(boneName)) {
			float k = (stance.getStance() == HumanoidStance.CROUCHING) ? 0.65f : 0.75f;

			if (selectedItem != null) {
				characterRotationMatrix.translate(new Vector3f(0f, k, 0));
				characterRotationMatrix.rotate(
						-(entityRotation.getVerticalRotation() + ((stance.getStance() == HumanoidStance.CROUCHING) ? -50f : 0f)) / 180f * 3.14159f,
						new Vector3f(1, 0, 0));
				characterRotationMatrix.translate(new Vector3f(0f, -k, 0));

				if (stance.getStance() == HumanoidStance.CROUCHING
						&& entity.equals(((WorldClient) entity.getWorld()).getClient().getPlayer().getControlledEntity()))
					characterRotationMatrix.translate(new Vector3f(-0.25f, -0.2f, 0f));

			}
		}

		if (boneName.equals("boneItemInHand") && selectedItem.getItem() instanceof ItemCustomHoldingAnimation) {
			animationTime = ((ItemCustomHoldingAnimation) selectedItem.getItem()).transformAnimationTime(animationTime);
		}

		return characterRotationMatrix.mul(getAnimationPlayingForBone(boneName, animationTime).getBone(boneName).getTransformationMatrix(animationTime));
	}

	// TODO
	/* public boolean shouldHideBone(RenderingInterface renderingContext, String
	 * boneName) { if (entity.equals(((WorldClient)
	 * entity.getWorld()).getClient().getPlayer().getControlledEntity())) { if
	 * (renderingContext.getCurrentPass().name.startsWith("shadow")) return false;
	 * 
	 * ItemPile selectedItem = entity.traits.tryWith(TraitSelectedItem.class, eci ->
	 * eci.getSelectedItem());
	 * 
	 * if (Arrays.asList("boneArmRU", "boneArmRD").contains(boneName) &&
	 * selectedItem != null) if (selectedItem.getItem() instanceof ItemVoxel ||
	 * selectedItem.getItem() instanceof ItemMiningTool) return true;
	 * 
	 * if (Arrays.asList("boneArmLU", "boneArmRU", "boneArmLD",
	 * "boneArmRD").contains(boneName) && selectedItem != null) return false;
	 * 
	 * return true; } return false; } */
}
