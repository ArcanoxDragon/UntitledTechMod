package me.arcanox.techmod

import me.arcanox.techmod.client.IClientInitHandler
import me.arcanox.techmod.common.IInitHandler
import me.arcanox.techmod.util.Logger
import me.arcanox.techmod.util.reflect.ClientInitHandler
import me.arcanox.techmod.util.reflect.InitHandler
import me.arcanox.techmod.util.reflect.ReflectionHelper
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT

@Mod(TechMod.ModID)
object TechMod {
	const val Name = "Tech Mod" // TODO: change these to actual name
	const val ModID = "techmod" // TODO: change these to actual name
	
	private val commonInitHandlers = emptyList<IInitHandler>().toMutableList();
	private val clientInitHandlers = emptyList<IClientInitHandler>().toMutableList();
	
	init {
		MOD_CONTEXT.getEventBus().addListener(this::onCommonInit);
		MOD_CONTEXT.getEventBus().addListener(this::onClientInit);
	}
	
	private fun onCommonInit(event: FMLCommonSetupEvent) {
		Logger.info("Beginning common initialization phase...");
		
		// Find all IInitHandler classes and allow them to initialize
		this.commonInitHandlers += ReflectionHelper
			.getInstancesWithAnnotation(InitHandler::class, IInitHandler::class)
			.sortedBy { it.second.priority }
			.map { it.first };
		this.commonInitHandlers.forEach { it.onInit(event) };
		
		Logger.info("Common initialization phase complete.");
	}
	
	private fun onClientInit(event: FMLClientSetupEvent) {
		Logger.info("Beginning client initialization phase...");
		
		// Find all IClientInitHandler classes and allow them to initialize
		this.clientInitHandlers += ReflectionHelper
			.getInstancesWithAnnotation(ClientInitHandler::class, IClientInitHandler::class)
			.sortedBy { it.second.priority }
			.map { it.first };
		this.clientInitHandlers.forEach { it.onClientInit(event) };
		
		Logger.info("Client initialization phase complete.");
	}
}