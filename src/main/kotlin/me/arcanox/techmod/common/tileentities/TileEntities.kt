package me.arcanox.techmod.common.tileentities

import me.arcanox.techmod.TechMod
import me.arcanox.techmod.api.blocks.BlocksAPI
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
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import java.util.function.Function
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.starProjectedType

@ClientInitHandler
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object TileEntities : IClientInitHandler {
	private const val TesrPackagePrefix = "me.arcanox.techmod.client.tileentities.renderers";
	
	private val tileEntities = mutableMapOf<KClass<out TileEntity>, TileEntityType<out TileEntity>>();
	
	@OnlyIn(Dist.CLIENT)
	private val tileEntityRenderers = mutableMapOf<KClass<out TileEntity>, TileEntityRenderer<in TileEntity>>();
	
	@SubscribeEvent
	fun registerTileEntities(event: RegistryEvent.Register<TileEntityType<out TileEntity>>) {
		this.tileEntities += ReflectionHelper
			.getClassesWithAnnotation(ModTileEntity::class, TileEntity::class)
			.associate { (tileEntityClass, tileEntityAnnotation) ->
				val validBlocks = tileEntityAnnotation.blocks.map { BlocksAPI.getBlock(it) }.toTypedArray()
				
				@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
				val tileEntityType = TileEntityType.Builder.create({ tileEntityClass.createInstance() }, *validBlocks).build(null);
				
				tileEntityType.setRegistryName(TechMod.ModID, tileEntityAnnotation.id);
				event.registry.register(tileEntityType);
				
				Pair(tileEntityClass, tileEntityType);
			};
	}
	
	override fun onClientInit(e: FMLClientSetupEvent) {
		this.tileEntities.forEach { (tileEntityClass, tileEntityType) ->
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
	
	fun getTileEntityType(clazz: KClass<out TileEntity>): TileEntityType<out TileEntity> {
		if (clazz !in this.tileEntities)
			throw Exception("Tile entity type \"${clazz.simpleName}\" not found in registered TileEntities list. Does it have a @ModTileEntity attribute?");
		
		return this.tileEntities[clazz]!!;
	}
	
	inline fun <reified T : TileEntity> getTileEntityType() = this.getTileEntityType(T::class)
}