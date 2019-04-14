package me.arcanox.techmod.api;

import me.arcanox.techmod.api.blocks.IBlockAPI;
import me.arcanox.techmod.api.items.IItemAPI;
import org.jetbrains.annotations.Contract;

public class API implements IAPI {
	// region Static
	
	// Initial state is an uninitialized instance
	static final API instance = new API();
	
	@Contract( pure = true )
	public static IAPI getInstance() {
		return instance;
	}
	
	// endregion
	
	private IBlockAPI blockApi;
	private IItemAPI  itemApi;
	private boolean   modLoaded;
	
	void initialize(IBlockAPI blockApi, IItemAPI itemApi) {
		this.blockApi = blockApi;
		this.itemApi = itemApi;
		this.modLoaded = true;
	}
	
	@Override
	@Contract( pure = true )
	public boolean isModLoaded() { return this.modLoaded; }
	
	// region Blocks
	
	@Override
	@Contract( pure = true )
	public IItemAPI items() {
		if ( !this.isModLoaded() ) return null;
		
		return this.itemApi;
	}
	
	// endregion
	
	// region Items
	
	@Override
	@Contract( pure = true )
	public IBlockAPI blocks() {
		if ( !this.isModLoaded() ) return null;
		
		return this.blockApi;
	}
	
	// endregion
}
