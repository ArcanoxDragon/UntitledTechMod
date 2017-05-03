package me.arcanox.techmod

import me.arcanox.techmod.api.APIImpl
import me.arcanox.techmod.util.Logger
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

@Mod(modid = TechMod.ModID, version = TechMod.Version, dependencies = TechMod.Dependencies, modLanguageAdapter = TechMod.Adapter)
object TechMod {
	const val Name = "TechMod" // TODO: change these to actual name
	const val ModID = "techmod" // TODO: change these to actual name
	const val Version = "1.0"
	const val Dependencies = "required-after:forgelin;required-after:forge@[13.20.0.2285,)"
	const val Adapter = "net.shadowfacts.forgelin.KotlinAdapter"
	
	@EventHandler
	fun preInit(event: FMLPreInitializationEvent): Unit {
		Logger.info("Beginning pre-initialization phase...");
		
		APIImpl.init();
		
		Logger.info("Pre-initialization phase complete.");
	}
	
	@EventHandler
	fun init(event: FMLInitializationEvent): Unit {
		Logger.info("Beginning initialization phase...");
		
		Logger.info("Initialization phase complete.");
	}
	
	@EventHandler
	fun postInit(event: FMLPostInitializationEvent): Unit {
		Logger.info("Beginning post-initialization phase...");
		
		APIImpl.finishInit();
		
		Logger.info("Post-initialization phase complete.");
	}
}