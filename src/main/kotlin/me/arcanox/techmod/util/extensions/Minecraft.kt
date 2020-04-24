package me.arcanox.techmod.util.extensions

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.util.math.BlockPos

// region BlockPos

fun BlockPos.horizontalNeighbors() = arrayOf(
	this.north(),
	this.south(),
	this.east(),
	this.west()
)

// endregion BlockPos

// region MatrixStack

fun MatrixStack.translateVoxels(x: Int, y: Int, z: Int) {
	this.translate(x / 16.0, y / 16.0, z / 16.0);
}

// endregion MatrixStack