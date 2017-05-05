package me.arcanox.techmod.util

import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

internal interface IInitStageHandler {
	fun onPreInit(e: FMLPreInitializationEvent) {}
	fun onInit(e: FMLInitializationEvent) {}
	fun onPostInit(e: FMLPostInitializationEvent) {}
}