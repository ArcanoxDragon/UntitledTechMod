package me.arcanox.techmod.api

import me.arcanox.techmod.api.impl.TechModApiImpl
import me.arcanox.techmod.api.impl.blocks.BlocksApiImpl
import me.arcanox.techmod.api.impl.items.ItemsApiImpl
import me.arcanox.techmod.common.IInitHandler
import me.arcanox.techmod.util.reflect.InitHandler
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent

@InitHandler(priority = 999 /* after everything (theoretically) */)
object ApiInit : IInitHandler {
	init {
		TechModApi.initialize(TechModApiImpl);
	}
	
	override fun onInit(e: FMLCommonSetupEvent) {
		TechModApiImpl.initialize(BlocksApiImpl, ItemsApiImpl);
	}
}
