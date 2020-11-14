package me.arcanox.techmod.api.impl.blocks

import me.arcanox.techmod.api.blocks.IBlocksApi
import me.arcanox.techmod.common.blocks.Blocks
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import kotlin.reflect.KClass

object BlocksApiImpl : IBlocksApi {
	override fun getBlock(name: String): Block? {
		if (name !in Blocks.blocks) return null;
		
		return Blocks.blocks[name];
	}
	
	override fun getBlockItem(name: String): BlockItem? {
		if (name !in Blocks.blockItems) return null;
		
		return Blocks.blockItems[name];
	}
	
	override fun getBlockItemStack(name: String, count: Int): ItemStack? {
		val block = getBlock(name) ?: return null;
		
		return ItemStack(block, count);
	}
	
	internal fun getBlock(clazz: KClass<out Block>): Block? {
		if (clazz !in Blocks.blockClasses) return null;
		
		return Blocks.blockClasses[clazz];
	}
}
