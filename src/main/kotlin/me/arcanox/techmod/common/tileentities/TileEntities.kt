package me.arcanox.techmod.common.tileentities

import me.arcanox.techmod.client.tileentities.renderers.ITESRWithModels
import me.arcanox.techmod.util.IInitStageHandler
import me.arcanox.techmod.util.Logger
import me.arcanox.techmod.util.reflect.*
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.reflect.KClass


object TileEntities : IInitStageHandler {
	private const val TesrPackagePrefix = "me.arcanox.techmod.client.tileentities.renderers";
	private val tileEntities = ArrayList<Pair<KClass<out TileEntity>, ModTileEntity>>();
	
	@SideOnly(Side.CLIENT)
	private val tesrs = emptyMap<KClass<out TileEntity>, TileEntitySpecialRenderer<in TileEntity>>().toMutableMap();
	
	override fun onPreInit(e: FMLPreInitializationEvent) {
		this.tileEntities += ReflectionHelper.getClassesWithAnnotation(e.asmData, ModTileEntity::class, TileEntity::class)
		
		Logger.info("Registering ${tileEntities.size} tile entities...");
		
		this.tileEntities.forEach { (c, a) ->
			GameRegistry.registerTileEntity(c.java, a.id);
			
			if (FMLCommonHandler.instance().effectiveSide == Side.CLIENT) {
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
			}
		}
	}
	
	override fun onPostInit(e: FMLPostInitializationEvent) {
		if (FMLCommonHandler.instance().effectiveSide == Side.CLIENT) {
			this.tesrs.forEach { (c, it) ->
				try {
					if (it is ITESRWithModels)
						it.loadModels();
				} catch (ex: Exception) {
					Logger.warn("An error occurred while loading the models for the TESR for Tile Entity ${c.simpleName}");
					ex.printStackTrace();
				}
			}
		}
	}
}