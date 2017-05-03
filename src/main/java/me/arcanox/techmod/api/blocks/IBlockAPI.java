package me.arcanox.techmod.api.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public interface IBlockAPI {
	Block getBlock( String name );
	
	ItemStack getBlockItemStack( String name, int count );
}
