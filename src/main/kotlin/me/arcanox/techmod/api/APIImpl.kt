package me.arcanox.techmod.api

import me.arcanox.techmod.api.blocks.BlocksAPI
import me.arcanox.techmod.api.items.ItemsAPI
import me.arcanox.techmod.common.IInitHandler
import me.arcanox.techmod.util.reflect.InitHandler
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent

@InitHandler(priority = 999 /* after everything (theoretically) */)
object APIImpl : IInitHandler {
	override fun onInit(e: FMLCommonSetupEvent) {
		API.instance.initialize(BlocksAPI, ItemsAPI);
	}
}
