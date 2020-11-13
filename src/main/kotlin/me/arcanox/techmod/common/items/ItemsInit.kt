package me.arcanox.techmod.common.items

import me.arcanox.techmod.util.Logger
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object ItemsInit {
	internal val items = mutableMapOf<String, Item>()
	
	// region Item Registration
	
	@Suppress("UNUSED_PARAMETER")
	@SubscribeEvent
	fun registerItems(event: RegistryEvent.Register<Item>) {
		// TODO: Discover and register all Items
		
		Logger.info("Registering ${this.items.size} items...");
	}
	
	// endregion Item Registration
}