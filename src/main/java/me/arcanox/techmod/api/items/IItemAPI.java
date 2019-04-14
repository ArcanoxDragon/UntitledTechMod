package me.arcanox.techmod.api.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IItemAPI {
	Item getItem( String name );
	ItemStack getItemStack( String name, int count );
}
