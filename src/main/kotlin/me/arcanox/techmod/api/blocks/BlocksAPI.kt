package me.arcanox.techmod.api.blocks

import me.arcanox.techmod.TechMod
import me.arcanox.techmod.common.Constants
import me.arcanox.techmod.common.blocks.*

import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

import net.minecraftforge.fml.common.registry.GameRegistry

object BlocksAPI : IBlockAPI {
	val blocks: MutableMap<String, Block> = mutableMapOf()
	
	internal fun init(): Unit {
		this.blocks += Pair(Constants.Blocks.AutomaticDoor, BlockAutomaticDoor());
		
		this.doRegister();
	}
	
	fun doRegister(): Unit {
		for ((name, block) in this.blocks) {
			GameRegistry.register(block, ResourceLocation(TechMod.ModID, "blocks/$name"))
		}
	}
	
	override fun getBlock(name: String): Block? {
		if (name !in this.blocks) return null;
		
		return this.blocks[name];
	}
	
	override fun getBlockItemStack(name: String, count: Int): ItemStack? {
		if (name !in this.blocks) return null;
		
		return ItemStack(getBlock(name), count);
	}
}
