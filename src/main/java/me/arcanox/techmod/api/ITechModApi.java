package me.arcanox.techmod.api;

import me.arcanox.techmod.api.blocks.IBlocksApi;
import me.arcanox.techmod.api.items.IItemsApi;
import org.jetbrains.annotations.Contract;

public interface ITechModApi {
	/**
	 * @return True if the mod has finished loading, otherwise false.
	 */
	@Contract( pure = true )
	boolean isModLoaded();
	
	/**
	 * @return An instance of the IItemsApi interface, used to access mod items
	 */
	@Contract( pure = true )
	IItemsApi items();
	
	/**
	 * @return An instance of the IBlocksApi interface, used to access mod blocks
	 */
	@Contract( pure = true )
	IBlocksApi blocks();
}
