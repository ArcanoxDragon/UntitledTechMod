package me.arcanox.techmod.common.items

import me.arcanox.techmod.util.Logger
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

object Items {
	internal val items = mutableMapOf<String, Item>()
}

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object ItemsInit {
	// region Item Registration
	
	@Suppress("UNUSED_PARAMETER")
	@SubscribeEvent
	fun registerItems(event: RegistryEvent.Register<Item>) {
		// TODO: Discover and register all Items
		
		Logger.info("Registering ${Items.items.size} items...");
	}
	
	// endregion Item Registration
}