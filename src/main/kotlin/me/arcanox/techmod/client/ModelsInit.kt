package me.arcanox.techmod.client

import me.arcanox.lib.client.util.render.ModelLoader
import me.arcanox.techmod.TechMod
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object ModelsInit {
	@Suppress("UNUSED_PARAMETER")
	@SubscribeEvent
	fun registerModels(event: ModelRegistryEvent) {
		ModelLoader.registerModelsInPackage(TechMod.ModID, TechMod.PackagePrefix);
	}
}