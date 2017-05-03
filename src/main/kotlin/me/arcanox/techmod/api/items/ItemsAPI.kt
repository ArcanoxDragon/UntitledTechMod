package me.arcanox.techmod.api.items

import me.arcanox.techmod.util.IInitStageHandler
import me.arcanox.techmod.util.Logger
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object ItemsAPI : IItemAPI, IInitStageHandler {
	val items: MutableMap<String, Item> = mutableMapOf()
	
	override fun onPreInit(): Unit {
		this.defineItems();
	}
	
	private fun defineItems() {
	
	}
	
	@SubscribeEvent
	fun registerItems(event: RegistryEvent.Register<Item>): Unit {
		Logger.info("Registering items...");
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
