package me.arcanox.techmod.common.proxy

import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent


interface IClientInitHandler {
	fun onClientPreInit(e: FMLPreInitializationEvent) {}
	fun onClientInit(e: FMLInitializationEvent) {}
	fun onClientPostInit(e: FMLPostInitializationEvent) {}
}