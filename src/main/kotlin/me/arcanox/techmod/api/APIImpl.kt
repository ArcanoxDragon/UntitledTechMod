package me.arcanox.techmod.api

import me.arcanox.techmod.api.blocks.BlocksAPI
import me.arcanox.techmod.api.items.ItemsAPI;
import me.arcanox.techmod.util.IInitStageHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

object APIImpl : IInitStageHandler {
	private val stageHandlers = arrayOf(BlocksAPI,
	                                    ItemsAPI)
	
	override fun onPreInit(e: FMLPreInitializationEvent) {
		this.stageHandlers.forEach { it.onPreInit(e) };
	}
	
	override fun onInit(e: FMLInitializationEvent) {
		this.stageHandlers.forEach { it.onInit(e) };
	}
	
	override fun onPostInit(e: FMLPostInitializationEvent) {
		this.stageHandlers.forEach { it.onPostInit(e) };
		
		API.blockAPI = BlocksAPI;
		API.itemAPI = ItemsAPI;
		
		API.modLoaded = true;
	}
}
