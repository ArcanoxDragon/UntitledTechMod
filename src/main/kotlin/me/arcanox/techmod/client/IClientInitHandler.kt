package me.arcanox.techmod.client

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

interface IClientInitHandler {
	fun onClientInit(e: FMLClientSetupEvent);
}