package me.arcanox.techmod

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent

@Mod( modid = TechMod.MODID, version = TechMod.VERSION, modLanguage = "scala" ) object TechMod {
	final val NAME    = "TechMod" // TODO: change these to actual name
	final val MODID   = "techmod" // TODO: change these to actual name
	final val VERSION = "1.0"
	
	@EventHandler def init( event: FMLInitializationEvent ): Unit = {
	
	}
}