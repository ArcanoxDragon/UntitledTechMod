package me.arcanox.techmod.api.impl

import me.arcanox.techmod.api.ITechModApi
import org.jetbrains.annotations.Contract

object TechModApiImpl : ITechModApi {
	private var modLoaded = false
	
	fun initialize() {
		modLoaded = true
	}
	
	@Contract(pure = true)
	override fun isModLoaded(): Boolean {
		return modLoaded
	}
}