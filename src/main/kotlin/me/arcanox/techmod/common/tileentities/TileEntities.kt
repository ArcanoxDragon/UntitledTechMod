package me.arcanox.techmod.common.tileentities

import me.arcanox.techmod.TechMod
import me.arcanox.techmod.client.IClientInitHandler
import me.arcanox.techmod.util.Logger
import me.arcanox.techmod.util.reflect.*
import net.minecraft.client.renderer.tileentity.TileEntityRenderer
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import java.util.function.Supplier
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

@ClientInitHandler
@EventBusSubscriberObject
object TileEntities : IClientInitHandler {
	private const val TesrPackagePrefix = "me.arcanox.techmod.client.tileentities.renderers";
	
	private val tileEntities = ArrayList<Pair<TileEntityType<out TileEntity>, KClass<out TileEntity>>>();
	
	@OnlyIn(Dist.CLIENT)
	private val tileEntityRenderers = mutableMapOf<KClass<out TileEntity>, TileEntityRenderer<in TileEntity>>();
	
	fun registerTileEntities(event: RegistryEvent.Register<TileEntityType<out TileEntity>>) {
		this.tileEntities += ReflectionHelper
			.getClassesWithAnnotation(ModTileEntity::class, TileEntity::class)
			.map { (tileEntityClass, tileEntityAnnotation) ->
				@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
				val type = TileEntityType.Builder.create(Supplier { tileEntityClass.createInstance() }).build(null);
				
				type.setRegistryName(TechMod.ModID, tileEntityAnnotation.id);
				
				return@map Pair(type, tileEntityClass);
			};
	}
	
	override fun onClientInit(e: FMLClientSetupEvent) {
		this.tileEntities.forEach { (tileEntityType, tileEntityClass) ->
			try {
				if (tileEntityClass.hasAnnotation<HasTileEntityRenderer>()) {
					val tileEntityRendererAnnotation = tileEntityClass.java.getDeclaredAnnotation(HasTileEntityRenderer::class.java);
					val tileEntityRendererClass = tileEntityRendererAnnotation.rendererClass;
					val tileEntityRendererConstructor = tileEntityRendererClass.constructors.find { c ->
						c.parameters.size == 1 && c.parameters.first().type == TileEntityRendererDispatcher::class
					};
					
					if (tileEntityRendererConstructor == null) {
						Logger.warn("TileEntityRenderer type \"${tileEntityRendererClass.simpleName}\" is missing the required constructor accepting a TileEntityRendererDispatcher parameter")
						return@forEach;
					}
					
					@Suppress("UNCHECKED_CAST")
					ClientRegistry.bindTileEntityRenderer(tileEntityType) { d -> tileEntityRendererConstructor.call(d) as TileEntityRenderer<in TileEntity> };
				}
			} catch (ex: Exception) {
				Logger.warn("An error occurred during the initialization of a TileEntityRenderer for TileEntity type \"${tileEntityClass.simpleName}\"");
				ex.printStackTrace();
			}
		};
	}
}