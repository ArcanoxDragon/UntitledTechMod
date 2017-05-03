package me.arcanox.techmod.api;

import me.arcanox.techmod.api.blocks.IBlockAPI;
import me.arcanox.techmod.api.items.IItemAPI;
import org.jetbrains.annotations.Contract;

public class API {
	static IBlockAPI blockAPI;
	static IItemAPI  itemAPI;
	static boolean   modLoaded;
	
	@Contract( pure = true )
	public static boolean isModLoaded() { return modLoaded; }
	
	// region Blocks
	
	@Contract( pure = true )
	public static IItemAPI items() {
		if ( !isModLoaded() ) return null;
		
		return itemAPI;
	}
	
	// endregion
	
	// region Items
	
	@Contract( pure = true )
	public static IBlockAPI blocks() {
		if ( !isModLoaded() ) return null;
		
		return blockAPI;
	}
	
	// endregion
}
