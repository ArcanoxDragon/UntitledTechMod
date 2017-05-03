package me.arcanox.techmod

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

object CreativeTab : CreativeTabs( CreativeTabs.getNextID(), TechMod.Name ) {
	override fun getTabIconItem(): ItemStack? = null;
}
