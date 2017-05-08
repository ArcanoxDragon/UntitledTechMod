package me.arcanox.techmod.client.proxy

import me.arcanox.techmod.common.proxy.CommonProxy
import me.arcanox.techmod.common.proxy.IClientInitHandler
import me.arcanox.techmod.util.reflect.ClientInitHandler
import me.arcanox.techmod.util.reflect.ReflectionHelper
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent


class ClientProxy : CommonProxy() {
	private val stageHandlers = emptyList<IClientInitHandler>().toMutableList();
	
	override fun onPreInit(e: FMLPreInitializationEvent) {
		this.stageHandlers += ReflectionHelper
			.getInstancesWithAnnotation(e.asmData, ClientInitHandler::class, IClientInitHandler::class)
			.sortedBy { it.second.priority }
			.map { it.first };
		
		super.onPreInit(e);
		
		this.stageHandlers.forEach { it.onClientPreInit(e) };
	}
	
	override fun onInit(e: FMLInitializationEvent) {
		super.onInit(e);
		
		this.stageHandlers.forEach { it.onClientInit(e) };
	}
	
	override fun onPostInit(e: FMLPostInitializationEvent) {
		super.onPostInit(e);
		
		this.stageHandlers.forEach { it.onClientPostInit(e) };
	}
}