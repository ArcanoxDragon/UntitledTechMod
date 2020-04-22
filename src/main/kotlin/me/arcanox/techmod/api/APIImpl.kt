package me.arcanox.techmod.api

import me.arcanox.techmod.api.blocks.BlocksAPI
import me.arcanox.techmod.api.items.ItemsAPI
import me.arcanox.techmod.common.IInitHandler
import me.arcanox.techmod.util.reflect.InitHandler
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent

@InitHandler
object APIImpl : IInitHandler {
	override fun onPostInit(e: FMLPostInitializationEvent) {
		API.instance.initialize(BlocksAPI, ItemsAPI);
	}
}
