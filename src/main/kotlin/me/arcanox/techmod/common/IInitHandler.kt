package me.arcanox.techmod.common

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent

internal interface IInitHandler {
	fun onInit(e: FMLCommonSetupEvent);
}