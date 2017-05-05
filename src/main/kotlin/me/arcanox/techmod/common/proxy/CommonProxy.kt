package me.arcanox.techmod.common.proxy

import me.arcanox.techmod.api.APIImpl
import me.arcanox.techmod.common.tileentities.TileEntities
import me.arcanox.techmod.util.IInitStageHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

open class CommonProxy : IInitStageHandler {
	private val stageHandlers = arrayOf(APIImpl,
	                                    TileEntities)
	
	override fun onPreInit(e: FMLPreInitializationEvent) {
		this.stageHandlers.forEach { it.onPreInit(e) };
	}
	
	override fun onInit(e: FMLInitializationEvent) {
		this.stageHandlers.forEach { it.onInit(e) };
	}
	
	override fun onPostInit(e: FMLPostInitializationEvent) {
		this.stageHandlers.forEach { it.onPostInit(e) };
	}
}