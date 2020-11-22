package me.arcanox.techmod.api;

import org.jetbrains.annotations.Contract;

public interface ITechModApi {
	/**
	 * @return True if the mod has finished loading, otherwise false.
	 */
	@Contract( pure = true )
	boolean isModLoaded();
}
