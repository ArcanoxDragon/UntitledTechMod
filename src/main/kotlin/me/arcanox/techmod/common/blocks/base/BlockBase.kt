package me.arcanox.techmod.common.blocks.base

import me.arcanox.techmod.CreativeTab
import net.minecraft.block.Block
import net.minecraft.block.material.Material


abstract class BlockBase(name: String, material: Material) : Block(material) {
	var apiName: String
		private set
	
	init {
		this.apiName = name;
		
		this.setRegistryName(name);
		this.setCreativeTab(CreativeTab);
	}
	
	override fun getUnlocalizedName(): String = "tile.${this.registryName}";
}