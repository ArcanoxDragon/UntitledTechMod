package me.arcanox.techmod.common.tileentities

import me.arcanox.techmod.client.tileentities.renderers.ITESRWithModels
import me.arcanox.techmod.common.proxy.IClientInitHandler
import me.arcanox.techmod.common.proxy.IInitStageHandler
import me.arcanox.techmod.util.Logger
import me.arcanox.techmod.util.reflect.*
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.reflect.KClass

@InitHandler(priority = 4 /* before blocks and TESRs */)
@ClientInitHandler
object TileEntities : IInitStageHandler, IClientInitHandler {
	private const val TesrPackagePrefix = "me.arcanox.techmod.client.tileentities.renderers";
	private val tileEntities = ArrayList<Pair<KClass<out TileEntity>, ModTileEntity>>();
	
	@SideOnly(Side.CLIENT)
	private val tesrs = emptyMap<KClass<out TileEntity>, TileEntitySpecialRenderer<in TileEntity>>().toMutableMap();
	
	override fun onPreInit(e: FMLPreInitializationEvent) {
		MinecraftForge.EVENT_BUS.register(this);
		
		this.tileEntities += ReflectionHelper.getClassesWithAnnotation(e.asmData, ModTileEntity::class, TileEntity::class)
		
		Logger.info("Registering ${tileEntities.size} tile entities...");
		
		this.tileEntities.forEach { (c, a) ->
			GameRegistry.registerTileEntity(c.java, a.id);
		}
	}
	
	@SubscribeEvent
	fun onModelBake(e: ModelBakeEvent) {
		Logger.info("Baking models for ${this.tesrs.size} TESRs");
		
		this.tesrs.forEach { (c, it) ->
			try {
				if (it is ITESRWithModels)
					it.loadModels(e);
			} catch (ex: Exception) {
				Logger.warn("An error occurred while loading the models for the TESR for Tile Entity ${c.simpleName}");
				ex.printStackTrace();
			}
		}
	}
	
	override fun onClientPreInit(e: FMLPreInitializationEvent) {
		this.tileEntities.forEach { (c, _) ->
			try {
				if (c.hasAnnotation<HasTESR>()) {
					val tesrAnnotation = c.java.getDeclaredAnnotation(HasTESR::class.java);
					val tesrName = "$TesrPackagePrefix.${tesrAnnotation.tesrName}";
					val tesrClass = Class.forName(tesrName).asSubclass(TileEntitySpecialRenderer::class.java);
					
					if (tesrClass == null) {
						Logger.warn("Tile Entity ${c.simpleName} has a HasTESR annotation but the TESR class could not be found: $tesrName");
						return@forEach;
					}
					
					val tesrKClass = tesrClass.kotlin;
					
					if (tesrKClass.objectInstance == null) {
						Logger.warn("TESR registered to Tile Entity ${c.simpleName} must be a Kotlin object.");
						return@forEach;
					}
					
					@Suppress("UNCHECKED_CAST")
					val tesrInst = tesrKClass.objectInstance as TileEntitySpecialRenderer<in TileEntity>;
					ClientRegistry.bindTileEntitySpecialRenderer(c.java, tesrInst);
					this.tesrs += Pair(c, tesrInst);
				}
			} catch (ex: Exception) {
				Logger.warn("An error occurred during the initialization of a TESR for Tile Entity ${c.simpleName}");
				ex.printStackTrace();
			}
		};
	}
}