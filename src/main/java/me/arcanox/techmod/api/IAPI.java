package me.arcanox.techmod.api;

import me.arcanox.techmod.api.blocks.IBlockAPI;
import me.arcanox.techmod.api.items.IItemAPI;
import org.jetbrains.annotations.Contract;

public interface IAPI {
	@Contract( pure = true )
	boolean isModLoaded();
	
	@Contract( pure = true )
	IItemAPI items();
	
	@Contract( pure = true )
	IBlockAPI blocks();
}
