package me.arcanox.techmod.common.tiles

import me.arcanox.lib.client.IClientInitHandler
import me.arcanox.lib.util.reflect.*
import me.arcanox.techmod.TechMod
import me.arcanox.techmod.common.blocks.Blocks
import me.arcanox.techmod.util.Logger
import net.minecraft.client.renderer.tileentity.TileEntityRenderer
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import java.util.function.Function
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.starProjectedType

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
internal object TileEntities {
	val tileEntities = mutableMapOf<KClass<out TileEntity>, TileEntityType<out TileEntity>>();
	
	fun getTileEntityType(clazz: KClass<out TileEntity>): TileEntityType<out TileEntity> {
		if (clazz !in tileEntities)
			throw Exception("Tile entity type \"${clazz.simpleName}\" not found in registered TileEntities list. Does it have a @ModTileEntity attribute?");
		
		return tileEntities[clazz]!!;
	}
	
	inline fun <reified T : TileEntity> getTileEntityType() = this.getTileEntityType(T::class)
	
	@SubscribeEvent
	fun registerTileEntities(event: RegistryEvent.Register<TileEntityType<out TileEntity>>) {
		ReflectionHelper.forClassesWithAnnotation(ModTileEntity::class, TileEntity::class, TechMod.PackagePrefix) { tileEntityClass, tileEntityAnnotation ->
			val validBlocks = tileEntityAnnotation.validBlocks.map(Blocks::getBlock).toTypedArray()
			
			@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
			val tileEntityType = TileEntityType.Builder.create({ tileEntityClass.createInstance() }, *validBlocks).build(null);
			
			tileEntityType.setRegistryName(TechMod.ModID, tileEntityAnnotation.id);
			
			tileEntities += tileEntityClass to tileEntityType;
		};
		
		Logger.info("Registering ${tileEntities.size} tile entities...");
		
		tileEntities.values.forEach(event.registry::register);
	}
	
	@ClientInitHandler
	object TileEntitiesClient : IClientInitHandler {
		val tileEntityRenderers = mutableMapOf<KClass<out TileEntity>, TileEntityRenderer<in TileEntity>>();
		
		override fun onClientInit(e: FMLClientSetupEvent) {
			tileEntities.forEach { (tileEntityClass, tileEntityType) ->
				try {
					if (tileEntityClass.hasAnnotation<HasTileEntityRenderer>()) {
						val tileEntityRendererAnnotation = tileEntityClass.java.getDeclaredAnnotation(HasTileEntityRenderer::class.java);
						val tileEntityRendererClass = tileEntityRendererAnnotation.rendererClass;
						val tileEntityRendererConstructor = tileEntityRendererClass.constructors.find { c ->
							c.parameters.size == 1 && c.parameters.first().type == TileEntityRendererDispatcher::class.starProjectedType
						};
						
						if (tileEntityRendererConstructor == null) {
							Logger.warn("TileEntityRenderer type \"${tileEntityRendererClass.simpleName}\" is missing the required constructor accepting a TileEntityRendererDispatcher parameter")
							return@forEach;
						}
						
						@Suppress("UNCHECKED_CAST")
						ClientRegistry.bindTileEntityRenderer(tileEntityType, Function { d ->
							val renderer = tileEntityRendererConstructor.call(d);
							
							return@Function renderer as TileEntityRenderer<in TileEntity>;
						});
					}
				} catch (ex: Exception) {
					Logger.warn("An error occurred during the initialization of a TileEntityRenderer for TileEntity type \"${tileEntityClass.simpleName}\"");
					ex.printStackTrace();
				}
			};
		}
	}
}