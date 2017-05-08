package me.arcanox.techmod.common.proxy

import me.arcanox.techmod.util.reflect.InitHandler
import me.arcanox.techmod.util.reflect.ReflectionHelper
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

open class CommonProxy : IInitStageHandler {
	private val stageHandlers = emptyList<IInitStageHandler>().toMutableList();
	
	override fun onPreInit(e: FMLPreInitializationEvent) {
		this.stageHandlers += ReflectionHelper
			.getInstancesWithAnnotation(e.asmData, InitHandler::class, IInitStageHandler::class)
			.sortedBy { it.second.priority }
			.map { it.first };
		
		this.stageHandlers.forEach { it.onPreInit(e) };
	}
	
	override fun onInit(e: FMLInitializationEvent) {
		this.stageHandlers.forEach { it.onInit(e) };
	}
	
	override fun onPostInit(e: FMLPostInitializationEvent) {
		this.stageHandlers.forEach { it.onPostInit(e) };
	}
}