package me.arcanox.techmod.api

import me.arcanox.techmod.api.blocks.BlocksAPI
import me.arcanox.techmod.api.items.ItemsAPI;
import me.arcanox.techmod.util.IInitStageHandler
import net.minecraftforge.common.MinecraftForge

object APIImpl : IInitStageHandler {
	internal fun registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(BlocksAPI);
		MinecraftForge.EVENT_BUS.register(ItemsAPI);
	}
	
	override fun onPreInit() {
		BlocksAPI.onPreInit();
		ItemsAPI.onPreInit();
	}
	
	override fun onPostInit() {
		API.blockAPI = BlocksAPI;
		API.itemAPI = ItemsAPI;
		
		API.modLoaded = true;
	}
}
