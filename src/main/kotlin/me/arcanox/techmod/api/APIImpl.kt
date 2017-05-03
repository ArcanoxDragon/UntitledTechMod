package me.arcanox.techmod.api

import me.arcanox.techmod.api.blocks.BlocksAPI
import me.arcanox.techmod.api.items.ItemsAPI;

object APIImpl {
	internal fun init(): Unit {
		BlocksAPI.init();
		ItemsAPI.init();
	}
	
	internal fun finishInit(): Unit {
		API.itemAPI = ItemsAPI;
		API.modLoaded = true;
	}
}
