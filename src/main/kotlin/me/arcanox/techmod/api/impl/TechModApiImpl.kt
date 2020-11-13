package me.arcanox.techmod.api.impl

import me.arcanox.techmod.api.ITechModApi
import me.arcanox.techmod.api.TechModApi
import me.arcanox.techmod.api.blocks.IBlocksApi
import me.arcanox.techmod.api.items.IItemsApi
import org.jetbrains.annotations.Contract

object TechModApiImpl : ITechModApi {
	private var blockApi: IBlocksApi? = null
	private var itemApi: IItemsApi? = null
	private var modLoaded = false
	
	fun initialize(blockApi: IBlocksApi?, itemApi: IItemsApi?) {
		this.blockApi = blockApi
		this.itemApi = itemApi
		modLoaded = true
	}
	
	@Contract(pure = true)
	override fun isModLoaded(): Boolean {
		return modLoaded
	}
	
	// region Blocks
	@Contract(pure = true)
	override fun items(): IItemsApi {
		if (!this.isModLoaded) throw IllegalStateException("Cannot access items before mod is loaded")
		
		return itemApi!!
	}
	
	// endregion
	// region Items
	@Contract(pure = true)
	override fun blocks(): IBlocksApi {
		if (!this.isModLoaded) throw IllegalStateException("Cannot access blocks before mod is loaded")
		
		return blockApi!!
	} // endregion
}