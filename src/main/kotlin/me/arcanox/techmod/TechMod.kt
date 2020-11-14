package me.arcanox.techmod

import me.arcanox.lib.ArcanoxModBase
import me.arcanox.techmod.util.Logger
import net.minecraftforge.fml.common.Mod

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
		finalizeInit();
	}
}