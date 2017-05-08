package me.arcanox.techmod.api

import me.arcanox.techmod.api.blocks.BlocksAPI
import me.arcanox.techmod.api.items.ItemsAPI;
import me.arcanox.techmod.common.proxy.IInitStageHandler
import me.arcanox.techmod.util.reflect.InitHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

@InitHandler
object APIImpl : IInitStageHandler {
	override fun onPostInit(e: FMLPostInitializationEvent) {API.blockAPI = BlocksAPI;
		API.itemAPI = ItemsAPI;
		
		API.modLoaded = true;
	}
}
