package me.arcanox.techmod.client.util.render

import me.arcanox.techmod.client.IClientInitHandler
import me.arcanox.techmod.util.Logger
import me.arcanox.techmod.util.reflect.ClientInitHandler
import me.arcanox.techmod.util.reflect.ReflectionHelper
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import kotlin.reflect.full.companionObjectInstance

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object ModelRegistration {
	@SubscribeEvent
	fun registerModels(event: ModelRegistryEvent) {
		Logger.info("Beginning model loading...");
		
		// Find all IModelConsumers and register their models
		ReflectionHelper
			.getClassesWithAnnotation(ConsumesModels::class, Any::class)
			.forEach { (modelConsumerClass, _) ->
				val companionInstance = modelConsumerClass.companionObjectInstance;
				val modelConsumer = companionInstance as? IModelConsumer;
				
				if (modelConsumer == null) {
					Logger.warn("Class \"${modelConsumerClass.simpleName}\" has a ConsumesModels annotation, but its companion object does not implement IModelConsumer");
					return@forEach;
				}
				
				modelConsumer.getModelLocations().forEach { ModelLoader.addSpecialModel(it) };
			};
		
		Logger.info("Model loading complete.");
	}
}