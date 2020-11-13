package me.arcanox.techmod.client.util.render

import me.arcanox.techmod.util.Logger
import me.arcanox.techmod.util.LazyCache
import me.arcanox.techmod.util.reflect.ReflectionHelper
import me.arcanox.techmod.util.lazyCache
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.model.IBakedModel
import net.minecraft.profiler.IProfiler
import net.minecraft.resources.IFutureReloadListener
import net.minecraft.resources.IReloadableResourceManager
import net.minecraft.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import kotlin.reflect.full.companionObjectInstance
import net.minecraftforge.client.model.ModelLoader as ForgeModelLoader

@Target(AnnotationTarget.CLASS)
annotation class ConsumesModels;

interface IModelConsumer {
	fun reloadModels();
	fun getModelLocations(): Sequence<ResourceLocation>;
}

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object ModelLoader : IFutureReloadListener {
	private val modelConsumers = mutableListOf<IModelConsumer>()
	
	/**
	 * Returns a LazyCache which will initialize with an IBakedModel instance for the provided ResourceLocation
	 */
	fun getModel(resourceLocation: ResourceLocation): LazyCache<IBakedModel> = lazyCache { Minecraft.getInstance().modelManager.getModel(resourceLocation) }
	
	@Suppress("UNUSED_PARAMETER")
	@SubscribeEvent
	fun registerModels(event: ModelRegistryEvent) {
		Logger.info("Beginning model loading...");
		
		// Find all IModelConsumers and register their models
		ReflectionHelper.forClassesWithAnnotation(ConsumesModels::class, Any::class) { modelConsumerClass, _ ->
			val companionInstance = modelConsumerClass.companionObjectInstance;
			val modelConsumer = companionInstance as? IModelConsumer;
			
			if (modelConsumer == null) {
				Logger.warn("Class \"${modelConsumerClass.simpleName}\" has a ConsumesModels annotation, but it does not have a companion object which implements IModelConsumer");
				return@forClassesWithAnnotation;
			}
			
			modelConsumer.getModelLocations().forEach { ForgeModelLoader.addSpecialModel(it) };
			this.modelConsumers += modelConsumer;
		};
		
		// Listen for reloads
		val reloadableResourceManager = Minecraft.getInstance().resourceManager as? IReloadableResourceManager;
		
		reloadableResourceManager?.addReloadListener(this);
		
		Logger.info("Model loading complete.");
	}
	
	override fun reload(stage: IFutureReloadListener.IStage, resourceManager: IResourceManager, preparationsProfiler: IProfiler,
	                    reloadProfiler: IProfiler, backgroundExecutor: Executor, gameExecutor: Executor): CompletableFuture<Void> = CompletableFuture.runAsync {
		Logger.debug("Beginning ModelConsumer reload...");
		this.modelConsumers.forEach { it.reloadModels() };
		Logger.debug("ModelConsumer reload is complete.");
	}.thenCompose(stage::markCompleteAwaitingOthers)
}