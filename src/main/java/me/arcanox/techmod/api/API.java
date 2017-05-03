package me.arcanox.techmod.api;

import me.arcanox.techmod.api.items.IItemAPI;
import org.jetbrains.annotations.Contract;

public class API {
	static IItemAPI itemAPI;
	static boolean  modLoaded;
	
	@Contract( pure = true )
	public static boolean isModLoaded() { return modLoaded; }
	
	// region Items
	
	@Contract( pure = true )
	public static IItemAPI items() {
		if ( !isModLoaded() ) return null;
		
		return itemAPI;
	}
	
	// endregion
}
