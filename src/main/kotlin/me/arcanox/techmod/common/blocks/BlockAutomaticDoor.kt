package me.arcanox.techmod.common.blocks

import me.arcanox.techmod.common.Constants

import net.minecraft.block.Block
import net.minecraft.block.material.Material

class BlockAutomaticDoor : Block(Material.IRON) {
	init {
		this.unlocalizedName = Constants.Blocks.AutomaticDoor;
	}
}
