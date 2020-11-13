package me.arcanox.techmod.api.impl.items

import me.arcanox.techmod.api.items.IItemsApi
import me.arcanox.techmod.common.items.ItemsInit
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

object ItemsApiImpl : IItemsApi {
	override fun getItem(name: String): Item? {
		if (name !in ItemsInit.items) return null;
		
		return ItemsInit.items[name];
	}
	
	override fun getItemStack(name: String, count: Int): ItemStack? {
		if (name !in ItemsInit.items) return null;
		
		return ItemStack(getItem(name)!!, count);
	}
}
