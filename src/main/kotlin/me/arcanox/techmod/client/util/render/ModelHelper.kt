package me.arcanox.techmod.client.util.render

import me.arcanox.techmod.util.Logger
import me.arcanox.techmod.util.LazyCache
import me.arcanox.techmod.util.reflect.ReflectionHelper
import me.arcanox.techmod.util.lazyCache
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.model.IBakedModel
import net.minecraft.client.resources.ReloadListener
import net.minecraft.profiler.IProfiler
import net.minecraft.resources.IFutureReloadListener
import net.minecraft.resources.IReloadableResourceManager
import net.minecraft.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import kotlin.reflect.full.companionObjectInstance

@Target(AnnotationTarget.CLASS)
annotation class ConsumesModels;

public interface IModelConsumer {
	fun reloadModels(): Unit;
	fun getModelLocations(): Sequence<ResourceLocation>;
}

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object ModelHelper : IFutureReloadListener {
	private val modelConsumers = mutableListOf<IModelConsumer>()
	
	/**
	 * It's shorter to type.
	 */
	fun getModel(resourceLocation: ResourceLocation): LazyCache<IBakedModel> = lazyCache { Minecraft.getInstance().modelManager.getModel(resourceLocation) }
	
	@Suppress("UNUSED_PARAMETER")
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
				this.modelConsumers += modelConsumer;
			};
		
		// Listen for reloads
		val resourceManager = Minecraft.getInstance().resourceManager;
		
		if (resourceManager is IReloadableResourceManager) {
			resourceManager.addReloadListener(this);
		}
		
		Logger.info("Model loading complete.");
	}
	
	@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
	override fun reload(stage: IFutureReloadListener.IStage,
	                    resourceManager: IResourceManager,
	                    preparationsProfiler: IProfiler,
	                    reloadProfiler: IProfiler,
	                    backgroundExecutor: Executor,
	                    gameExecutor: Executor): CompletableFuture<Void> = stage.markCompleteAwaitingOthers(null).thenAcceptAsync {
		this.modelConsumers.forEach { it.reloadModels() };
	}
}