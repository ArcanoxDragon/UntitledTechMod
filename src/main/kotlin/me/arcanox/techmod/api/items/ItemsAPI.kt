package me.arcanox.techmod.api.items

import me.arcanox.techmod.common.proxy.IInitStageHandler
import me.arcanox.techmod.util.Logger
import me.arcanox.techmod.util.reflect.InitHandler
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

@InitHandler
object ItemsAPI : IItemAPI, IInitStageHandler {
	val items: MutableMap<String, Item> = mutableMapOf()
	
	override fun onPreInit(e: FMLPreInitializationEvent) {
		this.registerItems();
	}
	
	fun registerItems(): Unit {
		Logger.info("Registering ${this.items.size} items...");
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
