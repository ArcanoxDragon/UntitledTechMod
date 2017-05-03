package me.arcanox.techmod.api.items

import net.minecraft.item.Item
import net.minecraft.item.ItemStack

object ItemsAPI : IItemAPI {
	val items: Map<String, Item> = emptyMap()
	
	internal fun init(): Unit {
	
	}
	
	override fun getItem(name: String): Item? {
		if (name !in this.items) return null;
		
		return this.items[name];
	}
	
	override fun getItemStack(name: String, count: Int): ItemStack? {
		if (name !in this.items) return null;
		
		return ItemStack(getItem(name), count);
	}
}
