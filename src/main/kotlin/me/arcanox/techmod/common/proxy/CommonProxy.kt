package me.arcanox.techmod.common.proxy

import me.arcanox.techmod.api.APIImpl
import me.arcanox.techmod.util.IInitStageHandler

open class CommonProxy : IInitStageHandler {
	init {
		this.registerEventHandlers();
	}
	
	fun registerEventHandlers() {
		APIImpl.registerEventHandlers();
	}
	
	override fun onPreInit() {
		APIImpl.onPreInit();
	}
	
	override fun onInit() {
	
	}
	
	override fun onPostInit() {
		APIImpl.onPostInit();
	}
}