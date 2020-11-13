package me.arcanox.techmod.util.extensions

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.util.math.BlockPos

// region BlockPos

/**
 * Returns an array of the BlockPos positions at each of the four
 * cardinal directions relative to this BlockPos
 */
fun BlockPos.horizontalNeighbors() = arrayOf(
	this.north(),
	this.south(),
	this.east(),
	this.west()
)

// endregion BlockPos

// region MatrixStack

/**
 * Translates this MatrixStack by sub-block voxels. Each 1x1x1 block
 * contains 16x16x16 internal voxels.
 */
fun MatrixStack.translateVoxels(x: Int, y: Int, z: Int) {
	this.translate(x / 16.0, y / 16.0, z / 16.0);
}

/**
 * Pushes this MatrixStack's current state, runs the provided action,
 * and then pops the MatrixStack's state again.
 */
fun MatrixStack.pushAnd(action: MatrixStack.() -> Unit) {
	this.push();
	
	try {
		action(this);
	} finally {
		this.pop();
	}
}

// endregion MatrixStack