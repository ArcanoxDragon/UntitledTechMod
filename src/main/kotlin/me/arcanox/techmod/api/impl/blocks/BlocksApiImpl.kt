package me.arcanox.techmod.api.impl.blocks

import me.arcanox.techmod.api.blocks.IBlocksApi
import me.arcanox.techmod.common.blocks.BlocksInit
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import kotlin.reflect.KClass

object BlocksApiImpl : IBlocksApi {
	override fun getBlock(name: String): Block? {
		if (name !in BlocksInit.blocks) return null;
		
		return BlocksInit.blocks[name];
	}
	
	override fun getBlockItem(name: String): BlockItem? {
		if (name !in BlocksInit.blockItems) return null;
		
		return BlocksInit.blockItems[name];
	}
	
	override fun getBlockItemStack(name: String, count: Int): ItemStack? {
		val block = getBlock(name) ?: return null;
		
		return ItemStack(block, count);
	}
	
	internal fun getBlock(clazz: KClass<out Block>): Block? {
		if (clazz !in BlocksInit.blockClasses) return null;
		
		return BlocksInit.blockClasses[clazz];
	}
}
