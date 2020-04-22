package me.arcanox.techmod.util.extensions

import net.minecraft.util.math.BlockPos

// region BlockPos

fun BlockPos.cardinals() = arrayOf(
	this.north(),
	this.south(),
	this.east(),
	this.west()
)

// endregion BlockPos