package me.arcanox.techmod.api.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IItemsApi {
	Item getItem( String name );
	
	ItemStack getItemStack( String name, int count );
}
