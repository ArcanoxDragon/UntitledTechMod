package me.arcanox.techmod.api.items

import me.arcanox.techmod.util.Logger
import me.arcanox.techmod.util.reflect.EventBusSubscriberObject
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

@EventBusSubscriberObject
object ItemsAPI : IItemAPI {
	val items = mutableMapOf<String, Item>()
	
	@SubscribeEvent
	fun registerItems(event: RegistryEvent.Register<Item>): Unit {
		// TODO: Discover and register all Items
		
		Logger.info("Registering ${this.items.size} items...");
	}
	
	override fun getItem(name: String): Item? {
		if (name !in this.items) return null;
		
		return this.items[name];
	}
	
	override fun getItemStack(name: String, count: Int): ItemStack? {
		if (name !in this.items) return null;
		
		return ItemStack(this.getItem(name)!!, count);
	}
}
