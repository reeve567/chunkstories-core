//
// This file is a part of the Chunk Stories Core codebase
// Check out README.md for more information
// Website: http://chunkstories.xyz
//

package xyz.chunkstories.core.voxel

import org.joml.Matrix4f
import xyz.chunkstories.api.graphics.MeshMaterial
import xyz.chunkstories.api.graphics.representation.Model
import xyz.chunkstories.api.voxel.MiningTool
import xyz.chunkstories.api.voxel.Voxel
import xyz.chunkstories.api.voxel.VoxelDefinition
import xyz.chunkstories.api.voxel.VoxelSide
import xyz.chunkstories.api.world.cell.EditableCell

class VoxelSmallPlant(definition: VoxelDefinition) : Voxel(definition) {
	val model: Model = definition.store.parent.models["voxels/blockmodels/grass_prop/grass_prop.dae"]

	init {

		val mappedOverrides = mapOf(0 to MeshMaterial("material", mapOf("albedoTexture" to "voxels/textures/${this.voxelTextures[VoxelSide.FRONT.ordinal].name}.png")))

		customRenderingRoutine = { cell ->
			val matrix = Matrix4f()

			matrix.translate(0.5f, 0f, 0.5f)
			matrix.rotate(wangUniform(cell.z + 32187) * 2f * Math.random().toFloat(), 0f, 1f, 0f)
			matrix.translate(-0.5f, 0f, -0.5f)

			matrix.translate((-0.5f + wangUniform(cell.x + 48461558)) * 0.25f, 0f, (-0.5f + wangUniform(cell.y + 871413)) * 0.25f)

			addModel(model, matrix, mappedOverrides)
		}
	}

	override fun tick(cell: EditableCell) {
		if(cell.y == 0)
			return
		val below = cell.world.peek(cell.x, cell.y - 1, cell.z)
		if(!below.voxel.solid || !below.voxel.opaque) {
			cell.voxel.breakBlock(cell, SmallPlantsAutoDestroy, null)
		}
	}

	object SmallPlantsAutoDestroy : MiningTool {
		override val miningEfficiency = Float.POSITIVE_INFINITY
		override val toolTypeName = "world"
	}
}

inline fun wangUniform(seed: Int): Float = (wangHash(seed) and 0xffff).toFloat() / 65536.0f

/* Deterministic rng */
inline fun wangHash(seed: Int): Int {
	var hash = (seed xor 61) xor (seed shr 16)
	hash *= 9
	hash = hash xor (hash shr 4)
	hash *= 0x27d4eb2d
	hash = hash xor (hash shr 15)
	return hash
}