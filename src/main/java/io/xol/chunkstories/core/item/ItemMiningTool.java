//
// This file is a part of the Chunk Stories API codebase
// Check out README.md for more information
// Website: http://chunkstories.xyz
//

package io.xol.chunkstories.core.item;

import org.joml.Matrix4f;

import io.xol.chunkstories.api.Location;
import io.xol.chunkstories.api.entity.Entity;
import io.xol.chunkstories.api.entity.components.EntityInventory;
import io.xol.chunkstories.api.item.Item;
import io.xol.chunkstories.api.item.ItemDefinition;
import io.xol.chunkstories.api.item.inventory.ItemPile;
import io.xol.chunkstories.api.rendering.RenderingInterface;
import io.xol.chunkstories.api.rendering.item.ItemRenderer;
import io.xol.chunkstories.api.world.World;
import io.xol.chunkstories.core.entity.traits.MinerTrait;
import io.xol.chunkstories.core.item.renderer.FlatIconItemRenderer;
import io.xol.chunkstories.core.item.renderer.ItemModelRenderer;

public class ItemMiningTool extends Item implements MiningTool {

	public final String toolType;
	public final float miningEfficiency;

	public final long animationCycleDuration;

	//public static MiningProgress myProgress;

	public ItemMiningTool(ItemDefinition type) {
		super(type);

		this.toolType = type.resolveProperty("toolType", "pickaxe");
		this.miningEfficiency = Float.parseFloat(type.resolveProperty("miningEfficiency", "0.5"));

		this.animationCycleDuration = Long.parseLong(type.resolveProperty("animationCycleDuration", "500"));
	}
	
	/** Some weapons have fancy renderers */
	public ItemRenderer getCustomItemRenderer(ItemRenderer fallbackRenderer)
	{
		ItemRenderer itemRenderer;
		
		String modelName = getDefinition().resolveProperty("model", "none");
		if (!modelName.equals("none"))
			itemRenderer = new ItemModelRenderer(this, fallbackRenderer, modelName, getDefinition().resolveProperty("modelDiffuse", "none")) {

				@Override
				public void renderItemInWorld(RenderingInterface renderingContext, ItemPile pile, World world,
						Location location, Matrix4f handTransformation) {
					
					boolean mining = false;
					if(pile.getInventory() instanceof EntityInventory) {
						Entity entity = ((EntityInventory)pile.getInventory()).entity;
						//System.out.println(entity);
						MinerTrait miningTrait = entity.traits.get(MinerTrait.class);
						if(miningTrait != null) {
							if(miningTrait.getProgress() != null)
								mining = true;
						}
					}
					
					if(mining) {
						handTransformation.rotate((float)Math.PI , 0, 0, 1);
					}
					handTransformation.rotate((float)Math.PI * 1.5f, 0, 1, 0);
					handTransformation.translate(0, -0.2f, 0);
					handTransformation.scale(0.5f);
					
					super.renderItemInWorld(renderingContext, pile, world, location, handTransformation);
				}
		};
		else
			itemRenderer = new FlatIconItemRenderer(this, fallbackRenderer, getDefinition());
		
		return itemRenderer;
	}

	@Override
	public void tickInHand(Entity owner, ItemPile itemPile) {
		/*World world = owner.getWorld();
		if (owner instanceof EntityControllable && owner instanceof EntityWorldModifier) {
			EntityControllable entityControllable = (EntityControllable) owner;
			Controller controller = entityControllable.getController();

			if (controller != null && controller instanceof Player) {
				InputsManager inputs = controller.getInputsManager();

				Location lookingAt = entityControllable.getBlockLookingAt(true);

				if (lookingAt != null && lookingAt.distance(owner.getLocation()) > 7f)
					lookingAt = null;

				if (inputs.getInputByName("mouse.left").isPressed() && lookingAt != null) {

					WorldCell cell = world.peekSafely(lookingAt);

					// Cancel mining if looking away or the block changed by itself
					if (lookingAt == null || (progress != null && (lookingAt.distance(progress.loc) > 0 || !cell.getVoxel().sameKind(progress.voxel)))) {
						progress = null;
					}

					if (progress == null) {
						// Try starting mining something
						if (lookingAt != null)
							progress = new MiningProgress(world.peekSafely(lookingAt), this);
					} else {
						progress.keepGoing(entityControllable, controller);
					}
				} else {
					progress = null;
				}

				Player player = (Player) controller;
				if (player.getContext() instanceof ClientInterface) {
					Player me = ((ClientInterface) player.getContext()).getPlayer();
					if (me.equals(player)) {
						myProgress = progress;
					}
				}
			}
		}*/

	}

	/*@Override
	public ItemRenderer getCustomItemRenderer(ItemRenderer fallbackRenderer) {
		return new SwingToolRenderer(fallbackRenderer);
	}

	class SwingToolRenderer extends ItemRenderer {

		public SwingToolRenderer(ItemRenderer fallbackRenderer) {
			super(fallbackRenderer);
		}

		@Override
		public void renderItemInWorld(RenderingInterface renderingInterface, ItemPile pile, World world, Location location, Matrix4f transformation) {

			MinerTrait trait = null;
			Inventory inv = pile.getInventory();
			if(inv instanceof EntityInventory)
				trait = ((EntityInventory)inv).entity.traits.get(MinerTrait.class);
				
			if(trait != null){
				Matrix4f rotated = new Matrix4f(transformation);

				Vector3f center = new Vector3f(0.0f, -0.0f, -100f);

				rotated.translate(0.05f, -0.1f, 0f);
				MiningProgress progress = trait.getProgress();

				if (progress != null) {
					long elapsed = System.currentTimeMillis() - progress.started;
					float elapsedd = (float) elapsed;
					elapsedd /= (float) animationCycleDuration;

					if (elapsedd >= progress.timesSoundPlayed && elapsed > 50) {
						world.getSoundManager().playSoundEffect("sounds/gameplay/voxel_remove.ogg", Mode.NORMAL, progress.context.getLocation(), 1.5f, 1.0f);
						progress.timesSoundPlayed++;
					}

					float swingCycle = (float) Math.sin(Math.PI * 2 * elapsedd + Math.PI);

					rotated.translate(center);
					rotated.rotate((float) (swingCycle), 0f, 0f, 1f);

					center.negate();
					rotated.translate(center);
				}

				// rotated.rotate((System.currentTimeMillis() % 100000) / 10000f, 0f, 0f, 1f);
				rotated.scale(2.0f);

				super.renderItemInWorld(renderingInterface, pile, world, location, rotated);

			} else
				super.renderItemInWorld(renderingInterface, pile, world, location, transformation);
		}
	}*/

	@Override
	public float getMiningEfficiency() {
		return this.miningEfficiency;
	}

	@Override
	public String getToolTypeName() {
		return this.toolType;
	}
}
