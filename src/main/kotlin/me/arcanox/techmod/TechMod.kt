package me.arcanox.techmod

import me.arcanox.lib.ArcanoxModBase
import me.arcanox.techmod.common.blocks.Blocks
import me.arcanox.techmod.common.items.Items
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

@Mod(TechMod.ModID)
class TechMod : ArcanoxModBase() {
	companion object {
		const val Name = "Tech Mod" // TODO: change these to actual name
		const val ModID = "techmod" // TODO: change these to actual name
		const val PackagePrefix = "me.arcanox.techmod" // TODO: change these to actual name
	}
	
	override val modId: String
		get() = ModID
	override val packagePrefix: String
		get() = PackagePrefix
	
	init {
		val eventBus = FMLJavaModLoadingContext.get().modEventBus;
		
		Blocks.register(eventBus);
		Items.register(eventBus);
		
		finalizeInit();
	}
}