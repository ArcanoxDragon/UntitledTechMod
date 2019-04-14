package me.arcanox.techmod.api.items

import me.arcanox.techmod.common.proxy.IInitStageHandler
import me.arcanox.techmod.util.Logger
import me.arcanox.techmod.util.reflect.InitHandler
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@InitHandler
object ItemsAPI : IItemAPI, IInitStageHandler {
	val items = mutableMapOf<String, Item>()
	
	init {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	fun registerItems(e: RegistryEvent.Register<Item>): Unit {
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
