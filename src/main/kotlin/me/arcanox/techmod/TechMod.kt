package me.arcanox.techmod

import me.arcanox.techmod.common.proxy.CommonProxy
import me.arcanox.techmod.util.Logger
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

@Mod(name = TechMod.Name, modid = TechMod.ModID, version = TechMod.Version, dependencies = TechMod.Dependencies, modLanguageAdapter = TechMod.Adapter)
object TechMod {
	const val Name = "Tech Mod" // TODO: change these to actual name
	const val ModID = "techmod" // TODO: change these to actual name
	const val Version = "@VERSION@"
	const val Dependencies = "required-after:forgelin;required-after:forge@[13.20.0.2285,)"
	const val Adapter = "net.shadowfacts.forgelin.KotlinAdapter"
	
	@SidedProxy(
			clientSide = "me.arcanox.$ModID.client.proxy.ClientProxy",
			serverSide = "me.arcanox.$ModID.server.proxy.ClientProxy"
	)
	lateinit var proxy: CommonProxy;
	
	@EventHandler
	fun preInit(event: FMLPreInitializationEvent): Unit {
		Logger.info("Beginning pre-initialization phase...");
		
		this.proxy.onPreInit(event);
		
		Logger.info("Pre-initialization phase complete.");
	}
	
	@EventHandler
	fun init(event: FMLInitializationEvent): Unit {
		Logger.info("Beginning initialization phase...");
		
		this.proxy.onInit(event);
		
		Logger.info("Initialization phase complete.");
	}
	
	@EventHandler
	fun postInit(event: FMLPostInitializationEvent): Unit {
		Logger.info("Beginning post-initialization phase...");
		
		this.proxy.onPostInit(event);
		
		Logger.info("Post-initialization phase complete.");
	}
}