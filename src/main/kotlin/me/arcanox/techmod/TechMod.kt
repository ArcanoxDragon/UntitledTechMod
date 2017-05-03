package me.arcanox.techmod

import me.arcanox.techmod.api.APIImpl
import me.arcanox.techmod.common.proxy.CommonProxy
import me.arcanox.techmod.util.Logger
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

@Mod(modid = TechMod.ModID, version = TechMod.Version, dependencies = TechMod.Dependencies, modLanguageAdapter = TechMod.Adapter)
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
	var proxy: CommonProxy? = null;
	
	@EventHandler
	fun preInit(event: FMLPreInitializationEvent): Unit {
		Logger.info("Beginning pre-initialization phase...");
		
		if (this.proxy == null) throw NullPointerException("[$Name] Side-specific mod proxy was null during pre-initialization! This is a bad thing!")
		
		this.proxy!!.onPreInit();
		
		Logger.info("Pre-initialization phase complete.");
	}
	
	@EventHandler
	fun init(event: FMLInitializationEvent): Unit {
		Logger.info("Beginning initialization phase...");
		
		if (this.proxy == null) throw NullPointerException("[$Name] Side-specific mod proxy was null during initialization! This is a bad thing!")
		
		this.proxy!!.onInit();
		
		Logger.info("Initialization phase complete.");
	}
	
	@EventHandler
	fun postInit(event: FMLPostInitializationEvent): Unit {
		Logger.info("Beginning post-initialization phase...");
		
		if (this.proxy == null) throw NullPointerException("[$Name] Side-specific mod proxy was null during post-initialization! This is a bad thing!")
		
		this.proxy!!.onPostInit();
		
		Logger.info("Post-initialization phase complete.");
	}
}