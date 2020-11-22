package me.arcanox.techmod.common.items

import me.arcanox.techmod.TechMod
import net.minecraft.item.Item
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.RegistryObject
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
internal object Items {
	// region Holder fields
	
	internal val registry: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, TechMod.ModID)
	private val items = mutableMapOf<String, RegistryObject<out Item>>()
	
	// endregion Holder fields
	
	fun register(eventBus: IEventBus) = registry.register(eventBus)
	
	// region Registry helper methods
	
	private fun <T : Item> register(name: String, supplier: () -> T) = registry.register(name, supplier).also { items += name to it }!!
	
	// endregion Registry helper methods
}