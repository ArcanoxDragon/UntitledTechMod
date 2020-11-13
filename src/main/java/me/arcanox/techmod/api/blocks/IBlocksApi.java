package me.arcanox.techmod.api.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public interface IBlocksApi {
	Block getBlock( String name );
	
	BlockItem getBlockItem( String name );
	
	ItemStack getBlockItemStack( String name, int count );
}
